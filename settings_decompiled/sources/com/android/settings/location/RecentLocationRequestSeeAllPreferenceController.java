package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settingslib.location.RecentLocationApps;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RecentLocationRequestSeeAllPreferenceController extends LocationBasePreferenceController {
    private PreferenceScreen mCategoryAllRecentLocationRequests;
    private Preference mPreference;
    private RecentLocationApps mRecentLocationApps;
    private boolean mShowSystem;
    private int mType;

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

    public RecentLocationRequestSeeAllPreferenceController(Context context, String str) {
        super(context, str);
        this.mShowSystem = false;
        this.mType = 3;
        this.mRecentLocationApps = new RecentLocationApps(context);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mCategoryAllRecentLocationRequests.setEnabled(this.mLocationEnabler.isEnabled(i));
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mCategoryAllRecentLocationRequests = (PreferenceScreen) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mCategoryAllRecentLocationRequests.removeAll();
        this.mPreference = preference;
        UserManager userManager = UserManager.get(this.mContext);
        ArrayList<RecentLocationApps.Request> arrayList = new ArrayList();
        for (RecentLocationApps.Request request : this.mRecentLocationApps.getAppListSorted(this.mShowSystem)) {
            if (RecentLocationRequestPreferenceController.isRequestMatchesProfileType(userManager, request, this.mType)) {
                arrayList.add(request);
            }
        }
        if (arrayList.isEmpty()) {
            AppPreference appPreference = new AppPreference(this.mContext);
            appPreference.setTitle(R$string.location_no_recent_apps);
            appPreference.setSelectable(false);
            this.mCategoryAllRecentLocationRequests.addPreference(appPreference);
            return;
        }
        for (RecentLocationApps.Request request2 : arrayList) {
            this.mCategoryAllRecentLocationRequests.addPreference(RecentLocationRequestPreferenceController.createAppPreference(preference.getContext(), request2, this.mFragment));
        }
    }

    public void setProfileType(int i) {
        this.mType = i;
    }

    public void setShowSystem(boolean z) {
        this.mShowSystem = z;
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }
}
