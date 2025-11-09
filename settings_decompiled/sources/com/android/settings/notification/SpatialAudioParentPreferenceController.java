package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Spatializer;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class SpatialAudioParentPreferenceController extends BasePreferenceController {
    private SpatialAudioPreferenceController mSpatialAudioPreferenceController;
    private SpatialAudioWiredHeadphonesController mSpatialAudioWiredHeadphonesController;
    private final Spatializer mSpatializer;
    private static final String TAG = "SpatialAudioSetting";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SpatialAudioParentPreferenceController(Context context, String str) {
        super(context, str);
        this.mSpatializer = ((AudioManager) context.getSystemService(AudioManager.class)).getSpatializer();
        this.mSpatialAudioPreferenceController = new SpatialAudioPreferenceController(context, "unused");
        this.mSpatialAudioWiredHeadphonesController = new SpatialAudioWiredHeadphonesController(context, "unused");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        int immersiveAudioLevel = this.mSpatializer.getImmersiveAudioLevel();
        if (DEBUG) {
            Log.d(TAG, "spatialization level: " + immersiveAudioLevel);
        }
        return immersiveAudioLevel == 0 ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        boolean z = this.mSpatialAudioPreferenceController.isAvailable() && this.mSpatialAudioPreferenceController.isChecked();
        boolean z2 = this.mSpatialAudioWiredHeadphonesController.isAvailable() && this.mSpatialAudioWiredHeadphonesController.isChecked();
        if (z && z2) {
            Context context = this.mContext;
            return context.getString(R$string.spatial_summary_on_two, context.getString(R$string.spatial_audio_speaker), this.mContext.getString(R$string.spatial_audio_wired_headphones));
        } else if (z) {
            Context context2 = this.mContext;
            return context2.getString(R$string.spatial_summary_on_one, context2.getString(R$string.spatial_audio_speaker));
        } else if (z2) {
            Context context3 = this.mContext;
            return context3.getString(R$string.spatial_summary_on_one, context3.getString(R$string.spatial_audio_wired_headphones));
        } else {
            return this.mContext.getString(R$string.spatial_summary_off);
        }
    }
}
