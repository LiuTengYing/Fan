package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import com.android.settingslib.bluetooth.BluetoothUtils;
import java.util.List;
/* loaded from: classes.dex */
public class BluetoothFeatureProviderImpl implements BluetoothFeatureProvider {
    @Override // com.android.settings.bluetooth.BluetoothFeatureProvider
    public List<ComponentName> getRelatedTools() {
        return null;
    }

    public BluetoothFeatureProviderImpl(Context context) {
    }

    @Override // com.android.settings.bluetooth.BluetoothFeatureProvider
    public Uri getBluetoothDeviceSettingsUri(BluetoothDevice bluetoothDevice) {
        byte[] metadata = bluetoothDevice.getMetadata(16);
        if (metadata == null) {
            return null;
        }
        return Uri.parse(new String(metadata));
    }

    @Override // com.android.settings.bluetooth.BluetoothFeatureProvider
    public String getBluetoothDeviceControlUri(BluetoothDevice bluetoothDevice) {
        return BluetoothUtils.getControlUriMetaData(bluetoothDevice);
    }
}
