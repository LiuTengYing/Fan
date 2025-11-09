package com.android.settings.wifi.savedaccesspoints2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.wifitrackerlib.SavedNetworkTracker;
import java.time.ZoneOffset;
/* loaded from: classes2.dex */
public class SavedAccessPointsWifiSettings2 extends DashboardFragment implements SavedNetworkTracker.SavedNetworkTrackerCallback {
    static final String TAG = "SavedAccessPoints2";
    SavedNetworkTracker mSavedNetworkTracker;
    HandlerThread mWorkerThread;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 106;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.wifi_display_saved_access_points2;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SavedAccessPointsPreferenceController2) use(SavedAccessPointsPreferenceController2.class)).setHost(this);
        ((SubscribedAccessPointsPreferenceController2) use(SubscribedAccessPointsPreferenceController2.class)).setHost(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        HandlerThread handlerThread = new HandlerThread("SavedAccessPoints2{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mSavedNetworkTracker = new SavedNetworkTracker(getSettingsLifecycle(), context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsWifiSettings2.1
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        onSavedWifiEntriesChanged();
        onSubscriptionWifiEntriesChanged();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mWorkerThread.quit();
        super.onDestroy();
    }

    public void showWifiPage(String str, CharSequence charSequence) {
        removeDialog(1);
        if (TextUtils.isEmpty(str)) {
            Log.e(TAG, "Not able to show WifiEntry of an empty key");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", str);
        new SubSettingLauncher(getContext()).setTitleText(charSequence).setDestination(WifiNetworkDetailsFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSavedWifiEntriesChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        ((SavedAccessPointsPreferenceController2) use(SavedAccessPointsPreferenceController2.class)).displayPreference(getPreferenceScreen(), this.mSavedNetworkTracker.getSavedWifiEntries());
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSubscriptionWifiEntriesChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        ((SubscribedAccessPointsPreferenceController2) use(SubscribedAccessPointsPreferenceController2.class)).displayPreference(getPreferenceScreen(), this.mSavedNetworkTracker.getSubscriptionWifiEntries());
    }
}
