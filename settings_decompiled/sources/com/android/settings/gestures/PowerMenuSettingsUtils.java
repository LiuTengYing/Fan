package com.android.settings.gestures;

import android.content.Context;
import android.provider.Settings;
/* loaded from: classes.dex */
final class PowerMenuSettingsUtils {
    public static int getPowerButtonSettingValue(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "power_button_long_press", context.getResources().getInteger(17694854));
    }

    public static boolean isLongPressPowerForAssistEnabled(Context context) {
        return getPowerButtonSettingValue(context) == 5;
    }
}
