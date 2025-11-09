package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class UnrestrictedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    String KEY_UNRESTRICTED_PREF;
    BatteryOptimizeUtils mBatteryOptimizeUtils;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public UnrestrictedPreferenceController(Context context, int i, String str) {
        super(context);
        this.KEY_UNRESTRICTED_PREF = "unrestricted_pref";
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(context, i, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (!this.mBatteryOptimizeUtils.isValidPackageName()) {
            Log.d("UNRESTRICTED_PREF", "invalid package name, disable pref");
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            Log.d("UNRESTRICTED_PREF", "is system or default app, unrestricted states only");
            ((SelectorWithWidgetPreference) preference).setChecked(true);
        } else if (this.mBatteryOptimizeUtils.getAppOptimizationMode() == 2) {
            Log.d("UNRESTRICTED_PREF", "is unrestricted states");
            ((SelectorWithWidgetPreference) preference).setChecked(true);
        } else {
            ((SelectorWithWidgetPreference) preference).setChecked(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY_UNRESTRICTED_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return getPreferenceKey().equals(preference.getKey());
    }
}
