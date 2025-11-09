package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.LocaleConfig;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.LocaleList;
import android.util.FeatureFlagUtils;
import android.util.Log;
import com.android.settings.R$array;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class AppLocaleUtil {
    public static final Intent LAUNCHER_ENTRY_INTENT = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
    private static final String TAG = "AppLocaleUtil";

    public static boolean canDisplayLocaleUi(Context context, String str, List<ResolveInfo> list) {
        boolean isDisallowedPackage = isDisallowedPackage(context, str);
        boolean hasLauncherEntry = hasLauncherEntry(str, list);
        boolean isSignedWithPlatformKey = isSignedWithPlatformKey(context, str);
        boolean z = !isDisallowedPackage && !isSignedWithPlatformKey && hasLauncherEntry && isAppLocaleSupported(context, str);
        String str2 = TAG;
        Log.i(str2, "Can display preference - [" + str + "] : isDisallowedPackage : " + isDisallowedPackage + " / isSignedWithPlatformKey : " + isSignedWithPlatformKey + " / hasLauncherEntry : " + hasLauncherEntry + " / canDisplay : " + z);
        return z;
    }

    private static boolean isDisallowedPackage(Context context, String str) {
        for (String str2 : context.getResources().getStringArray(R$array.config_disallowed_app_localeChange_packages)) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSignedWithPlatformKey(Context context, String str) {
        PackageInfo packageInfo;
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        try {
            packageInfo = packageManager.getPackageInfoAsUser(str, 0, ActivityManager.getCurrentUser());
        } catch (PackageManager.NameNotFoundException unused) {
            String str2 = TAG;
            Log.e(str2, "package not found: " + str);
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        }
        return packageInfo.applicationInfo.isSignedWithPlatformKey();
    }

    private static boolean hasLauncherEntry(final String str, List<ResolveInfo> list) {
        return list.stream().anyMatch(new Predicate() { // from class: com.android.settings.applications.AppLocaleUtil$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$hasLauncherEntry$0;
                lambda$hasLauncherEntry$0 = AppLocaleUtil.lambda$hasLauncherEntry$0(str, (ResolveInfo) obj);
                return lambda$hasLauncherEntry$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$hasLauncherEntry$0(String str, ResolveInfo resolveInfo) {
        return resolveInfo.activityInfo.packageName.equals(str);
    }

    public static boolean isAppLocaleSupported(Context context, String str) {
        LocaleList packageLocales = getPackageLocales(context, str);
        if (packageLocales == null || packageLocales.size() <= 0) {
            return !FeatureFlagUtils.isEnabled(context, "settings_app_locale_opt_in_enabled") && getAssetLocales(context, str).length > 0;
        }
        return true;
    }

    public static String[] getAssetLocales(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String[] nonSystemLocales = packageManager.getResourcesForApplication(packageManager.getPackageInfo(str, 131072).applicationInfo).getAssets().getNonSystemLocales();
            if (nonSystemLocales == null) {
                String str2 = TAG;
                Log.i(str2, "[" + str + "] locales are null.");
            }
            if (nonSystemLocales.length <= 0) {
                String str3 = TAG;
                Log.i(str3, "[" + str + "] locales length is 0.");
                return new String[0];
            }
            String str4 = nonSystemLocales[0];
            String str5 = TAG;
            Log.i(str5, "First asset locale - [" + str + "] " + str4);
            return nonSystemLocales;
        } catch (PackageManager.NameNotFoundException e) {
            String str6 = TAG;
            Log.w(str6, "Can not found the package name : " + str + " / " + e);
            return new String[0];
        }
    }

    public static LocaleList getPackageLocales(Context context, String str) {
        try {
            LocaleConfig localeConfig = new LocaleConfig(context.createPackageContext(str, 0));
            if (localeConfig.getStatus() == 0) {
                return localeConfig.getSupportedLocales();
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = TAG;
            Log.w(str2, "Can not found the package name : " + str + " / " + e);
            return null;
        }
    }
}
