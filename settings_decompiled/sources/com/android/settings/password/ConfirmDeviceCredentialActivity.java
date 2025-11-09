package com.android.settings.password;

import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.biometrics.PromptInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.password.ChooseLockSettingsHelper;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ConfirmDeviceCredentialActivity extends FragmentActivity {
    public static final String TAG = "ConfirmDeviceCredentialActivity";
    private BiometricFragment mBiometricFragment;
    private boolean mCheckDevicePolicyManager;
    private Context mContext;
    private int mCredentialMode;
    private String mDetails;
    private DevicePolicyManager mDevicePolicyManager;
    private boolean mGoingToBackground;
    private LockPatternUtils mLockPatternUtils;
    private String mTitle;
    private TrustManager mTrustManager;
    private int mUserId;
    private UserManager mUserManager;
    private boolean mWaitingForBiometricCallback;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Executor mExecutor = new Executor() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda0
        @Override // java.util.concurrent.Executor
        public final void execute(Runnable runnable) {
            ConfirmDeviceCredentialActivity.this.lambda$new$0(runnable);
        }
    };
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity.1
        @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
        public void onAuthenticationError(int i, CharSequence charSequence) {
            if (!ConfirmDeviceCredentialActivity.this.mGoingToBackground) {
                ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
                if (i == 10 || i == 5) {
                    ConfirmDeviceCredentialActivity.this.finish();
                } else if (ConfirmDeviceCredentialActivity.this.mUserManager.getUserInfo(ConfirmDeviceCredentialActivity.this.mUserId) == null) {
                    String str = ConfirmDeviceCredentialActivity.TAG;
                    Log.i(str, "Finishing, user no longer valid: " + ConfirmDeviceCredentialActivity.this.mUserId);
                    ConfirmDeviceCredentialActivity.this.finish();
                } else {
                    ConfirmDeviceCredentialActivity.this.showConfirmCredentials();
                }
            } else if (ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback) {
                ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
                ConfirmDeviceCredentialActivity.this.finish();
            }
        }

        @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
            ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
            ConfirmDeviceCredentialActivity.this.mTrustManager.setDeviceLockedForUser(ConfirmDeviceCredentialActivity.this.mUserId, false);
            ConfirmDeviceCredentialUtils.reportSuccessfulAttempt(ConfirmDeviceCredentialActivity.this.mLockPatternUtils, ConfirmDeviceCredentialActivity.this.mUserManager, ConfirmDeviceCredentialActivity.this.mDevicePolicyManager, ConfirmDeviceCredentialActivity.this.mUserId, authenticationResult.getAuthenticationType() == 1);
            ConfirmDeviceCredentialUtils.checkForPendingIntent(ConfirmDeviceCredentialActivity.this);
            ConfirmDeviceCredentialActivity.this.setResult(-1);
            ConfirmDeviceCredentialActivity.this.finish();
        }

        @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
        public void onAuthenticationFailed() {
            ConfirmDeviceCredentialActivity.this.mWaitingForBiometricCallback = false;
            ConfirmDeviceCredentialActivity.this.mDevicePolicyManager.reportFailedBiometricAttempt(ConfirmDeviceCredentialActivity.this.mUserId);
        }

        public void onSystemEvent(int i) {
            String str = ConfirmDeviceCredentialActivity.TAG;
            Log.d(str, "SystemEvent: " + i);
            if (i != 1) {
                return;
            }
            ConfirmDeviceCredentialActivity.this.finish();
        }
    };

    /* loaded from: classes.dex */
    public static class InternalActivity extends ConfirmDeviceCredentialActivity {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        boolean z;
        super.onCreate(bundle);
        getWindow().addFlags(Integer.MIN_VALUE);
        boolean z2 = false;
        getWindow().setStatusBarColor(0);
        this.mDevicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        this.mUserManager = UserManager.get(this);
        this.mTrustManager = (TrustManager) getSystemService(TrustManager.class);
        this.mLockPatternUtils = new LockPatternUtils(this);
        Intent intent = getIntent();
        this.mContext = this;
        this.mCheckDevicePolicyManager = intent.getBooleanExtra("check_dpm", false);
        this.mTitle = intent.getStringExtra("android.app.extra.TITLE");
        this.mDetails = intent.getStringExtra("android.app.extra.DESCRIPTION");
        String stringExtra = intent.getStringExtra("android.app.extra.ALTERNATE_BUTTON_LABEL");
        boolean equals = "android.app.action.CONFIRM_FRP_CREDENTIAL".equals(intent.getAction());
        this.mUserId = UserHandle.myUserId();
        if (isInternalActivity()) {
            try {
                this.mUserId = Utils.getUserIdFromBundle(this, intent.getExtras());
            } catch (SecurityException e) {
                Log.e(TAG, "Invalid intent extra", e);
            }
        }
        int credentialOwnerProfile = this.mUserManager.getCredentialOwnerProfile(this.mUserId);
        boolean isManagedProfile = UserManager.get(this).isManagedProfile(credentialOwnerProfile);
        if (this.mTitle == null && isManagedProfile) {
            this.mTitle = getTitleFromOrganizationName(this.mUserId);
        }
        PromptInfo promptInfo = new PromptInfo();
        promptInfo.setTitle(this.mTitle);
        promptInfo.setDescription(this.mDetails);
        promptInfo.setDisallowBiometricsIfPolicyExists(this.mCheckDevicePolicyManager);
        int credentialType = Utils.getCredentialType(this.mContext, credentialOwnerProfile);
        if (this.mTitle == null) {
            promptInfo.setDeviceCredentialTitle(getTitleFromCredentialType(credentialType, isManagedProfile));
        }
        if (this.mDetails == null) {
            promptInfo.setSubtitle(getDetailsFromCredentialType(credentialType, isManagedProfile));
        }
        if (equals) {
            z2 = new ChooseLockSettingsHelper.Builder(this).setHeader(this.mTitle).setDescription(this.mDetails).setAlternateButton(stringExtra).setExternal(true).setUserId(-9999).show();
            z = false;
        } else if (isManagedProfile && isInternalActivity()) {
            this.mCredentialMode = 2;
            if (isBiometricAllowed(credentialOwnerProfile, this.mUserId)) {
                showBiometricPrompt(promptInfo);
                z = true;
            } else {
                showConfirmCredentials();
                z = false;
                z2 = true;
            }
        } else {
            this.mCredentialMode = 1;
            if (isBiometricAllowed(credentialOwnerProfile, this.mUserId)) {
                showBiometricPrompt(promptInfo);
                z = true;
            } else {
                showConfirmCredentials();
                z = false;
                z2 = true;
            }
        }
        if (z2) {
            finish();
        } else if (z) {
            this.mWaitingForBiometricCallback = true;
        } else {
            Log.d(TAG, "No pattern, password or PIN set.");
            setResult(-1);
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getTitleFromCredentialType$1() {
        return getString(R$string.lockpassword_confirm_your_work_pin_header);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getTitleFromCredentialType$2() {
        return getString(R$string.lockpassword_confirm_your_work_pattern_header);
    }

    private String getTitleFromCredentialType(int i, boolean z) {
        if (i == 1) {
            if (z) {
                return this.mDevicePolicyManager.getResources().getString("Settings.CONFIRM_WORK_PROFILE_PATTERN_HEADER", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda2
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getTitleFromCredentialType$2;
                        lambda$getTitleFromCredentialType$2 = ConfirmDeviceCredentialActivity.this.lambda$getTitleFromCredentialType$2();
                        return lambda$getTitleFromCredentialType$2;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_pattern_header);
        } else if (i == 3) {
            if (z) {
                return this.mDevicePolicyManager.getResources().getString("Settings.CONFIRM_WORK_PROFILE_PIN_HEADER", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getTitleFromCredentialType$1;
                        lambda$getTitleFromCredentialType$1 = ConfirmDeviceCredentialActivity.this.lambda$getTitleFromCredentialType$1();
                        return lambda$getTitleFromCredentialType$1;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_pin_header);
        } else if (i != 4) {
            return null;
        } else {
            if (z) {
                return this.mDevicePolicyManager.getResources().getString("Settings.CONFIRM_WORK_PROFILE_PASSWORD_HEADER", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda3
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getTitleFromCredentialType$3;
                        lambda$getTitleFromCredentialType$3 = ConfirmDeviceCredentialActivity.this.lambda$getTitleFromCredentialType$3();
                        return lambda$getTitleFromCredentialType$3;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_password_header);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getTitleFromCredentialType$3() {
        return getString(R$string.lockpassword_confirm_your_work_password_header);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDetailsFromCredentialType$4() {
        return getString(R$string.lockpassword_confirm_your_pin_generic_profile);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDetailsFromCredentialType$5() {
        return getString(R$string.lockpassword_confirm_your_pattern_generic_profile);
    }

    private String getDetailsFromCredentialType(int i, boolean z) {
        if (i == 1) {
            if (z) {
                return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_CONFIRM_PATTERN", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda5
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDetailsFromCredentialType$5;
                        lambda$getDetailsFromCredentialType$5 = ConfirmDeviceCredentialActivity.this.lambda$getDetailsFromCredentialType$5();
                        return lambda$getDetailsFromCredentialType$5;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_pattern_generic);
        } else if (i == 3) {
            if (z) {
                return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_CONFIRM_PIN", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda4
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDetailsFromCredentialType$4;
                        lambda$getDetailsFromCredentialType$4 = ConfirmDeviceCredentialActivity.this.lambda$getDetailsFromCredentialType$4();
                        return lambda$getDetailsFromCredentialType$4;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_pin_generic);
        } else if (i != 4) {
            return null;
        } else {
            if (z) {
                return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_CONFIRM_PASSWORD", new Supplier() { // from class: com.android.settings.password.ConfirmDeviceCredentialActivity$$ExternalSyntheticLambda6
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDetailsFromCredentialType$6;
                        lambda$getDetailsFromCredentialType$6 = ConfirmDeviceCredentialActivity.this.lambda$getDetailsFromCredentialType$6();
                        return lambda$getDetailsFromCredentialType$6;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_password_generic);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDetailsFromCredentialType$6() {
        return getString(R$string.lockpassword_confirm_your_password_generic_profile);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        setVisible(true);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (!isChangingConfigurations()) {
            this.mGoingToBackground = true;
            if (this.mWaitingForBiometricCallback) {
                return;
            }
            finish();
            return;
        }
        this.mGoingToBackground = false;
    }

    private boolean isStrongAuthRequired(int i) {
        return (this.mLockPatternUtils.isBiometricAllowedForUser(i) && this.mUserManager.isUserUnlocked(this.mUserId)) ? false : true;
    }

    private boolean isBiometricAllowed(int i, int i2) {
        return (isStrongAuthRequired(i) || this.mLockPatternUtils.hasPendingEscrowToken(i2)) ? false : true;
    }

    private void showBiometricPrompt(PromptInfo promptInfo) {
        boolean z;
        BiometricFragment biometricFragment = (BiometricFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        this.mBiometricFragment = biometricFragment;
        if (biometricFragment == null) {
            this.mBiometricFragment = BiometricFragment.newInstance(promptInfo);
            z = true;
        } else {
            z = false;
        }
        this.mBiometricFragment.setCallbacks(this.mExecutor, this.mAuthenticationCallback);
        this.mBiometricFragment.setUser(this.mUserId);
        if (z) {
            getSupportFragmentManager().beginTransaction().add(this.mBiometricFragment, "fragment").commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConfirmCredentials() {
        boolean show;
        int i = this.mCredentialMode;
        if (i == 2) {
            show = new ChooseLockSettingsHelper.Builder(this).setHeader(this.mTitle).setDescription(this.mDetails).setExternal(true).setUserId(this.mUserId).setForceVerifyPath(true).show();
        } else {
            show = i == 1 ? new ChooseLockSettingsHelper.Builder(this).setHeader(this.mTitle).setDescription(this.mDetails).setExternal(true).setUserId(this.mUserId).show() : false;
        }
        if (!show) {
            Log.d(TAG, "No pin/pattern/pass set");
            setResult(-1);
        }
        finish();
    }

    private boolean isInternalActivity() {
        return this instanceof InternalActivity;
    }

    private String getTitleFromOrganizationName(int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService("device_policy");
        CharSequence organizationNameForUser = devicePolicyManager != null ? devicePolicyManager.getOrganizationNameForUser(i) : null;
        if (organizationNameForUser != null) {
            return organizationNameForUser.toString();
        }
        return null;
    }
}
