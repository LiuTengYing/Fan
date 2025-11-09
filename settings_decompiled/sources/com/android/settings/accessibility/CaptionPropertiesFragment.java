package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.CaptioningManager;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CaptionPropertiesFragment extends DashboardFragment implements Preference.OnPreferenceChangeListener, OnMainSwitchChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.captioning_settings);
    private CaptioningManager mCaptioningManager;
    private Preference mMoreOptions;
    private final List<Preference> mPreferenceList = new ArrayList();
    private SettingsMainSwitchPreference mSwitch;
    private Preference mTextAppearance;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "CaptionPropertiesFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 3;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        initializeAllPreferences();
        installUpdateListeners();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSwitch.setChecked(this.mCaptioningManager.isEnabled());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.captioning_settings;
    }

    private void initializeAllPreferences() {
        this.mSwitch = (SettingsMainSwitchPreference) findPreference("captioning_preference_switch");
        this.mTextAppearance = findPreference("captioning_caption_appearance");
        this.mMoreOptions = findPreference("captioning_more_options");
        this.mPreferenceList.add(this.mTextAppearance);
        this.mPreferenceList.add(this.mMoreOptions);
    }

    private void installUpdateListeners() {
        this.mSwitch.setOnPreferenceChangeListener(this);
        this.mSwitch.addOnSwitchChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mSwitch == preference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_caption;
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        Settings.Secure.putInt(getActivity().getContentResolver(), "accessibility_captioning_enabled", z ? 1 : 0);
    }
}
