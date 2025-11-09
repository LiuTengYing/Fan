package com.android.settingslib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;
/* loaded from: classes2.dex */
public class CustomEditTextPreferenceCompat extends EditTextPreference {
    private CustomPreferenceDialogFragment mFragment;

    protected void onClick(DialogInterface dialogInterface, int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogClosed(boolean z) {
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
    }

    public CustomEditTextPreferenceCompat(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CustomEditTextPreferenceCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomEditTextPreferenceCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomEditTextPreferenceCompat(Context context) {
        super(context);
    }

    public EditText getEditText() {
        Dialog dialog;
        CustomPreferenceDialogFragment customPreferenceDialogFragment = this.mFragment;
        if (customPreferenceDialogFragment == null || (dialog = customPreferenceDialogFragment.getDialog()) == null) {
            return null;
        }
        return (EditText) dialog.findViewById(16908291);
    }

    public Dialog getDialog() {
        CustomPreferenceDialogFragment customPreferenceDialogFragment = this.mFragment;
        if (customPreferenceDialogFragment != null) {
            return customPreferenceDialogFragment.getDialog();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onBindDialogView(View view) {
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(16385);
            editText.requestFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFragment(CustomPreferenceDialogFragment customPreferenceDialogFragment) {
        this.mFragment = customPreferenceDialogFragment;
    }

    /* loaded from: classes2.dex */
    public static class CustomPreferenceDialogFragment extends EditTextPreferenceDialogFragmentCompat {
        public static CustomPreferenceDialogFragment newInstance(String str) {
            CustomPreferenceDialogFragment customPreferenceDialogFragment = new CustomPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", str);
            customPreferenceDialogFragment.setArguments(bundle);
            return customPreferenceDialogFragment;
        }

        private CustomEditTextPreferenceCompat getCustomizablePreference() {
            return (CustomEditTextPreferenceCompat) getPreference();
        }

        @Override // androidx.preference.EditTextPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);
            getCustomizablePreference().onBindDialogView(view);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat
        protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            getCustomizablePreference().setFragment(this);
            getCustomizablePreference().onPrepareDialogBuilder(builder, this);
        }

        @Override // androidx.preference.EditTextPreferenceDialogFragmentCompat, androidx.preference.PreferenceDialogFragmentCompat
        public void onDialogClosed(boolean z) {
            super.onDialogClosed(z);
            getCustomizablePreference().onDialogClosed(z);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            super.onClick(dialogInterface, i);
            getCustomizablePreference().onClick(dialogInterface, i);
        }
    }
}
