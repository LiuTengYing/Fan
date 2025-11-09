package com.android.settings.accessibility;

import android.util.Log;
import androidx.fragment.app.FragmentManager;
import com.android.settings.bluetooth.HearingAidPairingDialogFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
/* loaded from: classes.dex */
public final class HearingAidUtils {
    public static void launchHearingAidPairingDialog(FragmentManager fragmentManager, CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice.isConnectedHearingAidDevice() && cachedBluetoothDevice.getDeviceMode() == 1 && cachedBluetoothDevice.getSubDevice() == null) {
            launchHearingAidPairingDialogInternal(fragmentManager, cachedBluetoothDevice);
        }
    }

    private static void launchHearingAidPairingDialogInternal(FragmentManager fragmentManager, CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice.getDeviceSide() == -1) {
            Log.w("HearingAidUtils", "Can not launch hearing aid pairing dialog for invalid side");
        } else {
            HearingAidPairingDialogFragment.newInstance(cachedBluetoothDevice).show(fragmentManager, "HearingAidPairingDialogFragment");
        }
    }
}
