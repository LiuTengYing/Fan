package com.android.settings.applications.specialaccess.notificationaccess;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.VersionedPackage;
import android.service.notification.NotificationListenerFilter;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
/* loaded from: classes.dex */
public class BridgedAppsPreferenceController extends BasePreferenceController implements LifecycleObserver, ApplicationsState.Callbacks, AppStateBaseBridge.Callback {
    private ApplicationsState mApplicationsState;
    private ComponentName mCn;
    private ApplicationsState.AppFilter mFilter;
    private NotificationListenerFilter mNlf;
    private NotificationBackend mNm;
    private PreferenceScreen mScreen;
    private ApplicationsState.Session mSession;
    private int mUserId;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BridgedAppsPreferenceController(Context context, String str) {
        super(context, str);
    }

    public BridgedAppsPreferenceController setAppState(ApplicationsState applicationsState) {
        this.mApplicationsState = applicationsState;
        return this;
    }

    public BridgedAppsPreferenceController setCn(ComponentName componentName) {
        this.mCn = componentName;
        return this;
    }

    public BridgedAppsPreferenceController setUserId(int i) {
        this.mUserId = i;
        return this;
    }

    public BridgedAppsPreferenceController setNm(NotificationBackend notificationBackend) {
        this.mNm = notificationBackend;
        return this;
    }

    public BridgedAppsPreferenceController setFilter(ApplicationsState.AppFilter appFilter) {
        this.mFilter = appFilter;
        return this;
    }

    public BridgedAppsPreferenceController setSession(Lifecycle lifecycle) {
        this.mSession = this.mApplicationsState.newSession(this, lifecycle);
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settings.applications.AppStateBaseBridge.Callback
    public void onExtraInfoUpdated() {
        rebuild();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        rebuild();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        if (arrayList == null) {
            return;
        }
        this.mNlf = this.mNm.getListenerFilter(this.mCn, this.mUserId);
        TreeSet treeSet = new TreeSet();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = arrayList.get(i);
            String str = appEntry.info.packageName + "|" + appEntry.info.uid;
            treeSet.add(str);
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.mScreen.findPreference(str);
            if (checkBoxPreference == null) {
                checkBoxPreference = new CheckBoxPreference(this.mScreen.getContext());
                checkBoxPreference.setIcon(AppUtils.getIcon(this.mContext, appEntry));
                checkBoxPreference.setTitle(appEntry.label);
                checkBoxPreference.setKey(str);
                this.mScreen.addPreference(checkBoxPreference);
            }
            checkBoxPreference.setOrder(i);
            NotificationListenerFilter notificationListenerFilter = this.mNlf;
            ApplicationInfo applicationInfo = appEntry.info;
            checkBoxPreference.setChecked(notificationListenerFilter.isPackageAllowed(new VersionedPackage(applicationInfo.packageName, applicationInfo.uid)));
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.BridgedAppsPreferenceController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return BridgedAppsPreferenceController.this.onPreferenceChange(preference, obj);
                }
            });
        }
        removeUselessPrefs(treeSet);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
        rebuild();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
        rebuild();
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference instanceof CheckBoxPreference) {
            String substring = preference.getKey().substring(0, preference.getKey().indexOf("|"));
            int parseInt = Integer.parseInt(preference.getKey().substring(preference.getKey().indexOf("|") + 1));
            boolean z = obj == Boolean.TRUE;
            NotificationListenerFilter listenerFilter = this.mNm.getListenerFilter(this.mCn, this.mUserId);
            this.mNlf = listenerFilter;
            if (z) {
                listenerFilter.removePackage(new VersionedPackage(substring, parseInt));
            } else {
                listenerFilter.addPackage(new VersionedPackage(substring, parseInt));
            }
            this.mNm.setListenerFilter(this.mCn, this.mUserId, this.mNlf);
            return true;
        }
        return false;
    }

    public void rebuild() {
        ArrayList<ApplicationsState.AppEntry> rebuild = this.mSession.rebuild(this.mFilter, ApplicationsState.ALPHA_COMPARATOR);
        if (rebuild != null) {
            onRebuildComplete(rebuild);
        }
    }

    private void removeUselessPrefs(Set<String> set) {
        int preferenceCount = this.mScreen.getPreferenceCount();
        if (preferenceCount > 0) {
            for (int i = preferenceCount - 1; i >= 0; i--) {
                Preference preference = this.mScreen.getPreference(i);
                if (!set.contains(preference.getKey())) {
                    this.mScreen.removePreference(preference);
                }
            }
        }
    }
}
