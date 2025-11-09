package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.TetheringManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public abstract class WifiTetherBasePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    protected int mHotspotState;
    protected final OnTetherConfigUpdateListener mListener;
    protected Preference mPreference;
    protected SoftApConfiguration mSoftApConfig;
    protected final TetheringManager mTm;
    protected final WifiManager mWifiManager;
    protected final String[] mWifiRegexs;

    /* loaded from: classes2.dex */
    public interface OnTetherConfigUpdateListener {
        void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController);
    }

    public abstract void updateDisplay();

    public WifiTetherBasePreferenceController(Context context, OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context);
        this.mHotspotState = 11;
        this.mListener = onTetherConfigUpdateListener;
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mWifiManager = wifiManager;
        TetheringManager tetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        this.mTm = tetheringManager;
        this.mWifiRegexs = tetheringManager.getTetherableWifiRegexs();
        this.mSoftApConfig = wifiManager.getSoftApConfiguration();
        this.mHotspotState = wifiManager.getWifiApState();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String[] strArr;
        return (this.mWifiManager == null || (strArr = this.mWifiRegexs) == null || strArr.length <= 0) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        updateDisplay();
    }

    public void updateWifiApConfig(SoftApConfiguration softApConfiguration) {
        this.mSoftApConfig = softApConfiguration;
    }

    public void updateWifiApState(int i) {
        this.mHotspotState = i;
    }
}
