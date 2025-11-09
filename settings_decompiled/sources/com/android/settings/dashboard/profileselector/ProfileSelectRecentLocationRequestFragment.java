package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.R$xml;
import com.android.settings.location.RecentLocationRequestSeeAllFragment;
/* loaded from: classes.dex */
public class ProfileSelectRecentLocationRequestFragment extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 2);
        RecentLocationRequestSeeAllFragment recentLocationRequestSeeAllFragment = new RecentLocationRequestSeeAllFragment();
        recentLocationRequestSeeAllFragment.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 1);
        RecentLocationRequestSeeAllFragment recentLocationRequestSeeAllFragment2 = new RecentLocationRequestSeeAllFragment();
        recentLocationRequestSeeAllFragment2.setArguments(bundle2);
        return new Fragment[]{recentLocationRequestSeeAllFragment2, recentLocationRequestSeeAllFragment};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.location_recent_requests_header;
    }
}
