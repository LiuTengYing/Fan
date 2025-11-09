package com.android.settings.applications.specialaccess.zenaccess;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class ZenAccessController extends BasePreferenceController {
    private static final String TAG = "ZenAccessController";

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public ZenAccessController(Context context, String str) {
        super(context, str);
    }

    public static Set<String> getPackagesRequestingNotificationPolicyAccess() {
        return getPackagesWithPermissions(new String[]{"android.permission.ACCESS_NOTIFICATION_POLICY"});
    }

    public static Set<String> getPackagesWithManageNotifications() {
        return getPackagesWithPermissions(new String[]{"android.permission.MANAGE_NOTIFICATIONS"});
    }

    public static Set<String> getPackagesWithPermissions(String[] strArr) {
        ArraySet arraySet = new ArraySet();
        try {
            List<PackageInfo> list = AppGlobals.getPackageManager().getPackagesHoldingPermissions(strArr, 0L, ActivityManager.getCurrentUser()).getList();
            if (list != null) {
                for (PackageInfo packageInfo : list) {
                    if (packageInfo.applicationInfo.enabled) {
                        arraySet.add(packageInfo.packageName);
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot reach packagemanager", e);
        }
        return arraySet;
    }

    public static Set<String> getAutoApprovedPackages(Context context) {
        ArraySet arraySet = new ArraySet();
        arraySet.addAll(((NotificationManager) context.getSystemService(NotificationManager.class)).getEnabledNotificationListenerPackages());
        return arraySet;
    }

    public static boolean hasAccess(Context context, String str) {
        return ((NotificationManager) context.getSystemService(NotificationManager.class)).isNotificationPolicyAccessGrantedForPackage(str);
    }

    public static void setAccess(Context context, String str, boolean z) {
        logSpecialPermissionChange(z, str, context);
        ((NotificationManager) context.getSystemService(NotificationManager.class)).setNotificationPolicyAccessGranted(str, z);
    }

    public static void deleteRules(Context context, String str) {
        ((NotificationManager) context.getSystemService(NotificationManager.class)).removeAutomaticZenRules(str);
    }

    static void logSpecialPermissionChange(boolean z, String str, Context context) {
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(context, z ? 768 : 769, str);
    }
}
