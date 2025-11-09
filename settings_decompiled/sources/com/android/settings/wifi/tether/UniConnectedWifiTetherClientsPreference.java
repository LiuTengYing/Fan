package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.MacAddress;
/* loaded from: classes2.dex */
public class UniConnectedWifiTetherClientsPreference extends UniWifiTetherClientsPreference {
    private String mHostName;
    private String mIpAddress;
    private MacAddress mMacAddress;
    private int mPreferenceTypes;

    public UniConnectedWifiTetherClientsPreference(Context context, String str, MacAddress macAddress, String str2, int i) {
        super(context, str, macAddress, str2, i);
        this.mHostName = str;
        this.mMacAddress = macAddress;
        this.mIpAddress = str2;
        this.mPreferenceTypes = i;
        setTitle(str);
        setSummary("IP: " + this.mIpAddress + "\nMAC: " + this.mMacAddress.toString());
    }

    @Override // com.android.settings.wifi.tether.UniWifiTetherClientsPreference
    public int getPreferenceTypes() {
        return this.mPreferenceTypes;
    }

    @Override // com.android.settings.wifi.tether.UniWifiTetherClientsPreference
    public String getHostName() {
        return this.mHostName;
    }

    @Override // com.android.settings.wifi.tether.UniWifiTetherClientsPreference
    public MacAddress getMacAddress() {
        return this.mMacAddress;
    }

    @Override // com.android.settings.wifi.tether.UniWifiTetherClientsPreference
    public String getIpAddress() {
        return this.mIpAddress;
    }
}
