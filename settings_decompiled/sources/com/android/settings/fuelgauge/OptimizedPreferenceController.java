package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class OptimizedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    String KEY_OPTIMIZED_PREF;
    BatteryOptimizeUtils mBatteryOptimizeUtils;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OptimizedPreferenceController(Context context, int i, String str) {
        super(context);
        this.KEY_OPTIMIZED_PREF = "optimized_pref";
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(context, i, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (!this.mBatteryOptimizeUtils.isValidPackageName()) {
            Log.d("OPTIMIZED_PREF", "invalid package name, optimized states only");
            preference.setEnabled(true);
            ((SelectorWithWidgetPreference) preference).setChecked(true);
        } else if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            Log.d("OPTIMIZED_PREF", "is system or default app, disable pref");
            ((SelectorWithWidgetPreference) preference).setChecked(false);
            preference.setEnabled(false);
        } else if (this.mBatteryOptimizeUtils.getAppOptimizationMode() == 3) {
            Log.d("OPTIMIZED_PREF", "is optimized states");
            ((SelectorWithWidgetPreference) preference).setChecked(true);
        } else {
            ((SelectorWithWidgetPreference) preference).setChecked(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY_OPTIMIZED_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return getPreferenceKey().equals(preference.getKey());
    }
}
