package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.BluetoothServiceConnectionListener;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.List;
/* loaded from: classes.dex */
public abstract class AbstractBluetoothPreferenceController extends DeveloperOptionsPreferenceController implements BluetoothServiceConnectionListener, LifecycleObserver, OnDestroy, PreferenceControllerMixin {
    protected volatile BluetoothA2dp mBluetoothA2dp;
    BluetoothAdapter mBluetoothAdapter;

    /* loaded from: classes.dex */
    public interface Callback {
        void onBluetoothCodecChanged();

        void onBluetoothHDAudioEnabled(boolean z);
    }

    public AbstractBluetoothPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mBluetoothAdapter = ((BluetoothManager) context.getSystemService(BluetoothManager.class)).getAdapter();
    }

    public void onBluetoothServiceConnected(BluetoothA2dp bluetoothA2dp) {
        this.mBluetoothA2dp = bluetoothA2dp;
        updateState(this.mPreference);
    }

    @Override // com.android.settings.development.BluetoothServiceConnectionListener
    public void onBluetoothCodecUpdated() {
        updateState(this.mPreference);
    }

    @Override // com.android.settings.development.BluetoothServiceConnectionListener
    public void onBluetoothServiceDisconnected() {
        this.mBluetoothA2dp = null;
        updateState(this.mPreference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mBluetoothA2dp = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BluetoothDevice getA2dpActiveDevice() {
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
}
