package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.location.LocationEnabler;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public abstract class LocationBasePreferenceController extends BasePreferenceController implements LocationEnabler.LocationModeChangeListener {
    protected DashboardFragment mFragment;
    protected Lifecycle mLifecycle;
    protected LocationEnabler mLocationEnabler;
    protected UserManager mUserManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public abstract /* synthetic */ void onLocationModeChanged(int i, boolean z);

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationBasePreferenceController(Context context, String str) {
        super(context, str);
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
    }

    public void init(DashboardFragment dashboardFragment) {
        this.mFragment = dashboardFragment;
        this.mLifecycle = dashboardFragment.getSettingsLifecycle();
        this.mLocationEnabler = new LocationEnabler(this.mContext, this, this.mLifecycle);
    }
}
