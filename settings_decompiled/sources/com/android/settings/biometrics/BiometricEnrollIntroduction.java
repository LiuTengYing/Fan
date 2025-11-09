package com.android.settings.biometrics;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$anim;
import com.android.settings.R$string;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.span.LinkSpan;
import com.google.android.setupdesign.template.RequireScrollMixin;
import com.google.android.setupdesign.util.DynamicColorPalette;
/* loaded from: classes.dex */
public abstract class BiometricEnrollIntroduction extends BiometricEnrollBase implements LinkSpan.OnClickListener {
    private boolean mBiometricUnlockDisabledByAdmin;
    protected boolean mConfirmingCredentials;
    private TextView mErrorText;
    private boolean mHasPassword;
    private boolean mHasScrolledToBottom = false;
    private PorterDuffColorFilter mIconColorFilter;
    protected boolean mNextClicked;
    private boolean mParentalConsentRequired;
    private UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public interface GenerateChallengeCallback {
        void onChallengeGenerated(int i, int i2, long j);
    }

    private static boolean isResultSkipOrFinished(int i) {
        return i == 2 || i == 11 || i == 1;
    }

    protected abstract int checkMaxEnrolled();

    protected abstract int getAgreeButtonTextRes();

    protected abstract void getChallenge(GenerateChallengeCallback generateChallengeCallback);

    protected abstract int getConfirmLockTitleResId();

    protected abstract String getDescriptionDisabledByAdmin();

    protected abstract Intent getEnrollingIntent();

    protected abstract TextView getErrorTextView();

    protected abstract String getExtraKeyForBiometric();

    protected abstract int getHeaderResDefault();

    protected abstract int getHeaderResDisabledByAdmin();

    protected abstract int getLayoutResource();

    public abstract int getModality();

    protected abstract int getMoreButtonTextRes();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public abstract FooterButton getNextButton();

    protected abstract FooterButton getPrimaryFooterButton();

    protected abstract FooterButton getSecondaryFooterButton();

    protected abstract boolean isDisabledByAdmin();

    protected boolean onSetOrConfirmCredentials(Intent intent) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mConfirmingCredentials = bundle.getBoolean("confirming_credentials");
            this.mHasScrolledToBottom = bundle.getBoolean("scrolled");
        }
        Intent intent = getIntent();
        if (intent.getStringExtra("theme") == null) {
            intent.putExtra("theme", SetupWizardUtils.getThemeString(intent));
        }
        this.mBiometricUnlockDisabledByAdmin = isDisabledByAdmin();
        setContentView(getLayoutResource());
        boolean z = ParentalControlsUtils.parentConsentRequired(this, getModality()) != null;
        this.mParentalConsentRequired = z;
        if (this.mBiometricUnlockDisabledByAdmin && !z) {
            setHeaderText(getHeaderResDisabledByAdmin());
        } else {
            setHeaderText(getHeaderResDefault());
        }
        this.mErrorText = getErrorTextView();
        this.mUserManager = UserManager.get(this);
        updatePasswordQuality();
        if (!this.mConfirmingCredentials) {
            if (!this.mHasPassword) {
                this.mConfirmingCredentials = true;
                launchChooseLock();
            } else if (!BiometricUtils.containsGatekeeperPasswordHandle(getIntent()) && this.mToken == null) {
                this.mConfirmingCredentials = true;
                launchConfirmLock(getConfirmLockTitleResId());
            }
        }
        GlifLayout layout = getLayout();
        FooterBarMixin footerBarMixin = (FooterBarMixin) layout.getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(getPrimaryFooterButton());
        this.mFooterBarMixin.setSecondaryButton(getSecondaryFooterButton(), true);
        this.mFooterBarMixin.getSecondaryButton().setVisibility(this.mHasScrolledToBottom ? 0 : 4);
        RequireScrollMixin requireScrollMixin = (RequireScrollMixin) layout.getMixin(RequireScrollMixin.class);
        requireScrollMixin.requireScrollWithButton(this, getPrimaryFooterButton(), getMoreButtonTextRes(), new View.OnClickListener() { // from class: com.android.settings.biometrics.BiometricEnrollIntroduction$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BiometricEnrollIntroduction.this.onNextButtonClick(view);
            }
        });
        requireScrollMixin.setOnRequireScrollStateChangedListener(new RequireScrollMixin.OnRequireScrollStateChangedListener() { // from class: com.android.settings.biometrics.BiometricEnrollIntroduction$$ExternalSyntheticLambda1
            @Override // com.google.android.setupdesign.template.RequireScrollMixin.OnRequireScrollStateChangedListener
            public final void onRequireScrollStateChanged(boolean z2) {
                BiometricEnrollIntroduction.this.lambda$onCreate$0(z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(boolean z) {
        int agreeButtonTextRes;
        if (!(checkMaxEnrolled() != 0)) {
            if (z) {
                agreeButtonTextRes = getMoreButtonTextRes();
            } else {
                agreeButtonTextRes = getAgreeButtonTextRes();
            }
            getPrimaryFooterButton().setText(this, agreeButtonTextRes);
        }
        if (z) {
            return;
        }
        getSecondaryFooterButton().setVisibility(0);
        this.mHasScrolledToBottom = true;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        int checkMaxEnrolled = checkMaxEnrolled();
        if (checkMaxEnrolled == 0) {
            this.mErrorText.setText((CharSequence) null);
            this.mErrorText.setVisibility(8);
            getNextButton().setVisibility(0);
            return;
        }
        this.mErrorText.setText(checkMaxEnrolled);
        this.mErrorText.setVisibility(0);
        getNextButton().setText(getResources().getString(R$string.done));
        getNextButton().setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("confirming_credentials", this.mConfirmingCredentials);
        bundle.putBoolean("scrolled", this.mHasScrolledToBottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public boolean shouldFinishWhenBackgrounded() {
        return (!super.shouldFinishWhenBackgrounded() || this.mConfirmingCredentials || this.mNextClicked) ? false : true;
    }

    private void updatePasswordQuality() {
        this.mHasPassword = new LockPatternUtils(this).getActivePasswordQuality(this.mUserManager.getCredentialOwnerProfile(this.mUserId)) != 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        this.mNextClicked = true;
        if (checkMaxEnrolled() == 0) {
            launchNextEnrollingActivity(this.mToken);
        } else if (BiometricUtils.tryStartingNextBiometricEnroll(this, 6, "enrollIntroduction#onNextButtonClicked")) {
        } else {
            setResult(1);
            finish();
        }
    }

    private void launchChooseLock() {
        Intent chooseLockIntent = BiometricUtils.getChooseLockIntent(this, getIntent());
        chooseLockIntent.putExtra("hide_insecure_options", true);
        chooseLockIntent.putExtra("request_gk_pw_handle", true);
        chooseLockIntent.putExtra(getExtraKeyForBiometric(), true);
        int i = this.mUserId;
        if (i != -10000) {
            chooseLockIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        startActivityForResult(chooseLockIntent, 1);
    }

    private void launchNextEnrollingActivity(byte[] bArr) {
        Intent enrollingIntent = getEnrollingIntent();
        if (bArr != null) {
            enrollingIntent.putExtra("hw_auth_token", bArr);
        }
        int i = this.mUserId;
        if (i != -10000) {
            enrollingIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        BiometricUtils.copyMultiBiometricExtras(getIntent(), enrollingIntent);
        enrollingIntent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        enrollingIntent.putExtra("challenge", this.mChallenge);
        enrollingIntent.putExtra("sensor_id", this.mSensorId);
        startActivityForResult(enrollingIntent, 2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, final Intent intent) {
        if (i == 2) {
            if (isResultSkipOrFinished(i2)) {
                handleBiometricResultSkipOrFinished(i2, intent);
            } else if (i2 == 3) {
                setResult(i2, intent);
                finish();
            }
        } else if (i == 1) {
            this.mConfirmingCredentials = false;
            if (i2 == 1) {
                updatePasswordQuality();
                if (!onSetOrConfirmCredentials(intent)) {
                    overridePendingTransition(R$anim.sud_slide_next_in, R$anim.sud_slide_next_out);
                    getNextButton().setEnabled(false);
                    getChallenge(new GenerateChallengeCallback() { // from class: com.android.settings.biometrics.BiometricEnrollIntroduction$$ExternalSyntheticLambda2
                        @Override // com.android.settings.biometrics.BiometricEnrollIntroduction.GenerateChallengeCallback
                        public final void onChallengeGenerated(int i3, int i4, long j) {
                            BiometricEnrollIntroduction.this.lambda$onActivityResult$1(intent, i3, i4, j);
                        }
                    });
                }
            } else {
                setResult(i2, intent);
                finish();
            }
        } else if (i == 4) {
            this.mConfirmingCredentials = false;
            if (i2 == -1 && intent != null) {
                if (!onSetOrConfirmCredentials(intent)) {
                    overridePendingTransition(R$anim.sud_slide_next_in, R$anim.sud_slide_next_out);
                    getNextButton().setEnabled(false);
                    getChallenge(new GenerateChallengeCallback() { // from class: com.android.settings.biometrics.BiometricEnrollIntroduction$$ExternalSyntheticLambda3
                        @Override // com.android.settings.biometrics.BiometricEnrollIntroduction.GenerateChallengeCallback
                        public final void onChallengeGenerated(int i3, int i4, long j) {
                            BiometricEnrollIntroduction.this.lambda$onActivityResult$2(intent, i3, i4, j);
                        }
                    });
                }
            } else {
                setResult(i2, intent);
                finish();
            }
        } else if (i == 3) {
            overridePendingTransition(R$anim.sud_slide_back_in, R$anim.sud_slide_back_out);
        } else if (i == 6) {
            Log.d("BiometricEnrollIntroduction", "ENROLL_NEXT_BIOMETRIC_REQUEST, result: " + i2);
            if (isResultSkipOrFinished(i2)) {
                handleBiometricResultSkipOrFinished(i2, intent);
            } else if (i2 != 0) {
                setResult(i2, intent);
                finish();
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$1(Intent intent, int i, int i2, long j) {
        this.mSensorId = i;
        this.mChallenge = j;
        this.mToken = BiometricUtils.requestGatekeeperHat(this, intent, this.mUserId, j);
        BiometricUtils.removeGatekeeperPasswordHandle(this, intent);
        getNextButton().setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$2(Intent intent, int i, int i2, long j) {
        this.mSensorId = i;
        this.mChallenge = j;
        this.mToken = BiometricUtils.requestGatekeeperHat(this, intent, this.mUserId, j);
        BiometricUtils.removeGatekeeperPasswordHandle(this, intent);
        getNextButton().setEnabled(true);
    }

    private void handleBiometricResultSkipOrFinished(int i, Intent intent) {
        if (intent != null && intent.getBooleanExtra("skip_pending_enroll", false)) {
            getIntent().removeExtra("enroll_after_face");
        }
        if (i == 2) {
            onEnrollmentSkipped(intent);
        } else if (i == 1) {
            onFinishedEnrolling(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        onEnrollmentSkipped(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onEnrollmentSkipped(Intent intent) {
        setResult(2, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFinishedEnrolling(Intent intent) {
        setResult(1, intent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public void initViews() {
        super.initViews();
        if (!this.mBiometricUnlockDisabledByAdmin || this.mParentalConsentRequired) {
            return;
        }
        setDescriptionText(getDescriptionDisabledByAdmin());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PorterDuffColorFilter getIconColorFilter() {
        if (this.mIconColorFilter == null) {
            this.mIconColorFilter = new PorterDuffColorFilter(DynamicColorPalette.getColor(this, 0), PorterDuff.Mode.SRC_IN);
        }
        return this.mIconColorFilter;
    }
}
