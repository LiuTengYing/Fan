package com.android.settings.wifi.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils;
/* loaded from: classes2.dex */
public class WifiP2pPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnPause, OnResume {
    private final IntentFilter mFilter;
    boolean mIsWifiDirectAllow;
    final BroadcastReceiver mReceiver;
    private Preference mWifiDirectPref;
    private final WifiManager mWifiManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_direct";
    }

    public WifiP2pPreferenceController(Context context, Lifecycle lifecycle, WifiManager wifiManager) {
        super(context);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.p2p.WifiP2pPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiP2pPreferenceController.this.togglePreferences();
            }
        };
        this.mFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mWifiManager = wifiManager;
        this.mIsWifiDirectAllow = WifiEnterpriseRestrictionUtils.isWifiDirectAllowed(context);
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mWifiDirectPref = preferenceScreen.findPreference("wifi_direct");
        togglePreferences();
        if (this.mIsWifiDirectAllow) {
            return;
        }
        this.mWifiDirectPref.setSummary(R$string.not_allowed_by_ent);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(isWifiP2pAvailable());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.registerReceiver(this.mReceiver, this.mFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.direct");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void togglePreferences() {
        Preference preference = this.mWifiDirectPref;
        if (preference != null) {
            preference.setEnabled(isWifiP2pAvailable());
        }
    }

    private boolean isWifiP2pAvailable() {
        return this.mWifiManager.isWifiEnabled() && this.mIsWifiDirectAllow;
    }
}
