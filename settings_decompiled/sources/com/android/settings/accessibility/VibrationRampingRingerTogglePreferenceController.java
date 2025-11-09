package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.accessibility.VibrationPreferenceConfig;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class VibrationRampingRingerTogglePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final AudioManager mAudioManager;
    private final DeviceConfigProvider mDeviceConfigProvider;
    private Preference mPreference;
    private final VibrationPreferenceConfig.SettingObserver mRingSettingObserver;
    private final VibrationPreferenceConfig mRingVibrationPreferenceConfig;
    private final ContentObserver mSettingObserver;

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

    /* loaded from: classes.dex */
    protected static class DeviceConfigProvider {
        protected DeviceConfigProvider() {
        }

        public boolean isRampingRingerEnabledOnTelephonyConfig() {
            return DeviceConfig.getBoolean("telephony", "ramping_ringer_enabled", false);
        }
    }

    public VibrationRampingRingerTogglePreferenceController(Context context, String str) {
        this(context, str, new DeviceConfigProvider());
    }

    protected VibrationRampingRingerTogglePreferenceController(Context context, String str, DeviceConfigProvider deviceConfigProvider) {
        super(context, str);
        this.mDeviceConfigProvider = deviceConfigProvider;
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
        RingVibrationPreferenceConfig ringVibrationPreferenceConfig = new RingVibrationPreferenceConfig(context);
        this.mRingVibrationPreferenceConfig = ringVibrationPreferenceConfig;
        this.mRingSettingObserver = new VibrationPreferenceConfig.SettingObserver(ringVibrationPreferenceConfig);
        this.mSettingObserver = new ContentObserver(new Handler(true)) { // from class: com.android.settings.accessibility.VibrationRampingRingerTogglePreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                VibrationRampingRingerTogglePreferenceController vibrationRampingRingerTogglePreferenceController = VibrationRampingRingerTogglePreferenceController.this;
                vibrationRampingRingerTogglePreferenceController.updateState(vibrationRampingRingerTogglePreferenceController.mPreference);
            }
        };
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!Utils.isVoiceCapable(this.mContext) || this.mDeviceConfigProvider.isRampingRingerEnabledOnTelephonyConfig()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mRingSettingObserver.register(this.mContext);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("apply_ramping_ringer"), false, this.mSettingObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mRingSettingObserver.unregister(this.mContext);
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingObserver);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        this.mRingSettingObserver.onDisplayPreference(this, findPreference);
        this.mPreference.setEnabled(isRingVibrationEnabled());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isRingVibrationEnabled() && this.mAudioManager.isRampingRingerEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (isRingVibrationEnabled()) {
            this.mAudioManager.setRampingRingerEnabled(z);
            if (z) {
                this.mRingVibrationPreferenceConfig.playVibrationPreview();
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setEnabled(isRingVibrationEnabled());
        }
    }

    private boolean isRingVibrationEnabled() {
        return this.mRingVibrationPreferenceConfig.isPreferenceEnabled() && this.mRingVibrationPreferenceConfig.readIntensity() != 0;
    }
}
