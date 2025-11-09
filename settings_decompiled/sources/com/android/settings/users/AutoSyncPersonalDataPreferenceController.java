package com.android.settings.users;

import android.content.Context;
import android.os.UserHandle;
import androidx.preference.PreferenceFragmentCompat;
/* loaded from: classes.dex */
public class AutoSyncPersonalDataPreferenceController extends AutoSyncDataPreferenceController {
    @Override // com.android.settings.users.AutoSyncDataPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_sync_personal_account_data";
    }

    public AutoSyncPersonalDataPreferenceController(Context context, PreferenceFragmentCompat preferenceFragmentCompat) {
        super(context, preferenceFragmentCompat);
    }

    @Override // com.android.settings.users.AutoSyncDataPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (this.mUserManager.isManagedProfile() || this.mUserManager.isLinkedUser() || this.mUserManager.getProfiles(UserHandle.myUserId()).size() <= 1) ? false : true;
    }
}
