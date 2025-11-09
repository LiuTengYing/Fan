package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.MacAddress;
import androidx.preference.Preference;
/* loaded from: classes2.dex */
public class UniWifiTetherClientsPreference extends Preference {
    private String mHostName;
    private String mIpAddress;
    private MacAddress mMacAddress;
    private int mPreferenceTypes;

    public UniWifiTetherClientsPreference(Context context, String str, MacAddress macAddress, String str2, int i) {
        super(context);
        this.mPreferenceTypes = i;
        this.mHostName = str;
        this.mMacAddress = macAddress;
        this.mIpAddress = str2;
    }

    public int getPreferenceTypes() {
        return this.mPreferenceTypes;
    }

    public String getHostName() {
        return this.mHostName;
    }

    public MacAddress getMacAddress() {
        return this.mMacAddress;
    }

    public String getIpAddress() {
        return this.mIpAddress;
    }
}
