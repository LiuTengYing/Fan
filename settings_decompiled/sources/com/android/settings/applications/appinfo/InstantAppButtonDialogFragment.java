package com.android.settings.applications.appinfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.overlay.FeatureFactory;
/* loaded from: classes.dex */
public class InstantAppButtonDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private String mPackageName;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 558;
    }

    public static InstantAppButtonDialogFragment newInstance(String str) {
        InstantAppButtonDialogFragment instantAppButtonDialogFragment = new InstantAppButtonDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("packageName", str);
        instantAppButtonDialogFragment.setArguments(bundle);
        return instantAppButtonDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        this.mPackageName = getArguments().getString("packageName");
        return createDialog();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        Context context = getContext();
        PackageManager packageManager = context.getPackageManager();
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, 923, this.mPackageName);
        packageManager.deletePackageAsUser(this.mPackageName, null, 0, UserHandle.myUserId());
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int i = R$string.clear_instant_app_data;
        return builder.setPositiveButton(i, this).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).setTitle(i).setMessage(R$string.clear_instant_app_confirmation).create();
    }
}
