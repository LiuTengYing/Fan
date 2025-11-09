package com.android.settings.connecteddevice.usb;

import android.app.Activity;
import android.content.Context;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class UsbDetailsHeaderController extends UsbDetailsController {
    private EntityHeaderController mHeaderController;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_device_header";
    }

    public UsbDetailsHeaderController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mHeaderController = EntityHeaderController.newInstance(this.mFragment.getActivity(), this.mFragment, ((LayoutPreference) preferenceScreen.findPreference("usb_device_header")).findViewById(R$id.entity_header));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController
    public void refresh(boolean z, long j, int i, int i2) {
        this.mHeaderController.setLabel(((UsbDetailsController) this).mContext.getString(R$string.usb_pref));
        this.mHeaderController.setIcon(((UsbDetailsController) this).mContext.getDrawable(R$drawable.ic_usb));
        this.mHeaderController.done((Activity) this.mFragment.getActivity(), true);
    }
}
