package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class FingerprintEnrollParentalConsent extends FingerprintEnrollIntroduction {
    public static final int[] CONSENT_STRING_RESOURCES = {R$string.security_settings_fingerprint_enroll_consent_introduction_title, R$string.security_settings_fingerprint_enroll_introduction_consent_message, R$string.security_settings_fingerprint_enroll_introduction_footer_title_consent_1, R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_2, R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_3, R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_4, R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_5, R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_6};

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1892;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected boolean onSetOrConfirmCredentials(Intent intent) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setDescriptionText(R$string.security_settings_fingerprint_enroll_introduction_consent_message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onNextButtonClick(View view) {
        onConsentResult(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onSkipButtonClick(View view) {
        onConsentResult(false);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void onEnrollmentSkipped(Intent intent) {
        onConsentResult(false);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void onFinishedEnrolling(Intent intent) {
        onConsentResult(true);
    }

    private void onConsentResult(boolean z) {
        Intent intent = new Intent();
        intent.putExtra("sensor_modality", 2);
        setResult(z ? 4 : 5, intent);
        finish();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected int getFooterTitle1() {
        return R$string.security_settings_fingerprint_enroll_introduction_footer_title_consent_1;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected int getFooterMessage2() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_2;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected int getFooterMessage3() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_3;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected int getFooterMessage4() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_4;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected int getFooterMessage5() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_5;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected int getFooterMessage6() {
        return R$string.security_settings_fingerprint_v2_enroll_introduction_footer_message_consent_6;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getHeaderResDefault() {
        return R$string.security_settings_fingerprint_enroll_consent_introduction_title;
    }
}
