package com.android.settings.biometrics.fingerprint;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$id;
import com.android.settings.R$integer;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollIntroduction;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.span.LinkSpan;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FingerprintEnrollIntroduction extends BiometricEnrollIntroduction {
    private DevicePolicyManager mDevicePolicyManager;
    @VisibleForTesting
    private FingerprintManager mFingerprintManager;
    private FooterButton mPrimaryFooterButton;
    private FooterButton mSecondaryFooterButton;

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected String getExtraKeyForBiometric() {
        return "for_fingerprint";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 243;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getModality() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        this.mFingerprintManager = fingerprintManagerOrNull;
        if (fingerprintManagerOrNull == null) {
            Log.e("FingerprintIntro", "Null FingerprintManager");
            finish();
            return;
        }
        super.onCreate(bundle);
        this.mDevicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        ((ImageView) findViewById(R$id.icon_fingerprint)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(R$id.icon_device_locked)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(R$id.icon_trash_can)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(R$id.icon_info)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(R$id.icon_link)).getDrawable().setColorFilter(getIconColorFilter());
        String string = getResources().getString(R$string.security_settings_fingerprint_v2_enroll_introduction_message_learn_more);
        LinearLayout linearLayout = (LinearLayout) findViewById(R$id.message_learn_more);
        if (string == null || string.length() == 0) {
            linearLayout.setVisibility(8);
        }
        ((TextView) findViewById(R$id.footer_message_2)).setText(getFooterMessage2());
        ((TextView) findViewById(R$id.footer_message_3)).setText(getFooterMessage3());
        ((TextView) findViewById(R$id.footer_message_4)).setText(getFooterMessage4());
        ((TextView) findViewById(R$id.footer_message_5)).setText(getFooterMessage5());
        ((TextView) findViewById(R$id.footer_message_6)).setText(getFooterMessage6());
        ((TextView) findViewById(R$id.footer_title_1)).setText(getFooterTitle1());
        ((TextView) findViewById(R$id.footer_title_2)).setText(getFooterTitle2());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        boolean z = false;
        boolean z2 = i == 2 || i == 6;
        if (i2 == 2 || i2 == 11 || i2 == 1) {
            z = true;
        }
        if (z2 && z) {
            intent = setSkipPendingEnroll(intent);
        }
        super.onActivityResult(i, i2, intent);
    }

    protected void onCancelButtonClick(View view) {
        setResult(2, setSkipPendingEnroll(new Intent()));
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onSkipButtonClick(View view) {
        onCancelButtonClick(view);
    }

    int getNegativeButtonTextId() {
        return R$string.security_settings_fingerprint_enroll_introduction_no_thanks;
    }

    protected int getFooterTitle1() {
        return R$string.security_settings_fingerprint_enroll_introduction_footer_title_1;
    }

    protected int getFooterTitle2() {
        return R$string.security_settings_fingerprint_enroll_introduction_footer_title_2;
    }

    protected int getFooterMessage2() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_2;
    }

    protected int getFooterMessage3() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_3;
    }

    protected int getFooterMessage4() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_4;
    }

    protected int getFooterMessage5() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_5;
    }

    protected int getFooterMessage6() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_6;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected boolean isDisabledByAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 32, this.mUserId) != null;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getLayoutResource() {
        return R$layout.fingerprint_enroll_introduction;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getHeaderResDisabledByAdmin() {
        return R$string.security_settings_fingerprint_enroll_introduction_title_unlock_disabled;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getHeaderResDefault() {
        return R$string.security_settings_fingerprint_enroll_introduction_title;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected String getDescriptionDisabledByAdmin() {
        return this.mDevicePolicyManager.getResources().getString("Settings.FINGERPRINT_UNLOCK_DISABLED", new Supplier() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getDescriptionDisabledByAdmin$0;
                lambda$getDescriptionDisabledByAdmin$0 = FingerprintEnrollIntroduction.this.lambda$getDescriptionDisabledByAdmin$0();
                return lambda$getDescriptionDisabledByAdmin$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getDescriptionDisabledByAdmin$0() {
        return getString(R$string.security_settings_fingerprint_enroll_introduction_message_unlock_disabled);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FooterButton getCancelButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getSecondaryButton();
        }
        return null;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase
    protected FooterButton getNextButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getPrimaryButton();
        }
        return null;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected TextView getErrorTextView() {
        return (TextView) findViewById(R$id.error_text);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int checkMaxEnrolled() {
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        boolean isDeferredSetupWizard = WizardManagerHelper.isDeferredSetupWizard(getIntent());
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        if (fingerprintManager != null) {
            int i = ((FingerprintSensorPropertiesInternal) fingerprintManager.getSensorPropertiesInternal().get(0)).maxEnrollmentsPerUser;
            int size = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size();
            int integer = getApplicationContext().getResources().getInteger(R$integer.suw_max_fingerprints_enrollable);
            if (!isAnySetupWizard || isDeferredSetupWizard) {
                if (size >= i) {
                    return R$string.fingerprint_intro_error_max;
                }
                return 0;
            } else if (size >= integer) {
                return R$string.fingerprint_intro_error_max;
            } else {
                return 0;
            }
        }
        return R$string.fingerprint_intro_error_unknown;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void getChallenge(final BiometricEnrollIntroduction.GenerateChallengeCallback generateChallengeCallback) {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        this.mFingerprintManager = fingerprintManagerOrNull;
        if (fingerprintManagerOrNull == null) {
            generateChallengeCallback.onChallengeGenerated(0, 0, 0L);
            return;
        }
        int i = this.mUserId;
        Objects.requireNonNull(generateChallengeCallback);
        fingerprintManagerOrNull.generateChallenge(i, new FingerprintManager.GenerateChallengeCallback() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction$$ExternalSyntheticLambda2
            public final void onChallengeGenerated(int i2, int i3, long j) {
                BiometricEnrollIntroduction.GenerateChallengeCallback.this.onChallengeGenerated(i2, i3, j);
            }
        });
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected Intent getEnrollingIntent() {
        Intent intent = new Intent(this, FingerprintEnrollFindSensor.class);
        if (BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
            intent.putExtra("gk_pw_handle", BiometricUtils.getGatekeeperPasswordHandle(getIntent()));
        }
        return intent;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getConfirmLockTitleResId() {
        return R$string.security_settings_fingerprint_preference_title;
    }

    @Override // com.google.android.setupdesign.span.LinkSpan.OnClickListener
    public void onClick(LinkSpan linkSpan) {
        if ("url".equals(linkSpan.getId())) {
            Intent helpIntent = HelpUtils.getHelpIntent(this, getString(R$string.help_url_fingerprint), getClass().getName());
            if (helpIntent == null) {
                Log.w("FingerprintIntro", "Null help intent.");
                return;
            }
            try {
                startActivityForResult(helpIntent, 3);
            } catch (ActivityNotFoundException e) {
                Log.w("FingerprintIntro", "Activity was not found for intent, " + e);
            }
        }
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected FooterButton getPrimaryFooterButton() {
        if (this.mPrimaryFooterButton == null) {
            this.mPrimaryFooterButton = new FooterButton.Builder(this).setText(R$string.security_settings_fingerprint_enroll_introduction_agree).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FingerprintEnrollIntroduction.this.onNextButtonClick(view);
                }
            }).setButtonType(6).setTheme(R$style.SudGlifButton_Primary).build();
        }
        return this.mPrimaryFooterButton;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected FooterButton getSecondaryFooterButton() {
        if (this.mSecondaryFooterButton == null) {
            this.mSecondaryFooterButton = new FooterButton.Builder(this).setText(getNegativeButtonTextId()).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FingerprintEnrollIntroduction.this.onSkipButtonClick(view);
                }
            }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build();
        }
        return this.mSecondaryFooterButton;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getAgreeButtonTextRes() {
        return R$string.security_settings_fingerprint_enroll_introduction_agree;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getMoreButtonTextRes() {
        return R$string.security_settings_face_enroll_introduction_more;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Intent setSkipPendingEnroll(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra("skip_pending_enroll", true);
        return intent;
    }
}
