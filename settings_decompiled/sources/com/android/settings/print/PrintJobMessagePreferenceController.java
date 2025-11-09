package com.android.settings.print;

import android.content.Context;
import android.content.IntentFilter;
import android.print.PrintJob;
import android.text.TextUtils;
/* loaded from: classes.dex */
public class PrintJobMessagePreferenceController extends PrintJobPreferenceControllerBase {
    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PrintJobMessagePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.print.PrintJobPreferenceControllerBase
    protected void updateUi() {
        PrintJob printJob = getPrintJob();
        if (printJob == null) {
            this.mFragment.finish();
        } else if (printJob.isCancelled() || printJob.isCompleted()) {
            this.mFragment.finish();
        } else {
            CharSequence status = printJob.getInfo().getStatus(this.mContext.getPackageManager());
            this.mPreference.setVisible(!TextUtils.isEmpty(status));
            this.mPreference.setSummary(status);
        }
    }
}
