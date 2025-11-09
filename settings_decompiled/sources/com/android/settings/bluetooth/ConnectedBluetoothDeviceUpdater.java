package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.connecteddevice.DevicePreferenceCallback;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
/* loaded from: classes.dex */
public class ConnectedBluetoothDeviceUpdater extends BluetoothDeviceUpdater {
    private static final boolean DBG = Log.isLoggable("ConnBluetoothDeviceUpdater", 3);
    private final AudioManager mAudioManager;

    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    protected String getPreferenceKey() {
        return "connected_bt";
    }

    public ConnectedBluetoothDeviceUpdater(Context context, DashboardFragment dashboardFragment, DevicePreferenceCallback devicePreferenceCallback) {
        super(context, dashboardFragment, devicePreferenceCallback);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAudioModeChanged() {
        forceUpdate();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0053  */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isFilterMatched(com.android.settingslib.bluetooth.CachedBluetoothDevice r8) {
        /*
            r7 = this;
            android.media.AudioManager r0 = r7.mAudioManager
            int r0 = r0.getMode()
            r1 = 2
            r2 = 1
            if (r0 == r2) goto L12
            if (r0 == r1) goto L12
            r3 = 3
            if (r0 != r3) goto L10
            goto L12
        L10:
            r0 = r1
            goto L13
        L12:
            r0 = r2
        L13:
            boolean r7 = r7.isDeviceConnected(r8)
            r3 = 0
            if (r7 == 0) goto L74
            boolean r7 = com.android.settings.bluetooth.ConnectedBluetoothDeviceUpdater.DBG
            java.lang.String r4 = "ConnBluetoothDeviceUpdater"
            if (r7 == 0) goto L34
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "isFilterMatched() current audio profile : "
            r5.append(r6)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r4, r5)
        L34:
            boolean r5 = r8.isConnectedHearingAidDevice()
            if (r5 != 0) goto L74
            boolean r5 = r8.isConnectedLeAudioDevice()
            if (r5 == 0) goto L41
            goto L74
        L41:
            if (r0 == r2) goto L4b
            if (r0 == r1) goto L46
            goto L51
        L46:
            boolean r0 = r8.isConnectedA2dpDevice()
            goto L4f
        L4b:
            boolean r0 = r8.isConnectedHfpDevice()
        L4f:
            r0 = r0 ^ r2
            r3 = r0
        L51:
            if (r7 == 0) goto L74
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r0 = "isFilterMatched() device : "
            r7.append(r0)
            java.lang.String r8 = r8.getName()
            r7.append(r8)
            java.lang.String r8 = ", isFilterMatched : "
            r7.append(r8)
            r7.append(r3)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r4, r7)
        L74:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.bluetooth.ConnectedBluetoothDeviceUpdater.isFilterMatched(com.android.settingslib.bluetooth.CachedBluetoothDevice):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDeviceUpdater
    public void addPreference(CachedBluetoothDevice cachedBluetoothDevice) {
        super.addPreference(cachedBluetoothDevice);
        BluetoothDevice device = cachedBluetoothDevice.getDevice();
        if (this.mPreferenceMap.containsKey(device)) {
            BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) this.mPreferenceMap.get(device);
            bluetoothDevicePreference.setOnGearClickListener(null);
            bluetoothDevicePreference.hideSecondTarget(true);
            bluetoothDevicePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.ConnectedBluetoothDeviceUpdater$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$addPreference$0;
                    lambda$addPreference$0 = ConnectedBluetoothDeviceUpdater.this.lambda$addPreference$0(preference);
                    return lambda$addPreference$0;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$addPreference$0(Preference preference) {
        lambda$new$0(preference);
        return true;
    }
}
