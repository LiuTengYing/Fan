package com.android.settings.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.UniWifiManager;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
/* loaded from: classes.dex */
public class UniWifiConnectedWarningPreferenceController extends TogglePreferenceController {
    private UniWifiManager mUniWifiManager;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public UniWifiConnectedWarningPreferenceController(Context context, String str) {
        super(context, str);
        this.mUniWifiManager = UniWifiManager.getInstance();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mUniWifiManager.isShowNotificationSupported() ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mUniWifiManager.isShowNotificationEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return this.mUniWifiManager.setShowNotificationEnabled(z);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_network;
    }
}
