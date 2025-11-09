package com.abupdate.iot_libs.engine.trigger;

import com.abupdate.iot_libs.OtaAgentPolicy;
import com.abupdate.iot_libs.utils.ConvertUtils;
import com.abupdate.iot_libs.utils.SPFTool;
import com.abupdate.trace.Trace;
/* loaded from: classes.dex */
public class CheckPeriod {
    public static int DEFAULT_CHECK_VERSION_PERIOD = 90000000;

    public static boolean isArrived() {
        boolean z = System.currentTimeMillis() - getLastCheckTime() > getCheckPeriod();
        Trace.i("CheckPeriod", "check version alarm isArrived :" + z);
        return z;
    }

    public static void setCheckPeriod(long j) {
        if (getCheckPeriod() != j) {
            SPFTool.putLong("key_period_interval_time", j);
            Trace.i("CheckPeriod", "setCheckPeriod() %s", Long.valueOf(j));
        }
    }

    public static void resetPeriod() {
        long checkPeriod = getCheckPeriod();
        if (checkPeriod < 3600000 || checkPeriod > 608400000) {
            Trace.d("CheckPeriod", "resetPeriod() cycle: %s is invalid", Long.valueOf(checkPeriod));
            checkPeriod = DEFAULT_CHECK_VERSION_PERIOD;
        }
        Trace.d("CheckPeriod", "resetPeriod() cycle: %s", Long.valueOf(checkPeriod));
        resetPeriod(checkPeriod);
    }

    public static void resetPeriod(long j) {
        setCheckPeriod(j);
        setLastCheckTime(System.currentTimeMillis());
        AlarmManagerr.startCycleCheck(OtaAgentPolicy.sCx);
    }

    public static long getCheckPeriod() {
        return SPFTool.getLong("key_period_interval_time", DEFAULT_CHECK_VERSION_PERIOD);
    }

    public static long getLastCheckTime() {
        return SPFTool.getLong("key_previous_time", -1L);
    }

    public static void setLastCheckTime(long j) {
        Trace.d("CheckPeriod", "setLastCheckTime: " + ConvertUtils.dateToFormateTime(j));
        SPFTool.putLong("key_previous_time", j);
    }
}
