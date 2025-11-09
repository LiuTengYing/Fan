package com.android.settings.factory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class PinDialogFragment extends InstrumentedDialogFragment {
    private ImageView clearCode;
    private TextView mCodeText;
    public StringBuffer mCurPassword = new StringBuffer();
    private View mRootView;
    private TextView pinCode0;
    private TextView pinCode1;
    private TextView pinCode10;
    private TextView pinCode11;
    private TextView pinCode2;
    private TextView pinCode3;
    private TextView pinCode4;
    private TextView pinCode5;
    private TextView pinCode6;
    private TextView pinCode7;
    private TextView pinCode8;
    private TextView pinCode9;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder title = new AlertDialog.Builder(getActivity()).setTitle(getArguments().getString("arg_key_dialog_title"));
        View inflate = LayoutInflater.from(title.getContext()).inflate(R$layout.dialog_pin_password, (ViewGroup) null);
        this.mRootView = inflate;
        AlertDialog create = title.setView(inflate).create();
        create.setCanceledOnTouchOutside(false);
        create.setCancelable(false);
        create.getWindow().setFlags(8192, 8192);
        init();
        initListener(create);
        return create;
    }

    private void init() {
        this.pinCode0 = (TextView) this.mRootView.findViewById(R$id.pin_code0);
        this.pinCode1 = (TextView) this.mRootView.findViewById(R$id.pin_code1);
        this.pinCode2 = (TextView) this.mRootView.findViewById(R$id.pin_code2);
        this.pinCode3 = (TextView) this.mRootView.findViewById(R$id.pin_code3);
        this.pinCode4 = (TextView) this.mRootView.findViewById(R$id.pin_code4);
        this.pinCode5 = (TextView) this.mRootView.findViewById(R$id.pin_code5);
        this.pinCode6 = (TextView) this.mRootView.findViewById(R$id.pin_code6);
        this.pinCode7 = (TextView) this.mRootView.findViewById(R$id.pin_code7);
        this.pinCode8 = (TextView) this.mRootView.findViewById(R$id.pin_code8);
        this.pinCode9 = (TextView) this.mRootView.findViewById(R$id.pin_code9);
        this.pinCode10 = (TextView) this.mRootView.findViewById(R$id.pin_code10);
        this.pinCode11 = (TextView) this.mRootView.findViewById(R$id.pin_code11);
        this.clearCode = (ImageView) this.mRootView.findViewById(R$id.clear_password);
        this.mCodeText = (TextView) this.mRootView.findViewById(R$id.txt_password);
    }

    private void initListener(Dialog dialog) {
        this.pinCode0.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("0");
            }
        });
        this.pinCode1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("1");
            }
        });
        this.pinCode2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("2");
            }
        });
        this.pinCode3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("3");
            }
        });
        this.pinCode4.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("4");
            }
        });
        this.pinCode5.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("5");
            }
        });
        this.pinCode6.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("6");
            }
        });
        this.pinCode7.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("7");
            }
        });
        this.pinCode8.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("8");
            }
        });
        this.pinCode9.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PinDialogFragment.this.inputCode("9");
            }
        });
        this.clearCode.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.factory.PinDialogFragment.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PinDialogFragment.this.mCurPassword.length() > 0) {
                    StringBuffer stringBuffer = PinDialogFragment.this.mCurPassword;
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    if (PinDialogFragment.this.mCodeText != null) {
                        PinDialogFragment.this.mCodeText.setText(PinDialogFragment.this.mCurPassword);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inputCode(String str) {
        if (this.mCurPassword.length() < 8) {
            this.mCurPassword.append(str);
            TextView textView = this.mCodeText;
            if (textView != null) {
                textView.setText(this.mCurPassword);
            }
        }
    }
}
