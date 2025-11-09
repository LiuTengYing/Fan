package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.permission.PermissionControllerManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class AppLocationPermissionPreferenceController extends LocationBasePreferenceController implements PreferenceControllerMixin {
    final AtomicInteger loadingInProgress;
    private final LocationManager mLocationManager;
    int mNumHasLocation;
    private int mNumHasLocationLoading;
    int mNumTotal;
    private int mNumTotalLoading;
    private Preference mPreference;

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

    public AppLocationPermissionPreferenceController(Context context, String str) {
        super(context, str);
        this.mNumTotal = -1;
        this.mNumHasLocation = -1;
        this.loadingInProgress = new AtomicInteger(0);
        this.mNumTotalLoading = 0;
        this.mNumHasLocationLoading = 0;
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "location_settings_link_to_permissions_enabled", 1) == 1 ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mLocationManager.isLocationEnabled()) {
            if (this.mNumTotal == -1 || this.mNumHasLocation == -1) {
                return this.mContext.getString(R$string.location_settings_loading_app_permission_stats);
            }
            Resources resources = this.mContext.getResources();
            int i = R$plurals.location_app_permission_summary_location_on;
            int i2 = this.mNumHasLocation;
            return resources.getQuantityString(i, i2, Integer.valueOf(i2), Integer.valueOf(this.mNumTotal));
        }
        return this.mContext.getString(R$string.location_app_permission_summary_location_off);
    }

    private void setAppCounts(int i, int i2) {
        this.mNumTotal = i;
        this.mNumHasLocation = i2;
        refreshSummary(this.mPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference = preference;
        refreshSummary(preference);
        if (this.mLocationManager.isLocationEnabled() && this.loadingInProgress.get() == 0) {
            this.mNumTotalLoading = 0;
            this.mNumHasLocationLoading = 0;
            List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserProfiles();
            this.loadingInProgress.set(userProfiles.size() * 2);
            for (UserHandle userHandle : userProfiles) {
                Context createPackageContextAsUser = Utils.createPackageContextAsUser(this.mContext, userHandle.getIdentifier());
                if (createPackageContextAsUser == null) {
                    for (int i = 0; i < 2; i++) {
                        if (this.loadingInProgress.decrementAndGet() == 0) {
                            setAppCounts(this.mNumTotalLoading, this.mNumHasLocationLoading);
                        }
                    }
                } else {
                    PermissionControllerManager permissionControllerManager = (PermissionControllerManager) createPackageContextAsUser.getSystemService(PermissionControllerManager.class);
                    permissionControllerManager.countPermissionApps(Arrays.asList("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"), 0, new PermissionControllerManager.OnCountPermissionAppsResultCallback() { // from class: com.android.settings.location.AppLocationPermissionPreferenceController$$ExternalSyntheticLambda0
                        public final void onCountPermissionApps(int i2) {
                            AppLocationPermissionPreferenceController.this.lambda$updateState$0(i2);
                        }
                    }, (Handler) null);
                    permissionControllerManager.countPermissionApps(Arrays.asList("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"), 1, new PermissionControllerManager.OnCountPermissionAppsResultCallback() { // from class: com.android.settings.location.AppLocationPermissionPreferenceController$$ExternalSyntheticLambda1
                        public final void onCountPermissionApps(int i2) {
                            AppLocationPermissionPreferenceController.this.lambda$updateState$1(i2);
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
            setAppCounts(this.mNumTotalLoading, this.mNumHasLocationLoading);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(int i) {
        this.mNumHasLocationLoading += i;
        if (this.loadingInProgress.decrementAndGet() == 0) {
            setAppCounts(this.mNumTotalLoading, this.mNumHasLocationLoading);
        }
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }
}
