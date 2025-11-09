package com.android.settings.bluetooth;

import android.content.Context;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public class BluetoothDetailsMacAddressController extends BluetoothDetailsController {
    private FooterPreference mFooterPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_details_footer";
    }

    public BluetoothDetailsMacAddressController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference("device_details_footer");
        this.mFooterPreference = footerPreference;
        footerPreference.setTitle(((BluetoothDetailsController) this).mContext.getString(R$string.bluetooth_device_mac_address, this.mCachedDevice.getIdentityAddress()));
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        if (this.mCachedDevice.getGroupId() != -1) {
            StringBuilder sb = new StringBuilder(((BluetoothDetailsController) this).mContext.getString(R$string.bluetooth_multuple_devices_mac_address, this.mCachedDevice.getIdentityAddress()));
            for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevice.getMemberDevice()) {
                sb.append("\n");
                sb.append(cachedBluetoothDevice.getIdentityAddress());
            }
            this.mFooterPreference.setTitle(sb);
            return;
        }
        this.mFooterPreference.setTitle(((BluetoothDetailsController) this).mContext.getString(R$string.bluetooth_device_mac_address, this.mCachedDevice.getIdentityAddress()));
    }
}
