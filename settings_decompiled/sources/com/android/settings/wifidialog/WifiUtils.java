package com.android.settings.wifidialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.wifidialog.WifiReceiver;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class WifiUtils {
    private Context mContext;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    /* loaded from: classes2.dex */
    private static class SingletonHelper {
        @SuppressLint({"StaticFieldLeak"})
        private static final WifiUtils INSTANCE = new WifiUtils();
    }

    public static WifiUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public WifiManager getWifiManager() {
        Context context = this.mContext;
        if (context == null) {
            throw new RuntimeException("please init first");
        }
        if (this.wifiManager == null) {
            this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        }
        return this.wifiManager;
    }

    public boolean isWifiEnable() {
        return getWifiManager().isWifiEnabled();
    }

    public void openWifi() {
        getWifiManager().setWifiEnabled(true);
    }

    public void closeWifi() {
        getWifiManager().setWifiEnabled(false);
    }

    public void connectWifi(String str, String str2) {
        if (!isWifiEnable()) {
            openWifi();
        }
        getWifiManager().disableNetwork(getWifiManager().getConnectionInfo().getNetworkId());
        getWifiManager().enableNetwork(getWifiManager().addNetwork(getWifiConfig(str, str2, !TextUtils.isEmpty(str2))), true);
    }

    public void removeConnectedWifi() {
        getWifiManager().disableNetwork(getWifiManager().getConnectionInfo().getNetworkId());
    }

    public void startScanWifi() {
        getWifiManager().startScan();
    }

    public boolean isConnectedTargetSsid(String str) {
        return str.equals(getSsid());
    }

    public String getSsid() {
        String ssid = getWifiManager().getConnectionInfo().getSSID();
        return TextUtils.isEmpty(ssid) ? "" : (ssid.startsWith("\"") && ssid.endsWith("\"")) ? ssid.subSequence(1, ssid.length() - 1).toString() : ssid;
    }

    public List<ScanResult> getWifiList() {
        ArrayList arrayList = new ArrayList();
        if (getWifiManager() != null && isWifiEnable()) {
            arrayList.addAll(getWifiManager().getScanResults());
        }
        return arrayList;
    }

    private WifiConfiguration getWifiConfig(String str, String str2, boolean z) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.SSID = "\"" + str + "\"";
        WifiConfiguration isExist = isExist(str);
        if (isExist != null) {
            getWifiManager().removeNetwork(isExist.networkId);
        }
        if (z) {
            wifiConfiguration.preSharedKey = "\"" + str2 + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.allowedAuthAlgorithms.set(0);
            wifiConfiguration.allowedGroupCiphers.set(2);
            wifiConfiguration.allowedKeyManagement.set(1);
            wifiConfiguration.allowedPairwiseCiphers.set(1);
            wifiConfiguration.allowedGroupCiphers.set(3);
            wifiConfiguration.allowedPairwiseCiphers.set(2);
            wifiConfiguration.status = 2;
        } else {
            wifiConfiguration.allowedKeyManagement.set(0);
        }
        return wifiConfiguration;
    }

    @SuppressLint({"MissingPermission"})
    private WifiConfiguration isExist(String str) {
        List<WifiConfiguration> configuredNetworks = getWifiManager().getConfiguredNetworks();
        if (configuredNetworks == null) {
            Log.e("WifiUtils", "isExist: null");
            return null;
        }
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            String str2 = wifiConfiguration.SSID;
            if (str2.equals("\"" + str + "\"")) {
                return wifiConfiguration;
            }
        }
        return null;
    }

    public boolean isRegisterWifiBroadcast() {
        return this.wifiReceiver != null;
    }

    public void registerWifiBroadcast(WifiReceiver.WifiStateListener wifiStateListener) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        WifiReceiver wifiReceiver = new WifiReceiver();
        this.wifiReceiver = wifiReceiver;
        this.mContext.registerReceiver(wifiReceiver, intentFilter);
        this.wifiReceiver.setWifiStateListener(wifiStateListener);
    }

    public void unregisterWifiBroadcast() {
        if (isRegisterWifiBroadcast()) {
            this.mContext.unregisterReceiver(this.wifiReceiver);
            this.wifiReceiver.setWifiStateListener(null);
            this.wifiReceiver = null;
        }
    }
}
