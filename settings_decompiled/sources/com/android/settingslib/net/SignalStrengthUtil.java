package com.android.settingslib.net;

import android.content.Context;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
/* loaded from: classes2.dex */
public class SignalStrengthUtil {
    public static boolean shouldInflateSignalStrength(Context context, int i) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        PersistableBundle configForSubId = carrierConfigManager != null ? carrierConfigManager.getConfigForSubId(i) : null;
        return configForSubId != null && configForSubId.getBoolean("inflate_signal_strength_bool", false);
    }
}
