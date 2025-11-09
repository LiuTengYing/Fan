package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.ims.internal.ImsManagerEx;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.settings.R$string;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.network.telephony.BroadcastReceiverChanged;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Enhanced4gBasePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop, BroadcastReceiverChanged.BroadcastReceiverChangedClient {
    protected static final int MODE_4G_CALLING = 2;
    protected static final int MODE_ADVANCED_CALL = 1;
    protected static final int MODE_NONE = -1;
    protected static final int MODE_VOLTE = 0;
    private static final int MSG_VOLTE_SETTINGS = 3;
    private static final String TAG = "Enhanced4g";
    private static final long UPDATE_VOLTE_STATUS_DELAY = 1000;
    private int m4gCurrentMode;
    private final List<On4gLteUpdateListener> m4gLteListeners;
    private BroadcastReceiverChanged mBroadcastReceiverChanged;
    private Integer mCallState;
    ContentObserver mContentObserver;
    private MyHandler mHandler;
    private boolean mHas5gCapability;
    boolean mIsNrEnabledFromCarrierConfig;
    Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private boolean mShow5gLimitedDialog;
    private PhoneCallStateTelephonyCallback mTelephonyCallback;

    /* loaded from: classes.dex */
    public interface On4gLteUpdateListener {
        void on4gLteUpdated();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected int getMode() {
        return MODE_NONE;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public Enhanced4gBasePreferenceController(Context context, String str) {
        super(context, str);
        this.mHandler = null;
        this.m4gCurrentMode = MODE_NONE;
        this.m4gLteListeners = new ArrayList();
        this.mHandler = new MyHandler();
    }

    public Enhanced4gBasePreferenceController init(int i) {
        if (this.mTelephonyCallback == null) {
            this.mTelephonyCallback = new PhoneCallStateTelephonyCallback();
        }
        this.mSubId = i;
        PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
        if (carrierConfigForSubId == null) {
            return this;
        }
        boolean z = carrierConfigForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        int i2 = carrierConfigForSubId.getInt("enhanced_4g_lte_title_variant_int");
        this.m4gCurrentMode = i2;
        if (i2 != 1) {
            this.m4gCurrentMode = z ? 2 : 0;
        }
        this.mShow5gLimitedDialog = carrierConfigForSubId.getBoolean("volte_5g_limited_alert_dialog_bool");
        this.mIsNrEnabledFromCarrierConfig = !ArrayUtils.isEmpty(carrierConfigForSubId.getIntArray("carrier_nr_availabilities_int_array"));
        return this;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        init(i);
        if (isModeMatched()) {
            VolteQueryImsState queryImsState = queryImsState(i);
            if (queryImsState.isVoImsOptInEnabled()) {
                return 0;
            }
            PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
            if (isEnhance4gLteEnabled(i, carrierConfigForSubId) && queryImsState.isReadyToVoLte()) {
                return (isUserControlAllowed(carrierConfigForSubId) && queryImsState.isAllowUserControl()) ? 0 : 1;
            }
            return 2;
        }
        return 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback == null) {
            return;
        }
        phoneCallStateTelephonyCallback.register(this.mContext, this.mSubId);
        if (this.mBroadcastReceiverChanged == null) {
            this.mBroadcastReceiverChanged = new BroadcastReceiverChanged(this.mContext, this);
        }
        this.mBroadcastReceiverChanged.start();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.network.telephony.Enhanced4gBasePreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (Enhanced4gBasePreferenceController.this.mHandler.hasMessages(3)) {
                    Enhanced4gBasePreferenceController.this.mHandler.removeMessages(3);
                }
                Enhanced4gBasePreferenceController.this.mHandler.sendMessageDelayed(Enhanced4gBasePreferenceController.this.mHandler.obtainMessage(3), Enhanced4gBasePreferenceController.UPDATE_VOLTE_STATUS_DELAY);
            }
        };
        contentResolver.registerContentObserver(Settings.Global.getUriFor("preferred_network_mode" + this.mSubId), false, this.mContentObserver);
        contentResolver.registerContentObserver(MobileNetworkUtils.getNotifyContentUri(SubscriptionManager.ADVANCED_CALLING_ENABLED_CONTENT_URI, true, this.mSubId), true, this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        BroadcastReceiverChanged broadcastReceiverChanged = this.mBroadcastReceiverChanged;
        if (broadcastReceiverChanged != null) {
            broadcastReceiverChanged.stop();
            this.mBroadcastReceiverChanged = null;
        }
        if (this.mContentObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback == null) {
            return;
        }
        phoneCallStateTelephonyCallback.unregister();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        switchPreference.setVisible(isAvailable());
        VolteQueryImsState queryImsState = queryImsState(this.mSubId);
        boolean z = true;
        switchPreference.setEnabled(isUserControlAllowed(getCarrierConfigForSubId(this.mSubId)) && queryImsState.isAllowUserControl() && isSimStateLoaded());
        if (!queryImsState.isEnabledByUser() || !queryImsState.isAllowUserControl()) {
            z = false;
        }
        switchPreference.setChecked(z);
        if (UniMobileNetworkUtils.isSubsidyLocked(this.mContext) && UniMobileNetworkUtils.isPrefEnabledForSubsidy(this.mContext, this.mSubId)) {
            switchPreference.setEnabled(false);
        }
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onPhoneStateChanged() {
        Log.d(TAG, "onPhoneStateChanged");
        updateState(this.mPreference);
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onCarrierConfigChanged(int i) {
        if (SubscriptionManager.isValidPhoneId(i) && SubscriptionManager.getPhoneId(this.mSubId) == i && SubscriptionManager.getSimStateForSlotIndex(i) == 10) {
            Log.i(TAG, "onCarrierConfigChanged");
            PreferenceScreen preferenceScreen = this.mPreferenceScreen;
            if (preferenceScreen != null) {
                displayPreference(preferenceScreen);
                updateState(this.mPreference);
            }
        }
    }

    public boolean isSimStateLoaded() {
        int phoneId = SubscriptionManager.getPhoneId(this.mSubId);
        return SubscriptionManager.isValidPhoneId(phoneId) && SubscriptionManager.getSimStateForSlotIndex(phoneId) == 10;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            if (!isImsTurnOffAllowed() && !z) {
                Toast.makeText(this.mContext, String.format(this.mContext.getResources().getString(R$string.turn_off_ims_error), getTitle()), 1).show();
                return false;
            }
            ImsMmTelManager createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(this.mSubId);
            if (createForSubscriptionId == null) {
                return false;
            }
            if (isDialogNeeded() && !z) {
                show5gLimitedDialog(createForSubscriptionId);
                return false;
            }
            return setAdvancedCallingSettingEnabled(createForSubscriptionId, z);
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return queryImsState(this.mSubId).isEnabledByUser();
    }

    public Enhanced4gBasePreferenceController addListener(On4gLteUpdateListener on4gLteUpdateListener) {
        this.m4gLteListeners.add(on4gLteUpdateListener);
        return this;
    }

    public Enhanced4gBasePreferenceController addListeners(List<On4gLteUpdateListener> list) {
        this.m4gLteListeners.clear();
        if (list != null) {
            this.m4gLteListeners.addAll(list);
        }
        return this;
    }

    protected String getTitle() {
        return this.mContext.getString(R$string.enhanced_4g_lte_mode_title);
    }

    private boolean isModeMatched() {
        return this.m4gCurrentMode == getMode();
    }

    protected VolteQueryImsState queryImsState(int i) {
        return new VolteQueryImsState(this.mContext, i);
    }

    protected boolean isCallStateIdle() {
        Integer num = this.mCallState;
        return num != null && num.intValue() == 0;
    }

    private boolean isUserControlAllowed(PersistableBundle persistableBundle) {
        return (isInCall() || persistableBundle == null || !persistableBundle.getBoolean("editable_enhanced_4g_lte_bool")) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PhoneCallStateTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        private PhoneCallStateTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.CallStateListener
        public void onCallStateChanged(int i) {
            Enhanced4gBasePreferenceController.this.mCallState = Integer.valueOf(i);
            Enhanced4gBasePreferenceController enhanced4gBasePreferenceController = Enhanced4gBasePreferenceController.this;
            enhanced4gBasePreferenceController.updateState(enhanced4gBasePreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
            }
            Enhanced4gBasePreferenceController.this.mCallState = Integer.valueOf(this.mTelephonyManager.getCallState(i));
            this.mTelephonyManager.registerTelephonyCallback(((AbstractPreferenceController) Enhanced4gBasePreferenceController.this).mContext.getMainExecutor(), Enhanced4gBasePreferenceController.this.mTelephonyCallback);
            long supportedRadioAccessFamily = this.mTelephonyManager.getSupportedRadioAccessFamily();
            Enhanced4gBasePreferenceController.this.mHas5gCapability = (supportedRadioAccessFamily & 524288) > 0;
        }

        public void unregister() {
            Enhanced4gBasePreferenceController.this.mCallState = null;
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager != null) {
                telephonyManager.unregisterTelephonyCallback(this);
            }
        }
    }

    private boolean isDialogNeeded() {
        Log.d(TAG, "Has5gCapability:" + this.mHas5gCapability);
        return this.mShow5gLimitedDialog && this.mHas5gCapability && this.mIsNrEnabledFromCarrierConfig;
    }

    private void show5gLimitedDialog(final ImsMmTelManager imsMmTelManager) {
        Log.d(TAG, "show5gLimitedDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(R$string.volte_5G_limited_title).setMessage(R$string.volte_5G_limited_text).setNeutralButton(this.mContext.getResources().getString(R$string.cancel), (DialogInterface.OnClickListener) null).setPositiveButton(this.mContext.getResources().getString(R$string.condition_turn_off), new DialogInterface.OnClickListener() { // from class: com.android.settings.network.telephony.Enhanced4gBasePreferenceController.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(Enhanced4gBasePreferenceController.TAG, "onClick,isChecked:false");
                Enhanced4gBasePreferenceController.this.setAdvancedCallingSettingEnabled(imsMmTelManager, false);
                Enhanced4gBasePreferenceController enhanced4gBasePreferenceController = Enhanced4gBasePreferenceController.this;
                enhanced4gBasePreferenceController.updateState(enhanced4gBasePreferenceController.mPreference);
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setAdvancedCallingSettingEnabled(ImsMmTelManager imsMmTelManager, boolean z) {
        Log.d(TAG, "set VoLTE state : " + z);
        try {
            imsMmTelManager.setAdvancedCallingSettingEnabled(z);
            for (On4gLteUpdateListener on4gLteUpdateListener : this.m4gLteListeners) {
                on4gLteUpdateListener.on4gLteUpdated();
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "fail to set VoLTE=" + z + ". subId=" + this.mSubId, e);
            return false;
        }
    }

    private boolean isEnhance4gLteEnabled(int i, PersistableBundle persistableBundle) {
        return (persistableBundle == null || persistableBundle.getBoolean("hide_enhanced_4g_lte_bool") || hide4gLteForNetwork(i) || UniTelephonyManager.isDeviceSupportNr()) ? false : true;
    }

    private boolean hide4gLteForNetwork(int i) {
        if (MobileNetworkUtils.getBooleanCarrierConfig(this.mContext, "hide_enhanced_4g_lte_by_network", false, i)) {
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                return !((RadioAccessFamily.getRafFromNetworkType(MobileNetworkUtils.getNetworkTypeFromRaf((int) telephonyManager.createForSubscriptionId(i).getAllowedNetworkTypesForReason(0))) & 266240) != 0);
            }
            return false;
        }
        return false;
    }

    /* loaded from: classes.dex */
    private class MyHandler extends Handler {
        public MyHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 3 && Enhanced4gBasePreferenceController.this.mPreferenceScreen != null) {
                Enhanced4gBasePreferenceController enhanced4gBasePreferenceController = Enhanced4gBasePreferenceController.this;
                enhanced4gBasePreferenceController.displayPreference(enhanced4gBasePreferenceController.mPreferenceScreen);
                Enhanced4gBasePreferenceController enhanced4gBasePreferenceController2 = Enhanced4gBasePreferenceController.this;
                enhanced4gBasePreferenceController2.updateState(enhanced4gBasePreferenceController2.mPreference);
            }
        }
    }

    private boolean isImsTurnOffAllowed() {
        return (ImsManagerEx.synSettingForWFCandVoLTE(this.mContext, this.mSubId) && queryWFCState(this.mSubId).isReadyToWifiCalling() && queryWFCState(this.mSubId).isEnabledByUser()) ? false : true;
    }

    WifiCallingQueryImsState queryWFCState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }

    public boolean isInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) this.mContext.getSystemService(AirplaneModePreferenceController.TELECOM_SERVICE);
    }
}
