package com.android.settingslib.net;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.TetheringManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
/* loaded from: classes2.dex */
public class UidDetailProvider {
    private final Context mContext;
    private final SparseArray<UidDetail> mUidDetailCache = new SparseArray<>();

    public static int buildKeyForUser(int i) {
        return (-2000) - i;
    }

    public static int getUserIdForKey(int i) {
        return (-2000) - i;
    }

    public static boolean isKeyForUser(int i) {
        return i <= -2000;
    }

    public UidDetailProvider(Context context) {
        this.mContext = context;
    }

    public void clearCache() {
        synchronized (this.mUidDetailCache) {
            this.mUidDetailCache.clear();
        }
    }

    public UidDetail getUidDetail(int i, boolean z) {
        UidDetail uidDetail;
        synchronized (this.mUidDetailCache) {
            uidDetail = this.mUidDetailCache.get(i);
        }
        if (uidDetail != null) {
            return uidDetail;
        }
        if (z) {
            UidDetail buildUidDetail = buildUidDetail(i);
            synchronized (this.mUidDetailCache) {
                this.mUidDetailCache.put(i, buildUidDetail);
            }
            return buildUidDetail;
        }
        return null;
    }

    private UidDetail buildUidDetail(int i) {
        int i2;
        UserInfo userInfo;
        Resources resources = this.mContext.getResources();
        PackageManager packageManager = this.mContext.getPackageManager();
        UidDetail uidDetail = new UidDetail();
        uidDetail.label = packageManager.getNameForUid(i);
        uidDetail.icon = packageManager.getDefaultActivityIcon();
        if (i == -5) {
            uidDetail.label = resources.getString(Utils.getTetheringLabel((TetheringManager) this.mContext.getSystemService(TetheringManager.class)));
            uidDetail.icon = packageManager.getDefaultActivityIcon();
            return uidDetail;
        } else if (i == -4) {
            if (UserManager.supportsMultipleUsers()) {
                i2 = R$string.data_usage_uninstalled_apps_users;
            } else {
                i2 = R$string.data_usage_uninstalled_apps;
            }
            uidDetail.label = resources.getString(i2);
            uidDetail.icon = packageManager.getDefaultActivityIcon();
            return uidDetail;
        } else if (i == 1000) {
            uidDetail.label = resources.getString(R$string.process_kernel_label);
            uidDetail.icon = packageManager.getDefaultActivityIcon();
            return uidDetail;
        } else if (i == 1061) {
            uidDetail.label = resources.getString(R$string.data_usage_ota);
            uidDetail.icon = this.mContext.getDrawable(R$drawable.ic_system_update);
            return uidDetail;
        } else {
            UserManager userManager = (UserManager) this.mContext.getSystemService("user");
            if (isKeyForUser(i) && (userInfo = userManager.getUserInfo(getUserIdForKey(i))) != null) {
                uidDetail.label = Utils.getUserLabel(this.mContext, userInfo);
                uidDetail.icon = Utils.getUserIcon(this.mContext, userManager, userInfo);
                return uidDetail;
            }
            String[] packagesForUid = packageManager.getPackagesForUid(i);
            int i3 = 0;
            int length = packagesForUid != null ? packagesForUid.length : 0;
            try {
                int userId = UserHandle.getUserId(i);
                UserHandle userHandle = new UserHandle(userId);
                IPackageManager packageManager2 = AppGlobals.getPackageManager();
                if (length == 1) {
                    ApplicationInfo applicationInfo = packageManager2.getApplicationInfo(packagesForUid[0], 0L, userId);
                    if (applicationInfo != null) {
                        uidDetail.label = applicationInfo.loadLabel(packageManager).toString();
                        uidDetail.icon = userManager.getBadgedIconForUser(applicationInfo.loadIcon(packageManager), new UserHandle(userId));
                    }
                } else if (length > 1) {
                    uidDetail.detailLabels = new CharSequence[length];
                    uidDetail.detailContentDescriptions = new CharSequence[length];
                    int i4 = 0;
                    while (i4 < length) {
                        String str = packagesForUid[i4];
                        PackageInfo packageInfo = packageManager.getPackageInfo(str, i3);
                        String[] strArr = packagesForUid;
                        int i5 = length;
                        ApplicationInfo applicationInfo2 = packageManager2.getApplicationInfo(str, 0L, userId);
                        if (applicationInfo2 != null) {
                            uidDetail.detailLabels[i4] = applicationInfo2.loadLabel(packageManager).toString();
                            uidDetail.detailContentDescriptions[i4] = userManager.getBadgedLabelForUser(uidDetail.detailLabels[i4], userHandle);
                            int i6 = packageInfo.sharedUserLabel;
                            if (i6 != 0) {
                                uidDetail.label = packageManager.getText(str, i6, packageInfo.applicationInfo).toString();
                                uidDetail.icon = userManager.getBadgedIconForUser(applicationInfo2.loadIcon(packageManager), userHandle);
                            }
                        }
                        i4++;
                        length = i5;
                        packagesForUid = strArr;
                        i3 = 0;
                    }
                }
                uidDetail.contentDescription = userManager.getBadgedLabelForUser(uidDetail.label, userHandle);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("DataUsage", "Error while building UI detail for uid " + i, e);
            } catch (RemoteException e2) {
                Log.w("DataUsage", "Error while building UI detail for uid " + i, e2);
            }
            if (TextUtils.isEmpty(uidDetail.label)) {
                uidDetail.label = Integer.toString(i);
            }
            return uidDetail;
        }
    }
}
