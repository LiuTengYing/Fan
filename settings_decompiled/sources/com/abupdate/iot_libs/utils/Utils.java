package com.abupdate.iot_libs.utils;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
/* loaded from: classes.dex */
public class Utils {
    public static String format(String str, Object... objArr) {
        return String.format(Locale.US, str, objArr);
    }

    public static ThreadFactory threadFactory(final String str, final boolean z) {
        return new ThreadFactory() { // from class: com.abupdate.iot_libs.utils.Utils.1
            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, str);
                thread.setDaemon(z);
                return thread;
            }
        };
    }
}
