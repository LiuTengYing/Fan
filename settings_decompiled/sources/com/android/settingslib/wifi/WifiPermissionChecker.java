package com.android.settingslib.wifi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
/* loaded from: classes2.dex */
public class WifiPermissionChecker {
    private IActivityManager mActivityManager;
    private String mLaunchedPackage;
    private PackageManager mPackageManager;

    public WifiPermissionChecker(Activity activity) {
        this(activity, ActivityManager.getService());
    }

    public WifiPermissionChecker(Activity activity, IActivityManager iActivityManager) {
        this.mActivityManager = iActivityManager;
        this.mPackageManager = activity.getPackageManager();
        this.mLaunchedPackage = getLaunchedFromPackage(activity);
    }

    public String getLaunchedPackage() {
        return this.mLaunchedPackage;
    }

    public boolean canAccessWifiState() {
        return checkPermission("android.permission.ACCESS_WIFI_STATE");
    }

    public boolean canAccessFineLocation() {
        return checkPermission("android.permission.ACCESS_FINE_LOCATION");
    }

    private boolean checkPermission(String str) {
        if (this.mPackageManager == null || TextUtils.isEmpty(this.mLaunchedPackage)) {
            Log.e("WifiPermChecker", "Failed to check package permission! {PackageManager:" + this.mPackageManager + ", LaunchedPackage:" + this.mLaunchedPackage + "}");
            return false;
        } else if (this.mPackageManager.checkPermission(str, this.mLaunchedPackage) == 0) {
            return true;
        } else {
            Log.w("WifiPermChecker", "The launched package does not have the required permission! {LaunchedPackage:" + this.mLaunchedPackage + ", Permission:" + str + "}");
            return false;
        }
    }

    private String getLaunchedFromPackage(Activity activity) {
        try {
            return this.mActivityManager.getLaunchedFromPackage(activity.getActivityToken());
        } catch (RemoteException unused) {
            Log.e("WifiPermChecker", "Can not get the launched package from activity manager!");
            return null;
        }
    }
}
