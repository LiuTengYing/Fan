package com.android.settings.enterprise;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;
/* loaded from: classes.dex */
public class EnterprisePrivacySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.enterprise.EnterprisePrivacySettings.1
        private PrivacySettingsPreference mPrivacySettingsPreference;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return EnterprisePrivacySettings.isPageEnabled(context);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            PrivacySettingsPreference createPrivacySettingsPreference = PrivacySettingsPreferenceFactory.createPrivacySettingsPreference(context);
            this.mPrivacySettingsPreference = createPrivacySettingsPreference;
            return createPrivacySettingsPreference.getXmlResourcesToIndex();
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            PrivacySettingsPreference createPrivacySettingsPreference = PrivacySettingsPreferenceFactory.createPrivacySettingsPreference(context);
            this.mPrivacySettingsPreference = createPrivacySettingsPreference;
            return createPrivacySettingsPreference.createPreferenceControllers(false);
        }
    };
    @VisibleForTesting
    PrivacySettingsPreference mPrivacySettingsPreference;

    @Override // com.android.settings.dashboard.DashboardFragment
    protected String getLogTag() {
        return "EnterprisePrivacySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 628;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        this.mPrivacySettingsPreference = PrivacySettingsPreferenceFactory.createPrivacySettingsPreference(context);
        super.onAttach(context);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mPrivacySettingsPreference instanceof PrivacySettingsFinancedPreference) {
            return;
        }
        replaceEnterprisePreferenceScreenTitle("Settings.MANAGED_DEVICE_INFO", R$string.enterprise_privacy_settings);
        replaceEnterpriseStringTitle("exposure_category", "Settings.INFORMATION_SEEN_BY_ORGANIZATION_TITLE", R$string.enterprise_privacy_exposure_category);
        replaceEnterpriseStringTitle("enterprise_privacy_enterprise_data", "Settings.ADMIN_CAN_SEE_WORK_DATA_WARNING", R$string.enterprise_privacy_enterprise_data);
        replaceEnterpriseStringTitle("enterprise_privacy_installed_packages", "Settings.ADMIN_CAN_SEE_APPS_WARNING", R$string.enterprise_privacy_installed_packages);
        replaceEnterpriseStringTitle("enterprise_privacy_usage_stats", "Settings.ADMIN_CAN_SEE_USAGE_WARNING", R$string.enterprise_privacy_usage_stats);
        replaceEnterpriseStringTitle("network_logs", "Settings.ADMIN_CAN_SEE_NETWORK_LOGS_WARNING", R$string.enterprise_privacy_network_logs);
        replaceEnterpriseStringTitle("bug_reports", "Settings.ADMIN_CAN_SEE_BUG_REPORT_WARNING", R$string.enterprise_privacy_bug_reports);
        replaceEnterpriseStringTitle("security_logs", "Settings.ADMIN_CAN_SEE_SECURITY_LOGS_WARNING", R$string.enterprise_privacy_security_logs);
        replaceEnterpriseStringTitle("exposure_changes_category", "Settings.CHANGES_BY_ORGANIZATION_TITLE", R$string.enterprise_privacy_exposure_changes_category);
        replaceEnterpriseStringTitle("number_enterprise_installed_packages", "Settings.ADMIN_ACTION_APPS_INSTALLED", R$string.enterprise_privacy_enterprise_installed_packages);
        replaceEnterpriseStringTitle("enterprise_privacy_number_location_access_packages", "Settings.ADMIN_ACTION_ACCESS_LOCATION", R$string.enterprise_privacy_location_access);
        replaceEnterpriseStringTitle("enterprise_privacy_number_microphone_access_packages", "Settings.ADMIN_ACTION_ACCESS_MICROPHONE", R$string.enterprise_privacy_microphone_access);
        replaceEnterpriseStringTitle("enterprise_privacy_number_camera_access_packages", "Settings.ADMIN_ACTION_ACCESS_CAMERA", R$string.enterprise_privacy_camera_access);
        replaceEnterpriseStringTitle("number_enterprise_set_default_apps", "Settings.ADMIN_ACTION_SET_DEFAULT_APPS", R$string.enterprise_privacy_enterprise_set_default_apps);
        replaceEnterpriseStringTitle("always_on_vpn_managed_profile", "Settings.ALWAYS_ON_VPN_WORK_PROFILE", R$string.enterprise_privacy_always_on_vpn_work);
        replaceEnterpriseStringTitle("input_method", "Settings.ADMIN_ACTION_SET_CURRENT_INPUT_METHOD", R$string.enterprise_privacy_input_method);
        replaceEnterpriseStringTitle("global_http_proxy", "Settings.ADMIN_ACTION_SET_HTTP_PROXY", R$string.enterprise_privacy_global_http_proxy);
        replaceEnterpriseStringTitle("ca_certs_current_user", "Settings.CA_CERTS_PERSONAL_PROFILE", R$string.enterprise_privacy_ca_certs_personal);
        replaceEnterpriseStringTitle("ca_certs_managed_profile", "Settings.CA_CERTS_WORK_PROFILE", R$string.enterprise_privacy_ca_certs_work);
        replaceEnterpriseStringTitle("device_access_category", "Settings.YOUR_ACCESS_TO_THIS_DEVICE_TITLE", R$string.enterprise_privacy_device_access_category);
        replaceEnterpriseStringTitle("enterprise_privacy_lock_device", "Settings.ADMIN_CAN_LOCK_DEVICE", R$string.enterprise_privacy_lock_device);
        replaceEnterpriseStringTitle("enterprise_privacy_wipe_device", "Settings.ADMIN_CAN_WIPE_DEVICE", R$string.enterprise_privacy_wipe_device);
        replaceEnterpriseStringTitle("failed_password_wipe_current_user", "Settings.ADMIN_CONFIGURED_FAILED_PASSWORD_WIPE_DEVICE", R$string.enterprise_privacy_failed_password_wipe_device);
        replaceEnterpriseStringTitle("failed_password_wipe_managed_profile", "Settings.ADMIN_CONFIGURED_FAILED_PASSWORD_WIPE_WORK_PROFILE", R$string.enterprise_privacy_failed_password_wipe_work);
        replaceEnterpriseStringTitle("enterprise_privacy_footer", "Settings.ENTERPRISE_PRIVACY_FOOTER", R$string.enterprise_privacy_header);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mPrivacySettingsPreference = null;
        super.onDetach();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    protected int getPreferenceScreenResId() {
        return this.mPrivacySettingsPreference.getPreferenceScreenResId();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return this.mPrivacySettingsPreference.createPreferenceControllers(true);
    }

    public static boolean isPageEnabled(Context context) {
        return FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context).hasDeviceOwner();
    }
}
