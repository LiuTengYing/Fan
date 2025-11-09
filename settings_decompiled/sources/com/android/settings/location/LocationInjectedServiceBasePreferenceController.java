package com.android.settings.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class LocationInjectedServiceBasePreferenceController extends LocationBasePreferenceController implements LifecycleObserver {
    static final IntentFilter INTENT_FILTER_INJECTED_SETTING_CHANGED = new IntentFilter("android.location.InjectedSettingChanged");
    private static final String TAG = "LocationPrefCtrl";
    BroadcastReceiver mInjectedSettingsReceiver;
    AppSettingsInjector mInjector;

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

    protected abstract void injectLocationServices(PreferenceScreen preferenceScreen);

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

    public LocationInjectedServiceBasePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController
    public void init(DashboardFragment dashboardFragment) {
        super.init(dashboardFragment);
        this.mInjector = new AppSettingsInjector(this.mContext, getMetricsCategory());
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        injectLocationServices(preferenceScreen);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mInjector.reloadStatusMessages();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mInjectedSettingsReceiver == null) {
            this.mInjectedSettingsReceiver = new BroadcastReceiver() { // from class: com.android.settings.location.LocationInjectedServiceBasePreferenceController.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (Log.isLoggable(LocationInjectedServiceBasePreferenceController.TAG, 3)) {
                        Log.d(LocationInjectedServiceBasePreferenceController.TAG, "Received settings change intent: " + intent);
                    }
                    LocationInjectedServiceBasePreferenceController.this.mInjector.reloadStatusMessages();
                }
            };
        }
        this.mContext.registerReceiver(this.mInjectedSettingsReceiver, INTENT_FILTER_INJECTED_SETTING_CHANGED, 2);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mContext.unregisterReceiver(this.mInjectedSettingsReceiver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<Integer, List<Preference>> getLocationServices() {
        int managedProfileId = Utils.getManagedProfileId(this.mUserManager, UserHandle.myUserId());
        return this.mInjector.getInjectedSettings(this.mFragment.getPreferenceManager().getContext(), (managedProfileId == -10000 || this.mLocationEnabler.getShareLocationEnforcedAdmin(managedProfileId) == null) ? -2 : UserHandle.myUserId());
    }
}
