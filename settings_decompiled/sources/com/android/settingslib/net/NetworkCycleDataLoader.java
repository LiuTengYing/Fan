package com.android.settingslib.net;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.util.Pair;
import android.util.Range;
import androidx.loader.content.AsyncTaskLoader;
import com.android.settingslib.NetworkPolicyEditor;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class NetworkCycleDataLoader<D> extends AsyncTaskLoader<D> {
    private final ArrayList<Long> mCycles;
    protected final NetworkStatsManager mNetworkStatsManager;
    protected final NetworkTemplate mNetworkTemplate;
    private final NetworkPolicy mPolicy;

    abstract D getCycleUsage();

    abstract void recordUsage(long j, long j2);

    /* JADX INFO: Access modifiers changed from: protected */
    public NetworkCycleDataLoader(Builder<?> builder) {
        super(((Builder) builder).mContext);
        NetworkTemplate networkTemplate = ((Builder) builder).mNetworkTemplate;
        this.mNetworkTemplate = networkTemplate;
        this.mCycles = ((Builder) builder).mCycles;
        this.mNetworkStatsManager = (NetworkStatsManager) ((Builder) builder).mContext.getSystemService("netstats");
        NetworkPolicyEditor networkPolicyEditor = new NetworkPolicyEditor(NetworkPolicyManager.from(((Builder) builder).mContext));
        networkPolicyEditor.read();
        this.mPolicy = networkPolicyEditor.getPolicy(networkTemplate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public D loadInBackground() {
        ArrayList<Long> arrayList = this.mCycles;
        if (arrayList != null && arrayList.size() > 1) {
            loadDataForSpecificCycles();
        } else if (this.mPolicy == null) {
            loadFourWeeksData();
        } else {
            loadPolicyData();
        }
        return getCycleUsage();
    }

    void loadPolicyData() {
        Iterator cycleIterator = NetworkPolicyManager.cycleIterator(this.mPolicy);
        while (cycleIterator.hasNext()) {
            Pair pair = (Pair) cycleIterator.next();
            recordUsage(((ZonedDateTime) pair.first).toInstant().toEpochMilli(), ((ZonedDateTime) pair.second).toInstant().toEpochMilli());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onReset() {
        super.onReset();
        cancelLoad();
    }

    void loadFourWeeksData() {
        NetworkTemplate networkTemplate = this.mNetworkTemplate;
        if (networkTemplate == null) {
            return;
        }
        try {
            Range timeRangeOf = getTimeRangeOf(this.mNetworkStatsManager.queryDetailsForDevice(networkTemplate, Long.MIN_VALUE, Long.MAX_VALUE));
            long longValue = ((Long) timeRangeOf.getUpper()).longValue();
            while (longValue > ((Long) timeRangeOf.getLower()).longValue()) {
                long j = longValue - 2419200000L;
                recordUsage(j, longValue);
                longValue = j;
            }
        } catch (IllegalArgumentException unused) {
        }
    }

    void loadDataForSpecificCycles() {
        long longValue = this.mCycles.get(0).longValue();
        int i = 1;
        int size = this.mCycles.size() - 1;
        while (i <= size) {
            long longValue2 = this.mCycles.get(i).longValue();
            recordUsage(longValue2, longValue);
            i++;
            longValue = longValue2;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getTotalUsage(NetworkStats networkStats) {
        long j = 0;
        if (networkStats != null) {
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            while (networkStats.hasNextBucket() && networkStats.getNextBucket(bucket)) {
                j += bucket.getRxBytes() + bucket.getTxBytes();
            }
            networkStats.close();
        }
        return j;
    }

    Range getTimeRangeOf(NetworkStats networkStats) {
        long j = Long.MAX_VALUE;
        long j2 = Long.MIN_VALUE;
        while (hasNextBucket(networkStats)) {
            NetworkStats.Bucket nextBucket = getNextBucket(networkStats);
            j = Math.min(j, nextBucket.getStartTimeStamp());
            j2 = Math.max(j2, nextBucket.getEndTimeStamp());
        }
        return new Range(Long.valueOf(j), Long.valueOf(j2));
    }

    boolean hasNextBucket(NetworkStats networkStats) {
        return networkStats.hasNextBucket();
    }

    NetworkStats.Bucket getNextBucket(NetworkStats networkStats) {
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket;
    }

    public ArrayList<Long> getCycles() {
        return this.mCycles;
    }

    /* loaded from: classes2.dex */
    public static abstract class Builder<T extends NetworkCycleDataLoader> {
        private final Context mContext;
        private ArrayList<Long> mCycles;
        private NetworkTemplate mNetworkTemplate;

        public abstract T build();

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder<T> setNetworkTemplate(NetworkTemplate networkTemplate) {
            this.mNetworkTemplate = networkTemplate;
            return this;
        }

        public Builder<T> setCycles(ArrayList<Long> arrayList) {
            this.mCycles = arrayList;
            return this;
        }
    }
}
