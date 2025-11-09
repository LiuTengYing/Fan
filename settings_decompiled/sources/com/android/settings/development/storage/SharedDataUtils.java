package com.android.settings.development.storage;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import java.util.Locale;
/* loaded from: classes.dex */
class SharedDataUtils {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z");
    private static final Calendar CALENDAR = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatTime(long j) {
        Calendar calendar = CALENDAR;
        calendar.setTimeInMillis(j);
        return FORMATTER.format(calendar.getTime());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatSize(long j) {
        return String.format("%.2f MB", Double.valueOf(j / 1048576.0d));
    }
}
