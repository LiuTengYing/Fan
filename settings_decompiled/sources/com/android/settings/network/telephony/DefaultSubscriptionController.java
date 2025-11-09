package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Toast;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settingslib.WirelessUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class DefaultSubscriptionController extends TelephonyBasePreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private static final String EMERGENCY_ACCOUNT_HANDLE_ID = "E";
    private static final ComponentName PSTN_CONNECTION_SERVICE_COMPONENT = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
    private static final String TAG = "DefaultSubController";
    private final BroadcastReceiver mBroadcastReceiver;
    protected SubscriptionsChangeListener mChangeListener;
    private boolean mIsRtlMode;
    protected SubscriptionManager mManager;
    protected ListPreference mPreference;
    protected boolean mRegisterBefore;
    protected TelecomManager mTelecomManager;
    protected boolean mUnRegisterBefore;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return 0;
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    protected abstract int getDefaultSubscriptionId();

    protected abstract SubscriptionInfo getDefaultSubscriptionInfo();

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

    protected boolean isAskEverytimeSupported() {
        return true;
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
    public void onAirplaneModeChanged(boolean z) {
    }

    protected abstract void setDefaultSubscription(int i);

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DefaultSubscriptionController(Context context, String str) {
        super(context, str);
        this.mRegisterBefore = false;
        this.mUnRegisterBefore = false;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.DefaultSubscriptionController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                DefaultSubscriptionController defaultSubscriptionController = DefaultSubscriptionController.this;
                if (defaultSubscriptionController.mPreference != null) {
                    defaultSubscriptionController.updateEntries();
                    DefaultSubscriptionController defaultSubscriptionController2 = DefaultSubscriptionController.this;
                    defaultSubscriptionController2.refreshSummary(defaultSubscriptionController2.mPreference);
                }
            }
        };
        this.mManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mChangeListener = new SubscriptionsChangeListener(context, this);
        this.mIsRtlMode = context.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        this.mChangeListener.start();
        if (!this.mRegisterBefore) {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
            intentFilter.addAction("android.telephony.action.DEFAULT_SMS_SUBSCRIPTION_CHANGED");
            this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
            this.mRegisterBefore = true;
            this.mUnRegisterBefore = false;
        }
        updateEntries();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mChangeListener.stop();
        if (this.mUnRegisterBefore) {
            return;
        }
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mRegisterBefore = false;
        this.mUnRegisterBefore = true;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        updateEntries();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$refreshSummary$0(Preference preference) {
        return getSummary();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void refreshSummary(Preference preference) {
        if (preference != null) {
            preference.setSummaryProvider(new Preference.SummaryProvider() { // from class: com.android.settings.network.telephony.DefaultSubscriptionController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.SummaryProvider
                public final CharSequence provideSummary(Preference preference2) {
                    CharSequence lambda$refreshSummary$0;
                    lambda$refreshSummary$0 = DefaultSubscriptionController.this.lambda$refreshSummary$0(preference2);
                    return lambda$refreshSummary$0;
                }
            });
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        PhoneAccountHandle defaultCallingAccountHandle = getDefaultCallingAccountHandle();
        if (defaultCallingAccountHandle != null && !isCallingAccountBindToSubscription(defaultCallingAccountHandle)) {
            return getLabelFromCallingAccount(defaultCallingAccountHandle);
        }
        SubscriptionInfo defaultSubscriptionInfo = getDefaultSubscriptionInfo();
        if (defaultSubscriptionInfo != null) {
            return defaultSubscriptionInfo.getDisplayName();
        }
        return isAskEverytimeSupported() ? this.mContext.getString(R$string.calls_and_sms_ask_every_time) : "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEntries() {
        if (this.mPreference == null) {
            return;
        }
        if (!isAvailable()) {
            this.mPreference.setVisible(false);
            return;
        }
        this.mPreference.setVisible(true);
        this.mPreference.setOnPreferenceChangeListener(this);
        final List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(this.mManager);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (activeSubscriptions.size() == 1) {
            this.mPreference.setEnabled(false);
            this.mPreference.setSummaryProvider(new Preference.SummaryProvider() { // from class: com.android.settings.network.telephony.DefaultSubscriptionController$$ExternalSyntheticLambda1
                @Override // androidx.preference.Preference.SummaryProvider
                public final CharSequence provideSummary(Preference preference) {
                    CharSequence lambda$updateEntries$1;
                    lambda$updateEntries$1 = DefaultSubscriptionController.this.lambda$updateEntries$1(activeSubscriptions, preference);
                    return lambda$updateEntries$1;
                }
            });
            return;
        }
        int defaultSubscriptionId = getDefaultSubscriptionId();
        boolean z = false;
        for (SubscriptionInfo subscriptionInfo : activeSubscriptions) {
            if (!subscriptionInfo.isOpportunistic()) {
                arrayList.add(subscriptionInfo.getDisplayName());
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                arrayList2.add(Integer.toString(subscriptionId));
                if (subscriptionId == defaultSubscriptionId) {
                    z = true;
                }
            }
        }
        if (isAskEverytimeSupported()) {
            arrayList.add(this.mContext.getString(R$string.calls_and_sms_ask_every_time));
            arrayList2.add(Integer.toString(-1));
        }
        this.mPreference.setEnabled(true);
        this.mPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[0]));
        this.mPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[0]));
        if (z) {
            this.mPreference.setValue(Integer.toString(defaultSubscriptionId));
        } else {
            this.mPreference.setValue(Integer.toString(-1));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$updateEntries$1(List list, Preference preference) {
        return SubscriptionUtil.getUniqueSubscriptionDisplayName((SubscriptionInfo) list.get(0), this.mContext);
    }

    public PhoneAccountHandle getDefaultCallingAccountHandle() {
        PhoneAccountHandle userSelectedOutgoingPhoneAccount = getTelecomManager().getUserSelectedOutgoingPhoneAccount();
        if (userSelectedOutgoingPhoneAccount == null) {
            return null;
        }
        List<PhoneAccountHandle> callCapablePhoneAccounts = getTelecomManager().getCallCapablePhoneAccounts(false);
        if (userSelectedOutgoingPhoneAccount.equals(new PhoneAccountHandle(PSTN_CONNECTION_SERVICE_COMPONENT, EMERGENCY_ACCOUNT_HANDLE_ID))) {
            return null;
        }
        for (PhoneAccountHandle phoneAccountHandle : callCapablePhoneAccounts) {
            if (userSelectedOutgoingPhoneAccount.equals(phoneAccountHandle)) {
                return userSelectedOutgoingPhoneAccount;
            }
        }
        return null;
    }

    @VisibleForTesting
    TelecomManager getTelecomManager() {
        if (this.mTelecomManager == null) {
            this.mTelecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        }
        return this.mTelecomManager;
    }

    @VisibleForTesting
    PhoneAccount getPhoneAccount(PhoneAccountHandle phoneAccountHandle) {
        return getTelecomManager().getPhoneAccount(phoneAccountHandle);
    }

    public boolean isCallingAccountBindToSubscription(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = getPhoneAccount(phoneAccountHandle);
        if (phoneAccount == null) {
            return false;
        }
        return phoneAccount.hasCapabilities(4);
    }

    public CharSequence getLabelFromCallingAccount(PhoneAccountHandle phoneAccountHandle) {
        PhoneAccount phoneAccount = getPhoneAccount(phoneAccountHandle);
        CharSequence label = phoneAccount != null ? phoneAccount.getLabel() : null;
        if (label != null) {
            label = this.mContext.getPackageManager().getUserBadgedLabel(label, phoneAccountHandle.getUserHandle());
        }
        return label != null ? label : "";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        if (this.mManager.getActiveSubscriptionInfoCount() <= 1 || WirelessUtils.isAirplaneModeOn(this.mContext)) {
            Toast.makeText(this.mContext, R$string.default_subscription_invalid, 0).show();
            return false;
        }
        setDefaultSubscription(parseInt);
        refreshSummary(this.mPreference);
        return true;
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        if (this.mPreference != null) {
            updateEntries();
            refreshSummary(this.mPreference);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRtlMode() {
        return this.mIsRtlMode;
    }
}
