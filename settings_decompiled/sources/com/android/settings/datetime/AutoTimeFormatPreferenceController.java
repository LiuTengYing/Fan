package com.android.settings.datetime;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Locale;
/* loaded from: classes.dex */
public class AutoTimeFormatPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_24hour";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AutoTimeFormatPreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            ((SwitchPreference) preference).setChecked(isAutoTimeFormatSelection(this.mContext));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ((preference instanceof TwoStatePreference) && TextUtils.equals("auto_24hour", preference.getKey())) {
            TimeFormatPreferenceController.update24HourFormat(this.mContext, ((SwitchPreference) preference).isChecked() ? null : Boolean.valueOf(is24HourLocale(this.mContext.getResources().getConfiguration().locale)));
            return true;
        }
        return false;
    }

    boolean is24HourLocale(Locale locale) {
        return DateFormat.is24HourLocale(locale);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAutoTimeFormatSelection(Context context) {
        return Settings.System.getString(context.getContentResolver(), "time_12_24") == null;
    }
}
