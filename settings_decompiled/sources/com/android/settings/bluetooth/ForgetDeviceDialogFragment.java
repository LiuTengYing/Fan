package com.android.settings.bluetooth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
/* loaded from: classes.dex */
public class ForgetDeviceDialogFragment extends InstrumentedDialogFragment {
    private CachedBluetoothDevice mDevice;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1031;
    }

    public static ForgetDeviceDialogFragment newInstance(String str) {
        Bundle bundle = new Bundle(1);
        bundle.putString("device_address", str);
        ForgetDeviceDialogFragment forgetDeviceDialogFragment = new ForgetDeviceDialogFragment();
        forgetDeviceDialogFragment.setArguments(bundle);
        return forgetDeviceDialogFragment;
    }

    CachedBluetoothDevice getDevice(Context context) {
        String string = getArguments().getString("device_address");
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
        return localBtManager.getCachedDeviceManager().findDevice(localBtManager.getBluetoothAdapter().getRemoteDevice(string));
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.bluetooth.ForgetDeviceDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ForgetDeviceDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        };
        Context context = getContext();
        this.mDevice = getDevice(context);
        AlertDialog create = new AlertDialog.Builder(context).setPositiveButton(R$string.bluetooth_unpair_dialog_forget_confirm_button, onClickListener).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        create.setTitle(R$string.bluetooth_unpair_dialog_title);
        create.setMessage(context.getString(R$string.bluetooth_unpair_dialog_body, this.mDevice.getName()));
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        this.mDevice.unpair();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
}
