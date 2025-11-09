package com.android.settings.biometrics.face;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class FaceEnrollParentalConsent extends FaceEnrollIntroduction {
    public static final int[] CONSENT_STRING_RESOURCES = {R$string.security_settings_face_enroll_consent_introduction_title, R$string.security_settings_face_enroll_introduction_consent_message, R$string.security_settings_face_enroll_introduction_info_consent_glasses, R$string.security_settings_face_enroll_introduction_info_consent_looking, R$string.security_settings_face_enroll_introduction_info_consent_gaze, R$string.security_settings_face_enroll_introduction_how_consent_message, R$string.security_settings_face_enroll_introduction_control_consent_title, R$string.security_settings_face_enroll_introduction_control_consent_message};

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected boolean generateChallengeOnCreate() {
        return false;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1893;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    protected boolean onSetOrConfirmCredentials(Intent intent) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setDescriptionText(R$string.security_settings_face_enroll_introduction_consent_message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onNextButtonClick(View view) {
        onConsentResult(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onSkipButtonClick(View view) {
        onConsentResult(false);
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void onEnrollmentSkipped(Intent intent) {
        onConsentResult(false);
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    protected void onFinishedEnrolling(Intent intent) {
        onConsentResult(true);
    }

    private void onConsentResult(boolean z) {
        Intent intent = new Intent();
        intent.putExtra("sensor_modality", 8);
        setResult(z ? 4 : 5, intent);
        finish();
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected int getInfoMessageGlasses() {
        return R$string.security_settings_face_enroll_introduction_info_consent_glasses;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected int getInfoMessageLooking() {
        return R$string.security_settings_face_enroll_introduction_info_consent_looking;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected int getInfoMessageRequireEyes() {
        return R$string.security_settings_face_enroll_introduction_info_consent_gaze;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected int getHowMessage() {
        return R$string.security_settings_face_enroll_introduction_how_consent_message;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected int getInControlTitle() {
        return R$string.security_settings_face_enroll_introduction_control_consent_title;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction
    protected int getInControlMessage() {
        return R$string.security_settings_face_enroll_introduction_control_consent_message;
    }

    @Override // com.android.settings.biometrics.face.FaceEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    protected int getHeaderResDefault() {
        return R$string.security_settings_face_enroll_consent_introduction_title;
    }
}
