package com.android.settings.deviceinfo;

import android.content.Context;
import com.android.settings.R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.wifi.WifiUtils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractWifiMacAddressPreferenceController;
/* loaded from: classes.dex */
public class WifiMacAddressPreferenceController extends AbstractWifiMacAddressPreferenceController implements PreferenceControllerMixin {
    public WifiMacAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override // com.android.settingslib.deviceinfo.AbstractWifiMacAddressPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return WifiUtils.hasSystemFeature(this.mContext, "android.hardware.wifi") && this.mContext.getResources().getBoolean(R$bool.config_show_wifi_mac_address);
    }
}
