package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.ProvisioningManager;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.CarrierConfigCache;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.network.ims.VtQueryImsState;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.network.telephony.BroadcastReceiverChanged;
import com.android.settings.network.telephony.Enhanced4gBasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class VideoResolutionPreferenceController extends TelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop, MobileDataEnabledListener.Client, Preference.OnPreferenceChangeListener, Enhanced4gBasePreferenceController.On4gLteUpdateListener, BroadcastReceiverChanged.BroadcastReceiverChangedClient {
    private static final int MSG_VOLTE_UPDATE = 1;
    private static final String TAG = "VideoResolutionPreference";
    private BroadcastReceiverChanged mBroadcastReceiverChanged;
    Integer mCallState;
    private MobileDataEnabledListener mDataContentObserver;
    private MyHandler mHandler;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private PhoneTelephonyCallback mTelephonyCallback;

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

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onPhoneStateChanged() {
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VideoResolutionPreferenceController(Context context, String str) {
        super(context, str);
        this.mHandler = null;
        this.mDataContentObserver = new MobileDataEnabledListener(context, this);
        this.mTelephonyCallback = new PhoneTelephonyCallback();
        this.mHandler = new MyHandler();
        this.mBroadcastReceiverChanged = new BroadcastReceiverChanged(context, this);
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (SubscriptionManager.isValidSubscriptionId(i) && isVtResolutionEnabled(i)) ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mTelephonyCallback.register(this.mContext, this.mSubId);
        this.mDataContentObserver.start(this.mSubId);
        this.mBroadcastReceiverChanged.start();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mTelephonyCallback.unregister();
        this.mDataContentObserver.stop();
        this.mBroadcastReceiverChanged.stop();
    }

    /* loaded from: classes.dex */
    private class MyHandler extends Handler {
        public MyHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            VideoResolutionPreferenceController videoResolutionPreferenceController = VideoResolutionPreferenceController.this;
            videoResolutionPreferenceController.updateState(videoResolutionPreferenceController.mPreference);
        }
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onCarrierConfigChanged(int i) {
        PreferenceScreen preferenceScreen;
        if (SubscriptionManager.isValidPhoneId(i) && SubscriptionManager.getPhoneId(this.mSubId) == i && SubscriptionManager.getSimStateForSlotIndex(i) == 10 && (preferenceScreen = this.mPreferenceScreen) != null) {
            displayPreference(preferenceScreen);
            updateState(this.mPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!(preference instanceof ListPreference) || this.mCallState == null || preference == null) {
            return;
        }
        ListPreference listPreference = (ListPreference) preference;
        getVideoQualityFromPreference(listPreference);
        if (isVtResolutionEnabled(this.mSubId)) {
            boolean z = true;
            if (!((queryVoLteState(this.mSubId).isEnabledByUser() || (queryWFCState(this.mSubId).isReadyToWifiCalling() && queryWFCState(this.mSubId).isEnabledByUser() && this.mSubId == SubscriptionManager.getDefaultDataSubscriptionId())) && queryImsState(this.mSubId).isAllowUserControl() && MobileNetworkUtils.isVTResolutionEditable(this.mContext, this.mSubId)) || this.mCallState.intValue() != 0) {
                z = false;
            }
            listPreference.setEnabled(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference instanceof ListPreference) {
            int parseInt = Integer.parseInt((String) obj);
            ListPreference listPreference = (ListPreference) preference;
            ProvisioningManager createForSubscriptionId = ProvisioningManager.createForSubscriptionId(this.mSubId);
            Log.d(TAG, "onPreferenceChange videoQuality = " + parseInt);
            if (createForSubscriptionId == null) {
                return false;
            }
            try {
                createForSubscriptionId.setProvisioningIntValue(55, parseInt);
            } catch (Exception e) {
                Log.w(TAG, "Unable to set VT resolution videoQuality = " + parseInt + ". subId =" + this.mSubId, e);
            }
            listPreference.setValueIndex(parseInt);
            listPreference.setSummary(listPreference.getEntry());
            return true;
        }
        return false;
    }

    public VideoResolutionPreferenceController init(int i) {
        this.mSubId = i;
        return this;
    }

    boolean isVtResolutionEnabled(int i) {
        PersistableBundle configForSubId;
        if (!SubscriptionManager.isValidSubscriptionId(i) || (configForSubId = CarrierConfigCache.getInstance(this.mContext).getConfigForSubId(i)) == null || configForSubId.getBoolean("hide_vt_resolution_bool", false)) {
            return false;
        }
        if (configForSubId.getBoolean("ignore_data_enabled_changed_for_video_calls") || ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).isDataEnabled()) {
            return queryImsState(i).isReadyToVideoCall();
        }
        return false;
    }

    @Override // com.android.settings.network.telephony.Enhanced4gBasePreferenceController.On4gLteUpdateListener
    public void on4gLteUpdated() {
        if (this.mHandler.hasMessages(1)) {
            this.mHandler.removeMessages(1);
        }
        MyHandler myHandler = this.mHandler;
        myHandler.sendMessage(myHandler.obtainMessage(1));
    }

    /* loaded from: classes.dex */
    private class PhoneTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        private PhoneTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.CallStateListener
        public void onCallStateChanged(int i) {
            VideoResolutionPreferenceController.this.mCallState = Integer.valueOf(i);
            VideoResolutionPreferenceController videoResolutionPreferenceController = VideoResolutionPreferenceController.this;
            videoResolutionPreferenceController.updateState(videoResolutionPreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
            }
            VideoResolutionPreferenceController.this.mCallState = Integer.valueOf(this.mTelephonyManager.getCallState(i));
            this.mTelephonyManager.registerTelephonyCallback(context.getMainExecutor(), this);
        }

        public void unregister() {
            VideoResolutionPreferenceController.this.mCallState = null;
            this.mTelephonyManager.unregisterTelephonyCallback(this);
        }
    }

    private void getVideoQualityFromPreference(ListPreference listPreference) {
        try {
            int resolutionValue = queryImsState(this.mSubId).getResolutionValue(55);
            Log.d(TAG, "get vt resolution value = " + resolutionValue);
            listPreference.setValueIndex(resolutionValue);
            listPreference.setSummary(listPreference.getEntry());
        } catch (Exception unused) {
            Log.w(TAG, "Unable to get VT resolution subId =" + this.mSubId);
        }
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        updateState(this.mPreference);
    }

    VtQueryImsState queryImsState(int i) {
        return new VtQueryImsState(this.mContext, i);
    }

    VolteQueryImsState queryVoLteState(int i) {
        return new VolteQueryImsState(this.mContext, i);
    }

    WifiCallingQueryImsState queryWFCState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }
}
