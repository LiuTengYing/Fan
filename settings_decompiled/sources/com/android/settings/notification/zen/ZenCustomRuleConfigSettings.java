package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenCustomRuleConfigSettings extends ZenCustomRuleSettingsBase {
    private Preference mCallsPreference;
    private Preference mMessagesPreference;
    private Preference mNotificationsPreference;
    private ZenModeSettings.SummaryBuilder mSummaryBuilder;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1605;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    String getPreferenceCategoryKey() {
        return "zen_custom_rule_configuration_category";
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.support.actionbar.HelpResourceProvider
    public /* bridge */ /* synthetic */ int getHelpResource() {
        return super.getHelpResource();
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public /* bridge */ /* synthetic */ void updatePreferences() {
        super.updatePreferences();
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(this.mContext);
        Preference findPreference = getPreferenceScreen().findPreference("zen_rule_calls_settings");
        this.mCallsPreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenCustomRuleConfigSettings.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                new SubSettingLauncher(ZenCustomRuleConfigSettings.this.mContext).setDestination(ZenCustomRuleCallsSettings.class.getName()).setArguments(ZenCustomRuleConfigSettings.this.createZenRuleBundle()).setSourceMetricsCategory(1611).launch();
                return true;
            }
        });
        Preference findPreference2 = getPreferenceScreen().findPreference("zen_rule_messages_settings");
        this.mMessagesPreference = findPreference2;
        findPreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenCustomRuleConfigSettings.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                new SubSettingLauncher(ZenCustomRuleConfigSettings.this.mContext).setDestination(ZenCustomRuleMessagesSettings.class.getName()).setArguments(ZenCustomRuleConfigSettings.this.createZenRuleBundle()).setSourceMetricsCategory(1610).launch();
                return true;
            }
        });
        Preference findPreference3 = getPreferenceScreen().findPreference("zen_rule_notifications");
        this.mNotificationsPreference = findPreference3;
        findPreference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenCustomRuleConfigSettings.3
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                new SubSettingLauncher(ZenCustomRuleConfigSettings.this.mContext).setDestination(ZenCustomRuleNotificationsSettings.class.getName()).setArguments(ZenCustomRuleConfigSettings.this.createZenRuleBundle()).setSourceMetricsCategory(1608).launch();
                return true;
            }
        });
        updateSummaries();
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.notification.zen.ZenModeSettingsBase
    public void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
        updateSummaries();
    }

    private void updateSummaries() {
        NotificationManager.Policy notificationPolicy = this.mBackend.toNotificationPolicy(this.mRule.getZenPolicy());
        this.mCallsPreference.setSummary(this.mSummaryBuilder.getCallsSettingSummary(notificationPolicy));
        this.mMessagesPreference.setSummary(this.mSummaryBuilder.getMessagesSettingSummary(notificationPolicy));
        this.mNotificationsPreference.setSummary(this.mSummaryBuilder.getBlockedEffectsSummary(notificationPolicy));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_custom_rule_configuration;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_alarms", 5, 1226));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_media", 6, 1227));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_system", 7, 1340));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_reminders", 0, 167));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_events", 1, 168));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateSummaries();
    }
}
