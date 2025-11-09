package com.android.settingslib.deviceinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.R$bool;
import com.android.settingslib.R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes2.dex */
public abstract class AbstractWifiMacAddressPreferenceController extends AbstractConnectivityPreferenceController {
    private static final String[] CONNECTIVITY_INTENTS = {"android.net.conn.CONNECTIVITY_CHANGE", "android.net.wifi.LINK_CONFIGURATION_CHANGED", "android.net.wifi.STATE_CHANGE", "android.net.wifi.WIFI_STATE_CHANGED"};
    static final String KEY_WIFI_MAC_ADDRESS = "wifi_mac_address";
    private static final String MACID_FILE_PATH = "/mnt/vendor/wifimac.txt";
    static final int OFF = 0;
    static final int ON = 1;
    private Preference mWifiMacAddress;
    private final WifiManager mWifiManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_WIFI_MAC_ADDRESS;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AbstractWifiMacAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController, com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        super.onStart();
        updateConnectivity();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mWifiMacAddress = preferenceScreen.findPreference(KEY_WIFI_MAC_ADDRESS);
            updateConnectivity();
        }
    }

    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    protected String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    @Override // com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController
    @SuppressLint({"HardwareIds"})
    protected void updateConnectivity() {
        if (this.mWifiManager == null || this.mWifiMacAddress == null) {
            return;
        }
        this.mWifiMacAddress.setSummary(getMacAddress());
    }

    @SuppressLint({"HardwareIds"})
    public String getMacAddress() {
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        String str = (factoryMacAddresses == null || factoryMacAddresses.length <= 0) ? null : factoryMacAddresses[0];
        if (TextUtils.isEmpty(str)) {
            if (this.mContext.getResources().getBoolean(R$bool.config_enableGetWifiMacFromFile)) {
                str = getMacFromFile();
            }
            return TextUtils.isEmpty(str) ? this.mContext.getResources().getString(R$string.status_unavailable) : str;
        }
        return str;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(7:1|(2:2|3)|(3:5|6|(1:8))|9|10|11|(2:(0)|(0))) */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x003c, code lost:
        if (r3 == null) goto L11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0021, code lost:
        android.util.Log.w("AbstractWifiMacAddressPreferenceController", "reader close exception");
     */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0044 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getMacFromFile() {
        /*
            r5 = this;
            java.lang.String r5 = "reader close exception"
            java.lang.String r0 = "AbstractWifiMacAddressPreferenceController"
            java.io.File r1 = new java.io.File
            java.lang.String r2 = "/mnt/vendor/wifimac.txt"
            r1.<init>(r2)
            r2 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b java.io.FileNotFoundException -> L35
            java.io.FileReader r4 = new java.io.FileReader     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b java.io.FileNotFoundException -> L35
            r4.<init>(r1)     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b java.io.FileNotFoundException -> L35
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L29 java.lang.Exception -> L2b java.io.FileNotFoundException -> L35
            java.lang.String r1 = r3.readLine()     // Catch: java.lang.Exception -> L25 java.io.FileNotFoundException -> L27 java.lang.Throwable -> L40
            if (r1 == 0) goto L1d
            r2 = r1
        L1d:
            r3.close()     // Catch: java.io.IOException -> L21
            goto L3f
        L21:
            android.util.Log.w(r0, r5)
            goto L3f
        L25:
            r1 = move-exception
            goto L2d
        L27:
            r1 = move-exception
            goto L37
        L29:
            r1 = move-exception
            goto L42
        L2b:
            r1 = move-exception
            r3 = r2
        L2d:
            java.lang.String r4 = "get mac from file caught exception"
            android.util.Log.w(r0, r4, r1)     // Catch: java.lang.Throwable -> L40
            if (r3 == 0) goto L3f
            goto L1d
        L35:
            r1 = move-exception
            r3 = r2
        L37:
            java.lang.String r4 = "Mac file not exist"
            android.util.Log.w(r0, r4, r1)     // Catch: java.lang.Throwable -> L40
            if (r3 == 0) goto L3f
            goto L1d
        L3f:
            return r2
        L40:
            r1 = move-exception
            r2 = r3
        L42:
            if (r2 == 0) goto L4b
            r2.close()     // Catch: java.io.IOException -> L48
            goto L4b
        L48:
            android.util.Log.w(r0, r5)
        L4b:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.deviceinfo.AbstractWifiMacAddressPreferenceController.getMacFromFile():java.lang.String");
    }
}
