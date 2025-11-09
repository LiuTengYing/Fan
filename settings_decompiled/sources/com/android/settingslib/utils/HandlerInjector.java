package com.android.settingslib.utils;

import android.os.Handler;
/* loaded from: classes2.dex */
public class HandlerInjector {
    protected final Handler mHandler;

    public HandlerInjector(Handler handler) {
        this.mHandler = handler;
    }

    public void postDelayed(Runnable runnable, long j) {
        this.mHandler.postDelayed(runnable, j);
    }

    public void removeCallbacks(Runnable runnable) {
        this.mHandler.removeCallbacks(runnable);
    }
}
