package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class ConversationListSummaryPreferenceController extends BasePreferenceController {
    private NotificationBackend mBackend;

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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ConversationListSummaryPreferenceController(Context context, String str) {
        super(context, str);
        this.mBackend = new NotificationBackend();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int size = this.mBackend.getConversations(true).getList().size();
        if (size == 0) {
            return this.mContext.getText(R$string.priority_conversation_count_zero);
        }
        return this.mContext.getResources().getQuantityString(R$plurals.priority_conversation_count, size, Integer.valueOf(size));
    }

    void setBackend(NotificationBackend notificationBackend) {
        this.mBackend = notificationBackend;
    }
}
