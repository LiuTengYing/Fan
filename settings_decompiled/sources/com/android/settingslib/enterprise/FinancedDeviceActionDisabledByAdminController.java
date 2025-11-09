package com.android.settingslib.enterprise;

import android.content.Context;
/* loaded from: classes2.dex */
final class FinancedDeviceActionDisabledByAdminController extends BaseActionDisabledByAdminController {
    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public CharSequence getAdminSupportContentString(Context context, CharSequence charSequence) {
        return charSequence;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FinancedDeviceActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider) {
        super(deviceAdminStringProvider);
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public void setupLearnMoreButton(Context context) {
        assertInitialized();
        this.mLauncher.setupLearnMoreButtonToShowAdminPolicies(context, this.mEnforcementAdminUserId, this.mEnforcedAdmin);
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public String getAdminSupportTitle(String str) {
        return this.mStringProvider.getDisabledByPolicyTitleForFinancedDevice();
    }
}
