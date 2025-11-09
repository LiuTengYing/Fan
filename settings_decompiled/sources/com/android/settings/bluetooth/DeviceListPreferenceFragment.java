package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.BidiFormatter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$string;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.BluetoothDeviceFilter;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public abstract class DeviceListPreferenceFragment extends RestrictedDashboardFragment implements BluetoothCallback {
    private final Runnable mAddCachedDevicesRunnable;
    BluetoothAdapter mBluetoothAdapter;
    PreferenceGroup mDeviceListGroup;
    final HashMap<CachedBluetoothDevice, BluetoothDevicePreference> mDevicePreferenceMap;
    private BluetoothDeviceFilter.Filter mFilter;
    LocalBluetoothManager mLocalManager;
    boolean mPairingBond;
    boolean mScanEnabled;
    BluetoothDevice mSelectedDevice;
    final List<BluetoothDevice> mSelectedList;
    boolean mShowDevicesWithoutNames;

    public abstract String getDeviceListKey();

    /* JADX INFO: Access modifiers changed from: protected */
    public void initDevicePreference(BluetoothDevicePreference bluetoothDevicePreference) {
    }

    abstract void initPreferencesFromPreferenceScreen();

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy()) {
            onDeviceAdded(cachedBluetoothDevice);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeviceListPreferenceFragment(String str) {
        super(str);
        this.mDevicePreferenceMap = new HashMap<>();
        this.mSelectedList = new ArrayList();
        this.mAddCachedDevicesRunnable = new Runnable() { // from class: com.android.settings.bluetooth.DeviceListPreferenceFragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DeviceListPreferenceFragment.this.lambda$new$0();
            }
        };
        this.mFilter = BluetoothDeviceFilter.ALL_FILTER;
    }

    final void setFilter(BluetoothDeviceFilter.Filter filter) {
        this.mFilter = filter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setFilter(int i) {
        this.mFilter = BluetoothDeviceFilter.getFilter(i);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("DeviceListPreferenceFragment", "Bluetooth is not supported on this device");
            return;
        }
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mShowDevicesWithoutNames = SystemProperties.getBoolean("persist.bluetooth.showdeviceswithoutnames", false);
        initPreferencesFromPreferenceScreen();
        this.mDeviceListGroup = (PreferenceCategory) findPreference(getDeviceListKey());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        this.mLocalManager.setForegroundActivity(getActivity());
        this.mLocalManager.getEventManager().registerCallback(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mLocalManager == null || isUiRestricted()) {
            return;
        }
        removeAllDevices();
        this.mLocalManager.setForegroundActivity(null);
        this.mLocalManager.getEventManager().unregisterCallback(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAllDevices() {
        getView().removeCallbacks(this.mAddCachedDevicesRunnable);
        this.mDevicePreferenceMap.clear();
        this.mDeviceListGroup.removeAll();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addCachedDevices() {
        getView().post(this.mAddCachedDevicesRunnable);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("bt_scan".equals(preference.getKey())) {
            startScanning();
            return true;
        } else if (preference instanceof BluetoothDevicePreference) {
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) preference;
            this.mSelectedDevice = bluetoothDevicePreference.getCachedDevice().getDevice();
            boolean z = false;
            for (int i = 0; i < this.mSelectedList.size(); i++) {
                if (this.mSelectedList.get(i).getBondState() == 11) {
                    z = true;
                }
            }
            if (!z && !this.mPairingBond) {
                this.mPairingBond = true;
                this.mSelectedList.add(this.mSelectedDevice);
                onDevicePreferenceClick(bluetoothDevicePreference);
            }
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDevicePreferenceClick(BluetoothDevicePreference bluetoothDevicePreference) {
        bluetoothDevicePreference.onClicked();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDevicePreferenceMap.get(cachedBluetoothDevice) == null && this.mBluetoothAdapter.getState() == 12 && this.mFilter.matches(cachedBluetoothDevice.getDevice())) {
            createDevicePreference(cachedBluetoothDevice);
        }
    }

    void createDevicePreference(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mDeviceListGroup == null) {
            Log.w("DeviceListPreferenceFragment", "Trying to create a device preference before the list group/category exists!");
        } else if (isBtPhone(cachedBluetoothDevice)) {
            Log.d("DeviceListPreferenceFragment", "createDevicePreference: is btphone");
        } else {
            String address = cachedBluetoothDevice.getDevice().getAddress();
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) getCachedPreference(address);
            if (bluetoothDevicePreference == null) {
                bluetoothDevicePreference = new BluetoothDevicePreference(getPrefContext(), cachedBluetoothDevice, this.mShowDevicesWithoutNames, 2);
                bluetoothDevicePreference.setKey(address);
                bluetoothDevicePreference.hideSecondTarget(true);
                this.mDeviceListGroup.addPreference(bluetoothDevicePreference);
            }
            initDevicePreference(bluetoothDevicePreference);
            this.mDevicePreferenceMap.put(cachedBluetoothDevice, bluetoothDevicePreference);
        }
    }

    private boolean isBtPhone(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        if (btClass != null) {
            String address = cachedBluetoothDevice.getDevice().getAddress();
            Log.d("DeviceListPreferenceFragment", "isBtPhone: " + address + btClass.getMajorDeviceClass());
            return btClass.getMajorDeviceClass() == 512;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFooterPreference(Preference preference) {
        preference.setTitle(getString(R$string.bluetooth_footer_mac_message, BidiFormatter.getInstance().unicodeWrap(this.mBluetoothAdapter.getAddress())));
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        BluetoothDevicePreference remove = this.mDevicePreferenceMap.remove(cachedBluetoothDevice);
        if (remove != null) {
            this.mDeviceListGroup.removePreference(remove);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableScanning() {
        if (this.mScanEnabled) {
            return;
        }
        startScanning();
        this.mScanEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableScanning() {
        if (this.mScanEnabled) {
            stopScanning();
            this.mScanEnabled = false;
        }
    }

    public void onScanningStateChanged(boolean z) {
        if (z || !this.mScanEnabled) {
            return;
        }
        startScanning();
    }

    public void addDeviceCategory(PreferenceGroup preferenceGroup, int i, BluetoothDeviceFilter.Filter filter, boolean z) {
        cacheRemoveAllPrefs(preferenceGroup);
        preferenceGroup.setTitle(i);
        this.mDeviceListGroup = preferenceGroup;
        if (z) {
            setFilter(BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
            addCachedDevices();
        } else {
            setFilter(filter);
        }
        preferenceGroup.setEnabled(true);
        removeCachedPrefs(preferenceGroup);
    }

    void startScanning() {
        if (this.mBluetoothAdapter.isDiscovering()) {
            return;
        }
        this.mBluetoothAdapter.startDiscovery();
    }

    void stopScanning() {
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
    }
}
