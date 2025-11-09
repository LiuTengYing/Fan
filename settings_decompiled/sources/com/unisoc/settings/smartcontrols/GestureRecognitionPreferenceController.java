package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.os.SystemProperties;
import com.android.settings.R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes2.dex */
public class GestureRecognitionPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private static String isSupportGestureRecognition = SystemProperties.get("persist.vendor.gesture_recognition.supported", "false");

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "gesture_recognition";
    }

    public GestureRecognitionPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isPocketModeAvailable(this.mContext);
    }

    public static boolean isPocketModeAvailable(Context context) {
        return isSupportGestureRecognition.equals("true") && context.getResources().getBoolean(R$bool.config_enable_gesture_recognition);
    }
}
