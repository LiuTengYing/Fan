package com.android.settings.network;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.helper.ConfirmationSimDeletionPredicate;
import com.android.settings.system.ResetDashboardFragment;
import com.android.settings.wifi.dpp.WifiDppUtils;
/* loaded from: classes.dex */
public class EraseEuiccDataDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1857;
    }

    public static void show(ResetDashboardFragment resetDashboardFragment) {
        EraseEuiccDataDialogFragment eraseEuiccDataDialogFragment = new EraseEuiccDataDialogFragment();
        eraseEuiccDataDialogFragment.setTargetFragment(resetDashboardFragment, 0);
        eraseEuiccDataDialogFragment.show(resetDashboardFragment.getActivity().getSupportFragmentManager(), "EraseEuiccDataDlg");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R$string.reset_esim_title).setMessage(R$string.reset_esim_desc).setPositiveButton(R$string.erase_euicc_data_button, this).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).setOnDismissListener(this).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (!(getTargetFragment() instanceof ResetDashboardFragment)) {
            Log.e("EraseEuiccDataDlg", "getTargetFragment return unexpected type");
        }
        if (i == -1) {
            final Context context = getContext();
            if (ConfirmationSimDeletionPredicate.getSingleton().test(context)) {
                WifiDppUtils.showLockScreen(context, new Runnable() { // from class: com.android.settings.network.EraseEuiccDataDialogFragment$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EraseEuiccDataDialogFragment.this.lambda$onClick$0(context);
                    }
                });
            } else {
                lambda$onClick$0(context);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: runAsyncWipe */
    public void lambda$onClick$0(final Context context) {
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.network.EraseEuiccDataDialogFragment.1
            @Override // java.lang.Runnable
            public void run() {
                RecoverySystem.wipeEuiccData(context, "com.android.settings.network");
            }
        });
    }
}
