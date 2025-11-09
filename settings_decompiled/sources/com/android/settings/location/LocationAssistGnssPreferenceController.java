package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationFeaturesUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
/* loaded from: classes.dex */
public class LocationAssistGnssPreferenceController extends LocationBasePreferenceController {
    private static boolean GNSS_DISABLED = false;
    private static final String KEY_LOCATION_ASSIST_GNSS = "location_assist_gnss";
    private static boolean SUPPORT_AGPS_SETTING = false;
    private static final String TAG = "LocationAssistGnssPreferenceController";
    private Preference mAssistGnssPre;

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationAssistGnssPreferenceController(Context context, String str) {
        super(context, str);
        SUPPORT_AGPS_SETTING = LocationFeaturesUtils.getInstance(this.mContext).isSupportAgpsSettings();
        GNSS_DISABLED = LocationFeaturesUtils.getInstance(this.mContext).isGnssDisabled();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (GNSS_DISABLED || !SUPPORT_AGPS_SETTING) ? 3 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mAssistGnssPre = preferenceScreen.findPreference(KEY_LOCATION_ASSIST_GNSS);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        String str = TAG;
        Log.d(str, "onLocationModeChanged location mode = " + i);
        Preference preference = this.mAssistGnssPre;
        if (preference != null) {
            preference.setEnabled(this.mLocationEnabler.isEnabled(i));
        }
    }
}
