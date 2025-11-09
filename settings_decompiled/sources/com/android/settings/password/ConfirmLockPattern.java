package com.android.settings.password;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.R$anim;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.password.ConfirmDeviceCredentialBaseActivity;
import com.android.settings.password.ConfirmLockPattern;
import com.android.settings.password.CredentialCheckResultTracker;
import com.android.settingslib.animation.AppearAnimationCreator;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.google.android.setupdesign.GlifLayout;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ConfirmLockPattern extends ConfirmDeviceCredentialBaseActivity {

    /* loaded from: classes.dex */
    public static class InternalActivity extends ConfirmLockPattern {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum Stage {
        NeedToUnlock,
        NeedToUnlockWrong,
        LockedOut
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmLockPatternFragment.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return ConfirmLockPatternFragment.class.getName().equals(str);
    }

    /* loaded from: classes.dex */
    public static class ConfirmLockPatternFragment extends ConfirmDeviceCredentialBaseFragment implements AppearAnimationCreator<Object>, CredentialCheckResultTracker.Listener {
        private AppearAnimationUtils mAppearAnimationUtils;
        private CountDownTimer mCountdownTimer;
        private CredentialCheckResultTracker mCredentialCheckResultTracker;
        private CharSequence mDetailsText;
        private DisappearAnimationUtils mDisappearAnimationUtils;
        private GlifLayout mGlifLayout;
        private CharSequence mHeaderText;
        private boolean mIsManagedProfile;
        private LockPatternView mLockPatternView;
        private AsyncTask<?, ?, ?> mPendingLockCheck;
        private boolean mDisappearing = false;
        private Runnable mClearPatternRunnable = new Runnable() { // from class: com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.2
            @Override // java.lang.Runnable
            public void run() {
                ConfirmLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };
        private LockPatternView.OnPatternListener mConfirmExistingLockPatternListener = new AnonymousClass3();

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected String getLastTryOverrideErrorMessageId(int i) {
            return i == 2 ? "Settings.WORK_PROFILE_LAST_PATTERN_ATTEMPT_BEFORE_WIPE" : "UNDEFINED";
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 31;
        }

        @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected void onShowError() {
        }

        @Override // androidx.fragment.app.Fragment
        @SuppressLint({"ClickableViewAccessibility"})
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            int i;
            if (((ConfirmLockPattern) getActivity()).getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.NORMAL) {
                i = R$layout.confirm_lock_pattern_normal;
            } else {
                i = R$layout.confirm_lock_pattern;
            }
            View inflate = layoutInflater.inflate(i, viewGroup, false);
            this.mGlifLayout = (GlifLayout) inflate.findViewById(R$id.setup_wizard_layout);
            this.mLockPatternView = inflate.findViewById(R$id.lockPattern);
            this.mErrorTextView = (TextView) inflate.findViewById(R$id.errorText);
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mEffectiveUserId);
            inflate.findViewById(R$id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                this.mHeaderText = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
                this.mDetailsText = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.details");
            }
            if (TextUtils.isEmpty(this.mHeaderText) && this.mIsManagedProfile) {
                this.mHeaderText = this.mDevicePolicyManager.getOrganizationNameForUser(this.mUserId);
            }
            this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(this.mEffectiveUserId));
            this.mLockPatternView.setOnPatternListener(this.mConfirmExistingLockPatternListener);
            this.mLockPatternView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$$ExternalSyntheticLambda0
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$onCreateView$0;
                    lambda$onCreateView$0 = ConfirmLockPattern.ConfirmLockPatternFragment.lambda$onCreateView$0(view, motionEvent);
                    return lambda$onCreateView$0;
                }
            });
            updateStage(Stage.NeedToUnlock);
            if (bundle == null && !this.mFrp && !this.mLockPatternUtils.isLockPatternEnabled(this.mEffectiveUserId)) {
                getActivity().setResult(-1);
                getActivity().finish();
            }
            this.mAppearAnimationUtils = new AppearAnimationUtils(getContext(), 220L, 2.0f, 1.3f, AnimationUtils.loadInterpolator(getContext(), 17563662));
            this.mDisappearAnimationUtils = new DisappearAnimationUtils(getContext(), 125L, 4.0f, 0.3f, AnimationUtils.loadInterpolator(getContext(), 17563663), new AppearAnimationUtils.RowTranslationScaler() { // from class: com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.1
                @Override // com.android.settingslib.animation.AppearAnimationUtils.RowTranslationScaler
                public float getRowTranslationScale(int i2, int i3) {
                    return (i3 - i2) / i3;
                }
            });
            setAccessibilityTitle(this.mGlifLayout.getHeaderText());
            CredentialCheckResultTracker credentialCheckResultTracker = (CredentialCheckResultTracker) getFragmentManager().findFragmentByTag("check_lock_result");
            this.mCredentialCheckResultTracker = credentialCheckResultTracker;
            if (credentialCheckResultTracker == null) {
                this.mCredentialCheckResultTracker = new CredentialCheckResultTracker();
                getFragmentManager().beginTransaction().add(this.mCredentialCheckResultTracker, "check_lock_result").commit();
            }
            return inflate;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onCreateView$0(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
            return false;
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            Button button = this.mForgotButton;
            if (button != null) {
                button.setText(R$string.lockpassword_forgot_pattern);
            }
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            this.mCredentialCheckResultTracker.setListener(null);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment, com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId);
            if (lockoutAttemptDeadline != 0) {
                this.mCredentialCheckResultTracker.clearResult();
                handleAttemptLockout(lockoutAttemptDeadline);
            } else if (!this.mLockPatternView.isEnabled()) {
                updateStage(Stage.NeedToUnlock);
            }
            this.mCredentialCheckResultTracker.setListener(this);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void prepareEnterAnimation() {
            super.prepareEnterAnimation();
            this.mGlifLayout.getHeaderTextView().setAlpha(0.0f);
            this.mCancelButton.setAlpha(0.0f);
            Button button = this.mForgotButton;
            if (button != null) {
                button.setAlpha(0.0f);
            }
            this.mLockPatternView.setAlpha(0.0f);
            this.mGlifLayout.getDescriptionTextView().setAlpha(0.0f);
        }

        private String getDefaultDetails() {
            if (this.mFrp) {
                return getString(R$string.lockpassword_confirm_your_pattern_details_frp);
            }
            boolean isStrongAuthRequired = isStrongAuthRequired();
            if (this.mIsManagedProfile) {
                if (isStrongAuthRequired) {
                    return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_PATTERN_REQUIRED", new Supplier() { // from class: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$$ExternalSyntheticLambda2
                        @Override // java.util.function.Supplier
                        public final Object get() {
                            String lambda$getDefaultDetails$1;
                            lambda$getDefaultDetails$1 = ConfirmLockPattern.ConfirmLockPatternFragment.this.lambda$getDefaultDetails$1();
                            return lambda$getDefaultDetails$1;
                        }
                    });
                }
                return this.mDevicePolicyManager.getResources().getString("Settings.WORK_PROFILE_CONFIRM_PATTERN", new Supplier() { // from class: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$$ExternalSyntheticLambda3
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDefaultDetails$2;
                        lambda$getDefaultDetails$2 = ConfirmLockPattern.ConfirmLockPatternFragment.this.lambda$getDefaultDetails$2();
                        return lambda$getDefaultDetails$2;
                    }
                });
            } else if (isStrongAuthRequired) {
                return getString(R$string.lockpassword_strong_auth_required_device_pattern);
            } else {
                return getString(R$string.lockpassword_confirm_your_pattern_generic);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getDefaultDetails$1() {
            return getString(R$string.lockpassword_strong_auth_required_work_pattern);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getDefaultDetails$2() {
            return getString(R$string.lockpassword_confirm_your_pattern_generic_profile);
        }

        private Object[][] getActiveViews() {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new ArrayList(Collections.singletonList(this.mGlifLayout.getHeaderTextView())));
            arrayList.add(new ArrayList(Collections.singletonList(this.mGlifLayout.getDescriptionTextView())));
            if (this.mCancelButton.getVisibility() == 0) {
                arrayList.add(new ArrayList(Collections.singletonList(this.mCancelButton)));
            }
            if (this.mForgotButton != null) {
                arrayList.add(new ArrayList(Collections.singletonList(this.mForgotButton)));
            }
            LockPatternView.CellState[][] cellStates = this.mLockPatternView.getCellStates();
            for (LockPatternView.CellState[] cellStateArr : cellStates) {
                ArrayList arrayList2 = new ArrayList();
                int i = 0;
                while (true) {
                    if (i < cellStateArr.length) {
                        arrayList2.add(cellStateArr[i]);
                        i++;
                    }
                }
                arrayList.add(arrayList2);
            }
            Object[][] objArr = (Object[][]) Array.newInstance(Object.class, arrayList.size(), cellStates[0].length);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                ArrayList arrayList3 = (ArrayList) arrayList.get(i2);
                for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                    objArr[i2][i3] = arrayList3.get(i3);
                }
            }
            return objArr;
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        public void startEnterAnimation() {
            super.startEnterAnimation();
            this.mLockPatternView.setAlpha(1.0f);
            this.mAppearAnimationUtils.startAnimation2d(getActiveViews(), null, this);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateStage(Stage stage) {
            int i = AnonymousClass1.$SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage[stage.ordinal()];
            if (i == 1) {
                CharSequence charSequence = this.mHeaderText;
                if (charSequence != null) {
                    this.mGlifLayout.setHeaderText(charSequence);
                } else {
                    this.mGlifLayout.setHeaderText(getDefaultHeader());
                }
                CharSequence charSequence2 = this.mDetailsText;
                if (charSequence2 != null) {
                    this.mGlifLayout.setDescriptionText(charSequence2);
                } else {
                    this.mGlifLayout.setDescriptionText(getDefaultDetails());
                }
                this.mErrorTextView.setText("");
                updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
                this.mLockPatternView.setEnabled(true);
                this.mLockPatternView.enableInput();
                this.mLockPatternView.clearPattern();
            } else if (i == 2) {
                showError(R$string.lockpattern_need_to_unlock_wrong, 3000L);
                this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                this.mLockPatternView.setEnabled(true);
                this.mLockPatternView.enableInput();
            } else if (i == 3) {
                this.mLockPatternView.clearPattern();
                this.mLockPatternView.setEnabled(false);
            }
            this.mGlifLayout.getHeaderTextView().announceForAccessibility(this.mGlifLayout.getHeaderText());
        }

        private String getDefaultHeader() {
            if (this.mFrp) {
                return getString(R$string.lockpassword_confirm_your_pattern_header_frp);
            }
            if (this.mIsManagedProfile) {
                return this.mDevicePolicyManager.getResources().getString("Settings.CONFIRM_WORK_PROFILE_PATTERN_HEADER", new Supplier() { // from class: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$$ExternalSyntheticLambda1
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$getDefaultHeader$3;
                        lambda$getDefaultHeader$3 = ConfirmLockPattern.ConfirmLockPatternFragment.this.lambda$getDefaultHeader$3();
                        return lambda$getDefaultHeader$3;
                    }
                });
            }
            return getString(R$string.lockpassword_confirm_your_pattern_header);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getDefaultHeader$3() {
            return getString(R$string.lockpassword_confirm_your_work_pattern_header);
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 3000L);
        }

        private void startDisappearAnimation(final Intent intent) {
            if (this.mDisappearing) {
                return;
            }
            this.mDisappearing = true;
            final ConfirmLockPattern confirmLockPattern = (ConfirmLockPattern) getActivity();
            if (confirmLockPattern == null || confirmLockPattern.isFinishing()) {
                return;
            }
            if (confirmLockPattern.getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.DARK) {
                this.mLockPatternView.clearPattern();
                this.mDisappearAnimationUtils.startAnimation2d(getActiveViews(), new Runnable() { // from class: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ConfirmLockPattern.ConfirmLockPatternFragment.lambda$startDisappearAnimation$4(ConfirmLockPattern.this, intent);
                    }
                }, this);
                return;
            }
            confirmLockPattern.setResult(-1, intent);
            confirmLockPattern.finish();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$startDisappearAnimation$4(ConfirmLockPattern confirmLockPattern, Intent intent) {
            confirmLockPattern.setResult(-1, intent);
            confirmLockPattern.finish();
            confirmLockPattern.overridePendingTransition(R$anim.confirm_credential_close_enter, R$anim.confirm_credential_close_exit);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$3  reason: invalid class name */
        /* loaded from: classes.dex */
        public class AnonymousClass3 implements LockPatternView.OnPatternListener {
            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            AnonymousClass3() {
            }

            public void onPatternStart() {
                ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPatternFragment.this.mClearPatternRunnable);
            }

            public void onPatternCleared() {
                ConfirmLockPatternFragment.this.mLockPatternView.removeCallbacks(ConfirmLockPatternFragment.this.mClearPatternRunnable);
            }

            public void onPatternDetected(List<LockPatternView.Cell> list) {
                if (ConfirmLockPatternFragment.this.mPendingLockCheck != null || ConfirmLockPatternFragment.this.mDisappearing) {
                    return;
                }
                ConfirmLockPatternFragment.this.mLockPatternView.setEnabled(false);
                LockscreenCredential createPattern = LockscreenCredential.createPattern(list);
                Intent intent = new Intent();
                ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                if (confirmLockPatternFragment.mReturnGatekeeperPassword) {
                    if (isInternalActivity()) {
                        startVerifyPattern(createPattern, intent, 1);
                        return;
                    }
                } else if (confirmLockPatternFragment.mForceVerifyPath) {
                    if (isInternalActivity()) {
                        startVerifyPattern(createPattern, intent, 0);
                        return;
                    }
                } else {
                    startCheckPattern(createPattern, intent);
                    return;
                }
                ConfirmLockPatternFragment.this.mCredentialCheckResultTracker.setResult(false, intent, 0, ConfirmLockPatternFragment.this.mEffectiveUserId);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public boolean isInternalActivity() {
                return ConfirmLockPatternFragment.this.getActivity() instanceof InternalActivity;
            }

            private void startVerifyPattern(LockscreenCredential lockscreenCredential, final Intent intent, final int i) {
                AsyncTask verifyTiedProfileChallenge;
                ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                final int i2 = confirmLockPatternFragment.mEffectiveUserId;
                int i3 = confirmLockPatternFragment.mUserId;
                LockPatternChecker.OnVerifyCallback onVerifyCallback = new LockPatternChecker.OnVerifyCallback() { // from class: com.android.settings.password.ConfirmLockPattern$ConfirmLockPatternFragment$3$$ExternalSyntheticLambda0
                    public final void onVerified(VerifyCredentialResponse verifyCredentialResponse, int i4) {
                        ConfirmLockPattern.ConfirmLockPatternFragment.AnonymousClass3.this.lambda$startVerifyPattern$0(i, intent, i2, verifyCredentialResponse, i4);
                    }
                };
                ConfirmLockPatternFragment confirmLockPatternFragment2 = ConfirmLockPatternFragment.this;
                if (i2 == i3) {
                    verifyTiedProfileChallenge = LockPatternChecker.verifyCredential(confirmLockPatternFragment2.mLockPatternUtils, lockscreenCredential, i3, i, onVerifyCallback);
                } else {
                    verifyTiedProfileChallenge = LockPatternChecker.verifyTiedProfileChallenge(confirmLockPatternFragment2.mLockPatternUtils, lockscreenCredential, i3, i, onVerifyCallback);
                }
                confirmLockPatternFragment2.mPendingLockCheck = verifyTiedProfileChallenge;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$startVerifyPattern$0(int i, Intent intent, int i2, VerifyCredentialResponse verifyCredentialResponse, int i3) {
                ConfirmLockPatternFragment.this.mPendingLockCheck = null;
                boolean isMatched = verifyCredentialResponse.isMatched();
                if (isMatched && ConfirmLockPatternFragment.this.mReturnCredentials) {
                    if ((i & 1) != 0) {
                        intent.putExtra("gk_pw_handle", verifyCredentialResponse.getGatekeeperPasswordHandle());
                    } else {
                        intent.putExtra("hw_auth_token", verifyCredentialResponse.getGatekeeperHAT());
                    }
                }
                ConfirmLockPatternFragment.this.mCredentialCheckResultTracker.setResult(isMatched, intent, i3, i2);
            }

            private void startCheckPattern(final LockscreenCredential lockscreenCredential, final Intent intent) {
                if (lockscreenCredential.size() < 4) {
                    ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                    confirmLockPatternFragment.onPatternChecked(false, intent, 0, confirmLockPatternFragment.mEffectiveUserId, false);
                    return;
                }
                ConfirmLockPatternFragment confirmLockPatternFragment2 = ConfirmLockPatternFragment.this;
                final int i = confirmLockPatternFragment2.mEffectiveUserId;
                confirmLockPatternFragment2.mPendingLockCheck = LockPatternChecker.checkCredential(confirmLockPatternFragment2.mLockPatternUtils, lockscreenCredential, i, new LockPatternChecker.OnCheckCallback() { // from class: com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.3.1
                    public void onChecked(boolean z, int i2) {
                        ConfirmLockPatternFragment.this.mPendingLockCheck = null;
                        if (z && AnonymousClass3.this.isInternalActivity() && ConfirmLockPatternFragment.this.mReturnCredentials) {
                            intent.putExtra("password", (Parcelable) lockscreenCredential);
                        }
                        ConfirmLockPatternFragment.this.mCredentialCheckResultTracker.setResult(z, intent, i2, i);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onPatternChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            this.mLockPatternView.setEnabled(true);
            if (z) {
                if (z2) {
                    ConfirmDeviceCredentialUtils.reportSuccessfulAttempt(this.mLockPatternUtils, this.mUserManager, this.mDevicePolicyManager, this.mEffectiveUserId, true);
                }
                startDisappearAnimation(intent);
                ConfirmDeviceCredentialUtils.checkForPendingIntent(getActivity());
                return;
            }
            if (i > 0) {
                refreshLockScreen();
                handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(i2, i));
            } else {
                updateStage(Stage.NeedToUnlockWrong);
                postClearPatternRunnable();
            }
            if (z2) {
                reportFailedAttempt();
            }
        }

        @Override // com.android.settings.password.CredentialCheckResultTracker.Listener
        public void onCredentialChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            onPatternChecked(z, intent, i, i2, z2);
        }

        @Override // com.android.settings.password.ConfirmDeviceCredentialBaseFragment
        protected int getLastTryDefaultErrorMessage(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        return R$string.lock_last_pattern_attempt_before_wipe_user;
                    }
                    throw new IllegalArgumentException("Unrecognized user type:" + i);
                }
                return R$string.lock_last_pattern_attempt_before_wipe_profile;
            }
            return R$string.lock_last_pattern_attempt_before_wipe_device;
        }

        private void handleAttemptLockout(long j) {
            updateStage(Stage.LockedOut);
            this.mCountdownTimer = new CountDownTimer(j - SystemClock.elapsedRealtime(), 1000L) { // from class: com.android.settings.password.ConfirmLockPattern.ConfirmLockPatternFragment.4
                @Override // android.os.CountDownTimer
                public void onTick(long j2) {
                    ConfirmLockPatternFragment confirmLockPatternFragment = ConfirmLockPatternFragment.this;
                    confirmLockPatternFragment.mErrorTextView.setText(confirmLockPatternFragment.getString(R$string.lockpattern_too_many_failed_confirmation_attempts, Integer.valueOf((int) (j2 / 1000))));
                }

                @Override // android.os.CountDownTimer
                public void onFinish() {
                    ConfirmLockPatternFragment.this.updateStage(Stage.NeedToUnlock);
                }
            }.start();
        }

        @Override // com.android.settingslib.animation.AppearAnimationCreator
        public void createAnimation(Object obj, long j, long j2, float f, boolean z, Interpolator interpolator, Runnable runnable) {
            if (obj instanceof LockPatternView.CellState) {
                this.mLockPatternView.startCellStateAnimation((LockPatternView.CellState) obj, 1.0f, z ? 1.0f : 0.0f, z ? f : 0.0f, z ? 0.0f : f, z ? 0.0f : 1.0f, 1.0f, j, j2, interpolator, runnable);
                return;
            }
            this.mAppearAnimationUtils.createAnimation((View) obj, j, j2, f, z, interpolator, runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ConfirmLockPattern$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage;

        static {
            int[] iArr = new int[Stage.values().length];
            $SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage = iArr;
            try {
                iArr[Stage.NeedToUnlock.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage[Stage.NeedToUnlockWrong.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$password$ConfirmLockPattern$Stage[Stage.LockedOut.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }
}
