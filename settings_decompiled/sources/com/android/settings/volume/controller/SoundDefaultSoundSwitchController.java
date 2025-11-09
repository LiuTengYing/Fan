package com.android.settings.volume.controller;

import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class SoundDefaultSoundSwitchController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private boolean isChecked;
    private RestrictedSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "defaultsound_switch";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        IpcObj.getInstance().setCmd(4, 12, booleanValue ? 1 : 0);
        this.isChecked = booleanValue;
        setChecked(booleanValue);
        return booleanValue;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        this.mPreference.setChecked(z);
    }
}
