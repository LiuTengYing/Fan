package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.CaptioningManager;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class CaptionMoreOptionsFragment extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.captioning_more_options);
    private CaptioningManager mCaptioningManager;
    private LocalePreference mLocale;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "CaptionMoreOptionsFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1820;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        initializeAllPreferences();
        updateAllPreferences();
        installUpdateListeners();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.captioning_more_options;
    }

    private void initializeAllPreferences() {
        this.mLocale = (LocalePreference) findPreference("captioning_locale");
    }

    private void installUpdateListeners() {
        this.mLocale.setOnPreferenceChangeListener(this);
    }

    private void updateAllPreferences() {
        String rawLocale = this.mCaptioningManager.getRawLocale();
        LocalePreference localePreference = this.mLocale;
        if (rawLocale == null) {
            rawLocale = "";
        }
        localePreference.setValue(rawLocale);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mLocale == preference) {
            Settings.Secure.putString(contentResolver, "accessibility_captioning_locale", (String) obj);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_caption;
    }
}
