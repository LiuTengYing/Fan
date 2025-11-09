package com.android.settingslib.development;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public abstract class DeveloperOptionsPreferenceController extends AbstractPreferenceController {
    protected Preference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public DeveloperOptionsPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onDeveloperOptionsEnabled() {
        if (isAvailable()) {
            onDeveloperOptionsSwitchEnabled();
        }
    }

    public void onDeveloperOptionsDisabled() {
        if (isAvailable()) {
            onDeveloperOptionsSwitchDisabled();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchEnabled() {
        this.mPreference.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchDisabled() {
        this.mPreference.setEnabled(false);
    }
}
