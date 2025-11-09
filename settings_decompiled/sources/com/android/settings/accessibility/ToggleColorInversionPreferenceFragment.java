package com.android.settings.accessibility;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.widget.SettingsMainSwitchPreference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ToggleColorInversionPreferenceFragment extends ToggleFeaturePreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1817;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onPreferenceToggled(String str, boolean z) {
        if (z == (Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1)) {
            return;
        }
        if (z) {
            showQuickSettingsTooltipIfNeeded(1);
        }
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        Settings.Secure.putInt(getContentResolver(), "accessibility_display_inversion_enabled", z ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_color_inversion_settings;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onRemoveSwitchPreferenceToggleSwitch() {
        super.onRemoveSwitchPreferenceToggleSwitch();
        this.mToggleServiceSwitchPreference.setOnPreferenceClickListener(null);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        settingsMainSwitchPreference.setTitle(R$string.accessibility_display_inversion_switch_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected CharSequence getShortcutTitle() {
        return getText(R$string.accessibility_display_inversion_shortcut_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mComponentName = AccessibilityShortcutController.COLOR_INVERSION_COMPONENT_NAME;
        this.mPackageName = getText(R$string.accessibility_display_inversion_preference_title);
        this.mHtmlDescription = getText(R$string.accessibility_display_inversion_preference_subtitle);
        this.mTopIntroTitle = getText(R$string.accessibility_display_inversion_preference_intro_text);
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(getPrefContext().getPackageName()).appendPath(String.valueOf(R$raw.accessibility_color_inversion_banner)).build();
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        updateFooterPreference();
        return onCreateView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void registerKeysToObserverCallback(AccessibilitySettingsContentObserver accessibilitySettingsContentObserver) {
        super.registerKeysToObserverCallback(accessibilitySettingsContentObserver);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("accessibility_display_inversion_enabled");
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(arrayList, new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.ToggleColorInversionPreferenceFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                ToggleColorInversionPreferenceFragment.this.lambda$registerKeysToObserverCallback$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerKeysToObserverCallback$0(String str) {
        updateSwitchBarToggleSwitch();
    }

    private void updateFooterPreference() {
        String string = getPrefContext().getString(R$string.accessibility_color_inversion_about_title);
        String string2 = getPrefContext().getString(R$string.accessibility_color_inversion_footer_learn_more_content_description);
        this.mFooterPreferenceController.setIntroductionTitle(string);
        this.mFooterPreferenceController.setupHelpLink(getHelpResource(), string2);
        this.mFooterPreferenceController.displayPreference(getPreferenceScreen());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_color_inversion;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    ComponentName getTileComponentName() {
        return AccessibilityShortcutController.COLOR_INVERSION_TILE_COMPONENT_NAME;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    CharSequence getTileTooltipContent(int i) {
        int i2;
        if (i == 0) {
            i2 = R$string.accessibility_color_inversion_qs_tooltip_content;
        } else {
            i2 = R$string.accessibility_color_inversion_auto_added_qs_tooltip_content;
        }
        return getText(i2);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateSwitchBarToggleSwitch() {
        boolean z = Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1;
        if (this.mToggleServiceSwitchPreference.isChecked() == z) {
            return;
        }
        this.mToggleServiceSwitchPreference.setChecked(z);
    }
}
