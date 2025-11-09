package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
/* loaded from: classes.dex */
public class LongPressPowerButtonPreferenceController extends TogglePreferenceController {
    private static final String ASSIST_SWITCH_KEY = "gesture_power_menu_long_press_for_assist";
    private static final String FOOTER_HINT_KEY = "power_menu_power_volume_up_hint";
    private static final int KEY_CHORD_POWER_VOLUME_UP_DEFAULT_VALUE_RESOURCE = 17694842;
    @VisibleForTesting
    static final int KEY_CHORD_POWER_VOLUME_UP_GLOBAL_ACTIONS = 2;
    @VisibleForTesting
    static final int KEY_CHORD_POWER_VOLUME_UP_MUTE_TOGGLE = 1;
    @VisibleForTesting
    static final int KEY_CHORD_POWER_VOLUME_UP_NO_ACTION = 0;
    private static final String KEY_CHORD_POWER_VOLUME_UP_SETTING = "key_chord_power_volume_up";
    @VisibleForTesting
    Preference mAssistSwitch;
    @VisibleForTesting
    Preference mFooterHint;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LongPressPowerButtonPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mFooterHint = preferenceScreen.findPreference(FOOTER_HINT_KEY);
        this.mAssistSwitch = preferenceScreen.findPreference(ASSIST_SWITCH_KEY);
        refreshStateDisplay();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int powerButtonSettingValue = PowerMenuSettingsUtils.getPowerButtonSettingValue(this.mContext);
        if (powerButtonSettingValue == 5) {
            return this.mContext.getString(R$string.power_menu_summary_long_press_for_assist_enabled);
        }
        if (powerButtonSettingValue == 1) {
            return this.mContext.getString(R$string.power_menu_summary_long_press_for_assist_disabled_with_power_menu);
        }
        return this.mContext.getString(R$string.power_menu_summary_long_press_for_assist_disabled_no_action);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(17891698) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return PowerMenuSettingsUtils.isLongPressPowerForAssistEnabled(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (setPowerLongPressValue(z)) {
            setPowerVolumeChordValue(z);
            refreshStateDisplay();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_system;
    }

    private void refreshStateDisplay() {
        Preference preference = this.mAssistSwitch;
        if (preference != null) {
            preference.setSummary(getSummary());
        }
        if (this.mFooterHint != null) {
            String string = this.mContext.getString(R$string.power_menu_power_volume_up_hint);
            if (this.mContext.getResources().getBoolean(17891828)) {
                string = string + "\n\n" + this.mContext.getString(R$string.power_menu_power_prevent_ringing_hint);
            }
            this.mFooterHint.setSummary(string);
            this.mFooterHint.setVisible(isPowerMenuKeyChordEnabled(this.mContext));
        }
    }

    private static boolean isPowerMenuKeyChordEnabled(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), KEY_CHORD_POWER_VOLUME_UP_SETTING, context.getResources().getInteger(KEY_CHORD_POWER_VOLUME_UP_DEFAULT_VALUE_RESOURCE)) == 2;
    }

    private boolean setPowerLongPressValue(boolean z) {
        if (z) {
            return Settings.Global.putInt(this.mContext.getContentResolver(), "power_button_long_press", 5);
        }
        int integer = this.mContext.getResources().getInteger(17694854);
        if (integer == 5) {
            return Settings.Global.putInt(this.mContext.getContentResolver(), "power_button_long_press", 1);
        }
        return Settings.Global.putInt(this.mContext.getContentResolver(), "power_button_long_press", integer);
    }

    private boolean setPowerVolumeChordValue(boolean z) {
        if (z) {
            return Settings.Global.putInt(this.mContext.getContentResolver(), KEY_CHORD_POWER_VOLUME_UP_SETTING, this.mContext.getResources().getInteger(KEY_CHORD_POWER_VOLUME_UP_DEFAULT_VALUE_RESOURCE));
        }
        return Settings.Global.putInt(this.mContext.getContentResolver(), KEY_CHORD_POWER_VOLUME_UP_SETTING, 1);
    }
}
