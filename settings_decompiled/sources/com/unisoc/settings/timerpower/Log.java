package com.unisoc.settings.timerpower;

import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes2.dex */
class Log {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void v(String str) {
        android.util.Log.v("TimerPower", str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void wtf(String str) {
        android.util.Log.wtf("TimerPower", str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatTime(long j) {
        return new SimpleDateFormat("HH:mm:ss.SSS aaa").format(new Date(j));
    }
}
