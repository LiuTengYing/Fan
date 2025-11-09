package com.android.settings.applications;

import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.compat.CompatChanges;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public class AppStateAlarmsAndRemindersBridge extends AppStateBaseBridge {
    public static final ApplicationsState.AppFilter FILTER_CLOCK_APPS = new ApplicationsState.AppFilter() { // from class: com.android.settings.applications.AppStateAlarmsAndRemindersBridge.1
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            Object obj = appEntry.extraInfo;
            if (obj instanceof AlarmsAndRemindersState) {
                return ((AlarmsAndRemindersState) obj).shouldBeVisible();
            }
            return false;
        }
    };
    @VisibleForTesting
    AlarmManager mAlarmManager;
    @VisibleForTesting
    String[] mRequesterPackages;

    public AppStateAlarmsAndRemindersBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback) {
        super(applicationsState, callback);
        this.mAlarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
        try {
            this.mRequesterPackages = AppGlobals.getPackageManager().getAppOpPermissionPackages("android.permission.SCHEDULE_EXACT_ALARM");
        } catch (RemoteException e) {
            Log.e("AlarmsAndRemindersBridge", "Cannot reach package manager", e);
            this.mRequesterPackages = EmptyArray.STRING;
        }
    }

    private boolean isChangeEnabled(String str, int i) {
        return CompatChanges.isChangeEnabled(171306433L, str, UserHandle.of(i));
    }

    public AlarmsAndRemindersState createPermissionState(String str, int i) {
        int userId = UserHandle.getUserId(i);
        return new AlarmsAndRemindersState(ArrayUtils.contains(this.mRequesterPackages, str) && isChangeEnabled(str, userId), this.mAlarmManager.hasScheduleExactAlarm(str, userId));
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        appEntry.extraInfo = createPermissionState(str, i);
    }

    @Override // com.android.settings.applications.AppStateBaseBridge
    protected void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        for (int i = 0; i < allApps.size(); i++) {
            ApplicationsState.AppEntry appEntry = allApps.get(i);
            ApplicationInfo applicationInfo = appEntry.info;
            updateExtraInfo(appEntry, applicationInfo.packageName, applicationInfo.uid);
        }
    }

    /* loaded from: classes.dex */
    public static class AlarmsAndRemindersState {
        private boolean mPermissionGranted;
        private boolean mPermissionRequested;

        AlarmsAndRemindersState(boolean z, boolean z2) {
            this.mPermissionRequested = z;
            this.mPermissionGranted = z2;
        }

        public boolean shouldBeVisible() {
            return this.mPermissionRequested;
        }

        public boolean isAllowed() {
            return this.mPermissionGranted;
        }
    }
}
