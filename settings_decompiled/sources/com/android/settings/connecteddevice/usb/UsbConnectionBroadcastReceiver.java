package com.android.settings.connecteddevice.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbPortStatus;
import android.util.Log;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public class UsbConnectionBroadcastReceiver extends BroadcastReceiver implements LifecycleObserver, OnResume, OnPause {
    private static final boolean DEBUG = Log.isLoggable("UsbBroadcastReceiver", 3);
    private boolean mConnected;
    private Context mContext;
    private boolean mListeningToUsbEvents;
    private UsbBackend mUsbBackend;
    private UsbConnectionListener mUsbConnectionListener;
    private long mFunctions = 0;
    private int mDataRole = 0;
    private int mPowerRole = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface UsbConnectionListener {
        void onUsbConnectionChanged(boolean z, long j, int i, int i2, boolean z2);
    }

    public UsbConnectionBroadcastReceiver(Context context, UsbConnectionListener usbConnectionListener, UsbBackend usbBackend) {
        this.mContext = context;
        this.mUsbConnectionListener = usbConnectionListener;
        this.mUsbBackend = usbBackend;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) {
            Log.d("UsbBroadcastReceiver", "onReceive() action : " + intent.getAction());
        }
        boolean z = intent.getExtras() != null ? intent.getExtras().getBoolean("configured") : false;
        if ("android.hardware.usb.action.USB_STATE".equals(intent.getAction())) {
            this.mConnected = intent.getExtras().getBoolean("connected") || intent.getExtras().getBoolean("host_connected");
            long j = 0;
            if (intent.getExtras().getBoolean("mtp") && intent.getExtras().getBoolean("unlocked", false)) {
                j = 4;
            }
            if (intent.getExtras().getBoolean("ptp") && intent.getExtras().getBoolean("unlocked", false)) {
                j |= 16;
            }
            if (intent.getExtras().getBoolean("midi")) {
                j |= 8;
            }
            if (intent.getExtras().getBoolean("rndis")) {
                j |= 32;
            }
            if (intent.getExtras().getBoolean("accessory")) {
                j |= 2;
            }
            if (intent.getExtras().getBoolean("ncm")) {
                j |= 1024;
            }
            this.mFunctions = j;
            this.mDataRole = this.mUsbBackend.getDataRole();
            this.mPowerRole = this.mUsbBackend.getPowerRole();
        } else if ("android.hardware.usb.action.USB_PORT_CHANGED".equals(intent.getAction())) {
            UsbPortStatus parcelable = intent.getExtras().getParcelable("portStatus");
            if (parcelable != null) {
                this.mDataRole = parcelable.getCurrentDataRole();
                this.mPowerRole = parcelable.getCurrentPowerRole();
            }
        } else if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction()) && intent.getIntExtra("plugged", 0) == 0) {
            this.mConnected = false;
        }
        UsbConnectionListener usbConnectionListener = this.mUsbConnectionListener;
        if (usbConnectionListener != null) {
            usbConnectionListener.onUsbConnectionChanged(this.mConnected, this.mFunctions, this.mPowerRole, this.mDataRole, z);
        }
    }

    public void register() {
        if (this.mListeningToUsbEvents) {
            return;
        }
        this.mConnected = false;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        intentFilter.addAction("android.hardware.usb.action.USB_PORT_CHANGED");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        Intent registerReceiver = this.mContext.registerReceiver(this, intentFilter);
        if (registerReceiver != null) {
            onReceive(this.mContext, registerReceiver);
        }
        this.mListeningToUsbEvents = true;
    }

    public void unregister() {
        if (this.mListeningToUsbEvents) {
            this.mContext.unregisterReceiver(this);
            this.mListeningToUsbEvents = false;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        register();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        unregister();
    }
}
