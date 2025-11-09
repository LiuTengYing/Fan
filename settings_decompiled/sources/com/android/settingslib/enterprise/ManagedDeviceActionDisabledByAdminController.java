package com.android.settingslib.enterprise;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher;
import java.util.Objects;
/* loaded from: classes2.dex */
final class ManagedDeviceActionDisabledByAdminController extends BaseActionDisabledByAdminController {
    public static final ForegroundUserChecker DEFAULT_FOREGROUND_USER_CHECKER = new ForegroundUserChecker() { // from class: com.android.settingslib.enterprise.ManagedDeviceActionDisabledByAdminController$$ExternalSyntheticLambda0
        @Override // com.android.settingslib.enterprise.ManagedDeviceActionDisabledByAdminController.ForegroundUserChecker
        public final boolean isUserForeground(Context context, UserHandle userHandle) {
            boolean isUserForeground;
            isUserForeground = ManagedDeviceActionDisabledByAdminController.isUserForeground(context, userHandle);
            return isUserForeground;
        }
    };
    private final ForegroundUserChecker mForegroundUserChecker;
    private final UserHandle mPreferredUserHandle;
    private final ActionDisabledLearnMoreButtonLauncher.ResolveActivityChecker mResolveActivityChecker;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface ForegroundUserChecker {
        boolean isUserForeground(Context context, UserHandle userHandle);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ManagedDeviceActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider, UserHandle userHandle, ForegroundUserChecker foregroundUserChecker, ActionDisabledLearnMoreButtonLauncher.ResolveActivityChecker resolveActivityChecker) {
        super(deviceAdminStringProvider);
        Objects.requireNonNull(userHandle);
        this.mPreferredUserHandle = userHandle;
        Objects.requireNonNull(foregroundUserChecker);
        this.mForegroundUserChecker = foregroundUserChecker;
        Objects.requireNonNull(resolveActivityChecker);
        this.mResolveActivityChecker = resolveActivityChecker;
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public void setupLearnMoreButton(Context context) {
        assertInitialized();
        String learnMoreHelpPageUrl = this.mStringProvider.getLearnMoreHelpPageUrl();
        if (!TextUtils.isEmpty(learnMoreHelpPageUrl) && canLaunchHelpPageInPreferredOrCurrentUser(context, learnMoreHelpPageUrl, this.mPreferredUserHandle)) {
            setupLearnMoreButtonToLaunchHelpPage(context, learnMoreHelpPageUrl, this.mPreferredUserHandle);
        } else {
            this.mLauncher.setupLearnMoreButtonToShowAdminPolicies(context, this.mEnforcementAdminUserId, this.mEnforcedAdmin);
        }
    }

    private boolean canLaunchHelpPageInPreferredOrCurrentUser(Context context, String str, UserHandle userHandle) {
        PackageManager packageManager = context.getPackageManager();
        if (this.mLauncher.canLaunchHelpPage(packageManager, str, userHandle, this.mResolveActivityChecker) && this.mForegroundUserChecker.isUserForeground(context, userHandle)) {
            return true;
        }
        return this.mLauncher.canLaunchHelpPage(packageManager, str, context.getUser(), this.mResolveActivityChecker);
    }

    private void setupLearnMoreButtonToLaunchHelpPage(Context context, String str, UserHandle userHandle) {
        PackageManager packageManager = context.getPackageManager();
        if (this.mLauncher.canLaunchHelpPage(packageManager, str, userHandle, this.mResolveActivityChecker) && this.mForegroundUserChecker.isUserForeground(context, userHandle)) {
            this.mLauncher.setupLearnMoreButtonToLaunchHelpPage(context, str, userHandle);
        }
        if (this.mLauncher.canLaunchHelpPage(packageManager, str, context.getUser(), this.mResolveActivityChecker)) {
            this.mLauncher.setupLearnMoreButtonToLaunchHelpPage(context, str, context.getUser());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isUserForeground(Context context, UserHandle userHandle) {
        return ((UserManager) context.createContextAsUser(userHandle, 0).getSystemService(UserManager.class)).isUserForeground();
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public String getAdminSupportTitle(String str) {
        if (str == null) {
            return this.mStringProvider.getDefaultDisabledByPolicyTitle();
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -1040305701:
                if (str.equals("no_sms")) {
                    c = 0;
                    break;
                }
                break;
            case -932215031:
                if (str.equals("policy_disable_camera")) {
                    c = 1;
                    break;
                }
                break;
            case 620339799:
                if (str.equals("policy_disable_screen_capture")) {
                    c = 2;
                    break;
                }
                break;
            case 1416425725:
                if (str.equals("policy_suspend_packages")) {
                    c = 3;
                    break;
                }
                break;
            case 1950494080:
                if (str.equals("no_outgoing_calls")) {
                    c = 4;
                    break;
                }
                break;
            case 2135693260:
                if (str.equals("no_adjust_volume")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return this.mStringProvider.getDisallowSmsTitle();
            case 1:
                return this.mStringProvider.getDisableCameraTitle();
            case 2:
                return this.mStringProvider.getDisableScreenCaptureTitle();
            case 3:
                return this.mStringProvider.getSuspendPackagesTitle();
            case 4:
                return this.mStringProvider.getDisallowOutgoingCallsTitle();
            case 5:
                return this.mStringProvider.getDisallowAdjustVolumeTitle();
            default:
                return this.mStringProvider.getDefaultDisabledByPolicyTitle();
        }
    }

    @Override // com.android.settingslib.enterprise.ActionDisabledByAdminController
    public CharSequence getAdminSupportContentString(Context context, CharSequence charSequence) {
        return charSequence != null ? charSequence : this.mStringProvider.getDefaultDisabledByPolicyContent();
    }
}
