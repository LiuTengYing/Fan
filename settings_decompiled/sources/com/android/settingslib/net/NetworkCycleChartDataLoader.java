package com.android.settingslib.net;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.util.Log;
import com.android.settingslib.net.NetworkCycleChartData;
import com.android.settingslib.net.NetworkCycleData;
import com.android.settingslib.net.NetworkCycleDataLoader;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class NetworkCycleChartDataLoader extends NetworkCycleDataLoader<List<NetworkCycleChartData>> {
    private final List<NetworkCycleChartData> mData;

    private NetworkCycleChartDataLoader(Builder builder) {
        super(builder);
        this.mData = new ArrayList();
    }

    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    void recordUsage(long j, long j2) {
        try {
            NetworkStats.Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(this.mNetworkTemplate, j, j2);
            long rxBytes = querySummaryForDevice == null ? 0L : querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
            if (rxBytes > 0) {
                NetworkCycleChartData.Builder builder = new NetworkCycleChartData.Builder();
                builder.setUsageBuckets(getUsageBuckets(j, j2)).setStartTime(j).setEndTime(j2).setTotalUsage(rxBytes);
                this.mData.add(builder.build());
            }
        } catch (RuntimeException e) {
            Log.e("NetworkCycleChartLoader", "Exception querying network detail.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    public List<NetworkCycleChartData> getCycleUsage() {
        return this.mData;
    }

    public static Builder<?> builder(Context context) {
        return new Builder<NetworkCycleChartDataLoader>(context) { // from class: com.android.settingslib.net.NetworkCycleChartDataLoader.1
            @Override // com.android.settingslib.net.NetworkCycleDataLoader.Builder
            public NetworkCycleChartDataLoader build() {
                return new NetworkCycleChartDataLoader(this);
            }
        };
    }

    private List<NetworkCycleData> getUsageBuckets(long j, long j2) {
        ArrayList arrayList = new ArrayList();
        long j3 = j;
        for (long j4 = j + NetworkCycleChartData.BUCKET_DURATION_MS; j4 <= j2; j4 = NetworkCycleChartData.BUCKET_DURATION_MS + j4) {
            long j5 = 0;
            try {
                NetworkStats.Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(this.mNetworkTemplate, j3, j4);
                if (querySummaryForDevice != null) {
                    j5 = querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
                }
            } catch (RuntimeException e) {
                Log.e("NetworkCycleChartLoader", "Exception querying network detail.", e);
            }
            arrayList.add(new NetworkCycleData.Builder().setStartTime(j3).setEndTime(j4).setTotalUsage(j5).build());
            j3 = j4;
        }
        return arrayList;
    }

    /* loaded from: classes2.dex */
    public static abstract class Builder<T extends NetworkCycleChartDataLoader> extends NetworkCycleDataLoader.Builder<T> {
        public Builder(Context context) {
            super(context);
        }
    }
}
