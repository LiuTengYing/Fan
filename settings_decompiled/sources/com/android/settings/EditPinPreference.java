package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import com.android.settingslib.CustomEditTextPreferenceCompat;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class EditPinPreference extends CustomEditTextPreferenceCompat {
    private boolean mIsShowing;
    private OnPinEnteredListener mPinListener;

    /* loaded from: classes.dex */
    interface OnPinEnteredListener {
        void onPinEntered(EditPinPreference editPinPreference, boolean z);
    }

    public EditPinPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsShowing = false;
    }

    public EditPinPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsShowing = false;
    }

    public void setOnPinEnteredListener(OnPinEnteredListener onPinEnteredListener) {
        this.mPinListener = onPinEnteredListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null) {
            editText.setInputType(18);
            editText.setTextAlignment(5);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        }
    }

    public boolean isDialogOpen() {
        Dialog dialog = getDialog();
        return dialog != null && dialog.isShowing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        this.mIsShowing = false;
        OnPinEnteredListener onPinEnteredListener = this.mPinListener;
        if (onPinEnteredListener != null) {
            onPinEnteredListener.onPinEntered(this, z);
        }
    }

    public void showPinDialog() {
        Dialog dialog = getDialog();
        if (dialog == null || !dialog.isShowing()) {
            onClick();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        if (!this.mIsShowing) {
            super.onClick();
        }
        this.mIsShowing = true;
    }
}
