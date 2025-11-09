package com.android.settings.fuelgauge;

import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes.dex */
public class AppAutoRunFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference mAppAsLunchPref;
    private Preference mAppAutoRunPref;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 54;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.app_auto_run_fragment);
        Preference findPreference = findPreference("app_auto_run");
        this.mAppAutoRunPref = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        Preference findPreference2 = findPreference("app_as_lunch");
        this.mAppAsLunchPref = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int i;
        String key = preference.getKey();
        key.hashCode();
        int i2 = 1003;
        if (key.equals("app_as_lunch")) {
            i = R$string.app_as_lunch_optimization;
            i2 = 1004;
        } else if (key.equals("app_auto_run")) {
            i = R$string.app_auto_run_optimization;
        } else {
            i = R$string.app_auto_run_optimization;
        }
        Log.d("AppAutoRun", " onPreferenceClick key:" + key);
        Bundle bundle = new Bundle();
        bundle.putInt("app_list_type", 4);
        bundle.putInt("type_app_config", i2);
        new SubSettingLauncher(getActivity()).setDestination(UnisocManageApplications.class.getName()).setArguments(bundle).setTitleRes(i).setSourceMetricsCategory(getMetricsCategory()).launch();
        return true;
    }
}
