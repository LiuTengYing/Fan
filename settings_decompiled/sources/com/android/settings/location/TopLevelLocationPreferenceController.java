package com.android.settings.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationFeaturesUtils;
import android.location.LocationManager;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.permission.PermissionControllerManager;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class TopLevelLocationPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final IntentFilter INTENT_FILTER_LOCATION_MODE_CHANGED = new IntentFilter("android.location.MODE_CHANGED");
    private static String sSummary = null;
    private AtomicInteger loadingInProgress;
    private final LocationManager mLocationManager;
    private int mNumTotalLoading;
    private Preference mPreference;
    private BroadcastReceiver mReceiver;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TopLevelLocationPreferenceController(Context context, String str) {
        super(context, str);
        this.mNumTotalLoading = 0;
        this.loadingInProgress = new AtomicInteger(0);
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return LocationFeaturesUtils.getInstance(this.mContext).isLocationDisabled() ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mLocationManager.isLocationEnabled()) {
            if (sSummary == null) {
                sSummary = this.mContext.getString(R$string.location_settings_loading_app_permission_stats);
            }
            return sSummary;
        }
        return this.mContext.getString(R$string.location_settings_summary_location_off);
    }

    void setLocationAppCount(int i) {
        sSummary = this.mContext.getResources().getQuantityString(R$plurals.location_settings_summary_location_on, i, Integer.valueOf(i));
        refreshSummary(this.mPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference = preference;
        refreshSummary(preference);
        if (this.mLocationManager.isLocationEnabled() && this.loadingInProgress.get() == 0) {
            this.mNumTotalLoading = 0;
            List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserProfiles();
            this.loadingInProgress.set(userProfiles.size());
            for (UserHandle userHandle : userProfiles) {
                Context createPackageContextAsUser = Utils.createPackageContextAsUser(this.mContext, userHandle.getIdentifier());
                if (createPackageContextAsUser == null) {
                    if (this.loadingInProgress.decrementAndGet() == 0) {
                        setLocationAppCount(this.mNumTotalLoading);
                    }
                } else {
                    ((PermissionControllerManager) createPackageContextAsUser.getSystemService(PermissionControllerManager.class)).countPermissionApps(Arrays.asList("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"), 1, new PermissionControllerManager.OnCountPermissionAppsResultCallback() { // from class: com.android.settings.location.TopLevelLocationPreferenceController$$ExternalSyntheticLambda0
                        public final void onCountPermissionApps(int i) {
                            TopLevelLocationPreferenceController.this.lambda$updateState$0(i);
                        }
                    }, (Handler) null);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(int i) {
        this.mNumTotalLoading += i;
        if (this.loadingInProgress.decrementAndGet() == 0) {
            setLocationAppCount(this.mNumTotalLoading);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mReceiver == null) {
            this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.location.TopLevelLocationPreferenceController.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    TopLevelLocationPreferenceController.this.refreshLocationMode();
                }
            };
        }
        this.mContext.registerReceiver(this.mReceiver, INTENT_FILTER_LOCATION_MODE_CHANGED);
        refreshLocationMode();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshLocationMode() {
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }
}
