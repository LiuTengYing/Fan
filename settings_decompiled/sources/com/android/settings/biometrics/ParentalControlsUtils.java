package com.android.settings.biometrics;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.biometrics.ParentalControlsUtilsInternal;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.RestrictedLockUtils;
/* loaded from: classes.dex */
public class ParentalControlsUtils {
    public static RestrictedLockUtils.EnforcedAdmin parentConsentRequired(Context context, int i) {
        int myUserId = UserHandle.myUserId();
        UserHandle userHandle = new UserHandle(myUserId);
        ComponentName testComponentName = ParentalControlsUtilsInternal.getTestComponentName(context, myUserId);
        if (testComponentName != null) {
            Log.d("ParentalControlsUtils", "Requiring consent for test flow");
            return new RestrictedLockUtils.EnforcedAdmin(testComponentName, "disallow_biometric", userHandle);
        }
        return parentConsentRequiredInternal((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class), i, userHandle);
    }

    @VisibleForTesting
    static RestrictedLockUtils.EnforcedAdmin parentConsentRequiredInternal(DevicePolicyManager devicePolicyManager, int i, UserHandle userHandle) {
        if (ParentalControlsUtilsInternal.parentConsentRequired(devicePolicyManager, i, userHandle)) {
            return new RestrictedLockUtils.EnforcedAdmin(ParentalControlsUtilsInternal.getSupervisionComponentName(devicePolicyManager, userHandle), "disallow_biometric", userHandle);
        }
        return null;
    }
}
