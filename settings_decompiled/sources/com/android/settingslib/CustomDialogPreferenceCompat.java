package com.android.settingslib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
/* loaded from: classes2.dex */
public class CustomDialogPreferenceCompat extends DialogPreference {
    private CustomPreferenceDialogFragment mFragment;
    private DialogInterface.OnShowListener mOnShowListener;

    /* JADX INFO: Access modifiers changed from: protected */
    public void onBindDialogView(View view) {
    }

    protected void onClick(DialogInterface dialogInterface, int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDialogClosed(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
    }

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomDialogPreferenceCompat(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomDialogPreferenceCompat(Context context) {
        super(context);
    }

    public Dialog getDialog() {
        CustomPreferenceDialogFragment customPreferenceDialogFragment = this.mFragment;
        if (customPreferenceDialogFragment != null) {
            return customPreferenceDialogFragment.getDialog();
        }
        return null;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mOnShowListener = onShowListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFragment(CustomPreferenceDialogFragment customPreferenceDialogFragment) {
        this.mFragment = customPreferenceDialogFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DialogInterface.OnShowListener getOnShowListener() {
        return this.mOnShowListener;
    }

    /* loaded from: classes2.dex */
    public static class CustomPreferenceDialogFragment extends PreferenceDialogFragmentCompat {
        public static CustomPreferenceDialogFragment newInstance(String str) {
            CustomPreferenceDialogFragment customPreferenceDialogFragment = new CustomPreferenceDialogFragment();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", str);
            customPreferenceDialogFragment.setArguments(bundle);
            return customPreferenceDialogFragment;
        }

        private CustomDialogPreferenceCompat getCustomizablePreference() {
            return (CustomDialogPreferenceCompat) getPreference();
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat
        protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
            super.onPrepareDialogBuilder(builder);
            getCustomizablePreference().setFragment(this);
            getCustomizablePreference().onPrepareDialogBuilder(builder, this);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat
        public void onDialogClosed(boolean z) {
            getCustomizablePreference().onDialogClosed(z);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat
        protected void onBindDialogView(View view) {
            super.onBindDialogView(view);
            getCustomizablePreference().onBindDialogView(view);
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Dialog onCreateDialog = super.onCreateDialog(bundle);
            onCreateDialog.setOnShowListener(getCustomizablePreference().getOnShowListener());
            return onCreateDialog;
        }

        @Override // androidx.preference.PreferenceDialogFragmentCompat, android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            super.onClick(dialogInterface, i);
            getCustomizablePreference().onClick(dialogInterface, i);
        }
    }
}
