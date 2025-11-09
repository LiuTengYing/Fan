package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.network.CarrierConfigCache;
import com.android.settings.overlay.FeatureFactory;
import com.android.wifitrackerlib.MergedCarrierEntry;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
import java.time.ZoneOffset;
/* loaded from: classes.dex */
public class WifiPickerTrackerHelper implements LifecycleObserver {
    private static final Clock ELAPSED_REALTIME_CLOCK = new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.WifiPickerTrackerHelper.1
        public long millis() {
            return SystemClock.elapsedRealtime();
        }
    };
    protected final CarrierConfigCache mCarrierConfigCache;
    protected final WifiManager mWifiManager;
    protected WifiPickerTracker mWifiPickerTracker;
    protected HandlerThread mWorkerThread;

    public WifiPickerTrackerHelper(Lifecycle lifecycle, Context context, WifiPickerTracker.WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        if (lifecycle == null) {
            throw new IllegalArgumentException("lifecycle must be non-null.");
        }
        lifecycle.addObserver(this);
        HandlerThread handlerThread = new HandlerThread("WifiPickerTrackerHelper{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWifiPickerTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createWifiPickerTracker(lifecycle, context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), ELAPSED_REALTIME_CLOCK, 15000L, 10000L, wifiPickerTrackerCallback);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mCarrierConfigCache = CarrierConfigCache.getInstance(context);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mWorkerThread.quit();
    }

    public WifiPickerTracker getWifiPickerTracker() {
        return this.mWifiPickerTracker;
    }

    public boolean isCarrierNetworkProvisionEnabled(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigCache.getConfigForSubId(i);
        if (configForSubId == null) {
            Log.e("WifiPickerTrackerHelper", "Could not get carrier config, subId:" + i);
            return false;
        }
        boolean z = configForSubId.getBoolean("carrier_provisions_wifi_merged_networks_bool");
        Log.i("WifiPickerTrackerHelper", "isCarrierNetworkProvisionEnabled:" + z);
        return z;
    }

    public boolean isCarrierNetworkEnabled() {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null) {
            Log.e("WifiPickerTrackerHelper", "Failed to get MergedCarrierEntry to query enabled status");
            return false;
        }
        boolean isEnabled = mergedCarrierEntry.isEnabled();
        Log.i("WifiPickerTrackerHelper", "isCarrierNetworkEnabled:" + isEnabled);
        return isEnabled;
    }

    public void setCarrierNetworkEnabled(boolean z) {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null) {
            Log.e("WifiPickerTrackerHelper", "Unable to get MergedCarrierEntry to set enabled status");
            return;
        }
        Log.i("WifiPickerTrackerHelper", "setCarrierNetworkEnabled:" + z);
        mergedCarrierEntry.setEnabled(z);
    }

    public boolean connectCarrierNetwork(WifiEntry.ConnectCallback connectCallback) {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null || !mergedCarrierEntry.canConnect()) {
            return false;
        }
        mergedCarrierEntry.connect(connectCallback);
        return true;
    }

    public boolean isCarrierNetworkActive() {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        return mergedCarrierEntry != null && mergedCarrierEntry.isDefaultNetwork();
    }

    public String getCarrierNetworkSsid() {
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry == null) {
            return null;
        }
        return mergedCarrierEntry.getSsid();
    }

    public int getCarrierNetworkLevel() {
        int level;
        MergedCarrierEntry mergedCarrierEntry = this.mWifiPickerTracker.getMergedCarrierEntry();
        if (mergedCarrierEntry != null && (level = mergedCarrierEntry.getLevel()) >= 0) {
            return level;
        }
        return 0;
    }

    @VisibleForTesting
    void setWifiPickerTracker(WifiPickerTracker wifiPickerTracker) {
        this.mWifiPickerTracker = wifiPickerTracker;
    }

    @VisibleForTesting
    void setWorkerThread(HandlerThread handlerThread) {
        this.mWorkerThread = handlerThread;
    }
}
