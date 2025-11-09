package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothLeAudioContentMetadata;
import android.bluetooth.BluetoothLeBroadcast;
import android.bluetooth.BluetoothLeBroadcastMetadata;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settingslib.R$string;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
/* loaded from: classes2.dex */
public class LocalBluetoothLeBroadcast implements LocalBluetoothProfile {
    private String mAppSourceName;
    private BluetoothLeAudioContentMetadata mBluetoothLeAudioContentMetadata;
    private BluetoothLeBroadcastMetadata mBluetoothLeBroadcastMetadata;
    private final BluetoothLeBroadcast.Callback mBroadcastCallback;
    private byte[] mBroadcastCode;
    private BluetoothLeAudioContentMetadata.Builder mBuilder;
    private Executor mExecutor;
    private boolean mIsProfileReady;
    private String mProgramInfo;
    private BluetoothLeBroadcast mService;
    private final BluetoothProfile.ServiceListener mServiceListener;
    private SharedPreferences mSharedPref;
    private int mBroadcastId = -1;
    private String mNewAppSourceName = "";

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean accessProfileEnabled() {
        return false;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 0;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getOrdinal() {
        return 1;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getProfileId() {
        return 26;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return false;
    }

    public String toString() {
        return "LE_AUDIO_BROADCAST";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalBluetoothLeBroadcast(Context context) {
        byte[] bytes;
        this.mAppSourceName = "";
        BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settingslib.bluetooth.LocalBluetoothLeBroadcast.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                Log.d("LocalBluetoothLeBroadcast", "Bluetooth service connected");
                LocalBluetoothLeBroadcast.this.mService = (BluetoothLeBroadcast) bluetoothProfile;
                LocalBluetoothLeBroadcast.this.mIsProfileReady = true;
                LocalBluetoothLeBroadcast localBluetoothLeBroadcast = LocalBluetoothLeBroadcast.this;
                localBluetoothLeBroadcast.registerServiceCallBack(localBluetoothLeBroadcast.mExecutor, LocalBluetoothLeBroadcast.this.mBroadcastCallback);
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                Log.d("LocalBluetoothLeBroadcast", "Bluetooth service disconnected");
                LocalBluetoothLeBroadcast.this.mIsProfileReady = false;
                LocalBluetoothLeBroadcast localBluetoothLeBroadcast = LocalBluetoothLeBroadcast.this;
                localBluetoothLeBroadcast.unregisterServiceCallBack(localBluetoothLeBroadcast.mBroadcastCallback);
            }
        };
        this.mServiceListener = serviceListener;
        this.mBroadcastCallback = new BluetoothLeBroadcast.Callback() { // from class: com.android.settingslib.bluetooth.LocalBluetoothLeBroadcast.2
            public void onPlaybackStarted(int i, int i2) {
            }

            public void onPlaybackStopped(int i, int i2) {
            }

            public void onBroadcastStarted(int i, int i2) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastStarted(), reason = " + i + ", broadcastId = " + i2);
                LocalBluetoothLeBroadcast.this.setLatestBroadcastId(i2);
                LocalBluetoothLeBroadcast localBluetoothLeBroadcast = LocalBluetoothLeBroadcast.this;
                localBluetoothLeBroadcast.setAppSourceName(localBluetoothLeBroadcast.mNewAppSourceName);
            }

            public void onBroadcastStartFailed(int i) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastStartFailed(), reason = " + i);
            }

            public void onBroadcastMetadataChanged(int i, BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastMetadataChanged(), broadcastId = " + i);
                LocalBluetoothLeBroadcast.this.setLatestBluetoothLeBroadcastMetadata(bluetoothLeBroadcastMetadata);
            }

            public void onBroadcastStopped(int i, int i2) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastStopped(), reason = " + i + ", broadcastId = " + i2);
                LocalBluetoothLeBroadcast.this.resetCacheInfo();
            }

            public void onBroadcastStopFailed(int i) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastStopFailed(), reason = " + i);
            }

            public void onBroadcastUpdated(int i, int i2) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastUpdated(), reason = " + i + ", broadcastId = " + i2);
                LocalBluetoothLeBroadcast.this.setLatestBroadcastId(i2);
                LocalBluetoothLeBroadcast localBluetoothLeBroadcast = LocalBluetoothLeBroadcast.this;
                localBluetoothLeBroadcast.setAppSourceName(localBluetoothLeBroadcast.mNewAppSourceName);
            }

            public void onBroadcastUpdateFailed(int i, int i2) {
                Log.d("LocalBluetoothLeBroadcast", "onBroadcastUpdateFailed(), reason = " + i + ", broadcastId = " + i2);
            }
        };
        this.mExecutor = Executors.newSingleThreadExecutor();
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, serviceListener, 26);
        this.mBuilder = new BluetoothLeAudioContentMetadata.Builder();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LocalBluetoothLeBroadcast", 0);
        this.mSharedPref = sharedPreferences;
        if (sharedPreferences != null) {
            String string = sharedPreferences.getString("PrefProgramInfo", "");
            setProgramInfo(string.isEmpty() ? getDefaultValueOfProgramInfo() : string);
            String string2 = this.mSharedPref.getString("PrefBroadcastCode", "");
            if (string2.isEmpty()) {
                bytes = getDefaultValueOfBroadcastCode();
            } else {
                bytes = string2.getBytes(StandardCharsets.UTF_8);
            }
            setBroadcastCode(bytes);
            this.mAppSourceName = this.mSharedPref.getString("PrefAppSourceName", "");
        }
    }

    public void startBroadcast(String str, String str2) {
        this.mNewAppSourceName = str;
        if (this.mService == null) {
            Log.d("LocalBluetoothLeBroadcast", "The BluetoothLeBroadcast is null when starting the broadcast.");
            return;
        }
        Log.d("LocalBluetoothLeBroadcast", "startBroadcast: language = " + str2 + " ,programInfo = " + this.mProgramInfo);
        buildContentMetadata(str2, this.mProgramInfo);
        this.mService.startBroadcast(this.mBluetoothLeAudioContentMetadata, this.mBroadcastCode);
    }

    public void setProgramInfo(String str) {
        if (str == null || str.isEmpty()) {
            Log.d("LocalBluetoothLeBroadcast", "setProgramInfo: programInfo is null or empty");
            return;
        }
        Log.d("LocalBluetoothLeBroadcast", "setProgramInfo: " + str);
        this.mProgramInfo = str;
        SharedPreferences sharedPreferences = this.mSharedPref;
        if (sharedPreferences == null) {
            Log.d("LocalBluetoothLeBroadcast", "setProgramInfo: sharedPref is null");
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("PrefProgramInfo", this.mProgramInfo);
        edit.apply();
    }

    public void setBroadcastCode(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            Log.d("LocalBluetoothLeBroadcast", "setBroadcastCode: broadcastCode is null or empty");
            return;
        }
        this.mBroadcastCode = bArr;
        SharedPreferences sharedPreferences = this.mSharedPref;
        if (sharedPreferences == null) {
            Log.d("LocalBluetoothLeBroadcast", "setBroadcastCode: sharedPref is null");
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("PrefBroadcastCode", new String(bArr, StandardCharsets.UTF_8));
        edit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLatestBroadcastId(int i) {
        this.mBroadcastId = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAppSourceName(String str) {
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        this.mAppSourceName = str;
        this.mNewAppSourceName = "";
        SharedPreferences sharedPreferences = this.mSharedPref;
        if (sharedPreferences == null) {
            Log.d("LocalBluetoothLeBroadcast", "setBroadcastCode: sharedPref is null");
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("PrefAppSourceName", str);
        edit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLatestBluetoothLeBroadcastMetadata(BluetoothLeBroadcastMetadata bluetoothLeBroadcastMetadata) {
        if (bluetoothLeBroadcastMetadata == null || bluetoothLeBroadcastMetadata.getBroadcastId() != this.mBroadcastId) {
            return;
        }
        this.mBluetoothLeBroadcastMetadata = bluetoothLeBroadcastMetadata;
    }

    public void registerServiceCallBack(Executor executor, BluetoothLeBroadcast.Callback callback) {
        BluetoothLeBroadcast bluetoothLeBroadcast = this.mService;
        if (bluetoothLeBroadcast == null) {
            Log.d("LocalBluetoothLeBroadcast", "The BluetoothLeBroadcast is null.");
        } else {
            bluetoothLeBroadcast.registerCallback(executor, callback);
        }
    }

    public void unregisterServiceCallBack(BluetoothLeBroadcast.Callback callback) {
        BluetoothLeBroadcast bluetoothLeBroadcast = this.mService;
        if (bluetoothLeBroadcast == null) {
            Log.d("LocalBluetoothLeBroadcast", "The BluetoothLeBroadcast is null.");
        } else {
            bluetoothLeBroadcast.unregisterCallback(callback);
        }
    }

    private void buildContentMetadata(String str, String str2) {
        this.mBluetoothLeAudioContentMetadata = this.mBuilder.setLanguage(str).setProgramInfo(str2).build();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isProfileReady() {
        return this.mIsProfileReady;
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothLeBroadcast bluetoothLeBroadcast = this.mService;
        if (bluetoothLeBroadcast == null) {
            return 0;
        }
        return bluetoothLeBroadcast.getConnectionState(bluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public boolean isEnabled(BluetoothDevice bluetoothDevice) {
        BluetoothLeBroadcast bluetoothLeBroadcast = this.mService;
        if (bluetoothLeBroadcast == null) {
            return false;
        }
        return !bluetoothLeBroadcast.getAllBroadcastMetadata().isEmpty();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfile
    public int getNameResource(BluetoothDevice bluetoothDevice) {
        return R$string.summary_empty;
    }

    protected void finalize() {
        Log.d("LocalBluetoothLeBroadcast", "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(26, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w("LocalBluetoothLeBroadcast", "Error cleaning up LeAudio proxy", th);
            }
        }
    }

    private String getDefaultValueOfProgramInfo() {
        int nextInt = ThreadLocalRandom.current().nextInt(AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP, 9999);
        return BluetoothAdapter.getDefaultAdapter().getName() + "_" + nextInt;
    }

    private byte[] getDefaultValueOfBroadcastCode() {
        return generateRandomPassword().getBytes(StandardCharsets.UTF_8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetCacheInfo() {
        Log.d("LocalBluetoothLeBroadcast", "resetCacheInfo:");
        this.mNewAppSourceName = "";
        this.mAppSourceName = "";
        this.mBluetoothLeBroadcastMetadata = null;
        this.mBroadcastId = -1;
    }

    private String generateRandomPassword() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13);
    }
}
