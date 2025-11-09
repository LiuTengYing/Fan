package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes2.dex */
public class HearingAidDeviceManager {
    private final LocalBluetoothManager mBtManager;
    private final List<CachedBluetoothDevice> mCachedDevices;

    private boolean isValidHiSyncId(long j) {
        return j != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HearingAidDeviceManager(LocalBluetoothManager localBluetoothManager, List<CachedBluetoothDevice> list) {
        this.mBtManager = localBluetoothManager;
        this.mCachedDevices = list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initHearingAidDeviceIfNeeded(CachedBluetoothDevice cachedBluetoothDevice) {
        long hiSyncId = getHiSyncId(cachedBluetoothDevice.getDevice());
        if (isValidHiSyncId(hiSyncId)) {
            cachedBluetoothDevice.setHiSyncId(hiSyncId);
        }
    }

    private long getHiSyncId(BluetoothDevice bluetoothDevice) {
        HearingAidProfile hearingAidProfile = this.mBtManager.getProfileManager().getHearingAidProfile();
        if (hearingAidProfile != null) {
            return hearingAidProfile.getHiSyncId(bluetoothDevice);
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setSubDeviceIfNeeded(CachedBluetoothDevice cachedBluetoothDevice) {
        CachedBluetoothDevice cachedDevice;
        long hiSyncId = cachedBluetoothDevice.getHiSyncId();
        if (!isValidHiSyncId(hiSyncId) || (cachedDevice = getCachedDevice(hiSyncId)) == null) {
            return false;
        }
        cachedDevice.setSubDevice(cachedBluetoothDevice);
        return true;
    }

    private CachedBluetoothDevice getCachedDevice(long j) {
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
            if (cachedBluetoothDevice.getHiSyncId() == j) {
                return cachedBluetoothDevice;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateHearingAidsDevices() {
        HashSet<Long> hashSet = new HashSet();
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (!isValidHiSyncId(cachedBluetoothDevice.getHiSyncId())) {
                long hiSyncId = getHiSyncId(cachedBluetoothDevice.getDevice());
                if (isValidHiSyncId(hiSyncId)) {
                    cachedBluetoothDevice.setHiSyncId(hiSyncId);
                    hashSet.add(Long.valueOf(hiSyncId));
                }
            }
        }
        for (Long l : hashSet) {
            onHiSyncIdChanged(l.longValue());
        }
    }

    @VisibleForTesting
    void onHiSyncIdChanged(long j) {
        CachedBluetoothDevice cachedBluetoothDevice;
        int size = this.mCachedDevices.size() - 1;
        int i = -1;
        while (size >= 0) {
            CachedBluetoothDevice cachedBluetoothDevice2 = this.mCachedDevices.get(size);
            if (cachedBluetoothDevice2.getHiSyncId() == j) {
                if (i != -1) {
                    if (cachedBluetoothDevice2.isConnected()) {
                        cachedBluetoothDevice = this.mCachedDevices.get(i);
                        size = i;
                    } else {
                        cachedBluetoothDevice2 = this.mCachedDevices.get(i);
                        cachedBluetoothDevice = cachedBluetoothDevice2;
                    }
                    cachedBluetoothDevice2.setSubDevice(cachedBluetoothDevice);
                    this.mCachedDevices.remove(size);
                    log("onHiSyncIdChanged: removed from UI device =" + cachedBluetoothDevice + ", with hiSyncId=" + j);
                    this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice);
                    return;
                }
                i = size;
            }
            size--;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onProfileConnectionStateChangedIfProcessed(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (i != 0) {
            if (i != 2) {
                return false;
            }
            onHiSyncIdChanged(cachedBluetoothDevice.getHiSyncId());
            CachedBluetoothDevice findMainDevice = findMainDevice(cachedBluetoothDevice);
            if (findMainDevice != null) {
                if (findMainDevice.isConnected()) {
                    findMainDevice.refresh();
                    return true;
                }
                this.mBtManager.getEventManager().dispatchDeviceRemoved(findMainDevice);
                findMainDevice.switchSubDeviceContent();
                findMainDevice.refresh();
                this.mBtManager.getEventManager().dispatchDeviceAdded(findMainDevice);
                return true;
            }
            return false;
        }
        CachedBluetoothDevice findMainDevice2 = findMainDevice(cachedBluetoothDevice);
        if (cachedBluetoothDevice.getUnpairing()) {
            return true;
        }
        if (findMainDevice2 != null) {
            findMainDevice2.refresh();
            return true;
        }
        CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
        if (subDevice == null || !subDevice.isConnected()) {
            return false;
        }
        this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice);
        cachedBluetoothDevice.switchSubDeviceContent();
        cachedBluetoothDevice.refresh();
        this.mBtManager.getEventManager().dispatchDeviceAdded(cachedBluetoothDevice);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDevice findMainDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        CachedBluetoothDevice subDevice;
        for (CachedBluetoothDevice cachedBluetoothDevice2 : this.mCachedDevices) {
            if (isValidHiSyncId(cachedBluetoothDevice2.getHiSyncId()) && (subDevice = cachedBluetoothDevice2.getSubDevice()) != null && subDevice.equals(cachedBluetoothDevice)) {
                return cachedBluetoothDevice2;
            }
        }
        return null;
    }

    private void log(String str) {
        Log.d("HearingAidDeviceManager", str);
    }
}
