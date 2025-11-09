package com.android.settings.fuelgauge;

import android.content.Context;
import android.os.BatteryStatsManager;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.util.Log;
import com.android.settingslib.utils.AsyncLoaderCompat;
/* loaded from: classes.dex */
public class BatteryUsageStatsLoader extends AsyncLoaderCompat<BatteryUsageStats> {
    private final BatteryStatsManager mBatteryStatsManager;
    private final boolean mIncludeBatteryHistory;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(BatteryUsageStats batteryUsageStats) {
    }

    public BatteryUsageStatsLoader(Context context, boolean z) {
        super(context);
        this.mBatteryStatsManager = (BatteryStatsManager) context.getSystemService(BatteryStatsManager.class);
        this.mIncludeBatteryHistory = z;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public BatteryUsageStats loadInBackground() {
        BatteryUsageStatsQuery.Builder builder = new BatteryUsageStatsQuery.Builder();
        if (this.mIncludeBatteryHistory) {
            builder.includeBatteryHistory();
        }
        try {
            return this.mBatteryStatsManager.getBatteryUsageStats(builder.build());
        } catch (RuntimeException e) {
            Log.e("BatteryUsageStatsLoader", "loadInBackground() for getBatteryUsageStats()", e);
            return new BatteryUsageStats.Builder(new String[0]).build();
        }
    }
}
