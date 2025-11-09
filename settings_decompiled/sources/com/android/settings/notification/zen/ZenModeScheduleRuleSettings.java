package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
/* loaded from: classes.dex */
public class ZenModeScheduleRuleSettings extends ZenModeRuleSettingsBase {
    private AlertDialog mDayDialog;
    private Preference mDays;
    private TimePickerPreference mEnd;
    private SwitchPreference mExitAtAlarm;
    private ZenModeConfig.ScheduleInfo mSchedule;
    private final ZenRuleScheduleHelper mScheduleHelper = new ZenRuleScheduleHelper();
    private TimePickerPreference mStart;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 144;
    }

    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase
    protected boolean setRule(AutomaticZenRule automaticZenRule) {
        ZenModeConfig.ScheduleInfo tryParseScheduleConditionId = automaticZenRule != null ? ZenModeConfig.tryParseScheduleConditionId(automaticZenRule.getConditionId()) : null;
        this.mSchedule = tryParseScheduleConditionId;
        return tryParseScheduleConditionId != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_schedule_rule_settings;
    }

    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase
    protected void onCreateInternal() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference findPreference = preferenceScreen.findPreference("days");
        this.mDays = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                ZenModeScheduleRuleSettings.this.showDaysDialog();
                return true;
            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerPreference timePickerPreference = new TimePickerPreference(getPrefContext(), fragmentManager);
        this.mStart = timePickerPreference;
        timePickerPreference.setKey("start_time");
        this.mStart.setTitle(R$string.zen_mode_start_time);
        this.mStart.setCallback(new TimePickerPreference.Callback() { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.2
            @Override // com.android.settings.notification.zen.ZenModeScheduleRuleSettings.TimePickerPreference.Callback
            public boolean onSetTime(int i, int i2) {
                if (ZenModeScheduleRuleSettings.this.mDisableListeners) {
                    return true;
                }
                if (ZenModeConfig.isValidHour(i) && ZenModeConfig.isValidMinute(i2)) {
                    if (i == ZenModeScheduleRuleSettings.this.mSchedule.startHour && i2 == ZenModeScheduleRuleSettings.this.mSchedule.startMinute) {
                        return true;
                    }
                    if (ZenModeRuleSettingsBase.DEBUG) {
                        Log.d("ZenModeSettings", "onPrefChange start h=" + i + " m=" + i2);
                    }
                    ZenModeScheduleRuleSettings.this.mSchedule.startHour = i;
                    ZenModeScheduleRuleSettings.this.mSchedule.startMinute = i2;
                    ZenModeScheduleRuleSettings zenModeScheduleRuleSettings = ZenModeScheduleRuleSettings.this;
                    zenModeScheduleRuleSettings.updateRule(ZenModeConfig.toScheduleConditionId(zenModeScheduleRuleSettings.mSchedule));
                    return true;
                }
                return false;
            }
        });
        preferenceScreen.addPreference(this.mStart);
        this.mStart.setDependency(this.mDays.getKey());
        TimePickerPreference timePickerPreference2 = new TimePickerPreference(getPrefContext(), fragmentManager);
        this.mEnd = timePickerPreference2;
        timePickerPreference2.setKey("end_time");
        this.mEnd.setTitle(R$string.zen_mode_end_time);
        this.mEnd.setCallback(new TimePickerPreference.Callback() { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.3
            @Override // com.android.settings.notification.zen.ZenModeScheduleRuleSettings.TimePickerPreference.Callback
            public boolean onSetTime(int i, int i2) {
                if (ZenModeScheduleRuleSettings.this.mDisableListeners) {
                    return true;
                }
                if (ZenModeConfig.isValidHour(i) && ZenModeConfig.isValidMinute(i2)) {
                    if (i == ZenModeScheduleRuleSettings.this.mSchedule.endHour && i2 == ZenModeScheduleRuleSettings.this.mSchedule.endMinute) {
                        return true;
                    }
                    if (ZenModeRuleSettingsBase.DEBUG) {
                        Log.d("ZenModeSettings", "onPrefChange end h=" + i + " m=" + i2);
                    }
                    ZenModeScheduleRuleSettings.this.mSchedule.endHour = i;
                    ZenModeScheduleRuleSettings.this.mSchedule.endMinute = i2;
                    ZenModeScheduleRuleSettings zenModeScheduleRuleSettings = ZenModeScheduleRuleSettings.this;
                    zenModeScheduleRuleSettings.updateRule(ZenModeConfig.toScheduleConditionId(zenModeScheduleRuleSettings.mSchedule));
                    return true;
                }
                return false;
            }
        });
        preferenceScreen.addPreference(this.mEnd);
        this.mEnd.setDependency(this.mDays.getKey());
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference("exit_at_alarm");
        this.mExitAtAlarm = switchPreference;
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                ZenModeScheduleRuleSettings.this.mSchedule.exitAtAlarm = ((Boolean) obj).booleanValue();
                ZenModeScheduleRuleSettings zenModeScheduleRuleSettings = ZenModeScheduleRuleSettings.this;
                zenModeScheduleRuleSettings.updateRule(ZenModeConfig.toScheduleConditionId(zenModeScheduleRuleSettings.mSchedule));
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDays() {
        String daysDescription = this.mScheduleHelper.getDaysDescription(this.mContext, this.mSchedule);
        if (daysDescription != null) {
            this.mDays.setSummary(daysDescription);
            this.mDays.notifyDependencyChange(false);
            return;
        }
        this.mDays.setSummary(R$string.zen_mode_schedule_rule_days_none);
        this.mDays.notifyDependencyChange(true);
    }

    private void updateEndSummary() {
        ZenModeConfig.ScheduleInfo scheduleInfo = this.mSchedule;
        this.mEnd.setSummaryFormat((scheduleInfo.startHour * 60) + scheduleInfo.startMinute >= (scheduleInfo.endHour * 60) + scheduleInfo.endMinute ? R$string.zen_mode_end_time_next_day_summary_format : 0);
    }

    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase
    protected void updateControlsInternal() {
        updateDays();
        TimePickerPreference timePickerPreference = this.mStart;
        ZenModeConfig.ScheduleInfo scheduleInfo = this.mSchedule;
        timePickerPreference.setTime(scheduleInfo.startHour, scheduleInfo.startMinute);
        TimePickerPreference timePickerPreference2 = this.mEnd;
        ZenModeConfig.ScheduleInfo scheduleInfo2 = this.mSchedule;
        timePickerPreference2.setTime(scheduleInfo2.endHour, scheduleInfo2.endMinute);
        this.mExitAtAlarm.setChecked(this.mSchedule.exitAtAlarm);
        updateEndSummary();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenModeRuleSettingsBase) this).mHeader = new ZenAutomaticRuleHeaderPreferenceController(context, this, getSettingsLifecycle());
        this.mActionButtons = new ZenRuleButtonsPreferenceController(context, this, getSettingsLifecycle());
        this.mSwitch = new ZenAutomaticRuleSwitchPreferenceController(context, this, getSettingsLifecycle());
        arrayList.add(((ZenModeRuleSettingsBase) this).mHeader);
        arrayList.add(this.mActionButtons);
        arrayList.add(this.mSwitch);
        return arrayList;
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDayDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mDayDialog.dismiss();
        this.mDayDialog = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDaysDialog() {
        this.mDayDialog = new AlertDialog.Builder(this.mContext).setTitle(R$string.zen_mode_schedule_rule_days).setView(new ZenModeScheduleDaysSelection(this.mContext, this.mSchedule.days) { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.6
            @Override // com.android.settings.notification.zen.ZenModeScheduleDaysSelection
            protected void onChanged(int[] iArr) {
                ZenModeScheduleRuleSettings zenModeScheduleRuleSettings = ZenModeScheduleRuleSettings.this;
                if (zenModeScheduleRuleSettings.mDisableListeners || Arrays.equals(iArr, zenModeScheduleRuleSettings.mSchedule.days)) {
                    return;
                }
                if (ZenModeRuleSettingsBase.DEBUG) {
                    Log.d("ZenModeSettings", "days.onChanged days=" + Arrays.asList(iArr));
                }
                ZenModeScheduleRuleSettings.this.mSchedule.days = iArr;
                ZenModeScheduleRuleSettings zenModeScheduleRuleSettings2 = ZenModeScheduleRuleSettings.this;
                zenModeScheduleRuleSettings2.updateRule(ZenModeConfig.toScheduleConditionId(zenModeScheduleRuleSettings2.mSchedule));
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.5
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                ZenModeScheduleRuleSettings.this.updateDays();
            }
        }).setPositiveButton(R$string.done_button, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TimePickerPreference extends Preference {
        private Callback mCallback;
        private final Context mContext;
        private int mHourOfDay;
        private int mMinute;
        private int mSummaryFormat;

        /* loaded from: classes.dex */
        public interface Callback {
            boolean onSetTime(int i, int i2);
        }

        public TimePickerPreference(Context context, final FragmentManager fragmentManager) {
            super(context);
            this.mContext = context;
            setPersistent(false);
            setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenModeScheduleRuleSettings.TimePickerPreference.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    TimePickerFragment timePickerFragment = new TimePickerFragment();
                    timePickerFragment.pref = TimePickerPreference.this;
                    timePickerFragment.show(fragmentManager, TimePickerPreference.class.getName());
                    return true;
                }
            });
        }

        public void setCallback(Callback callback) {
            this.mCallback = callback;
        }

        public void setSummaryFormat(int i) {
            this.mSummaryFormat = i;
            updateSummary();
        }

        public void setTime(int i, int i2) {
            Callback callback = this.mCallback;
            if (callback == null || callback.onSetTime(i, i2)) {
                this.mHourOfDay = i;
                this.mMinute = i2;
                updateSummary();
            }
        }

        private void updateSummary() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(11, this.mHourOfDay);
            calendar.set(12, this.mMinute);
            String format = DateFormat.getTimeFormat(this.mContext).format(calendar.getTime());
            if (this.mSummaryFormat != 0) {
                format = this.mContext.getResources().getString(this.mSummaryFormat, format);
            }
            setSummary(format);
        }

        /* loaded from: classes.dex */
        public static class TimePickerFragment extends InstrumentedDialogFragment implements TimePickerDialog.OnTimeSetListener {
            public TimePickerPreference pref;

            @Override // com.android.settingslib.core.instrumentation.Instrumentable
            public int getMetricsCategory() {
                return 556;
            }

            @Override // androidx.fragment.app.DialogFragment
            public Dialog onCreateDialog(Bundle bundle) {
                TimePickerPreference timePickerPreference = this.pref;
                boolean z = timePickerPreference != null && timePickerPreference.mHourOfDay >= 0 && this.pref.mMinute >= 0;
                Calendar calendar = Calendar.getInstance();
                return new TimePickerDialog(getActivity(), this, z ? this.pref.mHourOfDay : calendar.get(11), z ? this.pref.mMinute : calendar.get(12), DateFormat.is24HourFormat(getActivity()));
            }

            @Override // android.app.TimePickerDialog.OnTimeSetListener
            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                TimePickerPreference timePickerPreference = this.pref;
                if (timePickerPreference != null) {
                    timePickerPreference.setTime(i, i2);
                }
            }
        }
    }
}
