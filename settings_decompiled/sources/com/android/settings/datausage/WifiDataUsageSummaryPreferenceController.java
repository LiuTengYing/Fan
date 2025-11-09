package com.android.settings.datausage;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.net.DataUsageController;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class WifiDataUsageSummaryPreferenceController extends DataUsageSummaryPreferenceController {
    final Set<String> mAllNetworkKeys;

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiDataUsageSummaryPreferenceController(Activity activity, Lifecycle lifecycle, PreferenceFragmentCompat preferenceFragmentCompat, Set<String> set) {
        super(activity, lifecycle, preferenceFragmentCompat, -1);
        this.mAllNetworkKeys = new HashSet(set);
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null) {
            return;
        }
        DataUsageSummaryPreference dataUsageSummaryPreference = (DataUsageSummaryPreference) preference;
        NetworkTemplate build = new NetworkTemplate.Builder(4).setWifiNetworkKeys(this.mAllNetworkKeys).build();
        if (this.mDataUsageController == null) {
            Context context = this.mContext;
            int i = this.mSubId;
            updateConfiguration(context, i, getSubscriptionInfo(i));
        }
        DataUsageController.DataUsageInfo dataUsageInfo = this.mDataUsageController.getDataUsageInfo(build);
        this.mDataInfoController.updateDataLimit(dataUsageInfo, this.mPolicyEditor.getPolicy(build));
        dataUsageSummaryPreference.setWifiMode(true, dataUsageInfo.period, true);
        dataUsageSummaryPreference.setChartEnabled(true);
        long j = dataUsageInfo.usageLevel;
        dataUsageSummaryPreference.setUsageNumbers(j, j, false);
        dataUsageSummaryPreference.setProgress(100.0f);
        dataUsageSummaryPreference.setLabels(DataUsageUtils.formatDataUsage(this.mContext, 0L), DataUsageUtils.formatDataUsage(this.mContext, dataUsageInfo.usageLevel));
    }
}
