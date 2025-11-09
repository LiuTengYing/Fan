package com.android.settings.widget;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class MacAddressEditText extends LinearLayout {
    TextWatcher etListener;
    private MacWatcher mListener;
    private EditText mMac1;
    private EditText mMac2;
    private EditText mMac3;
    private EditText mMac4;
    private EditText mMac5;
    private EditText mMac6;
    private EditText[] mMacArray;
    private String mMacText;

    /* loaded from: classes.dex */
    public interface MacWatcher {
        void onTextChanged();
    }

    public MacAddressEditText(Context context) {
        super(context);
        this.mMacArray = new EditText[6];
        this.etListener = new TextWatcher() { // from class: com.android.settings.widget.MacAddressEditText.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                MacAddressEditText.this.checkFocus(charSequence);
            }
        };
        initView(context);
    }

    public MacAddressEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMacArray = new EditText[6];
        this.etListener = new TextWatcher() { // from class: com.android.settings.widget.MacAddressEditText.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                MacAddressEditText.this.checkFocus(charSequence);
            }
        };
        initView(context);
    }

    public MacAddressEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMacArray = new EditText[6];
        this.etListener = new TextWatcher() { // from class: com.android.settings.widget.MacAddressEditText.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                MacAddressEditText.this.checkFocus(charSequence);
            }
        };
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R$layout.mac_address, (ViewGroup) this, true);
        this.mMac1 = (EditText) findViewById(R$id.mac1);
        this.mMac2 = (EditText) findViewById(R$id.mac2);
        this.mMac3 = (EditText) findViewById(R$id.mac3);
        this.mMac4 = (EditText) findViewById(R$id.mac4);
        this.mMac5 = (EditText) findViewById(R$id.mac5);
        EditText editText = (EditText) findViewById(R$id.mac6);
        this.mMac6 = editText;
        EditText[] editTextArr = {this.mMac1, this.mMac2, this.mMac3, this.mMac4, this.mMac5, editText};
        this.mMacArray = editTextArr;
        for (EditText editText2 : editTextArr) {
            if (editText2 != null) {
                editText2.addTextChangedListener(this.etListener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkFocus(CharSequence charSequence) {
        this.mMacText = null;
        int i = 0;
        if (charSequence.toString().length() < 2) {
            EditText[] editTextArr = this.mMacArray;
            int length = editTextArr.length;
            while (i < length) {
                EditText editText = editTextArr[i];
                if (editText != null) {
                    if (editText == this.mMac1) {
                        this.mMacText = editText.getText().toString().trim();
                    } else {
                        this.mMacText += editText.getText().toString().trim();
                    }
                    if (editText != this.mMac6) {
                        this.mMacText += ":";
                    }
                }
                i++;
            }
            MacWatcher macWatcher = this.mListener;
            if (macWatcher != null) {
                macWatcher.onTextChanged();
                return;
            }
            return;
        }
        EditText[] editTextArr2 = this.mMacArray;
        int length2 = editTextArr2.length;
        while (i < length2) {
            EditText editText2 = editTextArr2[i];
            if (editText2 != null && editText2.getText().toString().length() > 1) {
                if (editText2 == this.mMac1) {
                    this.mMacText = editText2.getText().toString().trim();
                } else {
                    this.mMacText += editText2.getText().toString().trim();
                }
                if (editText2 != this.mMac6) {
                    this.mMacText += ":";
                } else {
                    MacWatcher macWatcher2 = this.mListener;
                    if (macWatcher2 != null) {
                        macWatcher2.onTextChanged();
                        return;
                    }
                    return;
                }
            } else if (editText2 != null) {
                editText2.setFocusable(true);
                editText2.requestFocus();
                return;
            }
            i++;
        }
    }

    public void addTextChangedListener(MacWatcher macWatcher) {
        this.mListener = macWatcher;
    }

    public Editable getText() {
        if (this.mMacText == null) {
            return null;
        }
        return new SpannableStringBuilder(this.mMacText);
    }
}
