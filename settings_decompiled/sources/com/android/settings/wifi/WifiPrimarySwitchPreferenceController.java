package com.android.settings.wifi;

import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.GenericSwitchController;
import com.android.settings.widget.SummaryUpdater;
import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class WifiPrimarySwitchPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, SummaryUpdater.OnSummaryChangeListener, LifecycleObserver, OnResume, OnPause, OnStart, OnStop {
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final WifiSummaryUpdater mSummaryHelper;
    private WifiEnabler mWifiEnabler;
    private PrimarySwitchPreference mWifiPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "main_toggle_wifi";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mWifiPreference = (PrimarySwitchPreference) preferenceScreen.findPreference("main_toggle_wifi");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_wifi_settings);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mSummaryHelper.register(true);
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.resume(this.mContext);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.pause();
        }
        this.mSummaryHelper.register(false);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mWifiEnabler = new WifiEnabler(this.mContext, new GenericSwitchController(this.mWifiPreference), this.mMetricsFeatureProvider);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.teardownSwitchController();
        }
    }

    @Override // com.android.settings.widget.SummaryUpdater.OnSummaryChangeListener
    public void onSummaryChanged(String str) {
        PrimarySwitchPreference primarySwitchPreference = this.mWifiPreference;
        if (primarySwitchPreference != null) {
            primarySwitchPreference.setSummary(str);
        }
    }
}
