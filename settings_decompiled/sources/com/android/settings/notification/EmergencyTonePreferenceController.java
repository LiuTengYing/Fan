package com.android.settings.notification;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import com.android.settings.R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class EmergencyTonePreferenceController extends SettingPrefController {
    public EmergencyTonePreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(1, "emergency_tone", "emergency_tone", 0, 1, 2, 0) { // from class: com.android.settings.notification.EmergencyTonePreferenceController.1
            @Override // com.android.settings.notification.SettingPref
            public boolean isApplicable(Context context2) {
                TelephonyManager telephonyManager = (TelephonyManager) context2.getSystemService("phone");
                return telephonyManager != null && telephonyManager.getCurrentPhoneType() == 2;
            }

            @Override // com.android.settings.notification.SettingPref
            protected String getCaption(Resources resources, int i) {
                if (i != 0) {
                    if (i != 1) {
                        if (i == 2) {
                            return resources.getString(R$string.emergency_tone_vibrate);
                        }
                        throw new IllegalArgumentException();
                    }
                    return resources.getString(R$string.emergency_tone_alert);
                }
                return resources.getString(R$string.emergency_tone_silent);
            }
        };
    }
}
