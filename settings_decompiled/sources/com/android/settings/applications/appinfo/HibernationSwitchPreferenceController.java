package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.apphibernation.AppHibernationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.permission.PermissionControllerManager;
import android.provider.DeviceConfig;
import android.util.Slog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import java.util.function.IntConsumer;
/* loaded from: classes.dex */
public final class HibernationSwitchPreferenceController extends AppInfoPreferenceControllerBase implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "HibernationSwitchPrefController";
    private final AppOpsManager mAppOpsManager;
    private int mHibernationEligibility;
    private boolean mHibernationEligibilityLoaded;
    private boolean mIsPackageExemptByDefault;
    boolean mIsPackageSet;
    private String mPackageName;
    private int mPackageUid;
    private final PermissionControllerManager mPermissionControllerManager;

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

    public HibernationSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mHibernationEligibility = -1;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mPermissionControllerManager = (PermissionControllerManager) context.getSystemService(PermissionControllerManager.class);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (isHibernationEnabled() && this.mIsPackageSet) ? 0 : 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPackage(String str) {
        this.mPackageName = str;
        PackageManager packageManager = this.mContext.getPackageManager();
        int i = packageManager.hasSystemFeature("android.hardware.type.automotive") ? 30 : 29;
        try {
            this.mPackageUid = packageManager.getPackageUid(str, 0);
            this.mIsPackageExemptByDefault = !hibernationTargetsPreSApps() && packageManager.getTargetSdkVersion(str) <= i;
            this.mIsPackageSet = true;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w(TAG, "Package [" + this.mPackageName + "] is not found!");
            this.mIsPackageSet = false;
        }
    }

    private boolean isAppEligibleForHibernation() {
        int i;
        return (!this.mHibernationEligibilityLoaded || (i = this.mHibernationEligibility) == 1 || i == -1) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        super.updateState(preference);
        ((SwitchPreference) preference).setChecked(isAppEligibleForHibernation() && !isPackageHibernationExemptByUser());
        preference.setEnabled(isAppEligibleForHibernation());
        if (this.mHibernationEligibilityLoaded) {
            return;
        }
        this.mPermissionControllerManager.getHibernationEligibility(this.mPackageName, this.mContext.getMainExecutor(), new IntConsumer() { // from class: com.android.settings.applications.appinfo.HibernationSwitchPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.IntConsumer
            public final void accept(int i) {
                HibernationSwitchPreferenceController.this.lambda$updateState$0(preference, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(Preference preference, int i) {
        this.mHibernationEligibility = i;
        this.mHibernationEligibilityLoaded = true;
        updateState(preference);
    }

    boolean isPackageHibernationExemptByUser() {
        if (this.mIsPackageSet) {
            int unsafeCheckOpNoThrow = this.mAppOpsManager.unsafeCheckOpNoThrow("android:auto_revoke_permissions_if_unused", this.mPackageUid, this.mPackageName);
            if (unsafeCheckOpNoThrow == 3) {
                return this.mIsPackageExemptByDefault;
            }
            return unsafeCheckOpNoThrow != 0;
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.mAppOpsManager.setUidMode("android:auto_revoke_permissions_if_unused", this.mPackageUid, booleanValue ? 0 : 1);
            if (!booleanValue) {
                AppHibernationManager appHibernationManager = (AppHibernationManager) this.mContext.getSystemService(AppHibernationManager.class);
                appHibernationManager.setHibernatingForUser(this.mPackageName, false);
                appHibernationManager.setHibernatingGlobally(this.mPackageName, false);
            }
            return true;
        } catch (RuntimeException unused) {
            return false;
        }
    }

    private static boolean isHibernationEnabled() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_enabled", true);
    }

    private static boolean hibernationTargetsPreSApps() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_targets_pre_s_apps", false);
    }
}
