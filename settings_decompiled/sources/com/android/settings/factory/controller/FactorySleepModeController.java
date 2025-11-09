package com.android.settings.factory.controller;

import android.content.Context;
import android.content.res.Resources;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.ipc.IpcObj;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactorySleepModeController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private boolean isChecked;
    private RestrictedSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "sleep_mode";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactorySleepModeController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Resources resources;
        int i;
        boolean booleanValue = ((Boolean) obj).booleanValue();
        IpcObj.getInstance().setCmd(0, 15, booleanValue ? 1 : 0);
        this.isChecked = booleanValue;
        setChecked(booleanValue);
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (this.isChecked) {
            resources = SettingsApplication.mResources;
            i = R$string.factory_sleep_mode_sleep;
        } else {
            resources = SettingsApplication.mResources;
            i = R$string.factory_sleep_mode_outage;
        }
        restrictedSwitchPreference.setState(resources.getString(i));
        return booleanValue;
    }

    public void setChecked(boolean z) {
        Resources resources;
        int i;
        this.isChecked = z;
        this.mPreference.setChecked(z);
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (this.isChecked) {
            resources = SettingsApplication.mResources;
            i = R$string.factory_sleep_mode_sleep;
        } else {
            resources = SettingsApplication.mResources;
            i = R$string.factory_sleep_mode_outage;
        }
        restrictedSwitchPreference.setState(resources.getString(i));
    }
}
