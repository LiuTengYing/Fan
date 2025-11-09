package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public class WifiWakeupPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnPause, OnResume {
    private static final String KEY_ENABLE_WIFI_WAKEUP = "enable_wifi_wakeup";
    private static final String TAG = "WifiWakeupPrefController";
    private Fragment mFragment;
    private final IntentFilter mLocationFilter;
    LocationManager mLocationManager;
    private final BroadcastReceiver mLocationReceiver;
    SwitchPreference mPreference;
    WifiManager mWifiManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiWakeupPreferenceController(Context context) {
        super(context, KEY_ENABLE_WIFI_WAKEUP);
        this.mLocationReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WifiWakeupPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiWakeupPreferenceController wifiWakeupPreferenceController = WifiWakeupPreferenceController.this;
                wifiWakeupPreferenceController.updateState(wifiWakeupPreferenceController.mPreference);
            }
        };
        this.mLocationFilter = new IntentFilter("android.location.MODE_CHANGED");
        this.mLocationManager = (LocationManager) context.getSystemService("location");
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return getWifiWakeupEnabled() && getWifiScanningEnabled() && this.mLocationManager.isLocationEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            if (this.mFragment == null) {
                throw new IllegalStateException("No fragment to start activity");
            }
            if (!this.mLocationManager.isLocationEnabled()) {
                this.mFragment.startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 600);
                return false;
            } else if (!getWifiScanningEnabled()) {
                showScanningDialog();
                return false;
            }
        }
        setWifiWakeupEnabled(z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!this.mLocationManager.isLocationEnabled()) {
            return getNoLocationSummary();
        }
        return this.mContext.getText(R$string.wifi_wakeup_summary);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_network;
    }

    CharSequence getNoLocationSummary() {
        return AnnotationSpan.linkify(this.mContext.getText(R$string.wifi_wakeup_summary_no_location), new AnnotationSpan.LinkInfo("link", null));
    }

    public void onActivityResult(int i, int i2) {
        if (i == 600 && this.mLocationManager.isLocationEnabled() && getWifiScanningEnabled()) {
            setWifiWakeupEnabled(true);
            updateState(this.mPreference);
        }
    }

    private boolean getWifiScanningEnabled() {
        return this.mWifiManager.isScanAlwaysAvailable();
    }

    private void showScanningDialog() {
        WifiScanningRequiredFragment newInstance = WifiScanningRequiredFragment.newInstance();
        newInstance.setTargetFragment(this.mFragment, 600);
        newInstance.show(this.mFragment.getFragmentManager(), TAG);
    }

    private boolean getWifiWakeupEnabled() {
        return this.mWifiManager.isAutoWakeupEnabled();
    }

    private void setWifiWakeupEnabled(boolean z) {
        this.mWifiManager.setAutoWakeupEnabled(z);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.registerReceiver(this.mLocationReceiver, this.mLocationFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mLocationReceiver);
    }
}
