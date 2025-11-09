package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.accessibility.TextReadingResetController;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.core.instrumentation.SettingsStatsLog;
/* loaded from: classes.dex */
public class HighTextContrastPreferenceController extends TogglePreferenceController implements TextReadingResetController.ResetStateListener {
    private int mEntryPoint;
    private SwitchPreference mSwitchPreference;

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

    public HighTextContrastPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "high_text_contrast_enabled", 0) == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        SettingsStatsLog.write(454, AccessibilityStatsLogUtils.convertToItemKeyName(getPreferenceKey()), z ? 1 : 0, AccessibilityStatsLogUtils.convertToEntryPoint(this.mEntryPoint));
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "high_text_contrast_enabled", z ? 1 : 0);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.accessibility.TextReadingResetController.ResetStateListener
    public void resetState() {
        setChecked(false);
        updateState(this.mSwitchPreference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEntryPoint(int i) {
        this.mEntryPoint = i;
    }
}
