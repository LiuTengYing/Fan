package com.unisoc.settings.timerpower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
/* loaded from: classes2.dex */
public class AlarmReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.v("AlarmReceiver :::: intent = " + intent);
        if ("com.android.settings.timerpower.SHUTDOWN".equals(intent.getAction())) {
            Alarm alarm = null;
            byte[] byteArrayExtra = intent.getByteArrayExtra("intent.extra.alarm_raw");
            Log.v("AlarmReceiver :::: data = " + byteArrayExtra);
            if (byteArrayExtra != null) {
                Parcel obtain = Parcel.obtain();
                obtain.unmarshall(byteArrayExtra, 0, byteArrayExtra.length);
                obtain.setDataPosition(0);
                Alarm createFromParcel = Alarm.CREATOR.createFromParcel(obtain);
                Log.v("AlarmReceiver :::: data.length = " + byteArrayExtra.length + ", alarm: " + createFromParcel);
                obtain.recycle();
                alarm = createFromParcel;
            }
            if (alarm == null) {
                Log.wtf("Failed to parse the alarm from the intent");
                Alarms.setNextAlert(context);
                return;
            }
            String str = alarm.label;
            if (str != null && str.equals("on")) {
                Log.v("alarm.label = on");
                return;
            }
            if (!alarm.daysOfWeek.isRepeatSet()) {
                Alarms.enableAlarm(context, alarm.id, false);
            } else {
                Alarms.setNextAlert(context);
            }
            long currentTimeMillis = System.currentTimeMillis();
            Log.v("Recevied alarm set for " + Log.formatTime(alarm.time));
            if (currentTimeMillis > alarm.time + 60000) {
                Log.v("Ignoring stale timer power shutdown");
                return;
            }
            AlarmAlertWakeLock.acquireCpuWakeLock(context);
            Log.v("AlarmReceiver startService");
            Intent intent2 = new Intent();
            intent2.setClass(context, AlarmKlaxon.class);
            intent2.putExtra("intent.extra.alarm", alarm);
            context.startService(intent2);
            Alarms.setNextAlert(context);
        } else if ("com.android.settings.timerpower.ALARM_ALERT".equals(intent.getAction())) {
            Alarms.calculateNextAlerts(context);
        }
    }
}
