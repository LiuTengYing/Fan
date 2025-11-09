package com.android.settings.wifi.tether;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R$string;
/* loaded from: classes2.dex */
public class WifiTetherFooterPreferenceController extends WifiTetherBasePreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "tether_prefs_footer_2";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    public WifiTetherFooterPreferenceController(Context context) {
        super(context, null);
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        if (this.mWifiManager.isStaApConcurrencySupported()) {
            this.mPreference.setTitle(R$string.tethering_footer_info_sta_ap_concurrency);
        } else {
            this.mPreference.setTitle(R$string.tethering_footer_info);
        }
    }
}
