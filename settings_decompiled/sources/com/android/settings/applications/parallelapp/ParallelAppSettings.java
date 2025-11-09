package com.android.settings.applications.parallelapp;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class ParallelAppSettings extends DashboardFragment {
    private IActivityManager mAms;
    PreferenceGroup mAppList;
    private int mCloneUserId = -1;
    private ApplicationsState.AppFilter mFilter;
    private UserManager mUm;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ParallelAppSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUm = (UserManager) context.getSystemService("user");
        this.mFilter = ApplicationsState.FILTER_ALL_ENABLED;
        ((ManageParallelAppsPreferenceController) use(ManageParallelAppsPreferenceController.class)).setSession(getSettingsLifecycle());
        ((ManageParallelAppsPreferenceController) use(ManageParallelAppsPreferenceController.class)).setParentFragment(this);
        ((ManageParallelAppsPreferenceController) use(ManageParallelAppsPreferenceController.class)).setFilter(this.mFilter);
        this.mAms = ActivityManager.getService();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAppList = (PreferenceGroup) findPreference("app_list");
        ((ManageParallelAppsPreferenceController) use(ManageParallelAppsPreferenceController.class)).setPreferenceGroup(this.mAppList);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        setLoading(true, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.parallel_app_settings;
    }

    public void onLoadFinish() {
        setLoading(false, false);
    }
}
