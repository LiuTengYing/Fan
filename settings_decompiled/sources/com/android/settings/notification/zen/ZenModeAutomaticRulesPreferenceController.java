package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.util.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.Map;
/* loaded from: classes.dex */
public class ZenModeAutomaticRulesPreferenceController extends AbstractZenModeAutomaticRulePreferenceController {
    protected PreferenceCategory mPreferenceCategory;
    protected Map<String, ZenRulePreference> mZenRulePreferences;

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_automatic_rules";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeAutomaticRulesPreferenceController(Context context, Fragment fragment, Lifecycle lifecycle) {
        super(context, "zen_mode_automatic_rules", fragment, lifecycle);
        this.mZenRulePreferences = new ArrayMap();
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setPersistent(false);
        if (this.mPreferenceCategory.getPreferenceCount() == 0) {
            this.mZenRulePreferences.clear();
        }
    }

    @Override // com.android.settings.notification.zen.AbstractZenModeAutomaticRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Map.Entry<String, AutomaticZenRule>[] rules = getRules();
        boolean z = true;
        if (this.mPreferenceCategory.getPreferenceCount() == rules.length) {
            int i = 0;
            while (true) {
                if (i >= rules.length) {
                    z = false;
                    break;
                } else if (!this.mZenRulePreferences.containsKey(rules[i].getKey())) {
                    break;
                } else {
                    i++;
                }
            }
        }
        ArrayMap arrayMap = new ArrayMap();
        if (z) {
            this.mPreferenceCategory.removeAll();
        }
        for (int i2 = 0; i2 < rules.length; i2++) {
            String key = rules[i2].getKey();
            if (this.mZenRulePreferences.containsKey(key)) {
                ZenRulePreference zenRulePreference = this.mZenRulePreferences.get(key);
                zenRulePreference.updatePreference(rules[i2].getValue());
                if (z) {
                    this.mPreferenceCategory.addPreference(zenRulePreference);
                    arrayMap.put(key, zenRulePreference);
                }
            } else {
                ZenRulePreference createZenRulePreference = createZenRulePreference(rules[i2]);
                this.mPreferenceCategory.addPreference(createZenRulePreference);
                arrayMap.put(key, createZenRulePreference);
            }
        }
        if (z) {
            this.mZenRulePreferences = arrayMap;
        }
    }

    ZenRulePreference createZenRulePreference(Map.Entry<String, AutomaticZenRule> entry) {
        return new ZenRulePreference(this.mPreferenceCategory.getContext(), entry, this.mParent, this.mMetricsFeatureProvider);
    }
}
