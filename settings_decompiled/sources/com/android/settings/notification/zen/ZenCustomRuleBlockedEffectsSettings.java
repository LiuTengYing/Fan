package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenCustomRuleBlockedEffectsSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1609;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    String getPreferenceCategoryKey() {
        return null;
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

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_block_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_intent", 0, 1332, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_light", 1, 1333, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_peek", 2, 1334, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_status", 3, 1335, new int[]{6}));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_badge", 4, 1336, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_ambient", 5, 1337, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_list", 6, 1338, null));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }
}
