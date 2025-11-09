package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.wifitrackerlib.WifiEntry;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class MergedCarrierEntry extends WifiEntry {
    private final Context mContext;
    boolean mIsCellDefaultRoute;
    private final String mKey;
    private final int mSubscriptionId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MergedCarrierEntry(Handler handler, WifiManager wifiManager, boolean z, Context context, int i) throws IllegalArgumentException {
        super(handler, wifiManager, z);
        this.mContext = context;
        this.mSubscriptionId = i;
        this.mKey = "MergedCarrierEntry:" + i;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSummary(boolean z) {
        StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.wifitrackerlib_summary_separator));
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSsid() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            return WifiInfo.sanitizeSsid(wifiInfo.getSSID());
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            String macAddress = wifiInfo.getMacAddress();
            if (!TextUtils.isEmpty(macAddress)) {
                if (!TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
                    return macAddress;
                }
            }
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canConnect() {
        boolean z;
        if (getConnectedState() == 0) {
            z = this.mIsCellDefaultRoute ? false : true;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void connect(WifiEntry.ConnectCallback connectCallback) {
        connect(connectCallback, true);
    }

    public synchronized void connect(WifiEntry.ConnectCallback connectCallback, boolean z) {
        this.mConnectCallback = connectCallback;
        this.mWifiManager.startRestrictingAutoJoinToSubscriptionId(this.mSubscriptionId);
        if (z) {
            Toast.makeText(this.mContext, R$string.wifitrackerlib_wifi_wont_autoconnect_for_now, 0).show();
        }
        if (this.mConnectCallback != null) {
            this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.MergedCarrierEntry$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MergedCarrierEntry.this.lambda$connect$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$connect$0() {
        WifiEntry.ConnectCallback connectCallback = this.mConnectCallback;
        if (connectCallback != null) {
            connectCallback.onConnectResult(0);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canDisconnect() {
        return getConnectedState() == 2;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void disconnect(WifiEntry.DisconnectCallback disconnectCallback) {
        this.mDisconnectCallback = disconnectCallback;
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        this.mWifiManager.startScan();
        if (this.mDisconnectCallback != null) {
            this.mCallbackHandler.post(new Runnable() { // from class: com.android.wifitrackerlib.MergedCarrierEntry$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MergedCarrierEntry.this.lambda$disconnect$1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnect$1() {
        WifiEntry.DisconnectCallback disconnectCallback = this.mDisconnectCallback;
        if (disconnectCallback != null) {
            disconnectCallback.onDisconnectResult(0);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return wifiInfo.isCarrierMerged() && this.mSubscriptionId == wifiInfo.getSubscriptionId();
    }

    public boolean isEnabled() {
        return this.mWifiManager.isCarrierNetworkOffloadEnabled(this.mSubscriptionId, true);
    }

    public void setEnabled(boolean z) {
        this.mWifiManager.setCarrierNetworkOffloadEnabled(this.mSubscriptionId, true, z);
        if (z) {
            return;
        }
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        this.mWifiManager.startScan();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSubscriptionId() {
        return this.mSubscriptionId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateIsCellDefaultRoute(boolean z) {
        this.mIsCellDefaultRoute = z;
        notifyOnUpdated();
    }
}
