package com.android.settings.utils;
/* loaded from: classes.dex */
public class FastDoubleClickUtils {
    private static long mLastClickTime;

    public static boolean isFastDoubleClick() {
        long currentTimeMillis = System.currentTimeMillis();
        if (Math.abs(currentTimeMillis - mLastClickTime) < 500) {
            return true;
        }
        mLastClickTime = currentTimeMillis;
        return false;
    }
}
