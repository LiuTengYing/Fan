package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.accessibility.VibrationPreferenceConfig;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public abstract class VibrationTogglePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    protected final VibrationPreferenceConfig mPreferenceConfig;
    private final VibrationPreferenceConfig.SettingObserver mSettingsContentObserver;

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

    /* JADX INFO: Access modifiers changed from: protected */
    public VibrationTogglePreferenceController(Context context, String str, VibrationPreferenceConfig vibrationPreferenceConfig) {
        super(context, str);
        this.mPreferenceConfig = vibrationPreferenceConfig;
        this.mSettingsContentObserver = new VibrationPreferenceConfig.SettingObserver(vibrationPreferenceConfig);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mSettingsContentObserver.register(this.mContext);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mSettingsContentObserver.unregister(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mSettingsContentObserver.onDisplayPreference(this, findPreference);
        findPreference.setEnabled(this.mPreferenceConfig.isPreferenceEnabled());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setEnabled(this.mPreferenceConfig.isPreferenceEnabled());
            preference.setSummary(this.mPreferenceConfig.getSummary());
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mPreferenceConfig.isPreferenceEnabled() && this.mPreferenceConfig.readIntensity() != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (this.mPreferenceConfig.isPreferenceEnabled()) {
            boolean updateIntensity = this.mPreferenceConfig.updateIntensity(z ? this.mPreferenceConfig.getDefaultIntensity() : 0);
            if (updateIntensity && z) {
                this.mPreferenceConfig.playVibrationPreview();
            }
            return updateIntensity;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }
}
