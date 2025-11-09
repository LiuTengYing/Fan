package com.android.wifitrackerlib;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserManager;
import android.util.ArraySet;
import java.util.Set;
/* loaded from: classes2.dex */
class WifiTrackerInjector {
    private final DevicePolicyManager mDevicePolicyManager;
    private final boolean mIsDemoMode;
    private final Set<String> mNoAttributionAnnotationPackages = new ArraySet();
    private final UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiTrackerInjector(Context context) {
        this.mIsDemoMode = NonSdkApiWrapper.isDemoMode(context);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        for (String str : context.getString(R$string.wifitrackerlib_no_attribution_annotation_packages).split(",")) {
            this.mNoAttributionAnnotationPackages.add(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDemoMode() {
        return this.mIsDemoMode;
    }

    public UserManager getUserManager() {
        return this.mUserManager;
    }

    public DevicePolicyManager getDevicePolicyManager() {
        return this.mDevicePolicyManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<String> getNoAttributionAnnotationPackages() {
        return this.mNoAttributionAnnotationPackages;
    }
}
