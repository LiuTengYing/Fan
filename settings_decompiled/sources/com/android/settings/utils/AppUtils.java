package com.android.settings.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
/* loaded from: classes.dex */
public class AppUtils {
    public static boolean isAppInstalled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (Exception unused) {
        }
        return packageInfo != null;
    }
}
