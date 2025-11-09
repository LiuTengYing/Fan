package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.SoftApConfiguration;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
/* loaded from: classes2.dex */
public class WifiTetherApBandPreferenceController extends WifiTetherBasePreferenceController {
    private String[] mBandEntries;
    private int mBandIndex;
    private String[] mBandSummaries;
    private boolean mIs5GhzSupported;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether_network_ap_band";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public WifiTetherApBandPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mIs5GhzSupported = false;
        updatePreferenceEntries();
        this.mIs5GhzSupported = this.mWifiManager.is5GHzBandSupported();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null) {
            this.mBandIndex = 1;
            Log.d("UniWifiTetherApBandPref", "Updating band index to BAND_2GHZ because no config");
        } else if (is5GhzBandSupported()) {
            this.mBandIndex = validateSelection(softApConfiguration.getBand());
            Log.d("UniWifiTetherApBandPref", "Updating band index to " + this.mBandIndex);
        } else {
            this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(softApConfiguration).setBand(1).build());
            this.mBandIndex = 1;
            Log.d("UniWifiTetherApBandPref", "5Ghz not supported, updating band index to 2GHz");
        }
        ListPreference listPreference = (ListPreference) this.mPreference;
        listPreference.setEntries(this.mBandSummaries);
        listPreference.setEntryValues(this.mBandEntries);
        if (!is5GhzBandSupported()) {
            listPreference.setEnabled(false);
            listPreference.setSummary(R$string.wifi_ap_choose_2G);
            return;
        }
        listPreference.setValue(Integer.toString(softApConfiguration.getBand()));
        listPreference.setSummary(getConfigSummary());
    }

    String getConfigSummary() {
        int i = this.mBandIndex;
        if (i != 1) {
            if (i == 2) {
                return this.mBandSummaries[1];
            }
            return this.mContext.getString(R$string.wifi_ap_prefer_5G);
        }
        return this.mBandSummaries[0];
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int validateSelection = validateSelection(Integer.parseInt((String) obj));
        if (this.mBandIndex != validateSelection) {
            this.mBandIndex = validateSelection;
            Log.d("UniWifiTetherApBandPref", "Band preference changed, updating band index to " + this.mBandIndex);
            preference.setSummary(getConfigSummary());
            this.mListener.onTetherConfigUpdated(this);
            return true;
        }
        return true;
    }

    private int validateSelection(int i) {
        return 2 == i ? !is5GhzBandSupported() ? 1 : 3 : i;
    }

    void updatePreferenceEntries() {
        Resources resources = this.mContext.getResources();
        int i = R$array.wifi_ap_band;
        int i2 = R$array.wifi_ap_band_summary;
        this.mBandEntries = resources.getStringArray(i);
        this.mBandSummaries = resources.getStringArray(i2);
    }

    private boolean is5GhzBandSupported() {
        return this.mIs5GhzSupported && this.mWifiManager.getCountryCode() != null;
    }

    public int getBandIndex() {
        return this.mBandIndex;
    }

    public void updateEnabled(boolean z) {
        if (is5GhzBandSupported()) {
            ((ListPreference) this.mPreference).setEnabled(z);
        }
    }
}
