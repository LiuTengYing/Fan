package com.android.settings.dashboard.profileselector;

import android.util.ArrayMap;
import com.android.settings.accounts.AccountDashboardFragment;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.deviceinfo.StorageDashboardFragment;
import com.android.settings.inputmethod.AvailableVirtualKeyboardFragment;
import com.android.settings.location.LocationServices;
import java.util.Map;
/* loaded from: classes.dex */
public class ProfileFragmentBridge {
    public static final Map<String, String> FRAGMENT_MAP;

    static {
        ArrayMap arrayMap = new ArrayMap();
        FRAGMENT_MAP = arrayMap;
        arrayMap.put(AccountDashboardFragment.class.getName(), ProfileSelectAccountFragment.class.getName());
        arrayMap.put(ManageApplications.class.getName(), ProfileSelectManageApplications.class.getName());
        arrayMap.put(LocationServices.class.getName(), ProfileSelectLocationServicesFragment.class.getName());
        arrayMap.put(StorageDashboardFragment.class.getName(), ProfileSelectStorageFragment.class.getName());
        arrayMap.put(AvailableVirtualKeyboardFragment.class.getName(), ProfileSelectKeyboardFragment.class.getName());
    }
}
