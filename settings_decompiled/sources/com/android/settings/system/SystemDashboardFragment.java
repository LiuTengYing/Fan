package com.android.settings.system;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class SystemDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.system.SystemDashboardFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.system_dashboard_fragment;
            return Arrays.asList(searchIndexableResource);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SystemDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 744;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (getVisiblePreferenceCount(preferenceScreen) == preferenceScreen.getInitialExpandedChildrenCount() + 1) {
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
        showRestrictionDialog();
    }

    public void showRestrictionDialog() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.getBoolean("show_aware_dialog_disabled", false)) {
            return;
        }
        FeatureFactory.getFactory(getContext()).getAwareFeatureProvider().showRestrictionDialog(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.system_dashboard_fragment;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_system_dashboard;
    }

    private int getVisiblePreferenceCount(PreferenceGroup preferenceGroup) {
        int i = 0;
        for (int i2 = 0; i2 < preferenceGroup.getPreferenceCount(); i2++) {
            Preference preference = preferenceGroup.getPreference(i2);
            if (preference instanceof PreferenceGroup) {
                i += getVisiblePreferenceCount((PreferenceGroup) preference);
            } else if (preference.isVisible()) {
                i++;
            }
        }
        return i;
    }
}
