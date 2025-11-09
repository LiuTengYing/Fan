package com.android.settings.connecteddevice.usb;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class UsbDetailsPowerRoleController extends UsbDetailsController implements Preference.OnPreferenceClickListener {
    private final Runnable mFailureCallback;
    private int mNextPowerRole;
    private PreferenceCategory mPreferenceCategory;
    private SwitchPreference mSwitchPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_details_power_role";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mNextPowerRole != 0) {
            this.mSwitchPreference.setSummary(R$string.usb_switching_failed);
            this.mNextPowerRole = 0;
        }
    }

    public UsbDetailsPowerRoleController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
        this.mFailureCallback = new Runnable() { // from class: com.android.settings.connecteddevice.usb.UsbDetailsPowerRoleController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UsbDetailsPowerRoleController.this.lambda$new$0();
            }
        };
        this.mNextPowerRole = 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        SwitchPreference switchPreference = new SwitchPreference(preferenceCategory.getContext());
        this.mSwitchPreference = switchPreference;
        switchPreference.setTitle(R$string.usb_use_power_only);
        this.mSwitchPreference.setOnPreferenceClickListener(this);
        this.mPreferenceCategory.addPreference(this.mSwitchPreference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController
    public void refresh(boolean z, long j, int i, int i2) {
        if (z && !this.mUsbBackend.areAllRolesSupported()) {
            this.mFragment.getPreferenceScreen().removePreference(this.mPreferenceCategory);
        } else if (z && this.mUsbBackend.areAllRolesSupported()) {
            this.mFragment.getPreferenceScreen().addPreference(this.mPreferenceCategory);
        }
        if (i == 1) {
            this.mSwitchPreference.setChecked(true);
            this.mPreferenceCategory.setEnabled(true);
        } else if (i == 2) {
            this.mSwitchPreference.setChecked(false);
            this.mPreferenceCategory.setEnabled(true);
        } else if (!z || i == 0) {
            this.mPreferenceCategory.setEnabled(false);
            if (this.mNextPowerRole == 0) {
                this.mSwitchPreference.setSummary("");
            }
        }
        int i3 = this.mNextPowerRole;
        if (i3 == 0 || i == 0) {
            return;
        }
        if (i3 == i) {
            this.mSwitchPreference.setSummary("");
        } else {
            this.mSwitchPreference.setSummary(R$string.usb_switching_failed);
        }
        this.mNextPowerRole = 0;
        this.mHandler.removeCallbacks(this.mFailureCallback);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        int i = this.mSwitchPreference.isChecked() ? 1 : 2;
        if (this.mUsbBackend.getPowerRole() != i && this.mNextPowerRole == 0 && !Utils.isMonkeyRunning()) {
            this.mUsbBackend.setPowerRole(i);
            this.mNextPowerRole = i;
            this.mSwitchPreference.setSummary(R$string.usb_switching);
            this.mHandler.postDelayed(this.mFailureCallback, this.mUsbBackend.areAllRolesSupported() ? 4000L : 15000L);
        }
        SwitchPreference switchPreference = this.mSwitchPreference;
        switchPreference.setChecked(!switchPreference.isChecked());
        return true;
    }

    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Utils.isMonkeyRunning();
    }
}
