package com.android.settingslib.notification;

import android.content.Context;
import com.android.internal.logging.MetricsLogger;
/* loaded from: classes2.dex */
public class ZenModeDialogMetricsLogger {
    private final Context mContext;

    public ZenModeDialogMetricsLogger(Context context) {
        this.mContext = context;
    }

    public void logOnEnableZenModeForever() {
        MetricsLogger.action(this.mContext, 1259);
    }

    public void logOnEnableZenModeUntilAlarm() {
        MetricsLogger.action(this.mContext, 1261);
    }

    public void logOnEnableZenModeUntilCountdown() {
        MetricsLogger.action(this.mContext, 1260);
    }

    public void logOnConditionSelected() {
        MetricsLogger.action(this.mContext, 164);
    }

    public void logOnClickTimeButton(boolean z) {
        MetricsLogger.action(this.mContext, 163, z);
    }
}
