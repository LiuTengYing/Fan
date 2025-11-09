package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SeekBarPreference;
import com.android.settings.widget.SettingsMainSwitchPreference;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ToggleReduceBrightColorsPreferenceFragment extends ToggleFeaturePreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.reduce_bright_colors_settings) { // from class: com.android.settings.accessibility.ToggleReduceBrightColorsPreferenceFragment.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ColorDisplayManager.isReduceBrightColorsAvailable(context);
        }
    };
    private ColorDisplayManager mColorDisplayManager;
    private ReduceBrightColorsIntensityPreferenceController mRbcIntensityPreferenceController;
    private ReduceBrightColorsPersistencePreferenceController mRbcPersistencePreferenceController;

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1853;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(getPrefContext().getPackageName()).appendPath(String.valueOf(R$raw.extra_dim_banner)).build();
        this.mComponentName = AccessibilityShortcutController.REDUCE_BRIGHT_COLORS_COMPONENT_NAME;
        this.mPackageName = getText(R$string.reduce_bright_colors_preference_title);
        this.mHtmlDescription = getText(R$string.reduce_bright_colors_preference_subtitle);
        this.mTopIntroTitle = getText(R$string.reduce_bright_colors_preference_intro_text);
        this.mRbcIntensityPreferenceController = new ReduceBrightColorsIntensityPreferenceController(getContext(), "rbc_intensity");
        this.mRbcPersistencePreferenceController = new ReduceBrightColorsPersistencePreferenceController(getContext(), "rbc_persist");
        this.mRbcIntensityPreferenceController.displayPreference(getPreferenceScreen());
        this.mRbcPersistencePreferenceController.displayPreference(getPreferenceScreen());
        this.mColorDisplayManager = (ColorDisplayManager) getContext().getSystemService(ColorDisplayManager.class);
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mToggleServiceSwitchPreference.setTitle(R$string.reduce_bright_colors_switch_title);
        updateGeneralCategoryOrder();
        updateFooterPreference();
        return onCreateView;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void registerKeysToObserverCallback(AccessibilitySettingsContentObserver accessibilitySettingsContentObserver) {
        super.registerKeysToObserverCallback(accessibilitySettingsContentObserver);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("reduce_bright_colors_activated");
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(arrayList, new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.ToggleReduceBrightColorsPreferenceFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                ToggleReduceBrightColorsPreferenceFragment.this.lambda$registerKeysToObserverCallback$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerKeysToObserverCallback$0(String str) {
        updateSwitchBarToggleSwitch();
    }

    private void updateGeneralCategoryOrder() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("general_categories");
        SeekBarPreference seekBarPreference = (SeekBarPreference) findPreference("rbc_intensity");
        getPreferenceScreen().removePreference(seekBarPreference);
        seekBarPreference.setOrder(this.mShortcutPreference.getOrder() - 2);
        preferenceCategory.addPreference(seekBarPreference);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("rbc_persist");
        getPreferenceScreen().removePreference(switchPreference);
        switchPreference.setOrder(this.mShortcutPreference.getOrder() - 1);
        preferenceCategory.addPreference(switchPreference);
    }

    private void updateFooterPreference() {
        this.mFooterPreferenceController.setIntroductionTitle(getPrefContext().getString(R$string.reduce_bright_colors_about_title));
        this.mFooterPreferenceController.displayPreference(getPreferenceScreen());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.reduce_bright_colors_settings;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onPreferenceToggled(String str, boolean z) {
        if (z) {
            showQuickSettingsTooltipIfNeeded(1);
        }
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        this.mColorDisplayManager.setReduceBrightColorsActivated(z);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onRemoveSwitchPreferenceToggleSwitch() {
        super.onRemoveSwitchPreferenceToggleSwitch();
        this.mToggleServiceSwitchPreference.setOnPreferenceClickListener(null);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        settingsMainSwitchPreference.setTitle(R$string.reduce_bright_colors_preference_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected CharSequence getShortcutTitle() {
        return getText(R$string.reduce_bright_colors_shortcut_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    ComponentName getTileComponentName() {
        return AccessibilityShortcutController.REDUCE_BRIGHT_COLORS_TILE_SERVICE_COMPONENT_NAME;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    CharSequence getTileTooltipContent(int i) {
        int i2;
        if (i == 0) {
            i2 = R$string.accessibility_reduce_bright_colors_qs_tooltip_content;
        } else {
            i2 = R$string.accessibility_reduce_bright_colors_auto_added_qs_tooltip_content;
        }
        return getText(i2);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateSwitchBarToggleSwitch() {
        boolean isReduceBrightColorsActivated = this.mColorDisplayManager.isReduceBrightColorsActivated();
        this.mRbcIntensityPreferenceController.updateState(getPreferenceScreen().findPreference("rbc_intensity"));
        this.mRbcPersistencePreferenceController.updateState(getPreferenceScreen().findPreference("rbc_persist"));
        if (this.mToggleServiceSwitchPreference.isChecked() != isReduceBrightColorsActivated) {
            this.mToggleServiceSwitchPreference.setChecked(isReduceBrightColorsActivated);
        }
    }
}
