package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import com.android.settings.SetupWizardUtils;
/* loaded from: classes.dex */
public class SetupFingerprintEnrollEnrolling extends FingerprintEnrollEnrolling {
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 246;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling, com.android.settings.biometrics.BiometricsEnrollEnrolling
    protected Intent getFinishIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollFinish.class);
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }
}
