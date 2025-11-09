package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.R$xml;
import com.android.settings.inputmethod.AvailableVirtualKeyboardFragment;
/* loaded from: classes.dex */
public final class ProfileSelectKeyboardFragment extends ProfileSelectFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.available_virtual_keyboard;
    }

    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt("profile", 1);
        AvailableVirtualKeyboardFragment availableVirtualKeyboardFragment = new AvailableVirtualKeyboardFragment();
        availableVirtualKeyboardFragment.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("profile", 2);
        AvailableVirtualKeyboardFragment availableVirtualKeyboardFragment2 = new AvailableVirtualKeyboardFragment();
        availableVirtualKeyboardFragment2.setArguments(bundle2);
        return new Fragment[]{availableVirtualKeyboardFragment, availableVirtualKeyboardFragment2};
    }
}
