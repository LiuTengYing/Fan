package com.android.settings.utils;

import com.android.settings.SettingsApplication;
/* loaded from: classes.dex */
public class SatelliteProgressUtils {
    public static int getProgressWidth() {
        int progressHeight;
        if (SettingsApplication.mWidthFix > SettingsApplication.mHeightFix) {
            progressHeight = getProgressHeight();
        } else {
            progressHeight = getProgressHeight();
        }
        return (int) (progressHeight * 0.1d);
    }

    public static int getProgressHeight() {
        double d;
        int i = SettingsApplication.mWidthFix;
        int i2 = SettingsApplication.mHeightFix;
        double d2 = 0.25d;
        if (i <= i2 || i2 > 1100) {
            d = i2;
        } else {
            d = i2;
            d2 = 0.21d;
        }
        return (int) (d * d2);
    }
}
