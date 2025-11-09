package com.abupdate.iot_libs.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
/* loaded from: classes.dex */
public final class ConvertUtils {
    public static String dateToFormateTime(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Long.valueOf(j));
    }
}
