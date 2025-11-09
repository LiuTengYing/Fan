package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserHandle;
import java.util.List;
/* loaded from: classes.dex */
public abstract class InstalledAppCounter extends AppCounter {
    private final int mInstallReason;

    public InstalledAppCounter(Context context, int i, PackageManager packageManager) {
        super(context, packageManager);
        this.mInstallReason = i;
    }

    @Override // com.android.settings.applications.AppCounter
    protected boolean includeInCount(ApplicationInfo applicationInfo) {
        return includeInCount(this.mInstallReason, this.mPm, applicationInfo);
    }

    public static boolean includeInCount(int i, PackageManager packageManager, ApplicationInfo applicationInfo) {
        int userId = UserHandle.getUserId(applicationInfo.uid);
        if (i == -1 || packageManager.getInstallReason(applicationInfo.packageName, new UserHandle(userId)) == i) {
            int i2 = applicationInfo.flags;
            if ((i2 & 128) == 0 && (i2 & 1) != 0) {
                List queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER").setPackage(applicationInfo.packageName), 786944, userId);
                return (queryIntentActivitiesAsUser == null || queryIntentActivitiesAsUser.size() == 0) ? false : true;
            }
            return true;
        }
        return false;
    }
}
