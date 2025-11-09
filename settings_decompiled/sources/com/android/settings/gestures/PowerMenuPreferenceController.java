package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class PowerMenuPreferenceController extends BasePreferenceController {
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

    public PowerMenuPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int powerButtonSettingValue = PowerMenuSettingsUtils.getPowerButtonSettingValue(this.mContext);
        if (powerButtonSettingValue == 5) {
            return this.mContext.getText(R$string.power_menu_summary_long_press_for_assist_enabled);
        }
        if (powerButtonSettingValue == 1) {
            return this.mContext.getText(R$string.power_menu_summary_long_press_for_assist_disabled_with_power_menu);
        }
        return this.mContext.getText(R$string.power_menu_summary_long_press_for_assist_disabled_no_action);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isAssistInvocationAvailable() ? 0 : 3;
    }

    private boolean isAssistInvocationAvailable() {
        return this.mContext.getResources().getBoolean(17891698);
    }
}
