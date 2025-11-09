package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class DisableLogPersistWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1225;
    }

    public static void show(LogPersistDialogHost logPersistDialogHost) {
        if (logPersistDialogHost instanceof Fragment) {
            Fragment fragment = (Fragment) logPersistDialogHost;
            FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
            if (supportFragmentManager.findFragmentByTag("DisableLogPersistDlg") == null) {
                DisableLogPersistWarningDialog disableLogPersistWarningDialog = new DisableLogPersistWarningDialog();
                disableLogPersistWarningDialog.setTargetFragment(fragment, 0);
                disableLogPersistWarningDialog.show(supportFragmentManager, "DisableLogPersistDlg");
            }
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R$string.dev_logpersist_clear_warning_title).setMessage(R$string.dev_logpersist_clear_warning_message).setPositiveButton(17039379, this).setNegativeButton(17039369, this).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        LogPersistDialogHost logPersistDialogHost = (LogPersistDialogHost) getTargetFragment();
        if (logPersistDialogHost == null) {
            return;
        }
        if (i == -1) {
            logPersistDialogHost.onDisableLogPersistDialogConfirmed();
        } else {
            logPersistDialogHost.onDisableLogPersistDialogRejected();
        }
    }
}
