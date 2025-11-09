package com.android.settings.wifi.tether;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
/* loaded from: classes2.dex */
public class UniWifiTetherHiddenSSIDPreferenceController extends WifiTetherBasePreferenceController {
    public boolean mSettingsOn;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "hotspot_hidden_ssid";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public UniWifiTetherHiddenSSIDPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        boolean isHiddenSsid = this.mSoftApConfig.isHiddenSsid();
        this.mSettingsOn = isHiddenSsid;
        ((SwitchPreference) this.mPreference).setChecked(isHiddenSsid);
        ((SwitchPreference) this.mPreference).setSummary(R$string.hotspot_hidden_ssid_summary);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mSettingsOn = ((Boolean) obj).booleanValue();
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    public void updateEnabled(boolean z) {
        ((SwitchPreference) this.mPreference).setEnabled(z);
    }

    public boolean getIsHiddenSSID() {
        return this.mSettingsOn;
    }
}
