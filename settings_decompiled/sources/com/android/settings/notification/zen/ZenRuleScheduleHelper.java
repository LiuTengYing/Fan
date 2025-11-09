package com.android.settings.notification.zen;

import android.content.Context;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
/* loaded from: classes.dex */
public class ZenRuleScheduleHelper {
    private SimpleDateFormat mDayFormat;

    public ZenRuleScheduleHelper() {
        this.mDayFormat = new SimpleDateFormat("EEE");
    }

    @VisibleForTesting
    public ZenRuleScheduleHelper(Locale locale) {
        this.mDayFormat = new SimpleDateFormat("EEE", locale);
    }

    public String getDaysDescription(Context context, ZenModeConfig.ScheduleInfo scheduleInfo) {
        int[] daysOfWeekForLocale;
        int[] iArr = scheduleInfo.days;
        if (iArr == null || iArr.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        for (int i : ZenModeScheduleDaysSelection.getDaysOfWeekForLocale(calendar)) {
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    break;
                } else if (i == iArr[i2]) {
                    calendar.set(7, i);
                    if (sb.length() > 0) {
                        sb.append(context.getString(R$string.summary_divider_text));
                    }
                    sb.append(this.mDayFormat.format(calendar.getTime()));
                } else {
                    i2++;
                }
            }
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        return null;
    }

    public String getShortDaysSummary(Context context, ZenModeConfig.ScheduleInfo scheduleInfo) {
        int[] iArr = scheduleInfo.days;
        if (iArr == null || iArr.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int[] daysOfWeekForLocale = ZenModeScheduleDaysSelection.getDaysOfWeekForLocale(calendar);
        int i = Integer.MIN_VALUE;
        int i2 = Integer.MIN_VALUE;
        int i3 = 0;
        while (i3 < daysOfWeekForLocale.length) {
            int i4 = daysOfWeekForLocale[i3];
            int i5 = i + 1;
            boolean z = i3 == i5;
            int i6 = 0;
            while (true) {
                if (i6 >= iArr.length) {
                    break;
                } else if (i4 == iArr[i6]) {
                    if (i3 == i5) {
                        z = false;
                    } else {
                        i2 = i3;
                    }
                    if (i3 == daysOfWeekForLocale.length - 1) {
                        i = i3;
                        z = true;
                    } else {
                        i = i3;
                    }
                } else {
                    i6++;
                }
            }
            if (z) {
                if (sb.length() > 0) {
                    sb.append(context.getString(R$string.summary_divider_text));
                }
                if (i2 == i) {
                    calendar.set(7, daysOfWeekForLocale[i2]);
                    sb.append(this.mDayFormat.format(calendar.getTime()));
                } else {
                    calendar.set(7, daysOfWeekForLocale[i2]);
                    calendar2.set(7, daysOfWeekForLocale[i]);
                    sb.append(context.getString(R$string.summary_range_symbol_combination, this.mDayFormat.format(calendar.getTime()), this.mDayFormat.format(calendar2.getTime())));
                    i3++;
                }
            }
            i3++;
        }
        if (sb.length() > 0) {
            return sb.toString();
        }
        return null;
    }

    private String timeString(Context context, int i, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, i);
        calendar.set(12, i2);
        return DateFormat.getTimeFormat(context).format(calendar.getTime());
    }

    public String getDaysAndTimeSummary(Context context, ZenModeConfig.ScheduleInfo scheduleInfo) {
        StringBuilder sb = new StringBuilder();
        String shortDaysSummary = getShortDaysSummary(context, scheduleInfo);
        if (shortDaysSummary == null) {
            return null;
        }
        sb.append(shortDaysSummary);
        sb.append(context.getString(R$string.summary_divider_text));
        sb.append(context.getString(R$string.summary_range_symbol_combination, timeString(context, scheduleInfo.startHour, scheduleInfo.startMinute), timeString(context, scheduleInfo.endHour, scheduleInfo.endMinute)));
        return sb.toString();
    }
}
