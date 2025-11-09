package com.android.settings.nfc;

import android.content.Context;
import android.provider.Settings;
import com.android.settingslib.widget.MainSwitchPreference;
/* loaded from: classes.dex */
public class NfcEnabler extends BaseNfcEnabler {
    private final MainSwitchPreference mPreference;

    public NfcEnabler(Context context, MainSwitchPreference mainSwitchPreference) {
        super(context);
        this.mPreference = mainSwitchPreference;
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    protected void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.updateStatus(false);
            this.mPreference.setEnabled(isToggleable());
        } else if (i == 2) {
            this.mPreference.updateStatus(true);
            this.mPreference.setEnabled(false);
        } else if (i == 3) {
            this.mPreference.updateStatus(true);
            this.mPreference.setEnabled(true);
        } else if (i != 4) {
        } else {
            this.mPreference.updateStatus(false);
            this.mPreference.setEnabled(false);
        }
    }

    boolean isToggleable() {
        return (!NfcPreferenceController.isToggleableInAirplaneMode(this.mContext) && NfcPreferenceController.shouldTurnOffNFCInAirplaneMode(this.mContext) && Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1) ? false : true;
    }
}
