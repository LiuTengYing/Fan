package com.android.settings.factory.controller;

import android.content.Context;
import android.os.SystemProperties;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class ProtocolUpAndDownSongController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private boolean isChecked;
    private RestrictedSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "up_and_down_song_swap";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ProtocolUpAndDownSongController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = restrictedSwitchPreference;
        if (restrictedSwitchPreference == null) {
            return;
        }
        boolean z = SystemProperties.getInt("persist.fyt.canbus_key_next", 0) == 1;
        this.isChecked = z;
        setChecked(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.isChecked = booleanValue;
        SystemProperties.set("persist.fyt.canbus_key_next", booleanValue ? "1" : "0");
        setChecked(this.isChecked);
        return false;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        this.mPreference.setChecked(z);
    }
}
