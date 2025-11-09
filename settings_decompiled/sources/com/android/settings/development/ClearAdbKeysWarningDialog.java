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
public class ClearAdbKeysWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1223;
    }

    public static void show(Fragment fragment) {
        FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("ClearAdbKeysDlg") == null) {
            ClearAdbKeysWarningDialog clearAdbKeysWarningDialog = new ClearAdbKeysWarningDialog();
            clearAdbKeysWarningDialog.setTargetFragment(fragment, 0);
            clearAdbKeysWarningDialog.show(supportFragmentManager, "ClearAdbKeysDlg");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(R$string.adb_keys_warning_message).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        AdbClearKeysDialogHost adbClearKeysDialogHost = (AdbClearKeysDialogHost) getTargetFragment();
        if (adbClearKeysDialogHost == null) {
            return;
        }
        adbClearKeysDialogHost.onAdbClearKeysDialogConfirmed();
    }
}
