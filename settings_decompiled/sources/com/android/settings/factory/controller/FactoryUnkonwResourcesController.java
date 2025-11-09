package com.android.settings.factory.controller;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class FactoryUnkonwResourcesController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private boolean isChecked;
    private RestrictedSwitchPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "unknowsource";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public FactoryUnkonwResourcesController(Context context, Lifecycle lifecycle) {
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
        setChecked(isNonMarketAppsAllowed());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.isChecked = booleanValue;
        setNonMarketAppsAllowed(booleanValue);
        if (booleanValue) {
            SettingsApplication settingsApplication = SettingsApplication.mApplication;
            Toast.makeText(settingsApplication, settingsApplication.getString(R$string.install_all_warning), 1).show();
        }
        setChecked(booleanValue);
        return false;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
        this.mPreference.setChecked(z);
    }

    private boolean isNonMarketAppsAllowed() {
        return Settings.Secure.getInt(SettingsApplication.mContentResolver, "install_non_market_apps", 0) > 0;
    }

    public void setNonMarketAppsAllowed(boolean z) {
        Settings.Secure.putInt(SettingsApplication.mContentResolver, "install_non_market_apps", z ? 1 : 0);
    }
}
