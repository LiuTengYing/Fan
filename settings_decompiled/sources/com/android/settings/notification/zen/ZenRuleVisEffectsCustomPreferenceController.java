package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
/* loaded from: classes.dex */
public class ZenRuleVisEffectsCustomPreferenceController extends AbstractZenCustomRulePreferenceController {
    private SelectorWithWidgetPreference mPreference;

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public /* bridge */ /* synthetic */ boolean isAvailable() {
        return super.isAvailable();
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.lifecycle.events.OnResume
    public /* bridge */ /* synthetic */ void onResume() {
        super.onResume();
    }

    @Override // com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public /* bridge */ /* synthetic */ void setIdAndRule(String str, AutomaticZenRule automaticZenRule) {
        super.setIdAndRule(str, automaticZenRule);
    }

    public ZenRuleVisEffectsCustomPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SelectorWithWidgetPreference selectorWithWidgetPreference = (SelectorWithWidgetPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = selectorWithWidgetPreference;
        selectorWithWidgetPreference.setOnClickListener(new SelectorWithWidgetPreference.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleVisEffectsCustomPreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
            public final void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference2) {
                ZenRuleVisEffectsCustomPreferenceController.this.lambda$displayPreference$0(selectorWithWidgetPreference2);
            }
        });
        this.mPreference.setExtraWidgetOnClickListener(new View.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleVisEffectsCustomPreferenceController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ZenRuleVisEffectsCustomPreferenceController.this.lambda$displayPreference$1(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        launchCustomSettings();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(View view) {
        launchCustomSettings();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule;
        super.updateState(preference);
        if (this.mId == null || (automaticZenRule = this.mRule) == null || automaticZenRule.getZenPolicy() == null) {
            return;
        }
        this.mPreference.setChecked((this.mRule.getZenPolicy().shouldHideAllVisualEffects() || this.mRule.getZenPolicy().shouldShowAllVisualEffects()) ? false : true);
    }

    private void launchCustomSettings() {
        this.mMetricsFeatureProvider.action(this.mContext, 1398, Pair.create(1603, this.mId));
        new SubSettingLauncher(this.mContext).setDestination(ZenCustomRuleBlockedEffectsSettings.class.getName()).setArguments(createBundle()).setSourceMetricsCategory(1609).launch();
    }
}
