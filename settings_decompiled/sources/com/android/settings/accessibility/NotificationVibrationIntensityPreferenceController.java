package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
/* loaded from: classes.dex */
public class NotificationVibrationIntensityPreferenceController extends VibrationIntensityPreferenceController {
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
    public static final class NotificationVibrationPreferenceConfig extends VibrationPreferenceConfig {
        @Override // com.android.settings.accessibility.VibrationPreferenceConfig
        public boolean isRestrictedByRingerModeSilent() {
            return true;
        }

        public NotificationVibrationPreferenceConfig(Context context) {
            super(context, "notification_vibration_intensity", 49);
        }
    }

    public NotificationVibrationIntensityPreferenceController(Context context, String str) {
        super(context, str, new NotificationVibrationPreferenceConfig(context));
    }

    protected NotificationVibrationIntensityPreferenceController(Context context, String str, int i) {
        super(context, str, new NotificationVibrationPreferenceConfig(context), i);
    }
}
