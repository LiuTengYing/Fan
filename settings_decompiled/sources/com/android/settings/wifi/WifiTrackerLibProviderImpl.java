package com.android.settings.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import androidx.lifecycle.Lifecycle;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
/* loaded from: classes.dex */
public class WifiTrackerLibProviderImpl implements WifiTrackerLibProvider {
    @Override // com.android.settings.wifi.WifiTrackerLibProvider
    public WifiPickerTracker createWifiPickerTracker(Lifecycle lifecycle, Context context, Handler handler, Handler handler2, Clock clock, long j, long j2, WifiPickerTracker.WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        return new WifiPickerTracker(lifecycle, context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), handler, handler2, clock, j, j2, wifiPickerTrackerCallback);
    }

    @Override // com.android.settings.wifi.WifiTrackerLibProvider
    public NetworkDetailsTracker createNetworkDetailsTracker(Lifecycle lifecycle, Context context, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        return NetworkDetailsTracker.createNetworkDetailsTracker(lifecycle, context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), handler, handler2, clock, j, j2, str);
    }
}
