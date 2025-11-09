package com.android.settingslib.enterprise;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes2.dex */
public class BiometricActionDisabledByAdminController extends BaseActionDisabledByAdminController {
    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public void setupLearnMoreButton(Context context) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BiometricActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider) {
        super(deviceAdminStringProvider);
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public String getAdminSupportTitle(String str) {
        return this.mStringProvider.getDisabledBiometricsParentConsentTitle();
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public CharSequence getAdminSupportContentString(Context context, CharSequence charSequence) {
        return this.mStringProvider.getDisabledBiometricsParentConsentContent();
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public DialogInterface.OnClickListener getPositiveButtonListener(final Context context, final RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        return new DialogInterface.OnClickListener() { // from class: com.android.settingslib.enterprise.BiometricActionDisabledByAdminController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BiometricActionDisabledByAdminController.lambda$getPositiveButtonListener$0(RestrictedLockUtils.EnforcedAdmin.this, context, dialogInterface, i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getPositiveButtonListener$0(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, Context context, DialogInterface dialogInterface, int i) {
        Log.d("BiometricActionDisabledByAdminController", "Positive button clicked, component: " + enforcedAdmin.component);
        context.startActivity(new Intent("android.settings.MANAGE_SUPERVISOR_RESTRICTED_SETTING").putExtra("android.provider.extra.SUPERVISOR_RESTRICTED_SETTING_KEY", 1).setPackage(enforcedAdmin.component.getPackageName()));
    }
}
