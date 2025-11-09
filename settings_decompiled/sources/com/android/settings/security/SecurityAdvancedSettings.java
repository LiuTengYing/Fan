package com.android.settings.security;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.biometrics.combination.CombinedBiometricProfileStatusPreferenceController;
import com.android.settings.biometrics.face.FaceProfileStatusPreferenceController;
import com.android.settings.biometrics.fingerprint.FingerprintProfileStatusPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.safetycenter.SafetyCenterManagerWrapper;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.trustagent.TrustAgentListPreferenceController;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SecurityAdvancedSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.security_advanced_settings) { // from class: com.android.settings.security.SecurityAdvancedSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SecurityAdvancedSettings.buildPreferenceControllers(context, null, null);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SecurityAdvancedSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1885;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        replaceEnterpriseStringTitle("unlock_set_or_change_profile", "Settings.WORK_PROFILE_SET_UNLOCK_LAUNCH_PICKER_TITLE", R$string.unlock_set_unlock_launch_picker_title_profile);
        replaceEnterpriseStringSummary("unification", "Settings.WORK_PROFILE_UNIFY_LOCKS_SUMMARY", R$string.lock_settings_profile_unification_summary);
        replaceEnterpriseStringTitle(FingerprintProfileStatusPreferenceController.KEY_FINGERPRINT_SETTINGS, "Settings.FINGERPRINT_FOR_WORK", R$string.security_settings_work_fingerprint_preference_title);
        replaceEnterpriseStringTitle("manage_device_admin", "Settings.MANAGE_DEVICE_ADMIN_APPS", R$string.manage_device_admin);
        replaceEnterpriseStringTitle("security_category_profile", "Settings.WORK_PROFILE_SECURITY_TITLE", R$string.lock_settings_profile_title);
        replaceEnterpriseStringTitle("enterprise_privacy", "Settings.MANAGED_DEVICE_INFO", R$string.enterprise_privacy_settings);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    public String getCategoryKey() {
        Context context = getContext();
        if (context == null) {
            return "com.android.settings.category.ia.legacy_advanced_security";
        }
        if (SafetyCenterManagerWrapper.get().isEnabled(context)) {
            return "com.android.settings.category.ia.advanced_security";
        }
        SecuritySettingsFeatureProvider securitySettingsFeatureProvider = FeatureFactory.getFactory(context).getSecuritySettingsFeatureProvider();
        return securitySettingsFeatureProvider.hasAlternativeSecuritySettingsFragment() ? securitySettingsFeatureProvider.getAlternativeAdvancedSettingsCategoryKey() : "com.android.settings.category.ia.legacy_advanced_security";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.security_advanced_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (((TrustAgentListPreferenceController) use(TrustAgentListPreferenceController.class)).handleActivityResult(i, i2) || ((LockUnificationPreferenceController) use(LockUnificationPreferenceController.class)).handleActivityResult(i, i2, intent)) {
            return;
        }
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, DashboardFragment dashboardFragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new TrustAgentListPreferenceController(context, dashboardFragment, lifecycle));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new ChangeProfileScreenLockPreferenceController(context, dashboardFragment));
        arrayList2.add(new LockUnificationPreferenceController(context, dashboardFragment));
        arrayList2.add(new VisiblePatternProfilePreferenceController(context, lifecycle));
        arrayList2.add(new FaceProfileStatusPreferenceController(context, lifecycle));
        arrayList2.add(new FingerprintProfileStatusPreferenceController(context, lifecycle));
        arrayList2.add(new CombinedBiometricProfileStatusPreferenceController(context, lifecycle));
        arrayList.add(new PreferenceCategoryController(context, "security_category_profile").setChildren(arrayList2));
        arrayList.addAll(arrayList2);
        return arrayList;
    }
}
