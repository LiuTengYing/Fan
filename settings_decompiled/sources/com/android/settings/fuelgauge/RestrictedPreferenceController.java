package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class RestrictedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    String KEY_RESTRICTED_PREF;
    BatteryOptimizeUtils mBatteryOptimizeUtils;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public RestrictedPreferenceController(Context context, int i, String str) {
        super(context);
        this.KEY_RESTRICTED_PREF = "restricted_pref";
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(context, i, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (!this.mBatteryOptimizeUtils.isValidPackageName()) {
            Log.d("RESTRICTED_PREF", "invalid package name, disable pref");
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            Log.d("RESTRICTED_PREF", "is system or default app, disable pref");
            ((SelectorWithWidgetPreference) preference).setChecked(false);
            preference.setEnabled(false);
        } else if (this.mBatteryOptimizeUtils.getAppOptimizationMode() == 1) {
            Log.d("RESTRICTED_PREF", "is restricted states");
            ((SelectorWithWidgetPreference) preference).setChecked(true);
        } else {
            ((SelectorWithWidgetPreference) preference).setChecked(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY_RESTRICTED_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        this.mBatteryOptimizeUtils.setAppUsageState(1);
        return getPreferenceKey().equals(preference.getKey());
    }
}
