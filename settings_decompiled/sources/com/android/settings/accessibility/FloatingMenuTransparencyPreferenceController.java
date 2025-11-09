package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
/* loaded from: classes.dex */
public class FloatingMenuTransparencyPreferenceController extends SliderPreferenceController implements LifecycleObserver, OnResume, OnPause {
    static final float DEFAULT_TRANSPARENCY = 0.45f;
    private static final int FADE_ENABLED = 1;
    static final float MAXIMUM_TRANSPARENCY = 1.0f;
    private static final float MAX_PROGRESS = 90.0f;
    private static final float MIN_PROGRESS = 0.0f;
    static final float PRECISION = 100.0f;
    final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private SeekBarPreference mPreference;

    private float convertTransparencyIntToFloat(int i) {
        return i / PRECISION;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        return 90;
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

    public FloatingMenuTransparencyPreferenceController(Context context, String str) {
        super(context, str);
        this.mContentResolver = context.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.accessibility.FloatingMenuTransparencyPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                FloatingMenuTransparencyPreferenceController.this.updateAvailabilityStatus();
            }
        };
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return AccessibilityUtil.isFloatingMenuEnabled(this.mContext) ? 0 : 5;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SeekBarPreference seekBarPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = seekBarPreference;
        seekBarPreference.setContinuousUpdates(true);
        this.mPreference.setMax(getMax());
        this.mPreference.setMin(getMin());
        this.mPreference.setHapticFeedbackMode(2);
        updateAvailabilityStatus();
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_button_mode"), false, this.mContentObserver);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_floating_menu_fade_enabled"), false, this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        return convertTransparencyFloatToInt(getTransparency());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        return Settings.Secure.putFloat(this.mContentResolver, "accessibility_floating_menu_opacity", MAXIMUM_TRANSPARENCY - convertTransparencyIntToFloat(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAvailabilityStatus() {
        boolean z = true;
        boolean z2 = Settings.Secure.getInt(this.mContentResolver, "accessibility_floating_menu_fade_enabled", 1) == 1;
        SeekBarPreference seekBarPreference = this.mPreference;
        if (!AccessibilityUtil.isFloatingMenuEnabled(this.mContext) || !z2) {
            z = false;
        }
        seekBarPreference.setEnabled(z);
    }

    private int convertTransparencyFloatToInt(float f) {
        return Math.round(f * PRECISION);
    }

    private float getTransparency() {
        float f = MAXIMUM_TRANSPARENCY - Settings.Secure.getFloat(this.mContentResolver, "accessibility_floating_menu_opacity", DEFAULT_TRANSPARENCY);
        return (f < MIN_PROGRESS || f > 0.9f) ? DEFAULT_TRANSPARENCY : f;
    }
}
