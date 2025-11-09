package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.widget.SeekBarPreference;
/* loaded from: classes.dex */
public class ReduceBrightColorsIntensityPreferenceController extends SliderPreferenceController {
    private static final int INVERSE_PERCENTAGE_BASE = 100;
    private final ColorDisplayManager mColorDisplayManager;

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ReduceBrightColorsIntensityPreferenceController(Context context, String str) {
        super(context, str);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (ColorDisplayManager.isReduceBrightColorsAvailable(this.mContext)) {
            return !this.mColorDisplayManager.isReduceBrightColorsActivated() ? 5 : 0;
        }
        return 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SeekBarPreference seekBarPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        seekBarPreference.setContinuousUpdates(true);
        seekBarPreference.setMax(getMax());
        seekBarPreference.setMin(getMin());
        seekBarPreference.setHapticFeedbackMode(2);
        updateState(seekBarPreference);
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public final void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(this.mColorDisplayManager.isReduceBrightColorsActivated());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        return 100 - this.mColorDisplayManager.getReduceBrightColorsStrength();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        return this.mColorDisplayManager.setReduceBrightColorsStrength(100 - i);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        return 100 - ColorDisplayManager.getMinimumReduceBrightColorsStrength(this.mContext);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMin() {
        return 100 - ColorDisplayManager.getMaximumReduceBrightColorsStrength(this.mContext);
    }
}
