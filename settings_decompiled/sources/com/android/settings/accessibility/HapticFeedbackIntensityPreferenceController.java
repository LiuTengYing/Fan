package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
/* loaded from: classes.dex */
public class HapticFeedbackIntensityPreferenceController extends VibrationIntensityPreferenceController {
    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.accessibility.VibrationIntensityPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* loaded from: classes.dex */
    public static final class HapticFeedbackVibrationPreferenceConfig extends VibrationPreferenceConfig {
        public HapticFeedbackVibrationPreferenceConfig(Context context) {
            super(context, "haptic_feedback_intensity", 18);
        }

        @Override // com.android.settings.accessibility.VibrationPreferenceConfig
        public int readIntensity() {
            if (Settings.System.getInt(this.mContentResolver, "haptic_feedback_enabled", 1) == 0) {
                return 0;
            }
            return super.readIntensity();
        }

        @Override // com.android.settings.accessibility.VibrationPreferenceConfig
        public boolean updateIntensity(int i) {
            boolean updateIntensity = super.updateIntensity(i);
            int i2 = i == 0 ? 1 : 0;
            Settings.System.putInt(this.mContentResolver, "haptic_feedback_enabled", i2 ^ 1);
            ContentResolver contentResolver = this.mContentResolver;
            if (i2 != 0) {
                i = getDefaultIntensity();
            }
            Settings.System.putInt(contentResolver, "hardware_haptic_feedback_intensity", i);
            return updateIntensity;
        }
    }

    public HapticFeedbackIntensityPreferenceController(Context context, String str) {
        super(context, str, new HapticFeedbackVibrationPreferenceConfig(context));
    }

    protected HapticFeedbackIntensityPreferenceController(Context context, String str, int i) {
        super(context, str, new HapticFeedbackVibrationPreferenceConfig(context), i);
    }
}
