package com.android.settings.common.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$layout;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class CommonSteerSettingsDialogFragment extends InstrumentedDialogFragment {
    private View mRootView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
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
