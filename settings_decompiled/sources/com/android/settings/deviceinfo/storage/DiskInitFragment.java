package com.android.settings.deviceinfo.storage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.StorageWizardInit;
/* loaded from: classes.dex */
public class DiskInitFragment extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 561;
    }

    public static void show(Fragment fragment, int i, String str) {
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extra.TEXT", i);
        bundle.putString("android.os.storage.extra.DISK_ID", str);
        DiskInitFragment diskInitFragment = new DiskInitFragment();
        diskInitFragment.setArguments(bundle);
        diskInitFragment.setTargetFragment(fragment, 0);
        diskInitFragment.show(fragment.getFragmentManager(), "disk_init");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        final FragmentActivity activity = getActivity();
        int i = getArguments().getInt("android.intent.extra.TEXT");
        final String string = getArguments().getString("android.os.storage.extra.DISK_ID");
        return new AlertDialog.Builder(activity).setMessage(TextUtils.expandTemplate(getText(i), ((StorageManager) activity.getSystemService(StorageManager.class)).findDiskById(string).getDescription())).setPositiveButton(R$string.storage_menu_set_up, new DialogInterface.OnClickListener() { // from class: com.android.settings.deviceinfo.storage.DiskInitFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                DiskInitFragment.this.lambda$onCreateDialog$0(activity, string, dialogInterface, i2);
            }
        }).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(Context context, String str, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(context, StorageWizardInit.class);
        intent.putExtra("android.os.storage.extra.DISK_ID", str);
        startActivity(intent);
    }
}
