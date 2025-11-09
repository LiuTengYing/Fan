package com.unisoc.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
/* loaded from: classes2.dex */
public class UniMobileNetworkSwitchController extends BasePreferenceController implements SubscriptionsChangeListener.SubscriptionsChangeListenerClient, LifecycleObserver {
    private static final String TAG = "UniMobileNetworkSwitchCtrl";
    private final BroadcastReceiver mBroadcastReceiver;
    private SubscriptionsChangeListener mChangeListener;
    private final PhoneStateListener mPhoneStateListener;
    private int mSubId;
    private SubscriptionManager mSubscriptionManager;
    private SettingsMainSwitchPreference mSwitchBar;
    private TelephonyManager mTelephonyManager;
    private final Handler mUiHandler;
    private UniTelephonyManager mUniTelephonyManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public UniMobileNetworkSwitchController(Context context, String str) {
        super(context, str);
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.unisoc.settings.network.telephony.UniMobileNetworkSwitchController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String stringExtra;
                if (intent == null || (stringExtra = intent.getStringExtra("ss")) == null) {
                    return;
                }
                if (stringExtra.equals("LOADED") || stringExtra.equals("NOT_READY")) {
                    UniMobileNetworkSwitchController.this.update();
                }
            }
        };
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mSubId = -1;
        this.mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        this.mTelephonyManager = TelephonyManager.from(this.mContext);
        this.mUniTelephonyManager = UniTelephonyManager.from(context);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mPhoneStateListener = new PhoneStateListener() { // from class: com.unisoc.settings.network.telephony.UniMobileNetworkSwitchController.1
            @Override // android.telephony.PhoneStateListener
            public void onCallStateChanged(int i, String str2) {
                UniMobileNetworkSwitchController.this.update();
                Log.d(UniMobileNetworkSwitchController.TAG, "onPreciseCallStateChanged state: " + i + " incomingNumber:" + str2);
            }
        };
    }

    public void init(int i) {
        this.mSubId = i;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        this.mSwitchBar.setCheckedInternal(getSubEnable(this.mSubId));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, 2);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
        update();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SettingsMainSwitchPreference settingsMainSwitchPreference = (SettingsMainSwitchPreference) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mSwitchBar = settingsMainSwitchPreference;
        settingsMainSwitchPreference.setTitle(this.mContext.getString(R$string.mobile_network_use_sim_on));
        this.mSwitchBar.setOnBeforeCheckedChangeListener(new SettingsMainSwitchBar.OnBeforeCheckedChangeListener() { // from class: com.unisoc.settings.network.telephony.UniMobileNetworkSwitchController$$ExternalSyntheticLambda0
            @Override // com.android.settings.widget.SettingsMainSwitchBar.OnBeforeCheckedChangeListener
            public final boolean onBeforeCheckedChanged(Switch r1, boolean z) {
                boolean lambda$displayPreference$0;
                lambda$displayPreference$0 = UniMobileNetworkSwitchController.this.lambda$displayPreference$0(r1, z);
                return lambda$displayPreference$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$displayPreference$0(Switch r2, boolean z) {
        if (this.mSubscriptionManager.isActiveSubscriptionId(this.mSubId) != z) {
            SubscriptionUtil.startToggleSubscriptionDialogActivity(this.mContext, this.mSubId, z);
            Log.d(TAG, "SwitchBar should switch");
            return true;
        }
        Log.d(TAG, "SwitchBar shouldn't switch or setSubscriptionEnabled succeed");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (this.mSwitchBar == null) {
            return;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.unisoc.settings.network.telephony.UniMobileNetworkSwitchController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                UniMobileNetworkSwitchController.this.lambda$update$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$2() {
        final boolean subEnable = getSubEnable(this.mSubId);
        final SubscriptionInfo subInfo = getSubInfo();
        this.mUiHandler.post(new Runnable() { // from class: com.unisoc.settings.network.telephony.UniMobileNetworkSwitchController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                UniMobileNetworkSwitchController.this.lambda$update$1(subInfo, subEnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$update$1(SubscriptionInfo subscriptionInfo, boolean z) {
        boolean z2 = (WirelessUtils.isAirplaneModeOn(this.mContext) || isPhoneInCall()) ? false : true;
        if (subscriptionInfo == null || ((!subscriptionInfo.isEmbedded() && !SubscriptionUtil.showToggleForPhysicalSim(this.mSubscriptionManager)) || (isNeedRestric() && isRestrictSlotAuto(subscriptionInfo.getSimSlotIndex())))) {
            this.mSwitchBar.hide();
            return;
        }
        boolean z3 = UniMobileNetworkUtils.isDisableSimAllowedForSubsidy(this.mContext, subscriptionInfo.getIccId()) ? z2 : false;
        this.mSwitchBar.setCheckedInternal(z);
        this.mSwitchBar.setEnabled(z3);
    }

    public SubscriptionInfo getSubInfo() {
        for (SubscriptionInfo subscriptionInfo : SubscriptionUtil.getAvailableSubscriptions(this.mContext)) {
            if (subscriptionInfo.getSubscriptionId() == this.mSubId) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private boolean isRestrictSlotAuto(int i) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("restrict_sim_slot_auto");
        sb.append(i);
        return Settings.System.getInt(contentResolver, sb.toString(), 0) == 1;
    }

    private boolean isNeedRestric() {
        return "true".equals(Settings.System.getString(this.mContext.getContentResolver(), "dm_sim_restrict"));
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
        update();
    }

    private boolean isPhoneInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) this.mContext.getSystemService(AirplaneModePreferenceController.TELECOM_SERVICE);
    }

    private boolean getSubEnable(int i) {
        int isEnableSubId = this.mUniTelephonyManager.isEnableSubId(i);
        Log.d(TAG, "isEnable = " + isEnableSubId + "isActive = " + this.mSubscriptionManager.isActiveSubscriptionId(i));
        if (isEnableSubId == 1) {
            return true;
        }
        if (isEnableSubId == 0) {
            return false;
        }
        return this.mSubscriptionManager.isActiveSubscriptionId(i);
    }
}
