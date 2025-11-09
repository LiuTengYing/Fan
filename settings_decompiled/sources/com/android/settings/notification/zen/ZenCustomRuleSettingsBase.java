package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
abstract class ZenCustomRuleSettingsBase extends ZenModeSettingsBase {
    String mId;
    AutomaticZenRule mRule;
    List<AbstractPreferenceController> mControllers = new ArrayList();
    private boolean mIsFirstLaunch = true;

    abstract String getPreferenceCategoryKey();

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("RULE_ID")) {
            this.mId = arguments.getString("RULE_ID");
            updateRule();
            return;
        }
        Log.d("ZenCustomRuleSettings", "Rule id required to set custom dnd rule config settings");
        finish();
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        if (!this.mIsFirstLaunch) {
            updateRule();
        }
        super.onResume();
        updatePreferences();
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase
    public void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
        updateRule();
        updatePreferences();
        updatePreferenceStates();
    }

    private void updateRule() {
        this.mRule = this.mBackend.getAutomaticZenRule(this.mId);
        Iterator<AbstractPreferenceController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            ((AbstractZenCustomRulePreferenceController) it.next()).setIdAndRule(this.mId, this.mRule);
        }
    }

    public void updatePreferences() {
        Preference findPreference;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        String preferenceCategoryKey = getPreferenceCategoryKey();
        if (preferenceCategoryKey != null && (findPreference = preferenceScreen.findPreference(preferenceCategoryKey)) != null) {
            findPreference.setTitle(this.mContext.getResources().getString(R$string.zen_mode_custom_behavior_category_title, this.mRule.getName()));
        }
        Iterator<AbstractPreferenceController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            AbstractZenCustomRulePreferenceController abstractZenCustomRulePreferenceController = (AbstractZenCustomRulePreferenceController) it.next();
            abstractZenCustomRulePreferenceController.onResume();
            if (!this.mIsFirstLaunch) {
                abstractZenCustomRulePreferenceController.displayPreference(preferenceScreen);
            }
        }
        this.mIsFirstLaunch = false;
    }

    public int getHelpResource() {
        return R$string.help_uri_interruptions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bundle createZenRuleBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("RULE_ID", this.mId);
        return bundle;
    }
}
