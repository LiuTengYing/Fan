package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserManager;
/* loaded from: classes.dex */
public abstract class InstalledAppLister extends AppLister {
    public InstalledAppLister(PackageManager packageManager, UserManager userManager) {
        super(packageManager, userManager);
    }

    @Override // com.android.settings.applications.AppLister
    protected boolean includeInCount(ApplicationInfo applicationInfo) {
        return InstalledAppCounter.includeInCount(1, this.mPm, applicationInfo);
    }
}
