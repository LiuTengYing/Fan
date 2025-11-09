package com.android.settings.applications;

import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.RecentAppOpsAccess;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class RecentAppStatsMixin implements Comparator<UsageStats>, LifecycleObserver, OnStart {
    private static final Set<String> SKIP_SYSTEM_PACKAGES;
    private final ApplicationsState mApplicationsState;
    private Calendar mCalendar;
    private final Context mContext;
    private final int mMaximumApps;
    private final PackageManager mPm;
    private final PowerManager mPowerManager;
    private final UsageStatsManager mUsageStatsManager;
    private final int mUserId = UserHandle.myUserId();
    final List<UsageStats> mRecentApps = new ArrayList();
    private final List<RecentAppStatsListener> mAppStatsListeners = new ArrayList();

    /* loaded from: classes.dex */
    public interface RecentAppStatsListener {
        void onReloadDataCompleted(List<UsageStats> list);
    }

    static {
        ArraySet arraySet = new ArraySet();
        SKIP_SYSTEM_PACKAGES = arraySet;
        arraySet.addAll(Arrays.asList(RecentAppOpsAccess.ANDROID_SYSTEM_PACKAGE_NAME, "com.android.phone", "com.android.settings", "com.android.systemui", "com.android.providers.calendar", "com.android.providers.media"));
    }

    public RecentAppStatsMixin(Context context, int i) {
        this.mContext = context;
        this.mMaximumApps = i;
        this.mPm = context.getPackageManager();
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mUsageStatsManager = (UsageStatsManager) context.getSystemService(UsageStatsManager.class);
        this.mApplicationsState = ApplicationsState.getInstance((Application) context.getApplicationContext());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.applications.RecentAppStatsMixin$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RecentAppStatsMixin.this.lambda$onStart$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$1() {
        loadDisplayableRecentApps(this.mMaximumApps);
        for (final RecentAppStatsListener recentAppStatsListener : this.mAppStatsListeners) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.applications.RecentAppStatsMixin$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RecentAppStatsMixin.this.lambda$onStart$0(recentAppStatsListener);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(RecentAppStatsListener recentAppStatsListener) {
        recentAppStatsListener.onReloadDataCompleted(this.mRecentApps);
    }

    @Override // java.util.Comparator
    public final int compare(UsageStats usageStats, UsageStats usageStats2) {
        return Long.compare(usageStats2.getLastTimeUsed(), usageStats.getLastTimeUsed());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadDisplayableRecentApps(int i) {
        List<UsageStats> queryUsageStats;
        this.mRecentApps.clear();
        Calendar calendar = Calendar.getInstance();
        this.mCalendar = calendar;
        calendar.add(6, -1);
        if (this.mPowerManager.isPowerSaveMode()) {
            queryUsageStats = new ArrayList<>();
        } else {
            queryUsageStats = this.mUsageStatsManager.queryUsageStats(4, this.mCalendar.getTimeInMillis(), System.currentTimeMillis());
        }
        ArrayMap arrayMap = new ArrayMap();
        int size = queryUsageStats.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            UsageStats usageStats = queryUsageStats.get(i3);
            if (shouldIncludePkgInRecents(usageStats)) {
                String packageName = usageStats.getPackageName();
                UsageStats usageStats2 = (UsageStats) arrayMap.get(packageName);
                if (usageStats2 == null) {
                    arrayMap.put(packageName, usageStats);
                } else {
                    usageStats2.add(usageStats);
                }
            }
        }
        ArrayList<UsageStats> arrayList = new ArrayList();
        arrayList.addAll(arrayMap.values());
        Collections.sort(arrayList, this);
        for (UsageStats usageStats3 : arrayList) {
            if (this.mApplicationsState.getEntry(usageStats3.getPackageName(), this.mUserId) != null) {
                this.mRecentApps.add(usageStats3);
                i2++;
                if (i2 >= i) {
                    return;
                }
            }
        }
    }

    private boolean shouldIncludePkgInRecents(UsageStats usageStats) {
        ApplicationInfo applicationInfo;
        String packageName = usageStats.getPackageName();
        if (usageStats.getLastTimeUsed() < this.mCalendar.getTimeInMillis()) {
            Log.d("RecentAppStatsMixin", "Invalid timestamp (usage time is more than 24 hours ago), skipping " + packageName);
            return false;
        } else if (SKIP_SYSTEM_PACKAGES.contains(packageName)) {
            Log.d("RecentAppStatsMixin", "System package, skipping " + packageName);
            return false;
        } else if (AppUtils.isHiddenSystemModule(this.mContext, packageName)) {
            return false;
        } else {
            if (this.mPm.resolveActivity(new Intent().addCategory("android.intent.category.LAUNCHER").setPackage(packageName), 0) == null) {
                ApplicationsState.AppEntry entry = this.mApplicationsState.getEntry(packageName, this.mUserId);
                if (entry == null || (applicationInfo = entry.info) == null || !AppUtils.isInstant(applicationInfo)) {
                    Log.d("RecentAppStatsMixin", "Not a user visible or instant app, skipping " + packageName);
                    return false;
                }
                return true;
            }
            return true;
        }
    }
}
