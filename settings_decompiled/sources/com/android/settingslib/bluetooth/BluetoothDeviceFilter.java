package com.android.settingslib.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
/* loaded from: classes2.dex */
public final class BluetoothDeviceFilter {
    public static final Filter ALL_FILTER;
    public static final Filter BONDED_DEVICE_FILTER;
    private static final Filter[] FILTERS;
    public static final Filter UNBONDED_DEVICE_FILTER;

    /* loaded from: classes2.dex */
    public interface Filter {
        boolean matches(BluetoothDevice bluetoothDevice);
    }

    static {
        AllFilter allFilter = new AllFilter();
        ALL_FILTER = allFilter;
        BONDED_DEVICE_FILTER = new BondedDeviceFilter();
        UNBONDED_DEVICE_FILTER = new UnbondedDeviceFilter();
        FILTERS = new Filter[]{allFilter, new AudioFilter(), new TransferFilter(), new PanuFilter(), new NapFilter()};
    }

    public static Filter getFilter(int i) {
        if (i >= 0) {
            Filter[] filterArr = FILTERS;
            if (i < filterArr.length) {
                return filterArr[i];
            }
        }
        Log.w("BluetoothDeviceFilter", "Invalid filter type " + i + " for device picker");
        return ALL_FILTER;
    }

    /* loaded from: classes2.dex */
    private static final class AllFilter implements Filter {
        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return true;
        }

        private AllFilter() {
        }
    }

    /* loaded from: classes2.dex */
    private static final class BondedDeviceFilter implements Filter {
        private BondedDeviceFilter() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return bluetoothDevice.getBondState() == 12;
        }
    }

    /* loaded from: classes2.dex */
    private static final class UnbondedDeviceFilter implements Filter {
        private UnbondedDeviceFilter() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return bluetoothDevice.getBondState() != 12;
        }
    }

    /* loaded from: classes2.dex */
    private static abstract class ClassUuidFilter implements Filter {
        abstract boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass);

        private ClassUuidFilter() {
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.Filter
        public boolean matches(BluetoothDevice bluetoothDevice) {
            return matches(bluetoothDevice.getUuids(), bluetoothDevice.getBluetoothClass());
        }
    }

    /* loaded from: classes2.dex */
    private static final class AudioFilter extends ClassUuidFilter {
        private AudioFilter() {
            super();
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr != null) {
                if (BluetoothUuid.containsAnyUuid(parcelUuidArr, A2dpProfile.SINK_UUIDS) || BluetoothUuid.containsAnyUuid(parcelUuidArr, HeadsetProfile.UUIDS)) {
                    return true;
                }
            } else if (bluetoothClass != null && (BluetoothDeviceFilter.doesClassMatch(bluetoothClass, 1) || BluetoothDeviceFilter.doesClassMatch(bluetoothClass, 0))) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes2.dex */
    private static final class TransferFilter extends ClassUuidFilter {
        private TransferFilter() {
            super();
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr == null || !ArrayUtils.contains(parcelUuidArr, BluetoothUuid.OBEX_OBJECT_PUSH)) {
                return bluetoothClass != null && BluetoothDeviceFilter.doesClassMatch(bluetoothClass, 2);
            }
            return true;
        }
    }

    /* loaded from: classes2.dex */
    private static final class PanuFilter extends ClassUuidFilter {
        private PanuFilter() {
            super();
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr == null || !ArrayUtils.contains(parcelUuidArr, BluetoothUuid.PANU)) {
                return bluetoothClass != null && BluetoothDeviceFilter.doesClassMatch(bluetoothClass, 4);
            }
            return true;
        }
    }

    /* loaded from: classes2.dex */
    private static final class NapFilter extends ClassUuidFilter {
        private NapFilter() {
            super();
        }

        @Override // com.android.settingslib.bluetooth.BluetoothDeviceFilter.ClassUuidFilter
        boolean matches(ParcelUuid[] parcelUuidArr, BluetoothClass bluetoothClass) {
            if (parcelUuidArr == null || !ArrayUtils.contains(parcelUuidArr, BluetoothUuid.NAP)) {
                return bluetoothClass != null && BluetoothDeviceFilter.doesClassMatch(bluetoothClass, 5);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public static boolean doesClassMatch(BluetoothClass bluetoothClass, int i) {
        return bluetoothClass.doesClassMatch(i);
    }
}
