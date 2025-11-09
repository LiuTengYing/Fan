package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class NrAdvancedCallingPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "VoNrSettings";
    private Integer mCallState;
    private boolean mHas5gCapability;
    private boolean mIsNrEnableFromCarrierConfig;
    private boolean mIsVonrEnabledFromCarrierConfig;
    private boolean mIsVonrVisibleFromCarrierConfig;
    Preference mPreference;
    private PhoneCallStateTelephonyCallback mTelephonyCallback;
    private TelephonyManager mTelephonyManager;

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

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NrAdvancedCallingPreferenceController(Context context, String str) {
        super(context, str);
        this.mIsVonrEnabledFromCarrierConfig = false;
        this.mIsVonrVisibleFromCarrierConfig = false;
        this.mIsNrEnableFromCarrierConfig = false;
        this.mHas5gCapability = false;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
    }

    public NrAdvancedCallingPreferenceController init(int i) {
        Log.d(TAG, "init: ");
        if (this.mTelephonyCallback == null) {
            this.mTelephonyCallback = new PhoneCallStateTelephonyCallback();
        }
        this.mSubId = i;
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        }
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
        }
        this.mHas5gCapability = (this.mTelephonyManager.getSupportedRadioAccessFamily() & 524288) > 0;
        PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
        if (carrierConfigForSubId == null) {
            return this;
        }
        this.mIsVonrEnabledFromCarrierConfig = carrierConfigForSubId.getBoolean("vonr_enabled_bool");
        this.mIsVonrVisibleFromCarrierConfig = carrierConfigForSubId.getBoolean("vonr_setting_visibility_bool");
        this.mIsNrEnableFromCarrierConfig = !ArrayUtils.isEmpty(carrierConfigForSubId.getIntArray("carrier_nr_availabilities_int_array"));
        Log.d(TAG, "mHas5gCapability: " + this.mHas5gCapability + ",mIsNrEnabledFromCarrierConfig: " + this.mIsNrEnableFromCarrierConfig + ",mIsVonrEnabledFromCarrierConfig: " + this.mIsVonrEnabledFromCarrierConfig + ",mIsVonrVisibleFromCarrierConfig: " + this.mIsVonrVisibleFromCarrierConfig);
        return this;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        init(i);
        return (this.mHas5gCapability && this.mIsNrEnableFromCarrierConfig && this.mIsVonrEnabledFromCarrierConfig && this.mIsVonrVisibleFromCarrierConfig) ? 0 : 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback == null) {
            return;
        }
        phoneCallStateTelephonyCallback.register(this.mTelephonyManager);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback == null) {
            return;
        }
        phoneCallStateTelephonyCallback.unregister();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null) {
            return;
        }
        ((SwitchPreference) preference).setEnabled(isUserControlAllowed());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            Log.d(TAG, "setChecked: " + z);
            int voNrEnabled = this.mTelephonyManager.setVoNrEnabled(z);
            if (voNrEnabled == 0) {
                return true;
            }
            Log.d(TAG, "Fail to set VoNR result= " + voNrEnabled + ". subId=" + this.mSubId);
            return false;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTelephonyManager.isVoNrEnabled();
    }

    protected boolean isCallStateIdle() {
        Integer num = this.mCallState;
        return num != null && num.intValue() == 0;
    }

    private boolean isUserControlAllowed() {
        return isCallStateIdle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PhoneCallStateTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mLocalTelephonyManager;

        private PhoneCallStateTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.CallStateListener
        public void onCallStateChanged(int i) {
            NrAdvancedCallingPreferenceController.this.mCallState = Integer.valueOf(i);
            NrAdvancedCallingPreferenceController nrAdvancedCallingPreferenceController = NrAdvancedCallingPreferenceController.this;
            nrAdvancedCallingPreferenceController.updateState(nrAdvancedCallingPreferenceController.mPreference);
        }

        public void register(TelephonyManager telephonyManager) {
            this.mLocalTelephonyManager = telephonyManager;
            NrAdvancedCallingPreferenceController.this.mCallState = Integer.valueOf(telephonyManager.getCallState());
            this.mLocalTelephonyManager.registerTelephonyCallback(((AbstractPreferenceController) NrAdvancedCallingPreferenceController.this).mContext.getMainExecutor(), NrAdvancedCallingPreferenceController.this.mTelephonyCallback);
        }

        public void unregister() {
            NrAdvancedCallingPreferenceController.this.mCallState = null;
            TelephonyManager telephonyManager = this.mLocalTelephonyManager;
            if (telephonyManager != null) {
                telephonyManager.unregisterTelephonyCallback(this);
            }
        }
    }
}
