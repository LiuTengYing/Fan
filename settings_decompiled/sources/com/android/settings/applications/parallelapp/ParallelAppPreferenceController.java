package com.android.settings.applications.parallelapp;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class ParallelAppPreferenceController extends BasePreferenceController {
    private Context mContext;
    private final UserManager mUm;

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

    public ParallelAppPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mUm = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        boolean isAdminUser = this.mUm.isAdminUser();
        boolean z = this.mContext.getResources().getBoolean(R$bool.config_support_appclone);
        Log.d("fangli", "isAdmin" + isAdminUser + z);
        return 0;
    }
}
