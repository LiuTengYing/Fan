package com.android.settings.applications.specialaccess.notificationaccess;

import android.content.Context;
import android.content.IntentFilter;
/* loaded from: classes.dex */
public class AlertingTypeFilterPreferenceController extends TypeFilterPreferenceController {
    private static final String TAG = "AlertFilterPrefCntlr";

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController
    protected int getType() {
        return 2;
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.applications.specialaccess.notificationaccess.TypeFilterPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AlertingTypeFilterPreferenceController(Context context, String str) {
        super(context, str);
    }
}
