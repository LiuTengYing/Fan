package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telecom.PhoneAccountHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
/* loaded from: classes.dex */
public class SmsDefaultSubscriptionController extends DefaultSubscriptionController {
    private final boolean mIsAskEverytimeSupported;

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return 3;
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    public PhoneAccountHandle getDefaultCallingAccountHandle() {
        return null;
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SmsDefaultSubscriptionController(Context context, String str) {
        super(context, str);
        this.mIsAskEverytimeSupported = this.mContext.getResources().getBoolean(17891759);
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected SubscriptionInfo getDefaultSubscriptionInfo() {
        return this.mManager.getActiveSubscriptionInfo(getDefaultSubscriptionId());
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected int getDefaultSubscriptionId() {
        return SubscriptionManager.getDefaultSmsSubscriptionId();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected void setDefaultSubscription(int i) {
        this.mManager.setDefaultSmsSubId(i);
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected boolean isAskEverytimeSupported() {
        return this.mIsAskEverytimeSupported;
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return MobileNetworkUtils.getPreferredStatus(isRtlMode(), this.mContext, this.mManager, false);
    }
}
