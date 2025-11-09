package com.android.settingslib.applications;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.hardware.usb.IUsbManager;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;
import java.io.File;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class AppUtils {
    private static final Intent sBrowserIntent = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:"));

    public static boolean hasUsbDefaults(IUsbManager iUsbManager, String str) {
        if (iUsbManager != null) {
            try {
                return iUsbManager.hasDefaults(str, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.e("AppUtils", "mUsbManager.hasDefaults", e);
                return false;
            }
        }
        return false;
    }

    public static boolean hasPreferredActivities(PackageManager packageManager, String str) {
        ArrayList arrayList = new ArrayList();
        packageManager.getPreferredActivities(new ArrayList(), arrayList, str);
        Log.d("AppUtils", "Have " + arrayList.size() + " number of activities in preferred list");
        return arrayList.size() > 0;
    }

    public static boolean isInstant(ApplicationInfo applicationInfo) {
        String[] split;
        if (applicationInfo.isInstantApp()) {
            return true;
        }
        String str = SystemProperties.get("settingsdebug.instant.packages");
        if (str != null && !str.isEmpty() && applicationInfo.packageName != null && (split = str.split(",")) != null) {
            for (String str2 : split) {
                if (applicationInfo.packageName.contains(str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static CharSequence getApplicationLabel(PackageManager packageManager, String str) {
        return com.android.settingslib.utils.applications.AppUtils.getApplicationLabel(packageManager, str);
    }

    public static boolean isHiddenSystemModule(Context context, String str) {
        return ApplicationsState.getInstance((Application) context.getApplicationContext()).isHiddenModule(str);
    }

    public static boolean isSystemModule(Context context, String str) {
        return ApplicationsState.getInstance((Application) context.getApplicationContext()).isSystemModule(str);
    }

    public static boolean isMainlineModule(PackageManager packageManager, String str) {
        try {
            try {
                packageManager.getModuleInfo(str, 0);
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                return packageManager.getPackageInfo(str, 0).applicationInfo.sourceDir.startsWith(Environment.getApexDirectory().getAbsolutePath());
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            return false;
        }
    }

    public static boolean isBrowserApp(Context context, String str, int i) {
        Intent intent = sBrowserIntent;
        intent.setPackage(str);
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivitiesAsUser(intent, 131072, i)) {
            if (resolveInfo.activityInfo != null && resolveInfo.handleAllWebDataURI) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDefaultBrowser(Context context, String str) {
        return TextUtils.equals(str, context.getPackageManager().getDefaultBrowserPackageNameAsUser(UserHandle.myUserId()));
    }

    public static Drawable getIcon(Context context, ApplicationsState.AppEntry appEntry) {
        File file;
        if (appEntry == null || appEntry.info == null) {
            return null;
        }
        AppIconCacheManager appIconCacheManager = AppIconCacheManager.getInstance();
        ApplicationInfo applicationInfo = appEntry.info;
        String str = applicationInfo.packageName;
        int i = applicationInfo.uid;
        Drawable drawable = appIconCacheManager.get(str, i);
        if (drawable == null) {
            File file2 = appEntry.apkFile;
            if (file2 != null && file2.exists()) {
                Drawable badgedIcon = Utils.getBadgedIcon(context, appEntry.info);
                appIconCacheManager.put(str, i, badgedIcon);
                return badgedIcon;
            }
            setAppEntryMounted(appEntry, false);
            return context.getDrawable(17303687);
        } else if (appEntry.mounted || (file = appEntry.apkFile) == null || !file.exists()) {
            return drawable;
        } else {
            setAppEntryMounted(appEntry, true);
            Drawable badgedIcon2 = Utils.getBadgedIcon(context, appEntry.info);
            appIconCacheManager.put(str, i, badgedIcon2);
            return badgedIcon2;
        }
    }

    public static Drawable getIconFromCache(ApplicationsState.AppEntry appEntry) {
        if (appEntry == null || appEntry.info == null) {
            return null;
        }
        AppIconCacheManager appIconCacheManager = AppIconCacheManager.getInstance();
        ApplicationInfo applicationInfo = appEntry.info;
        return appIconCacheManager.get(applicationInfo.packageName, applicationInfo.uid);
    }

    public static void preloadTopIcons(final Context context, ArrayList<ApplicationsState.AppEntry> arrayList, int i) {
        if (arrayList == null || arrayList.isEmpty() || i <= 0) {
            return;
        }
        for (int i2 = 0; i2 < Math.min(arrayList.size(), i); i2++) {
            final ApplicationsState.AppEntry appEntry = arrayList.get(i2);
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settingslib.applications.AppUtils$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AppUtils.getIcon(context, appEntry);
                }
            });
        }
    }

    public static boolean isAppInstalled(ApplicationsState.AppEntry appEntry) {
        ApplicationInfo applicationInfo;
        return (appEntry == null || (applicationInfo = appEntry.info) == null || (applicationInfo.flags & 8388608) == 0) ? false : true;
    }

    private static void setAppEntryMounted(ApplicationsState.AppEntry appEntry, boolean z) {
        if (appEntry.mounted != z) {
            synchronized (appEntry) {
                appEntry.mounted = z;
            }
        }
    }
}
