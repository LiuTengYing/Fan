package com.android.settings.biometrics.fingerprint;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricUtils;
/* loaded from: classes.dex */
public class SetupFingerprintEnrollIntroduction extends FingerprintEnrollIntroduction {
    private boolean mAlreadyHadLockScreenSetup = false;

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 249;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            this.mAlreadyHadLockScreenSetup = isKeyguardSecure();
        } else {
            this.mAlreadyHadLockScreenSetup = bundle.getBoolean("wasLockScreenPresent", false);
        }
    }

    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("wasLockScreenPresent", this.mAlreadyHadLockScreenSetup);
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction
    protected Intent getEnrollingIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollFindSensor.class);
        if (BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
            intent.putExtra("gk_pw_handle", BiometricUtils.getGatekeeperPasswordHandle(getIntent()));
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollIntroduction, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2 && isKeyguardSecure()) {
            if (!this.mAlreadyHadLockScreenSetup) {
                intent = getMetricIntent(intent);
            }
            if (i2 == 1) {
                intent = setFingerprintCount(intent);
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    private Intent getMetricIntent(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra(":settings:password_quality", new LockPatternUtils(this).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
        return intent;
    }

    private Intent setFingerprintCount(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            intent.putExtra("fingerprint_enrolled_count", fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size());
        }
        return intent;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    protected void onCancelButtonClick(View view) {
        int i;
        Intent intent = null;
        if (isKeyguardSecure()) {
            i = 2;
            if (!this.mAlreadyHadLockScreenSetup) {
                intent = getMetricIntent(null);
            }
        } else {
            i = 11;
        }
        setResult(i, FingerprintEnrollIntroduction.setSkipPendingEnroll(intent));
        finish();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (!this.mAlreadyHadLockScreenSetup && isKeyguardSecure()) {
            setResult(0, getMetricIntent(null));
        }
        super.onBackPressed();
    }

    private boolean isKeyguardSecure() {
        return ((KeyguardManager) getSystemService(KeyguardManager.class)).isKeyguardSecure();
    }
}
