package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenCustomRuleCallsSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1611;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    String getPreferenceCategoryKey() {
        return "zen_mode_settings_category_calls";
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

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_custom_rule_calls_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleCallsPreferenceController(context, "zen_mode_calls", getSettingsLifecycle()));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleRepeatCallersPreferenceController(context, "zen_mode_repeat_callers", getSettingsLifecycle(), context.getResources().getInteger(17694966)));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleStarredContactsPreferenceController(context, getSettingsLifecycle(), 3, "zen_mode_starred_contacts_callers"));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public void updatePreferences() {
        super.updatePreferences();
        getPreferenceScreen().findPreference("footer_preference").setTitle(this.mContext.getResources().getString(R$string.zen_mode_custom_calls_footer, this.mRule.getName()));
    }
}
