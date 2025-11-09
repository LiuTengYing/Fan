package com.android.settings.datetime;

import android.app.time.TimeManager;
import android.app.time.TimeZoneCapabilitiesAndConfig;
import android.app.time.TimeZoneConfiguration;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class LocationTimeZoneDetectionPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop, TimeManager.TimeZoneDetectorListener {
    private static final String TAG = "location_time_zone_detection";
    private InstrumentedPreferenceFragment mFragment;
    private final LocationManager mLocationManager;
    private Preference mPreference;
    private final TimeManager mTimeManager;
    private TimeZoneCapabilitiesAndConfig mTimeZoneCapabilitiesAndConfig;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationTimeZoneDetectionPreferenceController(Context context) {
        super(context, TAG);
        this.mTimeManager = (TimeManager) context.getSystemService(TimeManager.class);
        this.mLocationManager = (LocationManager) context.getSystemService(LocationManager.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFragment(InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        this.mFragment = instrumentedPreferenceFragment;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTimeManager.getTimeZoneCapabilitiesAndConfig().getConfiguration().isGeoDetectionEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z && !this.mLocationManager.isLocationEnabled()) {
            new LocationToggleDisabledDialogFragment(this.mContext).show(this.mFragment.getFragmentManager(), TAG);
            return false;
        }
        return this.mTimeManager.updateTimeZoneConfiguration(new TimeZoneConfiguration.Builder().setGeoDetectionEnabled(z).build());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mTimeManager.addTimeZoneDetectorListener(this.mContext.getMainExecutor(), this);
        refreshUi();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mTimeManager.removeTimeZoneDetectorListener(this);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        int configureGeoDetectionEnabledCapability = getTimeZoneCapabilitiesAndConfig(false).getCapabilities().getConfigureGeoDetectionEnabledCapability();
        if (configureGeoDetectionEnabledCapability == 10 || configureGeoDetectionEnabledCapability == 20) {
            return 3;
        }
        if (configureGeoDetectionEnabledCapability == 30 || configureGeoDetectionEnabledCapability == 40) {
            return 0;
        }
        throw new IllegalStateException("Unknown capability=" + configureGeoDetectionEnabledCapability);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        TimeZoneCapabilitiesAndConfig timeZoneCapabilitiesAndConfig = getTimeZoneCapabilitiesAndConfig(false);
        int configureGeoDetectionEnabledCapability = timeZoneCapabilitiesAndConfig.getCapabilities().getConfigureGeoDetectionEnabledCapability();
        TimeZoneConfiguration configuration = timeZoneCapabilitiesAndConfig.getConfiguration();
        if (configureGeoDetectionEnabledCapability == 10) {
            i = R$string.location_time_zone_detection_not_supported;
        } else if (configureGeoDetectionEnabledCapability == 20) {
            i = R$string.location_time_zone_detection_not_allowed;
        } else if (configureGeoDetectionEnabledCapability != 30) {
            if (configureGeoDetectionEnabledCapability == 40) {
                return "";
            }
            throw new IllegalStateException("Unexpected configureGeoDetectionEnabledCapability=" + configureGeoDetectionEnabledCapability);
        } else if (!this.mLocationManager.isLocationEnabled()) {
            i = R$string.location_app_permission_summary_location_off;
        } else if (!configuration.isAutoDetectionEnabled()) {
            i = R$string.location_time_zone_detection_auto_is_off;
        } else {
            i = R$string.location_time_zone_detection_not_applicable;
        }
        return this.mContext.getString(i);
    }

    public void onChange() {
        refreshUi();
    }

    private void refreshUi() {
        getTimeZoneCapabilitiesAndConfig(true);
        refreshSummary(this.mPreference);
    }

    private TimeZoneCapabilitiesAndConfig getTimeZoneCapabilitiesAndConfig(boolean z) {
        if (z || this.mTimeZoneCapabilitiesAndConfig == null) {
            this.mTimeZoneCapabilitiesAndConfig = this.mTimeManager.getTimeZoneCapabilitiesAndConfig();
        }
        return this.mTimeZoneCapabilitiesAndConfig;
    }
}
