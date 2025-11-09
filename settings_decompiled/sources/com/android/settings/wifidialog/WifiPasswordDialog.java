package com.android.settings.wifidialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes2.dex */
public class WifiPasswordDialog extends Dialog {
    private CheckBox checkBox;
    private EditText editText;
    private TextView mLeft;
    private TextView mRight;
    private TextView mTitle;

    public WifiPasswordDialog(Context context, int i) {
        super(context, i);
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.layout_wifi_password_dialog);
        initViews();
    }

    private void initViews() {
        this.mTitle = (TextView) findViewById(R$id.wifi_dialog_name);
        this.mLeft = (TextView) findViewById(R$id.left_btn);
        this.mRight = (TextView) findViewById(R$id.right_btn);
        this.editText = (EditText) findViewById(R$id.password_edt);
        this.checkBox = (CheckBox) findViewById(R$id.show_password);
    }

    public void setTitle(String str) {
        TextView textView = this.mTitle;
        if (textView != null) {
            textView.setText(str);
        }
    }

    public void setOnLeftButtonClickListener(View.OnClickListener onClickListener) {
        TextView textView = this.mLeft;
        if (textView != null) {
            textView.setOnClickListener(onClickListener);
        }
    }

    public void setOnRightButtonClickListener(View.OnClickListener onClickListener) {
        TextView textView = this.mRight;
        if (textView != null) {
            textView.setOnClickListener(onClickListener);
        }
    }

    public String getEditText() {
        EditText editText = this.editText;
        return editText != null ? editText.getText().toString() : "";
    }

    public void setOnCheckBoxChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        CheckBox checkBox = this.checkBox;
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public void setPasswordShow(boolean z) {
        EditText editText = this.editText;
        if (editText != null) {
            if (z) {
                editText.setInputType(145);
            } else {
                editText.setInputType(129);
            }
        }
    }
}
