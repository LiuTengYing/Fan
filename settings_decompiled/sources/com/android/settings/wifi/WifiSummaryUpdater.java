package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.android.settings.R$string;
import com.android.settings.widget.SummaryUpdater;
import com.android.settingslib.wifi.WifiStatusTracker;
/* loaded from: classes.dex */
public final class WifiSummaryUpdater extends SummaryUpdater {
    private static final IntentFilter INTENT_FILTER;
    private final BroadcastReceiver mReceiver;
    private final WifiStatusTracker mWifiTracker;

    static {
        IntentFilter intentFilter = new IntentFilter();
        INTENT_FILTER = intentFilter;
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
    }

    public WifiSummaryUpdater(Context context, SummaryUpdater.OnSummaryChangeListener onSummaryChangeListener) {
        this(context, onSummaryChangeListener, null);
    }

    public WifiSummaryUpdater(Context context, SummaryUpdater.OnSummaryChangeListener onSummaryChangeListener, WifiStatusTracker wifiStatusTracker) {
        super(context, onSummaryChangeListener);
        this.mWifiTracker = wifiStatusTracker == null ? new WifiStatusTracker(context, (WifiManager) context.getSystemService(WifiManager.class), (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), new Runnable() { // from class: com.android.settings.wifi.WifiSummaryUpdater$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WifiSummaryUpdater.this.notifyChangeIfNeeded();
            }
        }) : wifiStatusTracker;
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WifiSummaryUpdater.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiSummaryUpdater.this.mWifiTracker.handleBroadcast(intent);
                WifiSummaryUpdater.this.notifyChangeIfNeeded();
            }
        };
    }

    public void register(boolean z) {
        if (z) {
            this.mWifiTracker.fetchInitialState();
            notifyChangeIfNeeded();
            this.mContext.registerReceiver(this.mReceiver, INTENT_FILTER, 2);
        } else {
            this.mContext.unregisterReceiver(this.mReceiver);
        }
        this.mWifiTracker.setListening(z);
    }

    @Override // com.android.settings.widget.SummaryUpdater
    public String getSummary() {
        WifiStatusTracker wifiStatusTracker = this.mWifiTracker;
        if (!wifiStatusTracker.enabled) {
            return this.mContext.getString(R$string.switch_off_text);
        }
        if (!wifiStatusTracker.connected) {
            return this.mContext.getString(R$string.disconnected);
        }
        String sanitizeSsid = android.net.wifi.WifiInfo.sanitizeSsid(wifiStatusTracker.ssid);
        return TextUtils.isEmpty(this.mWifiTracker.statusLabel) ? sanitizeSsid : this.mContext.getResources().getString(com.android.settingslib.R$string.preference_summary_default_combination, sanitizeSsid, this.mWifiTracker.statusLabel);
    }
}
