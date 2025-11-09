package com.android.settings.development;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class RebootWithMteDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1913;
    }

    public RebootWithMteDialog(Context context) {
        this.mContext = context;
    }

    public static void show(Context context, Fragment fragment) {
        FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("RebootWithMteDlg") == null) {
            RebootWithMteDialog rebootWithMteDialog = new RebootWithMteDialog(context);
            rebootWithMteDialog.setTargetFragment(fragment, 0);
            rebootWithMteDialog.show(supportFragmentManager, "RebootWithMteDlg");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R$string.reboot_with_mte_title).setMessage(R$string.reboot_with_mte_message).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setIcon(17301543).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        SystemProperties.set("arm64.memtag.bootctl", "memtag-once");
        ((PowerManager) this.mContext.getSystemService(PowerManager.class)).reboot(null);
    }
}
