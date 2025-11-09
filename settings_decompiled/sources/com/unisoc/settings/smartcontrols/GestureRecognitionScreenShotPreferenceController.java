package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class GestureRecognitionScreenShotPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private Context mContext;
    private final Fragment mHost;
    public SwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "sp_screenshot";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public GestureRecognitionScreenShotPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mContext = context;
        this.mHost = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference("sp_screenshot");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "gesture_recognition_screenshot", 0);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        ((SwitchPreference) preference).setChecked(i == 1);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), "gesture_recognition_screenshot", booleanValue ? 1 : 0);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return false;
        }
        ((SwitchPreference) preference).setChecked(booleanValue);
        return false;
    }
}
