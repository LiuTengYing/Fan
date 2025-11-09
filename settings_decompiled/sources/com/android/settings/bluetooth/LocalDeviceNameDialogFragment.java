package com.android.settings.bluetooth;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.widget.TextView;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class LocalDeviceNameDialogFragment extends BluetoothNameDialogFragment {
    private BluetoothAdapter mBluetoothAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.LocalDeviceNameDialogFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED".equals(action) || ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action) && intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 12)) {
                LocalDeviceNameDialogFragment.this.updateDeviceName();
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 538;
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

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, android.text.TextWatcher
    public /* bridge */ /* synthetic */ void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
        getActivity().registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    protected int getDialogTitle() {
        return R$string.bluetooth_rename_device;
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    protected String getDeviceName() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return null;
        }
        return this.mBluetoothAdapter.getName();
    }

    @Override // com.android.settings.bluetooth.BluetoothNameDialogFragment
    protected void setDeviceName(String str) {
        this.mBluetoothAdapter.setName(str);
    }
}
