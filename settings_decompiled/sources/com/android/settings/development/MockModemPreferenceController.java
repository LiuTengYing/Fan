package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class MockModemPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String ALLOW_MOCK_MODEM_PROPERTY = "persist.radio.allow_mock_modem";

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "allow_mock_modem";
    }

    public MockModemPreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            SystemProperties.set(ALLOW_MOCK_MODEM_PROPERTY, ((Boolean) obj).booleanValue() ? "true" : "false");
            return true;
        } catch (RuntimeException e) {
            Log.e("MockModemPreferenceController", "Fail to set radio system property: " + e.getMessage());
            return true;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        try {
            ((SwitchPreference) this.mPreference).setChecked(SystemProperties.getBoolean(ALLOW_MOCK_MODEM_PROPERTY, false));
        } catch (RuntimeException e) {
            Log.e("MockModemPreferenceController", "Fail to get radio system property: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        try {
            SystemProperties.set(ALLOW_MOCK_MODEM_PROPERTY, "false");
            ((SwitchPreference) this.mPreference).setChecked(false);
        } catch (RuntimeException e) {
            Log.e("MockModemPreferenceController", "Fail to set radio system property: " + e.getMessage());
        }
    }
}
