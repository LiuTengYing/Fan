package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import com.android.settings.R$string;
import com.android.settings.SetupWizardUtils;
/* loaded from: classes.dex */
public class SetupFingerprintEnrollFinish extends FingerprintEnrollFinish {
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollFinish, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 248;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public Intent getFingerprintEnrollingIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollEnrolling.class);
        intent.putExtra("hw_auth_token", this.mToken);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public void initViews() {
        super.initViews();
        getNextButton().setText(this, R$string.next_label);
    }
}
