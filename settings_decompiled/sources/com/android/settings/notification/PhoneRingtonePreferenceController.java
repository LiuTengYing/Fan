package com.android.settings.notification;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.android.settings.R$bool;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class PhoneRingtonePreferenceController extends RingtonePreferenceControllerBase {
    private String mKey;

    public PhoneRingtonePreferenceController(Context context) {
        super(context);
    }

    public PhoneRingtonePreferenceController(Context context, String str) {
        this(context);
        this.mKey = str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mKey;
    }

    @Override // com.android.settings.notification.RingtonePreferenceControllerBase, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return Utils.isVoiceCapable(this.mContext) && (!isDualSIMRingtoneSupported(this.mContext) ? !(isDualSIMRingtoneSupported(this.mContext) || !"phone_ringtone".equals(this.mKey)) : !(!"phone_ringtone1".equals(this.mKey) && !"phone_ringtone2".equals(this.mKey)));
    }

    @Override // com.android.settings.notification.RingtonePreferenceControllerBase
    public int getRingtoneType() {
        return (isDualSIMRingtoneSupported(this.mContext) && "phone_ringtone2".equals(this.mKey)) ? 8 : 1;
    }

    private static boolean isDualSIMRingtoneSupported(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_dual_SIM_card_ringtone) && ((TelephonyManager) context.getSystemService("phone")).getPhoneCount() >= 2;
    }
}
