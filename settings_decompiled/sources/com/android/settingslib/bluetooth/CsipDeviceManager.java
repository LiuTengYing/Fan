package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public class CsipDeviceManager {
    private final LocalBluetoothManager mBtManager;
    private final List<CachedBluetoothDevice> mCachedDevices;

    private boolean isValidGroupId(int i) {
        return i != -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CsipDeviceManager(LocalBluetoothManager localBluetoothManager, List<CachedBluetoothDevice> list) {
        this.mBtManager = localBluetoothManager;
        this.mCachedDevices = list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initCsipDeviceIfNeeded(CachedBluetoothDevice cachedBluetoothDevice) {
        int baseGroupId = getBaseGroupId(cachedBluetoothDevice.getDevice());
        if (isValidGroupId(baseGroupId)) {
            log("initCsipDeviceIfNeeded: " + cachedBluetoothDevice + " (group: " + baseGroupId + ")");
            cachedBluetoothDevice.setGroupId(baseGroupId);
        }
    }

    private int getBaseGroupId(BluetoothDevice bluetoothDevice) {
        Map<Integer, ParcelUuid> groupUuidMapByDevice;
        CsipSetCoordinatorProfile csipSetCoordinatorProfile = this.mBtManager.getProfileManager().getCsipSetCoordinatorProfile();
        if (csipSetCoordinatorProfile == null || (groupUuidMapByDevice = csipSetCoordinatorProfile.getGroupUuidMapByDevice(bluetoothDevice)) == null) {
            return -1;
        }
        for (Map.Entry<Integer, ParcelUuid> entry : groupUuidMapByDevice.entrySet()) {
            if (entry.getValue().equals(BluetoothUuid.CAP)) {
                return entry.getKey().intValue();
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setMemberDeviceIfNeeded(CachedBluetoothDevice cachedBluetoothDevice) {
        int groupId = cachedBluetoothDevice.getGroupId();
        if (isValidGroupId(groupId)) {
            CachedBluetoothDevice cachedDevice = getCachedDevice(groupId);
            log("setMemberDeviceIfNeeded, main: " + cachedDevice + ", member: " + cachedBluetoothDevice);
            if (cachedDevice != null) {
                cachedDevice.addMemberDevice(cachedBluetoothDevice);
                cachedBluetoothDevice.setName(cachedDevice.getName());
                return true;
            }
            return false;
        }
        return false;
    }

    private CachedBluetoothDevice getCachedDevice(int i) {
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
            if (cachedBluetoothDevice.getGroupId() == i) {
                return cachedBluetoothDevice;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateCsipDevices() {
        HashSet<Integer> hashSet = new HashSet();
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (!isValidGroupId(cachedBluetoothDevice.getGroupId())) {
                int baseGroupId = getBaseGroupId(cachedBluetoothDevice.getDevice());
                if (isValidGroupId(baseGroupId)) {
                    cachedBluetoothDevice.setGroupId(baseGroupId);
                    hashSet.add(Integer.valueOf(baseGroupId));
                }
            }
        }
        for (Integer num : hashSet) {
            onGroupIdChanged(num.intValue());
        }
    }

    @VisibleForTesting
    void onGroupIdChanged(int i) {
        CachedBluetoothDevice cachedBluetoothDevice = null;
        int i2 = -1;
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            CachedBluetoothDevice cachedBluetoothDevice2 = this.mCachedDevices.get(size);
            if (cachedBluetoothDevice2.getGroupId() == i) {
                if (i2 != -1) {
                    log("onGroupIdChanged: removed from UI device =" + cachedBluetoothDevice2 + ", with groupId=" + i + " firstMatchedIndex=" + i2);
                    cachedBluetoothDevice.addMemberDevice(cachedBluetoothDevice2);
                    this.mCachedDevices.remove(size);
                    this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice2);
                    return;
                }
                i2 = size;
                cachedBluetoothDevice = cachedBluetoothDevice2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onProfileConnectionStateChangedIfProcessed(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        log("onProfileConnectionStateChangedIfProcessed: " + cachedBluetoothDevice + ", state: " + i);
        if (i != 0) {
            if (i != 2) {
                return false;
            }
            onGroupIdChanged(cachedBluetoothDevice.getGroupId());
            CachedBluetoothDevice findMainDevice = findMainDevice(cachedBluetoothDevice);
            if (findMainDevice != null) {
                if (findMainDevice.isConnected()) {
                    findMainDevice.refresh();
                    return true;
                }
                this.mBtManager.getEventManager().dispatchDeviceRemoved(findMainDevice);
                findMainDevice.switchMemberDeviceContent(findMainDevice, cachedBluetoothDevice);
                findMainDevice.refresh();
                this.mBtManager.getEventManager().dispatchDeviceAdded(findMainDevice);
                return true;
            }
            return false;
        }
        CachedBluetoothDevice findMainDevice2 = findMainDevice(cachedBluetoothDevice);
        if (findMainDevice2 != null) {
            findMainDevice2.refresh();
            return true;
        }
        Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
        if (memberDevice.isEmpty()) {
            return false;
        }
        for (CachedBluetoothDevice cachedBluetoothDevice2 : memberDevice) {
            if (cachedBluetoothDevice2.isConnected()) {
                this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice);
                cachedBluetoothDevice.switchMemberDeviceContent(cachedBluetoothDevice2, cachedBluetoothDevice);
                cachedBluetoothDevice.refresh();
                this.mBtManager.getEventManager().dispatchDeviceAdded(cachedBluetoothDevice);
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedBluetoothDevice findMainDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        List<CachedBluetoothDevice> list;
        if (cachedBluetoothDevice != null && (list = this.mCachedDevices) != null) {
            for (CachedBluetoothDevice cachedBluetoothDevice2 : list) {
                if (isValidGroupId(cachedBluetoothDevice2.getGroupId())) {
                    Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice2.getMemberDevice();
                    if (memberDevice.isEmpty()) {
                        continue;
                    } else {
                        for (CachedBluetoothDevice cachedBluetoothDevice3 : memberDevice) {
                            if (cachedBluetoothDevice3 != null && cachedBluetoothDevice3.equals(cachedBluetoothDevice)) {
                                return cachedBluetoothDevice2;
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public boolean isExistedGroupId(int i) {
        return getCachedDevice(i) != null;
    }

    private void log(String str) {
        Log.d("CsipDeviceManager", str);
    }
}
