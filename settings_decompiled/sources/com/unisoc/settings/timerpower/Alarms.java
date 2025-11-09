package com.unisoc.settings.timerpower;

import android.app.AlarmManager;
import android.app.AlarmManagerWrapper;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import com.unisoc.settings.timerpower.Alarm;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/* loaded from: classes2.dex */
public class Alarms {
    private static File ALARM_FLAG_FILE = new File("/mnt/vendor/poweron_timeinmillis");
    public static boolean FIRST_KLAXON = false;

    public static Cursor getAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
    }

    private static Cursor getFilteredAlarmsCursor(ContentResolver contentResolver) {
        return contentResolver.query(Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS, "enabled=1", null, null);
    }

    private static ContentValues createContentValues(Alarm alarm) {
        ContentValues contentValues = new ContentValues(8);
        contentValues.put("enabled", Integer.valueOf(alarm.enabled ? 1 : 0));
        contentValues.put("hour", Integer.valueOf(alarm.hour));
        contentValues.put("minutes", Integer.valueOf(alarm.minutes));
        contentValues.put("alarmtime", Long.valueOf(alarm.time));
        contentValues.put("daysofweek", Integer.valueOf(alarm.daysOfWeek.getCoded()));
        contentValues.put("vibrate", Boolean.valueOf(alarm.vibrate));
        contentValues.put("message", alarm.label);
        Uri uri = alarm.alert;
        contentValues.put("alert", uri == null ? "silent" : uri.toString());
        return contentValues;
    }

    public static synchronized Alarm getAlarm(Context context, ContentResolver contentResolver, int i) {
        synchronized (Alarms.class) {
            if (i < 1) {
                return null;
            }
            Log.v("Alarms :::: Enter getAlarm,alarmId = " + i);
            Cursor query = contentResolver.query(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, (long) i), Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
            if (query != null) {
                r2 = query.moveToFirst() ? new Alarm(context, query) : null;
                query.close();
            }
            return r2;
        }
    }

    public static long setAlarm(Context context, Alarm alarm) {
        if (alarm == null) {
            return 0L;
        }
        context.getContentResolver().update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), createContentValues(alarm), null, null);
        long calculateAlarm = calculateAlarm(alarm);
        setNextAlert(context);
        Log.v("Alarms :::: setAlarm,alarmId = " + alarm.id + ",enabled = " + alarm.enabled + ",timeInMillis = " + calculateAlarm);
        return calculateAlarm;
    }

    public static void enableAlarm(Context context, int i, boolean z) {
        Log.v("Alarms :::: Enter enableAlarm enabled = " + z);
        enableAlarmInternal(context, i, z);
        setNextAlert(context);
    }

    private static void enableAlarmInternal(Context context, int i, boolean z) {
        Log.v("Alarms :::: Enter enableAlarmInternal");
        enableAlarmInternal(context, getAlarm(context, context.getContentResolver(), i), z);
    }

    private static void enableAlarmInternal(Context context, Alarm alarm, boolean z) {
        if (alarm == null) {
            return;
        }
        Log.v("Alarms :::: Enter enableAlarmInternal enabled = " + z);
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues(2);
        contentValues.put("enabled", Integer.valueOf(z ? 1 : 0));
        if (z) {
            long calculateAlarm = calculateAlarm(alarm);
            alarm.time = calculateAlarm;
            contentValues.put("alarmtime", Long.valueOf(calculateAlarm));
        }
        contentResolver.update(ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, alarm.id), contentValues, null, null);
    }

    public static List<Alarm> calculateNextAlerts(Context context) {
        ArrayList arrayList = new ArrayList();
        long currentTimeMillis = System.currentTimeMillis();
        Cursor filteredAlarmsCursor = getFilteredAlarmsCursor(context.getContentResolver());
        if (filteredAlarmsCursor != null) {
            if (filteredAlarmsCursor.moveToFirst()) {
                do {
                    Alarm alarm = new Alarm(context, filteredAlarmsCursor);
                    long j = alarm.time;
                    if (j == 0) {
                        alarm.time = calculateAlarm(alarm);
                    } else if (j < currentTimeMillis) {
                        Log.v("calculateNextAlerts :::: Disabling expired alarm set for " + Log.formatTime(alarm.time));
                        enableAlarmInternal(context, alarm, alarm.daysOfWeek.isRepeatSet());
                    }
                    if (alarm.time < Long.MAX_VALUE) {
                        arrayList.add(alarm);
                        Log.v("calculateNextAlerts :::: set minTime = 9223372036854775807");
                    }
                } while (filteredAlarmsCursor.moveToNext());
                filteredAlarmsCursor.close();
            } else {
                filteredAlarmsCursor.close();
            }
        }
        return arrayList;
    }

    public static void setNextAlert(Context context) {
        Log.v("Alarms :::: Enter setNextAlert");
        List<Alarm> calculateNextAlerts = calculateNextAlerts(context);
        disableAlert(context);
        for (Alarm alarm : calculateNextAlerts) {
            if (alarm != null) {
                enableAlert(context, alarm, alarm.time);
            }
        }
    }

    private static void enableAlert(Context context, Alarm alarm, long j) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        Log.v("Alarms :::: Enter enableAlert alarmId = " + alarm.id + ",atTime = " + j + ",label = " + alarm.label);
        String str = alarm.label;
        if (str != null && str.equals("on")) {
            alarm_flag_setup(j);
            Intent intent = new Intent("com.android.settings.timerpower.ALARM_ALERT");
            intent.setFlags(16777216);
            Parcel obtain = Parcel.obtain();
            alarm.writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
            intent.putExtra("intent.extra.alarm_raw", obtain.marshall());
            AlarmManagerWrapper.setPowerOnWakeup(alarmManager, j, PendingIntent.getBroadcast(context, 0, intent, 67108864));
            obtain.recycle();
            return;
        }
        Intent intent2 = new Intent("com.android.settings.timerpower.SHUTDOWN");
        intent2.setFlags(16777216);
        intent2.addFlags(268435456);
        Parcel obtain2 = Parcel.obtain();
        alarm.writeToParcel(obtain2, 0);
        obtain2.setDataPosition(0);
        Log.v("Alarms :::: TIMER_POWER_SHUTDOWN_ACTION out.length = " + obtain2.marshall().length);
        intent2.putExtra("intent.extra.alarm_raw", obtain2.marshall());
        alarmManager.setExact(0, j, PendingIntent.getBroadcast(context, 0, intent2, 335544320));
        obtain2.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void disableAlert(Context context) {
        Log.v("Alarms :::: Enter disableAlert");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        Intent intent = new Intent("com.android.settings.timerpower.ALARM_ALERT");
        intent.setFlags(16777216);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 67108864);
        if (broadcast == null) {
            Log.v("Alarms ::: sender is null!");
        } else {
            AlarmManagerWrapper.cancelAlarm(alarmManager, broadcast);
        }
        Intent intent2 = new Intent("com.android.settings.timerpower.SHUTDOWN");
        intent2.setFlags(16777216);
        PendingIntent broadcast2 = PendingIntent.getBroadcast(context, 0, intent2, 67108864);
        if (broadcast2 == null) {
            Log.v("Alarms ::: sender1 is null!");
        } else {
            AlarmManagerWrapper.cancelAlarm(alarmManager, broadcast2);
        }
        alarm_flag_cancel();
    }

    private static long calculateAlarm(Alarm alarm) {
        return calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Calendar calculateAlarm(int i, int i2, Alarm.DaysOfWeek daysOfWeek) {
        Log.v("Alarms :::: Enter calculateAlarm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i3 = calendar.get(11);
        int i4 = calendar.get(12);
        int i5 = calendar.get(13);
        if (i < i3 || ((i == i3 && i2 < i4) || ((i == i3 && i2 == i4 && 30 < i5 && FIRST_KLAXON && SystemProperties.get("ro.bootmode", "unknown").equals("alarm")) || (i == i3 && i2 == i4 && !FIRST_KLAXON)))) {
            calendar.add(6, 1);
        }
        calendar.set(11, i);
        calendar.set(12, i2);
        calendar.set(13, 0);
        calendar.set(14, 0);
        int nextAlarm = daysOfWeek.getNextAlarm(calendar);
        if (nextAlarm > 0) {
            calendar.add(7, nextAlarm);
        }
        return calendar;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String formatTime(Context context, int i, int i2, Alarm.DaysOfWeek daysOfWeek) {
        return formatTime(context, calculateAlarm(i, i2, daysOfWeek));
    }

    static String formatTime(Context context, Calendar calendar) {
        return calendar == null ? "" : (String) DateFormat.format(get24HourMode(context) ? "kk:mm" : "h:mm aa", calendar);
    }

    static boolean get24HourMode(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    public static boolean isSametimeAlarm(ContentResolver contentResolver, int i, int i2, int i3) {
        Uri uri = Alarm.Columns.CONTENT_URI;
        String[] strArr = Alarm.Columns.ALARM_QUERY_COLUMNS;
        Cursor query = contentResolver.query(uri, strArr, "hour = " + new Integer(i).toString() + " AND minutes = " + new Integer(i2).toString() + " AND _id != " + i3, null, "hour, minutes ASC");
        if (query == null) {
            return false;
        }
        boolean z = query.getCount() > 0;
        query.close();
        return z;
    }

    public static void resetAlarmStates(Context context) {
        disableAlert(context);
    }

    private static void alarm_flag_setup(long j) {
        FileOutputStream fileOutputStream;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 0, 1, 0, 0, 0);
        Calendar.getInstance().setTimeInMillis(j);
        long offset = ((j - calendar.getTimeZone().getOffset(j)) - calendar.getTimeInMillis()) / 1000;
        Log.v("write " + String.valueOf(offset) + " to" + ALARM_FLAG_FILE);
        if (ALARM_FLAG_FILE.exists()) {
            Log.v(ALARM_FLAG_FILE + " already exist, delete it");
            try {
                boolean delete = ALARM_FLAG_FILE.delete();
                Log.v(ALARM_FLAG_FILE + " delete before write,result = " + delete);
            } catch (Exception unused) {
                Log.v(ALARM_FLAG_FILE + " delete before write failed");
            }
        }
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                Runtime.getRuntime().exec("chmod 664 /mnt/vendor/poweron_timeinmillis");
                fileOutputStream = new FileOutputStream(ALARM_FLAG_FILE);
            } catch (Throwable th) {
                th = th;
            }
        } catch (Exception e) {
            e = e;
        }
        try {
            fileOutputStream.write(String.valueOf(offset).getBytes(Charset.defaultCharset()));
            fileOutputStream.write("\n".getBytes(Charset.defaultCharset()));
            fileOutputStream.write(String.valueOf(j / 1000).getBytes(Charset.defaultCharset()));
            fileOutputStream.write("\n".getBytes(Charset.defaultCharset()));
            fileOutputStream.flush();
            fileOutputStream.getFD().sync();
            fileOutputStream.close();
            Log.v(ALARM_FLAG_FILE + " write done");
        } catch (Exception e2) {
            e = e2;
            fileOutputStream2 = fileOutputStream;
            Log.v(ALARM_FLAG_FILE + " write error : " + e.getMessage());
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e3) {
                    Log.v("FileOutputStream close error : " + e3.getMessage());
                }
            }
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e4) {
                    Log.v("FileOutputStream close error : " + e4.getMessage());
                }
            }
            throw th;
        }
    }

    private static void alarm_flag_cancel() {
        if (ALARM_FLAG_FILE.exists()) {
            Log.v(ALARM_FLAG_FILE + " exist");
            try {
                boolean delete = ALARM_FLAG_FILE.delete();
                Log.v(ALARM_FLAG_FILE + " delete success = " + delete);
                return;
            } catch (Exception unused) {
                Log.v(ALARM_FLAG_FILE + " delete failed");
                return;
            }
        }
        Log.v(ALARM_FLAG_FILE + " already delete");
    }
}
