package com.android.settings.biometrics.fingerprint;

import android.app.Activity;
import android.hardware.fingerprint.FingerprintManager;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.biometrics.BiometricEnrollSidecar;
/* loaded from: classes.dex */
public class FingerprintEnrollSidecar extends BiometricEnrollSidecar {
    private int mEnrollReason;
    private FingerprintManager.EnrollmentCallback mEnrollmentCallback = new FingerprintManager.EnrollmentCallback() { // from class: com.android.settings.biometrics.fingerprint.FingerprintEnrollSidecar.1
        public void onEnrollmentProgress(int i) {
            FingerprintEnrollSidecar.super.onEnrollmentProgress(i);
        }

        public void onEnrollmentHelp(int i, CharSequence charSequence) {
            FingerprintEnrollSidecar.super.onEnrollmentHelp(i, charSequence);
        }

        public void onEnrollmentError(int i, CharSequence charSequence) {
            FingerprintEnrollSidecar.super.onEnrollmentError(i, charSequence);
        }
    };
    private FingerprintUpdater mFingerprintUpdater;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 245;
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar, androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mFingerprintUpdater = new FingerprintUpdater(activity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollSidecar
    public void startEnrollment() {
        super.startEnrollment();
        byte[] bArr = this.mToken;
        if (bArr == null) {
            Log.e("FingerprintEnrollSidecar", "Null hardware auth token for enroll");
            onEnrollmentError(1, getString(R$string.fingerprint_intro_error_unknown));
            return;
        }
        this.mFingerprintUpdater.enroll(bArr, this.mEnrollmentCancel, this.mUserId, this.mEnrollmentCallback, this.mEnrollReason);
    }

    public void setEnrollReason(int i) {
        this.mEnrollReason = i;
    }
}
