package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.R$xml;
import com.android.settings.location.LocationServices;
import com.android.settings.location.LocationServicesForWork;
/* loaded from: classes.dex */
public class ProfileSelectLocationServicesFragment extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    protected boolean forceUpdateHeight() {
        return true;
    }

    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 2);
        LocationServicesForWork locationServicesForWork = new LocationServicesForWork();
        locationServicesForWork.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 1);
        LocationServices locationServices = new LocationServices();
        locationServices.setArguments(bundle2);
        return new Fragment[]{locationServices, locationServicesForWork};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.location_services_header;
    }
}
