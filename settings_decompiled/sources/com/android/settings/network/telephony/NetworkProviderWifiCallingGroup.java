package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Settings;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class NetworkProviderWifiCallingGroup extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    protected CarrierConfigManager mCarrierConfigManager;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private Map<Integer, PhoneAccountHandle> mSimCallManagerList;
    private List<SubscriptionInfo> mSubInfoListForWfc;
    private SubscriptionManager mSubscriptionManager;
    private PhoneCallStateTelephonyCallback mTelephonyCallback;
    private Map<Integer, TelephonyManager> mTelephonyManagerList;
    private Map<Integer, Preference> mWifiCallingForSubPreferences;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "provider_model_wfc_group";
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    public NetworkProviderWifiCallingGroup(Context context, Lifecycle lifecycle, String str) {
        super(context);
        this.mTelephonyManagerList = new HashMap();
        this.mSimCallManagerList = new HashMap();
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mPreferenceGroupKey = str;
        this.mWifiCallingForSubPreferences = new ArrayMap();
        setSubscriptionInfoList(context);
        if (this.mTelephonyCallback == null) {
            this.mTelephonyCallback = new PhoneCallStateTelephonyCallback();
        }
        lifecycle.addObserver(this);
    }

    private void setSubscriptionInfoList(final Context context) {
        ArrayList arrayList = new ArrayList(SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager));
        this.mSubInfoListForWfc = arrayList;
        arrayList.removeIf(new Predicate() { // from class: com.android.settings.network.telephony.NetworkProviderWifiCallingGroup$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setSubscriptionInfoList$0;
                lambda$setSubscriptionInfoList$0 = NetworkProviderWifiCallingGroup.this.lambda$setSubscriptionInfoList$0(context, (SubscriptionInfo) obj);
                return lambda$setSubscriptionInfoList$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setSubscriptionInfoList$0(Context context, SubscriptionInfo subscriptionInfo) {
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        setTelephonyManagerForSubscriptionId(context, subscriptionId);
        setPhoneAccountHandleForSubscriptionId(context, subscriptionId);
        return !shouldShowWifiCallingForSub(subscriptionId) && this.mSubInfoListForWfc.contains(subscriptionInfo);
    }

    private void setTelephonyManagerForSubscriptionId(Context context, int i) {
        this.mTelephonyManagerList.put(Integer.valueOf(i), ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i));
    }

    private void setPhoneAccountHandleForSubscriptionId(Context context, int i) {
        this.mSimCallManagerList.put(Integer.valueOf(i), ((TelecomManager) context.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TelephonyManager getTelephonyManagerForSubscriptionId(int i) {
        return this.mTelephonyManagerList.get(Integer.valueOf(i));
    }

    protected PhoneAccountHandle getPhoneAccountHandleForSubscriptionId(int i) {
        return this.mSimCallManagerList.get(Integer.valueOf(i));
    }

    protected WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updateListener();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback != null) {
            phoneCallStateTelephonyCallback.unregister();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<SubscriptionInfo> list = this.mSubInfoListForWfc;
        if (list != null) {
            return list.size() >= 1;
        }
        Log.d("NetworkProviderWifiCallingGroup", "No active subscriptions, hide the controller");
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        setSubscriptionInfoList(this.mContext);
        update();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null) {
            return;
        }
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceGroup == null) {
            return;
        }
        if (!isAvailable()) {
            for (Preference preference : this.mWifiCallingForSubPreferences.values()) {
                this.mPreferenceGroup.removePreference(preference);
            }
            this.mWifiCallingForSubPreferences.clear();
            return;
        }
        Map<Integer, Preference> map = this.mWifiCallingForSubPreferences;
        this.mWifiCallingForSubPreferences = new ArrayMap();
        setSubscriptionInfoForPreference(map);
        for (Preference preference2 : map.values()) {
            this.mPreferenceGroup.removePreference(preference2);
        }
    }

    private void setSubscriptionInfoForPreference(Map<Integer, Preference> map) {
        Intent buildPhoneAccountConfigureIntent;
        int i = 10;
        for (final SubscriptionInfo subscriptionInfo : this.mSubInfoListForWfc) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            if (shouldShowWifiCallingForSub(subscriptionId)) {
                Preference remove = map.remove(Integer.valueOf(subscriptionId));
                if (remove == null) {
                    remove = new Preference(this.mPreferenceGroup.getContext());
                    this.mPreferenceGroup.addPreference(remove);
                }
                CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext);
                if (getPhoneAccountHandleForSubscriptionId(subscriptionId) != null && (buildPhoneAccountConfigureIntent = MobileNetworkUtils.buildPhoneAccountConfigureIntent(this.mContext, getPhoneAccountHandleForSubscriptionId(subscriptionId))) != null) {
                    PackageManager packageManager = this.mContext.getPackageManager();
                    uniqueSubscriptionDisplayName = packageManager.queryIntentActivities(buildPhoneAccountConfigureIntent, 0).get(0).loadLabel(packageManager);
                    remove.setIntent(buildPhoneAccountConfigureIntent);
                }
                remove.setTitle(uniqueSubscriptionDisplayName);
                remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.telephony.NetworkProviderWifiCallingGroup$$ExternalSyntheticLambda1
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$setSubscriptionInfoForPreference$1;
                        lambda$setSubscriptionInfoForPreference$1 = NetworkProviderWifiCallingGroup.this.lambda$setSubscriptionInfoForPreference$1(subscriptionInfo, preference);
                        return lambda$setSubscriptionInfoForPreference$1;
                    }
                });
                remove.setEnabled(getTelephonyManagerForSubscriptionId(subscriptionId).getCallState() == 0);
                int i2 = i + 1;
                remove.setOrder(i);
                remove.setSummary(queryImsState(subscriptionId).isEnabledByUser() ? R$string.calls_sms_wfc_summary : 17041769);
                this.mWifiCallingForSubPreferences.put(Integer.valueOf(subscriptionId), remove);
                i = i2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setSubscriptionInfoForPreference$1(SubscriptionInfo subscriptionInfo, Preference preference) {
        Intent intent = new Intent(this.mContext, Settings.WifiCallingSettingsActivity.class);
        intent.putExtra("android.provider.extra.SUB_ID", subscriptionInfo.getSubscriptionId());
        this.mContext.startActivity(intent);
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        setSubscriptionInfoList(this.mContext);
        updateListener();
        update();
    }

    private void updateListener() {
        for (SubscriptionInfo subscriptionInfo : this.mSubInfoListForWfc) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
            if (phoneCallStateTelephonyCallback != null) {
                phoneCallStateTelephonyCallback.register(this.mContext, subscriptionId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PhoneCallStateTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        private PhoneCallStateTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.CallStateListener
        public void onCallStateChanged(int i) {
            NetworkProviderWifiCallingGroup.this.update();
        }

        public void register(Context context, int i) {
            TelephonyManager telephonyManagerForSubscriptionId = NetworkProviderWifiCallingGroup.this.getTelephonyManagerForSubscriptionId(i);
            this.mTelephonyManager = telephonyManagerForSubscriptionId;
            telephonyManagerForSubscriptionId.registerTelephonyCallback(context.getMainExecutor(), this);
        }

        public void unregister() {
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager != null) {
                telephonyManager.unregisterTelephonyCallback(this);
            }
        }
    }

    protected boolean shouldShowWifiCallingForSub(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i) && MobileNetworkUtils.isWifiCallingEnabled(this.mContext, i, queryImsState(i), getPhoneAccountHandleForSubscriptionId(i)) && isWifiCallingAvailableForCarrier(i)) {
            return MobileNetworkUtils.isShowWfcByDefault(this.mContext, i) || MobileNetworkUtils.isOmaWfcEnable(this.mContext, i);
        }
        return false;
    }

    private boolean isWifiCallingAvailableForCarrier(int i) {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = this.mCarrierConfigManager;
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            return false;
        }
        return configForSubId.getBoolean("carrier_wfc_ims_available_bool");
    }
}
