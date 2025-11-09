package com.android.settings.wifi;

import android.os.Bundle;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
/* loaded from: classes.dex */
public class WifiInfo extends SettingsPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 89;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.testing_wifi_settings);
    }
}
