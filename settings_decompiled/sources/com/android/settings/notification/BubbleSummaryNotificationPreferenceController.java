package com.android.settings.notification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class BubbleSummaryNotificationPreferenceController extends BasePreferenceController {
    static final int ON = 1;

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

    public BubbleSummaryNotificationPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        Context context = this.mContext;
        if (areBubblesEnabled()) {
            i = R$string.notifications_bubble_setting_on_summary;
        } else {
            i = R$string.switch_off_text;
        }
        return context.getString(i);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ((ActivityManager) this.mContext.getSystemService(ActivityManager.class)).isLowRamDevice() ? 3 : 0;
    }

    private boolean areBubblesEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "notification_bubbles", 1) == 1;
    }
}
