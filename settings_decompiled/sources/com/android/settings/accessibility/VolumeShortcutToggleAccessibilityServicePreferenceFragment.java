package com.android.settings.accessibility;

import android.os.Bundle;
import android.view.View;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class VolumeShortcutToggleAccessibilityServicePreferenceFragment extends ToggleAccessibilityServicePreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mShortcutPreference.setSummary(getPrefContext().getText(R$string.accessibility_shortcut_edit_dialog_title_hardware));
        this.mShortcutPreference.setSettingsEditable(false);
        setAllowedPreferredShortcutType(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        int userShortcutTypes = super.getUserShortcutTypes();
        return (((getAccessibilityServiceInfo().flags & 256) != 0) && getArguments().getBoolean("checked")) ? userShortcutTypes | 1 : userShortcutTypes & (-2);
    }

    private void setAllowedPreferredShortcutType(int i) {
        PreferredShortcuts.saveUserShortcutType(getPrefContext(), new PreferredShortcut(this.mComponentName.flattenToString(), i));
    }
}
