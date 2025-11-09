package com.android.settings.wifi.tether;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
/* loaded from: classes2.dex */
public class UniWifiTetherSoftApManagerModeController extends WifiTetherBasePreferenceController {
    private boolean mSettingOn;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "hotspot_mode";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public UniWifiTetherSoftApManagerModeController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        boolean isClientControlByUserEnabled = this.mSoftApConfig.isClientControlByUserEnabled();
        this.mSettingOn = isClientControlByUserEnabled;
        ((SwitchPreference) this.mPreference).setChecked(isClientControlByUserEnabled);
        ((SwitchPreference) this.mPreference).setSummary(R$string.hotspot_white_mode);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mSettingOn = ((Boolean) obj).booleanValue();
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    public boolean getIsWhiteListMode() {
        return this.mSettingOn;
    }
}
