package com.android.settings.wifi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.ZoneOffset;
/* loaded from: classes.dex */
public class WifiConnectionPreferenceController extends AbstractPreferenceController implements WifiPickerTracker.WifiPickerTrackerCallback, LifecycleObserver {
    private int mMetricsCategory;
    private Context mPrefContext;
    private WifiEntryPreference mPreference;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private UpdateListener mUpdateListener;
    public WifiPickerTracker mWifiPickerTracker;
    private HandlerThread mWorkerThread;
    private int order;

    /* loaded from: classes.dex */
    public interface UpdateListener {
        void onChildrenUpdated();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "active_wifi_connection";
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    public WifiConnectionPreferenceController(Context context, Lifecycle lifecycle, UpdateListener updateListener, String str, int i, int i2) {
        super(context);
        lifecycle.addObserver(this);
        this.mUpdateListener = updateListener;
        this.mPreferenceGroupKey = str;
        this.order = i;
        this.mMetricsCategory = i2;
        HandlerThread handlerThread = new HandlerThread("WifiConnPrefCtrl{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWifiPickerTracker = FeatureFactory.getFactory(context).getWifiTrackerLibProvider().createWifiPickerTracker(lifecycle, context, new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.WifiConnectionPreferenceController.1
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mWorkerThread.quit();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mWifiPickerTracker.getConnectedWifiEntry() != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        this.mPrefContext = preferenceScreen.getContext();
        update();
    }

    private void updatePreference(final WifiEntry wifiEntry) {
        WifiEntryPreference wifiEntryPreference = this.mPreference;
        if (wifiEntryPreference != null) {
            this.mPreferenceGroup.removePreference(wifiEntryPreference);
            this.mPreference = null;
        }
        if (wifiEntry == null || this.mPrefContext == null) {
            return;
        }
        WifiEntryPreference wifiEntryPreference2 = new WifiEntryPreference(this.mPrefContext, wifiEntry);
        this.mPreference = wifiEntryPreference2;
        wifiEntryPreference2.setKey("active_wifi_connection");
        this.mPreference.refresh();
        this.mPreference.setOrder(this.order);
        this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.WifiConnectionPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$updatePreference$0;
                lambda$updatePreference$0 = WifiConnectionPreferenceController.this.lambda$updatePreference$0(wifiEntry, preference);
                return lambda$updatePreference$0;
            }
        });
        this.mPreferenceGroup.addPreference(this.mPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updatePreference$0(WifiEntry wifiEntry, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(this.mPrefContext).setTitleRes(R$string.pref_title_network_details).setDestination(WifiNetworkDetailsFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(this.mMetricsCategory).launch();
        return true;
    }

    private void update() {
        WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        if (connectedWifiEntry == null) {
            updatePreference(null);
        } else {
            WifiEntryPreference wifiEntryPreference = this.mPreference;
            if (wifiEntryPreference == null || !wifiEntryPreference.getWifiEntry().equals(connectedWifiEntry)) {
                updatePreference(connectedWifiEntry);
            } else {
                WifiEntryPreference wifiEntryPreference2 = this.mPreference;
                if (wifiEntryPreference2 != null) {
                    wifiEntryPreference2.refresh();
                }
            }
        }
        this.mUpdateListener.onChildrenUpdated();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        update();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        update();
    }
}
