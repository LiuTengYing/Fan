package com.android.settings.wifi.calling;

import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class EmergencyCallLimitationDisclaimer extends DisclaimerItem {
    @VisibleForTesting
    static final String KEY_HAS_AGREED_EMERGENCY_LIMITATION_DISCLAIMER = "key_has_agreed_emergency_limitation_disclaimer";

    @Override // com.android.settings.wifi.calling.DisclaimerItem
    protected String getName() {
        return "EmergencyCallLimitationDisclaimer";
    }

    @Override // com.android.settings.wifi.calling.DisclaimerItem
    protected String getPrefKey() {
        return KEY_HAS_AGREED_EMERGENCY_LIMITATION_DISCLAIMER;
    }

    public EmergencyCallLimitationDisclaimer(Context context, int i) {
        super(context, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public boolean shouldShow() {
        if (getCarrierConfig().getInt("emergency_notification_delay_int") == -1) {
            logd("shouldShow: false due to carrier config is default(-1).");
            return false;
        }
        return super.shouldShow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public int getTitleId() {
        return R$string.wfc_disclaimer_emergency_limitation_title_text;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.calling.DisclaimerItem
    public int getMessageId() {
        return R$string.wfc_disclaimer_emergency_limitation_desc_text;
    }
}
