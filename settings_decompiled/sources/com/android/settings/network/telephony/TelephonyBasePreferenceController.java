package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import com.android.settings.core.BasePreferenceController;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class TelephonyBasePreferenceController extends BasePreferenceController implements TelephonyAvailabilityCallback, TelephonyAvailabilityHandler {
    private AtomicInteger mAvailabilityStatus;
    private AtomicInteger mSetSessionCount;
    protected int mSubId;

    public abstract /* synthetic */ int getAvailabilityStatus(int i);

    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TelephonyBasePreferenceController(Context context, String str) {
        super(context, str);
        this.mAvailabilityStatus = new AtomicInteger(0);
        this.mSetSessionCount = new AtomicInteger(0);
        this.mSubId = -1;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mSetSessionCount.get() <= 0) {
            this.mAvailabilityStatus.set(MobileNetworkUtils.getAvailability(this.mContext, this.mSubId, new TelephonyAvailabilityCallback() { // from class: com.android.settings.network.telephony.TelephonyBasePreferenceController$$ExternalSyntheticLambda0
                @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback
                public final int getAvailabilityStatus(int i) {
                    return TelephonyBasePreferenceController.this.getAvailabilityStatus(i);
                }
            }));
        }
        return this.mAvailabilityStatus.get();
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityHandler
    public void setAvailabilityStatus(int i) {
        this.mAvailabilityStatus.set(i);
        this.mSetSessionCount.getAndIncrement();
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityHandler
    public void unsetAvailabilityStatus() {
        this.mSetSessionCount.getAndDecrement();
    }

    public PersistableBundle getCarrierConfigForSubId(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return ((CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        }
        return null;
    }

    public Resources getResourcesForSubId() {
        return SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
    }
}
