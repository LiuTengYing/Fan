package com.android.settings.password;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.password.ConfirmLockPassword;
import com.android.settings.password.ConfirmLockPattern;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public abstract class ConfirmDeviceCredentialBaseFragment extends InstrumentedFragment {
    public static final String TAG = ConfirmDeviceCredentialBaseFragment.class.getSimpleName();
    protected BiometricManager mBiometricManager;
    protected Button mCancelButton;
    protected DevicePolicyManager mDevicePolicyManager;
    protected int mEffectiveUserId;
    protected TextView mErrorTextView;
    protected Button mForgotButton;
    protected boolean mFrp;
    private CharSequence mFrpAlternateButtonText;
    protected LockPatternUtils mLockPatternUtils;
    protected int mUserId;
    protected UserManager mUserManager;
    protected boolean mReturnCredentials = false;
    protected boolean mReturnGatekeeperPassword = false;
    protected boolean mForceVerifyPath = false;
    protected final Handler mHandler = new Handler();
    private final Runnable mResetErrorRunnable = new Runnable() { // from class: com.android.settings.password.ConfirmDeviceCredentialBaseFragment.1
        @Override // java.lang.Runnable
        public void run() {
            ConfirmDeviceCredentialBaseFragment.this.mErrorTextView.setText("");
        }
    };

    protected abstract int getLastTryDefaultErrorMessage(int i);

    protected abstract String getLastTryOverrideErrorMessageId(int i);

    protected abstract void onShowError();

    public void prepareEnterAnimation() {
    }

    public void startEnterAnimation() {
    }

    private boolean isInternalActivity() {
        return (getActivity() instanceof ConfirmLockPassword.InternalActivity) || (getActivity() instanceof ConfirmLockPattern.InternalActivity);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getActivity().getIntent();
        this.mFrpAlternateButtonText = intent.getCharSequenceExtra("android.app.extra.ALTERNATE_BUTTON_LABEL");
        this.mReturnCredentials = intent.getBooleanExtra("return_credentials", false);
        this.mReturnGatekeeperPassword = intent.getBooleanExtra("request_gk_pw_handle", false);
        this.mForceVerifyPath = intent.getBooleanExtra("force_verify", false);
        int userIdFromBundle = Utils.getUserIdFromBundle(getActivity(), intent.getExtras(), isInternalActivity());
        this.mUserId = userIdFromBundle;
        this.mFrp = userIdFromBundle == -9999;
        UserManager userManager = UserManager.get(getActivity());
        this.mUserManager = userManager;
        this.mEffectiveUserId = userManager.getCredentialOwnerProfile(this.mUserId);
        this.mLockPatternUtils = new LockPatternUtils(getActivity());
        this.mDevicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        this.mBiometricManager = (BiometricManager) getActivity().getSystemService(BiometricManager.class);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mCancelButton = (Button) view.findViewById(R$id.cancelButton);
        int i = 0;
        boolean booleanExtra = getActivity().getIntent().getBooleanExtra("com.android.settings.ConfirmCredentials.showCancelButton", false);
        final boolean z = this.mFrp && !TextUtils.isEmpty(this.mFrpAlternateButtonText);
        Button button = this.mCancelButton;
        if (!booleanExtra && !z) {
            i = 8;
        }
        button.setVisibility(i);
        if (z) {
            this.mCancelButton.setText(this.mFrpAlternateButtonText);
        }
        this.mCancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.password.ConfirmDeviceCredentialBaseFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ConfirmDeviceCredentialBaseFragment.this.lambda$onViewCreated$0(z, view2);
            }
        });
        setupForgotButtonIfManagedProfile(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$0(boolean z, View view) {
        if (z) {
            getActivity().setResult(1);
        }
        getActivity().finish();
    }

    private void setupForgotButtonIfManagedProfile(View view) {
        if (this.mUserManager.isManagedProfile(this.mUserId) && this.mUserManager.isQuietModeEnabled(UserHandle.of(this.mUserId)) && this.mDevicePolicyManager.canProfileOwnerResetPasswordWhenLocked(this.mUserId)) {
            Button button = (Button) view.findViewById(R$id.forgotButton);
            this.mForgotButton = button;
            if (button == null) {
                Log.wtf(TAG, "Forgot button not found in managed profile credential dialog");
                return;
            }
            button.setVisibility(0);
            this.mForgotButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.password.ConfirmDeviceCredentialBaseFragment$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ConfirmDeviceCredentialBaseFragment.this.lambda$setupForgotButtonIfManagedProfile$1(view2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupForgotButtonIfManagedProfile$1(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", ForgotPasswordActivity.class.getName());
        intent.setFlags(268435456);
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isStrongAuthRequired() {
        return (!this.mFrp && this.mLockPatternUtils.isBiometricAllowedForUser(this.mEffectiveUserId) && this.mUserManager.isUserUnlocked(this.mUserId)) ? false : true;
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshLockScreen();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshLockScreen() {
        updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAccessibilityTitle(CharSequence charSequence) {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.title");
            if (charSequence == null) {
                return;
            }
            if (charSequenceExtra == null) {
                getActivity().setTitle(charSequence);
                return;
            }
            getActivity().setTitle(Utils.createAccessibleSequence(charSequenceExtra, charSequenceExtra + "," + charSequence));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reportFailedAttempt() {
        updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId) + 1);
        this.mLockPatternUtils.reportFailedPasswordAttempt(this.mEffectiveUserId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateErrorMessage(int i) {
        int maximumFailedPasswordsForWipe = this.mLockPatternUtils.getMaximumFailedPasswordsForWipe(this.mEffectiveUserId);
        if (maximumFailedPasswordsForWipe <= 0 || i <= 0) {
            return;
        }
        if (this.mErrorTextView != null) {
            showError(getActivity().getString(R$string.lock_failed_attempts_before_wipe, new Object[]{Integer.valueOf(i), Integer.valueOf(maximumFailedPasswordsForWipe)}), 0L);
        }
        int i2 = maximumFailedPasswordsForWipe - i;
        if (i2 > 1) {
            return;
        }
        FragmentManager childFragmentManager = getChildFragmentManager();
        int userTypeForWipe = getUserTypeForWipe();
        if (i2 == 1) {
            String string = getActivity().getString(R$string.lock_last_attempt_before_wipe_warning_title);
            String lastTryOverrideErrorMessageId = getLastTryOverrideErrorMessageId(userTypeForWipe);
            final int lastTryDefaultErrorMessage = getLastTryDefaultErrorMessage(userTypeForWipe);
            LastTryDialog.show(childFragmentManager, string, this.mDevicePolicyManager.getResources().getString(lastTryOverrideErrorMessageId, new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialBaseFragment$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateErrorMessage$2;
                    lambda$updateErrorMessage$2 = ConfirmDeviceCredentialBaseFragment.this.lambda$updateErrorMessage$2(lastTryDefaultErrorMessage);
                    return lambda$updateErrorMessage$2;
                }
            }), 17039370, false);
            return;
        }
        LastTryDialog.show(childFragmentManager, null, getWipeMessage(userTypeForWipe), com.android.settingslib.R$string.failed_attempts_now_wiping_dialog_dismiss, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateErrorMessage$2(int i) {
        return getString(i);
    }

    private int getUserTypeForWipe() {
        UserInfo userInfo = this.mUserManager.getUserInfo(this.mDevicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(this.mEffectiveUserId));
        if (userInfo == null || userInfo.isPrimary()) {
            return 1;
        }
        return userInfo.isManagedProfile() ? 2 : 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getWipeMessage$3() {
        return getString(com.android.settingslib.R$string.failed_attempts_now_wiping_profile);
    }

    private String getWipeMessage(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    return getString(com.android.settingslib.R$string.failed_attempts_now_wiping_user);
                }
                throw new IllegalArgumentException("Unrecognized user type:" + i);
            }
            return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_LOCK_ATTEMPTS_FAILED", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialBaseFragment$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getWipeMessage$3;
                    lambda$getWipeMessage$3 = ConfirmDeviceCredentialBaseFragment.this.lambda$getWipeMessage$3();
                    return lambda$getWipeMessage$3;
                }
            });
        }
        return getString(com.android.settingslib.R$string.failed_attempts_now_wiping_device);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showError(CharSequence charSequence, long j) {
        this.mErrorTextView.setText(charSequence);
        onShowError();
        this.mHandler.removeCallbacks(this.mResetErrorRunnable);
        if (j != 0) {
            this.mHandler.postDelayed(this.mResetErrorRunnable, j);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showError(int i, long j) {
        showError(getText(i), j);
    }

    /* loaded from: classes.dex */
    public static class LastTryDialog extends DialogFragment {
        private static final String TAG = LastTryDialog.class.getSimpleName();

        static boolean show(FragmentManager fragmentManager, String str, String str2, int i, boolean z) {
            String str3 = TAG;
            LastTryDialog lastTryDialog = (LastTryDialog) fragmentManager.findFragmentByTag(str3);
            if (lastTryDialog == null || lastTryDialog.isRemoving()) {
                Bundle bundle = new Bundle();
                bundle.putString("title", str);
                bundle.putString("message", str2);
                bundle.putInt("button", i);
                bundle.putBoolean("dismiss", z);
                LastTryDialog lastTryDialog2 = new LastTryDialog();
                lastTryDialog2.setArguments(bundle);
                lastTryDialog2.show(fragmentManager, str3);
                fragmentManager.executePendingTransactions();
                return true;
            }
            return false;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog create = new AlertDialog.Builder(getActivity()).setTitle(getArguments().getString("title")).setMessage(getArguments().getString("message")).setPositiveButton(getArguments().getInt("button"), (DialogInterface.OnClickListener) null).create();
            create.setCanceledOnTouchOutside(false);
            return create;
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            if (getActivity() == null || !getArguments().getBoolean("dismiss")) {
                return;
            }
            getActivity().finish();
        }
    }
}
