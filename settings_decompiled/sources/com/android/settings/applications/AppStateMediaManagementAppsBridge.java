package com.android.settings.applications;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class AppStateMediaManagementAppsBridge extends AppStateAppOpsBridge {
    public static final ApplicationsState.AppFilter FILTER_MEDIA_MANAGEMENT_APPS = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateMediaManagementAppsBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            return appEntry.extraInfo != null;
        }
    };
    private final AppOpsManager mAppOpsManager;

    public AppStateMediaManagementAppsBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(context, applicationsState, callback, AppOpsManager.strOpToOp("android:manage_media"), new String[]{"android.permission.MANAGE_MEDIA"});
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = createPermissionState(str, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateAppOpsBridge, com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        super.loadAllExtraInfo();
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        int size = allApps.size();
        for (int i = 0; i < size; i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            if (appEntry.extraInfo instanceof AppStateAppOpsBridge.PermissionState) {
                ApplicationInfo applicationInfo = appEntry.info;
                updateExtraInfo(appEntry, applicationInfo.packageName, applicationInfo.uid);
            }
        }
    }

    public AppStateAppOpsBridge.PermissionState createPermissionState(String str, int i) {
        AppStateAppOpsBridge.PermissionState permissionInfo = getPermissionInfo(str, i);
        permissionInfo.appOpMode = this.mAppOpsManager.unsafeCheckOpNoThrow("android:manage_media", i, str);
        return permissionInfo;
    }
}
