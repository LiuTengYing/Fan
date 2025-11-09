package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.SavedNetworkTracker;
import com.android.wifitrackerlib.StandardWifiEntry;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class SavedNetworkTracker extends BaseWifiTracker {
    private WifiEntry mConnectedWifiEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private final SavedNetworkTrackerCallback mListener;
    private final Object mLock;
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache;
    private final List<WifiEntry> mSavedWifiEntries;
    private final List<StandardWifiEntry> mStandardWifiEntryCache;
    private final List<WifiEntry> mSubscriptionWifiEntries;

    /* loaded from: classes2.dex */
    public interface SavedNetworkTrackerCallback extends BaseWifiTracker.BaseWifiTrackerCallback {
        void onSavedWifiEntriesChanged();

        void onSubscriptionWifiEntriesChanged();
    }

    public SavedNetworkTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, Handler handler, Handler handler2, Clock clock, long j, long j2, SavedNetworkTrackerCallback savedNetworkTrackerCallback) {
        this(new WifiTrackerInjector(context), lifecycle, context, wifiManager, connectivityManager, handler, handler2, clock, j, j2, savedNetworkTrackerCallback);
    }

    SavedNetworkTracker(WifiTrackerInjector wifiTrackerInjector, Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, Handler handler, Handler handler2, Clock clock, long j, long j2, SavedNetworkTrackerCallback savedNetworkTrackerCallback) {
        super(wifiTrackerInjector, lifecycle, context, wifiManager, connectivityManager, handler, handler2, clock, j, j2, savedNetworkTrackerCallback, "SavedNetworkTracker");
        this.mLock = new Object();
        this.mSavedWifiEntries = new ArrayList();
        this.mSubscriptionWifiEntries = new ArrayList();
        this.mStandardWifiEntryCache = new ArrayList();
        this.mPasspointWifiEntryCache = new HashMap();
        this.mListener = savedNetworkTrackerCallback;
    }

    public List<WifiEntry> getSavedWifiEntries() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mSavedWifiEntries);
        }
        return arrayList;
    }

    public List<WifiEntry> getSubscriptionWifiEntries() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mSubscriptionWifiEntries);
        }
        return arrayList;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleOnStart() {
        updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        boolean z = true;
        conditionallyUpdateScanResults(true);
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        updateConnectionInfo(connectionInfo, networkInfo);
        updateWifiEntries();
        handleNetworkCapabilitiesChanged(this.mConnectivityManager.getNetworkCapabilities(currentNetwork));
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            this.mConnectedWifiEntry.setIsLowQuality((this.mIsWifiValidated && this.mIsCellDefaultRoute) ? false : false);
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
        updateWifiEntries();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
        updateWifiEntries();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        updateWifiEntries();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleNetworkStateChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        this.mCurrentNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleRssiChangedAction() {
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.updateConnectionInfo(connectionInfo, this.mCurrentNetworkInfo);
        }
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry == null || wifiEntry.getConnectedState() != 2) {
            return;
        }
        this.mConnectedWifiEntry.updateLinkProperties(linkProperties);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry == null || wifiEntry.getConnectedState() != 2) {
            return;
        }
        this.mConnectedWifiEntry.updateNetworkCapabilities(networkCapabilities);
        this.mConnectedWifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleDefaultRouteChanged() {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null) {
            wifiEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            this.mConnectedWifiEntry.setIsLowQuality(this.mIsWifiValidated && this.mIsCellDefaultRoute);
        }
    }

    private void updateWifiEntries() {
        synchronized (this.mLock) {
            this.mConnectedWifiEntry = null;
            for (StandardWifiEntry standardWifiEntry : this.mStandardWifiEntryCache) {
                if (standardWifiEntry.getConnectedState() != 0) {
                    this.mConnectedWifiEntry = standardWifiEntry;
                }
            }
            for (WifiEntry wifiEntry : this.mSubscriptionWifiEntries) {
                if (wifiEntry.getConnectedState() != 0) {
                    this.mConnectedWifiEntry = wifiEntry;
                }
            }
            WifiEntry wifiEntry2 = this.mConnectedWifiEntry;
            if (wifiEntry2 != null) {
                wifiEntry2.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
            }
            this.mSavedWifiEntries.clear();
            this.mSavedWifiEntries.addAll(this.mStandardWifiEntryCache);
            Collections.sort(this.mSavedWifiEntries, WifiEntry.TITLE_COMPARATOR);
            this.mSubscriptionWifiEntries.clear();
            this.mSubscriptionWifiEntries.addAll(this.mPasspointWifiEntryCache.values());
            Collections.sort(this.mSubscriptionWifiEntries, WifiEntry.TITLE_COMPARATOR);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("SavedNetworkTracker", "Updated SavedWifiEntries: " + Arrays.toString(this.mSavedWifiEntries.toArray()));
                Log.v("SavedNetworkTracker", "Updated SubscriptionWifiEntries: " + Arrays.toString(this.mSubscriptionWifiEntries.toArray()));
            }
        }
        notifyOnSavedWifiEntriesChanged();
        notifyOnSubscriptionWifiEntriesChanged();
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        final Map map = (Map) list.stream().collect(Collectors.groupingBy(new SavedNetworkTracker$$ExternalSyntheticLambda6()));
        this.mStandardWifiEntryCache.forEach(new Consumer() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SavedNetworkTracker.lambda$updateStandardWifiEntryScans$0(map, (StandardWifiEntry) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateStandardWifiEntryScans$0(Map map, StandardWifiEntry standardWifiEntry) {
        standardWifiEntry.updateScanResultInfo((List) map.get(standardWifiEntry.getStandardWifiEntryKey().getScanResultKey()));
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1));
            }
        }
        for (PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            if (!treeSet.contains(passpointWifiEntry.getKey())) {
                passpointWifiEntry.updateScanResultInfo(null, null, null);
            }
        }
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        updateStandardWifiEntryScans(this.mScanResultUpdater.getScanResults(j));
        updatePasspointWifiEntryScans(this.mScanResultUpdater.getScanResults(j));
    }

    private void updateStandardWifiEntryConfigs(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        final Map map = (Map) list.stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateStandardWifiEntryConfigs$1;
                lambda$updateStandardWifiEntryConfigs$1 = SavedNetworkTracker.lambda$updateStandardWifiEntryConfigs$1((WifiConfiguration) obj);
                return lambda$updateStandardWifiEntryConfigs$1;
            }
        }).collect(Collectors.groupingBy(new SavedNetworkTracker$$ExternalSyntheticLambda3()));
        this.mStandardWifiEntryCache.removeIf(new Predicate() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateStandardWifiEntryConfigs$2;
                lambda$updateStandardWifiEntryConfigs$2 = SavedNetworkTracker.lambda$updateStandardWifiEntryConfigs$2(map, (StandardWifiEntry) obj);
                return lambda$updateStandardWifiEntryConfigs$2;
            }
        });
        for (StandardWifiEntry.StandardWifiEntryKey standardWifiEntryKey : map.keySet()) {
            this.mStandardWifiEntryCache.add(new StandardWifiEntry(this.mInjector, this.mContext, this.mMainHandler, standardWifiEntryKey, (List) map.get(standardWifiEntryKey), null, this.mWifiManager, true));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryConfigs$1(WifiConfiguration wifiConfiguration) {
        return !wifiConfiguration.carrierMerged;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateStandardWifiEntryConfigs$2(Map map, StandardWifiEntry standardWifiEntry) {
        standardWifiEntry.updateConfig((List) map.remove(standardWifiEntry.getStandardWifiEntryKey()));
        return !standardWifiEntry.isSaved();
    }

    private void updatePasspointWifiEntryConfigs(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        final Map map = (Map) list.stream().collect(Collectors.toMap(new Function() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$updatePasspointWifiEntryConfigs$3;
                lambda$updatePasspointWifiEntryConfigs$3 = SavedNetworkTracker.lambda$updatePasspointWifiEntryConfigs$3((PasspointConfiguration) obj);
                return lambda$updatePasspointWifiEntryConfigs$3;
            }
        }, Function.identity()));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updatePasspointWifiEntryConfigs$4;
                lambda$updatePasspointWifiEntryConfigs$4 = SavedNetworkTracker.lambda$updatePasspointWifiEntryConfigs$4(map, (Map.Entry) obj);
                return lambda$updatePasspointWifiEntryConfigs$4;
            }
        });
        for (String str : map.keySet()) {
            this.mPasspointWifiEntryCache.put(str, new PasspointWifiEntry(this.mInjector, this.mContext, this.mMainHandler, (PasspointConfiguration) map.get(str), this.mWifiManager, true));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$updatePasspointWifiEntryConfigs$3(PasspointConfiguration passpointConfiguration) {
        return PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePasspointWifiEntryConfigs$4(Map map, Map.Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.remove(passpointWifiEntry.getKey());
        if (passpointConfiguration != null) {
            passpointWifiEntry.updatePasspointConfig(passpointConfiguration);
            return false;
        }
        return true;
    }

    private void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        for (StandardWifiEntry standardWifiEntry : this.mStandardWifiEntryCache) {
            standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
            if (standardWifiEntry.getConnectedState() != 0) {
                this.mConnectedWifiEntry = standardWifiEntry;
            }
        }
        for (PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            passpointWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
            if (passpointWifiEntry.getConnectedState() != 0) {
                this.mConnectedWifiEntry = passpointWifiEntry;
            }
        }
    }

    private void notifyOnSavedWifiEntriesChanged() {
        final SavedNetworkTrackerCallback savedNetworkTrackerCallback = this.mListener;
        if (savedNetworkTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(savedNetworkTrackerCallback);
            handler.post(new Runnable() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    SavedNetworkTracker.SavedNetworkTrackerCallback.this.onSavedWifiEntriesChanged();
                }
            });
        }
    }

    private void notifyOnSubscriptionWifiEntriesChanged() {
        final SavedNetworkTrackerCallback savedNetworkTrackerCallback = this.mListener;
        if (savedNetworkTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(savedNetworkTrackerCallback);
            handler.post(new Runnable() { // from class: com.android.wifitrackerlib.SavedNetworkTracker$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    SavedNetworkTracker.SavedNetworkTrackerCallback.this.onSubscriptionWifiEntriesChanged();
                }
            });
        }
    }
}
