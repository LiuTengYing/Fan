package com.syu.util;

import android.os.Handler;
import android.os.HandlerThread;
import java.util.HashMap;
/* loaded from: classes2.dex */
public class ThreadMap {
    public static HashMap<String, HandlerThread> mHashMapThread = new HashMap<>();
    public static HashMap<String, Handler> mHashMapHandler = new HashMap<>();

    public static void startThread(String str, Runnable runnable, boolean z, int i) {
        if (runnable != null) {
            if (mHashMapThread.get(str) == null) {
                HandlerThread handlerThread = new HandlerThread(str);
                handlerThread.setPriority(i);
                handlerThread.start();
                mHashMapThread.put(str, handlerThread);
                mHashMapHandler.put(str, new Handler(handlerThread.getLooper()));
            }
            Handler handler = mHashMapHandler.get(str);
            if (handler != null) {
                if (z) {
                    handler.removeCallbacks(runnable);
                }
                handler.post(runnable);
            }
        }
    }
}
