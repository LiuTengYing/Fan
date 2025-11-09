package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.net.Uri;
import java.util.List;
/* loaded from: classes.dex */
public interface BluetoothFeatureProvider {
    String getBluetoothDeviceControlUri(BluetoothDevice bluetoothDevice);

    Uri getBluetoothDeviceSettingsUri(BluetoothDevice bluetoothDevice);

    List<ComponentName> getRelatedTools();
}
