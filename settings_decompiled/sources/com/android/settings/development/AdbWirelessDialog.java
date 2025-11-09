package com.android.settings.development;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class AdbWirelessDialog extends AlertDialog implements AdbWirelessDialogUiBase, DialogInterface.OnClickListener {
    private AdbWirelessDialogController mController;
    private final AdbWirelessDialogListener mListener;
    private final int mMode;
    private View mView;

    /* loaded from: classes.dex */
    public interface AdbWirelessDialogListener {
        default void onCancel() {
        }

        default void onDismiss() {
        }
    }

    public static AdbWirelessDialog createModal(Context context, AdbWirelessDialogListener adbWirelessDialogListener, int i) {
        return new AdbWirelessDialog(context, adbWirelessDialogListener, i);
    }

    AdbWirelessDialog(Context context, AdbWirelessDialogListener adbWirelessDialogListener, int i) {
        super(context);
        this.mListener = adbWirelessDialogListener;
        this.mMode = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(R$layout.adb_wireless_dialog, (ViewGroup) null);
        this.mView = inflate;
        setView(inflate);
        this.mController = new AdbWirelessDialogController(this, this.mView, this.mMode);
        super.onCreate(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onStop() {
        super.onStop();
        dismiss();
        AdbWirelessDialogListener adbWirelessDialogListener = this.mListener;
        if (adbWirelessDialogListener != null) {
            adbWirelessDialogListener.onDismiss();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        AdbWirelessDialogListener adbWirelessDialogListener = this.mListener;
        if (adbWirelessDialogListener == null || i != -2) {
            return;
        }
        adbWirelessDialogListener.onCancel();
    }

    public AdbWirelessDialogController getController() {
        return this.mController;
    }

    @Override // com.android.settings.development.AdbWirelessDialogUiBase
    public void setSubmitButton(CharSequence charSequence) {
        setButton(-1, charSequence, this);
    }

    @Override // com.android.settings.development.AdbWirelessDialogUiBase
    public void setCancelButton(CharSequence charSequence) {
        setButton(-2, charSequence, this);
    }
}
