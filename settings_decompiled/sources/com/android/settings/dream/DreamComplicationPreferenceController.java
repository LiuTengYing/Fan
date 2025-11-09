package com.android.settings.dream;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.dream.DreamBackend;
/* loaded from: classes.dex */
public class DreamComplicationPreferenceController extends TogglePreferenceController {
    private final DreamBackend mBackend;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DreamComplicationPreferenceController(Context context, String str) {
        super(context, str);
        this.mBackend = DreamBackend.getInstance(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mBackend.getSupportedComplications().isEmpty() ? 2 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mBackend.getEnabledComplications().containsAll(this.mBackend.getSupportedComplications());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        for (Integer num : this.mBackend.getSupportedComplications()) {
            this.mBackend.setComplicationEnabled(num.intValue(), z);
        }
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }
}
