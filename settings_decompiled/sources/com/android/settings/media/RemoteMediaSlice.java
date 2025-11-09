package com.android.settings.media;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRouter2Manager;
import android.media.RoutingSessionInfo;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.notification.SoundSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settings.slices.SliceBuilderUtils;
import java.util.List;
/* loaded from: classes.dex */
public class RemoteMediaSlice implements CustomSliceable {
    private final Context mContext;
    MediaRouter2Manager mRouterManager;
    private MediaDeviceUpdateWorker mWorker;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public RemoteMediaSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        int intExtra = intent.getIntExtra("android.app.slice.extra.RANGE_VALUE", -1);
        String stringExtra = intent.getStringExtra("media_id");
        if (!TextUtils.isEmpty(stringExtra)) {
            getWorker().adjustSessionVolume(stringExtra, intExtra);
        } else if (TextUtils.equals("action_launch_dialog", intent.getStringExtra("customized_action"))) {
            this.mContext.sendBroadcast(new Intent().setPackage("com.android.systemui").setAction("com.android.systemui.action.LAUNCH_MEDIA_OUTPUT_DIALOG").putExtra("package_name", ((RoutingSessionInfo) intent.getParcelableExtra("RoutingSessionInfo")).getClientPackageName()));
            this.mContext.sendBroadcast(new Intent().setPackage("com.android.settings").setAction("com.android.settings.panel.action.CLOSE_PANEL"));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        ListBuilder accentColor = new ListBuilder(this.mContext, getUri(), -1L).setAccentColor(-1);
        if (getWorker() == null) {
            Log.e("RemoteMediaSlice", "Unable to get the slice worker.");
            return accentColor.build();
        }
        if (this.mRouterManager == null) {
            this.mRouterManager = MediaRouter2Manager.getInstance(this.mContext);
        }
        List<RoutingSessionInfo> activeRemoteMediaDevice = getWorker().getActiveRemoteMediaDevice();
        if (activeRemoteMediaDevice.isEmpty()) {
            Log.d("RemoteMediaSlice", "No active remote media device");
            return accentColor.build();
        }
        CharSequence text = this.mContext.getText(R$string.remote_media_volume_option_title);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.ic_volume_remote);
        IconCompat createEmptyIcon = createEmptyIcon();
        for (RoutingSessionInfo routingSessionInfo : activeRemoteMediaDevice) {
            int volumeMax = routingSessionInfo.getVolumeMax();
            if (volumeMax <= 0) {
                Log.d("RemoteMediaSlice", "Unable to add Slice. " + ((Object) routingSessionInfo.getName()) + ": max volume is " + volumeMax);
            } else if (getWorker().shouldEnableVolumeSeekBar(routingSessionInfo)) {
                CharSequence applicationLabel = Utils.getApplicationLabel(this.mContext, routingSessionInfo.getClientPackageName());
                String string = this.mContext.getString(R$string.media_output_label_title, applicationLabel);
                accentColor.addInputRange(new ListBuilder.InputRangeBuilder().setTitleItem(createWithResource, 0).setTitle(text).setInputAction(getSliderInputAction(routingSessionInfo.getId().hashCode(), routingSessionInfo.getId())).setPrimaryAction(getSoundSettingAction(text, createWithResource, routingSessionInfo.getId())).setMax(volumeMax).setValue(routingSessionInfo.getVolume()));
                boolean shouldDisableMediaOutput = getWorker().shouldDisableMediaOutput(routingSessionInfo.getClientPackageName());
                if (TextUtils.isEmpty(applicationLabel)) {
                    applicationLabel = "";
                }
                SpannableString spannableString = new SpannableString(applicationLabel);
                spannableString.setSpan(new ForegroundColorSpan(com.android.settingslib.Utils.getColorAttrDefaultColor(this.mContext, 16842808)), 0, spannableString.length(), 33);
                ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
                if (shouldDisableMediaOutput) {
                    string = spannableString;
                }
                accentColor.addRow(rowBuilder.setTitle(string).setSubtitle(routingSessionInfo.getName()).setTitleItem(createEmptyIcon, 0).setPrimaryAction(getMediaOutputDialogAction(routingSessionInfo, shouldDisableMediaOutput)));
            } else {
                Log.d("RemoteMediaSlice", "Unable to add Slice. " + ((Object) routingSessionInfo.getName()) + ": This is a group session");
            }
        }
        return accentColor.build();
    }

    private IconCompat createEmptyIcon() {
        return IconCompat.createWithBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
    }

    private PendingIntent getSliderInputAction(int i, String str) {
        return PendingIntent.getBroadcast(this.mContext, i, new Intent(getUri().toString()).setData(getUri()).putExtra("media_id", str).setClass(this.mContext, SliceBroadcastReceiver.class), 33554432);
    }

    private SliceAction getSoundSettingAction(CharSequence charSequence, IconCompat iconCompat, String str) {
        Uri build = new Uri.Builder().appendPath(str).build();
        Intent buildSearchResultPageIntent = SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, SoundSettings.class.getName(), str, this.mContext.getText(R$string.sound_settings).toString(), 0, R$string.menu_key_sound);
        buildSearchResultPageIntent.setClassName(this.mContext.getPackageName(), SubSettings.class.getName());
        buildSearchResultPageIntent.setData(build);
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, 0, buildSearchResultPageIntent, 67108864), iconCompat, 0, charSequence);
    }

    private SliceAction getMediaOutputDialogAction(RoutingSessionInfo routingSessionInfo, boolean z) {
        PendingIntent broadcast = PendingIntent.getBroadcast(this.mContext, routingSessionInfo.hashCode(), new Intent(getUri().toString()).setData(getUri()).setClass(this.mContext, SliceBroadcastReceiver.class).putExtra("customized_action", z ? "" : "action_launch_dialog").putExtra("RoutingSessionInfo", routingSessionInfo).addFlags(268435456), 201326592);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, R$drawable.ic_volume_remote);
        Context context = this.mContext;
        return SliceAction.createDeeplink(broadcast, createWithResource, 0, context.getString(R$string.media_output_label_title, Utils.getApplicationLabel(context, routingSessionInfo.getClientPackageName())));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.REMOTE_MEDIA_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_connected_devices;
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return MediaDeviceUpdateWorker.class;
    }

    private MediaDeviceUpdateWorker getWorker() {
        if (this.mWorker == null) {
            this.mWorker = (MediaDeviceUpdateWorker) SliceBackgroundWorker.getInstance(getUri());
        }
        return this.mWorker;
    }
}
