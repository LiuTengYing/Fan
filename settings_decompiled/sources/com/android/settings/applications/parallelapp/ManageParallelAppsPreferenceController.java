package com.android.settings.applications.parallelapp;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$bool;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
/* loaded from: classes.dex */
public class ManageParallelAppsPreferenceController extends BasePreferenceController implements ApplicationsState.Callbacks, Preference.OnPreferenceChangeListener {
    PreferenceGroup mAppList;
    private final ApplicationsState mApplicationsState;
    private Context mContext;
    private ApplicationsState.AppFilter mFilter;
    private final PackageInstaller mPackageInstaller;
    private ParallelAppSettings mParentFragment;
    private final PackageManager mPm;
    private ApplicationsState.Session mSession;
    private HashSet<String> mSupportAppClone;
    private final UserManager mUm;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ManageParallelAppsPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mApplicationsState = ApplicationsState.getInstance((Application) context.getApplicationContext());
        PackageManager packageManager = this.mContext.getPackageManager();
        this.mPm = packageManager;
        this.mPackageInstaller = packageManager.getPackageInstaller();
        this.mUm = (UserManager) context.getSystemService("user");
        this.mSupportAppClone = new HashSet<>(Arrays.asList(context.getResources().getStringArray(134283315)));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        this.mUm.isAdminUser();
        this.mContext.getResources().getBoolean(R$bool.config_support_appclone);
        return 0;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        if (arrayList == null) {
            return;
        }
        TreeSet treeSet = new TreeSet();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = arrayList.get(i);
            boolean isUserAdmin = this.mUm.isUserAdmin(UserHandle.getUserId(appEntry.info.uid));
            if (this.mSupportAppClone.contains(appEntry.info.packageName) && isUserAdmin && shouldAddPreference(appEntry)) {
                String generateKey = ParallelAppPreference.generateKey(appEntry);
                treeSet.add(generateKey);
                ParallelAppPreference parallelAppPreference = (ParallelAppPreference) this.mAppList.findPreference(generateKey);
                if (parallelAppPreference == null) {
                    parallelAppPreference = new ParallelAppPreference(this.mContext, appEntry, this.mApplicationsState, this.mUm, this.mPm, this.mPackageInstaller);
                    parallelAppPreference.setOnPreferenceChangeListener(this);
                    this.mAppList.addPreference(parallelAppPreference);
                } else {
                    parallelAppPreference.updateState();
                }
                parallelAppPreference.setOrder(i);
            }
        }
        removeUselessPrefs(treeSet);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        rebuild();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
        this.mParentFragment.onLoadFinish();
        rebuild();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!Utils.isMonkeyRunning() && (preference instanceof ParallelAppPreference)) {
            ParallelAppPreference parallelAppPreference = (ParallelAppPreference) preference;
            if (((Boolean) obj).booleanValue()) {
                return parallelAppPreference.openParApp();
            }
            parallelAppPreference.createCloseOptionsDialog(this.mParentFragment.getActivity());
        }
        return true;
    }

    public void setFilter(ApplicationsState.AppFilter appFilter) {
        this.mFilter = appFilter;
    }

    public void setSession(Lifecycle lifecycle) {
        this.mSession = this.mApplicationsState.newSession(this, lifecycle);
    }

    public void setParentFragment(ParallelAppSettings parallelAppSettings) {
        this.mParentFragment = parallelAppSettings;
    }

    public void setPreferenceGroup(PreferenceGroup preferenceGroup) {
        this.mAppList = preferenceGroup;
    }

    private boolean shouldAddPreference(ApplicationsState.AppEntry appEntry) {
        return appEntry != null && UserHandle.isApp(appEntry.info.uid);
    }

    private void removeUselessPrefs(Set<String> set) {
        int preferenceCount = this.mAppList.getPreferenceCount();
        if (preferenceCount > 0) {
            for (int i = preferenceCount - 1; i >= 0; i--) {
                Preference preference = this.mAppList.getPreference(i);
                String key = preference.getKey();
                if (set.isEmpty() || !set.contains(key)) {
                    this.mAppList.removePreference(preference);
                }
            }
        }
    }

    public void rebuild() {
        ArrayList<ApplicationsState.AppEntry> rebuild = this.mSession.rebuild(this.mFilter, ApplicationsState.ALPHA_COMPARATOR);
        if (rebuild != null) {
            onRebuildComplete(rebuild);
        }
    }
}
