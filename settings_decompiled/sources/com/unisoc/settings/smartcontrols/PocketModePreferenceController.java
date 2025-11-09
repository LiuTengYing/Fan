package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.hardware.SensorManager;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class PocketModePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pocket_mode";
    }

    public PocketModePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isPocketModeAvailable(this.mContext);
    }

    public static boolean isPocketModeAvailable(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        return (sensorManager == null || sensorManager.getDefaultSensor(65538) == null) ? false : true;
    }
}
