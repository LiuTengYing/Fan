package com.android.settings.deletionhelper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class ActivationWarningFragment extends DialogFragment {
    public static ActivationWarningFragment newInstance() {
        return new ActivationWarningFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(R$string.automatic_storage_manager_activation_warning).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).create();
    }
}
