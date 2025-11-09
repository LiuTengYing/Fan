package com.android.settings.network;

import android.content.Intent;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Map;
@Deprecated
/* loaded from: classes.dex */
public class MobileNetworkListController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    @VisibleForTesting
    static final String KEY_ADD_MORE = "add_more";
    private SubscriptionsChangeListener mChangeListener;
    private PreferenceScreen mPreferenceScreen;
    private Map<Integer, Preference> mPreferences;
    private SubscriptionManager mSubscriptionManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        preferenceScreen.findPreference(KEY_ADD_MORE).setVisible(MobileNetworkUtils.showEuiccSettings(this.mContext));
        update();
    }

    private void update() {
        if (this.mPreferenceScreen == null) {
            return;
        }
        Map<Integer, Preference> map = this.mPreferences;
        this.mPreferences = new ArrayMap();
        for (final SubscriptionInfo subscriptionInfo : SubscriptionUtil.getAvailableSubscriptions(this.mContext)) {
            final int subscriptionId = subscriptionInfo.getSubscriptionId();
            Preference remove = map.remove(Integer.valueOf(subscriptionId));
            if (remove == null) {
                remove = new Preference(this.mPreferenceScreen.getContext());
                this.mPreferenceScreen.addPreference(remove);
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
                remove.setTitle(subscriptionInfo.getDisplayName());
            }
            if (subscriptionInfo.isEmbedded()) {
                if (this.mSubscriptionManager.isActiveSubscriptionId(subscriptionId)) {
                    remove.setSummary(R$string.mobile_network_active_esim);
                } else {
                    remove.setSummary(R$string.mobile_network_inactive_esim);
                }
            } else if (this.mSubscriptionManager.isActiveSubscriptionId(subscriptionId)) {
                remove.setSummary(R$string.mobile_network_active_sim);
            } else if (SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
                remove.setSummary(this.mContext.getString(R$string.mobile_network_inactive_sim));
            } else {
                remove.setSummary(this.mContext.getString(R$string.mobile_network_tap_to_activate, displayName));
            }
            remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.MobileNetworkListController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$update$0;
                    lambda$update$0 = MobileNetworkListController.this.lambda$update$0(subscriptionInfo, subscriptionId, preference);
                    return lambda$update$0;
                }
            });
            this.mPreferences.put(Integer.valueOf(subscriptionId), remove);
        }
        for (Preference preference : map.values()) {
            this.mPreferenceScreen.removePreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$0(SubscriptionInfo subscriptionInfo, int i, Preference preference) {
        if (!subscriptionInfo.isEmbedded() && !this.mSubscriptionManager.isActiveSubscriptionId(i) && !SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) {
            SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, i, true);
        } else {
            Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
            intent.setPackage("com.android.settings");
            intent.putExtra("android.provider.extra.SUB_ID", subscriptionInfo.getSubscriptionId());
            this.mContext.startActivity(intent);
        }
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        update();
    }
}
