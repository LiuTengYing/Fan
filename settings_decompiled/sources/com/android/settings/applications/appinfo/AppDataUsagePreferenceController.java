package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.Process;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.datausage.AppDataUsage;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settingslib.AppItem;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataForUidLoader;
import java.util.List;
/* loaded from: classes.dex */
public class AppDataUsagePreferenceController extends AppInfoPreferenceControllerBase implements LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>>, LifecycleObserver, OnResume, OnPause {
    private List<NetworkCycleDataForUid> mAppUsageData;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<NetworkCycleDataForUid>> loader) {
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppDataUsagePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isBandwidthControlEnabled() ? 0 : 2;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference.setEnabled(AppUtils.isAppInstalled(this.mAppEntry));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(getDataSummary());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            int i = this.mParent.getAppEntry().info.uid;
            new AppItem(i).addUid(i);
            this.mParent.getLoaderManager().restartLoader(2, null, this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (isAvailable()) {
            this.mParent.getLoaderManager().destroyLoader(2);
        }
    }

    /* JADX WARN: Type inference failed for: r1v3, types: [androidx.loader.content.Loader<java.util.List<com.android.settingslib.net.NetworkCycleDataForUid>>, com.android.settingslib.net.NetworkCycleDataLoader] */
    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<List<NetworkCycleDataForUid>> onCreateLoader(int i, Bundle bundle) {
        NetworkTemplate template = getTemplate(this.mContext);
        int i2 = this.mParent.getAppEntry().info.uid;
        NetworkCycleDataForUidLoader.Builder<?> builder = NetworkCycleDataForUidLoader.builder(this.mContext);
        builder.setRetrieveDetail(false).setNetworkTemplate(template);
        builder.addUid(i2);
        if (Process.isApplicationUid(i2)) {
            builder.addUid(Process.toSdkSandboxUid(i2));
        }
        return builder.build();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<NetworkCycleDataForUid>> loader, List<NetworkCycleDataForUid> list) {
        this.mAppUsageData = list;
        updateState(this.mPreference);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    protected Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppDataUsage.class;
    }

    private CharSequence getDataSummary() {
        if (this.mAppUsageData != null) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = 0;
            for (NetworkCycleDataForUid networkCycleDataForUid : this.mAppUsageData) {
                j += networkCycleDataForUid.getTotalUsage();
                long startTime = networkCycleDataForUid.getStartTime();
                if (startTime < currentTimeMillis) {
                    currentTimeMillis = startTime;
                }
            }
            if (j == 0) {
                return this.mContext.getString(R$string.no_data_usage);
            }
            Context context = this.mContext;
            return context.getString(R$string.data_summary_format, Formatter.formatFileSize(context, j, 8), DateUtils.formatDateTime(this.mContext, currentTimeMillis, 65552));
        }
        return this.mContext.getString(R$string.computing_size);
    }

    private static NetworkTemplate getTemplate(Context context) {
        if (DataUsageUtils.hasReadyMobileRadio(context)) {
            return new NetworkTemplate.Builder(1).setMeteredness(1).build();
        }
        if (DataUsageUtils.hasWifiRadio(context)) {
            return new NetworkTemplate.Builder(4).build();
        }
        return new NetworkTemplate.Builder(5).build();
    }

    boolean isBandwidthControlEnabled() {
        return Utils.isBandwidthControlEnabled();
    }
}
