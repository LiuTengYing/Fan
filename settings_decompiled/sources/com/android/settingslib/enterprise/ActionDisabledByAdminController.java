package com.android.settingslib.enterprise;

import android.content.Context;
import android.content.DialogInterface;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes2.dex */
public interface ActionDisabledByAdminController {
    CharSequence getAdminSupportContentString(Context context, CharSequence charSequence);

    String getAdminSupportTitle(String str);

    default DialogInterface.OnClickListener getPositiveButtonListener(Context context, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        return null;
    }

    void initialize(ActionDisabledLearnMoreButtonLauncher actionDisabledLearnMoreButtonLauncher);

    void setupLearnMoreButton(Context context);

    void updateEnforcedAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, int i);
}
