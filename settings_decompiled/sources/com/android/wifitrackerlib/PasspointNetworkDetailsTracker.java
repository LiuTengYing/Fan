package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class PasspointNetworkDetailsTracker extends NetworkDetailsTracker {
    private final PasspointWifiEntry mChosenEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private WifiConfiguration mCurrentWifiConfig;
    private OsuWifiEntry mOsuWifiEntry;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PasspointNetworkDetailsTracker(WifiTrackerInjector wifiTrackerInjector, Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, Handler handler, Handler handler2, Clock clock, long j, long j2, final String str) {
        super(wifiTrackerInjector, lifecycle, context, wifiManager, connectivityManager, handler, handler2, clock, j, j2, "PasspointNetworkDetailsTracker");
        Optional<PasspointConfiguration> findAny = this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$new$0;
                lambda$new$0 = PasspointNetworkDetailsTracker.lambda$new$0(str, (PasspointConfiguration) obj);
                return lambda$new$0;
            }
        }).findAny();
        if (findAny.isPresent()) {
            this.mChosenEntry = new PasspointWifiEntry(this.mInjector, this.mContext, this.mMainHandler, findAny.get(), this.mWifiManager, false);
        } else {
            Optional findAny2 = this.mWifiManager.getPrivilegedConfiguredNetworks().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$new$1;
                    lambda$new$1 = PasspointNetworkDetailsTracker.lambda$new$1(str, (WifiConfiguration) obj);
                    return lambda$new$1;
                }
            }).findAny();
            if (findAny2.isPresent()) {
                this.mChosenEntry = new PasspointWifiEntry(this.mInjector, this.mContext, this.mMainHandler, (WifiConfiguration) findAny2.get(), this.mWifiManager, false);
            } else {
                throw new IllegalArgumentException("Cannot find config for given PasspointWifiEntry key!");
            }
        }
        updateStartInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0(String str, PasspointConfiguration passpointConfiguration) {
        return TextUtils.equals(str, PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$1(String str, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.isPasspoint() && TextUtils.equals(str, PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
    }

    @Override // com.android.wifitrackerlib.NetworkDetailsTracker
    public WifiEntry getWifiEntry() {
        return this.mChosenEntry;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleOnStart() {
        updateStartInfo();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker
    protected void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateConfig();
    }

    private void updateStartInfo() {
        boolean z = true;
        conditionallyUpdateScanResults(true);
        conditionallyUpdateConfig();
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        this.mChosenEntry.updateConnectionInfo(connectionInfo, networkInfo);
        handleNetworkCapabilitiesChanged(this.mConnectivityManager.getNetworkCapabilities(currentNetwork));
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
        this.mChosenEntry.setIsDefaultNetwork(this.mIsWifiDefaultRoute);
        this.mChosenEntry.setIsLowQuality((this.mIsWifiValidated && this.mIsCellDefaultRoute) ? false : false);
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            if (TextUtils.equals(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()), this.mChosenEntry.getKey())) {
                this.mCurrentWifiConfig = wifiConfiguration;
                this.mChosenEntry.updateScanResultInfo(wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1));
                return;
            }
        }
        this.mChosenEntry.updateScanResultInfo(this.mCurrentWifiConfig, null, null);
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        Map matchingPasspointConfigsForOsuProviders = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet());
        OsuWifiEntry osuWifiEntry = this.mOsuWifiEntry;
        if (osuWifiEntry != null) {
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.get(osuWifiEntry.getOsuProvider()));
        } else {
            for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
                PasspointConfiguration passpointConfiguration = (PasspointConfiguration) matchingPasspointConfigsForOsuProviders.get(osuProvider);
                if (passpointConfiguration != null && TextUtils.equals(this.mChosenEntry.getKey(), PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()))) {
                    OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(this.mInjector, this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, false);
                    this.mOsuWifiEntry = osuWifiEntry2;
                    osuWifiEntry2.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
                    this.mOsuWifiEntry.setAlreadyProvisioned(true);
                    this.mChosenEntry.setOsuWifiEntry(this.mOsuWifiEntry);
                    return;
                }
            }
        }
        OsuWifiEntry osuWifiEntry3 = this.mOsuWifiEntry;
        if (osuWifiEntry3 == null || osuWifiEntry3.getLevel() != -1) {
            return;
        }
        this.mChosenEntry.setOsuWifiEntry(null);
        this.mOsuWifiEntry = null;
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            this.mChosenEntry.updateScanResultInfo(this.mCurrentWifiConfig, null, null);
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            cacheNewScanResults();
        } else {
            j += this.mScanIntervalMillis;
        }
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(j);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
    }

    private void conditionallyUpdateConfig() {
        this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$conditionallyUpdateConfig$2;
                lambda$conditionallyUpdateConfig$2 = PasspointNetworkDetailsTracker.this.lambda$conditionallyUpdateConfig$2((PasspointConfiguration) obj);
                return lambda$conditionallyUpdateConfig$2;
            }
        }).findAny().ifPresent(new Consumer() { // from class: com.android.wifitrackerlib.PasspointNetworkDetailsTracker$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PasspointNetworkDetailsTracker.this.lambda$conditionallyUpdateConfig$3((PasspointConfiguration) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$conditionallyUpdateConfig$2(PasspointConfiguration passpointConfiguration) {
        return TextUtils.equals(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()), this.mChosenEntry.getKey());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$conditionallyUpdateConfig$3(PasspointConfiguration passpointConfiguration) {
        this.mChosenEntry.updatePasspointConfig(passpointConfiguration);
    }

    private void cacheNewScanResults() {
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
    }
}
