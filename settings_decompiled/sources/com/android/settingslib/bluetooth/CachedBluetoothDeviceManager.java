package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class CachedBluetoothDeviceManager {
    private final LocalBluetoothManager mBtManager;
    @VisibleForTesting
    final List<CachedBluetoothDevice> mCachedDevices;
    private Context mContext;
    @VisibleForTesting
    CsipDeviceManager mCsipDeviceManager;
    @VisibleForTesting
    HearingAidDeviceManager mHearingAidDeviceManager;
    BluetoothDevice mOngoingSetMemberPair;

    public CachedBluetoothDeviceManager(Context context, LocalBluetoothManager localBluetoothManager) {
        ArrayList arrayList = new ArrayList();
        this.mCachedDevices = arrayList;
        this.mContext = context;
        this.mBtManager = localBluetoothManager;
        this.mHearingAidDeviceManager = new HearingAidDeviceManager(localBluetoothManager, arrayList);
        this.mCsipDeviceManager = new CsipDeviceManager(localBluetoothManager, arrayList);
    }

    public synchronized Collection<CachedBluetoothDevice> getCachedDevicesCopy() {
        return new ArrayList(this.mCachedDevices);
    }

    public void onDeviceNameUpdated(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice findDevice = findDevice(bluetoothDevice);
        if (findDevice != null) {
            findDevice.refreshName();
        }
    }

    public synchronized CachedBluetoothDevice findDevice(BluetoothDevice bluetoothDevice) {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (cachedBluetoothDevice.getDevice().equals(bluetoothDevice)) {
                return cachedBluetoothDevice;
            }
            Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
            if (!memberDevice.isEmpty()) {
                for (CachedBluetoothDevice cachedBluetoothDevice2 : memberDevice) {
                    if (cachedBluetoothDevice2.getDevice().equals(bluetoothDevice)) {
                        return cachedBluetoothDevice2;
                    }
                }
            }
            CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null && subDevice.getDevice().equals(bluetoothDevice)) {
                return subDevice;
            }
        }
        return null;
    }

    public CachedBluetoothDevice addDevice(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice findDevice;
        LocalBluetoothProfileManager profileManager = this.mBtManager.getProfileManager();
        synchronized (this) {
            findDevice = findDevice(bluetoothDevice);
            if (findDevice == null) {
                findDevice = new CachedBluetoothDevice(this.mContext, profileManager, bluetoothDevice);
                this.mCsipDeviceManager.initCsipDeviceIfNeeded(findDevice);
                this.mHearingAidDeviceManager.initHearingAidDeviceIfNeeded(findDevice);
                if (!this.mCsipDeviceManager.setMemberDeviceIfNeeded(findDevice) && !this.mHearingAidDeviceManager.setSubDeviceIfNeeded(findDevice)) {
                    this.mCachedDevices.add(findDevice);
                    this.mBtManager.getEventManager().dispatchDeviceAdded(findDevice);
                }
            }
        }
        return findDevice;
    }

    public synchronized boolean isSubDevice(BluetoothDevice bluetoothDevice) {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (!cachedBluetoothDevice.getDevice().equals(bluetoothDevice)) {
                Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
                if (!memberDevice.isEmpty()) {
                    for (CachedBluetoothDevice cachedBluetoothDevice2 : memberDevice) {
                        if (cachedBluetoothDevice2.getDevice().equals(bluetoothDevice)) {
                            return true;
                        }
                    }
                    continue;
                } else {
                    CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                    if (subDevice != null && subDevice.getDevice().equals(bluetoothDevice)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized void updateHearingAidsDevices() {
        this.mHearingAidDeviceManager.updateHearingAidsDevices();
    }

    public synchronized void updateCsipDevices() {
        this.mCsipDeviceManager.updateCsipDevices();
    }

    public String getName(BluetoothDevice bluetoothDevice) {
        CachedBluetoothDevice findDevice = findDevice(bluetoothDevice);
        if (findDevice != null && findDevice.getName() != null) {
            return findDevice.getName();
        }
        String alias = bluetoothDevice.getAlias();
        return alias != null ? alias : bluetoothDevice.getAddress();
    }

    public synchronized void clearNonBondedDevices() {
        clearNonBondedSubDevices();
        this.mCachedDevices.removeIf(new Predicate() { // from class: com.android.settingslib.bluetooth.CachedBluetoothDeviceManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$clearNonBondedDevices$0;
                lambda$clearNonBondedDevices$0 = CachedBluetoothDeviceManager.lambda$clearNonBondedDevices$0((CachedBluetoothDevice) obj);
                return lambda$clearNonBondedDevices$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$clearNonBondedDevices$0(CachedBluetoothDevice cachedBluetoothDevice) {
        return cachedBluetoothDevice.getBondState() == 10;
    }

    private void clearNonBondedSubDevices() {
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
            Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
            if (!memberDevice.isEmpty()) {
                Object[] array = memberDevice.toArray();
                for (Object obj : array) {
                    CachedBluetoothDevice cachedBluetoothDevice2 = (CachedBluetoothDevice) obj;
                    if (cachedBluetoothDevice2.getDevice().getBondState() == 10) {
                        cachedBluetoothDevice.removeMemberDevice(cachedBluetoothDevice2);
                    }
                }
                return;
            }
            CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null && subDevice.getDevice().getBondState() == 10) {
                cachedBluetoothDevice.setSubDevice(null);
            }
        }
    }

    public synchronized void onScanningStateChanged(boolean z) {
        if (z) {
            for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
                CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
                cachedBluetoothDevice.setJustDiscovered(false);
                Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
                if (!memberDevice.isEmpty()) {
                    for (CachedBluetoothDevice cachedBluetoothDevice2 : memberDevice) {
                        cachedBluetoothDevice2.setJustDiscovered(false);
                    }
                    return;
                }
                CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                if (subDevice != null) {
                    subDevice.setJustDiscovered(false);
                }
            }
        }
    }

    public synchronized void onBluetoothStateChanged(int i) {
        if (i == 13) {
            for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
                CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevices.get(size);
                Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
                if (!memberDevice.isEmpty()) {
                    for (CachedBluetoothDevice cachedBluetoothDevice2 : memberDevice) {
                        if (cachedBluetoothDevice2.getBondState() != 12) {
                            cachedBluetoothDevice.removeMemberDevice(cachedBluetoothDevice2);
                        }
                    }
                } else {
                    CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
                    if (subDevice != null && subDevice.getBondState() != 12) {
                        cachedBluetoothDevice.setSubDevice(null);
                    }
                }
                if (cachedBluetoothDevice.getBondState() != 12) {
                    cachedBluetoothDevice.setJustDiscovered(false);
                    this.mCachedDevices.remove(size);
                }
            }
        }
    }

    public synchronized boolean onProfileConnectionStateChangedIfProcessed(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        if (i2 == 21) {
            return this.mHearingAidDeviceManager.onProfileConnectionStateChangedIfProcessed(cachedBluetoothDevice, i);
        } else if (i2 == 25) {
            return this.mCsipDeviceManager.onProfileConnectionStateChangedIfProcessed(cachedBluetoothDevice, i);
        } else {
            return false;
        }
    }

    public synchronized void onDeviceUnpaired(CachedBluetoothDevice cachedBluetoothDevice) {
        CachedBluetoothDevice findMainDevice = this.mCsipDeviceManager.findMainDevice(cachedBluetoothDevice);
        Set<CachedBluetoothDevice> memberDevice = cachedBluetoothDevice.getMemberDevice();
        if (!memberDevice.isEmpty()) {
            for (CachedBluetoothDevice cachedBluetoothDevice2 : memberDevice) {
                cachedBluetoothDevice2.unpair();
                cachedBluetoothDevice.removeMemberDevice(cachedBluetoothDevice2);
            }
        } else if (findMainDevice != null) {
            findMainDevice.unpair();
        }
        CachedBluetoothDevice findMainDevice2 = this.mHearingAidDeviceManager.findMainDevice(cachedBluetoothDevice);
        CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
        if (subDevice != null) {
            subDevice.unpair();
            cachedBluetoothDevice.setSubDevice(null);
        } else if (findMainDevice2 != null) {
            findMainDevice2.unpair();
            findMainDevice2.setSubDevice(null);
        }
    }

    public synchronized boolean shouldPairByCsip(BluetoothDevice bluetoothDevice, int i) {
        if (this.mOngoingSetMemberPair == null && bluetoothDevice.getBondState() == 10 && this.mCsipDeviceManager.isExistedGroupId(i)) {
            Log.d("CachedBluetoothDeviceManager", "Bond " + bluetoothDevice.getName() + " by CSIP");
            this.mOngoingSetMemberPair = bluetoothDevice;
            return true;
        }
        return false;
    }

    public synchronized boolean onBondStateChangedIfProcess(BluetoothDevice bluetoothDevice, int i) {
        BluetoothDevice bluetoothDevice2 = this.mOngoingSetMemberPair;
        if (bluetoothDevice2 == null || !bluetoothDevice2.equals(bluetoothDevice)) {
            return false;
        }
        if (i == 11) {
            return true;
        }
        this.mOngoingSetMemberPair = null;
        if (i != 10 && findDevice(bluetoothDevice) == null) {
            this.mCachedDevices.add(new CachedBluetoothDevice(this.mContext, this.mBtManager.getProfileManager(), bluetoothDevice));
            findDevice(bluetoothDevice).connect();
        }
        return true;
    }

    public boolean isOngoingPairByCsip(BluetoothDevice bluetoothDevice) {
        BluetoothDevice bluetoothDevice2 = this.mOngoingSetMemberPair;
        return bluetoothDevice2 != null && bluetoothDevice2.equals(bluetoothDevice);
    }
}
