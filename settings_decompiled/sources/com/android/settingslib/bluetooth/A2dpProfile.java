package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes2.dex */
public class A2dpProfile implements LocalBluetoothProfile {
    static final ParcelUuid[] SINK_UUIDS = {BluetoothUuid.A2DP_SINK, BluetoothUuid.ADV_AUDIO_DIST};
    private final BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private boolean mIsProfileReady;
    private final LocalBluetoothProfileManager mProfileManager;
    private BluetoothA2dp mService;

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return true;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302335;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 1;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return 2;
    }

    public String toString() {
        return "A2DP";
    }

    /* loaded from: classes2.dex */
    private final class A2dpServiceListener implements BluetoothProfile.ServiceListener {
        private A2dpServiceListener() {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            A2dpProfile.this.mService = (BluetoothA2dp) bluetoothProfile;
            List<BluetoothDevice> connectedDevices = A2dpProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice remove = connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = A2dpProfile.this.mDeviceManager.findDevice(remove);
                if (findDevice == null) {
                    Log.w("A2dpProfile", "A2dpProfile found new device: " + remove);
                    findDevice = A2dpProfile.this.mDeviceManager.addDevice(remove);
                }
                findDevice.onProfileStateChanged(A2dpProfile.this, 2);
                findDevice.refresh();
            }
            A2dpProfile.this.mIsProfileReady = true;
            A2dpProfile.this.mProfileManager.callServiceConnectedListeners();
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            A2dpProfile.this.mIsProfileReady = false;
        }
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return this.mIsProfileReady;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public A2dpProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mContext = context;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        defaultAdapter.getProfileProxy(context, new A2dpServiceListener(), 2);
    }

    public List<BluetoothDevice> getConnectedDevices() {
        return getDevicesByStates(new int[]{2, 1, 3});
    }

    private List<BluetoothDevice> getDevicesByStates(int[] iArr) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return new ArrayList(0);
        }
        return bluetoothA2dp.getDevicesMatchingConnectionStates(iArr);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return 0;
        }
        return bluetoothA2dp.getConnectionState(bluetoothDevice);
    }

    public boolean setActiveDevice(BluetoothDevice bluetoothDevice) {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            return false;
        }
        if (bluetoothDevice == null) {
            return bluetoothAdapter.removeActiveDevice(0);
        }
        return bluetoothAdapter.setActiveDevice(bluetoothDevice, 0);
    }

    public BluetoothDevice getActiveDevice() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            return null;
        }
        List activeDevices = bluetoothAdapter.getActiveDevices(2);
        if (activeDevices.size() > 0) {
            return (BluetoothDevice) activeDevices.get(0);
        }
        return null;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        return bluetoothA2dp != null && bluetoothA2dp.getConnectionPolicy(bluetoothDevice) > 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothA2dp bluetoothA2dp = this.mService;
        if (bluetoothA2dp == null) {
            return false;
        }
        if (z) {
            if (bluetoothA2dp.getConnectionPolicy(bluetoothDevice) < 100) {
                return this.mService.setConnectionPolicy(bluetoothDevice, 100);
            }
            return false;
        }
        return bluetoothA2dp.setConnectionPolicy(bluetoothDevice, 0);
    }

    public boolean supportsHighQualityAudio(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            bluetoothDevice = getActiveDevice();
        }
        return bluetoothDevice != null && this.mService.isOptionalCodecsSupported(bluetoothDevice) == 1;
    }

    public boolean isHighQualityAudioEnabled(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            bluetoothDevice = getActiveDevice();
        }
        if (bluetoothDevice == null) {
            return false;
        }
        int isOptionalCodecsEnabled = this.mService.isOptionalCodecsEnabled(bluetoothDevice);
        if (isOptionalCodecsEnabled != -1) {
            return isOptionalCodecsEnabled == 1;
        } else if (getConnectionStatus(bluetoothDevice) == 2 || !supportsHighQualityAudio(bluetoothDevice)) {
            BluetoothCodecConfig codecConfig = this.mService.getCodecStatus(bluetoothDevice) != null ? this.mService.getCodecStatus(bluetoothDevice).getCodecConfig() : null;
            if (codecConfig != null) {
                return !codecConfig.isMandatoryCodec();
            }
            return false;
        } else {
            return true;
        }
    }

    public void setHighQualityAudioEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        if (bluetoothDevice == null) {
            bluetoothDevice = getActiveDevice();
        }
        if (bluetoothDevice == null) {
            return;
        }
        this.mService.setOptionalCodecsEnabled(bluetoothDevice, z ? 1 : 0);
        if (getConnectionStatus(bluetoothDevice) != 2) {
            return;
        }
        if (z) {
            this.mService.enableOptionalCodecs(bluetoothDevice);
        } else {
            this.mService.disableOptionalCodecs(bluetoothDevice);
        }
    }

    public String getHighQualityAudioOptionLabel(BluetoothDevice bluetoothDevice) {
        List list;
        BluetoothDevice activeDevice = bluetoothDevice != null ? bluetoothDevice : getActiveDevice();
        int i = R$string.bluetooth_profile_a2dp_high_quality_unknown_codec;
        if (activeDevice != null && supportsHighQualityAudio(bluetoothDevice)) {
            char c = 2;
            if (getConnectionStatus(bluetoothDevice) == 2) {
                BluetoothCodecConfig bluetoothCodecConfig = null;
                if (this.mService.getCodecStatus(bluetoothDevice) != null) {
                    list = this.mService.getCodecStatus(bluetoothDevice).getCodecsSelectableCapabilities();
                    Collections.sort(list, new Comparator() { // from class: com.android.settingslib.bluetooth.A2dpProfile$$ExternalSyntheticLambda0
                        @Override // java.util.Comparator
                        public final int compare(Object obj, Object obj2) {
                            int lambda$getHighQualityAudioOptionLabel$0;
                            lambda$getHighQualityAudioOptionLabel$0 = A2dpProfile.lambda$getHighQualityAudioOptionLabel$0((BluetoothCodecConfig) obj, (BluetoothCodecConfig) obj2);
                            return lambda$getHighQualityAudioOptionLabel$0;
                        }
                    });
                } else {
                    list = null;
                }
                if (list != null && list.size() >= 1) {
                    bluetoothCodecConfig = (BluetoothCodecConfig) list.get(0);
                }
                int codecType = (bluetoothCodecConfig == null || bluetoothCodecConfig.isMandatoryCodec()) ? 1000000 : bluetoothCodecConfig.getCodecType();
                if (codecType == 0) {
                    c = 1;
                } else if (codecType != 1) {
                    c = codecType != 2 ? codecType != 3 ? codecType != 4 ? (char) 65535 : (char) 5 : (char) 4 : (char) 3;
                }
                if (c < 0) {
                    return this.mContext.getString(i);
                }
                Context context = this.mContext;
                return context.getString(R$string.bluetooth_profile_a2dp_high_quality, context.getResources().getStringArray(R$array.bluetooth_a2dp_codec_titles)[c]);
            }
        }
        return this.mContext.getString(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getHighQualityAudioOptionLabel$0(BluetoothCodecConfig bluetoothCodecConfig, BluetoothCodecConfig bluetoothCodecConfig2) {
        return bluetoothCodecConfig2.getCodecPriority() - bluetoothCodecConfig.getCodecPriority();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return R$string.bluetooth_profile_a2dp;
    }

    protected void finalize() {
        Log.d("A2dpProfile", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(2, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("A2dpProfile", "Error cleaning up A2DP proxy", th);
            }
        }
    }
}
