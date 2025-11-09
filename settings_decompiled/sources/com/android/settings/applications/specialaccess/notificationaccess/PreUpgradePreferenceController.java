package com.android.settings.applications.specialaccess.notificationaccess;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerFilter;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.NotificationBackend;
/* loaded from: classes.dex */
public class PreUpgradePreferenceController extends BasePreferenceController {
    private ComponentName mCn;
    private NotificationListenerFilter mNlf;
    private NotificationBackend mNm;
    private int mTargetSdk;
    private int mUserId;

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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PreUpgradePreferenceController(Context context, String str) {
        super(context, str);
    }

    public PreUpgradePreferenceController setCn(ComponentName componentName) {
        this.mCn = componentName;
        return this;
    }

    public PreUpgradePreferenceController setUserId(int i) {
        this.mUserId = i;
        return this;
    }

    public PreUpgradePreferenceController setNm(NotificationBackend notificationBackend) {
        this.mNm = notificationBackend;
        return this;
    }

    public PreUpgradePreferenceController setTargetSdk(int i) {
        this.mTargetSdk = i;
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mNm.isNotificationListenerAccessGranted(this.mCn)) {
            NotificationListenerFilter listenerFilter = this.mNm.getListenerFilter(this.mCn, this.mUserId);
            this.mNlf = listenerFilter;
            if (this.mTargetSdk <= 31 && listenerFilter.areAllTypesAllowed() && this.mNlf.getDisallowedPackages().isEmpty()) {
                return 0;
            }
        }
        return 2;
    }
}
