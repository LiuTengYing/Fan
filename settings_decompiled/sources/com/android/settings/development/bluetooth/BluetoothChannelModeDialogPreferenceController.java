package com.android.settings.development.bluetooth;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.development.BluetoothA2dpConfigStore;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class BluetoothChannelModeDialogPreferenceController extends AbstractBluetoothDialogPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_channel_mode_settings";
    }

    public BluetoothChannelModeDialogPreferenceController(Context context, Lifecycle lifecycle, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        super(context, lifecycle, bluetoothA2dpConfigStore);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((BaseBluetoothDialogPreference) this.mPreference).setCallback(this);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0006, code lost:
        if (r3 != 2) goto L8;
     */
    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void writeConfigurationValues(int r3) {
        /*
            r2 = this;
            r0 = 2
            r1 = 1
            if (r3 == 0) goto Lb
            if (r3 == r1) goto L9
            if (r3 == r0) goto L1f
            goto L1e
        L9:
            r0 = r1
            goto L1f
        Lb:
            android.bluetooth.BluetoothCodecConfig r3 = r2.getCurrentCodecConfig()
            if (r3 == 0) goto L1e
            int r3 = r3.getCodecType()
            android.bluetooth.BluetoothCodecConfig r3 = r2.getSelectableByCodecType(r3)
            int r0 = com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController.getHighestChannelMode(r3)
            goto L1f
        L1e:
            r0 = 0
        L1f:
            com.android.settings.development.BluetoothA2dpConfigStore r2 = r2.mBluetoothA2dpConfigStore
            r2.setChannelMode(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.bluetooth.BluetoothChannelModeDialogPreferenceController.writeConfigurationValues(int):void");
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController
    protected int getCurrentIndexByConfig(BluetoothCodecConfig bluetoothCodecConfig) {
        if (bluetoothCodecConfig == null) {
            Log.e("BtChannelModeCtr", "Unable to get current config index. Config is null.");
        }
        return convertCfgToBtnIndex(bluetoothCodecConfig.getChannelMode());
    }

    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference.Callback
    public List<Integer> getSelectableIndex() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(getDefaultIndex()));
        BluetoothCodecConfig currentCodecConfig = getCurrentCodecConfig();
        if (currentCodecConfig != null) {
            int channelMode = getSelectableByCodecType(currentCodecConfig.getCodecType()).getChannelMode();
            int i = 0;
            while (true) {
                int[] iArr = AbstractBluetoothDialogPreferenceController.CHANNEL_MODES;
                if (i >= iArr.length) {
                    break;
                }
                int i2 = iArr[i];
                if ((channelMode & i2) != 0) {
                    arrayList.add(Integer.valueOf(convertCfgToBtnIndex(i2)));
                }
                i++;
            }
        }
        return arrayList;
    }

    int convertCfgToBtnIndex(int i) {
        int defaultIndex = getDefaultIndex();
        if (i != 1) {
            if (i != 2) {
                Log.e("BtChannelModeCtr", "Unsupported config:" + i);
                return defaultIndex;
            }
            return 2;
        }
        return 1;
    }
}
