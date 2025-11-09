package com.unisoc.settings.timerpower;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$array;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.unisoc.settings.timerpower.Alarm;
import com.unisoc.settings.utils.ToastManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class TimerPower extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, TimePickerDialog.OnTimeSetListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new TimerPowerSearchIndexProvider();
    private Context mContext;
    private int mCurrentTimePickerDialogId = 0;
    private Alarm mPowerOffAlarm;
    private AlarmRepeatPreference mPowerOffRepeatPref;
    private SwitchPreference mPowerOffSwitchPref;
    private Preference mPowerOffTimePref;
    private Alarm mPowerOnAlarm;
    private AlarmRepeatPreference mPowerOnRepeatPref;
    private SwitchPreference mPowerOnSwitchPref;
    private Preference mPowerOnTimePref;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 608;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50954;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        addPreferencesFromResource(R$xml.timer_power);
        initAllPreferences();
    }

    private void initAllPreferences() {
        this.mPowerOnTimePref = findPreference("time_power_on");
        this.mPowerOffTimePref = findPreference("time_power_off");
        this.mPowerOnRepeatPref = (AlarmRepeatPreference) findPreference("repeat_power_on");
        this.mPowerOffRepeatPref = (AlarmRepeatPreference) findPreference("repeat_power_off");
        this.mPowerOnSwitchPref = (SwitchPreference) findPreference("switch_power_on");
        this.mPowerOffSwitchPref = (SwitchPreference) findPreference("switch_power_off");
        this.mPowerOnRepeatPref.setOnPreferenceChangeListener(this);
        this.mPowerOffRepeatPref.setOnPreferenceChangeListener(this);
        this.mPowerOnSwitchPref.setOnPreferenceChangeListener(this);
        this.mPowerOffSwitchPref.setOnPreferenceChangeListener(this);
        Cursor alarmsCursor = Alarms.getAlarmsCursor(this.mContext.getContentResolver());
        if (alarmsCursor != null) {
            if (alarmsCursor.moveToFirst()) {
                do {
                    Alarm alarm = new Alarm(this.mContext, alarmsCursor);
                    if (!alarm.label.equals("") && alarm.label.equals("on")) {
                        this.mPowerOnAlarm = alarm;
                        this.mPowerOnSwitchPref.setChecked(alarm.enabled);
                        this.mPowerOnTimePref.setEnabled(alarm.enabled);
                        this.mPowerOnTimePref.setSummary(Alarms.formatTime(this.mContext, alarm.hour, alarm.minutes, alarm.daysOfWeek));
                        this.mPowerOnRepeatPref.setEnabled(alarm.enabled);
                        this.mPowerOnRepeatPref.setDaysOfWeek(alarm.daysOfWeek);
                    } else {
                        this.mPowerOffAlarm = alarm;
                        this.mPowerOffSwitchPref.setChecked(alarm.enabled);
                        this.mPowerOffTimePref.setEnabled(alarm.enabled);
                        this.mPowerOffTimePref.setSummary(Alarms.formatTime(this.mContext, alarm.hour, alarm.minutes, alarm.daysOfWeek));
                        this.mPowerOffRepeatPref.setEnabled(alarm.enabled);
                        this.mPowerOffRepeatPref.setDaysOfWeek(alarm.daysOfWeek);
                    }
                } while (alarmsCursor.moveToNext());
                alarmsCursor.close();
            }
            alarmsCursor.close();
        }
    }

    public void showTimePicker(int i) {
        removeDialog(i);
        showDialog(i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        this.mCurrentTimePickerDialogId = i;
        if (i == 1) {
            Context context = this.mContext;
            Alarm alarm = this.mPowerOnAlarm;
            return new TimePickerDialog(context, this, alarm.hour, alarm.minutes, DateFormat.is24HourFormat(context));
        } else if (i == 2) {
            Context context2 = this.mContext;
            Alarm alarm2 = this.mPowerOffAlarm;
            return new TimePickerDialog(context2, this, alarm2.hour, alarm2.minutes, DateFormat.is24HourFormat(context2));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override // android.app.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        Alarm alarm;
        Preference preference;
        ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null) {
            return;
        }
        int i3 = 1;
        if (this.mCurrentTimePickerDialogId == 1) {
            alarm = this.mPowerOnAlarm;
            preference = this.mPowerOnTimePref;
        } else {
            alarm = this.mPowerOffAlarm;
            preference = this.mPowerOffTimePref;
        }
        boolean isSametimeAlarm = Alarms.isSametimeAlarm(contentResolver, i, i2, alarm.id);
        boolean hasDuplicateAlarmDate = hasDuplicateAlarmDate(alarm);
        if (isAlarmStateChecked(this.mContext, alarm.id) && isSametimeAlarm && hasDuplicateAlarmDate) {
            showSameAlarmToast(i, i2, this.mCurrentTimePickerDialogId);
            Log.v("onTimeSet : same time can't set, please set again");
            return;
        }
        i3 = (hasDuplicateAlarmDate && isSametimeAlarm) ? 0 : 0;
        Log.v("onTimeSet : hasDuplicateDate = " + hasDuplicateAlarmDate + ",hasSametimeAlarm = " + isSametimeAlarm + ",sameTimeFlag = " + i3);
        Settings.System.putInt(contentResolver, "sameTime", i3);
        alarm.hour = i;
        alarm.minutes = i2;
        preference.setSummary(Alarms.formatTime(this.mContext, i, i2, alarm.daysOfWeek));
        saveAlarm(alarm);
        showAlarmSetToast(alarm);
    }

    private int getDaysCodeFromDB(String str) {
        ContentResolver contentResolver = getContentResolver();
        if (contentResolver != null) {
            Uri uri = Alarm.Columns.CONTENT_URI;
            Cursor query = contentResolver.query(uri, null, "message!='" + str + "'", null, null);
            if (query != null) {
                r6 = query.moveToFirst() ? query.getInt(query.getColumnIndex("daysofweek")) : -1;
                query.close();
            }
        }
        return r6;
    }

    private boolean hasDuplicateAlarmDate(Alarm alarm) {
        int daysCodeFromDB = getDaysCodeFromDB(alarm.label);
        Alarm.DaysOfWeek daysOfWeek = alarm.daysOfWeek;
        if (daysOfWeek != null) {
            int coded = daysOfWeek.getCoded();
            boolean hasDuplicateDate = (daysCodeFromDB == 0 && coded == 0) ? true : alarm.daysOfWeek.hasDuplicateDate(coded, daysCodeFromDB);
            Log.v("hasDuplicateAlarm days = " + coded + ",code = " + daysCodeFromDB + ",hasDuplicateDate = " + hasDuplicateDate);
            return hasDuplicateDate;
        }
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        key.hashCode();
        if (key.equals("time_power_off")) {
            showTimePicker(2);
        } else if (key.equals("time_power_on")) {
            showTimePicker(1);
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        key.hashCode();
        char c = 65535;
        switch (key.hashCode()) {
            case -1229003772:
                if (key.equals("switch_power_on")) {
                    c = 0;
                    break;
                }
                break;
            case -685788803:
                if (key.equals("repeat_power_on")) {
                    c = 1;
                    break;
                }
                break;
            case 215383441:
                if (key.equals("repeat_power_off")) {
                    c = 2;
                    break;
                }
                break;
            case 555588586:
                if (key.equals("switch_power_off")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                boolean booleanValue = ((Boolean) obj).booleanValue();
                if (processAlarmSwitchClick(booleanValue, this.mPowerOnAlarm)) {
                    this.mPowerOnTimePref.setEnabled(booleanValue);
                    this.mPowerOnRepeatPref.setEnabled(booleanValue);
                    break;
                } else {
                    return false;
                }
            case 1:
                this.mPowerOnAlarm.daysOfWeek = this.mPowerOnRepeatPref.getDaysOfWeek((Set) obj);
                if (processRepeatSet(1, this.mPowerOnAlarm)) {
                    this.mPowerOnRepeatPref.setDaysOfWeek(this.mPowerOnAlarm.daysOfWeek);
                    return true;
                }
                return false;
            case 2:
                this.mPowerOffAlarm.daysOfWeek = this.mPowerOffRepeatPref.getDaysOfWeek((Set) obj);
                if (processRepeatSet(2, this.mPowerOffAlarm)) {
                    this.mPowerOffRepeatPref.setDaysOfWeek(this.mPowerOffAlarm.daysOfWeek);
                    return true;
                }
                return false;
            case 3:
                boolean booleanValue2 = ((Boolean) obj).booleanValue();
                if (processAlarmSwitchClick(booleanValue2, this.mPowerOffAlarm)) {
                    this.mPowerOffTimePref.setEnabled(booleanValue2);
                    this.mPowerOffRepeatPref.setEnabled(booleanValue2);
                    break;
                } else {
                    return false;
                }
        }
        return true;
    }

    private boolean processRepeatSet(int i, Alarm alarm) {
        ContentResolver contentResolver = getContentResolver();
        int i2 = 0;
        if (contentResolver == null || alarm == null) {
            return false;
        }
        boolean isSametimeAlarm = Alarms.isSametimeAlarm(contentResolver, alarm.hour, alarm.minutes, alarm.id);
        boolean hasDuplicateAlarmDate = hasDuplicateAlarmDate(alarm);
        if (isAlarmStateChecked(this.mContext, alarm.id) && isSametimeAlarm && hasDuplicateAlarmDate) {
            showSameAlarmToast(alarm.hour, alarm.minutes, i);
            Log.v("processRepeatSet : same time can't set, please set again");
            return false;
        }
        if (hasDuplicateAlarmDate && isSametimeAlarm) {
            i2 = 1;
        }
        Log.v("processRepeatSet : hasDuplicateDate = " + hasDuplicateAlarmDate + ", hasSametimeAlarm = " + isSametimeAlarm + ", sameTimeFlag = " + i2);
        Settings.System.putInt(contentResolver, "sameTime", i2);
        saveAlarm(alarm);
        showAlarmSetToast(alarm);
        return true;
    }

    private boolean processAlarmSwitchClick(boolean z, Alarm alarm) {
        ContentResolver contentResolver = getContentResolver();
        if (contentResolver == null) {
            return false;
        }
        boolean z2 = 1 == Settings.System.getInt(contentResolver, "sameTime", 0);
        boolean isAlarmStateChecked = isAlarmStateChecked(this.mContext, alarm.id);
        Log.v("onClick isSameTime = " + z2 + ",isSameState = " + isAlarmStateChecked);
        if (z && z2 && isAlarmStateChecked) {
            showSameAlarmToast(alarm.hour, alarm.minutes);
            return false;
        }
        Alarms.enableAlarm(this.mContext, alarm.id, z);
        alarm.enabled = z;
        if (z) {
            showAlarmSetToast(alarm);
        }
        return true;
    }

    private boolean isAlarmStateChecked(Context context, int i) {
        Alarm alarm;
        if (i == 1) {
            alarm = Alarms.getAlarm(context, context.getContentResolver(), 2);
        } else {
            alarm = i == 2 ? Alarms.getAlarm(context, context.getContentResolver(), 1) : null;
        }
        if (alarm != null) {
            return alarm.enabled;
        }
        return false;
    }

    private void showSameAlarmToast(int i, int i2, int i3) {
        showSameAlarmToast(i, i2);
        showTimePicker(i3);
    }

    private void showSameAlarmToast(int i, int i2) {
        String str;
        int i3;
        int i4;
        if (DateFormat.is24HourFormat(this.mContext)) {
            str = i + ":" + i2;
            if (i2 < 10) {
                str = i + ":0" + i2;
            }
        } else if (i > 12) {
            StringBuilder sb = new StringBuilder();
            int i5 = i - 12;
            sb.append(i5);
            sb.append(":");
            sb.append(i2);
            sb.append(this.mContext.getResources().getString(R$string.timerpower_afternoon));
            str = sb.toString();
            if (i2 < 10) {
                str = i5 + ":0" + i2 + this.mContext.getResources().getString(i4);
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(i);
            sb2.append(":");
            sb2.append(i2);
            sb2.append(this.mContext.getResources().getString(R$string.timerpower_morning));
            str = sb2.toString();
            if (i2 < 10) {
                str = i + ":0" + i2 + this.mContext.getResources().getString(i3);
            }
        }
        Context context = this.mContext;
        Toast makeText = Toast.makeText(context, context.getResources().getString(R$string.alarm_alread_exist, str), 1);
        ToastManager.setToast(makeText);
        makeText.show();
    }

    private void showAlarmSetToast(Alarm alarm) {
        Toast makeText = Toast.makeText(this.mContext, formatToast(this.mContext, Alarms.calculateAlarm(alarm.hour, alarm.minutes, alarm.daysOfWeek).getTimeInMillis()), 1);
        ToastManager.setToast(makeText);
        makeText.show();
    }

    private String formatToast(Context context, long j) {
        String string;
        String string2;
        long currentTimeMillis = j - System.currentTimeMillis();
        long j2 = currentTimeMillis / 3600000;
        long j3 = (currentTimeMillis / 60000) % 60;
        long j4 = j2 / 24;
        long j5 = j2 % 24;
        int i = (j4 > 0L ? 1 : (j4 == 0L ? 0 : -1));
        String str = "";
        if (i == 0) {
            string = "";
        } else if (j4 == 1) {
            string = context.getString(R$string.day);
        } else {
            string = context.getString(R$string.days, Long.toString(j4));
        }
        int i2 = (j3 > 0L ? 1 : (j3 == 0L ? 0 : -1));
        if (i2 == 0) {
            string2 = "";
        } else if (j3 == 1) {
            string2 = context.getString(R$string.minute);
        } else {
            string2 = context.getString(R$string.minutes, Long.toString(j3));
        }
        int i3 = (j5 > 0L ? 1 : (j5 == 0L ? 0 : -1));
        if (i3 != 0) {
            if (j5 == 1) {
                str = context.getString(R$string.hour);
            } else {
                str = context.getString(R$string.hours, Long.toString(j5));
            }
        }
        return String.format(context.getResources().getStringArray(R$array.alarm_set)[(i3 > 0 ? (char) 2 : (char) 0) | (i > 0 ? (char) 1 : (char) 0) | (i2 > 0 ? 4 : 0)], string, str, string2);
    }

    private void saveAlarm(Alarm alarm) {
        Alarm alarm2 = new Alarm();
        alarm2.id = alarm.id;
        int i = alarm.hour;
        alarm2.hour = i;
        int i2 = alarm.minutes;
        alarm2.minutes = i2;
        Alarm.DaysOfWeek daysOfWeek = alarm.daysOfWeek;
        alarm2.daysOfWeek = daysOfWeek;
        alarm2.label = alarm.label;
        alarm2.enabled = alarm.enabled;
        alarm2.time = Alarms.calculateAlarm(i, i2, daysOfWeek).getTimeInMillis();
        if (alarm2.id != -1) {
            Alarms.setAlarm(this.mContext, alarm2);
            Alarms.enableAlarm(this.mContext, alarm2.id, alarm2.enabled);
        }
    }

    /* loaded from: classes2.dex */
    private static class TimerPowerSearchIndexProvider extends BaseSearchIndexProvider {
        private boolean mIsAdmin;

        public TimerPowerSearchIndexProvider() {
            this.mIsAdmin = UserHandle.myUserId() == 0;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (this.mIsAdmin && context.getResources().getBoolean(R$bool.config_support_scheduledPowerOnOff)) {
                SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
                searchIndexableResource.xmlResId = R$xml.timer_power;
                arrayList.add(searchIndexableResource);
                return arrayList;
            }
            return arrayList;
        }
    }
}
