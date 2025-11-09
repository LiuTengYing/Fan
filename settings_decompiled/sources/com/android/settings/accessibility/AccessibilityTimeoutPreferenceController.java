package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R$array;
import com.android.settings.core.BasePreferenceController;
import com.google.common.primitives.Ints;
/* loaded from: classes.dex */
public class AccessibilityTimeoutPreferenceController extends BasePreferenceController {
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

    public AccessibilityTimeoutPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String[] stringArray = this.mContext.getResources().getStringArray(R$array.accessibility_timeout_summaries);
        int indexOf = Ints.indexOf(this.mContext.getResources().getIntArray(R$array.accessibility_timeout_selector_values), AccessibilityTimeoutController.getSecureAccessibilityTimeoutValue(this.mContext.getContentResolver(), "accessibility_interactive_ui_timeout_ms"));
        if (indexOf == -1) {
            indexOf = 0;
        }
        return stringArray[indexOf];
    }
}
