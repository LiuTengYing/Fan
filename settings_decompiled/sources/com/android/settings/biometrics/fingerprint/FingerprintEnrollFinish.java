package com.android.settings.biometrics.fingerprint;

import android.content.ComponentName;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.settings.R$anim;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
/* loaded from: classes.dex */
public class FingerprintEnrollFinish extends BiometricEnrollBase {
    static final String FINGERPRINT_SUGGESTION_ACTIVITY = "com.android.settings.SetupFingerprintSuggestionActivity";
    static final int REQUEST_ADD_ANOTHER = 1;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 242;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.fingerprint_enroll_finish);
        setHeaderText(R$string.security_settings_fingerprint_enroll_finish_title);
        setDescriptionText(R$string.security_settings_fingerprint_enroll_finish_v2_message);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.fingerprint_enroll_button_add).setButtonType(7).setTheme(R$style.SudGlifButton_Secondary).build());
        this.mFooterBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_fingerprint_enroll_done).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFinish$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FingerprintEnrollFinish.this.onNextButtonClick(view);
            }
        }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        updateFingerprintSuggestionEnableState();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        FooterButton secondaryButton = this.mFooterBarMixin.getSecondaryButton();
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        boolean z = false;
        if (fingerprintManagerOrNull != null) {
            if (fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size() >= ((FingerprintSensorPropertiesInternal) fingerprintManagerOrNull.getSensorPropertiesInternal().get(0)).maxEnrollmentsPerUser) {
                z = true;
            }
        }
        if (z) {
            secondaryButton.setVisibility(4);
        } else {
            secondaryButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollFinish$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FingerprintEnrollFinish.this.onAddAnotherButtonClick(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        updateFingerprintSuggestionEnableState();
        setResult(1);
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            postEnroll();
        } else if (this.mFromSettingsSummary) {
            launchFingerprintSettings();
        }
        finish();
    }

    private void updateFingerprintSuggestionEnableState() {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            int size = fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size();
            getPackageManager().setComponentEnabledSetting(new ComponentName(getApplicationContext(), FINGERPRINT_SUGGESTION_ACTIVITY), size == 1 ? 1 : 2, 1);
            StringBuilder sb = new StringBuilder();
            sb.append("com.android.settings.SetupFingerprintSuggestionActivity enabled state = ");
            sb.append(size == 1);
            Log.d("FingerprintEnrollFinish", sb.toString());
        }
    }

    private void postEnroll() {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            fingerprintManagerOrNull.revokeChallenge(this.mUserId, this.mChallenge);
        }
    }

    private void launchFingerprintSettings() {
        Intent intent = new Intent("android.settings.FINGERPRINT_SETTINGS");
        intent.setPackage("com.android.settings");
        intent.putExtra("hw_auth_token", this.mToken);
        intent.setFlags(603979776);
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        intent.putExtra("challenge", this.mChallenge);
        startActivity(intent);
        overridePendingTransition(R$anim.sud_slide_back_in, R$anim.sud_slide_back_out);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAddAnotherButtonClick(View view) {
        startActivityForResult(getFingerprintEnrollingIntent(), 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        updateFingerprintSuggestionEnableState();
        if (i == 1 && i2 != 0) {
            setResult(i2, intent);
            finish();
            return;
        }
        super.onActivityResult(i, i2, intent);
    }
}
