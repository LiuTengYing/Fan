package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.wifitrackerlib.WifiPickerTracker;
/* loaded from: classes.dex */
public class CarrierWifiTogglePreferenceController extends TogglePreferenceController implements WifiPickerTracker.WifiPickerTrackerCallback {
    protected static final String CARRIER_WIFI_NETWORK_PREF_KEY = "carrier_wifi_network";
    protected static final String CARRIER_WIFI_TOGGLE_PREF_KEY = "carrier_wifi_toggle";
    private static final String TAG = "CarrierWifiTogglePreferenceController";
    protected Preference mCarrierNetworkPreference;
    protected final Context mContext;
    protected boolean mIsCarrierProvisionWifiEnabled;
    protected int mSubId;
    protected WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CarrierWifiTogglePreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        WifiPickerTrackerHelper wifiPickerTrackerHelper = new WifiPickerTrackerHelper(lifecycle, this.mContext, this);
        this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
        this.mIsCarrierProvisionWifiEnabled = wifiPickerTrackerHelper.isCarrierNetworkProvisionEnabled(this.mSubId);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mIsCarrierProvisionWifiEnabled ? 0 : 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mWifiPickerTrackerHelper.isCarrierNetworkEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        if (wifiPickerTrackerHelper == null) {
            return false;
        }
        wifiPickerTrackerHelper.setCarrierNetworkEnabled(z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mCarrierNetworkPreference = preferenceScreen.findPreference(CARRIER_WIFI_NETWORK_PREF_KEY);
        updateCarrierNetworkPreference();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_network;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        updateCarrierNetworkPreference();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        updateCarrierNetworkPreference();
    }

    protected void updateCarrierNetworkPreference() {
        if (this.mCarrierNetworkPreference == null) {
            return;
        }
        if (getAvailabilityStatus() != 0 || !isCarrierNetworkActive()) {
            this.mCarrierNetworkPreference.setVisible(false);
            return;
        }
        this.mCarrierNetworkPreference.setVisible(true);
        this.mCarrierNetworkPreference.setSummary(getCarrierNetworkSsid());
    }

    protected boolean isCarrierNetworkActive() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        if (wifiPickerTrackerHelper == null) {
            return false;
        }
        return wifiPickerTrackerHelper.isCarrierNetworkActive();
    }

    protected String getCarrierNetworkSsid() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        if (wifiPickerTrackerHelper == null) {
            return null;
        }
        return wifiPickerTrackerHelper.getCarrierNetworkSsid();
    }
}
