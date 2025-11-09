package com.android.settingslib.media;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
/* loaded from: classes2.dex */
public class BluetoothMediaDevice extends MediaDevice {
    private final AudioManager mAudioManager;
    private CachedBluetoothDevice mCachedDevice;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothMediaDevice(Context context, CachedBluetoothDevice cachedBluetoothDevice, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        super(context, mediaRouter2Manager, mediaRoute2Info, str);
        this.mCachedDevice = cachedBluetoothDevice;
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
        initDeviceRecord();
    }

    @Override // com.android.settingslib.media.MediaDevice
    public String getName() {
        return this.mCachedDevice.getName();
    }

    @Override // com.android.settingslib.media.MediaDevice
    public String getId() {
        return MediaDeviceUtils.getId(this.mCachedDevice);
    }

    public CachedBluetoothDevice getCachedDevice() {
        return this.mCachedDevice;
    }

    @Override // com.android.settingslib.media.MediaDevice
    protected boolean isCarKitDevice() {
        BluetoothClass bluetoothClass = this.mCachedDevice.getDevice().getBluetoothClass();
        if (bluetoothClass != null) {
            int deviceClass = bluetoothClass.getDeviceClass();
            return deviceClass == 1032 || deviceClass == 1056;
        }
        return false;
    }

    @Override // com.android.settingslib.media.MediaDevice
    public boolean isFastPairDevice() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        return cachedBluetoothDevice != null && BluetoothUtils.getBooleanMetaData(cachedBluetoothDevice.getDevice(), 6);
    }

    @Override // com.android.settingslib.media.MediaDevice
    public boolean isMutingExpectedDevice() {
        return this.mAudioManager.getMutingExpectedDevice() != null && this.mCachedDevice.getAddress().equals(this.mAudioManager.getMutingExpectedDevice().getAddress());
    }

    @Override // com.android.settingslib.media.MediaDevice
    public boolean isConnected() {
        return this.mCachedDevice.getBondState() == 12 && this.mCachedDevice.isConnected();
    }
}
