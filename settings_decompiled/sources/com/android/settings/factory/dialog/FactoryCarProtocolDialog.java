package com.android.settings.factory.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$layout;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class FactoryCarProtocolDialog extends InstrumentedDialogFragment {
    private View mRootView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static void show(Fragment fragment, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("FactoryCarProtocolDialog") == null) {
            Bundle bundle = new Bundle();
            bundle.putString("arg_key_dialog_title", str);
            FactoryCarProtocolDialog factoryCarProtocolDialog = new FactoryCarProtocolDialog();
            factoryCarProtocolDialog.setArguments(bundle);
            factoryCarProtocolDialog.show(childFragmentManager, "FactoryCarProtocolDialog");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getActivity()).setTitle(getArguments().getString("arg_key_dialog_title")).setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        View inflate = LayoutInflater.from(positiveButton.getContext()).inflate(R$layout.dialog_sim_status, (ViewGroup) null);
        this.mRootView = inflate;
        AlertDialog create = positiveButton.setView(inflate).create();
        create.getWindow().setFlags(8192, 8192);
        return create;
    }
}
