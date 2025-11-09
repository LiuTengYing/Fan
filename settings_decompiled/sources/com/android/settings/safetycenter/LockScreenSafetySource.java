package com.android.settings.safetycenter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.safetycenter.SafetyEvent;
import android.safetycenter.SafetySourceData;
import android.safetycenter.SafetySourceIssue;
import android.safetycenter.SafetySourceStatus;
import com.android.settings.R$string;
import com.android.settings.security.ScreenLockPreferenceDetailsUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
/* loaded from: classes.dex */
public final class LockScreenSafetySource {
    public static void setSafetySourceData(Context context, ScreenLockPreferenceDetailsUtils screenLockPreferenceDetailsUtils, SafetyEvent safetyEvent) {
        if (SafetyCenterManagerWrapper.get().isEnabled(context) && screenLockPreferenceDetailsUtils.isAvailable()) {
            int myUserId = UserHandle.myUserId();
            RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet = RestrictedLockUtilsInternal.checkIfPasswordQualityIsSet(context, myUserId);
            PendingIntent createPendingIntent = createPendingIntent(context, screenLockPreferenceDetailsUtils.getLaunchChooseLockGenericFragmentIntent(1917));
            SafetySourceStatus.IconAction createGearMenuIconAction = createGearMenuIconAction(context, screenLockPreferenceDetailsUtils);
            boolean z = !screenLockPreferenceDetailsUtils.isPasswordQualityManaged(myUserId, checkIfPasswordQualityIsSet);
            boolean isLockPatternSecure = screenLockPreferenceDetailsUtils.isLockPatternSecure();
            SafetySourceData.Builder status = new SafetySourceData.Builder().setStatus(new SafetySourceStatus.Builder(context.getString(R$string.unlock_set_unlock_launch_picker_title), screenLockPreferenceDetailsUtils.getSummary(UserHandle.myUserId()), z ? isLockPatternSecure ? 200 : 300 : 100).setPendingIntent(createPendingIntent).setEnabled(z).setIconAction(createGearMenuIconAction).build());
            if (z && !isLockPatternSecure) {
                status.addIssue(createNoScreenLockIssue(context, createPendingIntent));
            }
            SafetyCenterManagerWrapper.get().setSafetySourceData(context, "AndroidLockScreen", status.build(), safetyEvent);
        }
    }

    public static void onLockScreenChange(Context context) {
        if (context != null) {
            setSafetySourceData(context, new ScreenLockPreferenceDetailsUtils(context), new SafetyEvent.Builder(100).build());
            BiometricsSafetySource.onBiometricsChanged(context);
        }
    }

    private static SafetySourceStatus.IconAction createGearMenuIconAction(Context context, ScreenLockPreferenceDetailsUtils screenLockPreferenceDetailsUtils) {
        if (screenLockPreferenceDetailsUtils.shouldShowGearMenu()) {
            return new SafetySourceStatus.IconAction(100, createPendingIntent(context, screenLockPreferenceDetailsUtils.getLaunchScreenLockSettingsIntent(1917)));
        }
        return null;
    }

    private static PendingIntent createPendingIntent(Context context, Intent intent) {
        return PendingIntent.getActivity(context, 0, intent, 67108864);
    }

    private static SafetySourceIssue createNoScreenLockIssue(Context context, PendingIntent pendingIntent) {
        return new SafetySourceIssue.Builder("NoScreenLockIssue", context.getString(R$string.no_screen_lock_issue_title), context.getString(R$string.no_screen_lock_issue_summary), 300, "NoScreenLockIssueType").setIssueCategory(100).addAction(new SafetySourceIssue.Action.Builder("SetScreenLockAction", context.getString(R$string.no_screen_lock_issue_action_label), pendingIntent).build()).build();
    }
}
