package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
/* loaded from: classes.dex */
public class AlertDialogFragment extends BaseDialogFragment implements DialogInterface.OnClickListener {
    public static void show(FragmentActivity fragmentActivity, String str, String str2) {
        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putString("msg", str2);
        alertDialogFragment.setArguments(bundle);
        alertDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "AlertDialogFragment");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder positiveButton = new AlertDialog.Builder(getContext()).setTitle(getArguments().getString("title")).setPositiveButton(17039370, this);
        if (!TextUtils.isEmpty(getArguments().getString("msg"))) {
            positiveButton.setMessage(getArguments().getString("msg"));
        }
        return positiveButton.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (getActivity() != null) {
            getActivity().finish();
        }
        super.dismiss();
    }
}
