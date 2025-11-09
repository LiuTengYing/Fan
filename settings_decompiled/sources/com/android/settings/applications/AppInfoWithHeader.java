package com.android.settings.applications;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
/* loaded from: classes.dex */
public abstract class AppInfoWithHeader extends AppInfoBase {
    private boolean mCreated;

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mCreated) {
            Log.w("AppInfoWithHeader", "onActivityCreated: ignoring duplicate call");
            return;
        }
        this.mCreated = true;
        if (this.mPackageInfo == null) {
            return;
        }
        FragmentActivity activity = getActivity();
        getPreferenceScreen().addPreference(EntityHeaderController.newInstance(activity, this, null).setRecyclerView(getListView(), getSettingsLifecycle()).setIcon(Utils.getBadgedIcon(getContext(), this.mPackageInfo.applicationInfo)).setLabel(this.mPackageInfo.applicationInfo.loadLabel(this.mPm)).setSummary(this.mPackageInfo).setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo)).setPackageName(this.mPackageName).setUid(this.mPackageInfo.applicationInfo.uid).setHasAppInfoLink(true).setButtonActions(0, 0).done(activity, getPrefContext()));
    }
}
