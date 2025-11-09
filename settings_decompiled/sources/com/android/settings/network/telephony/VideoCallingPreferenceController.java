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
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
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
public class VideoCallingPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop, MobileDataEnabledListener.Client, Enhanced4gBasePreferenceController.On4gLteUpdateListener, BroadcastReceiverChanged.BroadcastReceiverChangedClient {
    private static final int MSG_VOLTE_UPDATE = 1;
    private static final String TAG = "VideoCallingPreference";
    private BroadcastReceiverChanged mBroadcastReceiverChanged;
    Integer mCallState;
    private MobileDataEnabledListener mDataContentObserver;
    private MyHandler mHandler;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private PhoneTelephonyCallback mTelephonyCallback;

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onPhoneStateChanged() {
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VideoCallingPreferenceController(Context context, String str) {
        super(context, str);
        this.mHandler = null;
        this.mDataContentObserver = new MobileDataEnabledListener(context, this);
        this.mTelephonyCallback = new PhoneTelephonyCallback();
        this.mBroadcastReceiverChanged = new BroadcastReceiverChanged(context, this);
        this.mHandler = new MyHandler();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (SubscriptionManager.isValidSubscriptionId(i) && isVideoCallEnabled(i)) ? 0 : 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
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

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onCarrierConfigChanged(int i) {
        PreferenceScreen preferenceScreen;
        if (SubscriptionManager.isValidPhoneId(i) && SubscriptionManager.getPhoneId(this.mSubId) == i && SubscriptionManager.getSimStateForSlotIndex(i) == 10 && (preferenceScreen = this.mPreferenceScreen) != null) {
            displayPreference(preferenceScreen);
            updateState(this.mPreference);
        }
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
            VideoCallingPreferenceController videoCallingPreferenceController = VideoCallingPreferenceController.this;
            videoCallingPreferenceController.updateState(videoCallingPreferenceController.mPreference);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mCallState == null || preference == null) {
            Log.d(TAG, "Skip update under mCallState=" + this.mCallState);
            return;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        boolean isVideoCallEnabled = isVideoCallEnabled(this.mSubId);
        switchPreference.setVisible(isVideoCallEnabled);
        if (isVideoCallEnabled) {
            boolean z = true;
            boolean z2 = (queryVoLteState(this.mSubId).isEnabledByUser() || (queryWFCState(this.mSubId).isReadyToWifiCalling() && queryWFCState(this.mSubId).isEnabledByUser())) && queryImsState(this.mSubId).isAllowUserControl();
            preference.setEnabled(z2 && this.mCallState.intValue() == 0);
            if (!z2 || !isChecked()) {
                z = false;
            }
            switchPreference.setChecked(z);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ImsMmTelManager createForSubscriptionId;
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && (createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(this.mSubId)) != null) {
            try {
                createForSubscriptionId.setVtSettingEnabled(z);
                return true;
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Unable to set VT status " + z + ". subId=" + this.mSubId, e);
                return false;
            }
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return queryImsState(this.mSubId).isEnabledByUser();
    }

    protected boolean isImsSupported() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony.ims");
    }

    public VideoCallingPreferenceController init(int i) {
        this.mSubId = i;
        return this;
    }

    boolean isVideoCallEnabled(int i) {
        PersistableBundle configForSubId;
        if (SubscriptionManager.isValidSubscriptionId(i) && (configForSubId = CarrierConfigCache.getInstance(this.mContext).getConfigForSubId(i)) != null) {
            return (configForSubId.getBoolean("ignore_data_enabled_changed_for_video_calls") || ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).isDataEnabled()) && isImsSupported() && queryImsState(i).isReadyToVideoCall();
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
            VideoCallingPreferenceController.this.mCallState = Integer.valueOf(i);
            VideoCallingPreferenceController videoCallingPreferenceController = VideoCallingPreferenceController.this;
            videoCallingPreferenceController.updateState(videoCallingPreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
            }
            VideoCallingPreferenceController.this.mCallState = Integer.valueOf(this.mTelephonyManager.getCallState(i));
            this.mTelephonyManager.registerTelephonyCallback(context.getMainExecutor(), this);
        }

        public void unregister() {
            VideoCallingPreferenceController.this.mCallState = null;
            this.mTelephonyManager.unregisterTelephonyCallback(this);
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
