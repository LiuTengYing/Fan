package com.android.settings.deviceinfo;

import android.content.Context;
import android.os.SystemProperties;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
/* loaded from: classes.dex */
public class HardwareRevisionPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "hardware_revision";
    }

    public HardwareRevisionPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R$bool.config_enableSoftwareAndHardwareVersion);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return SystemProperties.get("ro.boot.hardware.revision", this.mContext.getResources().getString(R$string.device_info_default));
    }
}
