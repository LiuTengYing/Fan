package com.android.settings.biometrics.fingerprint;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class SetupFingerprintEnrollFindSensor extends FingerprintEnrollFindSensor {
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 247;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public Intent getFingerprintEnrollingIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollEnrolling.class);
        intent.putExtra("hw_auth_token", this.mToken);
        intent.putExtra("challenge", this.mChallenge);
        intent.putExtra("sensor_id", this.mSensorId);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor
    public void onSkipButtonClick(View view) {
        new SkipFingerprintDialog().show(getSupportFragmentManager());
    }

    /* loaded from: classes.dex */
    public static class SkipFingerprintDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 573;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return onCreateDialogBuilder().create();
        }

        public AlertDialog.Builder onCreateDialogBuilder() {
            return new AlertDialog.Builder(getContext()).setTitle(R$string.setup_fingerprint_enroll_skip_title).setPositiveButton(R$string.skip_anyway_button_label, this).setNegativeButton(R$string.go_back_button_label, this).setMessage(R$string.setup_fingerprint_enroll_skip_after_adding_lock_text);
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            FragmentActivity activity;
            if (i == -1 && (activity = getActivity()) != null) {
                activity.setResult(2);
                activity.finish();
            }
        }

        public void show(FragmentManager fragmentManager) {
            show(fragmentManager, "skip_dialog");
        }
    }
}
