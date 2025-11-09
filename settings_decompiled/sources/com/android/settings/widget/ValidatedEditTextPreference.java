package com.android.settings.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.wifi.WifiUtils;
import com.android.settingslib.CustomEditTextPreferenceCompat;
/* loaded from: classes.dex */
public class ValidatedEditTextPreference extends CustomEditTextPreferenceCompat {
    private boolean mIsPassword;
    private boolean mIsSsid;
    private boolean mIsSummaryPassword;
    private final EditTextWatcher mTextWatcher;
    private Validator mValidator;

    /* loaded from: classes.dex */
    public interface Validator {
        boolean isTextValid(String str);
    }

    public ValidatedEditTextPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mTextWatcher = new EditTextWatcher();
    }

    public ValidatedEditTextPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTextWatcher = new EditTextWatcher();
    }

    public ValidatedEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTextWatcher = new EditTextWatcher();
    }

    public ValidatedEditTextPreference(Context context) {
        super(context);
        this.mTextWatcher = new EditTextWatcher();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        if (editText != null && !TextUtils.isEmpty(editText.getText())) {
            editText.setSelection(editText.getText().length());
        }
        if (this.mValidator == null || editText == null) {
            return;
        }
        editText.removeTextChangedListener(this.mTextWatcher);
        if (this.mIsPassword) {
            editText.setInputType(145);
            editText.setFilters(new InputFilter[]{new WifiUtils.WifiDevicePassWordFilter()});
            editText.setMaxLines(1);
        }
        if (this.mIsSsid) {
            editText.setFilters(new InputFilter[]{new WifiUtils.WifiSsidFilter()});
        }
        editText.addTextChangedListener(this.mTextWatcher);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView == null) {
            return;
        }
        if (this.mIsSummaryPassword) {
            textView.setInputType(129);
        } else {
            textView.setInputType(524289);
        }
    }

    public void setIsPassword(boolean z) {
        this.mIsPassword = z;
    }

    public void setIsSummaryPassword(boolean z) {
        this.mIsSummaryPassword = z;
    }

    public boolean isPassword() {
        return this.mIsPassword;
    }

    public void setValidator(Validator validator) {
        this.mValidator = validator;
    }

    /* loaded from: classes.dex */
    private class EditTextWatcher implements TextWatcher {
        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private EditTextWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            EditText editText = ValidatedEditTextPreference.this.getEditText();
            if (ValidatedEditTextPreference.this.mValidator == null || editText == null) {
                return;
            }
            ((AlertDialog) ValidatedEditTextPreference.this.getDialog()).getButton(-1).setEnabled(ValidatedEditTextPreference.this.mValidator.isTextValid(editText.getText().toString()));
        }
    }

    public void setIsSsid(boolean z) {
        this.mIsSsid = z;
    }
}
