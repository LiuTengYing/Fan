package com.android.settings.factory.controller;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class ProtocolTemperatureOutController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private boolean isChecked;
    private RestrictedSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "temperature_out";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ProtocolTemperatureOutController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Log.d("fangli", "onPreferenceChange: " + booleanValue);
        this.isChecked = booleanValue;
        SystemProperties.set("persist.syu.isshowouttemp", "" + booleanValue);
        setChecked(this.isChecked);
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = restrictedSwitchPreference;
        if (restrictedSwitchPreference == null) {
            return;
        }
        boolean z = SystemProperties.getBoolean("persist.syu.isshowouttemp", true);
        this.isChecked = z;
        setChecked(z);
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        this.mPreference.setChecked(z);
    }
}
