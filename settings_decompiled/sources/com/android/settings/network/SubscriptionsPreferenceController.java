package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.ArraySet;
import androidx.collection.ArrayMap;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.DataConnectivityListener;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.SignalStrengthListener;
import com.android.settings.network.telephony.TelephonyDisplayInfoListener;
import com.android.settings.widget.GearPreference;
import com.android.settings.widget.MutableGearPreference;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.SignalIcon$MobileIconGroup;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.mobile.MobileMappings;
import com.android.settingslib.mobile.TelephonyIcons;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.settingslib.utils.ThreadUtils;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class SubscriptionsPreferenceController extends AbstractPreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient, MobileDataEnabledListener.Client, DataConnectivityListener.Client, SignalStrengthListener.Callback, TelephonyDisplayInfoListener.Callback {
    private MobileMappings.Config mConfig;
    final BroadcastReceiver mConnectionChangeReceiver;
    private DataConnectivityListener mConnectivityListener;
    private MobileDataEnabledListener mDataEnabledListener;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private SignalStrengthListener mSignalStrengthListener;
    private int mStartOrder;
    private MutableGearPreference mSubsGearPref;
    private SubsPrefCtrlInjector mSubsPrefCtrlInjector;
    private SubscriptionManager mSubscriptionManager;
    private Map<Integer, Preference> mSubscriptionPreferences;
    private SubscriptionsChangeListener mSubscriptionsListener;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyDisplayInfoListener mTelephonyDisplayInfoListener;
    private TelephonyManager mTelephonyManager;
    private final Handler mUiHandler;
    private UpdateListener mUpdateListener;
    private final WifiManager mWifiManager;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    /* loaded from: classes.dex */
    public interface UpdateListener {
        void onChildrenUpdated();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    public SubscriptionsPreferenceController(Context context, Lifecycle lifecycle, UpdateListener updateListener, String str, int i) {
        super(context);
        this.mConnectionChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.SubscriptionsPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                    SubscriptionsPreferenceController subscriptionsPreferenceController = SubscriptionsPreferenceController.this;
                    subscriptionsPreferenceController.mConfig = subscriptionsPreferenceController.mSubsPrefCtrlInjector.getConfig(((AbstractPreferenceController) SubscriptionsPreferenceController.this).mContext);
                    SubscriptionsPreferenceController.this.update();
                } else if (action.equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                    SubscriptionsPreferenceController.this.update();
                }
            }
        };
        this.mConfig = null;
        this.mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mUpdateListener = updateListener;
        this.mPreferenceGroupKey = str;
        this.mStartOrder = i;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mSubscriptionPreferences = new ArrayMap();
        this.mSubscriptionsListener = new SubscriptionsChangeListener(context, this);
        this.mDataEnabledListener = new MobileDataEnabledListener(context, this);
        this.mConnectivityListener = new DataConnectivityListener(context, this);
        this.mSignalStrengthListener = new SignalStrengthListener(context, this);
        this.mTelephonyDisplayInfoListener = new TelephonyDisplayInfoListener(context, this);
        lifecycle.addObserver(this);
        SubsPrefCtrlInjector createSubsPrefCtrlInjector = createSubsPrefCtrlInjector();
        this.mSubsPrefCtrlInjector = createSubsPrefCtrlInjector;
        this.mConfig = createSubsPrefCtrlInjector.getConfig(this.mContext);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        this.mContext.registerReceiver(this.mConnectionChangeReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        BroadcastReceiver broadcastReceiver = this.mConnectionChangeReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mSubscriptionsListener.start();
        this.mDataEnabledListener.start(this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId());
        this.mConnectivityListener.start();
        this.mSignalStrengthListener.resume();
        this.mTelephonyDisplayInfoListener.resume();
        registerReceiver();
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mSubscriptionsListener.stop();
        this.mDataEnabledListener.stop();
        this.mConnectivityListener.stop();
        this.mSignalStrengthListener.pause();
        this.mTelephonyDisplayInfoListener.pause();
        unRegisterReceiver();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mPreferenceGroup == null) {
            return;
        }
        if (!isAvailable()) {
            MutableGearPreference mutableGearPreference = this.mSubsGearPref;
            if (mutableGearPreference != null) {
                this.mPreferenceGroup.removePreference(mutableGearPreference);
            }
            for (Preference preference : this.mSubscriptionPreferences.values()) {
                this.mPreferenceGroup.removePreference(preference);
            }
            this.mSubscriptionPreferences.clear();
            this.mSignalStrengthListener.updateSubscriptionIds(Collections.emptySet());
            this.mTelephonyDisplayInfoListener.updateSubscriptionIds(Collections.emptySet());
            this.mUpdateListener.onChildrenUpdated();
        } else if (UniMobileNetworkUtils.isSubsidyShowing()) {
            this.mPreferenceGroup.removeAll();
        } else {
            final SubscriptionInfo defaultDataSubscriptionInfo = this.mSubscriptionManager.getDefaultDataSubscriptionInfo();
            if (defaultDataSubscriptionInfo == null) {
                this.mPreferenceGroup.removeAll();
                return;
            }
            if (this.mSubsGearPref == null) {
                this.mPreferenceGroup.removeAll();
                MutableGearPreference mutableGearPreference2 = new MutableGearPreference(this.mContext, null);
                this.mSubsGearPref = mutableGearPreference2;
                mutableGearPreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda1
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference2) {
                        boolean lambda$update$0;
                        lambda$update$0 = SubscriptionsPreferenceController.this.lambda$update$0(preference2);
                        return lambda$update$0;
                    }
                });
            }
            this.mSubsGearPref.setOnGearClickListener(new GearPreference.OnGearClickListener() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda2
                @Override // com.android.settings.widget.GearPreference.OnGearClickListener
                public final void onGearClick(GearPreference gearPreference) {
                    SubscriptionsPreferenceController.this.lambda$update$1(defaultDataSubscriptionInfo, gearPreference);
                }
            });
            if (!((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser()) {
                this.mSubsGearPref.setGearEnabled(false);
            }
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionsPreferenceController.this.lambda$update$3(defaultDataSubscriptionInfo);
                }
            });
            this.mSubsGearPref.setOrder(this.mStartOrder);
            this.mSubsGearPref.setSummary(getMobilePreferenceSummary(defaultDataSubscriptionInfo.getSubscriptionId()));
            this.mSubsGearPref.setIcon(getIcon(defaultDataSubscriptionInfo.getSubscriptionId()));
            this.mPreferenceGroup.addPreference(this.mSubsGearPref);
            ArraySet arraySet = new ArraySet();
            arraySet.add(Integer.valueOf(defaultDataSubscriptionInfo.getSubscriptionId()));
            this.mSignalStrengthListener.updateSubscriptionIds(arraySet);
            this.mTelephonyDisplayInfoListener.updateSubscriptionIds(arraySet);
            this.mUpdateListener.onChildrenUpdated();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$0(Preference preference) {
        connectCarrierNetwork();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(SubscriptionInfo subscriptionInfo, GearPreference gearPreference) {
        MobileNetworkUtils.launchMobileNetworkSettings(this.mContext, subscriptionInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$3(SubscriptionInfo subscriptionInfo) {
        final CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, this.mContext);
        this.mUiHandler.post(new Runnable() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionsPreferenceController.this.lambda$update$2(uniqueSubscriptionDisplayName);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$2(CharSequence charSequence) {
        this.mSubsGearPref.setTitle(charSequence);
    }

    private CharSequence getMobilePreferenceSummary(int i) {
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        if (!createForSubscriptionId.isDataEnabled()) {
            return this.mContext.getString(R$string.mobile_data_off_summary);
        }
        ServiceState serviceState = createForSubscriptionId.getServiceState();
        NetworkRegistrationInfo networkRegistrationInfo = serviceState == null ? null : serviceState.getNetworkRegistrationInfo(2, 1);
        boolean isRegistered = networkRegistrationInfo == null ? false : networkRegistrationInfo.isRegistered();
        boolean isCarrierNetworkActive = isCarrierNetworkActive();
        String networkType = this.mSubsPrefCtrlInjector.getNetworkType(this.mContext, this.mConfig, this.mTelephonyDisplayInfo, i, isCarrierNetworkActive);
        if (this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext) || isCarrierNetworkActive) {
            if (networkType.isEmpty()) {
                networkType = this.mContext.getString(R$string.mobile_data_connection_active);
            } else {
                Context context = this.mContext;
                networkType = context.getString(R$string.preference_summary_default_combination, context.getString(R$string.mobile_data_connection_active), networkType);
            }
        } else if (!isRegistered) {
            networkType = this.mContext.getString(R$string.mobile_data_no_connection);
        }
        return Html.fromHtml(networkType, 0);
    }

    Drawable getIcon(int i) {
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        SignalStrength signalStrength = createForSubscriptionId.getSignalStrength();
        boolean z = false;
        int level = signalStrength == null ? 0 : signalStrength.getLevel();
        boolean isCarrierNetworkActive = isCarrierNetworkActive();
        int i2 = 5;
        if (isCarrierNetworkActive) {
            level = getCarrierNetworkLevel();
        } else if (shouldInflateSignalStrength(i)) {
            level++;
            i2 = 6;
        }
        Drawable drawable = this.mContext.getDrawable(R$drawable.ic_signal_strength_zero_bar_no_internet);
        ServiceState serviceState = createForSubscriptionId.getServiceState();
        NetworkRegistrationInfo networkRegistrationInfo = serviceState == null ? null : serviceState.getNetworkRegistrationInfo(2, 1);
        boolean isRegistered = networkRegistrationInfo == null ? false : networkRegistrationInfo.isRegistered();
        if (serviceState != null && serviceState.getState() == 0) {
            z = true;
        }
        if (isRegistered || z || isCarrierNetworkActive) {
            drawable = this.mSubsPrefCtrlInjector.getIcon(this.mContext, level, i2, !createForSubscriptionId.isDataEnabled());
        }
        if (this.mSubsPrefCtrlInjector.isActiveCellularNetwork(this.mContext) || isCarrierNetworkActive) {
            drawable.setTint(Utils.getColorAccentDefaultColor(this.mContext));
        }
        return drawable;
    }

    boolean shouldInflateSignalStrength(int i) {
        return SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, i);
    }

    void setIcon(Preference preference, int i, boolean z) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        SignalStrength signalStrength = createForSubscriptionId.getSignalStrength();
        boolean z2 = false;
        int level = signalStrength == null ? 0 : signalStrength.getLevel();
        int i2 = 5;
        if (shouldInflateSignalStrength(i)) {
            level++;
            i2 = 6;
        }
        preference.setIcon(this.mSubsPrefCtrlInjector.getIcon(this.mContext, level, i2, (z && createForSubscriptionId.isDataEnabled()) ? true : true));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<SubscriptionInfo> activeSubscriptions;
        return (!this.mSubscriptionsListener.isAirplaneModeOn() || (this.mWifiManager.isWifiEnabled() && isCarrierNetworkActive())) && (activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(this.mSubscriptionManager)) != null && activeSubscriptions.stream().filter(new Predicate() { // from class: com.android.settings.network.SubscriptionsPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isAvailable$4;
                lambda$isAvailable$4 = SubscriptionsPreferenceController.this.lambda$isAvailable$4((SubscriptionInfo) obj);
                return lambda$isAvailable$4;
            }
        }).count() >= 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isAvailable$4(SubscriptionInfo subscriptionInfo) {
        return this.mSubsPrefCtrlInjector.canSubscriptionBeDisplayed(this.mContext, subscriptionInfo.getSubscriptionId());
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
        update();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        int defaultDataSubscriptionId = this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId();
        if (defaultDataSubscriptionId != this.mDataEnabledListener.getSubId()) {
            this.mDataEnabledListener.stop();
            this.mDataEnabledListener.start(defaultDataSubscriptionId);
        }
        this.mSubsGearPref = null;
        update();
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        update();
    }

    @Override // com.android.settings.network.telephony.DataConnectivityListener.Client
    public void onDataConnectivityChange() {
        update();
    }

    @Override // com.android.settings.network.telephony.SignalStrengthListener.Callback
    public void onSignalStrengthChanged() {
        update();
    }

    @Override // com.android.settings.network.telephony.TelephonyDisplayInfoListener.Callback
    public void onTelephonyDisplayInfoChanged(int i, TelephonyDisplayInfo telephonyDisplayInfo) {
        if (i != this.mSubsPrefCtrlInjector.getDefaultDataSubscriptionId()) {
            return;
        }
        this.mTelephonyDisplayInfo = telephonyDisplayInfo;
        update();
    }

    public void setWifiPickerTrackerHelper(WifiPickerTrackerHelper wifiPickerTrackerHelper) {
        this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
    }

    public void connectCarrierNetwork() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper;
        if (MobileNetworkUtils.isMobileDataEnabled(this.mContext) && (wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper) != null) {
            wifiPickerTrackerHelper.connectCarrierNetwork(null);
        }
    }

    SubsPrefCtrlInjector createSubsPrefCtrlInjector() {
        return new SubsPrefCtrlInjector();
    }

    boolean isCarrierNetworkActive() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        return wifiPickerTrackerHelper != null && wifiPickerTrackerHelper.isCarrierNetworkActive();
    }

    private int getCarrierNetworkLevel() {
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        if (wifiPickerTrackerHelper == null) {
            return 0;
        }
        return wifiPickerTrackerHelper.getCarrierNetworkLevel();
    }

    /* loaded from: classes.dex */
    public static class SubsPrefCtrlInjector {
        public boolean canSubscriptionBeDisplayed(Context context, int i) {
            return SubscriptionUtil.getAvailableSubscription(context, ProxySubscriptionManager.getInstance(context), i) != null;
        }

        public int getDefaultDataSubscriptionId() {
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }

        public boolean isActiveCellularNetwork(Context context) {
            return MobileNetworkUtils.activeNetworkIsCellular(context);
        }

        public MobileMappings.Config getConfig(Context context) {
            return MobileMappings.Config.readConfig(context);
        }

        public String getNetworkType(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i) {
            SignalIcon$MobileIconGroup signalIcon$MobileIconGroup = MobileMappings.mapIconSets(config).get(MobileMappings.getIconKey(telephonyDisplayInfo));
            int i2 = signalIcon$MobileIconGroup != null ? signalIcon$MobileIconGroup.dataContentDescription : 0;
            return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
        }

        public String getNetworkType(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i, boolean z) {
            if (z) {
                int i2 = TelephonyIcons.CARRIER_MERGED_WIFI.dataContentDescription;
                return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
            }
            return getNetworkType(context, config, telephonyDisplayInfo, i);
        }

        public Drawable getIcon(Context context, int i, int i2, boolean z) {
            return MobileNetworkUtils.getSignalStrengthIcon(context, i, i2, 0, z);
        }
    }
}
