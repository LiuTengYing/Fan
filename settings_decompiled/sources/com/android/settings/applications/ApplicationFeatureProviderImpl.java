package com.android.settings.applications;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.location.LocationManager;
import android.os.UserManager;
import android.telecom.DefaultDialerManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.R$string;
import com.android.settings.applications.ApplicationFeatureProvider;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class ApplicationFeatureProviderImpl implements ApplicationFeatureProvider {
    protected final Context mContext;
    private final DevicePolicyManager mDpm;
    private final PackageManager mPm;
    private final IPackageManager mPms;
    private final UserManager mUm;

    public ApplicationFeatureProviderImpl(Context context, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mPm = packageManager;
        this.mPms = iPackageManager;
        this.mDpm = devicePolicyManager;
        this.mUm = UserManager.get(applicationContext);
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void calculateNumberOfPolicyInstalledApps(boolean z, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
        CurrentUserAndManagedProfilePolicyInstalledAppCounter currentUserAndManagedProfilePolicyInstalledAppCounter = new CurrentUserAndManagedProfilePolicyInstalledAppCounter(this.mContext, this.mPm, numberOfAppsCallback);
        if (z) {
            currentUserAndManagedProfilePolicyInstalledAppCounter.execute(new Void[0]);
        } else {
            currentUserAndManagedProfilePolicyInstalledAppCounter.executeInForeground();
        }
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void listPolicyInstalledApps(ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
        new CurrentUserPolicyInstalledAppLister(this.mPm, this.mUm, listOfAppsCallback).execute(new Void[0]);
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void calculateNumberOfAppsWithAdminGrantedPermissions(String[] strArr, boolean z, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
        CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter currentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter = new CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter(this.mContext, strArr, this.mPm, this.mPms, this.mDpm, numberOfAppsCallback);
        if (z) {
            currentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter.execute(new Void[0]);
        } else {
            currentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter.executeInForeground();
        }
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public void listAppsWithAdminGrantedPermissions(String[] strArr, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
        new CurrentUserAppWithAdminGrantedPermissionsLister(strArr, this.mPm, this.mPms, this.mDpm, this.mUm, listOfAppsCallback).execute(new Void[0]);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0031 A[Catch: RemoteException -> 0x0041, TryCatch #0 {RemoteException -> 0x0041, blocks: (B:5:0x0016, B:7:0x001e, B:17:0x0031, B:19:0x003e, B:10:0x0025, B:13:0x002a), top: B:23:0x0016 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0041 A[SYNTHETIC] */
    @Override // com.android.settings.applications.ApplicationFeatureProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.List<com.android.settings.applications.UserAppInfo> findPersistentPreferredActivities(int r9, android.content.Intent[] r10) {
        /*
            r8 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.util.ArraySet r1 = new android.util.ArraySet
            r1.<init>()
            android.os.UserManager r2 = r8.mUm
            android.content.pm.UserInfo r2 = r2.getUserInfo(r9)
            int r3 = r10.length
            r4 = 0
        L12:
            if (r4 >= r3) goto L44
            r5 = r10[r4]
            android.content.pm.IPackageManager r6 = r8.mPms     // Catch: android.os.RemoteException -> L41
            android.content.pm.ResolveInfo r5 = r6.findPersistentPreferredActivity(r5, r9)     // Catch: android.os.RemoteException -> L41
            if (r5 == 0) goto L41
            r6 = 0
            android.content.pm.ActivityInfo r7 = r5.activityInfo     // Catch: android.os.RemoteException -> L41
            if (r7 == 0) goto L25
        L23:
            r6 = r7
            goto L2f
        L25:
            android.content.pm.ServiceInfo r7 = r5.serviceInfo     // Catch: android.os.RemoteException -> L41
            if (r7 == 0) goto L2a
            goto L23
        L2a:
            android.content.pm.ProviderInfo r5 = r5.providerInfo     // Catch: android.os.RemoteException -> L41
            if (r5 == 0) goto L2f
            r6 = r5
        L2f:
            if (r6 == 0) goto L41
            com.android.settings.applications.UserAppInfo r5 = new com.android.settings.applications.UserAppInfo     // Catch: android.os.RemoteException -> L41
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo     // Catch: android.os.RemoteException -> L41
            r5.<init>(r2, r6)     // Catch: android.os.RemoteException -> L41
            boolean r6 = r1.add(r5)     // Catch: android.os.RemoteException -> L41
            if (r6 == 0) goto L41
            r0.add(r5)     // Catch: android.os.RemoteException -> L41
        L41:
            int r4 = r4 + 1
            goto L12
        L44:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.ApplicationFeatureProviderImpl.findPersistentPreferredActivities(int, android.content.Intent[]):java.util.List");
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider
    public Set<String> getKeepEnabledPackages() {
        ArraySet arraySet = new ArraySet();
        String defaultDialerApplication = DefaultDialerManager.getDefaultDialerApplication(this.mContext);
        if (!TextUtils.isEmpty(defaultDialerApplication)) {
            arraySet.add(defaultDialerApplication);
        }
        ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this.mContext, true);
        if (defaultSmsApplication != null) {
            arraySet.add(defaultSmsApplication.getPackageName());
        }
        ComponentInfo findEuiccService = findEuiccService(this.mPm);
        if (findEuiccService != null) {
            arraySet.add(findEuiccService.packageName);
        }
        arraySet.addAll(getEnabledPackageAllowlist());
        String extraLocationControllerPackage = ((LocationManager) this.mContext.getSystemService("location")).getExtraLocationControllerPackage();
        if (extraLocationControllerPackage != null) {
            arraySet.add(extraLocationControllerPackage);
        }
        return arraySet;
    }

    private Set<String> getEnabledPackageAllowlist() {
        ArraySet arraySet = new ArraySet();
        arraySet.add(this.mContext.getString(R$string.config_settingsintelligence_package_name));
        arraySet.add(this.mContext.getString(R$string.config_package_installer_package_name));
        if (this.mPm.getWellbeingPackageName() != null) {
            arraySet.add(this.mPm.getWellbeingPackageName());
        }
        return arraySet;
    }

    /* loaded from: classes.dex */
    private static class CurrentUserAndManagedProfilePolicyInstalledAppCounter extends InstalledAppCounter {
        private ApplicationFeatureProvider.NumberOfAppsCallback mCallback;

        CurrentUserAndManagedProfilePolicyInstalledAppCounter(Context context, PackageManager packageManager, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
            super(context, 1, packageManager);
            this.mCallback = numberOfAppsCallback;
        }

        @Override // com.android.settings.applications.AppCounter
        protected void onCountComplete(int i) {
            this.mCallback.onNumberOfAppsResult(i);
        }
    }

    /* loaded from: classes.dex */
    private static class CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter extends AppWithAdminGrantedPermissionsCounter {
        private ApplicationFeatureProvider.NumberOfAppsCallback mCallback;

        CurrentUserAndManagedProfileAppWithAdminGrantedPermissionsCounter(Context context, String[] strArr, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager, ApplicationFeatureProvider.NumberOfAppsCallback numberOfAppsCallback) {
            super(context, strArr, packageManager, iPackageManager, devicePolicyManager);
            this.mCallback = numberOfAppsCallback;
        }

        @Override // com.android.settings.applications.AppCounter
        protected void onCountComplete(int i) {
            this.mCallback.onNumberOfAppsResult(i);
        }
    }

    /* loaded from: classes.dex */
    private static class CurrentUserPolicyInstalledAppLister extends InstalledAppLister {
        private ApplicationFeatureProvider.ListOfAppsCallback mCallback;

        CurrentUserPolicyInstalledAppLister(PackageManager packageManager, UserManager userManager, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super(packageManager, userManager);
            this.mCallback = listOfAppsCallback;
        }

        @Override // com.android.settings.applications.AppLister
        protected void onAppListBuilt(List<UserAppInfo> list) {
            this.mCallback.onListOfAppsResult(list);
        }
    }

    /* loaded from: classes.dex */
    private static class CurrentUserAppWithAdminGrantedPermissionsLister extends AppWithAdminGrantedPermissionsLister {
        private ApplicationFeatureProvider.ListOfAppsCallback mCallback;

        CurrentUserAppWithAdminGrantedPermissionsLister(String[] strArr, PackageManager packageManager, IPackageManager iPackageManager, DevicePolicyManager devicePolicyManager, UserManager userManager, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super(strArr, packageManager, iPackageManager, devicePolicyManager, userManager);
            this.mCallback = listOfAppsCallback;
        }

        @Override // com.android.settings.applications.AppLister
        protected void onAppListBuilt(List<UserAppInfo> list) {
            this.mCallback.onListOfAppsResult(list);
        }
    }

    ComponentInfo findEuiccService(PackageManager packageManager) {
        ComponentInfo findEuiccService = findEuiccService(packageManager, packageManager.queryIntentServices(new Intent("android.service.euicc.EuiccService"), 269484096));
        if (findEuiccService == null) {
            Log.w("AppFeatureProviderImpl", "No valid EuiccService implementation found");
        }
        return findEuiccService;
    }

    private ComponentInfo findEuiccService(PackageManager packageManager, List<ResolveInfo> list) {
        ComponentInfo componentInfo = null;
        if (list != null) {
            int i = Integer.MIN_VALUE;
            for (ResolveInfo resolveInfo : list) {
                if (isValidEuiccComponent(packageManager, resolveInfo) && resolveInfo.filter.getPriority() > i) {
                    i = resolveInfo.filter.getPriority();
                    componentInfo = getComponentInfo(resolveInfo);
                }
            }
        }
        return componentInfo;
    }

    private boolean isValidEuiccComponent(PackageManager packageManager, ResolveInfo resolveInfo) {
        String str;
        ComponentInfo componentInfo = getComponentInfo(resolveInfo);
        String str2 = componentInfo.packageName;
        if (packageManager.checkPermission("android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS", str2) != 0) {
            Log.e("AppFeatureProviderImpl", "Package " + str2 + " does not declare WRITE_EMBEDDED_SUBSCRIPTIONS");
            return false;
        }
        if (componentInfo instanceof ServiceInfo) {
            str = ((ServiceInfo) componentInfo).permission;
        } else if (componentInfo instanceof ActivityInfo) {
            str = ((ActivityInfo) componentInfo).permission;
        } else {
            throw new IllegalArgumentException("Can only verify services/activities");
        }
        if (!TextUtils.equals(str, "android.permission.BIND_EUICC_SERVICE")) {
            Log.e("AppFeatureProviderImpl", "Package " + str2 + " does not require the BIND_EUICC_SERVICE permission");
            return false;
        }
        IntentFilter intentFilter = resolveInfo.filter;
        if (intentFilter == null || intentFilter.getPriority() == 0) {
            Log.e("AppFeatureProviderImpl", "Package " + str2 + " does not specify a priority");
            return false;
        }
        return true;
    }

    private ComponentInfo getComponentInfo(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        if (activityInfo != null) {
            return activityInfo;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        if (serviceInfo != null) {
            return serviceInfo;
        }
        ProviderInfo providerInfo = resolveInfo.providerInfo;
        if (providerInfo != null) {
            return providerInfo;
        }
        throw new IllegalStateException("Missing ComponentInfo!");
    }
}
