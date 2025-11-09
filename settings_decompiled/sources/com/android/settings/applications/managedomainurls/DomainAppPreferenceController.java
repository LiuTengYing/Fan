package com.android.settings.applications.managedomainurls;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$integer;
import com.android.settings.R$string;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.intentpicker.AppLaunchSettings;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
import java.util.Map;
/* loaded from: classes.dex */
public class DomainAppPreferenceController extends BasePreferenceController implements ApplicationsState.Callbacks {
    private static final int INSTALLED_APP_DETAILS = 1;
    private ApplicationsState mApplicationsState;
    private PreferenceGroup mDomainAppList;
    private ManageDomainUrls mFragment;
    private int mMetricsCategory;
    private Map<String, Preference> mPreferenceCache;
    private ApplicationsState.Session mSession;

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
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
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

    public DomainAppPreferenceController(Context context, String str) {
        super(context, str);
        this.mApplicationsState = ApplicationsState.getInstance((Application) this.mContext.getApplicationContext());
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mDomainAppList = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference instanceof DomainAppPreference) {
            ApplicationsState.AppEntry entry = ((DomainAppPreference) preference).getEntry();
            String string = this.mContext.getString(R$string.auto_launch_label);
            ApplicationInfo applicationInfo = entry.info;
            AppInfoBase.startAppInfoFragment(AppLaunchSettings.class, string, applicationInfo.packageName, applicationInfo.uid, this.mFragment, 1, this.mMetricsCategory);
            return true;
        }
        return false;
    }

    public void setFragment(ManageDomainUrls manageDomainUrls) {
        this.mFragment = manageDomainUrls;
        this.mMetricsCategory = manageDomainUrls.getMetricsCategory();
        this.mSession = this.mApplicationsState.newSession(this, this.mFragment.getSettingsLifecycle());
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        AppUtils.preloadTopIcons(context, arrayList, context.getResources().getInteger(R$integer.config_num_visible_app_icons));
        rebuildAppList(this.mDomainAppList, arrayList);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
        rebuild();
    }

    private void cacheAllPrefs(PreferenceGroup preferenceGroup) {
        this.mPreferenceCache = new ArrayMap();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
    }

    private Preference getCachedPreference(String str) {
        Map<String, Preference> map = this.mPreferenceCache;
        if (map != null) {
            return map.remove(str);
        }
        return null;
    }

    private void removeCachedPrefs(PreferenceGroup preferenceGroup) {
        for (Preference preference : this.mPreferenceCache.values()) {
            preferenceGroup.removePreference(preference);
        }
        this.mPreferenceCache = null;
    }

    private void rebuild() {
        ArrayList<ApplicationsState.AppEntry> rebuild = this.mSession.rebuild(ApplicationsState.FILTER_WITH_DOMAIN_URLS, ApplicationsState.ALPHA_COMPARATOR);
        if (rebuild != null) {
            onRebuildComplete(rebuild);
        }
    }

    private void rebuildAppList(PreferenceGroup preferenceGroup, ArrayList<ApplicationsState.AppEntry> arrayList) {
        cacheAllPrefs(preferenceGroup);
        int size = arrayList.size();
        Context context = preferenceGroup.getContext();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = arrayList.get(i);
            String str = appEntry.info.packageName + "|" + appEntry.info.uid;
            DomainAppPreference domainAppPreference = (DomainAppPreference) getCachedPreference(str);
            if (domainAppPreference == null) {
                domainAppPreference = new DomainAppPreference(context, appEntry);
                domainAppPreference.setKey(str);
                preferenceGroup.addPreference(domainAppPreference);
            } else {
                domainAppPreference.reuse();
            }
            domainAppPreference.setOrder(i);
        }
        removeCachedPrefs(preferenceGroup);
    }
}
