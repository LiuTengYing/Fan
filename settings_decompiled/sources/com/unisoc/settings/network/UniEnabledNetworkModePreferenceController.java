package com.unisoc.settings.network;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.PrimarySubManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda0;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.unisoc.settings.network.UniEnabledNetworkModePreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class UniEnabledNetworkModePreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String ACTION_NETWORK_MODE_CHANGED = "com.sprd.dm.NETWORK_MODE_CHANGED";
    private static final String LOG_TAG = "EnabledNetworkMode";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private final BroadcastReceiver mBroadcastReceiver;
    private PreferenceEntriesBuilder mBuilder;
    private int mCallState;
    private CarrierConfigManager mCarrierConfigManager;
    private final PhoneStateListener mPhoneStateListener;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private SubscriptionsChangeListener mSubscriptionChangeLisener;
    private TelephonyManager mTelephonyManager;
    private Handler mUiHandler;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public UniEnabledNetworkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mCallState = 0;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "mBroadcastReceive action : " + action + ", updatePreference.");
                UniEnabledNetworkModePreferenceController.this.lambda$init$2();
            }
        };
        this.mSubscriptionChangeLisener = new SubscriptionsChangeListener(context, this);
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mPhoneStateListener = new PhoneStateListener() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController.1
            @Override // android.telephony.PhoneStateListener
            public void onCallStateChanged(int i, String str2) {
                UniEnabledNetworkModePreferenceController.this.mCallState = i;
                UniEnabledNetworkModePreferenceController.this.lambda$init$2();
                Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "onPreciseCallStateChanged state: " + i + " incomingNumber:" + str2);
            }
        };
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        if (this.mCarrierConfigManager == null) {
            this.mCarrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        return i != -1 && configForSubId != null && !configForSubId.getBoolean("hide_carrier_network_settings_bool") && !configForSubId.getBoolean("hide_preferred_network_type_bool") && !configForSubId.getBoolean("world_phone_bool") && isAllowedToShowNetworkTypeOption(i) ? 0 : 2;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null) {
            allowedNetworkTypesListener.register(this.mContext, this.mSubId);
        }
        SubscriptionsChangeListener subscriptionsChangeListener = this.mSubscriptionChangeLisener;
        if (subscriptionsChangeListener != null) {
            subscriptionsChangeListener.start();
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.ACTION_SET_RADIO_CAPABILITY_DONE");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, 2);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null) {
            allowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
        }
        SubscriptionsChangeListener subscriptionsChangeListener = this.mSubscriptionChangeLisener;
        if (subscriptionsChangeListener != null) {
            subscriptionsChangeListener.stop();
        }
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        this.mBuilder.setPreferenceEntries();
        this.mBuilder.setPreferenceValueAndSummary();
        listPreference.setEntries(this.mBuilder.getEntries());
        listPreference.setEntryValues(this.mBuilder.getEntryValues());
        listPreference.setValue(Integer.toString(this.mBuilder.getSelectedEntryValue()));
        listPreference.setSummary(this.mBuilder.getSummary());
        boolean isAirplaneModeOn = WirelessUtils.isAirplaneModeOn(this.mContext);
        boolean z = !isAirplaneModeOn && isCallStateIdle();
        Log.d(LOG_TAG, "updateState, isAirplaneModeOn:" + isAirplaneModeOn + "; isCallStateIdle:" + isCallStateIdle());
        preference.setEnabled(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean isActiveSubscriptionId = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(this.mSubId);
        boolean isAirplaneModeOn = WirelessUtils.isAirplaneModeOn(this.mContext);
        final int parseInt = Integer.parseInt((String) obj);
        if ("true".equals(SystemProperties.get("persist.vendor.radio.engtest.enable", "false"))) {
            Toast.makeText(this.mContext, R$string.network_mode_setting_prompt, 0).show();
        } else if (isActiveSubscriptionId && !isAirplaneModeOn && isCallStateIdle()) {
            final ListPreference listPreference = (ListPreference) preference;
            listPreference.setEnabled(false);
            ThreadUtils.postOnBackgroundThread(new Callable() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    Object lambda$onPreferenceChange$1;
                    lambda$onPreferenceChange$1 = UniEnabledNetworkModePreferenceController.this.lambda$onPreferenceChange$1(parseInt, listPreference);
                    return lambda$onPreferenceChange$1;
                }
            });
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Object lambda$onPreferenceChange$1(final int i, final ListPreference listPreference) throws Exception {
        Log.d(LOG_TAG, "set preferred network type in backgroud: " + i);
        final boolean preferredNetworkTypeBitmask = this.mTelephonyManager.setPreferredNetworkTypeBitmask(MobileNetworkUtils.getRafFromNetworkType(i));
        this.mUiHandler.post(new Runnable() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UniEnabledNetworkModePreferenceController.this.lambda$onPreferenceChange$0(listPreference, preferredNetworkTypeBitmask, i);
            }
        });
        return Boolean.TRUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceChange$0(ListPreference listPreference, boolean z, int i) {
        listPreference.setEnabled(true);
        if (z) {
            this.mBuilder.setPreferenceValueAndSummary(i);
            listPreference.setValue(Integer.toString(this.mBuilder.getSelectedEntryValue()));
            listPreference.setSummary(this.mBuilder.getSummary());
            Intent intent = new Intent(ACTION_NETWORK_MODE_CHANGED);
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, SubscriptionManager.getPhoneId(this.mSubId));
            intent.setPackage("com.sprd.dm.mbselfreg");
            intent.addFlags(16777216);
            this.mContext.sendBroadcast(intent);
        }
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        this.mCarrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.mBuilder = new PreferenceEntriesBuilder(this.mContext, this.mSubId);
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new AllowedNetworkTypesListener.OnAllowedNetworkTypesListener() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController$$ExternalSyntheticLambda2
                @Override // com.android.settings.network.AllowedNetworkTypesListener.OnAllowedNetworkTypesListener
                public final void onAllowedNetworkTypesChanged() {
                    UniEnabledNetworkModePreferenceController.this.lambda$init$2();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$init$2() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class PreferenceEntriesBuilder {
        private boolean is4gconfig;
        private boolean mAllowed5gNetworkType;
        private CarrierConfigManager mCarrierConfigManager;
        private Context mContext;
        private List<String> mEntries;
        private List<Integer> mEntriesValue;
        private boolean mIs5gEntryDisplayed;
        private boolean mIsGlobalCdma;
        private int mSelectedEntry;
        private boolean mShow4gForLTE;
        private int mSubId;
        private String mSummary;
        private boolean mSupported5gRadioAccessFamily;
        private TelephonyManager mTelephonyManager;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public enum EnabledNetworks {
            ENABLED_NETWORKS_UNKNOWN,
            ENABLED_NETWORKS_CDMA_CHOICES,
            ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES,
            ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES,
            ENABLED_NETWORKS_TDSCDMA_CHOICES,
            ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES,
            ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES,
            ENABLED_NETWORKS_EXCEPT_GSM_CHOICES,
            ENABLED_NETWORKS_EXCEPT_LTE_CHOICES,
            ENABLED_NETWORKS_4G_CHOICES,
            ENABLED_NETWORKS_CHOICES,
            PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE,
            UNISOC_ENABLED_NETWORKS_NR_CHOICES,
            UNISOC_ENABLED_NETWORKS_EXCEPT_LTE_CHOICES,
            UNISOC_ENABLED_NETWORKS_ONLY_LTE_PREFER_CHOICES,
            UNISOC_ENABLED_NETWORKS_LTE_CONTAIN_W_ONLY_CHOICES,
            UNISOC_ENABLED_NETWORKS_LTE_CONTAIN_LTE_ONLY_CHOICES,
            UNISOC_ENABLED_NETWORKS_LTE_EXCEPT_GSM_CHOICES,
            UNISOC_ENABLED_NETWORKS_LTE_EXCEPT_GSM_3GONLY_CHOICES,
            UNISOC_ENABLED_NETWORKS_CHOICES,
            UNISOC_ENABLED_NETWORKS_ONLY_WCDMA_PREFER_CHOICES,
            UNISOC_ENABLED_NETWORKS_SUBSIDYLOCK_CHOICES
        }

        private static int addNrToLteNetworkType(int i) {
            switch (i) {
                case 8:
                    return 25;
                case 9:
                    return 26;
                case 10:
                    return 27;
                case 11:
                    return 24;
                case 12:
                    return 28;
                case 13:
                case 14:
                case 16:
                case 18:
                case 21:
                default:
                    return i;
                case 15:
                    return 29;
                case 17:
                    return 30;
                case 19:
                    return 31;
                case 20:
                    return 32;
                case 22:
                    return 33;
            }
        }

        private boolean checkSupportedRadioBitmask(long j, long j2) {
            return (j2 & j) > 0;
        }

        private long getNetworkTypeLte() {
            return 266240L;
        }

        private long getNetworkTypeNr() {
            return 524288L;
        }

        private long getNetworkTypeWcdma() {
            return 17284L;
        }

        private int reduceNrToLteNetworkType(int i) {
            switch (i) {
                case 24:
                    return 11;
                case 25:
                    return 8;
                case 26:
                    return 9;
                case 27:
                    return 10;
                case 28:
                    return 12;
                case 29:
                    return 15;
                case 30:
                    return 17;
                case 31:
                    return 19;
                case 32:
                    return 20;
                case 33:
                    return 22;
                default:
                    return i;
            }
        }

        PreferenceEntriesBuilder(Context context, int i) {
            boolean z = true;
            this.is4gconfig = 9 == SystemProperties.getInt("ro.telephony.default_network", -1);
            this.mEntries = new ArrayList();
            this.mEntriesValue = new ArrayList();
            this.mContext = context;
            this.mSubId = i;
            this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
            this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
            this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
            this.mAllowed5gNetworkType = checkSupportedRadioBitmask(this.mTelephonyManager.getAllowedNetworkTypes(), 524288L);
            this.mSupported5gRadioAccessFamily = checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), 524288L);
            this.mIsGlobalCdma = this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled() && configForSubId.getBoolean("show_cdma_choices_bool");
            this.mShow4gForLTE = (configForSubId == null || !configForSubId.getBoolean("show_4g_for_lte_data_icon_bool")) ? false : z;
        }

        void setPreferenceEntries() {
            clearAllEntries();
            switch (AnonymousClass3.$SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[getEnabledNetworkType().ordinal()]) {
                case 1:
                    add5gEntry(addNrToLteNetworkType(8));
                    addLteEntry(8);
                    add3gEntry(4);
                    add1xEntry(5);
                    addGlobalEntry();
                    return;
                case 2:
                    add3gEntry(4);
                    add1xEntry(5);
                    return;
                case 3:
                    addLteEntry(8);
                    addGlobalEntry();
                    return;
                case 4:
                    add5gEntry(addNrToLteNetworkType(22));
                    addLteEntry(22);
                    add3gEntry(18);
                    add2gEntry(1);
                    return;
                case 5:
                    add3gEntry(0);
                    return;
                case 6:
                    add5gEntry(addNrToLteNetworkType(9));
                    add4gEntry(9);
                    add3gEntry(0);
                    return;
                case 7:
                    add5gEntry(addNrToLteNetworkType(9));
                    addLteEntry(9);
                    add3gEntry(0);
                    return;
                case 8:
                    add3gEntry(0);
                    add2gEntry(1);
                    return;
                case 9:
                    add5gEntry(addNrToLteNetworkType(9));
                    add4gEntry(9);
                    add3gEntry(0);
                    add2gEntry(1);
                    return;
                case 10:
                    add5gEntry(addNrToLteNetworkType(9));
                    addLteEntry(9);
                    add3gEntry(0);
                    add2gEntry(1);
                    return;
                case 11:
                    addGlobalEntry();
                    addCustomEntry(this.mContext.getString(R$string.network_world_mode_cdma_lte), 8);
                    addCustomEntry(this.mContext.getString(R$string.network_world_mode_gsm_lte), 9);
                    return;
                case 12:
                    add5gAutoEntry(26);
                    add4gAutoEntry(9);
                    return;
                case 13:
                    add5gEntry(addNrToLteNetworkType(9));
                    add4gAutoEntry(9);
                    add3gAutoEntry(0);
                    add2gOnlyEntry(1);
                    return;
                case 14:
                    add4gAutoEntry(9);
                    add3gAutoEntry(0);
                    add3gOnlyEntry(2);
                    return;
                case 15:
                    add4gAutoEntry(9);
                    add3gAutoEntry(0);
                    return;
                case 16:
                    add4gAutoEntry(9);
                    return;
                case 17:
                    add3gAutoEntry(0);
                    add3gOnlyEntry(2);
                    add2gOnlyEntry(1);
                    return;
                case 18:
                    add4gAutoEntry(9);
                    add4gOnlyEntry(11);
                    add3gAutoEntry(0);
                    add3gOnlyEntry(2);
                    add2gOnlyEntry(1);
                    return;
                case 19:
                    add4gAutoEntry(9);
                    add3gAutoEntry(0);
                    add3gOnlyEntry(2);
                    add2gOnlyEntry(1);
                    return;
                case 20:
                    add3gAutoEntry(0);
                    return;
                case 21:
                    add4gAutoEntry(9);
                    add4gOnlyEntry(11);
                    return;
                default:
                    throw new IllegalArgumentException("Not supported enabled network types.");
            }
        }

        private int getPreferredNetworkMode() {
            int networkTypeFromRaf = MobileNetworkUtils.getNetworkTypeFromRaf((int) this.mTelephonyManager.getAllowedNetworkTypesForReason(0));
            if (!showNrList()) {
                Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "Network mode :" + networkTypeFromRaf + " reduce NR");
                networkTypeFromRaf = reduceNrToLteNetworkType(networkTypeFromRaf);
            }
            Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "getPreferredNetworkMode: " + networkTypeFromRaf + ", for subid:" + this.mSubId);
            return networkTypeFromRaf;
        }

        private EnabledNetworks getEnabledNetworkType() {
            EnabledNetworks enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_UNKNOWN;
            int phoneType = this.mTelephonyManager.getPhoneType();
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
            if (phoneType == 2) {
                ContentResolver contentResolver = this.mContext.getContentResolver();
                int i = Settings.Global.getInt(contentResolver, "lte_service_forced" + this.mSubId, 0);
                int preferredNetworkMode = getPreferredNetworkMode();
                if (this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled()) {
                    if (i == 0) {
                        enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES;
                    } else {
                        switch (preferredNetworkMode) {
                            case 4:
                            case 5:
                            case 6:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES;
                                break;
                            case 7:
                            case 8:
                            case 10:
                            case 11:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES;
                                break;
                            case 9:
                            default:
                                enabledNetworks = EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES;
                                break;
                        }
                    }
                }
            } else if (phoneType == 1) {
                enabledNetworks = MobileNetworkUtils.isTdscdmaSupported(this.mContext, this.mSubId) ? EnabledNetworks.ENABLED_NETWORKS_TDSCDMA_CHOICES : (configForSubId == null || configForSubId.getBoolean("prefer_2g_bool") || configForSubId.getBoolean("lte_enabled_bool")) ? (configForSubId == null || configForSubId.getBoolean("prefer_2g_bool")) ? (configForSubId == null || configForSubId.getBoolean("lte_enabled_bool")) ? this.mIsGlobalCdma ? EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES : this.mShow4gForLTE ? EnabledNetworks.ENABLED_NETWORKS_4G_CHOICES : EnabledNetworks.ENABLED_NETWORKS_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_LTE_CHOICES : this.mShow4gForLTE ? EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_CHOICES : EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES;
            }
            if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                enabledNetworks = EnabledNetworks.PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE;
            }
            if (checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), getNetworkTypeNr())) {
                enabledNetworks = EnabledNetworks.UNISOC_ENABLED_NETWORKS_NR_CHOICES;
            } else if (checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), getNetworkTypeLte())) {
                Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
                EnabledNetworks enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_CHOICES;
                if (resourcesForSubId.getBoolean(R$bool.config_carrier_contains_3g_only_network)) {
                    enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_CONTAIN_W_ONLY_CHOICES;
                } else if (resourcesForSubId.getBoolean(R$bool.config_carrier_contains_lte_only_network)) {
                    enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_CONTAIN_LTE_ONLY_CHOICES;
                } else if (resourcesForSubId.getBoolean(R$bool.config_carrier_only_lte_auto_network)) {
                    enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_ONLY_LTE_PREFER_CHOICES;
                } else if (resourcesForSubId.getBoolean(R$bool.config_carrier_contains_lte_except_gsm_network)) {
                    enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_EXCEPT_GSM_CHOICES;
                } else if (resourcesForSubId.getBoolean(R$bool.config_carrier_contains_lte_except_gsm_3gonly_network)) {
                    enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_EXCEPT_GSM_3GONLY_CHOICES;
                } else if (SubscriptionManager.isValidPhoneId(SubscriptionManager.getPhoneId(this.mSubId)) && !PrimarySubManager.from(this.mContext).isDisableSimAllowedForSubsidy(SubscriptionManager.getPhoneId(this.mSubId))) {
                    enabledNetworks2 = EnabledNetworks.UNISOC_ENABLED_NETWORKS_SUBSIDYLOCK_CHOICES;
                }
                enabledNetworks = isEEUKsim(this.mSubId) ? EnabledNetworks.UNISOC_ENABLED_NETWORKS_ONLY_LTE_PREFER_CHOICES : enabledNetworks2;
            } else if (checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), getNetworkTypeWcdma())) {
                enabledNetworks = EnabledNetworks.UNISOC_ENABLED_NETWORKS_EXCEPT_LTE_CHOICES;
                if (isEEUKsim(this.mSubId)) {
                    enabledNetworks = EnabledNetworks.UNISOC_ENABLED_NETWORKS_ONLY_WCDMA_PREFER_CHOICES;
                }
            }
            Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "enabledNetworkType: " + enabledNetworks + ", for subid:" + this.mSubId);
            return enabledNetworks;
        }

        private boolean isEEUKsim(int i) {
            String simOperatorNumeric = this.mTelephonyManager.getSimOperatorNumeric(i);
            if (TextUtils.isEmpty(simOperatorNumeric) || !"23430".equals(simOperatorNumeric)) {
                return false;
            }
            String groupIdLevel1 = this.mTelephonyManager.getGroupIdLevel1(i);
            Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "gid1 is " + groupIdLevel1);
            return TextUtils.isEmpty(groupIdLevel1) || groupIdLevel1.length() < 4 || !"2800".equals(groupIdLevel1.substring(0, 4));
        }

        void setPreferenceValueAndSummary(int i) {
            setSelectedEntry(i);
            switch (i) {
                case 0:
                case 3:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(0);
                        setSummary(R$string.network_3G_auto);
                        return;
                    }
                    setSelectedEntry(10);
                    setSummary(R$string.network_global);
                    return;
                case 1:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(1);
                        setSummary(R$string.network_2G_only);
                        return;
                    }
                    setSelectedEntry(10);
                    setSummary(R$string.network_global);
                    return;
                case 2:
                    setSelectedEntry(2);
                    setSummary(R$string.network_3G_only);
                    return;
                case 4:
                case 6:
                case 7:
                    setSelectedEntry(4);
                    setSummary(R$string.network_3G);
                    return;
                case 5:
                    setSelectedEntry(5);
                    setSummary(R$string.network_1x);
                    return;
                case 8:
                    if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R$string.preferred_network_mode_lte_cdma_summary);
                        return;
                    }
                    setSelectedEntry(8);
                    setSummary(is5gEntryDisplayed() ? R$string.network_lte_pure : R$string.network_lte);
                    return;
                case 9:
                    if (MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R$string.preferred_network_mode_lte_gsm_umts_summary);
                    } else {
                        setSummary(R$string.network_4g_auto);
                    }
                    setSelectedEntry(9);
                    return;
                case 10:
                case 15:
                case 17:
                case 19:
                case 20:
                case 22:
                    if (MobileNetworkUtils.isTdscdmaSupported(this.mContext, this.mSubId)) {
                        setSelectedEntry(22);
                        setSummary(is5gEntryDisplayed() ? R$string.network_lte_pure : R$string.network_lte);
                        return;
                    }
                    setSelectedEntry(10);
                    if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R$string.network_global);
                        return;
                    } else if (is5gEntryDisplayed()) {
                        setSummary(this.mShow4gForLTE ? R$string.network_4G_pure : R$string.network_lte_pure);
                        return;
                    } else {
                        setSummary(this.mShow4gForLTE ? R$string.network_4G : R$string.network_lte);
                        return;
                    }
                case 11:
                    setSelectedEntry(11);
                    setSummary(R$string.network_4G_only);
                    return;
                case 12:
                    if (!this.mIsGlobalCdma) {
                        setSelectedEntry(9);
                        if (is5gEntryDisplayed()) {
                            setSummary(this.mShow4gForLTE ? R$string.network_4G_pure : R$string.network_lte_pure);
                            return;
                        } else {
                            setSummary(this.mShow4gForLTE ? R$string.network_4G : R$string.network_lte);
                            return;
                        }
                    }
                    setSelectedEntry(10);
                    setSummary(R$string.network_global);
                    return;
                case 13:
                    setSelectedEntry(13);
                    setSummary(R$string.network_3G);
                    return;
                case 14:
                case 16:
                case 18:
                    setSelectedEntry(18);
                    setSummary(R$string.network_3G);
                    return;
                case 21:
                    setSelectedEntry(21);
                    setSummary(R$string.network_3G);
                    return;
                case 23:
                    setSelectedEntry(23);
                    setSummary(R$string.network_5G_only);
                    return;
                case 24:
                case 26:
                case 28:
                    setSelectedEntry(26);
                    setSummary(this.mContext.getString(R$string.network_5G_auto));
                    return;
                case 25:
                    setSelectedEntry(25);
                    setSummary(this.mContext.getString(R$string.network_5G_auto) + this.mContext.getString(R$string.network_recommended));
                    return;
                case 27:
                    setSelectedEntry(27);
                    if (this.mTelephonyManager.getPhoneType() == 2 || this.mIsGlobalCdma || MobileNetworkUtils.isWorldMode(this.mContext, this.mSubId)) {
                        setSummary(R$string.network_global);
                        return;
                    }
                    setSummary(this.mContext.getString(R$string.network_5G_auto) + this.mContext.getString(R$string.network_recommended));
                    return;
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                    setSelectedEntry(33);
                    setSummary(this.mContext.getString(R$string.network_5G_auto) + this.mContext.getString(R$string.network_recommended));
                    return;
                default:
                    setSummary(this.mContext.getString(R$string.mobile_network_mode_error, Integer.valueOf(i)));
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPreferenceValueAndSummary() {
            setPreferenceValueAndSummary(getPreferredNetworkMode());
        }

        private boolean showNrList() {
            Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "showNrList, update allowed5gNetworkType and radioAccessFamily. For subid:" + this.mSubId);
            this.mAllowed5gNetworkType = checkSupportedRadioBitmask(this.mTelephonyManager.getAllowedNetworkTypes(), 524288L);
            boolean checkSupportedRadioBitmask = checkSupportedRadioBitmask(this.mTelephonyManager.getSupportedRadioAccessFamily(), 524288L);
            this.mSupported5gRadioAccessFamily = checkSupportedRadioBitmask;
            return checkSupportedRadioBitmask && this.mAllowed5gNetworkType;
        }

        private void add5gEntry(int i) {
            boolean z = i >= 23;
            if (!this.is4gconfig && this.mSupported5gRadioAccessFamily && this.mAllowed5gNetworkType && z) {
                List<String> list = this.mEntries;
                list.add(this.mContext.getString(R$string.network_5G_auto) + this.mContext.getString(R$string.network_recommended));
                this.mEntriesValue.add(Integer.valueOf(i));
                this.mIs5gEntryDisplayed = true;
                return;
            }
            this.mIs5gEntryDisplayed = false;
            Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "Hide 5G option.  supported5GRadioAccessFamily: " + this.mSupported5gRadioAccessFamily + " allowed5GNetworkType: " + this.mAllowed5gNetworkType + " isNRValue: " + z);
        }

        private void addGlobalEntry() {
            Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "addGlobalEntry.  supported5GRadioAccessFamily: " + this.mSupported5gRadioAccessFamily + " allowed5GNetworkType: " + this.mAllowed5gNetworkType);
            this.mEntries.add(this.mContext.getString(R$string.network_global));
            if (this.mSupported5gRadioAccessFamily & this.mAllowed5gNetworkType) {
                this.mEntriesValue.add(27);
            } else {
                this.mEntriesValue.add(10);
            }
        }

        private void addLteEntry(int i) {
            if (this.mSupported5gRadioAccessFamily) {
                this.mEntries.add(this.mContext.getString(R$string.network_lte_pure));
            } else {
                this.mEntries.add(this.mContext.getString(R$string.network_lte));
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add4gEntry(int i) {
            if (this.mSupported5gRadioAccessFamily) {
                this.mEntries.add(this.mContext.getString(R$string.network_4G_pure));
            } else {
                this.mEntries.add(this.mContext.getString(R$string.network_4G));
            }
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add3gEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_3G));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add2gEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_2G));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add1xEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_1x));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void addCustomEntry(String str, int i) {
            this.mEntries.add(str);
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String[] getEntries() {
            return (String[]) this.mEntries.toArray(new String[0]);
        }

        private void clearAllEntries() {
            this.mEntries.clear();
            this.mEntriesValue.clear();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String[] getEntryValues() {
            return (String[]) Arrays.stream((Integer[]) this.mEntriesValue.toArray(new Integer[0])).map(new EnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda0()).toArray(new IntFunction() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    String[] lambda$getEntryValues$0;
                    lambda$getEntryValues$0 = UniEnabledNetworkModePreferenceController.PreferenceEntriesBuilder.lambda$getEntryValues$0(i);
                    return lambda$getEntryValues$0;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ String[] lambda$getEntryValues$0(int i) {
            return new String[i];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getSelectedEntryValue() {
            return this.mSelectedEntry;
        }

        private void setSelectedEntry(final int i) {
            if (this.mEntriesValue.stream().anyMatch(new Predicate() { // from class: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$setSelectedEntry$1;
                    lambda$setSelectedEntry$1 = UniEnabledNetworkModePreferenceController.PreferenceEntriesBuilder.lambda$setSelectedEntry$1(i, (Integer) obj);
                    return lambda$setSelectedEntry$1;
                }
            })) {
                this.mSelectedEntry = i;
            } else if (this.mEntriesValue.size() > 0) {
                this.mSelectedEntry = this.mEntriesValue.get(0).intValue();
            } else {
                Log.e(UniEnabledNetworkModePreferenceController.LOG_TAG, "entriesValue is empty");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$setSelectedEntry$1(int i, Integer num) {
            return num.intValue() == i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getSummary() {
            return this.mSummary;
        }

        private void setSummary(int i) {
            setSummary(this.mContext.getString(i));
        }

        private void setSummary(String str) {
            this.mSummary = str;
        }

        private boolean is5gEntryDisplayed() {
            return this.mIs5gEntryDisplayed;
        }

        private void add5gAutoEntry(int i) {
            if (this.is4gconfig) {
                Log.d(UniEnabledNetworkModePreferenceController.LOG_TAG, "Hide 5G option");
                return;
            }
            this.mEntries.add(this.mContext.getString(R$string.network_5G_auto));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add4gAutoEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_4g_auto));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add4gOnlyEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_4G_only));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add3gAutoEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_3G_auto));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add3gOnlyEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_3G_only));
            this.mEntriesValue.add(Integer.valueOf(i));
        }

        private void add2gOnlyEntry(int i) {
            this.mEntries.add(this.mContext.getString(R$string.network_2G_only));
            this.mEntriesValue.add(Integer.valueOf(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.unisoc.settings.network.UniEnabledNetworkModePreferenceController$3  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks;

        static {
            int[] iArr = new int[PreferenceEntriesBuilder.EnabledNetworks.values().length];
            $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks = iArr;
            try {
                iArr[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_CDMA_CHOICES.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_CDMA_NO_LTE_CHOICES.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_CDMA_ONLY_LTE_CHOICES.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_TDSCDMA_CHOICES.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_LTE_CHOICES.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_4G_CHOICES.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_EXCEPT_GSM_CHOICES.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_EXCEPT_LTE_CHOICES.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_4G_CHOICES.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.ENABLED_NETWORKS_CHOICES.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.PREFERRED_NETWORK_MODE_CHOICES_WORLD_MODE.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_NR_CHOICES.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_CHOICES.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_EXCEPT_GSM_CHOICES.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_EXCEPT_GSM_3GONLY_CHOICES.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_ONLY_LTE_PREFER_CHOICES.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_EXCEPT_LTE_CHOICES.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_CONTAIN_LTE_ONLY_CHOICES.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_LTE_CONTAIN_W_ONLY_CHOICES.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_ONLY_WCDMA_PREFER_CHOICES.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$unisoc$settings$network$UniEnabledNetworkModePreferenceController$PreferenceEntriesBuilder$EnabledNetworks[PreferenceEntriesBuilder.EnabledNetworks.UNISOC_ENABLED_NETWORKS_SUBSIDYLOCK_CHOICES.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
        }
    }

    private boolean removeRestrictNetworkType(int i) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("restrict_network_type");
        sb.append(i);
        return 1 == Settings.Global.getInt(contentResolver, sb.toString(), 0);
    }

    private boolean isNetworkOptionNotAllowedForNonUSIMCard() {
        Context context = this.mContext;
        if (context != null) {
            return context.getResources().getBoolean(R$bool.config_network_option_not_allowed_for_non_USIM_card);
        }
        return false;
    }

    private boolean isNetworkOptionAllowedForNonPrimaryCard() {
        Context context = this.mContext;
        if (context != null) {
            return context.getResources().getBoolean(R$bool.config_network_option_not_allowed_for_non_primary_card);
        }
        return true;
    }

    private boolean isAllowedToShowNetworkTypeOption(int i) {
        TelephonyManager createForSubscriptionId = TelephonyManager.from(this.mContext).createForSubscriptionId(i);
        int phoneId = SubscriptionManager.getPhoneId(i);
        if (!SubscriptionManager.isValidPhoneId(phoneId)) {
            Log.d(LOG_TAG, "phone id is not valid.");
            return false;
        } else if (isNetworkOptionNotAllowedForNonUSIMCard() && !createForSubscriptionId.isApplicationOnUicc(2)) {
            Log.d(LOG_TAG, "Not allowed to show network type option for non-USIM card.");
            return false;
        } else if (removeRestrictNetworkType(phoneId)) {
            Log.d(LOG_TAG, "remove restrict network type");
            return false;
        } else {
            PrimarySubManager from = PrimarySubManager.from(this.mContext);
            if (!SubscriptionManager.isValidPhoneId(phoneId) || from == null || !from.restrictedNetworkTypeNeeded(phoneId)) {
                return isMaxRafSubId(i) || !isNetworkOptionAllowedForNonPrimaryCard();
            }
            Log.d(LOG_TAG, "simlock restrict networktype");
            return false;
        }
    }

    private boolean isMaxRafSubId(int i) {
        int[] activeSubscriptionIdList = ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).getActiveSubscriptionIdList();
        int length = activeSubscriptionIdList.length;
        long[] jArr = new long[length];
        long j = 0;
        for (int i2 = 0; i2 < activeSubscriptionIdList.length; i2++) {
            long supportedRadioAccessFamily = TelephonyManager.from(this.mContext).createForSubscriptionId(activeSubscriptionIdList[i2]).getSupportedRadioAccessFamily();
            jArr[i2] = supportedRadioAccessFamily;
            if (j < supportedRadioAccessFamily) {
                j = supportedRadioAccessFamily;
            }
        }
        Log.d(LOG_TAG, "[isMaxRafSubId] maxRaf =" + j);
        for (int i3 = 0; i3 < length; i3++) {
            if (i == activeSubscriptionIdList[i3]) {
                Log.d(LOG_TAG, "[isMaxRafSubId] raf =" + jArr[i3] + ", for subid =" + i);
                return j == jArr[i3];
            }
        }
        return false;
    }

    protected boolean isCallStateIdle() {
        TelecomManager telecomManager;
        Context context = this.mContext;
        if (context != null && (telecomManager = (TelecomManager) context.getSystemService(TelecomManager.class)) != null) {
            this.mCallState = telecomManager.getCallState();
        }
        return this.mCallState == 0;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
        Log.d(LOG_TAG, "onPhoneStateChanged");
        updateState(this.mPreference);
    }
}
