package com.android.settingslib.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.WifiStatusTracker;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public class WifiStatusTracker {
    private static final SimpleDateFormat SSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    public boolean connected;
    public boolean enabled;
    public boolean isCaptivePortal;
    public boolean isCarrierMerged;
    public boolean isDefaultNetwork;
    public int level;
    private final WifiNetworkScoreCache.CacheListener mCacheListener;
    private final Runnable mCallback;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private Network mDefaultNetwork;
    private final ConnectivityManager.NetworkCallback mDefaultNetworkCallback;
    private NetworkCapabilities mDefaultNetworkCapabilities;
    private final Handler mHandler;
    private final String[] mHistory;
    private int mHistoryIndex;
    private final Handler mMainThreadHandler;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    private final Set<Integer> mNetworks;
    private WifiInfo mWifiInfo;
    private final WifiManager mWifiManager;
    private final WifiNetworkScoreCache mWifiNetworkScoreCache;
    public int rssi;
    public String ssid;
    public int state;
    public String statusLabel;
    public int subId;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settingslib.wifi.WifiStatusTracker$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass1 extends ConnectivityManager.NetworkCallback {
        AnonymousClass1(int i) {
            super(i);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            WifiInfo wifiInfo;
            boolean z = false;
            if (networkCapabilities.hasTransport(0)) {
                wifiInfo = Utils.tryGetWifiInfoForVcn(networkCapabilities);
                r2 = false;
                z = wifiInfo != null;
            } else if (networkCapabilities.hasTransport(1)) {
                wifiInfo = (WifiInfo) networkCapabilities.getTransportInfo();
            } else {
                wifiInfo = null;
                r2 = false;
            }
            if (z || r2) {
                WifiStatusTracker.this.recordLastWifiNetwork(WifiStatusTracker.SSDF.format(Long.valueOf(System.currentTimeMillis())) + ",onCapabilitiesChanged: network=" + network + ",networkCapabilities=" + networkCapabilities);
            }
            if (wifiInfo == null || !wifiInfo.isPrimary()) {
                if (WifiStatusTracker.this.mNetworks.contains(Integer.valueOf(network.getNetId()))) {
                    WifiStatusTracker.this.mNetworks.remove(Integer.valueOf(network.getNetId()));
                    return;
                }
                return;
            }
            if (!WifiStatusTracker.this.mNetworks.contains(Integer.valueOf(network.getNetId()))) {
                WifiStatusTracker.this.mNetworks.add(Integer.valueOf(network.getNetId()));
            }
            WifiStatusTracker.this.updateWifiInfo(wifiInfo);
            WifiStatusTracker.this.updateStatusLabel();
            WifiStatusTracker.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.settingslib.wifi.WifiStatusTracker$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiStatusTracker.AnonymousClass1.this.lambda$onCapabilitiesChanged$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapabilitiesChanged$0() {
            WifiStatusTracker.this.postResults();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            WifiStatusTracker.this.recordLastWifiNetwork(WifiStatusTracker.SSDF.format(Long.valueOf(System.currentTimeMillis())) + ",onLost: network=" + network);
            if (WifiStatusTracker.this.mNetworks.contains(Integer.valueOf(network.getNetId()))) {
                WifiStatusTracker.this.mNetworks.remove(Integer.valueOf(network.getNetId()));
                if (WifiStatusTracker.this.mNetworks.size() == 0) {
                    WifiStatusTracker.this.updateWifiInfo(null);
                }
                WifiStatusTracker.this.updateStatusLabel();
                WifiStatusTracker.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.settingslib.wifi.WifiStatusTracker$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        WifiStatusTracker.AnonymousClass1.this.lambda$onLost$1();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLost$1() {
            WifiStatusTracker.this.postResults();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settingslib.wifi.WifiStatusTracker$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass2 extends ConnectivityManager.NetworkCallback {
        AnonymousClass2(int i) {
            super(i);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            WifiStatusTracker.this.mDefaultNetwork = network;
            WifiStatusTracker.this.mDefaultNetworkCapabilities = networkCapabilities;
            WifiStatusTracker.this.updateStatusLabel();
            WifiStatusTracker.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.settingslib.wifi.WifiStatusTracker$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiStatusTracker.AnonymousClass2.this.lambda$onCapabilitiesChanged$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCapabilitiesChanged$0() {
            WifiStatusTracker.this.postResults();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            WifiStatusTracker.this.mDefaultNetwork = null;
            WifiStatusTracker.this.mDefaultNetworkCapabilities = null;
            WifiStatusTracker.this.updateStatusLabel();
            WifiStatusTracker.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.settingslib.wifi.WifiStatusTracker$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WifiStatusTracker.AnonymousClass2.this.lambda$onLost$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLost$1() {
            WifiStatusTracker.this.postResults();
        }
    }

    public WifiStatusTracker(Context context, WifiManager wifiManager, NetworkScoreManager networkScoreManager, ConnectivityManager connectivityManager, Runnable runnable) {
        this(context, wifiManager, networkScoreManager, connectivityManager, runnable, null, null);
    }

    public WifiStatusTracker(Context context, WifiManager wifiManager, NetworkScoreManager networkScoreManager, ConnectivityManager connectivityManager, Runnable runnable, Handler handler, Handler handler2) {
        this.mNetworks = new HashSet();
        this.mHistory = new String[32];
        this.mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addCapability(15).addTransportType(1).addTransportType(0).build();
        this.mNetworkCallback = new AnonymousClass1(1);
        this.mDefaultNetworkCallback = new AnonymousClass2(1);
        this.mDefaultNetwork = null;
        this.mDefaultNetworkCapabilities = null;
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(context);
        this.mNetworkScoreManager = networkScoreManager;
        this.mConnectivityManager = connectivityManager;
        this.mCallback = runnable;
        if (handler2 == null) {
            HandlerThread handlerThread = new HandlerThread("WifiStatusTrackerHandler");
            handlerThread.start();
            this.mHandler = new Handler(handlerThread.getLooper());
        } else {
            this.mHandler = handler2;
        }
        this.mMainThreadHandler = handler == null ? new Handler(Looper.getMainLooper()) : handler;
        this.mCacheListener = new AnonymousClass3(this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settingslib.wifi.WifiStatusTracker$3  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass3 extends WifiNetworkScoreCache.CacheListener {
        AnonymousClass3(Handler handler) {
            super(handler);
        }

        public void networkCacheUpdated(List<ScoredNetwork> list) {
            WifiStatusTracker.this.updateStatusLabel();
            WifiStatusTracker.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.settingslib.wifi.WifiStatusTracker$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiStatusTracker.AnonymousClass3.this.lambda$networkCacheUpdated$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$networkCacheUpdated$0() {
            WifiStatusTracker.this.postResults();
        }
    }

    public void setListening(boolean z) {
        if (z) {
            this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mWifiNetworkScoreCache, 1);
            this.mWifiNetworkScoreCache.registerListener(this.mCacheListener);
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mHandler);
            this.mConnectivityManager.registerDefaultNetworkCallback(this.mDefaultNetworkCallback, this.mHandler);
            return;
        }
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mWifiNetworkScoreCache);
        this.mWifiNetworkScoreCache.unregisterListener();
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        this.mConnectivityManager.unregisterNetworkCallback(this.mDefaultNetworkCallback);
    }

    public void fetchInitialState() {
        if (this.mWifiManager == null) {
            return;
        }
        updateWifiState();
        boolean z = true;
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(1);
        z = (networkInfo == null || !networkInfo.isConnected()) ? false : false;
        this.connected = z;
        this.mWifiInfo = null;
        this.ssid = null;
        if (z) {
            WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            this.mWifiInfo = connectionInfo;
            if (connectionInfo != null) {
                if (connectionInfo.isPasspointAp() || this.mWifiInfo.isOsuAp()) {
                    this.ssid = this.mWifiInfo.getPasspointProviderFriendlyName();
                } else {
                    this.ssid = getValidSsid(this.mWifiInfo);
                }
                this.isCarrierMerged = this.mWifiInfo.isCarrierMerged();
                this.subId = this.mWifiInfo.getSubscriptionId();
                updateRssi(this.mWifiInfo.getRssi());
                maybeRequestNetworkScore();
            }
        }
        updateStatusLabel();
    }

    public void handleBroadcast(Intent intent) {
        if (this.mWifiManager != null && intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            updateWifiState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWifiInfo(WifiInfo wifiInfo) {
        updateWifiState();
        this.connected = wifiInfo != null;
        this.mWifiInfo = wifiInfo;
        this.ssid = null;
        if (wifiInfo != null) {
            if (wifiInfo.isPasspointAp() || this.mWifiInfo.isOsuAp()) {
                this.ssid = this.mWifiInfo.getPasspointProviderFriendlyName();
            } else {
                this.ssid = getValidSsid(this.mWifiInfo);
            }
            this.isCarrierMerged = this.mWifiInfo.isCarrierMerged();
            this.subId = this.mWifiInfo.getSubscriptionId();
            updateRssi(this.mWifiInfo.getRssi());
            maybeRequestNetworkScore();
        }
    }

    private void updateWifiState() {
        int wifiState = this.mWifiManager.getWifiState();
        this.state = wifiState;
        this.enabled = wifiState == 3;
    }

    private void updateRssi(int i) {
        this.rssi = i;
        this.level = this.mWifiManager.calculateSignalLevel(i);
    }

    private void maybeRequestNetworkScore() {
        NetworkKey createFromWifiInfo = NetworkKey.createFromWifiInfo(this.mWifiInfo);
        if (this.mWifiNetworkScoreCache.getScoredNetwork(createFromWifiInfo) == null) {
            this.mNetworkScoreManager.requestScores(new NetworkKey[]{createFromWifiInfo});
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatusLabel() {
        NetworkCapabilities networkCapabilities;
        NetworkCapabilities networkCapabilities2;
        if (this.mWifiManager == null) {
            return;
        }
        this.isDefaultNetwork = false;
        NetworkCapabilities networkCapabilities3 = this.mDefaultNetworkCapabilities;
        if (networkCapabilities3 != null) {
            boolean hasTransport = networkCapabilities3.hasTransport(1);
            boolean z = this.mDefaultNetworkCapabilities.hasTransport(0) && Utils.tryGetWifiInfoForVcn(this.mDefaultNetworkCapabilities) != null;
            if (hasTransport || z) {
                this.isDefaultNetwork = true;
            }
        }
        if (this.isDefaultNetwork) {
            networkCapabilities = this.mDefaultNetworkCapabilities;
        } else {
            networkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
        }
        this.isCaptivePortal = false;
        if (networkCapabilities != null) {
            if (networkCapabilities.hasCapability(17)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_status_sign_in_required);
                this.isCaptivePortal = true;
                return;
            } else if (networkCapabilities.hasCapability(24)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_limited_connection);
                return;
            } else if (!networkCapabilities.hasCapability(16)) {
                Settings.Global.getString(this.mContext.getContentResolver(), "private_dns_mode");
                if (networkCapabilities.isPrivateDnsBroken()) {
                    this.statusLabel = this.mContext.getString(R$string.private_dns_broken);
                    return;
                } else {
                    this.statusLabel = this.mContext.getString(R$string.wifi_status_no_internet);
                    return;
                }
            } else if (!this.isDefaultNetwork && (networkCapabilities2 = this.mDefaultNetworkCapabilities) != null && networkCapabilities2.hasTransport(0)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_connected_low_quality);
                return;
            }
        }
        ScoredNetwork scoredNetwork = this.mWifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(this.mWifiInfo));
        this.statusLabel = scoredNetwork == null ? null : AccessPoint.getSpeedLabel(this.mContext, scoredNetwork, this.rssi);
    }

    private String getValidSsid(WifiInfo wifiInfo) {
        String ssid = wifiInfo.getSSID();
        if (ssid == null || "<unknown ssid>".equals(ssid)) {
            return null;
        }
        return ssid;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recordLastWifiNetwork(String str) {
        String[] strArr = this.mHistory;
        int i = this.mHistoryIndex;
        strArr[i] = str;
        this.mHistoryIndex = (i + 1) % 32;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postResults() {
        this.mCallback.run();
    }
}
