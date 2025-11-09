package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class BluetoothPairingDetail extends DeviceListPreferenceFragment {
    static final String KEY_AVAIL_DEVICES = "available_devices";
    static final String KEY_FOOTER_PREF = "footer_preference";
    AlwaysDiscoverable mAlwaysDiscoverable;
    BluetoothProgressCategory mAvailableDevicesCategory;
    FooterPreference mFooterPreference;
    private boolean mInitialScanStarted;

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public String getDeviceListKey() {
        return KEY_AVAIL_DEVICES;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothPairingDetail";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1018;
    }

    public BluetoothPairingDetail() {
        super("no_config_bluetooth");
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mInitialScanStarted = false;
        this.mAlwaysDiscoverable = new AlwaysDiscoverable(getContext());
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager == null) {
            Log.e("BluetoothPairingDetail", "Bluetooth is not supported on this device");
            return;
        }
        updateBluetooth();
        this.mAvailableDevicesCategory.setProgress(this.mBluetoothAdapter.isDiscovering());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((BluetoothDeviceRenamePreferenceController) use(BluetoothDeviceRenamePreferenceController.class)).setFragment(this);
    }

    void updateBluetooth() {
        if (this.mBluetoothAdapter.isEnabled()) {
            updateContent(this.mBluetoothAdapter.getState());
        } else {
            this.mBluetoothAdapter.enable();
        }
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mLocalManager == null) {
            Log.e("BluetoothPairingDetail", "Bluetooth is not supported on this device");
            return;
        }
        this.mAlwaysDiscoverable.stop();
        disableScanning();
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    void initPreferencesFromPreferenceScreen() {
        this.mAvailableDevicesCategory = (BluetoothProgressCategory) findPreference(KEY_AVAIL_DEVICES);
        FooterPreference footerPreference = (FooterPreference) findPreference(KEY_FOOTER_PREF);
        this.mFooterPreference = footerPreference;
        footerPreference.setSelectable(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void enableScanning() {
        if (!this.mInitialScanStarted) {
            if (this.mAvailableDevicesCategory != null) {
                removeAllDevices();
            }
            this.mLocalManager.getCachedDeviceManager().clearNonBondedDevices();
            this.mInitialScanStarted = true;
        }
        super.enableScanning();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        disableScanning();
        super.onDevicePreferenceClick(bluetoothDevicePreference);
    }

    @Override // com.android.settings.bluetooth.DeviceListPreferenceFragment, com.android.settingslib.bluetooth.BluetoothCallback
    public void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
        this.mAvailableDevicesCategory.setProgress(z | this.mScanEnabled);
    }

    void updateContent(int i) {
        if (i == 10) {
            finish();
        } else if (i != 12) {
        } else {
            this.mDevicePreferenceMap.clear();
            this.mBluetoothAdapter.enable();
            addDeviceCategory(this.mAvailableDevicesCategory, R$string.bluetooth_preference_found_media_devices, BluetoothDeviceFilter.ALL_FILTER, this.mInitialScanStarted);
            updateFooterPreference(this.mFooterPreference);
            this.mAlwaysDiscoverable.start();
            enableScanning();
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
        updateContent(i);
        if (i == 12) {
            showBluetoothTurnedOnToast();
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (i == 12) {
            finish();
        } else if (this.mSelectedDevice == null || cachedBluetoothDevice == null) {
        } else {
            BluetoothDevice device = cachedBluetoothDevice.getDevice();
            if (device != null && this.mSelectedDevice.equals(device) && i == 10) {
                if (!this.mBluetoothAdapter.isDiscovering()) {
                    this.mScanEnabled = false;
                }
                this.mPairingBond = false;
                enableScanning();
            } else if (device == null || i != 10) {
            } else {
                this.mPairingBond = false;
            }
        }
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        if (cachedBluetoothDevice == null || !cachedBluetoothDevice.isConnected()) {
            return;
        }
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (device != null && this.mSelectedList.contains(device)) {
            finish();
        } else if (this.mDevicePreferenceMap.containsKey(cachedBluetoothDevice)) {
            onDeviceDeleted(cachedBluetoothDevice);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_bluetooth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.bluetooth_pairing_detail;
    }

    void showBluetoothTurnedOnToast() {
        Toast.makeText(getContext(), R$string.connected_device_bluetooth_turned_on_toast, 0).show();
    }
}
