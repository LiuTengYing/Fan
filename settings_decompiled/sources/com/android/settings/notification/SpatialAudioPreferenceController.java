package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioDeviceAttributes;
import android.media.AudioManager;
import android.media.Spatializer;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
/* loaded from: classes.dex */
public class SpatialAudioPreferenceController extends TogglePreferenceController {
    private final Spatializer mSpatializer;
    final AudioDeviceAttributes mSpeaker;

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

    public SpatialAudioPreferenceController(Context context, String str) {
        super(context, str);
        this.mSpeaker = new AudioDeviceAttributes(2, 2, "");
        this.mSpatializer = ((AudioManager) context.getSystemService(AudioManager.class)).getSpatializer();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mSpatializer.isAvailableForDevice(this.mSpeaker) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mSpatializer.getCompatibleAudioDevices().contains(this.mSpeaker);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            this.mSpatializer.addCompatibleAudioDevice(this.mSpeaker);
        } else {
            this.mSpatializer.removeCompatibleAudioDevice(this.mSpeaker);
        }
        return z == isChecked();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_sound;
    }
}
