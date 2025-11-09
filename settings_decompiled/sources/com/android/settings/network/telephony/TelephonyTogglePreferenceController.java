package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.telephony.SubscriptionManager;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.network.CarrierConfigCache;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class TelephonyTogglePreferenceController extends TogglePreferenceController implements TelephonyAvailabilityCallback, TelephonyAvailabilityHandler {
    private AtomicInteger mAvailabilityStatus;
    private AtomicInteger mSetSessionCount;
    protected int mSubId;

    public abstract /* synthetic */ int getAvailabilityStatus(int i);

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public TelephonyTogglePreferenceController(Context context, String str) {
        super(context, str);
        this.mAvailabilityStatus = new AtomicInteger(0);
        this.mSetSessionCount = new AtomicInteger(0);
        this.mSubId = -1;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mSetSessionCount.get() <= 0) {
            this.mAvailabilityStatus.set(MobileNetworkUtils.getAvailability(this.mContext, this.mSubId, new TelephonyAvailabilityCallback() { // from class: com.android.settings.network.telephony.TelephonyTogglePreferenceController$$ExternalSyntheticLambda0
                @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback
                public final int getAvailabilityStatus(int i) {
                    return TelephonyTogglePreferenceController.this.getAvailabilityStatus(i);
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
            return CarrierConfigCache.getInstance(this.mContext).getConfigForSubId(i);
        }
        return null;
    }

    public Resources getResourcesForSubId() {
        return SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId);
    }
}
