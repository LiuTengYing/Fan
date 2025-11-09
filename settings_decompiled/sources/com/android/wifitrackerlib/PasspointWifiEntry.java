package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class PasspointWifiEntry extends WifiEntry implements WifiEntry.WifiEntryCallback {
    private final Context mContext;
    private final List<ScanResult> mCurrentHomeScanResults;
    private final List<ScanResult> mCurrentRoamingScanResults;
    private final String mFqdn;
    private final String mFriendlyName;
    private final WifiTrackerInjector mInjector;
    private boolean mIsRoaming;
    private final String mKey;
    private int mMeteredOverride;
    private OsuWifiEntry mOsuWifiEntry;
    private PasspointConfiguration mPasspointConfig;
    private boolean mShouldAutoOpenCaptivePortal;
    protected long mSubscriptionExpirationTimeInMillis;
    private List<Integer> mTargetSecurityTypes;
    private WifiConfiguration mWifiConfig;
    private int ncUpdateCount;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getScanResultDescription() {
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PasspointWifiEntry(WifiTrackerInjector wifiTrackerInjector, Context context, Handler handler, PasspointConfiguration passpointConfiguration, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, z);
        this.mCurrentHomeScanResults = new ArrayList();
        this.mCurrentRoamingScanResults = new ArrayList();
        this.mTargetSecurityTypes = List.of(11, 12);
        this.mIsRoaming = false;
        this.mShouldAutoOpenCaptivePortal = false;
        this.ncUpdateCount = 0;
        this.mMeteredOverride = 0;
        Preconditions.checkNotNull(passpointConfiguration, "Cannot construct with null PasspointConfiguration!");
        this.mInjector = wifiTrackerInjector;
        this.mContext = context;
        this.mPasspointConfig = passpointConfiguration;
        this.mKey = uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId());
        String fqdn = passpointConfiguration.getHomeSp().getFqdn();
        this.mFqdn = fqdn;
        Preconditions.checkNotNull(fqdn, "Cannot construct with null PasspointConfiguration FQDN!");
        this.mFriendlyName = passpointConfiguration.getHomeSp().getFriendlyName();
        this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
        this.mMeteredOverride = this.mPasspointConfig.getMeteredOverride();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PasspointWifiEntry(WifiTrackerInjector wifiTrackerInjector, Context context, Handler handler, WifiConfiguration wifiConfiguration, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, z);
        this.mCurrentHomeScanResults = new ArrayList();
        this.mCurrentRoamingScanResults = new ArrayList();
        this.mTargetSecurityTypes = List.of(11, 12);
        this.mIsRoaming = false;
        this.mShouldAutoOpenCaptivePortal = false;
        this.ncUpdateCount = 0;
        this.mMeteredOverride = 0;
        Preconditions.checkNotNull(wifiConfiguration, "Cannot construct with null WifiConfiguration!");
        if (!wifiConfiguration.isPasspoint()) {
            throw new IllegalArgumentException("Given WifiConfiguration is not for Passpoint!");
        }
        this.mInjector = wifiTrackerInjector;
        this.mContext = context;
        this.mWifiConfig = wifiConfiguration;
        this.mKey = uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
        String str = wifiConfiguration.FQDN;
        this.mFqdn = str;
        Preconditions.checkNotNull(str, "Cannot construct with null WifiConfiguration FQDN!");
        this.mFriendlyName = this.mWifiConfig.providerFriendlyName;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getConnectedState() {
        OsuWifiEntry osuWifiEntry;
        if (isExpired() && super.getConnectedState() == 0 && (osuWifiEntry = this.mOsuWifiEntry) != null) {
            return osuWifiEntry.getConnectedState();
        }
        return super.getConnectedState();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getTitle() {
        return this.mFriendlyName;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSummary(boolean z) {
        StringJoiner stringJoiner;
        String disconnectedDescription;
        stringJoiner = new StringJoiner(this.mContext.getString(R$string.wifitrackerlib_summary_separator));
        if (isExpired()) {
            OsuWifiEntry osuWifiEntry = this.mOsuWifiEntry;
            if (osuWifiEntry != null) {
                stringJoiner.add(osuWifiEntry.getSummary(z));
            } else {
                stringJoiner.add(this.mContext.getString(R$string.wifitrackerlib_wifi_passpoint_expired));
            }
        } else {
            int connectedState = getConnectedState();
            if (connectedState == 0) {
                disconnectedDescription = Utils.getDisconnectedDescription(this.mInjector, this.mContext, this.mWifiConfig, this.mForSavedNetworksPage, z);
                this.ncUpdateCount = 0;
            } else if (connectedState == 1) {
                disconnectedDescription = Utils.getConnectingDescription(this.mContext, this.mNetworkInfo);
            } else if (connectedState == 2) {
                disconnectedDescription = Utils.getConnectedDescription(this.mContext, this.mWifiConfig, this.mNetworkCapabilities, this.mIsDefaultNetwork, this.mIsLowQuality, this.ncUpdateCount);
            } else {
                Log.e("PasspointWifiEntry", "getConnectedState() returned unknown state: " + connectedState);
                disconnectedDescription = null;
            }
            if (!TextUtils.isEmpty(disconnectedDescription)) {
                stringJoiner.add(disconnectedDescription);
            }
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized CharSequence getSecondSummary() {
        return getConnectedState() == 2 ? Utils.getImsiProtectionDescription(this.mContext, this.mWifiConfig) : "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSsid() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            return WifiInfo.sanitizeSsid(wifiInfo.getSSID());
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        return wifiConfiguration != null ? WifiInfo.sanitizeSsid(wifiConfiguration.SSID) : null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized Set<String> getAllUtf8Ssids() {
        ArraySet arraySet;
        arraySet = new ArraySet();
        for (ScanResult scanResult : this.mCurrentHomeScanResults) {
            arraySet.add(scanResult.SSID);
        }
        for (ScanResult scanResult2 : this.mCurrentRoamingScanResults) {
            arraySet.add(scanResult2.SSID);
        }
        return arraySet;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized List<Integer> getSecurityTypes() {
        return new ArrayList(this.mTargetSecurityTypes);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            String macAddress = wifiInfo.getMacAddress();
            if (!TextUtils.isEmpty(macAddress) && !TextUtils.equals(macAddress, "02:00:00:00:00:00")) {
                return macAddress;
            }
        }
        if (this.mWifiConfig != null && getPrivacy() == 1) {
            return this.mWifiConfig.getRandomizedMacAddress().toString();
        }
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        if (factoryMacAddresses.length > 0) {
            return factoryMacAddresses[0];
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x000e, code lost:
        if (r0.meteredHint != false) goto L11;
     */
    @Override // com.android.wifitrackerlib.WifiEntry
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean isMetered() {
        /*
            r2 = this;
            monitor-enter(r2)
            int r0 = r2.getMeteredChoice()     // Catch: java.lang.Throwable -> L14
            r1 = 1
            if (r0 == r1) goto L12
            android.net.wifi.WifiConfiguration r0 = r2.mWifiConfig     // Catch: java.lang.Throwable -> L14
            if (r0 == 0) goto L11
            boolean r0 = r0.meteredHint     // Catch: java.lang.Throwable -> L14
            if (r0 == 0) goto L11
            goto L12
        L11:
            r1 = 0
        L12:
            monitor-exit(r2)
            return r1
        L14:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.PasspointWifiEntry.isMetered():boolean");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isSuggestion() {
        boolean z;
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration != null) {
            z = wifiConfiguration.fromWifiNetworkSuggestion;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isSubscription() {
        return this.mPasspointConfig != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canConnect() {
        boolean z = true;
        if (isExpired()) {
            OsuWifiEntry osuWifiEntry = this.mOsuWifiEntry;
            if (osuWifiEntry == null || !osuWifiEntry.canConnect()) {
                z = false;
            }
            return z;
        }
        if (this.mLevel == -1 || getConnectedState() != 0 || this.mWifiConfig == null) {
            z = false;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void connect(WifiEntry.ConnectCallback connectCallback) {
        OsuWifiEntry osuWifiEntry;
        if (isExpired() && (osuWifiEntry = this.mOsuWifiEntry) != null) {
            osuWifiEntry.connect(connectCallback);
            return;
        }
        this.mShouldAutoOpenCaptivePortal = true;
        this.mConnectCallback = connectCallback;
        if (this.mWifiConfig == null) {
            new WifiEntry.ConnectActionListener().onFailure(0);
        }
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        this.mWifiManager.connect(this.mWifiConfig, new WifiEntry.ConnectActionListener());
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canDisconnect() {
        return getConnectedState() == 2;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void disconnect(final WifiEntry.DisconnectCallback disconnectCallback) {
        if (canDisconnect()) {
            this.mCalledDisconnect = true;
            this.mDisconnectCallback = disconnectCallback;
            this.mCallbackHandler.postDelayed(new Runnable() { // from class: com.android.wifitrackerlib.PasspointWifiEntry$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PasspointWifiEntry.this.lambda$disconnect$0(disconnectCallback);
                }
            }, 10000L);
            this.mWifiManager.disableEphemeralNetwork(this.mFqdn);
            this.mWifiManager.disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnect$0(WifiEntry.DisconnectCallback disconnectCallback) {
        if (disconnectCallback == null || !this.mCalledDisconnect) {
            return;
        }
        disconnectCallback.onDisconnectResult(1);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canForget() {
        boolean z;
        if (!isSuggestion()) {
            z = this.mPasspointConfig != null;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void forget(WifiEntry.ForgetCallback forgetCallback) {
        if (canForget()) {
            this.mForgetCallback = forgetCallback;
            this.mWifiManager.removePasspointConfiguration(this.mPasspointConfig.getHomeSp().getFqdn());
            new WifiEntry.ForgetActionListener().onSuccess();
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getMeteredChoice() {
        int i = this.mMeteredOverride;
        if (i == 1) {
            return 1;
        }
        return i == 2 ? 2 : 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSetMeteredChoice() {
        boolean z;
        if (!isSuggestion()) {
            z = this.mPasspointConfig != null;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setMeteredChoice(int i) {
        if (this.mPasspointConfig == null || !canSetMeteredChoice()) {
            return;
        }
        if (i == 0) {
            this.mMeteredOverride = 0;
        } else if (i == 1) {
            this.mMeteredOverride = 1;
        } else if (i != 2) {
            return;
        } else {
            this.mMeteredOverride = 2;
        }
        this.mWifiManager.setPasspointMeteredOverride(this.mPasspointConfig.getHomeSp().getFqdn(), this.mMeteredOverride);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSetPrivacy() {
        boolean z;
        if (!isSuggestion()) {
            z = this.mPasspointConfig != null;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized int getPrivacy() {
        PasspointConfiguration passpointConfiguration = this.mPasspointConfig;
        if (passpointConfiguration == null) {
            return 1;
        }
        return passpointConfiguration.isMacRandomizationEnabled() ? 1 : 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setPrivacy(int i) {
        if (this.mPasspointConfig != null && canSetPrivacy()) {
            this.mWifiManager.setMacRandomizationSettingPasspointEnabled(this.mPasspointConfig.getHomeSp().getFqdn(), i != 0);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean isAutoJoinEnabled() {
        PasspointConfiguration passpointConfiguration = this.mPasspointConfig;
        if (passpointConfiguration != null) {
            return passpointConfiguration.isAutojoinEnabled();
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration != null) {
            return wifiConfiguration.allowAutojoin;
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSetAutoJoinEnabled() {
        boolean z;
        if (this.mPasspointConfig == null) {
            z = this.mWifiConfig != null;
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void setAutoJoinEnabled(boolean z) {
        PasspointConfiguration passpointConfiguration = this.mPasspointConfig;
        if (passpointConfiguration != null) {
            this.mWifiManager.allowAutojoinPasspoint(passpointConfiguration.getHomeSp().getFqdn(), z);
        } else {
            WifiConfiguration wifiConfiguration = this.mWifiConfig;
            if (wifiConfiguration != null) {
                this.mWifiManager.allowAutojoin(wifiConfiguration.networkId, z);
            }
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSecurityString(boolean z) {
        return this.mContext.getString(R$string.wifitrackerlib_wifi_security_passpoint);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getStandardString() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            return Utils.getStandardString(this.mContext, wifiInfo.getWifiStandard());
        } else if (!this.mCurrentHomeScanResults.isEmpty()) {
            return Utils.getStandardString(this.mContext, this.mCurrentHomeScanResults.get(0).getWifiStandard());
        } else if (this.mCurrentRoamingScanResults.isEmpty()) {
            return "";
        } else {
            return Utils.getStandardString(this.mContext, this.mCurrentRoamingScanResults.get(0).getWifiStandard());
        }
    }

    public synchronized boolean isExpired() {
        if (this.mSubscriptionExpirationTimeInMillis <= 0) {
            return false;
        }
        return System.currentTimeMillis() >= this.mSubscriptionExpirationTimeInMillis;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updatePasspointConfig(PasspointConfiguration passpointConfiguration) {
        this.mPasspointConfig = passpointConfiguration;
        if (passpointConfiguration != null) {
            this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
            this.mMeteredOverride = passpointConfiguration.getMeteredOverride();
        }
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(WifiConfiguration wifiConfiguration, List<ScanResult> list, List<ScanResult> list2) throws IllegalArgumentException {
        this.mIsRoaming = false;
        this.mWifiConfig = wifiConfiguration;
        this.mCurrentHomeScanResults.clear();
        this.mCurrentRoamingScanResults.clear();
        if (list != null) {
            this.mCurrentHomeScanResults.addAll(list);
        }
        if (list2 != null) {
            this.mCurrentRoamingScanResults.addAll(list2);
        }
        if (this.mWifiConfig != null) {
            ArrayList arrayList = new ArrayList();
            if (list != null && !list.isEmpty()) {
                arrayList.addAll(list);
            } else if (list2 != null && !list2.isEmpty()) {
                arrayList.addAll(list2);
                this.mIsRoaming = true;
            }
            ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(arrayList);
            if (bestScanResultByLevel != null) {
                WifiConfiguration wifiConfiguration2 = this.mWifiConfig;
                wifiConfiguration2.SSID = "\"" + bestScanResultByLevel.SSID + "\"";
            }
            if (getConnectedState() == 0) {
                this.mLevel = bestScanResultByLevel != null ? this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level) : -1;
            }
        } else {
            this.mLevel = -1;
        }
        notifyOnUpdated();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected synchronized void updateSecurityTypes() {
        int currentSecurityType;
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null || (currentSecurityType = wifiInfo.getCurrentSecurityType()) == -1) {
            return;
        }
        this.mTargetSecurityTypes = Collections.singletonList(Integer.valueOf(currentSecurityType));
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo.isPasspointAp()) {
            return TextUtils.equals(wifiInfo.getPasspointFqdn(), this.mFqdn);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateNetworkCapabilities(networkCapabilities);
        if (this.ncUpdateCount == -1 && networkCapabilities.hasCapability(11)) {
            Log.d("PasspointWifiEntry", "NetworkCapabilities always NOT_METERED, reset ncUpdateCount to 1");
            this.ncUpdateCount = 1;
        }
        int i = this.ncUpdateCount;
        if (i < 2) {
            this.ncUpdateCount = i + 1;
            Log.d("PasspointWifiEntry", "ncUpdateCount is: " + this.ncUpdateCount);
        }
        if (networkCapabilities != null) {
            Log.d("PasspointWifiEntry", "Current NetworkCapabilities is: " + networkCapabilities.toString());
            if (this.ncUpdateCount == 1 && networkCapabilities.hasCapability(11)) {
                this.ncUpdateCount = -1;
            }
        }
        if (canSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            signIn(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String uniqueIdToPasspointWifiEntryKey(String str) {
        Preconditions.checkNotNull(str, "Cannot create key with null unique id!");
        return "PasspointWifiEntry:" + str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getNetworkSelectionDescription() {
        return Utils.getNetworkSelectionDescription(this.mWifiConfig);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setOsuWifiEntry(OsuWifiEntry osuWifiEntry) {
        this.mOsuWifiEntry = osuWifiEntry;
        if (osuWifiEntry != null) {
            osuWifiEntry.setListener(this);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
        notifyOnUpdated();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canSignIn() {
        boolean z;
        NetworkCapabilities networkCapabilities = this.mNetworkCapabilities;
        if (networkCapabilities != null) {
            z = networkCapabilities.hasCapability(17);
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void signIn(WifiEntry.SignInCallback signInCallback) {
        if (canSignIn()) {
            NonSdkApiWrapper.startCaptivePortalApp((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class), this.mWifiManager.getCurrentNetwork());
        }
    }
}
