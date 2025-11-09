package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.datausage.DataUsageUtils;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.telephony.Rlog;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
/* loaded from: classes.dex */
public class DataDuringCallsPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String LOG_TAG = "DataDuringCallsPreferenceController";
    private SubscriptionsChangeListener mChangeListener;
    private TelephonyManager mManager;
    private MobileDataContentObserver mMobileDataContentObserver;
    private SwitchPreference mPreference;
    private PreferenceScreen mScreen;

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

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DataDuringCallsPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(int i) {
        this.mSubId = i;
        this.mManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mChangeListener == null) {
            this.mChangeListener = new SubscriptionsChangeListener(this.mContext, this);
        }
        this.mChangeListener.start();
        if (this.mMobileDataContentObserver == null) {
            MobileDataContentObserver mobileDataContentObserver = new MobileDataContentObserver(new Handler(Looper.getMainLooper()));
            this.mMobileDataContentObserver = mobileDataContentObserver;
            mobileDataContentObserver.setOnMobileDataChangedListener(new MobileDataContentObserver.OnMobileDataChangedListener() { // from class: com.android.settings.network.telephony.DataDuringCallsPreferenceController$$ExternalSyntheticLambda0
                @Override // com.android.settings.network.MobileDataContentObserver.OnMobileDataChangedListener
                public final void onMobileDataChanged() {
                    DataDuringCallsPreferenceController.this.lambda$onResume$0();
                }
            });
        }
        this.mMobileDataContentObserver.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionsChangeListener subscriptionsChangeListener = this.mChangeListener;
        if (subscriptionsChangeListener != null) {
            subscriptionsChangeListener.stop();
        }
        MobileDataContentObserver mobileDataContentObserver = this.mMobileDataContentObserver;
        if (mobileDataContentObserver != null) {
            mobileDataContentObserver.unRegister(this.mContext);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mManager.isMobileDataPolicyEnabled(1);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mManager.setMobileDataPolicyEnabled(1, z);
        return true;
    }

    @VisibleForTesting
    protected boolean hasMobileData() {
        return DataUsageUtils.hasMobileData(this.mContext);
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        log("subId = " + i + " ,DefaultDataSubscriptionId = " + SubscriptionManager.getDefaultDataSubscriptionId());
        return (SubscriptionManager.isValidSubscriptionId(i) && SubscriptionManager.getDefaultDataSubscriptionId() != i && hasMobileData()) ? 0 : 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference == null) {
            return;
        }
        preference.setVisible(isAvailable());
        if (UniMobileNetworkUtils.isPrefEnabledForSubsidy(this.mContext, this.mSubId)) {
            return;
        }
        this.mPreference.setEnabled(false);
        setChecked(false);
        log("subsidylock setEnabled false");
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        updateState(this.mPreference);
    }

    @VisibleForTesting
    /* renamed from: refreshPreference */
    public void lambda$onResume$0() {
        PreferenceScreen preferenceScreen = this.mScreen;
        if (preferenceScreen != null) {
            super.displayPreference(preferenceScreen);
        }
    }

    private void log(String str) {
        Rlog.d(LOG_TAG, str);
    }
}
