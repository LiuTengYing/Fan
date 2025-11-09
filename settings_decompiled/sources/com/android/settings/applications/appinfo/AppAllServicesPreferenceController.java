package com.android.settings.applications.appinfo;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import java.util.Objects;
/* loaded from: classes.dex */
public class AppAllServicesPreferenceController extends AppInfoPreferenceControllerBase {
    private static final String SUMMARY_METADATA_KEY = "app_features_preference_summary";
    private static final String TAG = "AllServicesPrefControl";
    private boolean mCanPackageHandleAllServicesIntent;
    private boolean mIsLocationProvider;
    private final PackageManager mPackageManager;
    private String mPackageName;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppAllServicesPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
        this.mCanPackageHandleAllServicesIntent = false;
        this.mIsLocationProvider = false;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        CharSequence storageSummary = getStorageSummary();
        if (storageSummary != null) {
            this.mPreference.setSummary(storageSummary);
        }
    }

    CharSequence getStorageSummary() {
        ResolveInfo resolveInfo = getResolveInfo(128);
        if (resolveInfo == null) {
            Log.d(TAG, "mResolveInfo is null.");
            return null;
        }
        Bundle bundle = resolveInfo.activityInfo.metaData;
        if (bundle != null) {
            try {
                return this.mPackageManager.getResourcesForActivity(new ComponentName(this.mPackageName, resolveInfo.activityInfo.name)).getString(bundle.getInt(SUMMARY_METADATA_KEY));
            } catch (PackageManager.NameNotFoundException unused) {
                Log.d(TAG, "Name of resource not found for summary string.");
            } catch (Resources.NotFoundException unused2) {
                Log.d(TAG, "Resource not found for summary string.");
            }
        }
        return null;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mCanPackageHandleAllServicesIntent && this.mIsLocationProvider) ? 0 : 2;
    }

    private boolean isLocationProvider() {
        LocationManager locationManager = (LocationManager) this.mContext.getSystemService(LocationManager.class);
        Objects.requireNonNull(locationManager);
        return locationManager.isProviderPackage(this.mPackageName);
    }

    private boolean canPackageHandleIntent() {
        return getResolveInfo(0) != null;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            startAllServicesActivity();
            return true;
        }
        return false;
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
        updateAvailabilityConditions();
    }

    private void updateAvailabilityConditions() {
        this.mCanPackageHandleAllServicesIntent = canPackageHandleIntent();
        this.mIsLocationProvider = isLocationProvider();
    }

    private void startAllServicesActivity() {
        Intent intent = new Intent("android.intent.action.VIEW_APP_FEATURES");
        ResolveInfo resolveInfo = getResolveInfo(0);
        if (resolveInfo == null) {
            Log.d(TAG, "resolveInfo is null");
            return;
        }
        intent.setComponent(new ComponentName(this.mPackageName, resolveInfo.activityInfo.name));
        FragmentActivity activity = this.mParent.getActivity();
        if (activity != null) {
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                Log.e(TAG, "The app cannot handle android.intent.action.VIEW_APP_FEATURES");
            }
        }
    }

    private ResolveInfo getResolveInfo(int i) {
        if (this.mPackageName == null) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.VIEW_APP_FEATURES");
        intent.setPackage(this.mPackageName);
        return this.mPackageManager.resolveActivity(intent, i);
    }
}
