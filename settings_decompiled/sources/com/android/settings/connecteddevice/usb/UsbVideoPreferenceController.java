package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.BidiFormatter;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.SettingsApplication;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class UsbVideoPreferenceController extends BasePreferenceController {
    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public UsbVideoPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return BidiFormatter.getInstance().unicodeWrap(this.mContext.getString(R$string.common_usb_device_video_output));
    }

    public boolean getUSBConnectForProjection() {
        for (UsbDevice usbDevice : ((UsbManager) SettingsApplication.mApplication.getSystemService(UsbManager.class)).getDeviceList().values()) {
            int vendorId = usbDevice.getVendorId();
            int productId = usbDevice.getProductId();
            Log.i("fangli", "VendorId = " + vendorId);
            Log.i("fangli", "ProductId = " + productId);
            if (vendorId == 21325 && productId == 24609) {
                return true;
            }
        }
        return false;
    }
}
