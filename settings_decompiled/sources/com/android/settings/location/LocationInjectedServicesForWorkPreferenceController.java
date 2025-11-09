package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.widget.RestrictedAppPreference;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class LocationInjectedServicesForWorkPreferenceController extends LocationInjectedServiceBasePreferenceController {
    private static final String TAG = "LocationWorkPrefCtrl";

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationInjectedServicesForWorkPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.location.LocationInjectedServiceBasePreferenceController
    protected void injectLocationServices(PreferenceScreen preferenceScreen) {
        for (Map.Entry<Integer, List<Preference>> entry : getLocationServices().entrySet()) {
            for (Preference preference : entry.getValue()) {
                if (preference instanceof RestrictedAppPreference) {
                    ((RestrictedAppPreference) preference).checkRestrictionAndSetDisabled();
                }
            }
            if (entry.getKey().intValue() != UserHandle.myUserId()) {
                LocationSettings.addPreferencesSorted(entry.getValue(), preferenceScreen);
            }
        }
    }
}
