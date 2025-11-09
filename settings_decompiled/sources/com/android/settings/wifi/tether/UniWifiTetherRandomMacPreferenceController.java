package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
/* loaded from: classes2.dex */
public class UniWifiTetherRandomMacPreferenceController extends WifiTetherBasePreferenceController implements WifiManager.SoftApCallback {
    private final String[] mRandomMacEntries;
    private final String[] mRandomMacEntriesValues;
    private int mRandonMacValue;

    public static int translateMacRandomizedValueToPrefValue(int i) {
        return (i == 2 || i == 1) ? 0 : 1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether_random_mac";
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public UniWifiTetherRandomMacPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mRandonMacValue = 1;
        this.mRandomMacEntries = this.mContext.getResources().getStringArray(R$array.wifi_privacy_entries);
        this.mRandomMacEntriesValues = this.mContext.getResources().getStringArray(R$array.wifi_hotspot_privacy_values);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener;
        int translateMacRandomizedValueToPrefValue = translateMacRandomizedValueToPrefValue(Integer.parseInt((String) obj));
        preference.setSummary(getSummaryForRandonMacType(translateMacRandomizedValueToPrefValue));
        this.mRandonMacValue = Integer.parseInt(this.mRandomMacEntriesValues[translateMacRandomizedValueToPrefValue]);
        if (this.mRandonMacValue == this.mWifiManager.getSoftApConfiguration().getMacRandomizationSetting() || (onTetherConfigUpdateListener = this.mListener) == null) {
            return true;
        }
        onTetherConfigUpdateListener.onTetherConfigUpdated(this);
        return true;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration != null) {
            Log.d("UniWifiTetherRandomMacPref", "updateDisplay macRandomizationSetting is " + softApConfiguration.getMacRandomizationSetting());
            this.mRandonMacValue = softApConfiguration.getMacRandomizationSetting();
        }
        ListPreference listPreference = (ListPreference) this.mPreference;
        listPreference.setSummary(getSummaryForRandonMacType(translateMacRandomizedValueToPrefValue(this.mRandonMacValue)));
        listPreference.setValueIndex(translateMacRandomizedValueToPrefValue(this.mRandonMacValue));
    }

    private String getSummaryForRandonMacType(int i) {
        return this.mRandomMacEntries[i];
    }

    public int getRandomMacType() {
        return this.mRandonMacValue;
    }

    public void updateEnabled(boolean z) {
        ((ListPreference) this.mPreference).setEnabled(z);
    }
}
