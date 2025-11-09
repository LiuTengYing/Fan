package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.text.BidiFormatter;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.wifi.WifiUtils;
/* loaded from: classes.dex */
public class TopLevelNetworkEntryPreferenceController extends BasePreferenceController {
    private final MobileNetworkPreferenceController mMobileNetworkPreferenceController;

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

    public TopLevelNetworkEntryPreferenceController(Context context, String str) {
        super(context, str);
        this.mMobileNetworkPreferenceController = new MobileNetworkPreferenceController(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (Utils.isDemoUser(this.mContext) || !(this.mMobileNetworkPreferenceController.isAvailable() || WifiUtils.hasSystemFeature(this.mContext, "android.hardware.wifi"))) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mMobileNetworkPreferenceController.isAvailable() && WifiUtils.hasSystemFeature(this.mContext, "android.hardware.wifi")) {
            return BidiFormatter.getInstance().unicodeWrap(this.mContext.getString(R$string.network_dashboard_summary_mobile));
        }
        if (this.mMobileNetworkPreferenceController.isAvailable()) {
            return BidiFormatter.getInstance().unicodeWrap(this.mContext.getString(R$string.network_settings_title));
        }
        return BidiFormatter.getInstance().unicodeWrap(this.mContext.getString(R$string.network_dashboard_summary_no_mobile));
    }
}
