package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.settings.R$string;
import com.android.settings.accessibility.TextReadingResetController;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.core.instrumentation.SettingsStatsLog;
/* loaded from: classes.dex */
public class FontWeightAdjustmentPreferenceController extends TogglePreferenceController implements TextReadingResetController.ResetStateListener {
    static final int BOLD_TEXT_ADJUSTMENT = 300;
    private int mEntryPoint;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public FontWeightAdjustmentPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "font_weight_adjustment", 0) == BOLD_TEXT_ADJUSTMENT;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        SettingsStatsLog.write(454, AccessibilityStatsLogUtils.convertToItemKeyName(getPreferenceKey()), z ? 1 : 0, AccessibilityStatsLogUtils.convertToEntryPoint(this.mEntryPoint));
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "font_weight_adjustment", z ? BOLD_TEXT_ADJUSTMENT : 0);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    @Override // com.android.settings.accessibility.TextReadingResetController.ResetStateListener
    public void resetState() {
        setChecked(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEntryPoint(int i) {
        this.mEntryPoint = i;
    }
}
