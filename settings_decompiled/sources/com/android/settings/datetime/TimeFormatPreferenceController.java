package com.android.settings.datetime;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Calendar;
/* loaded from: classes.dex */
public class TimeFormatPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final Calendar mDummyDate;
    private final boolean mIsFromSUW;
    private final UpdateTimeAndDateCallback mUpdateTimeAndDateCallback;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "24 hour";
    }

    public TimeFormatPreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback, boolean z) {
        super(context);
        this.mIsFromSUW = z;
        this.mDummyDate = Calendar.getInstance();
        this.mUpdateTimeAndDateCallback = updateTimeAndDateCallback;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !this.mIsFromSUW;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof TwoStatePreference) {
            preference.setEnabled(!AutoTimeFormatPreferenceController.isAutoTimeFormatSelection(this.mContext));
            ((TwoStatePreference) preference).setChecked(is24Hour());
            Calendar calendar = Calendar.getInstance();
            this.mDummyDate.setTimeZone(calendar.getTimeZone());
            this.mDummyDate.set(calendar.get(1), 11, 31, 13, 0, 0);
            preference.setSummary(DateFormat.getTimeFormat(this.mContext).format(this.mDummyDate.getTime()));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ((preference instanceof TwoStatePreference) && TextUtils.equals("24 hour", preference.getKey())) {
            update24HourFormat(this.mContext, Boolean.valueOf(((SwitchPreference) preference).isChecked()));
            this.mUpdateTimeAndDateCallback.updateTimeAndDateDisplay(this.mContext);
            return true;
        }
        return false;
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update24HourFormat(Context context, Boolean bool) {
        set24Hour(context, bool);
        timeUpdated(context, bool);
    }

    static void timeUpdated(Context context, Boolean bool) {
        Intent intent = new Intent("android.intent.action.TIME_SET");
        intent.addFlags(16777216);
        intent.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", bool == null ? 2 : bool.booleanValue());
        context.sendBroadcast(intent);
    }

    static void set24Hour(Context context, Boolean bool) {
        String str;
        if (bool == null) {
            str = null;
        } else {
            str = bool.booleanValue() ? "24" : "12";
        }
        Settings.System.putString(context.getContentResolver(), "time_12_24", str);
    }
}
