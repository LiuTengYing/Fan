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
public class FactoryBackupTestController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private boolean isChecked;
    private RestrictedSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "backup_test";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryBackupTestController(Context context, Lifecycle lifecycle) {
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
        boolean z = SystemProperties.getBoolean("sys.fyt.video.debug", false);
        this.isChecked = z;
        this.mPreference.setChecked(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (booleanValue) {
            SystemProperties.set("sys.fyt.video.debug", "true");
        } else {
            SystemProperties.set("sys.fyt.video.debug", "false");
        }
        this.isChecked = booleanValue;
        setChecked(booleanValue);
        return booleanValue;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        this.mPreference.setChecked(z);
    }
}
