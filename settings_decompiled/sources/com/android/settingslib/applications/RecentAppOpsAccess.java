package com.android.settingslib.applications;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.PermissionChecker;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.permission.PermissionManager;
import android.util.IconDrawableFactory;
import android.util.Log;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes2.dex */
public class RecentAppOpsAccess {
    public static final String ANDROID_SYSTEM_PACKAGE_NAME = "android";
    private final Clock mClock;
    private final Context mContext;
    private final IconDrawableFactory mDrawableFactory;
    private final int[] mOps;
    private final PackageManager mPackageManager;
    static final int[] LOCATION_OPS = {1, 0};
    private static final int[] MICROPHONE_OPS = {27};
    private static final int[] CAMERA_OPS = {26};
    private static final String TAG = RecentAppOpsAccess.class.getSimpleName();

    public RecentAppOpsAccess(Context context, int[] iArr) {
        this(context, Clock.systemDefaultZone(), iArr);
    }

    RecentAppOpsAccess(Context context, Clock clock, int[] iArr) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mOps = iArr;
        this.mDrawableFactory = IconDrawableFactory.newInstance(context);
        this.mClock = clock;
    }

    public static RecentAppOpsAccess createForLocation(Context context) {
        return new RecentAppOpsAccess(context, LOCATION_OPS);
    }

    public List<Access> getAppList(boolean z) {
        List list;
        int i;
        List<UserHandle> list2;
        boolean z2;
        Access accessFromOps;
        List packagesForOps = ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).getPackagesForOps(this.mOps);
        int size = packagesForOps != null ? packagesForOps.size() : 0;
        ArrayList arrayList = new ArrayList(size);
        long millis = this.mClock.millis();
        List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserProfiles();
        int i2 = 0;
        while (i2 < size) {
            AppOpsManager.PackageOps packageOps = (AppOpsManager.PackageOps) packagesForOps.get(i2);
            String packageName = packageOps.getPackageName();
            int uid = packageOps.getUid();
            UserHandle userHandleForUid = UserHandle.getUserHandleForUid(uid);
            if (userProfiles.contains(userHandleForUid)) {
                if (!z) {
                    int[] iArr = this.mOps;
                    int length = iArr.length;
                    int i3 = 0;
                    while (i3 < length) {
                        String opToPermission = AppOpsManager.opToPermission(iArr[i3]);
                        list = packagesForOps;
                        int permissionFlags = this.mPackageManager.getPermissionFlags(opToPermission, packageName, userHandleForUid);
                        i = size;
                        list2 = userProfiles;
                        if (PermissionChecker.checkPermissionForPreflight(this.mContext, opToPermission, -1, uid, packageName) == 0) {
                            if ((permissionFlags & 256) == 0) {
                                z2 = false;
                                break;
                            }
                            i3++;
                            packagesForOps = list;
                            size = i;
                            userProfiles = list2;
                        } else if ((permissionFlags & 512) == 0) {
                            z2 = false;
                            break;
                        } else {
                            i3++;
                            packagesForOps = list;
                            size = i;
                            userProfiles = list2;
                        }
                    }
                }
                list = packagesForOps;
                i = size;
                list2 = userProfiles;
                z2 = true;
                if (z2 && PermissionManager.shouldShowPackageForIndicatorCached(this.mContext, packageName) && (accessFromOps = getAccessFromOps(millis, packageOps)) != null) {
                    arrayList.add(accessFromOps);
                }
            } else {
                list = packagesForOps;
                i = size;
                list2 = userProfiles;
            }
            i2++;
            packagesForOps = list;
            size = i;
            userProfiles = list2;
        }
        return arrayList;
    }

    public List<Access> getAppListSorted(boolean z) {
        List<Access> appList = getAppList(z);
        Collections.sort(appList, Collections.reverseOrder(new Comparator<Access>() { // from class: com.android.settingslib.applications.RecentAppOpsAccess.1
            @Override // java.util.Comparator
            public int compare(Access access, Access access2) {
                return Long.compare(access.accessFinishTime, access2.accessFinishTime);
            }
        }));
        return appList;
    }

    private Access getAccessFromOps(long j, AppOpsManager.PackageOps packageOps) {
        long j2;
        String packageName = packageOps.getPackageName();
        long j3 = j - 86400000;
        long j4 = 0;
        loop0: while (true) {
            j2 = j4;
            for (AppOpsManager.OpEntry opEntry : packageOps.getOps()) {
                j4 = opEntry.getLastAccessTime(13);
                if (j4 > j2) {
                    break;
                }
            }
        }
        if (j2 < j3) {
            return null;
        }
        int userId = UserHandle.getUserId(packageOps.getUid());
        try {
            ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(packageName, 128, userId);
            if (applicationInfoAsUser == null) {
                String str = TAG;
                Log.w(str, "Null application info retrieved for package " + packageName + ", userId " + userId);
                return null;
            }
            UserHandle userHandle = new UserHandle(userId);
            Drawable badgedIcon = this.mDrawableFactory.getBadgedIcon(applicationInfoAsUser, userId);
            CharSequence applicationLabel = this.mPackageManager.getApplicationLabel(applicationInfoAsUser);
            CharSequence userBadgedLabel = this.mPackageManager.getUserBadgedLabel(applicationLabel, userHandle);
            return new Access(packageName, userHandle, badgedIcon, applicationLabel, applicationLabel.toString().contentEquals(userBadgedLabel) ? null : userBadgedLabel, j2);
        } catch (PackageManager.NameNotFoundException unused) {
            String str2 = TAG;
            Log.w(str2, "package name not found for " + packageName + ", userId " + userId);
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static class Access {
        public final long accessFinishTime;
        public final CharSequence contentDescription;
        public final Drawable icon;
        public final CharSequence label;
        public final String packageName;
        public final UserHandle userHandle;

        public Access(String str, UserHandle userHandle, Drawable drawable, CharSequence charSequence, CharSequence charSequence2, long j) {
            this.packageName = str;
            this.userHandle = userHandle;
            this.icon = drawable;
            this.label = charSequence;
            this.contentDescription = charSequence2;
            this.accessFinishTime = j;
        }
    }
}
