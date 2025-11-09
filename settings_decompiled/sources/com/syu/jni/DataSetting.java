package com.syu.jni;

import android.os.SystemProperties;
import android.util.Log;
/* loaded from: classes2.dex */
public class DataSetting {
    public static String ANDROID_COMPANY = "APP";

    static {
        int i = SystemProperties.getInt("ro.build.fytmanufacturer", 0);
        int i2 = SystemProperties.getInt("ro.fyt.uiid", 0);
        Log.i("log", " == id  " + i);
        if (i == 63 && i2 == 7) {
            ANDROID_COMPANY = "COYOTE APP";
        }
    }
}
