package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public final class WifiTetherDisablePreferenceController extends TetherBasePreferenceController {
    private static final String TAG = "WifiTetherDisablePreferenceController";
    private PreferenceScreen mScreen;

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public int getTetherType() {
        return 0;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldEnable() {
        return true;
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiTetherDisablePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return !super.isChecked();
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return super.setChecked(!z);
    }

    private int getTetheringStateOfOtherInterfaces() {
        return this.mTetheringState & (-2);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController
    public boolean shouldShow() {
        String[] tetherableWifiRegexs = this.mTm.getTetherableWifiRegexs();
        return (tetherableWifiRegexs == null || tetherableWifiRegexs.length == 0 || Utils.isMonkeyRunning() || getTetheringStateOfOtherInterfaces() == 0) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int tetheringStateOfOtherInterfaces = getTetheringStateOfOtherInterfaces();
        if (tetheringStateOfOtherInterfaces != 2) {
            if (tetheringStateOfOtherInterfaces != 4) {
                if (tetheringStateOfOtherInterfaces != 6) {
                    if (tetheringStateOfOtherInterfaces != 32) {
                        if (tetheringStateOfOtherInterfaces != 34) {
                            if (tetheringStateOfOtherInterfaces != 36) {
                                if (tetheringStateOfOtherInterfaces == 38) {
                                    return this.mContext.getString(R$string.disable_wifi_hotspot_when_usb_and_bluetooth_and_ethernet_on);
                                }
                                return this.mContext.getString(R$string.summary_placeholder);
                            }
                            return this.mContext.getString(R$string.disable_wifi_hotspot_when_bluetooth_and_ethernet_on);
                        }
                        return this.mContext.getString(R$string.disable_wifi_hotspot_when_usb_and_ethernet_on);
                    }
                    return this.mContext.getString(R$string.disable_wifi_hotspot_when_ethernet_on);
                }
                return this.mContext.getString(R$string.disable_wifi_hotspot_when_usb_and_bluetooth_on);
            }
            return this.mContext.getString(R$string.disable_wifi_hotspot_when_bluetooth_on);
        }
        return this.mContext.getString(R$string.disable_wifi_hotspot_when_usb_on);
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.network.TetherBasePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setVisible(isAvailable());
        refreshSummary(preference);
    }
}
