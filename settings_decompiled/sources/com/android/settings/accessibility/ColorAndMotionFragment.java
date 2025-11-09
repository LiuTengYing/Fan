package com.android.settings.accessibility;

import android.hardware.display.ColorDisplayManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.sprdpower.IPowerManagerEx;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class ColorAndMotionFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER;
    private static final boolean SUPPORT_SUPER_POWER_SAVE;
    private Preference mDisplayDaltonizerPreferenceScreen;
    private Preference mToggleColorInversionPreference;
    private SwitchPreference mToggleDisableAnimationsPreference;
    private SwitchPreference mToggleLargePointerIconPreference;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ColorAndMotionFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1918;
    }

    static {
        SUPPORT_SUPER_POWER_SAVE = 1 == SystemProperties.getInt("ro.sys.pwctl.ultrasaving", 0);
        SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accessibility_color_and_motion);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initializeAllPreferences();
        updateSystemPreferences();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_color_and_motion;
    }

    private void initializeAllPreferences() {
        this.mToggleColorInversionPreference = findPreference("toggle_inversion_preference");
        this.mDisplayDaltonizerPreferenceScreen = findPreference("daltonizer_preference");
        this.mToggleDisableAnimationsPreference = (SwitchPreference) findPreference("toggle_disable_animations");
        this.mToggleLargePointerIconPreference = (SwitchPreference) findPreference("toggle_large_pointer_icon");
    }

    private int getPowerSaveMode() {
        try {
            return IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex")).getPowerSaveMode();
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        } catch (NullPointerException e2) {
            e2.printStackTrace();
            return -1;
        }
    }

    private void updateSystemPreferences() {
        PreferenceCategory preferenceCategory = (PreferenceCategory) getPreferenceScreen().findPreference("experimental_category");
        if (ColorDisplayManager.isColorTransformAccelerated(getContext())) {
            this.mDisplayDaltonizerPreferenceScreen.setSummary(AccessibilityUtil.getSummary(getContext(), "accessibility_display_daltonizer_enabled"));
            getPreferenceScreen().removePreference(preferenceCategory);
        } else {
            getPreferenceScreen().removePreference(this.mDisplayDaltonizerPreferenceScreen);
            getPreferenceScreen().removePreference(this.mToggleDisableAnimationsPreference);
            getPreferenceScreen().removePreference(this.mToggleLargePointerIconPreference);
            preferenceCategory.addPreference(this.mDisplayDaltonizerPreferenceScreen);
            preferenceCategory.addPreference(this.mToggleDisableAnimationsPreference);
            preferenceCategory.addPreference(this.mToggleLargePointerIconPreference);
        }
        boolean z = SUPPORT_SUPER_POWER_SAVE && getPowerSaveMode() == 4;
        Preference preference = this.mToggleColorInversionPreference;
        if (preference != null) {
            preference.setEnabled(!z);
        }
    }
}
