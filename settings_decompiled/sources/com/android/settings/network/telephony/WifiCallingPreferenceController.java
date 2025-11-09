package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.ims.internal.ImsManagerEx;
import com.android.settings.R$string;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.network.telephony.BroadcastReceiverChanged;
import com.android.settings.network.telephony.WifiCallingContentObserver;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class WifiCallingPreferenceController extends TelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop, BroadcastReceiverChanged.BroadcastReceiverChangedClient, WifiCallingContentObserver.WifiCallingContentObserverClient {
    private static final String TAG = "WifiCallingPreference";
    private BroadcastReceiverChanged mBroadcastReceiverChanged;
    Integer mCallState;
    CarrierConfigManager mCarrierConfigManager;
    private ImsMmTelManager mImsMmTelManager;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    PhoneAccountHandle mSimCallManager;
    private PhoneTelephonyCallback mTelephonyCallback;
    private WifiCallingContentObserver mWifiCallingContentObserver;

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

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiCallingPreferenceController(Context context, String str) {
        super(context, str);
        this.mWifiCallingContentObserver = null;
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mTelephonyCallback = new PhoneTelephonyCallback();
        this.mBroadcastReceiverChanged = new BroadcastReceiverChanged(context, this);
        this.mWifiCallingContentObserver = new WifiCallingContentObserver(context, this);
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (SubscriptionManager.isValidSubscriptionId(i) && isWifiCallingEnabled(this.mContext, i)) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mTelephonyCallback.register(this.mContext, this.mSubId);
        this.mWifiCallingContentObserver.startForSubid(this.mSubId);
        this.mBroadcastReceiverChanged.start();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mTelephonyCallback.unregister();
        this.mWifiCallingContentObserver.stop();
        this.mBroadcastReceiverChanged.stop();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        Intent intent = findPreference.getIntent();
        if (intent != null) {
            intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mCallState == null || preference == null) {
            Log.d(TAG, "Skip update under mCallState=" + this.mCallState);
            return;
        }
        preference.setVisible(isAvailable());
        CharSequence charSequence = null;
        PhoneAccountHandle phoneAccountHandle = this.mSimCallManager;
        if (phoneAccountHandle != null) {
            Intent buildPhoneAccountConfigureIntent = MobileNetworkUtils.buildPhoneAccountConfigureIntent(this.mContext, phoneAccountHandle);
            if (buildPhoneAccountConfigureIntent == null) {
                return;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            preference.setTitle(packageManager.queryIntentActivities(buildPhoneAccountConfigureIntent, 0).get(0).loadLabel(packageManager));
            preference.setIntent(buildPhoneAccountConfigureIntent);
        } else {
            preference.setTitle(SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId).getString(R$string.wifi_calling_settings_title));
            charSequence = getResourceIdForWfcMode(this.mSubId);
        }
        preference.setSummary(charSequence);
        preference.setEnabled(this.mCallState.intValue() == 0);
        synSettingForVoLTE();
    }

    private CharSequence getResourceIdForWfcMode(int i) {
        int i2;
        int i3;
        PersistableBundle configForSubId;
        if (queryImsState(i).isEnabledByUser()) {
            boolean z = false;
            CarrierConfigManager carrierConfigManager = this.mCarrierConfigManager;
            if (carrierConfigManager != null && (configForSubId = carrierConfigManager.getConfigForSubId(i)) != null) {
                z = configForSubId.getBoolean("use_wfc_home_network_mode_in_roaming_network_bool");
            }
            try {
                if (getTelephonyManager(this.mContext, i).isNetworkRoaming() && !z) {
                    i3 = this.mImsMmTelManager.getVoWiFiRoamingModeSetting();
                } else {
                    i3 = this.mImsMmTelManager.getVoWiFiModeSetting();
                }
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "fail to get WFC mode subId = " + this.mSubId, e);
                i3 = 2;
            }
            if (!MobileNetworkUtils.getBooleanCarrierConfig(this.mContext, "support_show_wifi_calling_preference", i)) {
                i2 = R$string.wifi_calling_on_summary;
            } else if (i3 == 0) {
                i2 = 17041738;
            } else if (i3 == 1) {
                i2 = 17041737;
            } else if (i3 == 2) {
                i2 = 17041739;
            } else if (i3 == 3) {
                i2 = 135004292;
            }
            return SubscriptionManager.getResourcesForSubId(this.mContext, i).getText(i2);
        }
        i2 = 17041769;
        return SubscriptionManager.getResourcesForSubId(this.mContext, i).getText(i2);
    }

    public WifiCallingPreferenceController init(int i) {
        this.mSubId = i;
        this.mImsMmTelManager = getImsMmTelManager(i);
        this.mSimCallManager = ((TelecomManager) this.mContext.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(this.mSubId);
        return this;
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onPhoneStateChanged() {
        updateState(this.mPreference);
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onCarrierConfigChanged(int i) {
        PreferenceScreen preferenceScreen;
        if (SubscriptionManager.isValidPhoneId(i) && SubscriptionManager.getPhoneId(this.mSubId) == i && SubscriptionManager.getSimStateForSlotIndex(i) == 10 && (preferenceScreen = this.mPreferenceScreen) != null) {
            displayPreference(preferenceScreen);
            updateState(this.mPreference);
        }
    }

    WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }

    protected ImsMmTelManager getImsMmTelManager(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return ImsMmTelManager.createForSubscriptionId(i);
        }
        return null;
    }

    TelephonyManager getTelephonyManager(Context context, int i) {
        TelephonyManager createForSubscriptionId;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        return (SubscriptionManager.isValidSubscriptionId(i) && (createForSubscriptionId = telephonyManager.createForSubscriptionId(i)) != null) ? createForSubscriptionId : telephonyManager;
    }

    /* loaded from: classes.dex */
    private class PhoneTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        private PhoneTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.CallStateListener
        public void onCallStateChanged(int i) {
            WifiCallingPreferenceController.this.mCallState = Integer.valueOf(i);
            WifiCallingPreferenceController wifiCallingPreferenceController = WifiCallingPreferenceController.this;
            wifiCallingPreferenceController.updateState(wifiCallingPreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            TelephonyManager telephonyManager = WifiCallingPreferenceController.this.getTelephonyManager(context, i);
            this.mTelephonyManager = telephonyManager;
            WifiCallingPreferenceController.this.mCallState = Integer.valueOf(telephonyManager.getCallState(i));
            this.mTelephonyManager.registerTelephonyCallback(context.getMainExecutor(), this);
        }

        public void unregister() {
            WifiCallingPreferenceController.this.mCallState = null;
            this.mTelephonyManager.unregisterTelephonyCallback(this);
        }
    }

    private boolean isWifiCallingEnabled(Context context, int i) {
        PhoneAccountHandle simCallManagerForSubscription = ((TelecomManager) context.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(i);
        SubscriptionManager.getSlotIndex(i);
        if (simCallManagerForSubscription != null) {
            if (MobileNetworkUtils.buildPhoneAccountConfigureIntent(context, simCallManagerForSubscription) != null) {
                return true;
            }
        } else if (queryImsState(i).isReadyToWifiCalling() && (MobileNetworkUtils.isShowWfcByDefault(context, i) || MobileNetworkUtils.isOmaWfcEnable(context, i))) {
            return true;
        }
        return false;
    }

    private void synSettingForVoLTE() {
        try {
            if (this.mCallState.intValue() == 0 && ImsManagerEx.synSettingForWFCandVoLTE(this.mContext, this.mSubId) && queryImsState(this.mSubId).isEnabledByUser() && this.mImsMmTelManager.isTtyOverVolteEnabled() && queryVotleState(this.mSubId).isReadyToVoLte() && !this.mImsMmTelManager.isAdvancedCallingSettingEnabled()) {
                Log.d(TAG, "Vowifi enable to sync open VOLTE");
                this.mImsMmTelManager.setAdvancedCallingSettingEnabled(true);
            }
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "fail to synSettingForVoLTE. subId=" + this.mSubId, e);
        }
    }

    protected VolteQueryImsState queryVotleState(int i) {
        return new VolteQueryImsState(this.mContext, i);
    }

    @Override // com.android.settings.network.telephony.WifiCallingContentObserver.WifiCallingContentObserverClient
    public void onWfcStateChanged() {
        updateState(this.mPreference);
    }
}
