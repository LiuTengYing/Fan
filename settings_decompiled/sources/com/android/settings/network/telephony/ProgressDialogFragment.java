package com.android.settings.network.telephony;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.android.settings.R$drawable;
/* loaded from: classes.dex */
public class ProgressDialogFragment extends DialogFragment {
    private OnDismissCallback mDismissCallback;

    /* loaded from: classes.dex */
    public interface OnDismissCallback {
        void onProgressDialogDismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onCreateDialog$0(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        return 4 == i;
    }

    public static void show(FragmentManager fragmentManager, String str, OnDismissCallback onDismissCallback) {
        ProgressDialogFragment progressDialogFragment = (ProgressDialogFragment) fragmentManager.findFragmentByTag("ProgressDialogFragment");
        if (progressDialogFragment == null || !TextUtils.equals(progressDialogFragment.getArguments().getString("title"), str)) {
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            if (progressDialogFragment != null) {
                beginTransaction.remove(progressDialogFragment);
            }
            ProgressDialogFragment progressDialogFragment2 = new ProgressDialogFragment();
            progressDialogFragment2.setDismissCallback(onDismissCallback);
            Bundle bundle = new Bundle();
            bundle.putString("title", str);
            progressDialogFragment2.setArguments(bundle);
            progressDialogFragment2.show(beginTransaction, "ProgressDialogFragment");
        }
    }

    public static void dismiss(FragmentManager fragmentManager) {
        DialogFragment dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag("ProgressDialogFragment");
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.getWindow().setBackgroundDrawableResource(R$drawable.sim_progress_dialog_rounded_bg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getArguments().getString("title"));
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.android.settings.network.telephony.ProgressDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnKeyListener
            public final boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                boolean lambda$onCreateDialog$0;
                lambda$onCreateDialog$0 = ProgressDialogFragment.lambda$onCreateDialog$0(dialogInterface, i, keyEvent);
                return lambda$onCreateDialog$0;
            }
        });
        return progressDialog;
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        OnDismissCallback onDismissCallback = this.mDismissCallback;
        if (onDismissCallback != null) {
            onDismissCallback.onProgressDialogDismiss();
        }
    }

    private void setDismissCallback(OnDismissCallback onDismissCallback) {
        this.mDismissCallback = onDismissCallback;
    }
}
