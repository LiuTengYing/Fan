package com.android.settings.wifi.calling;

import android.content.Context;
import android.os.PersistableBundle;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
/* loaded from: classes.dex */
class LocationPolicyDisclaimer extends DisclaimerItem {
    @VisibleForTesting
    static final String KEY_HAS_AGREED_LOCATION_DISCLAIMER = "key_has_agreed_location_disclaimer";

    @Override // com.android.settings.wifi.calling.DisclaimerItem
    protected String getName() {
        return "LocationPolicyDisclaimer";
    }

    @Override // com.android.settings.wifi.calling.DisclaimerItem
    protected String getPrefKey() {
        return KEY_HAS_AGREED_LOCATION_DISCLAIMER;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationPolicyDisclaimer(Context context, int i) {
        super(context, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public boolean shouldShow() {
        PersistableBundle carrierConfig = getCarrierConfig();
        if (!carrierConfig.getBoolean("show_wfc_location_privacy_policy_bool")) {
            logd("shouldShow: false due to carrier config is false.");
            return false;
        } else if (carrierConfig.getBoolean("carrier_default_wfc_ims_enabled_bool")) {
            logd("shouldShow: false due to WFC is on as default.");
            return false;
        } else {
            return super.shouldShow();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public int getTitleId() {
        return R$string.wfc_disclaimer_location_title_text;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public int getMessageId() {
        return R$string.wfc_disclaimer_location_desc_text;
    }
}
