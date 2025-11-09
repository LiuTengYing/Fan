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
public class RebootConfirmationDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private final RebootConfirmationDialogHost mHost;
    private final int mMessageId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1914;
    }

    public static void show(Fragment fragment, int i, RebootConfirmationDialogHost rebootConfirmationDialogHost) {
        FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("FreeformPrefRebootDlg") == null) {
            new RebootConfirmationDialogFragment(i, rebootConfirmationDialogHost).show(supportFragmentManager, "FreeformPrefRebootDlg");
        }
    }

    private RebootConfirmationDialogFragment(int i, RebootConfirmationDialogHost rebootConfirmationDialogHost) {
        this.mMessageId = i;
        this.mHost = rebootConfirmationDialogHost;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(this.mMessageId).setPositiveButton(R$string.reboot_dialog_reboot_now, this).setNegativeButton(R$string.reboot_dialog_reboot_later, (DialogInterface.OnClickListener) null).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        this.mHost.onRebootConfirmed();
    }
}
