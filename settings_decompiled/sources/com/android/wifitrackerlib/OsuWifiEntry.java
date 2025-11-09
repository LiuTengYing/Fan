package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.ProvisioningCallback;
import android.os.Handler;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Pair;
import androidx.core.os.BuildCompat;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class OsuWifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private boolean mHasAddConfigUserRestriction;
    private boolean mIsAlreadyProvisioned;
    private final String mKey;
    private final OsuProvider mOsuProvider;
    private String mOsuStatusString;
    private String mSsid;
    private final UserManager mUserManager;

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getMacAddress() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getScanResultDescription() {
        return "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OsuWifiEntry(WifiTrackerInjector wifiTrackerInjector, Context context, Handler handler, OsuProvider osuProvider, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, z);
        this.mCurrentScanResults = new ArrayList();
        this.mIsAlreadyProvisioned = false;
        this.mHasAddConfigUserRestriction = false;
        Preconditions.checkNotNull(osuProvider, "Cannot construct with null osuProvider!");
        this.mContext = context;
        this.mOsuProvider = osuProvider;
        this.mKey = osuProviderToOsuWifiEntryKey(osuProvider);
        UserManager userManager = wifiTrackerInjector.getUserManager();
        this.mUserManager = userManager;
        if (!BuildCompat.isAtLeastT() || userManager == null) {
            return;
        }
        this.mHasAddConfigUserRestriction = userManager.hasUserRestriction("no_add_wifi_config");
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getTitle() {
        String friendlyName = this.mOsuProvider.getFriendlyName();
        if (TextUtils.isEmpty(friendlyName)) {
            if (!TextUtils.isEmpty(this.mSsid)) {
                return this.mSsid;
            }
            Uri serverUri = this.mOsuProvider.getServerUri();
            if (serverUri != null) {
                return serverUri.toString();
            }
            return "";
        }
        return friendlyName;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSummary(boolean z) {
        String string;
        if (hasAdminRestrictions()) {
            return this.mContext.getString(R$string.wifitrackerlib_admin_restricted_network);
        }
        String str = this.mOsuStatusString;
        if (str != null) {
            return str;
        }
        if (!isAlreadyProvisioned()) {
            return this.mContext.getString(R$string.wifitrackerlib_tap_to_sign_up);
        }
        if (z) {
            string = this.mContext.getString(R$string.wifitrackerlib_wifi_passpoint_expired);
        } else {
            string = this.mContext.getString(R$string.wifitrackerlib_tap_to_renew_subscription_and_connect);
        }
        return string;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized String getSsid() {
        return this.mSsid;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized boolean canConnect() {
        boolean z = false;
        if (hasAdminRestrictions()) {
            return false;
        }
        if (this.mLevel != -1) {
            if (getConnectedState() == 0) {
                z = true;
            }
        }
        return z;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public synchronized void connect(WifiEntry.ConnectCallback connectCallback) {
        this.mConnectCallback = connectCallback;
        this.mWifiManager.stopRestrictingAutoJoinToSubscriptionId();
        this.mWifiManager.startSubscriptionProvisioning(this.mOsuProvider, this.mContext.getMainExecutor(), new OsuWifiEntryProvisioningCallback());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mCurrentScanResults.clear();
        this.mCurrentScanResults.addAll(list);
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
        if (bestScanResultByLevel != null) {
            this.mSsid = bestScanResultByLevel.SSID;
            if (getConnectedState() == 0) {
                this.mLevel = this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level);
            }
        } else {
            this.mLevel = -1;
        }
        notifyOnUpdated();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String osuProviderToOsuWifiEntryKey(OsuProvider osuProvider) {
        Preconditions.checkNotNull(osuProvider, "Cannot create key with null OsuProvider!");
        return "OsuWifiEntry:" + osuProvider.getFriendlyName() + "," + osuProvider.getServerUri().toString();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    protected boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return wifiInfo.isOsuAp() && TextUtils.equals(wifiInfo.getPasspointProviderFriendlyName(), this.mOsuProvider.getFriendlyName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OsuProvider getOsuProvider() {
        return this.mOsuProvider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean isAlreadyProvisioned() {
        return this.mIsAlreadyProvisioned;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setAlreadyProvisioned(boolean z) {
        this.mIsAlreadyProvisioned = z;
    }

    private boolean hasAdminRestrictions() {
        return this.mHasAddConfigUserRestriction && !this.mIsAlreadyProvisioned;
    }

    /* loaded from: classes2.dex */
    class OsuWifiEntryProvisioningCallback extends ProvisioningCallback {
        OsuWifiEntryProvisioningCallback() {
        }

        public void onProvisioningFailure(int i) {
            synchronized (OsuWifiEntry.this) {
                if (TextUtils.equals(OsuWifiEntry.this.mOsuStatusString, OsuWifiEntry.this.mContext.getString(R$string.wifitrackerlib_osu_completing_sign_up))) {
                    OsuWifiEntry osuWifiEntry = OsuWifiEntry.this;
                    osuWifiEntry.mOsuStatusString = osuWifiEntry.mContext.getString(R$string.wifitrackerlib_osu_sign_up_failed);
                } else {
                    OsuWifiEntry osuWifiEntry2 = OsuWifiEntry.this;
                    osuWifiEntry2.mOsuStatusString = osuWifiEntry2.mContext.getString(R$string.wifitrackerlib_osu_connect_failed);
                }
            }
            WifiEntry.ConnectCallback connectCallback = OsuWifiEntry.this.mConnectCallback;
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
            OsuWifiEntry.this.notifyOnUpdated();
        }

        public void onProvisioningStatus(int i) {
            String format;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    format = String.format(OsuWifiEntry.this.mContext.getString(R$string.wifitrackerlib_osu_opening_provider), OsuWifiEntry.this.getTitle());
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    format = OsuWifiEntry.this.mContext.getString(R$string.wifitrackerlib_osu_completing_sign_up);
                    break;
                default:
                    format = null;
                    break;
            }
            synchronized (OsuWifiEntry.this) {
                boolean z = TextUtils.equals(OsuWifiEntry.this.mOsuStatusString, format) ? false : true;
                OsuWifiEntry.this.mOsuStatusString = format;
                if (z) {
                    OsuWifiEntry.this.notifyOnUpdated();
                }
            }
        }

        public void onProvisioningComplete() {
            ScanResult bestScanResultByLevel;
            synchronized (OsuWifiEntry.this) {
                OsuWifiEntry osuWifiEntry = OsuWifiEntry.this;
                osuWifiEntry.mOsuStatusString = osuWifiEntry.mContext.getString(R$string.wifitrackerlib_osu_sign_up_complete);
            }
            OsuWifiEntry.this.notifyOnUpdated();
            OsuWifiEntry osuWifiEntry2 = OsuWifiEntry.this;
            PasspointConfiguration passpointConfiguration = (PasspointConfiguration) osuWifiEntry2.mWifiManager.getMatchingPasspointConfigsForOsuProviders(Collections.singleton(osuWifiEntry2.mOsuProvider)).get(OsuWifiEntry.this.mOsuProvider);
            WifiEntry.ConnectCallback connectCallback = OsuWifiEntry.this.mConnectCallback;
            if (passpointConfiguration == null) {
                if (connectCallback != null) {
                    connectCallback.onConnectResult(2);
                    return;
                }
                return;
            }
            String uniqueId = passpointConfiguration.getUniqueId();
            WifiManager wifiManager = OsuWifiEntry.this.mWifiManager;
            Iterator it = wifiManager.getAllMatchingWifiConfigs(wifiManager.getScanResults()).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Pair pair = (Pair) it.next();
                WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
                if (TextUtils.equals(wifiConfiguration.getKey(), uniqueId)) {
                    List list = (List) ((Map) pair.second).get(0);
                    List list2 = (List) ((Map) pair.second).get(1);
                    if (list != null && !list.isEmpty()) {
                        bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
                    } else if (list2 != null && !list2.isEmpty()) {
                        bestScanResultByLevel = Utils.getBestScanResultByLevel(list2);
                    }
                    wifiConfiguration.SSID = "\"" + bestScanResultByLevel.SSID + "\"";
                    OsuWifiEntry.this.mWifiManager.connect(wifiConfiguration, null);
                    return;
                }
            }
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
        }
    }
}
