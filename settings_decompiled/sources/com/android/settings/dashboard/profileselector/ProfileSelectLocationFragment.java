package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.location.LocationPersonalSettings;
import com.android.settings.location.LocationSwitchBarController;
import com.android.settings.location.LocationWorkProfileSettings;
import com.android.settings.widget.SettingsMainSwitchBar;
/* loaded from: classes.dex */
public class ProfileSelectLocationFragment extends ProfileSelectFragment {
    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        switchBar.setTitle(getContext().getString(R$string.location_settings_primary_switch_title));
        new LocationSwitchBarController(settingsActivity, switchBar, getSettingsLifecycle());
        switchBar.show();
    }

    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 2);
        LocationWorkProfileSettings locationWorkProfileSettings = new LocationWorkProfileSettings();
        locationWorkProfileSettings.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 1);
        LocationPersonalSettings locationPersonalSettings = new LocationPersonalSettings();
        locationPersonalSettings.setArguments(bundle2);
        return new Fragment[]{locationPersonalSettings, locationWorkProfileSettings};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.location_settings_header;
    }
}
