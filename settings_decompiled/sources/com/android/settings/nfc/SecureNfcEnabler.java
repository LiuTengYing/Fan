package com.android.settings.nfc;

import android.content.Context;
import android.os.UserManager;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class SecureNfcEnabler extends BaseNfcEnabler {
    private final SwitchPreference mPreference;
    private final UserManager mUserManager;

    public SecureNfcEnabler(Context context, SwitchPreference switchPreference) {
        super(context);
        this.mPreference = switchPreference;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    protected void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.setSummary(R$string.nfc_disabled_summary);
            this.mPreference.setEnabled(false);
        } else if (i == 2) {
            this.mPreference.setEnabled(false);
        } else if (i != 3) {
            if (i != 4) {
                return;
            }
            this.mPreference.setEnabled(false);
        } else {
            this.mPreference.setSummary(R$string.nfc_secure_toggle_summary);
            SwitchPreference switchPreference = this.mPreference;
            switchPreference.setChecked(switchPreference.isChecked());
            this.mPreference.setEnabled(isToggleable());
        }
    }

    private boolean isToggleable() {
        return !this.mUserManager.isGuestUser();
    }
}
