package com.android.settingslib.users;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public class AppRestrictionsHelper {
    private final Context mContext;
    private final IPackageManager mIPm;
    private final Injector mInjector;
    private boolean mLeanback;
    private final PackageManager mPackageManager;
    private final boolean mRestrictedProfile;
    HashMap<String, Boolean> mSelectedPackages;
    private final UserHandle mUser;
    private final UserManager mUserManager;
    private List<SelectableAppInfo> mVisibleApps;

    /* loaded from: classes2.dex */
    public interface OnDisableUiForPackageListener {
        void onDisableUiForPackage(String str);
    }

    public AppRestrictionsHelper(Context context, UserHandle userHandle) {
        this(new Injector(context, userHandle));
    }

    AppRestrictionsHelper(Injector injector) {
        this.mSelectedPackages = new HashMap<>();
        this.mInjector = injector;
        this.mContext = injector.getContext();
        this.mPackageManager = injector.getPackageManager();
        this.mIPm = injector.getIPackageManager();
        UserHandle user = injector.getUser();
        this.mUser = user;
        UserManager userManager = injector.getUserManager();
        this.mUserManager = userManager;
        this.mRestrictedProfile = userManager.getUserInfo(user.getIdentifier()).isRestricted();
    }

    public void setPackageSelected(String str, boolean z) {
        this.mSelectedPackages.put(str, Boolean.valueOf(z));
    }

    public boolean isPackageSelected(String str) {
        return this.mSelectedPackages.get(str).booleanValue();
    }

    public List<SelectableAppInfo> getVisibleApps() {
        return this.mVisibleApps;
    }

    public void applyUserAppsStates(OnDisableUiForPackageListener onDisableUiForPackageListener) {
        if (!this.mRestrictedProfile && this.mUser.getIdentifier() != UserHandle.myUserId()) {
            Log.e("AppRestrictionsHelper", "Cannot apply application restrictions on another user!");
            return;
        }
        for (Map.Entry<String, Boolean> entry : this.mSelectedPackages.entrySet()) {
            applyUserAppState(entry.getKey(), entry.getValue().booleanValue(), onDisableUiForPackageListener);
        }
    }

    public void applyUserAppState(String str, boolean z, OnDisableUiForPackageListener onDisableUiForPackageListener) {
        int identifier = this.mUser.getIdentifier();
        try {
            if (z) {
                ApplicationInfo applicationInfo = this.mIPm.getApplicationInfo(str, 4194304L, identifier);
                if (applicationInfo == null || !applicationInfo.enabled || (applicationInfo.flags & 8388608) == 0) {
                    this.mIPm.installExistingPackageAsUser(str, this.mUser.getIdentifier(), 4194304, 0, (List) null);
                }
                if (applicationInfo == null || (1 & applicationInfo.privateFlags) == 0 || (applicationInfo.flags & 8388608) == 0) {
                    return;
                }
                onDisableUiForPackageListener.onDisableUiForPackage(str);
                this.mIPm.setApplicationHiddenSettingAsUser(str, false, identifier);
            } else if (this.mIPm.getApplicationInfo(str, 0L, identifier) != null) {
                if (this.mRestrictedProfile) {
                    this.mPackageManager.deletePackageAsUser(str, null, 4, this.mUser.getIdentifier());
                    return;
                }
                onDisableUiForPackageListener.onDisableUiForPackage(str);
                this.mIPm.setApplicationHiddenSettingAsUser(str, true, identifier);
            }
        } catch (RemoteException unused) {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x00b1  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0159  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void fetchAndMergeApps() {
        /*
            Method dump skipped, instructions count: 377
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.users.AppRestrictionsHelper.fetchAndMergeApps():void");
    }

    private void addSystemImes(Set<String> set) {
        for (InputMethodInfo inputMethodInfo : this.mInjector.getInputMethodList()) {
            try {
                if (inputMethodInfo.isDefault(this.mContext) && isSystemPackage(inputMethodInfo.getPackageName())) {
                    set.add(inputMethodInfo.getPackageName());
                }
            } catch (Resources.NotFoundException unused) {
            }
        }
    }

    private void addSystemApps(List<SelectableAppInfo> list, Intent intent, Set<String> set) {
        ApplicationInfo applicationInfo;
        int applicationEnabledSetting;
        ApplicationInfo appInfoForUser;
        PackageManager packageManager = this.mPackageManager;
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 8704)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && (applicationInfo = activityInfo.applicationInfo) != null) {
                String str = activityInfo.packageName;
                int i = applicationInfo.flags;
                if ((i & 1) != 0 || (i & 128) != 0) {
                    if (!set.contains(str) && (((applicationEnabledSetting = packageManager.getApplicationEnabledSetting(str)) != 4 && applicationEnabledSetting != 2) || ((appInfoForUser = getAppInfoForUser(str, 0, this.mUser)) != null && (appInfoForUser.flags & 8388608) != 0))) {
                        SelectableAppInfo selectableAppInfo = new SelectableAppInfo();
                        ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                        selectableAppInfo.packageName = activityInfo2.packageName;
                        selectableAppInfo.appName = activityInfo2.applicationInfo.loadLabel(packageManager);
                        selectableAppInfo.icon = resolveInfo.activityInfo.loadIcon(packageManager);
                        CharSequence loadLabel = resolveInfo.activityInfo.loadLabel(packageManager);
                        selectableAppInfo.activityName = loadLabel;
                        if (loadLabel == null) {
                            selectableAppInfo.activityName = selectableAppInfo.appName;
                        }
                        list.add(selectableAppInfo);
                    }
                }
            }
        }
    }

    private boolean isSystemPackage(String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = this.mPackageManager.getPackageInfo(str, 0).applicationInfo;
        } catch (PackageManager.NameNotFoundException unused) {
        }
        if (applicationInfo == null) {
            return false;
        }
        int i = applicationInfo.flags;
        return ((i & 1) == 0 && (i & 128) == 0) ? false : true;
    }

    private ApplicationInfo getAppInfoForUser(String str, int i, UserHandle userHandle) {
        try {
            return this.mIPm.getApplicationInfo(str, i, userHandle.getIdentifier());
        } catch (RemoteException unused) {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static class SelectableAppInfo {
        public CharSequence activityName;
        public CharSequence appName;
        public Drawable icon;
        public String packageName;
        public SelectableAppInfo primaryEntry;

        public String toString() {
            return this.packageName + ": appName=" + ((Object) this.appName) + "; activityName=" + ((Object) this.activityName) + "; icon=" + this.icon + "; primaryEntry=" + this.primaryEntry;
        }
    }

    /* loaded from: classes2.dex */
    private static class AppLabelComparator implements Comparator<SelectableAppInfo> {
        private AppLabelComparator() {
        }

        @Override // java.util.Comparator
        public int compare(SelectableAppInfo selectableAppInfo, SelectableAppInfo selectableAppInfo2) {
            return selectableAppInfo.activityName.toString().toLowerCase().compareTo(selectableAppInfo2.activityName.toString().toLowerCase());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class Injector {
        private Context mContext;
        private UserHandle mUser;

        Injector(Context context, UserHandle userHandle) {
            this.mContext = context;
            this.mUser = userHandle;
        }

        Context getContext() {
            return this.mContext;
        }

        UserHandle getUser() {
            return this.mUser;
        }

        PackageManager getPackageManager() {
            return this.mContext.getPackageManager();
        }

        IPackageManager getIPackageManager() {
            return AppGlobals.getPackageManager();
        }

        UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService(UserManager.class);
        }

        List<InputMethodInfo> getInputMethodList() {
            return ((InputMethodManager) getContext().getSystemService("input_method")).getInputMethodListAsUser(this.mUser.getIdentifier());
        }
    }
}
