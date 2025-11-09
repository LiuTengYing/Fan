package com.android.settingslib.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.RestrictedLockUtils;
import java.util.Objects;
/* loaded from: classes2.dex */
public abstract class ActionDisabledLearnMoreButtonLauncher {
    public static ResolveActivityChecker DEFAULT_RESOLVE_ACTIVITY_CHECKER = new ResolveActivityChecker() { // from class: com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda1
        @Override // com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher.ResolveActivityChecker
        public final boolean canResolveActivityAsUser(PackageManager packageManager, String str, UserHandle userHandle) {
            boolean lambda$static$0;
            lambda$static$0 = ActionDisabledLearnMoreButtonLauncher.lambda$static$0(packageManager, str, userHandle);
            return lambda$static$0;
        }
    };

    /* loaded from: classes2.dex */
    interface ResolveActivityChecker {
        boolean canResolveActivityAsUser(PackageManager packageManager, String str, UserHandle userHandle);
    }

    protected void finishSelf() {
    }

    protected abstract void launchShowAdminPolicies(Context context, UserHandle userHandle, ComponentName componentName);

    protected abstract void launchShowAdminSettings(Context context);

    public abstract void setLearnMoreButton(Runnable runnable);

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$static$0(PackageManager packageManager, String str, UserHandle userHandle) {
        return packageManager.resolveActivityAsUser(createLearnMoreIntent(str), 65536, userHandle.getIdentifier()) != null;
    }

    public final void setupLearnMoreButtonToShowAdminPolicies(final Context context, int i, final RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        Objects.requireNonNull(context, "context cannot be null");
        if (isSameProfileGroup(context, i) || isEnforcedByDeviceOwnerOnSystemUserMode(context, i)) {
            setLearnMoreButton(new Runnable() { // from class: com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ActionDisabledLearnMoreButtonLauncher.this.lambda$setupLearnMoreButtonToShowAdminPolicies$1(context, enforcedAdmin);
                }
            });
        }
    }

    public final void setupLearnMoreButtonToLaunchHelpPage(final Context context, final String str, final UserHandle userHandle) {
        Objects.requireNonNull(context, "context cannot be null");
        Objects.requireNonNull(str, "url cannot be null");
        setLearnMoreButton(new Runnable() { // from class: com.android.settingslib.enterprise.ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ActionDisabledLearnMoreButtonLauncher.this.lambda$setupLearnMoreButtonToLaunchHelpPage$2(context, str, userHandle);
            }
        });
    }

    @VisibleForTesting
    protected boolean isSameProfileGroup(Context context, int i) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        return userManager.isSameProfileGroup(i, userManager.getProcessUserId());
    }

    private boolean isEnforcedByDeviceOwnerOnSystemUserMode(Context context, int i) {
        return i == 0 && i == ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getDeviceOwnerUserId();
    }

    @VisibleForTesting
    /* renamed from: showHelpPage */
    public void lambda$setupLearnMoreButtonToLaunchHelpPage$2(Context context, String str, UserHandle userHandle) {
        context.startActivityAsUser(createLearnMoreIntent(str), userHandle);
        finishSelf();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean canLaunchHelpPage(PackageManager packageManager, String str, UserHandle userHandle, ResolveActivityChecker resolveActivityChecker) {
        return resolveActivityChecker.canResolveActivityAsUser(packageManager, str, userHandle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showAdminPolicies */
    public void lambda$setupLearnMoreButtonToShowAdminPolicies$1(Context context, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        ComponentName componentName;
        if (enforcedAdmin != null && (componentName = enforcedAdmin.component) != null) {
            launchShowAdminPolicies(context, enforcedAdmin.user, componentName);
        } else {
            launchShowAdminSettings(context);
        }
        finishSelf();
    }

    private static Intent createLearnMoreIntent(String str) {
        return new Intent("android.intent.action.VIEW", Uri.parse(str)).setFlags(276824064);
    }
}
