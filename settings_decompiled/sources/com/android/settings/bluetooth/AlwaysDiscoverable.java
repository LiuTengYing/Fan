package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
/* loaded from: classes.dex */
public class AlwaysDiscoverable extends BroadcastReceiver {
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Context mContext;
    private IntentFilter mIntentFilter;
    boolean mStarted;

    public AlwaysDiscoverable(Context context) {
        this.mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
        this.mIntentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    }

    public void start() {
        if (this.mStarted) {
            return;
        }
        this.mContext.registerReceiver(this, this.mIntentFilter, 2);
        this.mStarted = true;
        if (this.mBluetoothAdapter.getScanMode() != 23) {
            this.mBluetoothAdapter.setScanMode(23);
        }
    }

    public void stop() {
        if (this.mStarted) {
            this.mContext.unregisterReceiver(this);
            this.mStarted = false;
            this.mBluetoothAdapter.setScanMode(21);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == "android.bluetooth.adapter.action.SCAN_MODE_CHANGED" || action == "android.bluetooth.adapter.action.STATE_CHANGED") {
            if ((!action.equals("android.bluetooth.adapter.action.STATE_CHANGED") || intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 12) && this.mBluetoothAdapter.getScanMode() != 23) {
                this.mBluetoothAdapter.setScanMode(23);
            }
        }
    }
}
