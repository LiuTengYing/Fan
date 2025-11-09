package com.android.settingslib.users;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes2.dex */
public class AppCopyHelper {
    private final IPackageManager mIPm;
    private boolean mLeanback;
    private final PackageManager mPackageManager;
    private final ArraySet<String> mSelectedPackages;
    private final UserHandle mUser;
    private List<SelectableAppInfo> mVisibleApps;

    public AppCopyHelper(Context context, UserHandle userHandle) {
        this(new Injector(context, userHandle));
    }

    AppCopyHelper(Injector injector) {
        this.mSelectedPackages = new ArraySet<>();
        this.mPackageManager = injector.getPackageManager();
        this.mIPm = injector.getIPackageManager();
        this.mUser = injector.getUser();
    }

    public void setPackageSelected(String str, boolean z) {
        if (z) {
            this.mSelectedPackages.add(str);
        } else {
            this.mSelectedPackages.remove(str);
        }
    }

    public void resetSelectedPackages() {
        this.mSelectedPackages.clear();
    }

    public List<SelectableAppInfo> getVisibleApps() {
        return this.mVisibleApps;
    }

    public void installSelectedApps() {
        for (int i = 0; i < this.mSelectedPackages.size(); i++) {
            installSelectedApp(this.mSelectedPackages.valueAt(i));
        }
    }

    private void installSelectedApp(String str) {
        int identifier = this.mUser.getIdentifier();
        try {
            ApplicationInfo applicationInfo = this.mIPm.getApplicationInfo(str, 4194304L, identifier);
            if (applicationInfo == null || !applicationInfo.enabled || (applicationInfo.flags & 8388608) == 0) {
                Log.i("AppCopyHelper", "Installing " + str);
                this.mIPm.installExistingPackageAsUser(str, this.mUser.getIdentifier(), 4194304, 0, (List) null);
            }
            if (applicationInfo == null || (applicationInfo.privateFlags & 1) == 0 || (applicationInfo.flags & 8388608) == 0) {
                return;
            }
            Log.i("AppCopyHelper", "Unhiding " + str);
            this.mIPm.setApplicationHiddenSettingAsUser(str, false, identifier);
        } catch (RemoteException unused) {
        }
    }

    public void fetchAndMergeApps() {
        this.mVisibleApps = new ArrayList();
        addCurrentUsersApps();
        removeSecondUsersApp();
    }

    private void addCurrentUsersApps() {
        addSystemApps(this.mVisibleApps, new Intent("android.intent.action.MAIN").addCategory(this.mLeanback ? "android.intent.category.LEANBACK_LAUNCHER" : "android.intent.category.LAUNCHER"));
        addSystemApps(this.mVisibleApps, new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
        for (ApplicationInfo applicationInfo : this.mPackageManager.getInstalledApplications(0)) {
            int i = applicationInfo.flags;
            if ((8388608 & i) != 0 && (i & 1) == 0 && (i & 128) == 0) {
                SelectableAppInfo selectableAppInfo = new SelectableAppInfo();
                selectableAppInfo.packageName = applicationInfo.packageName;
                selectableAppInfo.appName = applicationInfo.loadLabel(this.mPackageManager);
                selectableAppInfo.icon = applicationInfo.loadIcon(this.mPackageManager);
                this.mVisibleApps.add(selectableAppInfo);
            }
        }
        HashSet hashSet = new HashSet();
        for (int size = this.mVisibleApps.size() - 1; size >= 0; size--) {
            SelectableAppInfo selectableAppInfo2 = this.mVisibleApps.get(size);
            if (!TextUtils.isEmpty(selectableAppInfo2.packageName) && hashSet.contains(selectableAppInfo2.packageName)) {
                this.mVisibleApps.remove(size);
            } else {
                hashSet.add(selectableAppInfo2.packageName);
            }
        }
        this.mVisibleApps.sort(new AppLabelComparator());
    }

    private void removeSecondUsersApp() {
        HashSet hashSet = new HashSet();
        List installedApplicationsAsUser = this.mPackageManager.getInstalledApplicationsAsUser(8192, this.mUser.getIdentifier());
        for (int size = installedApplicationsAsUser.size() - 1; size >= 0; size--) {
            ApplicationInfo applicationInfo = (ApplicationInfo) installedApplicationsAsUser.get(size);
            if ((applicationInfo.flags & 8388608) != 0) {
                hashSet.add(applicationInfo.packageName);
            }
        }
        for (int size2 = this.mVisibleApps.size() - 1; size2 >= 0; size2--) {
            SelectableAppInfo selectableAppInfo = this.mVisibleApps.get(size2);
            if (!TextUtils.isEmpty(selectableAppInfo.packageName) && hashSet.contains(selectableAppInfo.packageName)) {
                this.mVisibleApps.remove(size2);
            }
        }
    }

    private void addSystemApps(List<SelectableAppInfo> list, Intent intent) {
        ApplicationInfo applicationInfo;
        for (ResolveInfo resolveInfo : this.mPackageManager.queryIntentActivities(intent, 0)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && (applicationInfo = activityInfo.applicationInfo) != null) {
                int i = applicationInfo.flags;
                if ((i & 1) != 0 || (i & 128) != 0) {
                    SelectableAppInfo selectableAppInfo = new SelectableAppInfo();
                    ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                    selectableAppInfo.packageName = activityInfo2.packageName;
                    selectableAppInfo.appName = activityInfo2.applicationInfo.loadLabel(this.mPackageManager);
                    selectableAppInfo.icon = resolveInfo.activityInfo.loadIcon(this.mPackageManager);
                    list.add(selectableAppInfo);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class SelectableAppInfo {
        public CharSequence appName;
        public Drawable icon;
        public String packageName;

        public String toString() {
            return this.packageName + ": appName=" + ((Object) this.appName) + "; icon=" + this.icon;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class AppLabelComparator implements Comparator<SelectableAppInfo> {
        private AppLabelComparator() {
        }

        @Override // java.util.Comparator
        public int compare(SelectableAppInfo selectableAppInfo, SelectableAppInfo selectableAppInfo2) {
            return selectableAppInfo.appName.toString().toLowerCase().compareTo(selectableAppInfo2.appName.toString().toLowerCase());
        }
    }

    /* loaded from: classes2.dex */
    static class Injector {
        private final Context mContext;
        private final UserHandle mUser;

        Injector(Context context, UserHandle userHandle) {
            this.mContext = context;
            this.mUser = userHandle;
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
    }
}
