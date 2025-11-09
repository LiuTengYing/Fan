package com.android.settings.wifidialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes2.dex */
public class ConfirmDialog extends Dialog {
    private Context mContext;
    private TextView mLeft;
    private TextView mRight;
    private TextView mTitle;

    public ConfirmDialog(Context context, int i) {
        super(context, i);
        this.mContext = context;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.layout_wifi_confirm_dialog);
        initViews();
    }

    private void initViews() {
        this.mTitle = (TextView) findViewById(R$id.wifi_dialog_title);
        this.mLeft = (TextView) findViewById(R$id.left_btn_confirm);
        this.mRight = (TextView) findViewById(R$id.right_btn_cancel);
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
}
