package com.android.settings.applications.specialaccess;

import android.content.Context;
import android.nfc.cardemulation.CardEmulation;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.nfc.BaseNfcEnabler;
/* loaded from: classes.dex */
public class PaymentSettingsEnabler extends BaseNfcEnabler {
    private final CardEmulation mCardEmuManager;
    boolean mIsPaymentAvailable;
    private final Preference mPreference;

    public PaymentSettingsEnabler(Context context, Preference preference) {
        super(context);
        this.mCardEmuManager = CardEmulation.getInstance(this.mNfcAdapter);
        this.mIsPaymentAvailable = false;
        this.mPreference = preference;
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    protected void handleNfcStateChanged(int i) {
        if (i == 1) {
            this.mPreference.setSummary(R$string.nfc_and_payment_settings_payment_off_nfc_off_summary);
            this.mPreference.setEnabled(false);
        } else if (i != 3) {
        } else {
            if (this.mIsPaymentAvailable) {
                this.mPreference.setSummary((CharSequence) null);
                this.mPreference.setEnabled(true);
                return;
            }
            this.mPreference.setSummary(R$string.nfc_and_payment_settings_no_payment_installed_summary);
            this.mPreference.setEnabled(false);
        }
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    public void resume() {
        if (isNfcAvailable()) {
            if (this.mCardEmuManager.getServices("payment").isEmpty()) {
                this.mIsPaymentAvailable = false;
            } else {
                this.mIsPaymentAvailable = true;
            }
            super.resume();
        }
    }
}
