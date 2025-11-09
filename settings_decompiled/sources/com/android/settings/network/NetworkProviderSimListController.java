package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class NetworkProviderSimListController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;
    final BroadcastReceiver mDataSubscriptionChangedReceiver;
    private PreferenceCategory mPreferenceCategory;
    private Map<Integer, Preference> mPreferences;
    private SubscriptionManager mSubscriptionManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "provider_model_sim_list";
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    public NetworkProviderSimListController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mDataSubscriptionChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.NetworkProviderSimListController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                    NetworkProviderSimListController.this.update();
                }
            }
        };
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mPreferences = new ArrayMap();
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        this.mContext.registerReceiver(this.mDataSubscriptionChangedReceiver, intentFilter);
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        BroadcastReceiver broadcastReceiver = this.mDataSubscriptionChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("provider_model_sim_category");
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceCategory == null) {
            return;
        }
        Map<Integer, Preference> map = this.mPreferences;
        this.mPreferences = new ArrayMap();
        for (final SubscriptionInfo subscriptionInfo : getAvailablePhysicalSubscription()) {
            final int subscriptionId = subscriptionInfo.getSubscriptionId();
            Preference remove = map.remove(Integer.valueOf(subscriptionId));
            if (remove == null) {
                remove = new Preference(this.mPreferenceCategory.getContext());
                this.mPreferenceCategory.addPreference(remove);
            }
            CharSequence displayName = subscriptionInfo.getDisplayName();
            if (SubscriptionManager.getResourcesForSubId(this.mContext, subscriptionId).getBoolean(134414356)) {
                if (subscriptionInfo.areUiccApplicationsEnabled()) {
                    String str = "SIM" + String.valueOf(subscriptionInfo.getSimSlotIndex() + 1);
                    Settings.Global.putString(this.mContext.getContentResolver(), "special_sim_card_name", str);
                    remove.setTitle(str);
                } else {
                    remove.setTitle(Settings.Global.getString(this.mContext.getContentResolver(), "special_sim_card_name"));
                }
            } else {
                remove.setTitle(displayName);
            }
            remove.setSummary(getSummary(subscriptionId, displayName));
            remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.NetworkProviderSimListController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$update$0;
                    lambda$update$0 = NetworkProviderSimListController.this.lambda$update$0(subscriptionId, subscriptionInfo, preference);
                    return lambda$update$0;
                }
            });
            this.mPreferences.put(Integer.valueOf(subscriptionId), remove);
        }
        for (Preference preference : map.values()) {
            this.mPreferenceCategory.removePreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$0(int i, SubscriptionInfo subscriptionInfo, Preference preference) {
        if (!this.mSubscriptionManager.isActiveSubscriptionId(i) && !SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
            SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, i, true);
        } else {
            MobileNetworkUtils.launchMobileNetworkSettings(this.mContext, subscriptionInfo);
        }
        return true;
    }

    public CharSequence getSummary(int i, CharSequence charSequence) {
        if (!this.mSubscriptionManager.isActiveSubscriptionId(i)) {
            return SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager) ? this.mContext.getString(R$string.sim_category_inactive_sim) : this.mContext.getString(R$string.mobile_network_tap_to_activate, charSequence);
        }
        CharSequence defaultSimConfig = SubscriptionUtil.getDefaultSimConfig(this.mContext, i);
        String string = this.mContext.getResources().getString(R$string.sim_category_active_sim);
        if (defaultSimConfig == null) {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        sb.append((CharSequence) string);
        sb.append(defaultSimConfig);
        return sb;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !getAvailablePhysicalSubscription().isEmpty();
    }

    protected List<SubscriptionInfo> getAvailablePhysicalSubscription() {
        ArrayList arrayList = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : SubscriptionUtil.getAvailableSubscriptions(this.mContext)) {
            if (!subscriptionInfo.isEmbedded()) {
                arrayList.add(subscriptionInfo);
            }
        }
        return arrayList;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        update();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(this.mPreferenceCategory);
        update();
    }

    protected int getDefaultVoiceSubscriptionId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    protected int getDefaultSmsSubscriptionId() {
        return SubscriptionManager.getDefaultSmsSubscriptionId();
    }

    protected int getDefaultDataSubscriptionId() {
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }
}
