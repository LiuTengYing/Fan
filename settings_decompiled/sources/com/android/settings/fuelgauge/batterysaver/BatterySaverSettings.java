package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class BatterySaverSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.battery_saver_settings) { // from class: com.android.settings.fuelgauge.batterysaver.BatterySaverSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return context.getResources().getBoolean(R$bool.config_show_top_level_battery);
        }
    };
    private String mHelpUri;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BatterySaverSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1881;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setupFooter();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.battery_saver_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_battery_saver_settings;
    }

    void setupFooter() {
        String string = getString(R$string.help_url_battery_saver_settings);
        this.mHelpUri = string;
        if (TextUtils.isEmpty(string)) {
            return;
        }
        addHelpLink();
    }

    void addHelpLink() {
        FooterPreference footerPreference = (FooterPreference) getPreferenceScreen().findPreference("battery_saver_footer_preference");
        if (footerPreference != null) {
            footerPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.fuelgauge.batterysaver.BatterySaverSettings$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BatterySaverSettings.this.lambda$addHelpLink$0(view);
                }
            });
            footerPreference.setLearnMoreText(getString(R$string.battery_saver_link_a11y));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addHelpLink$0(View view) {
        this.mMetricsFeatureProvider.action(getContext(), 1779, new Pair[0]);
        startActivityForResult(HelpUtils.getHelpIntent(getContext(), getString(R$string.help_url_battery_saver_settings), ""), 0);
    }
}
