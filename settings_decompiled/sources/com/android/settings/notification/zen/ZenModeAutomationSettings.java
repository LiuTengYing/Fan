package com.android.settings.notification.zen;

import android.app.AlertDialog;
import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.utils.ZenServiceListing;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class ZenModeAutomationSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.zen_mode_automation_settings) { // from class: com.android.settings.notification.zen.ZenModeAutomationSettings.3
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add("zen_mode_add_automatic_rule");
            nonIndexableKeys.add("zen_mode_automatic_rules");
            return nonIndexableKeys;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeAutomationSettings.buildPreferenceControllers(context, null, null, null);
        }
    };
    protected final ManagedServiceSettings.Config CONFIG = getConditionProviderConfig();
    private final int DELETE_RULES = 1;
    private boolean[] mDeleteDialogChecked;
    private String[] mDeleteDialogRuleIds;
    private CharSequence[] mDeleteDialogRuleNames;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 142;
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("DELETE_RULE")) {
            return;
        }
        this.mBackend.removeZenRule(arguments.getString("DELETE_RULE"));
        arguments.remove("DELETE_RULE");
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ZenServiceListing zenServiceListing = new ZenServiceListing(getContext(), this.CONFIG);
        zenServiceListing.reloadApprovedServices();
        return buildPreferenceControllers(context, this, zenServiceListing, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Fragment fragment, ZenServiceListing zenServiceListing, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeAddAutomaticRulePreferenceController(context, fragment, zenServiceListing, lifecycle));
        arrayList.add(new ZenModeAutomaticRulesPreferenceController(context, fragment, lifecycle));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_automation_settings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ManagedServiceSettings.Config getConditionProviderConfig() {
        return new ManagedServiceSettings.Config.Builder().setTag("ZenModeSettings").setIntentAction("android.service.notification.ConditionProviderService").setConfigurationIntentAction("android.app.action.AUTOMATIC_ZEN_RULE").setPermission("android.permission.BIND_CONDITION_PROVIDER_SERVICE").setNoun("condition provider").build();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 1, 0, R$string.zen_mode_delete_automatic_rules);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            final Map.Entry<String, AutomaticZenRule>[] automaticZenRules = this.mBackend.getAutomaticZenRules();
            this.mDeleteDialogRuleNames = new CharSequence[automaticZenRules.length];
            this.mDeleteDialogRuleIds = new String[automaticZenRules.length];
            this.mDeleteDialogChecked = new boolean[automaticZenRules.length];
            for (int i = 0; i < automaticZenRules.length; i++) {
                this.mDeleteDialogRuleNames[i] = automaticZenRules[i].getValue().getName();
                this.mDeleteDialogRuleIds[i] = automaticZenRules[i].getKey();
            }
            new AlertDialog.Builder(this.mContext).setTitle(R$string.zen_mode_delete_automatic_rules).setMultiChoiceItems(this.mDeleteDialogRuleNames, (boolean[]) null, new DialogInterface.OnMultiChoiceClickListener() { // from class: com.android.settings.notification.zen.ZenModeAutomationSettings.2
                @Override // android.content.DialogInterface.OnMultiChoiceClickListener
                public void onClick(DialogInterface dialogInterface, int i2, boolean z) {
                    ZenModeAutomationSettings.this.mDeleteDialogChecked[i2] = z;
                }
            }).setPositiveButton(R$string.zen_mode_schedule_delete, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.zen.ZenModeAutomationSettings.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    for (int i3 = 0; i3 < automaticZenRules.length; i3++) {
                        if (ZenModeAutomationSettings.this.mDeleteDialogChecked[i3]) {
                            ZenModeAutomationSettings zenModeAutomationSettings = ZenModeAutomationSettings.this;
                            zenModeAutomationSettings.mBackend.removeZenRule(zenModeAutomationSettings.mDeleteDialogRuleIds[i3]);
                        }
                    }
                }
            }).show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
