package com.unisoc.settings.timerpower;

import android.content.Context;
import android.os.PowerManager;
/* loaded from: classes2.dex */
class AlarmAlertWakeLock {
    private static PowerManager.WakeLock mCpuWakeLock;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void acquireCpuWakeLock(Context context) {
        if (mCpuWakeLock != null) {
            return;
        }
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(805306369, "TimerPower");
        mCpuWakeLock = newWakeLock;
        newWakeLock.acquire();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void releaseCpuLock() {
        PowerManager.WakeLock wakeLock = mCpuWakeLock;
        if (wakeLock != null) {
            wakeLock.release();
            mCpuWakeLock = null;
        }
    }
}
