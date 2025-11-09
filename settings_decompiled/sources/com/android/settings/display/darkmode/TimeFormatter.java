package com.android.settings.display.darkmode;

import android.content.Context;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimeZone;
/* loaded from: classes.dex */
public class TimeFormatter {
    private final Context mContext;
    private final DateFormat mFormatter;

    public TimeFormatter(Context context) {
        this.mContext = context;
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        this.mFormatter = timeFormat;
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public String of(LocalTime localTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(this.mFormatter.getTimeZone());
        calendar.set(11, localTime.getHour());
        calendar.set(12, localTime.getMinute());
        calendar.set(13, 0);
        calendar.set(14, 0);
        return this.mFormatter.format(calendar.getTime());
    }

    public boolean is24HourFormat() {
        return android.text.format.DateFormat.is24HourFormat(this.mContext);
    }
}
