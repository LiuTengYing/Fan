package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.ListFormatter;
import android.text.TextUtils;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class TopLevelSmartControlsPreferenceController extends BasePreferenceController {
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

    public TopLevelSmartControlsPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Utils.isSupportSmartControl(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = this.mContext.getString(R$string.smart_wake);
        String string2 = this.mContext.getString(R$string.ambient_display_pickup_title);
        ArrayList arrayList = new ArrayList();
        if (SmartWakePreferenceController.isSmartWakeAvailable(this.mContext) && SmartWakePreferenceController.isWakeGestureAvailable(this.mContext) && !TextUtils.isEmpty(string)) {
            arrayList.add(string);
        }
        if (SmartPickUpPreferenceController.isSmartPickUpAvailable(this.mContext) && !TextUtils.isEmpty(string2)) {
            arrayList.add(string2.toLowerCase());
        }
        return ListFormatter.getInstance().format(arrayList);
    }
}
