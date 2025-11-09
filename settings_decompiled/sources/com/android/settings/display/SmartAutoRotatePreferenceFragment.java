package com.android.settings.display;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.android.settingslib.widget.FooterPreference;
import java.util.List;
/* loaded from: classes.dex */
public class SmartAutoRotatePreferenceFragment extends DashboardFragment {
    static final String AUTO_ROTATE_MAIN_SWITCH_PREFERENCE_KEY = "auto_rotate_main_switch";
    static final String AUTO_ROTATE_SWITCH_PREFERENCE_KEY = "auto_rotate_switch";
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.auto_rotate_settings) { // from class: com.android.settings.display.SmartAutoRotatePreferenceFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            return DeviceStateAutoRotationHelper.getRawDataToIndex(context, z);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SmartAutoRotatePreferenceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1867;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.auto_rotate_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        DeviceStateAutoRotationHelper.initControllers(getLifecycle(), useAll(DeviceStateAutoRotateSettingController.class));
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return DeviceStateAutoRotationHelper.createPreferenceControllers(context);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        createHeader(settingsActivity);
        Preference findPreference = findPreference("auto_rotate_footer_preference");
        if (findPreference != null) {
            findPreference.setVisible(SmartAutoRotateController.isRotationResolverServiceAvailable(settingsActivity));
            setupFooter();
        }
        return onCreateView;
    }

    void createHeader(SettingsActivity settingsActivity) {
        boolean isDeviceStateRotationEnabled = DeviceStateAutoRotationHelper.isDeviceStateRotationEnabled(settingsActivity);
        if (SmartAutoRotateController.isRotationResolverServiceAvailable(settingsActivity) && !isDeviceStateRotationEnabled) {
            findPreference(AUTO_ROTATE_SWITCH_PREFERENCE_KEY).setVisible(false);
        } else {
            findPreference(AUTO_ROTATE_MAIN_SWITCH_PREFERENCE_KEY).setVisible(false);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_auto_rotate_settings;
    }

    void setupFooter() {
        if (TextUtils.isEmpty(getString(getHelpResource()))) {
            return;
        }
        addHelpLink();
    }

    void addHelpLink() {
        FooterPreference footerPreference = (FooterPreference) findPreference("auto_rotate_footer_preference");
        if (footerPreference != null) {
            footerPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.display.SmartAutoRotatePreferenceFragment$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SmartAutoRotatePreferenceFragment.this.lambda$addHelpLink$0(view);
                }
            });
            footerPreference.setLearnMoreText(getString(R$string.auto_rotate_link_a11y));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addHelpLink$0(View view) {
        startActivityForResult(HelpUtils.getHelpIntent(getContext(), getString(getHelpResource()), ""), 0);
    }
}
