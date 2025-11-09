package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenCustomRuleSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1604;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    String getPreferenceCategoryKey() {
        return "zen_custom_rule_category";
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.support.actionbar.HelpResourceProvider
    public /* bridge */ /* synthetic */ int getHelpResource() {
        return super.getHelpResource();
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onResume() {
        super.onResume();
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.notification.zen.ZenModeSettingsBase
    public /* bridge */ /* synthetic */ void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public /* bridge */ /* synthetic */ void updatePreferences() {
        super.updatePreferences();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_custom_rule_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleDefaultPolicyPreferenceController(context, getSettingsLifecycle(), "zen_custom_rule_setting_default"));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomPolicyPreferenceController(context, getSettingsLifecycle(), "zen_custom_rule_setting"));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }
}
