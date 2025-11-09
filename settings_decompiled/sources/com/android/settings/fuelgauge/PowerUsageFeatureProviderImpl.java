package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.ArraySet;
import android.util.SparseIntArray;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R$array;
import com.android.settingslib.fuelgauge.Estimate;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class PowerUsageFeatureProviderImpl implements PowerUsageFeatureProvider {
    private static final String[] PACKAGES_SYSTEM = {"com.android.providers.media", "com.android.providers.calendar", "com.android.systemui"};
    protected Context mContext;
    protected PackageManager mPackageManager;

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public String getAdvancedUsageScreenInfoString() {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Map<Long, Map<String, BatteryHistEntry>> getBatteryHistory(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Uri getBatteryHistoryUri() {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean getEarlyWarningSignal(Context context, String str) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Estimate getEnhancedBatteryPrediction(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public SparseIntArray getEnhancedBatteryPredictionCurve(Context context, long j) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public CharSequence[] getHideApplicationEntries(Context context) {
        return new CharSequence[0];
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Intent getResumeChargeIntent() {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isAdaptiveChargingSupported() {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isChartGraphEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isChartGraphSlotsEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isEnhancedBatteryPredictionEnabled(Context context) {
        return false;
    }

    public PowerUsageFeatureProviderImpl(Context context) {
        this.mPackageManager = context.getPackageManager();
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isTypeSystem(int i, String[] strArr) {
        if (i < 0 || i >= 10000) {
            if (strArr != null) {
                for (String str : strArr) {
                    if (ArrayUtils.contains(PACKAGES_SYSTEM, str)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isSmartBatterySupported() {
        return this.mContext.getResources().getBoolean(17891758);
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Set<CharSequence> getHideBackgroundUsageTimeSet(Context context) {
        return new ArraySet();
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public CharSequence[] getHideApplicationSummary(Context context) {
        return context.getResources().getTextArray(R$array.allowlist_hide_summary_in_battery_usage);
    }
}
