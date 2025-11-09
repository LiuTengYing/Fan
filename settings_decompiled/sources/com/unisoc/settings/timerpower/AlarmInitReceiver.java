package com.unisoc.settings.timerpower;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.unisoc.settings.timerpower.Alarm;
import java.util.Calendar;
/* loaded from: classes2.dex */
public class AlarmInitReceiver extends BroadcastReceiver {
    private static boolean isBoot = false;
    private static HandlerThread mHanderThread;
    private static MyHandler mThreadHander;
    private Context mContext;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Log.v("AlarmInitReceiver ---- intent = " + intent);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) || "android.intent.action.LOCKED_BOOT_COMPLETED".equals(intent.getAction())) {
            isBoot = true;
        }
        if (mHanderThread == null) {
            Log.v("onReceive mHanderThread is null.");
            HandlerThread handlerThread = new HandlerThread("HandlerThreadAlarmInitReceiver");
            mHanderThread = handlerThread;
            handlerThread.start();
            mThreadHander = new MyHandler(mHanderThread.getLooper());
        }
        mThreadHander.obtainMessage().sendToTarget();
    }

    /* loaded from: classes2.dex */
    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.v("AlarmInitReceiver MyHandler handleMessage isBoot = " + AlarmInitReceiver.isBoot);
            if (AlarmInitReceiver.isBoot) {
                AlarmInitReceiver alarmInitReceiver = AlarmInitReceiver.this;
                alarmInitReceiver.updateAlarmEnabled(alarmInitReceiver.mContext);
            } else {
                AlarmInitReceiver alarmInitReceiver2 = AlarmInitReceiver.this;
                alarmInitReceiver2.updateAlarmEnabled(alarmInitReceiver2.mContext);
                AlarmInitReceiver alarmInitReceiver3 = AlarmInitReceiver.this;
                alarmInitReceiver3.updateAlarmTime(alarmInitReceiver3.mContext);
            }
            Alarms.disableAlert(AlarmInitReceiver.this.mContext);
            Alarms.setNextAlert(AlarmInitReceiver.this.mContext);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmTime(Context context) {
        Cursor alarmsCursor = Alarms.getAlarmsCursor(context.getContentResolver());
        if (alarmsCursor != null) {
            if (alarmsCursor.moveToFirst()) {
                do {
                    Alarm alarm = new Alarm(context, alarmsCursor);
                    ContentValues contentValues = new ContentValues(1);
                    long timeInMillis = Alarms.calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis();
                    alarm.time = timeInMillis;
                    contentValues.put("alarmtime", Long.valueOf(timeInMillis));
                    context.getContentResolver().update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), contentValues, null, null);
                } while (alarmsCursor.moveToNext());
                alarmsCursor.close();
            } else {
                alarmsCursor.close();
            }
        }
        isBoot = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmEnabled(Context context) {
        Cursor alarmsCursor = Alarms.getAlarmsCursor(context.getContentResolver());
        if (alarmsCursor != null) {
            if (alarmsCursor.moveToFirst()) {
                do {
                    Alarm alarm = new Alarm(context, alarmsCursor);
                    ContentValues contentValues = new ContentValues(1);
                    int i = alarmsCursor.getInt(alarmsCursor.getColumnIndex("daysofweek"));
                    Log.v("repete = " + i);
                    if (Calendar.getInstance().after(Alarms.calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek)) && i == 0) {
                        Log.v("no use alarm");
                        alarm.enabled = false;
                    }
                    contentValues.put("enabled", Boolean.valueOf(alarm.enabled));
                    context.getContentResolver().update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), contentValues, null, null);
                } while (alarmsCursor.moveToNext());
                alarmsCursor.close();
            } else {
                alarmsCursor.close();
            }
        }
        isBoot = false;
    }
}
