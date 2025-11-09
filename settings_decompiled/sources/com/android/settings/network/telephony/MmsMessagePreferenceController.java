package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.Arrays;
import java.util.HashSet;
/* loaded from: classes.dex */
public class MmsMessagePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "MmsMessagePreferenceController";
    private MobileDataContentObserver mMobileDataContentObserver;
    private PreferenceScreen mScreen;
    private SubscriptionManager mSubscriptionManager;
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

    public MmsMessagePreferenceController(Context context, String str) {
        super(context, str);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        MobileDataContentObserver mobileDataContentObserver = new MobileDataContentObserver(new Handler(Looper.getMainLooper()));
        this.mMobileDataContentObserver = mobileDataContentObserver;
        mobileDataContentObserver.setOnMobileDataChangedListener(new MobileDataContentObserver.OnMobileDataChangedListener() { // from class: com.android.settings.network.telephony.MmsMessagePreferenceController$$ExternalSyntheticLambda0
            @Override // com.android.settings.network.MobileDataContentObserver.OnMobileDataChangedListener
            public final void onMobileDataChanged() {
                MmsMessagePreferenceController.this.lambda$new$0();
            }
        });
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        return (i == -1 || createForSubscriptionId.isDataEnabled() || !isApnMetered(i, createForSubscriptionId) || !createForSubscriptionId.isApnMetered(2)) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        int i = this.mSubId;
        if (i != -1) {
            this.mMobileDataContentObserver.register(this.mContext, i);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mSubId != -1) {
            this.mMobileDataContentObserver.unRegister(this.mContext);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mTelephonyManager.setMobileDataPolicyEnabled(2, z);
        return z == this.mTelephonyManager.isMobileDataPolicyEnabled(2);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTelephonyManager.isDataEnabledForApn(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: refreshPreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mScreen;
        if (preferenceScreen != null) {
            super.displayPreference(preferenceScreen);
        }
    }

    private boolean isApnMetered(int i, TelephonyManager telephonyManager) {
        PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
        if (carrierConfigForSubId == null) {
            return true;
        }
        if (!CarrierConfigManager.isConfigForIdentifiedCarrier(carrierConfigForSubId)) {
            Log.d(TAG, "carrier config has been not applied.");
            return false;
        }
        ServiceState serviceState = telephonyManager.getServiceState();
        if (serviceState == null) {
            return true;
        }
        if (new HashSet(Arrays.asList(carrierConfigForSubId.getStringArray(serviceState.getDataRoaming() ? "carrier_metered_roaming_apn_types_strings" : "carrier_metered_apn_types_strings"))).contains(ApnSetting.getApnTypeString(2))) {
            return true;
        }
        Log.d(TAG, ApnSetting.getApnTypeString(2) + " is not metered.");
        return false;
    }
}
