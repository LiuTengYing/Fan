package com.android.settings.notification.zen;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
/* loaded from: classes.dex */
public class ZenModeConversationsPreferenceController extends AbstractZenModePreferenceController {
    private final ZenModeBackend mBackend;
    private Preference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(this.KEY);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int zenMode = getZenMode();
        if (zenMode == 2 || zenMode == 3) {
            this.mPreference.setEnabled(false);
            this.mPreference.setSummary(this.mBackend.getAlarmsTotalSilencePeopleSummary(256));
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(this.mBackend.getConversationSummary());
    }
}
