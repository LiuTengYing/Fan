package com.android.settings.wifi.details2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.wifitrackerlib.WifiEntry;
/* loaded from: classes.dex */
public class AddDevicePreferenceController2 extends BasePreferenceController {
    private static final String KEY_ADD_DEVICE = "add_device_to_network";
    private static final String TAG = "AddDevicePreferenceController2";
    private WifiEntry mWifiEntry;
    private WifiManager mWifiManager;

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

    public AddDevicePreferenceController2(Context context) {
        super(context, KEY_ADD_DEVICE);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public void setWifiEntry(WifiEntry wifiEntry) {
        this.mWifiEntry = wifiEntry;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mWifiEntry.canEasyConnect() ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_ADD_DEVICE.equals(preference.getKey())) {
            WifiDppUtils.showLockScreen(this.mContext, new Runnable() { // from class: com.android.settings.wifi.details2.AddDevicePreferenceController2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AddDevicePreferenceController2.this.lambda$handlePreferenceTreeClick$0();
                }
            });
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: launchWifiDppConfiguratorQrCodeScanner */
    public void lambda$handlePreferenceTreeClick$0() {
        Intent configuratorQrCodeScannerIntentOrNull = WifiDppUtils.getConfiguratorQrCodeScannerIntentOrNull(this.mContext, this.mWifiManager, this.mWifiEntry);
        if (configuratorQrCodeScannerIntentOrNull == null) {
            Log.e(TAG, "Launch Wi-Fi QR code scanner with a wrong Wi-Fi network!");
        } else {
            this.mContext.startActivity(configuratorQrCodeScannerIntentOrNull);
        }
    }
}
