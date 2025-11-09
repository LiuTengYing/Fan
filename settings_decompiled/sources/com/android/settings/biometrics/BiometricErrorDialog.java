package com.android.settings.biometrics;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public abstract class BiometricErrorDialog extends InstrumentedDialogFragment {
    public abstract int getOkButtonTextResId();

    public abstract int getTitleResId();

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence charSequence = getArguments().getCharSequence("error_msg");
        final int i = getArguments().getInt("error_id");
        builder.setTitle(getTitleResId()).setMessage(charSequence).setCancelable(false).setPositiveButton(getOkButtonTextResId(), new DialogInterface.OnClickListener() { // from class: com.android.settings.biometrics.BiometricErrorDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
                boolean z = i == 3;
                FragmentActivity activity = BiometricErrorDialog.this.getActivity();
                activity.setResult(z ? 3 : 1);
                activity.finish();
            }
        });
        AlertDialog create = builder.create();
        create.setCanceledOnTouchOutside(false);
        return create;
    }
}
