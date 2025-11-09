package com.android.settings.nfc;

import android.content.Context;
import android.os.UserHandle;
import com.android.settings.R$string;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class AndroidBeamEnabler extends BaseNfcEnabler {
    private final boolean mBeamDisallowedBySystem;
    private final RestrictedPreference mPreference;

    public AndroidBeamEnabler(Context context, RestrictedPreference restrictedPreference) {
        super(context);
        this.mPreference = restrictedPreference;
        boolean hasBaseUserRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_outgoing_beam", UserHandle.myUserId());
        this.mBeamDisallowedBySystem = hasBaseUserRestriction;
        if (!isNfcAvailable()) {
            restrictedPreference.setEnabled(false);
        } else if (hasBaseUserRestriction) {
            restrictedPreference.setEnabled(false);
        }
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    protected void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.setEnabled(false);
            this.mPreference.setSummary(R$string.nfc_disabled_summary);
        } else if (i == 2) {
            this.mPreference.setEnabled(false);
        } else if (i != 3) {
            if (i != 4) {
                return;
            }
            this.mPreference.setEnabled(false);
        } else {
            if (this.mBeamDisallowedBySystem) {
                this.mPreference.setDisabledByAdmin(null);
                this.mPreference.setEnabled(false);
            } else {
                this.mPreference.checkRestrictionAndSetDisabled("no_outgoing_beam");
            }
            if (this.mNfcAdapter.isNdefPushEnabled() && this.mPreference.isEnabled()) {
                this.mPreference.setSummary(R$string.android_beam_on_summary);
            } else {
                this.mPreference.setSummary(R$string.android_beam_off_summary);
            }
        }
    }
}
