package com.android.settings.wifi.tether;

import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import java.util.List;
/* loaded from: classes2.dex */
public class WifiTetherSoftApManager {
    private WifiManager mWifiManager;
    private WifiTetherSoftApCallback mWifiTetherSoftApCallback;
    private WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() { // from class: com.android.settings.wifi.tether.WifiTetherSoftApManager.1
        public void onStateChanged(int i, int i2) {
            WifiTetherSoftApManager.this.mWifiTetherSoftApCallback.onStateChanged(i, i2);
        }

        public void onConnectedClientsChanged(List<WifiClient> list) {
            WifiTetherSoftApManager.this.mWifiTetherSoftApCallback.onConnectedClientsChanged(list);
        }
    };
    private Handler mHandler = new Handler();

    /* loaded from: classes2.dex */
    public interface WifiTetherSoftApCallback {
        void onConnectedClientsChanged(List<WifiClient> list);

        void onStateChanged(int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiTetherSoftApManager(WifiManager wifiManager, WifiTetherSoftApCallback wifiTetherSoftApCallback) {
        this.mWifiManager = wifiManager;
        this.mWifiTetherSoftApCallback = wifiTetherSoftApCallback;
    }

    public void registerSoftApCallback() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            wifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
        }
    }

    public void unRegisterSoftApCallback() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            wifiManager.unregisterSoftApCallback(this.mSoftApCallback);
        }
    }
}
