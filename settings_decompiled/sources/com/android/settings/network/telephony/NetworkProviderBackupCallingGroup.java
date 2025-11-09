package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.ArrayMap;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class NetworkProviderBackupCallingGroup extends TelephonyTogglePreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String KEY_PREFERENCE_BACKUPCALLING_GROUP = "provider_model_backup_call_group";
    private static final int PREF_START_ORDER = 10;
    private static final String TAG = "NetworkProviderBackupCallingGroup";
    private Map<Integer, SwitchPreference> mBackupCallingForSubPreferences;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private List<SubscriptionInfo> mSubInfoListForBackupCall;
    private SubscriptionsChangeListener mSubscriptionsChangeListener;
    private Map<Integer, TelephonyManager> mTelephonyManagerList;

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_PREFERENCE_BACKUPCALLING_GROUP;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return false;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return false;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NetworkProviderBackupCallingGroup(Context context, Lifecycle lifecycle, List<SubscriptionInfo> list, String str) {
        super(context, str);
        this.mTelephonyManagerList = new HashMap();
        this.mPreferenceGroupKey = str;
        this.mSubInfoListForBackupCall = list;
        this.mBackupCallingForSubPreferences = new ArrayMap();
        setSubscriptionInfoList(context);
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mSubscriptionsChangeListener == null) {
            this.mSubscriptionsChangeListener = new SubscriptionsChangeListener(this.mContext, this);
        }
        this.mSubscriptionsChangeListener.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionsChangeListener subscriptionsChangeListener = this.mSubscriptionsChangeListener;
        if (subscriptionsChangeListener != null) {
            subscriptionsChangeListener.stop();
        }
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        List<SubscriptionInfo> list = this.mSubInfoListForBackupCall;
        return (list == null || getSubscriptionInfoFromList(list, i) == null || this.mSubInfoListForBackupCall.size() <= 1) ? 2 : 0;
    }

    private boolean setCrossSimCallingEnabled(int i, boolean z) {
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(i);
        if (imsMmTelManager == null) {
            Log.d(TAG, "setCrossSimCallingEnabled(), ImsMmTelManager is null");
            return false;
        }
        try {
            imsMmTelManager.setCrossSimCallingEnabled(z);
            return true;
        } catch (ImsException e) {
            Log.w(TAG, "fail to get cross SIM calling configuration", e);
            return false;
        }
    }

    private boolean isCrossSimCallingEnabled(int i) {
        ImsMmTelManager imsMmTelManager = getImsMmTelManager(i);
        if (imsMmTelManager == null) {
            Log.d(TAG, "isCrossSimCallingEnabled(), ImsMmTelManager is null");
            return false;
        }
        try {
            return imsMmTelManager.isCrossSimCallingEnabled();
        } catch (ImsException e) {
            Log.w(TAG, "fail to get cross SIM calling configuration", e);
            return false;
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        update();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null) {
            return;
        }
        update();
    }

    private void update() {
        if (this.mPreferenceGroup == null) {
            return;
        }
        setSubscriptionInfoList(this.mContext);
        List<SubscriptionInfo> list = this.mSubInfoListForBackupCall;
        if (list == null || list.size() < 2) {
            for (SwitchPreference switchPreference : this.mBackupCallingForSubPreferences.values()) {
                this.mPreferenceGroup.removePreference(switchPreference);
            }
            this.mBackupCallingForSubPreferences.clear();
            return;
        }
        Map<Integer, SwitchPreference> map = this.mBackupCallingForSubPreferences;
        this.mBackupCallingForSubPreferences = new ArrayMap();
        setSubscriptionInfoForPreference(map);
    }

    private void setSubscriptionInfoForPreference(Map<Integer, SwitchPreference> map) {
        int i = 10;
        for (SubscriptionInfo subscriptionInfo : this.mSubInfoListForBackupCall) {
            final int subscriptionId = subscriptionInfo.getSubscriptionId();
            SwitchPreference remove = map.remove(Integer.valueOf(subscriptionId));
            if (remove == null) {
                remove = new SwitchPreference(this.mPreferenceGroup.getContext());
                this.mPreferenceGroup.addPreference(remove);
            }
            CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext);
            remove.setTitle(uniqueSubscriptionDisplayName);
            int i2 = i + 1;
            remove.setOrder(i);
            remove.setSummary(getSummary(uniqueSubscriptionDisplayName));
            final boolean isCrossSimCallingEnabled = isCrossSimCallingEnabled(subscriptionId);
            remove.setChecked(isCrossSimCallingEnabled);
            remove.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.telephony.NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$setSubscriptionInfoForPreference$0;
                    lambda$setSubscriptionInfoForPreference$0 = NetworkProviderBackupCallingGroup.this.lambda$setSubscriptionInfoForPreference$0(subscriptionId, isCrossSimCallingEnabled, preference);
                    return lambda$setSubscriptionInfoForPreference$0;
                }
            });
            this.mBackupCallingForSubPreferences.put(Integer.valueOf(subscriptionId), remove);
            i = i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setSubscriptionInfoForPreference$0(int i, boolean z, Preference preference) {
        setCrossSimCallingEnabled(i, !z);
        return true;
    }

    private String getSummary(CharSequence charSequence) {
        return String.format(getResourcesForSubId().getString(R$string.backup_calling_setting_summary), charSequence).toString();
    }

    private void setSubscriptionInfoList(final Context context) {
        List<SubscriptionInfo> list = this.mSubInfoListForBackupCall;
        if (list != null) {
            list.removeIf(new Predicate() { // from class: com.android.settings.network.telephony.NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$setSubscriptionInfoList$1;
                    lambda$setSubscriptionInfoList$1 = NetworkProviderBackupCallingGroup.this.lambda$setSubscriptionInfoList$1(context, (SubscriptionInfo) obj);
                    return lambda$setSubscriptionInfoList$1;
                }
            });
        } else {
            Log.d(TAG, "No active subscriptions");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setSubscriptionInfoList$1(Context context, SubscriptionInfo subscriptionInfo) {
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        setTelephonyManagerForSubscriptionId(context, subscriptionId);
        return !hasBackupCallingFeature(subscriptionId) && this.mSubInfoListForBackupCall.contains(subscriptionInfo);
    }

    private void setTelephonyManagerForSubscriptionId(Context context, int i) {
        this.mTelephonyManagerList.put(Integer.valueOf(i), ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i));
    }

    protected boolean hasBackupCallingFeature(int i) {
        return isCrossSimEnabledByPlatform(this.mContext, i);
    }

    protected boolean isCrossSimEnabledByPlatform(Context context, int i) {
        if (new WifiCallingQueryImsState(context, i).isWifiCallingSupported()) {
            PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
            return carrierConfigForSubId != null && carrierConfigForSubId.getBoolean("carrier_cross_sim_ims_available_bool", false);
        }
        Log.d(TAG, "WifiCalling is not supported by framework. subId = " + i);
        return false;
    }

    private ImsMmTelManager getImsMmTelManager(int i) {
        ImsManager imsManager;
        if (SubscriptionManager.isUsableSubscriptionId(i) && (imsManager = (ImsManager) this.mContext.getSystemService(ImsManager.class)) != null) {
            return imsManager.getImsMmTelManager(i);
        }
        return null;
    }

    private SubscriptionInfo getSubscriptionInfoFromList(List<SubscriptionInfo> list, int i) {
        for (SubscriptionInfo subscriptionInfo : list) {
            if (subscriptionInfo != null && subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        this.mSubInfoListForBackupCall = SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
        update();
    }
}
