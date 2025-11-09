package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.connecteddevice.PreviouslyConnectedDeviceDashboardFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class SavedBluetoothDeviceUpdater extends BluetoothDeviceUpdater implements Preference.OnPreferenceClickListener {
    private static final boolean DBG = Log.isLoggable("SavedBluetoothDeviceUpdater", 3);
    BluetoothAdapter mBluetoothAdapter;
    private final boolean mDisplayConnected;

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    protected String getPreferenceKey() {
        return "saved_bt";
    }

    public SavedBluetoothDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
        this.mDisplayConnected = dashboardFragment instanceof PreviouslyConnectedDeviceDashboardFragment;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public void forceUpdate() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            CachedBluetoothDeviceManager cachedDeviceManager = this.mLocalManager.getCachedDeviceManager();
            List mostRecentlyConnectedDevices = this.mBluetoothAdapter.getMostRecentlyConnectedDevices();
            removePreferenceIfNecessary(mostRecentlyConnectedDevices, cachedDeviceManager);
            for (BluetoothDevice bluetoothDevice : mostRecentlyConnectedDevices) {
                CachedBluetoothDevice findDevice = cachedDeviceManager.findDevice(bluetoothDevice);
                if (findDevice != null && !cachedDeviceManager.isSubDevice(bluetoothDevice)) {
                    update(findDevice);
                }
            }
            return;
        }
        removeAllDevicesFromPreference();
    }

    private void removePreferenceIfNecessary(List<BluetoothDevice> list, CachedBluetoothDeviceManager cachedBluetoothDeviceManager) {
        CachedBluetoothDevice findDevice;
        Iterator it = new ArrayList(this.mPreferenceMap.keySet()).iterator();
        while (it.hasNext()) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) it.next();
            if (!list.contains(bluetoothDevice) && (findDevice = cachedBluetoothDeviceManager.findDevice(bluetoothDevice)) != null) {
                removePreference(findDevice);
            }
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public void update(CachedBluetoothDevice cachedBluetoothDevice) {
        if (isFilterMatched(cachedBluetoothDevice)) {
            addPreference(cachedBluetoothDevice, 3);
        } else {
            removePreference(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public boolean isFilterMatched(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (DBG) {
            Log.d("SavedBluetoothDeviceUpdater", "isFilterMatched() device name : " + cachedBluetoothDevice.getName() + ", is connected : " + device.isConnected() + ", is profile connected : " + cachedBluetoothDevice.isConnected());
        }
        return device.getBondState() == 12 && (this.mDisplayConnected || !device.isConnected());
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, this.mFragment.getMetricsCategory());
        CachedBluetoothDevice bluetoothDevice = ((BluetoothDevicePreference) preference).getBluetoothDevice();
        if (bluetoothDevice.isConnected()) {
            return bluetoothDevice.setActive();
        }
        this.mMetricsFeatureProvider.action(this.mPrefContext, 867, new Pair[0]);
        bluetoothDevice.connect();
        return true;
    }
}
