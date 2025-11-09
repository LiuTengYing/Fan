package com.android.settings.display.darkmode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.FeatureFlagUtils;
/* loaded from: classes.dex */
public final class BedtimeSettings {
    private final ActivityManager mActivityManager;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final String mWellbeingPackage;

    public BedtimeSettings(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mWellbeingPackage = context.getResources().getString(17039950);
        this.mActivityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
    }

    public Intent getBedtimeSettingsIntent() {
        Intent intent;
        ResolveInfo resolveActivity;
        if (!FeatureFlagUtils.isEnabled(this.mContext, "settings_app_allow_dark_theme_activation_at_bedtime") || this.mActivityManager.isLowRamDevice() || (resolveActivity = this.mPackageManager.resolveActivity((intent = new Intent("android.settings.BEDTIME_SETTINGS").setPackage(this.mWellbeingPackage)), 65536)) == null || !resolveActivity.activityInfo.isEnabled()) {
            return null;
        }
        return intent;
    }
}
