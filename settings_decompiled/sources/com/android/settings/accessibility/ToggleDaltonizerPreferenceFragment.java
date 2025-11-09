package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.AccessibilitySettingsContentObserver;
import com.android.settings.accessibility.DaltonizerRadioButtonPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class ToggleDaltonizerPreferenceFragment extends ToggleFeaturePreferenceFragment implements DaltonizerRadioButtonPreferenceController.OnChangeListener {
    private static final List<AbstractPreferenceController> sControllers = new ArrayList();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accessibility_daltonizer_settings);

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 5;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        if (sControllers.size() == 0) {
            for (String str : context.getResources().getStringArray(R$array.daltonizer_mode_keys)) {
                sControllers.add(new DaltonizerRadioButtonPreferenceController(context, lifecycle, str));
            }
        }
        return sControllers;
    }

    @Override // com.android.settings.accessibility.DaltonizerRadioButtonPreferenceController.OnChangeListener
    public void onCheckedChanged(Preference preference) {
        for (AbstractPreferenceController abstractPreferenceController : sControllers) {
            abstractPreferenceController.updateState(preference);
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mComponentName = AccessibilityShortcutController.DALTONIZER_COMPONENT_NAME;
        this.mPackageName = getText(R$string.accessibility_display_daltonizer_preference_title);
        this.mHtmlDescription = getText(R$string.accessibility_display_daltonizer_preference_subtitle);
        this.mTopIntroTitle = getText(R$string.accessibility_daltonizer_about_intro_text);
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        updateFooterPreference();
        return onCreateView;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void registerKeysToObserverCallback(AccessibilitySettingsContentObserver accessibilitySettingsContentObserver) {
        super.registerKeysToObserverCallback(accessibilitySettingsContentObserver);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("accessibility_display_daltonizer_enabled");
        accessibilitySettingsContentObserver.registerKeysToObserverCallback(arrayList, new AccessibilitySettingsContentObserver.ContentObserverCallback() { // from class: com.android.settings.accessibility.ToggleDaltonizerPreferenceFragment$$ExternalSyntheticLambda0
            @Override // com.android.settings.accessibility.AccessibilitySettingsContentObserver.ContentObserverCallback
            public final void onChange(String str) {
                ToggleDaltonizerPreferenceFragment.this.lambda$registerKeysToObserverCallback$0(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerKeysToObserverCallback$0(String str) {
        updateSwitchBarToggleSwitch();
    }

    private void updateFooterPreference() {
        String string = getPrefContext().getString(R$string.accessibility_daltonizer_about_title);
        String string2 = getPrefContext().getString(R$string.accessibility_daltonizer_footer_learn_more_content_description);
        this.mFooterPreferenceController.setIntroductionTitle(string);
        this.mFooterPreferenceController.setupHelpLink(getHelpResource(), string2);
        this.mFooterPreferenceController.displayPreference(getPreferenceScreen());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected List<String> getPreferenceOrderList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("top_intro");
        arrayList.add("daltonizer_preview");
        arrayList.add("use_service");
        arrayList.add("daltonizer_mode_deuteranomaly");
        arrayList.add("daltonizer_mode_protanomaly");
        arrayList.add("daltonizer_mode_tritanomaly");
        arrayList.add("daltonizer_mode_grayscale");
        arrayList.add("general_categories");
        arrayList.add("html_description");
        return arrayList;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
        Iterator<AbstractPreferenceController> it = buildPreferenceControllers(getPrefContext(), getSettingsLifecycle()).iterator();
        while (it.hasNext()) {
            DaltonizerRadioButtonPreferenceController daltonizerRadioButtonPreferenceController = (DaltonizerRadioButtonPreferenceController) it.next();
            daltonizerRadioButtonPreferenceController.setOnChangeListener(this);
            daltonizerRadioButtonPreferenceController.displayPreference(getPreferenceScreen());
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        Iterator<AbstractPreferenceController> it = buildPreferenceControllers(getPrefContext(), getSettingsLifecycle()).iterator();
        while (it.hasNext()) {
            ((DaltonizerRadioButtonPreferenceController) it.next()).setOnChangeListener(null);
        }
        super.onPause();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_color_correction;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_daltonizer_settings;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onPreferenceToggled(String str, boolean z) {
        if (z == (Settings.Secure.getInt(getContentResolver(), "accessibility_display_daltonizer_enabled", 0) == 1)) {
            return;
        }
        if (z) {
            showQuickSettingsTooltipIfNeeded(1);
        }
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        Settings.Secure.putInt(getContentResolver(), "accessibility_display_daltonizer_enabled", z ? 1 : 0);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onRemoveSwitchPreferenceToggleSwitch() {
        super.onRemoveSwitchPreferenceToggleSwitch();
        this.mToggleServiceSwitchPreference.setOnPreferenceClickListener(null);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateToggleServiceTitle(SettingsMainSwitchPreference settingsMainSwitchPreference) {
        settingsMainSwitchPreference.setTitle(R$string.accessibility_daltonizer_primary_switch_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected CharSequence getShortcutTitle() {
        return getText(R$string.accessibility_daltonizer_shortcut_title);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    ComponentName getTileComponentName() {
        return AccessibilityShortcutController.DALTONIZER_TILE_COMPONENT_NAME;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    CharSequence getTileTooltipContent(int i) {
        int i2;
        if (i == 0) {
            i2 = R$string.accessibility_color_correction_qs_tooltip_content;
        } else {
            i2 = R$string.accessibility_color_correction_auto_added_qs_tooltip_content;
        }
        return getText(i2);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void updateSwitchBarToggleSwitch() {
        boolean z = Settings.Secure.getInt(getContentResolver(), "accessibility_display_daltonizer_enabled", 0) == 1;
        if (this.mToggleServiceSwitchPreference.isChecked() == z) {
            return;
        }
        this.mToggleServiceSwitchPreference.setChecked(z);
    }
}
