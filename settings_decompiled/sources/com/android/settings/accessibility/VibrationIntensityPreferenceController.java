package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$integer;
import com.android.settings.accessibility.VibrationPreferenceConfig;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public abstract class VibrationIntensityPreferenceController extends SliderPreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final int mMaxIntensity;
    protected final VibrationPreferenceConfig mPreferenceConfig;
    private final VibrationPreferenceConfig.SettingObserver mSettingsContentObserver;

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMin() {
        return 0;
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

    /* JADX INFO: Access modifiers changed from: protected */
    public VibrationIntensityPreferenceController(Context context, String str, VibrationPreferenceConfig vibrationPreferenceConfig) {
        this(context, str, vibrationPreferenceConfig, context.getResources().getInteger(R$integer.config_vibration_supported_intensity_levels));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VibrationIntensityPreferenceController(Context context, String str, VibrationPreferenceConfig vibrationPreferenceConfig, int i) {
        super(context, str);
        this.mPreferenceConfig = vibrationPreferenceConfig;
        this.mSettingsContentObserver = new VibrationPreferenceConfig.SettingObserver(vibrationPreferenceConfig);
        this.mMaxIntensity = Math.min(3, i);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mSettingsContentObserver.register(this.mContext);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mSettingsContentObserver.unregister(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SeekBarPreference seekBarPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mSettingsContentObserver.onDisplayPreference(this, seekBarPreference);
        seekBarPreference.setEnabled(this.mPreferenceConfig.isPreferenceEnabled());
        seekBarPreference.setSummaryProvider(new Preference.SummaryProvider() { // from class: com.android.settings.accessibility.VibrationIntensityPreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.SummaryProvider
            public final CharSequence provideSummary(Preference preference) {
                CharSequence lambda$displayPreference$0;
                lambda$displayPreference$0 = VibrationIntensityPreferenceController.this.lambda$displayPreference$0(preference);
                return lambda$displayPreference$0;
            }
        });
        seekBarPreference.setMin(getMin());
        seekBarPreference.setMax(getMax());
        seekBarPreference.setContinuousUpdates(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CharSequence lambda$displayPreference$0(Preference preference) {
        return this.mPreferenceConfig.getSummary();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setEnabled(this.mPreferenceConfig.isPreferenceEnabled());
        }
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        return this.mMaxIntensity;
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        if (!this.mPreferenceConfig.isPreferenceEnabled()) {
            return getMin();
        }
        return Math.min(this.mPreferenceConfig.readIntensity(), getMax());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        if (this.mPreferenceConfig.isPreferenceEnabled()) {
            boolean updateIntensity = this.mPreferenceConfig.updateIntensity(calculateVibrationIntensity(i));
            if (updateIntensity && i != 0) {
                this.mPreferenceConfig.playVibrationPreview();
            }
            return updateIntensity;
        }
        return false;
    }

    private int calculateVibrationIntensity(int i) {
        int max = getMax();
        if (i >= max) {
            if (max == 1) {
                return this.mPreferenceConfig.getDefaultIntensity();
            }
            return 3;
        }
        return i;
    }
}
