package com.android.settings.development.tare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.settings.R$array;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class TareFactorDialogFragment extends DialogFragment {
    private final String mFactorKey;
    private final int mFactorPolicy;
    private final String mFactorTitle;
    private final long mFactorValue;
    private EditText mFactorValueView;
    private final TareFactorController mTareFactorController;
    private Spinner mUnitSpinner;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
    }

    public TareFactorDialogFragment(String str, String str2, long j, int i, TareFactorController tareFactorController) {
        this.mFactorTitle = str;
        this.mFactorKey = str2;
        this.mFactorValue = j;
        this.mFactorPolicy = i;
        this.mTareFactorController = tareFactorController;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(this.mFactorTitle).setView(createDialogView()).setPositiveButton(R$string.tare_dialog_confirm_button_title, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.tare.TareFactorDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TareFactorDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.tare.TareFactorDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                TareFactorDialogFragment.lambda$onCreateDialog$1(dialogInterface, i);
            }
        }).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        String obj = this.mFactorValueView.getText().toString();
        long j = this.mFactorValue;
        try {
            j = Long.parseLong(obj);
            if (this.mUnitSpinner.getSelectedItemPosition() == 0) {
                j *= 1000000000;
            }
        } catch (NumberFormatException e) {
            Log.e("TareDialogFragment", "Error parsing '" + obj + "'. Using " + this.mFactorValue + " instead", e);
        }
        this.mTareFactorController.updateValue(this.mFactorKey, j, this.mFactorPolicy);
    }

    private View createDialogView() {
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(R$layout.dialog_edittext_dropdown, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(R$id.edittext);
        this.mFactorValueView = editText;
        editText.setInputType(2);
        this.mUnitSpinner = (Spinner) inflate.findViewById(R$id.spinner);
        this.mUnitSpinner.setAdapter((SpinnerAdapter) new ArrayAdapter(getActivity(), 17367048, getResources().getStringArray(R$array.tare_units)));
        long j = this.mFactorValue;
        int i = 0;
        if (j % 1000000000 == 0) {
            this.mFactorValueView.setText(String.format("%d", Long.valueOf(j / 1000000000)));
        } else {
            this.mFactorValueView.setText(String.format("%d", Long.valueOf(j)));
            i = 1;
        }
        this.mUnitSpinner.setSelection(i);
        this.mUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(i) { // from class: com.android.settings.development.tare.TareFactorDialogFragment.1
            private int mSelectedPosition;
            final /* synthetic */ int val$unitIdx;

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            {
                this.val$unitIdx = i;
                this.mSelectedPosition = i;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i2, long j2) {
                if (this.mSelectedPosition == i2) {
                    return;
                }
                this.mSelectedPosition = i2;
                String obj = TareFactorDialogFragment.this.mFactorValueView.getText().toString();
                try {
                    long parseLong = Long.parseLong(obj);
                    TareFactorDialogFragment.this.mFactorValueView.setText(String.format("%d", Long.valueOf(TareFactorDialogFragment.this.mUnitSpinner.getSelectedItemPosition() == 0 ? parseLong / 1000000000 : parseLong * 1000000000)));
                } catch (NumberFormatException e) {
                    Log.e("TareDialogFragment", "Error parsing '" + obj + "'", e);
                }
            }
        });
        Utils.setEditTextCursorPosition(this.mFactorValueView);
        return inflate;
    }
}
