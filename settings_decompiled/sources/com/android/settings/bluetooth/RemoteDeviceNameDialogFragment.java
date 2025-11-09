package com.android.settings.bluetooth;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.widget.TextView;
import com.android.settings.R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
/* loaded from: classes.dex */
public class RemoteDeviceNameDialogFragment extends BluetoothNameDialogFragment {
    private CachedBluetoothDevice mDevice;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1015;
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, android.text.TextWatcher
    public /* bridge */ /* synthetic */ void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, android.text.TextWatcher
    public /* bridge */ /* synthetic */ void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.beforeTextChanged(charSequence, i, i2, i3);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, androidx.fragment.app.DialogFragment
    public /* bridge */ /* synthetic */ Dialog onCreateDialog(Bundle bundle) {
        return super.onCreateDialog(bundle);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onDestroy() {
        super.onDestroy();
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, android.widget.TextView.OnEditorActionListener
    public /* bridge */ /* synthetic */ boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return super.onEditorAction(textView, i, keyEvent);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onResume() {
        super.onResume();
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, android.text.TextWatcher
    public /* bridge */ /* synthetic */ void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
    }

    public static RemoteDeviceNameDialogFragment newInstance(CachedBluetoothDevice cachedBluetoothDevice) {
        Bundle bundle = new Bundle(1);
        bundle.putString("cached_device", cachedBluetoothDevice.getDevice().getAddress());
        RemoteDeviceNameDialogFragment remoteDeviceNameDialogFragment = new RemoteDeviceNameDialogFragment();
        remoteDeviceNameDialogFragment.setArguments(bundle);
        return remoteDeviceNameDialogFragment;
    }

    CachedBluetoothDevice getDevice(Context context) {
        String string = getArguments().getString("cached_device");
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
        return localBtManager.getCachedDeviceManager().findDevice(localBtManager.getBluetoothAdapter().getRemoteDevice(string));
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mDevice = getDevice(context);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    protected int getDialogTitle() {
        return R$string.bluetooth_device_name;
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    protected String getDeviceName() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mDevice;
        if (cachedBluetoothDevice != null) {
            return cachedBluetoothDevice.getName();
        }
        return null;
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    protected void setDeviceName(String str) {
        CachedBluetoothDevice cachedBluetoothDevice = this.mDevice;
        if (cachedBluetoothDevice != null) {
            cachedBluetoothDevice.setName(str);
        }
    }
}
