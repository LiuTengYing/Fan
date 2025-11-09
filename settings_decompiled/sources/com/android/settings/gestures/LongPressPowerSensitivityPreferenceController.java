package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.widget.LabeledSeekBarPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class LongPressPowerSensitivityPreferenceController extends SliderPreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final ContentObserver mPowerButtonObserver;
    private LabeledSeekBarPreference mPreference;
    private final int[] mSensitivityValues;

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

    public LongPressPowerSensitivityPreferenceController(Context context, String str) {
        super(context, str);
        this.mPowerButtonObserver = new ContentObserver(Handler.getMain()) { // from class: com.android.settings.gestures.LongPressPowerSensitivityPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (LongPressPowerSensitivityPreferenceController.this.mPreference != null) {
                    LongPressPowerSensitivityPreferenceController longPressPowerSensitivityPreferenceController = LongPressPowerSensitivityPreferenceController.this;
                    longPressPowerSensitivityPreferenceController.updateState(longPressPowerSensitivityPreferenceController.mPreference);
                }
            }
        };
        this.mSensitivityValues = context.getResources().getIntArray(17236082);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("power_button_long_press"), false, this.mPowerButtonObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mPowerButtonObserver);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LabeledSeekBarPreference labeledSeekBarPreference = (LabeledSeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = labeledSeekBarPreference;
        if (labeledSeekBarPreference != null) {
            labeledSeekBarPreference.setContinuousUpdates(false);
            this.mPreference.setHapticFeedbackMode(1);
            this.mPreference.setMin(getMin());
            this.mPreference.setMax(getMax());
        }
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        LabeledSeekBarPreference labeledSeekBarPreference = (LabeledSeekBarPreference) preference;
        labeledSeekBarPreference.setEnabled(isAvailable() && PowerMenuSettingsUtils.isLongPressPowerForAssistEnabled(this.mContext));
        labeledSeekBarPreference.setProgress(getSliderPosition());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        int[] iArr = this.mSensitivityValues;
        if (iArr == null || iArr.length < 2) {
            return 3;
        }
        return !PowerMenuSettingsUtils.isLongPressPowerForAssistEnabled(this.mContext) ? 5 : 0;
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        int[] iArr = this.mSensitivityValues;
        if (iArr == null) {
            return 0;
        }
        return closestValueIndex(iArr, getCurrentSensitivityValue());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        int[] iArr = this.mSensitivityValues;
        if (iArr == null || i < 0 || i >= iArr.length) {
            return false;
        }
        return Settings.Global.putInt(this.mContext.getContentResolver(), "power_button_long_press_duration_ms", this.mSensitivityValues[i]);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        int[] iArr = this.mSensitivityValues;
        if (iArr == null || iArr.length == 0) {
            return 0;
        }
        return iArr.length - 1;
    }

    private int getCurrentSensitivityValue() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "power_button_long_press_duration_ms", this.mContext.getResources().getInteger(17694855));
    }

    private static int closestValueIndex(int[] iArr, int i) {
        int i2 = Integer.MAX_VALUE;
        int i3 = 0;
        for (int i4 = 0; i4 < iArr.length; i4++) {
            int abs = Math.abs(iArr[i4] - i);
            if (abs < i2) {
                i3 = i4;
                i2 = abs;
            }
        }
        return i3;
    }
}
