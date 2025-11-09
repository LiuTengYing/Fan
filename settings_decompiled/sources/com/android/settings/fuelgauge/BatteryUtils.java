package com.android.settings.fuelgauge;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryStatsManager;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.SystemClock;
import android.os.UidBatteryConsumer;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.settings.fuelgauge.batterytip.AnomalyInfo;
import com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import com.android.settingslib.utils.PowerUtil;
import com.android.settingslib.utils.ThreadUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
/* loaded from: classes.dex */
public class BatteryUtils {
    private static BatteryUtils sInstance;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private PackageManager mPackageManager;
    PowerUsageFeatureProvider mPowerUsageFeatureProvider;

    public double calculateBatteryPercent(double d, double d2, int i) {
        if (d2 == 0.0d) {
            return 0.0d;
        }
        return (d / d2) * i;
    }

    public boolean shouldHideDevicePowerComponent(BatteryConsumer batteryConsumer, int i) {
        return i == 0 || i == 2 || i == 8 || i == 11 || i == 16;
    }

    public static BatteryUtils getInstance(Context context) {
        BatteryUtils batteryUtils = sInstance;
        if (batteryUtils == null || batteryUtils.isDataCorrupted()) {
            sInstance = new BatteryUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public BatteryUtils(Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mPowerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
    }

    public boolean shouldHideUidBatteryConsumer(UidBatteryConsumer uidBatteryConsumer) {
        return shouldHideUidBatteryConsumer(uidBatteryConsumer, this.mPackageManager.getPackagesForUid(uidBatteryConsumer.getUid()));
    }

    public boolean shouldHideUidBatteryConsumer(UidBatteryConsumer uidBatteryConsumer, String[] strArr) {
        return this.mPowerUsageFeatureProvider.isTypeSystem(uidBatteryConsumer.getUid(), strArr) || shouldHideUidBatteryConsumerUnconditionally(uidBatteryConsumer, strArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldHideUidBatteryConsumerUnconditionally(UidBatteryConsumer uidBatteryConsumer, String[] strArr) {
        int uid = uidBatteryConsumer.getUid();
        if (uid == -5) {
            return false;
        }
        return uid < 0 || isHiddenSystemModule(strArr);
    }

    public boolean isHiddenSystemModule(String[] strArr) {
        if (strArr != null) {
            for (String str : strArr) {
                if (AppUtils.isHiddenSystemModule(this.mContext, str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getPackageName(int i) {
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return null;
        }
        return packagesForUid[0];
    }

    public long calculateLastFullChargeTime(BatteryUsageStats batteryUsageStats, long j) {
        return j - batteryUsageStats.getStatsStartTimestamp();
    }

    public static void logRuntime(String str, String str2, long j) {
        Log.d(str, str2 + ": " + (System.currentTimeMillis() - j) + "ms");
    }

    public static boolean isBatteryDefenderOn(BatteryInfo batteryInfo) {
        return batteryInfo.isOverheated && !batteryInfo.discharging;
    }

    public int getPackageUid(String str) {
        if (str != null) {
            try {
            } catch (PackageManager.NameNotFoundException unused) {
                return -1;
            }
        }
        return this.mPackageManager.getPackageUid(str, 128);
    }

    public void setForceAppStandby(final int i, final String str, final int i2) {
        if (isPreOApp(str)) {
            this.mAppOpsManager.setMode(63, i, str, i2);
        }
        this.mAppOpsManager.setMode(70, i, str, i2);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.BatteryUtils$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatteryUtils.this.lambda$setForceAppStandby$0(i2, i, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setForceAppStandby$0(int i, int i2, String str) {
        BatteryDatabaseManager batteryDatabaseManager = BatteryDatabaseManager.getInstance(this.mContext);
        if (i == 1) {
            batteryDatabaseManager.insertAction(0, i2, str, System.currentTimeMillis());
        } else if (i == 0) {
            batteryDatabaseManager.deleteAction(0, i2, str);
        }
    }

    public boolean isForceAppStandbyEnabled(int i, String str) {
        return this.mAppOpsManager.checkOpNoThrow(70, i, str) == 1;
    }

    public boolean clearForceAppStandby(String str) {
        int packageUid = getPackageUid(str);
        if (packageUid == -1 || !isForceAppStandbyEnabled(packageUid, str)) {
            return false;
        }
        setForceAppStandby(packageUid, str, 0);
        return true;
    }

    public BatteryInfo getBatteryInfo(String str) {
        BatteryUsageStats build;
        try {
            build = ((BatteryStatsManager) this.mContext.getSystemService(BatteryStatsManager.class)).getBatteryUsageStats(new BatteryUsageStatsQuery.Builder().includeBatteryHistory().build());
        } catch (RuntimeException e) {
            Log.e("BatteryUtils", "getBatteryInfo() error from getBatteryUsageStats()", e);
            build = new BatteryUsageStats.Builder(new String[0]).build();
        }
        long currentTimeMillis = System.currentTimeMillis();
        Intent registerReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        Estimate enhancedEstimate = getEnhancedEstimate();
        if (enhancedEstimate == null) {
            enhancedEstimate = new Estimate(build.getBatteryTimeRemainingMs(), false, -1L);
        }
        logRuntime(str, "BatteryInfoLoader post query", currentTimeMillis);
        BatteryInfo batteryInfo = BatteryInfo.getBatteryInfo(this.mContext, registerReceiver, build, enhancedEstimate, convertMsToUs, false);
        logRuntime(str, "BatteryInfoLoader.loadInBackground", currentTimeMillis);
        try {
            build.close();
        } catch (Exception e2) {
            Log.e("BatteryUtils", "BatteryUsageStats.close() failed", e2);
        }
        return batteryInfo;
    }

    Estimate getEnhancedEstimate() {
        if (Duration.between(Estimate.getLastCacheUpdateTime(this.mContext), Instant.now()).compareTo(Duration.ofSeconds(10L)) < 0) {
            return Estimate.getCachedEstimateIfAvailable(this.mContext);
        }
        PowerUsageFeatureProvider powerUsageFeatureProvider = this.mPowerUsageFeatureProvider;
        if (powerUsageFeatureProvider == null || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(this.mContext)) {
            return null;
        }
        Estimate enhancedBatteryPrediction = this.mPowerUsageFeatureProvider.getEnhancedBatteryPrediction(this.mContext);
        if (enhancedBatteryPrediction != null) {
            Estimate.storeCachedEstimate(this.mContext, enhancedBatteryPrediction);
        }
        return enhancedBatteryPrediction;
    }

    private boolean isDataCorrupted() {
        return this.mPackageManager == null || this.mAppOpsManager == null;
    }

    long getForegroundActivityTotalTimeUs(BatteryStats.Uid uid, long j) {
        BatteryStats.Timer foregroundActivityTimer = uid.getForegroundActivityTimer();
        if (foregroundActivityTimer != null) {
            return foregroundActivityTimer.getTotalTimeLocked(j, 0);
        }
        return 0L;
    }

    long getForegroundServiceTotalTimeUs(BatteryStats.Uid uid, long j) {
        BatteryStats.Timer foregroundServiceTimer = uid.getForegroundServiceTimer();
        if (foregroundServiceTimer != null) {
            return foregroundServiceTimer.getTotalTimeLocked(j, 0);
        }
        return 0L;
    }

    public boolean isPreOApp(String str) {
        try {
            return this.mPackageManager.getApplicationInfo(str, 128).targetSdkVersion < 26;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("BatteryUtils", "Cannot find package: " + str, e);
            return false;
        }
    }

    public boolean isPreOApp(String[] strArr) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        for (String str : strArr) {
            if (isPreOApp(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldHideAnomaly(PowerAllowlistBackend powerAllowlistBackend, int i, AnomalyInfo anomalyInfo) {
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid) || isSystemUid(i) || powerAllowlistBackend.isAllowlisted(packagesForUid)) {
            return true;
        }
        if (!isSystemApp(this.mPackageManager, packagesForUid) || hasLauncherEntry(packagesForUid)) {
            return isExcessiveBackgroundAnomaly(anomalyInfo) && !isPreOApp(packagesForUid);
        }
        return true;
    }

    private boolean isExcessiveBackgroundAnomaly(AnomalyInfo anomalyInfo) {
        return anomalyInfo.anomalyType.intValue() == 4;
    }

    private boolean isSystemUid(int i) {
        int appId = UserHandle.getAppId(i);
        return appId >= 0 && appId < 10000;
    }

    private boolean isSystemApp(PackageManager packageManager, String[] strArr) {
        for (String str : strArr) {
            try {
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("BatteryUtils", "Package not found: " + str, e);
            }
            if ((packageManager.getApplicationInfo(str, 0).flags & 1) != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLauncherEntry(String[] strArr) {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(intent, 1835520);
        int size = queryIntentActivities.size();
        for (int i = 0; i < size; i++) {
            if (ArrayUtils.contains(strArr, queryIntentActivities.get(i).activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public long getAppLongVersionCode(String str) {
        try {
            return this.mPackageManager.getPackageInfo(str, 0).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("BatteryUtils", "Cannot find package: " + str, e);
            return -1L;
        }
    }
}
