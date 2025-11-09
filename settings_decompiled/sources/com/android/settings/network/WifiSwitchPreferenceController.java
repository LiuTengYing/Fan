package com.android.settings.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.network.InternetUpdater;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.GenericSwitchController;
import com.android.settings.widget.RestrictedSwitchPreferences;
import com.android.settings.widget.SummaryUpdater;
import com.android.settings.wifi.WifiEnabler;
import com.android.settings.wifi.WifiSummaryUpdater;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class WifiSwitchPreferenceController extends AbstractPreferenceController implements LifecycleObserver, SummaryUpdater.OnSummaryChangeListener, InternetUpdater.InternetChangeListener {
    private static Map<Integer, Integer> sSummaryMap;
    private int mInternetType;
    private InternetUpdater mInternetUpdater;
    boolean mIsChangeWifiStateAllowed;
    private RestrictedSwitchPreferences mPreference;
    private WifiSummaryUpdater mSummaryHelper;
    WifiEnabler mWifiEnabler;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "main_toggle_wifi";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public WifiSwitchPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle == null) {
            throw new IllegalArgumentException("Lifecycle must be set");
        }
        lifecycle.addObserver(this);
        this.mIsChangeWifiStateAllowed = WifiEnterpriseRestrictionUtils.isChangeWifiStateAllowed(context);
        this.mSummaryHelper = new WifiSummaryUpdater(context, this);
        InternetUpdater internetUpdater = new InternetUpdater(context, lifecycle, this);
        this.mInternetUpdater = internetUpdater;
        this.mInternetType = internetUpdater.getInternetType();
    }

    static {
        HashMap hashMap = new HashMap();
        sSummaryMap = hashMap;
        hashMap.put(0, Integer.valueOf(R$string.condition_airplane_title));
        sSummaryMap.put(1, Integer.valueOf(R$string.networks_available));
        sSummaryMap.put(2, 0);
        sSummaryMap.put(3, 0);
        sSummaryMap.put(4, Integer.valueOf(R$string.to_switch_networks_disconnect_ethernet));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RestrictedSwitchPreferences restrictedSwitchPreferences = (RestrictedSwitchPreferences) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = restrictedSwitchPreferences;
        if (restrictedSwitchPreferences == null) {
            return;
        }
        restrictedSwitchPreferences.setChecked(isWifiEnabled());
        if (this.mIsChangeWifiStateAllowed) {
            return;
        }
        this.mPreference.setEnabled(false);
        this.mPreference.setSummary(R$string.not_allowed_by_ent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        RestrictedSwitchPreferences restrictedSwitchPreferences = this.mPreference;
        if (restrictedSwitchPreferences == null || !this.mIsChangeWifiStateAllowed) {
            return;
        }
        this.mWifiEnabler = new WifiEnabler(this.mContext, new GenericSwitchController(restrictedSwitchPreferences), FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.teardownSwitchController();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.resume(this.mContext);
        }
        this.mSummaryHelper.register(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.pause();
        }
        this.mSummaryHelper.register(false);
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
        if (wifiManager == null) {
            return false;
        }
        return wifiManager.isWifiEnabled();
    }

    @Override // com.android.settings.widget.SummaryUpdater.OnSummaryChangeListener
    public void onSummaryChanged(String str) {
        RestrictedSwitchPreferences restrictedSwitchPreferences;
        if (this.mInternetType != 2 || (restrictedSwitchPreferences = this.mPreference) == null) {
            return;
        }
        restrictedSwitchPreferences.setSummary(str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        RestrictedSwitchPreferences restrictedSwitchPreferences = this.mPreference;
        if (restrictedSwitchPreferences == null) {
            return;
        }
        if (this.mInternetType == 2) {
            restrictedSwitchPreferences.setSummary(this.mSummaryHelper.getSummary());
        } else {
            restrictedSwitchPreferences.setChecked(isWifiEnabled());
        }
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onInternetTypeChanged(int i) {
        boolean z = i != this.mInternetType;
        this.mInternetType = i;
        if (z) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.WifiSwitchPreferenceController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WifiSwitchPreferenceController.this.lambda$onInternetTypeChanged$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onInternetTypeChanged$0() {
        updateState(this.mPreference);
    }

    void updateCellularSummary() {
        SubscriptionInfo defaultDataSubscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null || (defaultDataSubscriptionInfo = subscriptionManager.getDefaultDataSubscriptionInfo()) == null) {
            return;
        }
        this.mPreference.setSummary(SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultDataSubscriptionInfo, this.mContext));
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onAirplaneModeChanged(boolean z) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.WifiSwitchPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                WifiSwitchPreferenceController.this.lambda$onAirplaneModeChanged$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAirplaneModeChanged$1() {
        updateState(this.mPreference);
    }
}
