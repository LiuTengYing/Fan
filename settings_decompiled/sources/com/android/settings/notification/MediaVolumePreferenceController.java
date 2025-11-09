package com.android.settings.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$bool;
import com.android.settings.R$drawable;
import com.android.settings.Utils;
import com.android.settings.media.MediaOutputIndicatorWorker;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.media.BluetoothMediaDevice;
import com.android.settingslib.media.MediaDevice;
/* loaded from: classes.dex */
public class MediaVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private static final String ACTION_LAUNCH_BROADCAST_DIALOG = "android.settings.MEDIA_BROADCAST_DIALOG";
    private static final String KEY_MEDIA_VOLUME = "media_volume";
    private static final String TAG = "MediaVolumePreCtrl";
    private MediaDevice mMediaDevice;
    private MediaOutputIndicatorWorker mWorker;

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getAudioStream() {
        return 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_MEDIA_VOLUME;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public MediaVolumePreferenceController(Context context) {
        super(context, KEY_MEDIA_VOLUME);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_media_volume) ? 0 : 3;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_MEDIA_VOLUME);
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMuteIcon() {
        return R$drawable.ic_media_stream_off;
    }

    private boolean isSupportEndItem() {
        return (getWorker() == null || getWorker().getActiveLocalMediaController() == null || !isConnectedBLEDevice()) ? false : true;
    }

    private boolean isConnectedBLEDevice() {
        MediaDevice currentConnectedMediaDevice = getWorker().getCurrentConnectedMediaDevice();
        this.mMediaDevice = currentConnectedMediaDevice;
        if (currentConnectedMediaDevice != null) {
            return currentConnectedMediaDevice.isBLEDevice();
        }
        return false;
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public SliceAction getSliceEndItem(Context context) {
        PendingIntent activity;
        if (!isSupportEndItem()) {
            Log.d(TAG, "The slice doesn't support end item");
            return null;
        }
        Intent intent = new Intent();
        if (getWorker().isDeviceBroadcasting()) {
            intent.setPackage("com.android.systemui");
            intent.setAction("com.android.systemui.action.LAUNCH_MEDIA_OUTPUT_BROADCAST_DIALOG");
            intent.putExtra("package_name", getWorker().getActiveLocalMediaController().getPackageName());
            activity = PendingIntent.getBroadcast(context, 0, intent, 201326592);
        } else {
            CachedBluetoothDevice cachedDevice = ((BluetoothMediaDevice) this.mMediaDevice).getCachedDevice();
            if (cachedDevice == null) {
                Log.d(TAG, "The bluetooth device is null");
                return null;
            }
            intent.setAction(ACTION_LAUNCH_BROADCAST_DIALOG);
            intent.putExtra("app_label", Utils.getApplicationLabel(this.mContext, getWorker().getPackageName()));
            intent.putExtra("device_address", cachedDevice.getAddress());
            activity = PendingIntent.getActivity(context, 0, intent, 201326592);
        }
        return SliceAction.createDeeplink(activity, getBroadcastIcon(context), 0, getPreferenceKey());
    }

    private IconCompat getBroadcastIcon(Context context) {
        Drawable drawable = context.getDrawable(com.android.settingslib.R$drawable.settings_input_antenna);
        if (drawable != null) {
            drawable.setTint(com.android.settingslib.Utils.getColorAccentDefaultColor(context));
            return Utils.createIconWithDrawable(drawable);
        }
        return null;
    }

    private MediaOutputIndicatorWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (MediaOutputIndicatorWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }

    private Uri getUri() {
        return CustomSliceRegistry.VOLUME_MEDIA_URI;
    }

    @Override // com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return MediaOutputIndicatorWorker.class;
    }
}
