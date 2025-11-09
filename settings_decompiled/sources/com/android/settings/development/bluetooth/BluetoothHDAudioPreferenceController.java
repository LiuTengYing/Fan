package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class BluetoothHDAudioPreferenceController extends AbstractBluetoothPreferenceController implements Preference.OnPreferenceChangeListener {
    private final AbstractBluetoothPreferenceController.Callback mCallback;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_hd_audio_settings";
    }

    public BluetoothHDAudioPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore, AbstractBluetoothPreferenceController.Callback callback) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
        this.mCallback = callback;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        if (bluetoothA2dp == null) {
            this.mPreference.setEnabled(false);
            return;
        }
        BluetoothDevice a2dpActiveDevice = getA2dpActiveDevice();
        if (a2dpActiveDevice == null) {
            Log.e("BtHDAudioCtr", "Active device is null. To disable HD audio button");
            this.mPreference.setEnabled(false);
            return;
        }
        boolean z = bluetoothA2dp.isOptionalCodecsSupported(a2dpActiveDevice) == 1;
        this.mPreference.setEnabled(z);
        if (z) {
            ((SwitchPreference) this.mPreference).setChecked(bluetoothA2dp.isOptionalCodecsEnabled(a2dpActiveDevice) == 1);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        if (bluetoothA2dp == null) {
            this.mPreference.setEnabled(false);
            return true;
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        BluetoothDevice a2dpActiveDevice = getA2dpActiveDevice();
        if (a2dpActiveDevice == null) {
            this.mPreference.setEnabled(false);
            return true;
        }
        bluetoothA2dp.setOptionalCodecsEnabled(a2dpActiveDevice, booleanValue ? 1 : 0);
        if (booleanValue) {
            bluetoothA2dp.enableOptionalCodecs(a2dpActiveDevice);
        } else {
            bluetoothA2dp.disableOptionalCodecs(a2dpActiveDevice);
        }
        this.mCallback.onBluetoothHDAudioEnabled(booleanValue);
        return true;
    }
}
