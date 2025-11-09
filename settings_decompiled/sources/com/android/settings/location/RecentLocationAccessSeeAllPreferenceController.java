package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.RecentAppOpsAccess;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RecentLocationAccessSeeAllPreferenceController extends LocationBasePreferenceController {
    private PreferenceScreen mCategoryAllRecentLocationAccess;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private Preference mPreference;
    private final RecentAppOpsAccess mRecentLocationAccesses;
    private boolean mShowSystem;

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

    public RecentLocationAccessSeeAllPreferenceController(Context context, String str) {
        super(context, str);
        boolean z = false;
        this.mShowSystem = false;
        if (DeviceConfig.getBoolean("privacy", "location_indicators_small_enabled", false) && Settings.Secure.getInt(this.mContext.getContentResolver(), "locationShowSystemOps", 0) == 1) {
            z = true;
        }
        this.mShowSystem = z;
        this.mRecentLocationAccesses = RecentAppOpsAccess.createForLocation(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mCategoryAllRecentLocationAccess.setEnabled(this.mLocationEnabler.isEnabled(i));
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mCategoryAllRecentLocationAccess = (PreferenceScreen) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mCategoryAllRecentLocationAccess.removeAll();
        this.mPreference = preference;
        UserManager userManager = UserManager.get(this.mContext);
        ArrayList<RecentAppOpsAccess.Access> arrayList = new ArrayList();
        for (RecentAppOpsAccess.Access access : this.mRecentLocationAccesses.getAppListSorted(this.mShowSystem)) {
            if (RecentLocationAccessPreferenceController.isRequestMatchesProfileType(userManager, access, 3)) {
                arrayList.add(access);
            }
        }
        if (arrayList.isEmpty()) {
            AppPreference appPreference = new AppPreference(this.mContext);
            appPreference.setTitle(R$string.location_no_recent_apps);
            appPreference.setSelectable(false);
            this.mCategoryAllRecentLocationAccess.addPreference(appPreference);
            return;
        }
        for (RecentAppOpsAccess.Access access2 : arrayList) {
            this.mCategoryAllRecentLocationAccess.addPreference(RecentLocationAccessPreferenceController.createAppPreference(preference.getContext(), access2, this.mFragment));
        }
    }

    public void setShowSystem(boolean z) {
        this.mShowSystem = z;
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
            this.mMetricsFeatureProvider.logClickedPreference(this.mPreference, getMetricsCategory());
        }
    }
}
