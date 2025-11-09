package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.widget.Switch;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class ZenAutomaticRuleSwitchPreferenceController extends AbstractZenModeAutomaticRulePreferenceController implements OnMainSwitchChangeListener {
    private String mId;
    private AutomaticZenRule mRule;
    private MainSwitchPreference mSwitchBar;

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_automatic_rule_switch";
    }

    public ZenAutomaticRuleSwitchPreferenceController(Context context, Fragment fragment, Lifecycle lifecycle) {
        super(context, "zen_automatic_rule_switch", fragment, lifecycle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIdAndRule(String str, AutomaticZenRule automaticZenRule) {
        this.mId = str;
        this.mRule = automaticZenRule;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (this.mRule == null || this.mId == null) ? false : true;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("zen_automatic_rule_switch");
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) findPreference;
        this.mSwitchBar = mainSwitchPreference;
        if (mainSwitchPreference != null) {
            mainSwitchPreference.setTitle(this.mContext.getString(R$string.zen_mode_use_automatic_rule));
            try {
                findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenAutomaticRuleSwitchPreferenceController$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$displayPreference$0;
                        lambda$displayPreference$0 = ZenAutomaticRuleSwitchPreferenceController.this.lambda$displayPreference$0(preference);
                        return lambda$displayPreference$0;
                    }
                });
                this.mSwitchBar.addOnSwitchChangeListener(this);
            } catch (IllegalStateException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Preference preference) {
        AutomaticZenRule automaticZenRule = this.mRule;
        automaticZenRule.setEnabled(!automaticZenRule.isEnabled());
        ((AbstractZenModeAutomaticRulePreferenceController) this).mBackend.updateZenRule(this.mId, this.mRule);
        return true;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModeAutomaticRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule != null) {
            this.mSwitchBar.updateStatus(automaticZenRule.isEnabled());
        }
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        if (z == this.mRule.isEnabled()) {
            return;
        }
        this.mRule.setEnabled(z);
        ((AbstractZenModeAutomaticRulePreferenceController) this).mBackend.updateZenRule(this.mId, this.mRule);
    }
}
