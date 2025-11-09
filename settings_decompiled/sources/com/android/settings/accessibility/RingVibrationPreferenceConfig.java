package com.android.settings.accessibility;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
/* loaded from: classes.dex */
public class RingVibrationPreferenceConfig extends VibrationPreferenceConfig {
    private final AudioManager mAudioManager;

    @Override // com.android.settings.accessibility.VibrationPreferenceConfig
    public boolean isRestrictedByRingerModeSilent() {
        return true;
    }

    public RingVibrationPreferenceConfig(Context context) {
        super(context, "ring_vibration_intensity", 33);
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
    }

    @Override // com.android.settings.accessibility.VibrationPreferenceConfig
    public boolean updateIntensity(int i) {
        boolean updateIntensity = super.updateIntensity(i);
        Settings.System.putInt(this.mContentResolver, "vibrate_when_ringing", i == 0 ? 0 : 1);
        return updateIntensity;
    }
}
