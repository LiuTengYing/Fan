package com.abupdate.iot_libs.engine.trigger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.abupdate.iot_libs.utils.ConvertUtils;
import com.abupdate.trace.Trace;
/* loaded from: classes.dex */
public class AlarmManagerr {
    public static void setRepeatAlarm(Context context, long j, long j2, int i, String str) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        Intent intent = new Intent();
        intent.setAction(str);
        intent.putExtra("id", i);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, i, intent, 201326592);
        if (alarmManager == null) {
            Trace.d("AlarmManagerr", "setRepeatAlarm() alarm manager is null");
            return;
        }
        Trace.d("AlarmManagerr", "闹钟时间:" + ConvertUtils.dateToFormateTime(j));
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(0, j, broadcast);
            return;
        }
        Intent intent2 = new Intent("android.settings.REQUEST_SCHEDULE_EXACT_ALARM");
        intent2.addFlags(268435456);
        context.startActivity(intent2);
    }

    public static void startCycleCheck(Context context) {
        setRepeatAlarm(context, System.currentTimeMillis() + CheckPeriod.getCheckPeriod(), CheckPeriod.getCheckPeriod(), 888, "ACTION_COM_ABUPDATE_ALARM_CHECK_VERSION");
    }
}
