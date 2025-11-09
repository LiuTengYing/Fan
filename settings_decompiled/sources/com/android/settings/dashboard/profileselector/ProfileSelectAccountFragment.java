package com.android.settings.dashboard.profileselector;

import androidx.fragment.app.Fragment;
import com.android.settings.R$xml;
import com.android.settings.accounts.AccountPersonalDashboardFragment;
import com.android.settings.accounts.AccountWorkProfileDashboardFragment;
/* loaded from: classes.dex */
public class ProfileSelectAccountFragment extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        return new Fragment[]{new AccountPersonalDashboardFragment(), new AccountWorkProfileDashboardFragment()};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accounts_dashboard_settings_header;
    }
}
