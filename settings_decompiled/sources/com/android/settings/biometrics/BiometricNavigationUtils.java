package com.android.settings.biometrics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class BiometricNavigationUtils {
    private final int mUserId;

    public BiometricNavigationUtils(int i) {
        this.mUserId = i;
    }

    public boolean launchBiometricSettings(Context context, String str, Bundle bundle) {
        Intent quietModeDialogIntent = getQuietModeDialogIntent(context);
        if (quietModeDialogIntent != null) {
            context.startActivity(quietModeDialogIntent);
            return false;
        }
        context.startActivity(getSettingsPageIntent(str, bundle));
        return true;
    }

    public Intent getBiometricSettingsIntent(Context context, String str, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, Bundle bundle) {
        if (enforcedAdmin != null) {
            return getRestrictedDialogIntent(context, enforcedAdmin);
        }
        Intent quietModeDialogIntent = getQuietModeDialogIntent(context);
        return quietModeDialogIntent != null ? quietModeDialogIntent : getSettingsPageIntent(str, bundle);
    }

    private Intent getQuietModeDialogIntent(Context context) {
        if (UserManager.get(context).isQuietModeEnabled(UserHandle.of(this.mUserId))) {
            return UnlaunchableAppActivity.createInQuietModeDialogIntent(this.mUserId);
        }
        return null;
    }

    private Intent getRestrictedDialogIntent(Context context, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        Intent showAdminSupportDetailsIntent = RestrictedLockUtils.getShowAdminSupportDetailsIntent(context, enforcedAdmin);
        int i = this.mUserId;
        UserHandle userHandle = enforcedAdmin.user;
        if (userHandle != null && RestrictedLockUtils.isCurrentUserOrProfile(context, userHandle.getIdentifier())) {
            i = enforcedAdmin.user.getIdentifier();
        }
        showAdminSupportDetailsIntent.putExtra("android.app.extra.RESTRICTION", enforcedAdmin.enforcedRestriction);
        showAdminSupportDetailsIntent.putExtra("android.intent.extra.USER_ID", i);
        return showAdminSupportDetailsIntent;
    }

    private Intent getSettingsPageIntent(String str, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", str);
        if (!bundle.isEmpty()) {
            intent.putExtras(bundle);
        }
        intent.putExtra("from_settings_summary", true);
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        intent.putExtra("page_transition_type", 1);
        return intent;
    }
}
