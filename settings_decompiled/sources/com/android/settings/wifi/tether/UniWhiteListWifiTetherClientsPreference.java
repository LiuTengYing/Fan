package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.MacAddress;
import android.text.TextUtils;
import android.util.Log;
/* loaded from: classes2.dex */
public class UniWhiteListWifiTetherClientsPreference extends UniWifiTetherClientsPreference {
    String mHostName;
    MacAddress mMacAddress;
    private SharedPreferences mSharedPreferences;

    public UniWhiteListWifiTetherClientsPreference(Context context, String str, MacAddress macAddress, String str2, int i) {
        super(context, str, macAddress, str2, i);
        this.mSharedPreferences = null;
        this.mMacAddress = macAddress;
        this.mSharedPreferences = context.getSharedPreferences("com.android.settings.wifi.tether.UniWhiteListWifiTetherClientsPreference", 0);
        displayWhiteListPre();
    }

    public void displayWhiteListPre() {
        this.mHostName = this.mSharedPreferences.getString(this.mMacAddress.toString(), null);
        Log.d("WhiteListWifiTetherPref", "displayWhiteListPref mHostName = " + this.mHostName);
        if (!TextUtils.isEmpty(this.mHostName)) {
            setTitle(this.mHostName);
            setSummary("MAC: " + this.mMacAddress);
            return;
        }
        setTitle(this.mMacAddress.toString());
    }

    @Override // com.android.settings.wifi.tether.UniWifiTetherClientsPreference
    public String getHostName() {
        return this.mHostName;
    }

    @Override // com.android.settings.wifi.tether.UniWifiTetherClientsPreference
    public MacAddress getMacAddress() {
        return this.mMacAddress;
    }
}
