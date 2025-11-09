package com.android.settings.biometrics.fingerprint;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricErrorDialog;
/* loaded from: classes.dex */
public class FingerprintErrorDialog extends BiometricErrorDialog {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 569;
    }

    public static void showErrorDialog(BiometricEnrollBase biometricEnrollBase, int i) {
        if (biometricEnrollBase.isFinishing()) {
            return;
        }
        FragmentManager supportFragmentManager = biometricEnrollBase.getSupportFragmentManager();
        if (supportFragmentManager.isDestroyed() || supportFragmentManager.isStateSaved()) {
            return;
        }
        newInstance(biometricEnrollBase.getText(getErrorMessage(i)), i).show(supportFragmentManager, FingerprintErrorDialog.class.getName());
    }

    private static int getErrorMessage(int i) {
        if (i != 3) {
            if (i == 18) {
                return R$string.security_settings_fingerprint_bad_calibration;
            }
            return R$string.security_settings_fingerprint_enroll_error_generic_dialog_message;
        }
        return R$string.security_settings_fingerprint_enroll_error_timeout_dialog_message;
    }

    private static FingerprintErrorDialog newInstance(CharSequence charSequence, int i) {
        FingerprintErrorDialog fingerprintErrorDialog = new FingerprintErrorDialog();
        Bundle bundle = new Bundle();
        bundle.putCharSequence("error_msg", charSequence);
        bundle.putInt("error_id", i);
        fingerprintErrorDialog.setArguments(bundle);
        return fingerprintErrorDialog;
    }

    @Override // com.android.settings.biometrics.BiometricErrorDialog
    public int getTitleResId() {
        return R$string.security_settings_fingerprint_enroll_error_dialog_title;
    }

    @Override // com.android.settings.biometrics.BiometricErrorDialog
    public int getOkButtonTextResId() {
        return R$string.security_settings_fingerprint_enroll_dialog_ok;
    }
}
