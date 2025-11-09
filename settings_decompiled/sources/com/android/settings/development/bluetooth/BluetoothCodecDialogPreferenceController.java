package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class BluetoothCodecDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    private final AbstractBluetoothPreferenceController.Callback mCallback;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_audio_codec_settings";
    }

    public BluetoothCodecDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore, AbstractBluetoothPreferenceController.Callback callback) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
        this.mCallback = callback;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((BaseBluetoothDialogPreference) this.mPreference).setCallback(this);
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        List<BluetoothCodecConfig> selectableConfigs;
        ArrayList arrayList = new ArrayList();
        BluetoothA2dp bluetoothA2dp = this.mBluetoothA2dp;
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        if (bluetoothA2dp == null) {
            return arrayList;
        }
        BluetoothDevice a2dpActiveDevice = getA2dpActiveDevice();
        if (a2dpActiveDevice == null) {
            Log.d("BtCodecCtr", "Unable to get selectable index. No Active Bluetooth device");
            return arrayList;
        } else if (bluetoothA2dp.isOptionalCodecsEnabled(a2dpActiveDevice) == 1 && (selectableConfigs = getSelectableConfigs(a2dpActiveDevice)) != null) {
            return getIndexFromConfig(selectableConfigs);
        } else {
            arrayList.add(Integer.valueOf(convertCfgToBtnIndex(0)));
            return arrayList;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x003e  */
    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void writeConfigurationValues(int r7) {
        /*
            r6 = this;
            r0 = 4
            r1 = 3
            r2 = 2
            r3 = 1
            r4 = 0
            r5 = 1000000(0xf4240, float:1.401298E-39)
            if (r7 == 0) goto L1f
            if (r7 == r3) goto L1d
            if (r7 == r2) goto L1b
            if (r7 == r1) goto L19
            if (r7 == r0) goto L17
            r1 = 5
            if (r7 == r1) goto L2d
            r0 = r4
            goto L2e
        L17:
            r0 = r1
            goto L2d
        L19:
            r0 = r2
            goto L2d
        L1b:
            r0 = r3
            goto L2d
        L1d:
            r0 = r4
            goto L2d
        L1f:
            android.bluetooth.BluetoothDevice r7 = r6.getA2dpActiveDevice()
            android.bluetooth.BluetoothA2dp r0 = r6.mBluetoothA2dp
            java.util.List r1 = r6.getSelectableConfigs(r7)
            int r0 = com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController.getHighestCodec(r0, r7, r1)
        L2d:
            r4 = r5
        L2e:
            com.android.settings.development.BluetoothA2dpConfigStore r7 = r6.mBluetoothA2dpConfigStore
            r7.setCodecType(r0)
            com.android.settings.development.BluetoothA2dpConfigStore r7 = r6.mBluetoothA2dpConfigStore
            r7.setCodecPriority(r4)
            android.bluetooth.BluetoothCodecConfig r7 = r6.getSelectableByCodecType(r0)
            if (r7 != 0) goto L45
            java.lang.String r0 = "BtCodecCtr"
            java.lang.String r1 = "Selectable config is null. Unable to reset"
            android.util.Log.d(r0, r1)
        L45:
            com.android.settings.development.BluetoothA2dpConfigStore r0 = r6.mBluetoothA2dpConfigStore
            int r1 = com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController.getHighestSampleRate(r7)
            r0.setSampleRate(r1)
            com.android.settings.development.BluetoothA2dpConfigStore r0 = r6.mBluetoothA2dpConfigStore
            int r1 = com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController.getHighestBitsPerSample(r7)
            r0.setBitsPerSample(r1)
            com.android.settings.development.BluetoothA2dpConfigStore r6 = r6.mBluetoothA2dpConfigStore
            int r7 = com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController.getHighestChannelMode(r7)
            r6.setChannelMode(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.bluetooth.BluetoothCodecDialogPreferenceController.writeConfigurationValues(int):void");
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    protected int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtCodecCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getCodecType());
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController, com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public void onIndexUpdated(int i) {
        super.onIndexUpdated(i);
        this.mCallback.onBluetoothCodecChanged();
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    public void onHDAudioEnabled(boolean z) {
        writeConfigurationValues(0);
    }

    private List<Integer> getIndexFromConfig(List<BluetoothCodecConfig> list) {
        ArrayList arrayList = new ArrayList();
        for (BluetoothCodecConfig bluetoothCodecConfig : list) {
            arrayList.add(Integer.valueOf(convertCfgToBtnIndex(bluetoothCodecConfig.getCodecType())));
        }
        return arrayList;
    }

    int convertCfgToBtnIndex(int i) {
        int defaultIndex = getDefaultIndex();
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            Log.e("BtCodecCtr", "Unsupported config:" + i);
                            return defaultIndex;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }
}
