package com.android.settingslib;

import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.RestrictedLockUtils;
import java.util.List;
/* loaded from: classes2.dex */
public class RestrictedLockUtilsInternal extends RestrictedLockUtils {
    private static final boolean DEBUG = Log.isLoggable("RestrictedLockUtils", 3);
    static Proxy sProxy = new Proxy();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface LockSettingCheck {
        boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i);
    }

    public static Drawable getRestrictedPadlock(Context context) {
        Drawable drawable = context.getDrawable(17301684);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(17104903);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843829});
        int color = obtainStyledAttributes.getColor(0, 0);
        obtainStyledAttributes.recycle();
        drawable.setTint(color);
        drawable.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
        return drawable;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced(Context context, String str, int i) {
        if (((DevicePolicyManager) context.getSystemService("device_policy")) == null) {
            return null;
        }
        UserManager userManager = UserManager.get(context);
        UserHandle of = UserHandle.of(i);
        List userRestrictionSources = userManager.getUserRestrictionSources(str, of);
        if (userRestrictionSources.isEmpty()) {
            return null;
        }
        int size = userRestrictionSources.size();
        if (size > 1) {
            RestrictedLockUtils.EnforcedAdmin createDefaultEnforcedAdminWithRestriction = RestrictedLockUtils.EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(str);
            createDefaultEnforcedAdminWithRestriction.user = of;
            if (DEBUG) {
                Log.d("RestrictedLockUtils", "Multiple (" + size + ") enforcing users for restriction '" + str + "' on user " + of + "; returning default admin (" + createDefaultEnforcedAdminWithRestriction + ")");
            }
            return createDefaultEnforcedAdminWithRestriction;
        }
        int userRestrictionSource = ((UserManager.EnforcingUser) userRestrictionSources.get(0)).getUserRestrictionSource();
        int identifier = ((UserManager.EnforcingUser) userRestrictionSources.get(0)).getUserHandle().getIdentifier();
        if (userRestrictionSource != 4) {
            if (userRestrictionSource == 2) {
                if (identifier == i) {
                    return getDeviceOwner(context, str);
                }
                return RestrictedLockUtils.EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(str);
            }
            return null;
        } else if (identifier == i) {
            return getProfileOwner(context, str, identifier);
        } else {
            UserInfo profileParent = userManager.getProfileParent(identifier);
            if (profileParent != null && profileParent.id == i) {
                return getProfileOwner(context, str, identifier);
            }
            return RestrictedLockUtils.EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(str);
        }
    }

    public static boolean hasBaseUserRestriction(Context context, String str, int i) {
        return ((UserManager) context.getSystemService("user")).hasBaseUserRestriction(str, UserHandle.of(i));
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled(Context context, final int i, final int i2) {
        LockSettingCheck lockSettingCheck = new LockSettingCheck() { // from class: com.android.settingslib.RestrictedLockUtilsInternal$$ExternalSyntheticLambda1
            @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
            public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i3) {
                boolean lambda$checkIfKeyguardFeaturesDisabled$0;
                lambda$checkIfKeyguardFeaturesDisabled$0 = RestrictedLockUtilsInternal.lambda$checkIfKeyguardFeaturesDisabled$0(i2, i, devicePolicyManager, componentName, i3);
                return lambda$checkIfKeyguardFeaturesDisabled$0;
            }
        };
        if (UserManager.get(context).getUserInfo(i2).isManagedProfile()) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
            return findEnforcedAdmin(devicePolicyManager.getActiveAdminsAsUser(i2), devicePolicyManager, i2, lockSettingCheck);
        }
        return checkForLockSetting(context, i2, lockSettingCheck);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$checkIfKeyguardFeaturesDisabled$0(int i, int i2, DevicePolicyManager devicePolicyManager, ComponentName componentName, int i3) {
        int keyguardDisabledFeatures = devicePolicyManager.getKeyguardDisabledFeatures(componentName, i3);
        if (i3 != i) {
            keyguardDisabledFeatures &= 438;
        }
        return (keyguardDisabledFeatures & i2) != 0;
    }

    private static UserHandle getUserHandleOf(int i) {
        if (i == -10000) {
            return null;
        }
        return UserHandle.of(i);
    }

    private static RestrictedLockUtils.EnforcedAdmin findEnforcedAdmin(List<ComponentName> list, DevicePolicyManager devicePolicyManager, int i, LockSettingCheck lockSettingCheck) {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = null;
        if (list == null) {
            return null;
        }
        UserHandle userHandleOf = getUserHandleOf(i);
        for (ComponentName componentName : list) {
            if (lockSettingCheck.isEnforcing(devicePolicyManager, componentName, i)) {
                if (enforcedAdmin == null) {
                    enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(componentName, userHandleOf);
                } else {
                    return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
                }
            }
        }
        return enforcedAdmin;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfUninstallBlocked(Context context, String str, int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = checkIfRestrictionEnforced(context, "no_control_apps", i);
        if (checkIfRestrictionEnforced != null) {
            return checkIfRestrictionEnforced;
        }
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced2 = checkIfRestrictionEnforced(context, "no_uninstall_apps", i);
        if (checkIfRestrictionEnforced2 != null) {
            return checkIfRestrictionEnforced2;
        }
        try {
            if (AppGlobals.getPackageManager().getBlockUninstallForUser(str, i)) {
                return RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
            }
            return null;
        } catch (RemoteException unused) {
            return null;
        }
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfApplicationIsSuspended(Context context, String str, int i) {
        try {
            if (AppGlobals.getPackageManager().isPackageSuspendedForUser(str, i)) {
                return RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
            }
            return null;
        } catch (RemoteException | IllegalArgumentException unused) {
            return null;
        }
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfInputMethodDisallowed(Context context, String str, int i) {
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null) {
            return null;
        }
        RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
        boolean z = true;
        boolean isInputMethodPermittedByAdmin = profileOrDeviceOwner != null ? devicePolicyManager.isInputMethodPermittedByAdmin(profileOrDeviceOwner.component, str, i) : true;
        int managedProfileId = getManagedProfileId(context, i);
        if (managedProfileId != -10000) {
            enforcedAdmin = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(managedProfileId));
            if (enforcedAdmin != null && devicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile()) {
                z = sProxy.getParentProfileInstance(devicePolicyManager, UserManager.get(context).getUserInfo(managedProfileId)).isInputMethodPermittedByAdmin(enforcedAdmin.component, str, managedProfileId);
            }
        } else {
            enforcedAdmin = null;
        }
        if (isInputMethodPermittedByAdmin || z) {
            if (isInputMethodPermittedByAdmin) {
                if (z) {
                    return null;
                }
                return enforcedAdmin;
            }
            return profileOrDeviceOwner;
        }
        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfRemoteContactSearchDisallowed(Context context, int i) {
        RestrictedLockUtils.EnforcedAdmin profileOwner;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null || (profileOwner = getProfileOwner(context, i)) == null) {
            return null;
        }
        UserHandle of = UserHandle.of(i);
        if (devicePolicyManager.getCrossProfileContactsSearchDisabled(of) && devicePolicyManager.getCrossProfileCallerIdDisabled(of)) {
            return profileOwner;
        }
        return null;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfAccessibilityServiceDisallowed(Context context, String str, int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null) {
            return null;
        }
        RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
        boolean isAccessibilityServicePermittedByAdmin = profileOrDeviceOwner != null ? devicePolicyManager.isAccessibilityServicePermittedByAdmin(profileOrDeviceOwner.component, str, i) : true;
        int managedProfileId = getManagedProfileId(context, i);
        RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner2 = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(managedProfileId));
        boolean isAccessibilityServicePermittedByAdmin2 = profileOrDeviceOwner2 != null ? devicePolicyManager.isAccessibilityServicePermittedByAdmin(profileOrDeviceOwner2.component, str, managedProfileId) : true;
        if (isAccessibilityServicePermittedByAdmin || isAccessibilityServicePermittedByAdmin2) {
            if (isAccessibilityServicePermittedByAdmin) {
                if (isAccessibilityServicePermittedByAdmin2) {
                    return null;
                }
                return profileOrDeviceOwner2;
            }
            return profileOrDeviceOwner;
        }
        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }

    private static int getManagedProfileId(Context context, int i) {
        for (UserInfo userInfo : ((UserManager) context.getSystemService("user")).getProfiles(i)) {
            if (userInfo.id != i && userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -10000;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfAccountManagementDisabled(Context context, String str, int i) {
        if (str == null) {
            return null;
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (!context.getPackageManager().hasSystemFeature("android.software.device_admin") || devicePolicyManager == null) {
            return null;
        }
        String[] accountTypesWithManagementDisabledAsUser = devicePolicyManager.getAccountTypesWithManagementDisabledAsUser(i);
        int length = accountTypesWithManagementDisabledAsUser.length;
        boolean z = false;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            } else if (str.equals(accountTypesWithManagementDisabledAsUser[i2])) {
                z = true;
                break;
            } else {
                i2++;
            }
        }
        if (z) {
            return RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
        }
        return null;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfUsbDataSignalingIsDisabled(Context context, int i) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        if (devicePolicyManager == null || devicePolicyManager.isUsbDataSignalingEnabledForUser(i)) {
            return null;
        }
        RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
        int managedProfileId = getManagedProfileId(context, i);
        return (profileOrDeviceOwner != null || managedProfileId == -10000) ? profileOrDeviceOwner : RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(managedProfileId));
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfMeteredDataRestricted(Context context, String str, int i) {
        RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(context, getUserHandleOf(i));
        if (profileOrDeviceOwner != null && ((DevicePolicyManager) context.getSystemService("device_policy")).isMeteredDataDisabledPackageForUser(profileOrDeviceOwner.component, str, i)) {
            return profileOrDeviceOwner;
        }
        return null;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet(Context context, int i) {
        LockSettingCheck lockSettingCheck = new LockSettingCheck() { // from class: com.android.settingslib.RestrictedLockUtilsInternal$$ExternalSyntheticLambda2
            @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
            public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i2) {
                boolean lambda$checkIfPasswordQualityIsSet$1;
                lambda$checkIfPasswordQualityIsSet$1 = RestrictedLockUtilsInternal.lambda$checkIfPasswordQualityIsSet$1(devicePolicyManager, componentName, i2);
                return lambda$checkIfPasswordQualityIsSet$1;
            }
        };
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = null;
        if (devicePolicyManager == null) {
            return null;
        }
        LockPatternUtils lockPatternUtils = new LockPatternUtils(context);
        int aggregatedPasswordComplexityForUser = devicePolicyManager.getAggregatedPasswordComplexityForUser(i);
        if (aggregatedPasswordComplexityForUser > 0) {
            UserHandle deviceOwnerUser = devicePolicyManager.getDeviceOwnerUser();
            if (deviceOwnerUser != null) {
                return new RestrictedLockUtils.EnforcedAdmin(devicePolicyManager.getDeviceOwnerComponentOnAnyUser(), deviceOwnerUser);
            }
            for (UserInfo userInfo : UserManager.get(context).getProfiles(i)) {
                ComponentName profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(userInfo.id);
                if (profileOwnerAsUser != null) {
                    return new RestrictedLockUtils.EnforcedAdmin(profileOwnerAsUser, getUserHandleOf(userInfo.id));
                }
            }
            throw new IllegalStateException(String.format("Could not find admin enforcing complexity %d for user %d", Integer.valueOf(aggregatedPasswordComplexityForUser), Integer.valueOf(i)));
        } else if (sProxy.isSeparateProfileChallengeEnabled(lockPatternUtils, i)) {
            List<ComponentName> activeAdminsAsUser = devicePolicyManager.getActiveAdminsAsUser(i);
            if (activeAdminsAsUser == null) {
                return null;
            }
            UserHandle userHandleOf = getUserHandleOf(i);
            for (ComponentName componentName : activeAdminsAsUser) {
                if (lockSettingCheck.isEnforcing(devicePolicyManager, componentName, i)) {
                    if (enforcedAdmin == null) {
                        enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(componentName, userHandleOf);
                    } else {
                        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
                    }
                }
            }
            return enforcedAdmin;
        } else {
            return checkForLockSetting(context, i, lockSettingCheck);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$checkIfPasswordQualityIsSet$1(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        return devicePolicyManager.getPasswordQuality(componentName, i) > 0;
    }

    public static RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockIsSet(Context context) {
        return checkForLockSetting(context, UserHandle.myUserId(), new LockSettingCheck() { // from class: com.android.settingslib.RestrictedLockUtilsInternal$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.RestrictedLockUtilsInternal.LockSettingCheck
            public final boolean isEnforcing(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
                boolean lambda$checkIfMaximumTimeToLockIsSet$2;
                lambda$checkIfMaximumTimeToLockIsSet$2 = RestrictedLockUtilsInternal.lambda$checkIfMaximumTimeToLockIsSet$2(devicePolicyManager, componentName, i);
                return lambda$checkIfMaximumTimeToLockIsSet$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$checkIfMaximumTimeToLockIsSet$2(DevicePolicyManager devicePolicyManager, ComponentName componentName, int i) {
        return devicePolicyManager.getMaximumTimeToLock(componentName, i) > 0;
    }

    private static RestrictedLockUtils.EnforcedAdmin checkForLockSetting(Context context, int i, LockSettingCheck lockSettingCheck) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = null;
        if (devicePolicyManager == null) {
            return null;
        }
        LockPatternUtils lockPatternUtils = new LockPatternUtils(context);
        for (UserInfo userInfo : UserManager.get(context).getProfiles(i)) {
            List<ComponentName> activeAdminsAsUser = devicePolicyManager.getActiveAdminsAsUser(userInfo.id);
            if (activeAdminsAsUser != null) {
                UserHandle userHandleOf = getUserHandleOf(userInfo.id);
                boolean isSeparateProfileChallengeEnabled = sProxy.isSeparateProfileChallengeEnabled(lockPatternUtils, userInfo.id);
                for (ComponentName componentName : activeAdminsAsUser) {
                    if (isSeparateProfileChallengeEnabled || !lockSettingCheck.isEnforcing(devicePolicyManager, componentName, userInfo.id)) {
                        if (userInfo.isManagedProfile() && lockSettingCheck.isEnforcing(sProxy.getParentProfileInstance(devicePolicyManager, userInfo), componentName, userInfo.id)) {
                            if (enforcedAdmin == null) {
                                enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(componentName, userHandleOf);
                            } else {
                                return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
                            }
                        }
                    } else if (enforcedAdmin == null) {
                        enforcedAdmin = new RestrictedLockUtils.EnforcedAdmin(componentName, userHandleOf);
                    } else {
                        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
                    }
                }
                continue;
            }
        }
        return enforcedAdmin;
    }

    public static RestrictedLockUtils.EnforcedAdmin getDeviceOwner(Context context) {
        return getDeviceOwner(context, null);
    }

    private static RestrictedLockUtils.EnforcedAdmin getDeviceOwner(Context context, String str) {
        ComponentName deviceOwnerComponentOnAnyUser;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        if (devicePolicyManager == null || (deviceOwnerComponentOnAnyUser = devicePolicyManager.getDeviceOwnerComponentOnAnyUser()) == null) {
            return null;
        }
        return new RestrictedLockUtils.EnforcedAdmin(deviceOwnerComponentOnAnyUser, str, devicePolicyManager.getDeviceOwnerUser());
    }

    private static RestrictedLockUtils.EnforcedAdmin getProfileOwner(Context context, int i) {
        return getProfileOwner(context, null, i);
    }

    private static RestrictedLockUtils.EnforcedAdmin getProfileOwner(Context context, String str, int i) {
        DevicePolicyManager devicePolicyManager;
        ComponentName profileOwnerAsUser;
        if (i == -10000 || (devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy")) == null || (profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser(i)) == null) {
            return null;
        }
        return new RestrictedLockUtils.EnforcedAdmin(profileOwnerAsUser, str, getUserHandleOf(i));
    }

    public static void setMenuItemAsDisabledByAdmin(final Context context, MenuItem menuItem, final RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(menuItem.getTitle());
        removeExistingRestrictedSpans(spannableStringBuilder);
        if (enforcedAdmin != null) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan(context.getColor(R$color.disabled_text_color)), 0, spannableStringBuilder.length(), 33);
            spannableStringBuilder.append(" ", new RestrictedLockImageSpan(context), 33);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.android.settingslib.RestrictedLockUtilsInternal.1
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem2) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(context, enforcedAdmin);
                    return true;
                }
            });
        } else {
            menuItem.setOnMenuItemClickListener(null);
        }
        menuItem.setTitle(spannableStringBuilder);
    }

    private static void removeExistingRestrictedSpans(SpannableStringBuilder spannableStringBuilder) {
        RestrictedLockImageSpan[] restrictedLockImageSpanArr;
        int length = spannableStringBuilder.length();
        for (RestrictedLockImageSpan restrictedLockImageSpan : (RestrictedLockImageSpan[]) spannableStringBuilder.getSpans(length - 1, length, RestrictedLockImageSpan.class)) {
            int spanStart = spannableStringBuilder.getSpanStart(restrictedLockImageSpan);
            int spanEnd = spannableStringBuilder.getSpanEnd(restrictedLockImageSpan);
            spannableStringBuilder.removeSpan(restrictedLockImageSpan);
            spannableStringBuilder.delete(spanStart, spanEnd);
        }
        for (ForegroundColorSpan foregroundColorSpan : (ForegroundColorSpan[]) spannableStringBuilder.getSpans(0, length, ForegroundColorSpan.class)) {
            spannableStringBuilder.removeSpan(foregroundColorSpan);
        }
    }

    public static boolean isAdminInCurrentUserOrProfile(Context context, ComponentName componentName) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        for (UserInfo userInfo : UserManager.get(context).getProfiles(UserHandle.myUserId())) {
            if (devicePolicyManager.isAdminActiveAsUser(componentName, userInfo.id)) {
                return true;
            }
        }
        return false;
    }

    public static void setTextViewAsDisabledByAdmin(Context context, TextView textView, boolean z) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(textView.getText());
        removeExistingRestrictedSpans(spannableStringBuilder);
        if (z) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Utils.getDisabled(context, textView.getCurrentTextColor())), 0, spannableStringBuilder.length(), 33);
            textView.setCompoundDrawables(null, null, getRestrictedPadlock(context), null);
            textView.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R$dimen.restricted_icon_padding));
        } else {
            textView.setCompoundDrawables(null, null, null, null);
        }
        textView.setText(spannableStringBuilder);
    }

    public static void sendShowRestrictedSettingDialogIntent(Context context, String str, int i) {
        context.startActivity(getShowRestrictedSettingsIntent(str, i));
    }

    private static Intent getShowRestrictedSettingsIntent(String str, int i) {
        Intent intent = new Intent("android.settings.SHOW_RESTRICTED_SETTING_DIALOG");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
        intent.putExtra("android.intent.extra.UID", i);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class Proxy {
        Proxy() {
        }

        public boolean isSeparateProfileChallengeEnabled(LockPatternUtils lockPatternUtils, int i) {
            return lockPatternUtils.isSeparateProfileChallengeEnabled(i);
        }

        public DevicePolicyManager getParentProfileInstance(DevicePolicyManager devicePolicyManager, UserInfo userInfo) {
            return devicePolicyManager.getParentProfileInstance(userInfo);
        }
    }
}
