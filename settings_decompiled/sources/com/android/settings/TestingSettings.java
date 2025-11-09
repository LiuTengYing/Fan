package com.android.settings;

import android.os.Bundle;
import android.os.UserManager;
import androidx.preference.PreferenceScreen;
/* loaded from: classes.dex */
public class TestingSettings extends SettingsPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 89;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.testing_settings);
        if (UserManager.get(getContext()).isAdminUser()) {
            return;
        }
        getPreferenceScreen().removePreference((PreferenceScreen) findPreference("radio_info_settings"));
    }
}
