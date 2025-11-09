package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.hardware.SensorManager;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class SmartMotionPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public static final int[] SMARTMOTION_SENSOR_LIST = {65548, 65537, 25, 65546, 65539};

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "smart_motion";
    }

    public SmartMotionPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isSmartMotionAvailable(this.mContext);
    }

    public static boolean isSmartMotionAvailable(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        Boolean bool = Boolean.FALSE;
        if (sensorManager == null) {
            return false;
        }
        int i = 0;
        while (true) {
            int[] iArr = SMARTMOTION_SENSOR_LIST;
            if (i >= iArr.length) {
                return bool.booleanValue();
            }
            bool = Boolean.valueOf(bool.booleanValue() | (sensorManager.getDefaultSensor(iArr[i]) != null));
            i++;
        }
    }
}
