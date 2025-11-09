package com.android.settings.language;

import android.content.Context;
import android.content.IntentFilter;
import android.speech.tts.TtsEngines;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class TtsPreferenceController extends BasePreferenceController {
    TtsEngines mTtsEngines;

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

    public TtsPreferenceController(Context context, String str) {
        super(context, str);
        this.mTtsEngines = new TtsEngines(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mTtsEngines.getEngines().isEmpty() || !this.mContext.getResources().getBoolean(R$bool.config_show_tts_settings_summary)) ? 2 : 0;
    }
}
