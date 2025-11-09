package com.android.settings.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RoutingSessionInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class MediaDeviceUpdateWorker extends SliceBackgroundWorker implements LocalMediaManager.DeviceCallback {
    private static final boolean DEBUG = Log.isLoggable("MediaDeviceUpdateWorker", 3);
    protected final Context mContext;
    private boolean mIsTouched;
    LocalMediaManager mLocalMediaManager;
    protected final Collection<MediaDevice> mMediaDevices;
    private final String mPackageName;
    private final DevicesChangedBroadcastReceiver mReceiver;

    public MediaDeviceUpdateWorker(Context context, Uri uri) {
        super(context, uri);
        this.mMediaDevices = new CopyOnWriteArrayList();
        this.mContext = context;
        this.mPackageName = uri.getQueryParameter("media_package_name");
        this.mReceiver = new DevicesChangedBroadcastReceiver();
    }

    @Override // com.android.settings.slices.SliceBackgroundWorker
    protected void onSlicePinned() {
        this.mMediaDevices.clear();
        this.mIsTouched = false;
        LocalMediaManager localMediaManager = this.mLocalMediaManager;
        if (localMediaManager == null || !TextUtils.equals(this.mPackageName, localMediaManager.getPackageName())) {
            this.mLocalMediaManager = new LocalMediaManager(this.mContext, this.mPackageName, null);
        }
        this.mLocalMediaManager.registerCallback(this);
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.media.STREAM_DEVICES_CHANGED_ACTION"));
        this.mLocalMediaManager.startScan();
    }

    @Override // com.android.settings.slices.SliceBackgroundWorker
    protected void onSliceUnpinned() {
        this.mLocalMediaManager.unregisterCallback(this);
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mLocalMediaManager.stopScan();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mLocalMediaManager = null;
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceListUpdate(List<MediaDevice> list) {
        buildMediaDevices(list);
        notifySliceChange();
    }

    private void buildMediaDevices(List<MediaDevice> list) {
        this.mMediaDevices.clear();
        this.mMediaDevices.addAll(list);
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceAttributesChanged() {
        notifySliceChange();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onRequestFailed(int i) {
        notifySliceChange();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustSessionVolume(String str, int i) {
        this.mLocalMediaManager.adjustSessionVolume(str, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<RoutingSessionInfo> getActiveRemoteMediaDevice() {
        ArrayList arrayList = new ArrayList();
        for (RoutingSessionInfo routingSessionInfo : this.mLocalMediaManager.getActiveMediaSession()) {
            if (!routingSessionInfo.isSystemSession()) {
                if (DEBUG) {
                    Log.d("MediaDeviceUpdateWorker", "getActiveRemoteMediaDevice() info : " + routingSessionInfo.toString() + ", package name : " + routingSessionInfo.getClientPackageName());
                }
                arrayList.add(routingSessionInfo);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldDisableMediaOutput(String str) {
        return this.mLocalMediaManager.shouldDisableMediaOutput(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldEnableVolumeSeekBar(RoutingSessionInfo routingSessionInfo) {
        return this.mLocalMediaManager.shouldEnableVolumeSeekBar(routingSessionInfo);
    }

    /* loaded from: classes.dex */
    private class DevicesChangedBroadcastReceiver extends BroadcastReceiver {
        private DevicesChangedBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals("android.media.STREAM_DEVICES_CHANGED_ACTION", intent.getAction()) && Utils.isAudioModeOngoingCall(MediaDeviceUpdateWorker.this.mContext)) {
                MediaDeviceUpdateWorker.this.notifySliceChange();
            }
        }
    }
}
