package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class TouchDisablePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final Fragment mHost;
    public SwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "touch_disable";
    }

    public TouchDisablePreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isTouchDisableAvailable(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference("touch_disable");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        if (PocketModeFragment.isPocketModeEnabled(this.mContext)) {
            i = Settings.Global.getInt(this.mContext.getContentResolver(), "touch_disable", 0);
        } else {
            i = Settings.Global.getInt(this.mContext.getContentResolver(), "touch_disable_switch", 0);
        }
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        ((SwitchPreference) preference).setChecked(i == 1);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), "touch_disable", booleanValue ? 1 : 0);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return false;
        }
        ((SwitchPreference) preference).setChecked(booleanValue);
        return false;
    }

    public static boolean isTouchDisableAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_touchDisable) && Utils.isSupportSensor(context, 65538) && Utils.isSupportSensor(context, 65538);
    }

    public void updateOnPocketModeChange(boolean z) {
        if (!z) {
            SwitchPreference switchPreference = this.mPreference;
            if (switchPreference != null && switchPreference.isChecked()) {
                Settings.Global.putInt(this.mContext.getContentResolver(), "touch_disable_switch", 1);
            }
            Settings.Global.putInt(this.mContext.getContentResolver(), "touch_disable", 0);
        } else if (Settings.Global.getInt(this.mContext.getContentResolver(), "touch_disable_switch", 0) == 1) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "touch_disable", 1);
            Settings.Global.putInt(this.mContext.getContentResolver(), "touch_disable_switch", 0);
        }
    }
}
