package com.android.settings.accessibility;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.google.android.setupdesign.GlifPreferenceLayout;
/* loaded from: classes.dex */
public class ToggleScreenMagnificationPreferenceFragmentForSetupWizard extends ToggleScreenMagnificationPreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    @Override // com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 368;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        String string = getContext().getString(R$string.accessibility_screen_magnification_title);
        String string2 = getContext().getString(R$string.accessibility_screen_magnification_intro_text);
        Drawable drawable = getContext().getDrawable(R$drawable.ic_accessibility_visibility);
        AccessibilitySetupWizardUtils.updateGlifPreferenceLayout(getContext(), (GlifPreferenceLayout) view, string, string2, drawable);
        hidePreferenceSettingComponents();
    }

    private void hidePreferenceSettingComponents() {
        this.mTopIntroPreference.setVisible(false);
        Preference preference = this.mSettingsPreference;
        if (preference != null) {
            preference.setVisible(false);
        }
        SwitchPreference switchPreference = this.mFollowingTypingSwitchPreference;
        if (switchPreference != null) {
            switchPreference.setVisible(false);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return ((GlifPreferenceLayout) viewGroup).onCreateRecyclerView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("checked") && this.mToggleServiceSwitchPreference.isChecked() != arguments.getBoolean("checked")) {
            this.mMetricsFeatureProvider.action(getContext(), 368, this.mToggleServiceSwitchPreference.isChecked());
        }
        super.onStop();
    }
}
