package com.unisoc.settings.timerpower;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.MultiSelectListPreference;
import com.unisoc.settings.timerpower.Alarm;
import java.text.DateFormatSymbols;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes2.dex */
public class AlarmRepeatPreference extends MultiSelectListPreference {
    private Alarm.DaysOfWeek mDaysOfWeek;
    private String[] mEntryValues;
    private String[] mWeekdays;

    public AlarmRepeatPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDaysOfWeek = new Alarm.DaysOfWeek(0);
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        this.mWeekdays = weekdays;
        String[] strArr = {weekdays[2], weekdays[3], weekdays[4], weekdays[5], weekdays[6], weekdays[7], weekdays[1]};
        this.mEntryValues = strArr;
        setEntries(strArr);
        setEntryValues(this.mEntryValues);
    }

    public void setDaysOfWeek(Alarm.DaysOfWeek daysOfWeek) {
        this.mDaysOfWeek.set(daysOfWeek);
        setSummary(daysOfWeek.toString(getContext(), true));
        setSelectedItems(this.mDaysOfWeek.getBooleanArray());
    }

    public Alarm.DaysOfWeek getDaysOfWeek(Set<String> set) {
        Alarm.DaysOfWeek daysOfWeek = new Alarm.DaysOfWeek(0);
        if (set == null) {
            return daysOfWeek;
        }
        int length = this.mEntryValues.length;
        boolean[] zArr = new boolean[length];
        for (int i = 0; i < length; i++) {
            boolean contains = set.contains(this.mEntryValues[i].toString());
            zArr[i] = contains;
            daysOfWeek.set(i, contains);
        }
        return daysOfWeek;
    }

    private void setSelectedItems(boolean[] zArr) {
        HashSet hashSet = new HashSet();
        for (int i = 0; i < zArr.length; i++) {
            if (zArr[i]) {
                hashSet.add(this.mEntryValues[i]);
            }
        }
        setValues(hashSet);
    }
}
