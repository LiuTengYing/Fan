package com.android.settings.fuelgauge;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
/* loaded from: classes.dex */
public class BatteryOptimizeUtils {
    boolean mAllowListed;
    AppOpsManager mAppOpsManager;
    BatteryUtils mBatteryUtils;
    int mMode;
    private final String mPackageName;
    PowerAllowlistBackend mPowerAllowListBackend;
    private final int mUid;

    public static int getAppOptimizationMode(int i, boolean z) {
        if (z || i != 1) {
            if (z && i == 0) {
                return 2;
            }
            return (z || i != 0) ? 0 : 3;
        }
        return 1;
    }

    public BatteryOptimizeUtils(Context context, int i, String str) {
        this.mUid = i;
        this.mPackageName = str;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mPowerAllowListBackend = PowerAllowlistBackend.getInstance(context);
        this.mMode = this.mAppOpsManager.checkOpNoThrow(70, i, str);
        this.mAllowListed = this.mPowerAllowListBackend.isAllowlisted(str);
    }

    public int getAppOptimizationMode() {
        refreshState();
        return getAppOptimizationMode(this.mMode, this.mAllowListed);
    }

    public void setAppUsageState(final int i) {
        if (getAppOptimizationMode(this.mMode, this.mAllowListed) == i) {
            Log.w("BatteryOptimizeUtils", "set the same optimization mode for: " + this.mPackageName);
            return;
        }
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.fuelgauge.BatteryOptimizeUtils$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatteryOptimizeUtils.this.lambda$setAppUsageState$0(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAppUsageState$0(int i) {
        if (i == 1) {
            setAppOptimizationMode(1, false);
        } else if (i == 2) {
            setAppOptimizationMode(0, true);
        } else if (i == 3) {
            setAppOptimizationMode(0, false);
        } else {
            Log.d("BatteryOptimizeUtils", "set unknown app optimization mode.");
        }
    }

    public boolean isValidPackageName() {
        return this.mBatteryUtils.getPackageUid(this.mPackageName) != -1;
    }

    public boolean isSystemOrDefaultApp() {
        this.mPowerAllowListBackend.refreshList();
        return this.mPowerAllowListBackend.isSysAllowlisted(this.mPackageName) || this.mPowerAllowListBackend.isDefaultActiveApp(this.mPackageName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getPackageName() {
        String str = this.mPackageName;
        return str == null ? "unknown" : str;
    }

    private void setAppOptimizationMode(int i, boolean z) {
        try {
            this.mBatteryUtils.setForceAppStandby(this.mUid, this.mPackageName, i);
            if (z) {
                this.mPowerAllowListBackend.addApp(this.mPackageName);
            } else {
                this.mPowerAllowListBackend.removeApp(this.mPackageName);
            }
        } catch (Exception e) {
            Log.e("BatteryOptimizeUtils", "set OPTIMIZED failed for " + this.mPackageName, e);
        }
    }

    private void refreshState() {
        this.mPowerAllowListBackend.refreshList();
        this.mAllowListed = this.mPowerAllowListBackend.isAllowlisted(this.mPackageName);
        this.mMode = this.mAppOpsManager.checkOpNoThrow(70, this.mUid, this.mPackageName);
        Log.d("BatteryOptimizeUtils", String.format("refresh %s state, allowlisted = %s, mode = %d", this.mPackageName, Boolean.valueOf(this.mAllowListed), Integer.valueOf(this.mMode)));
    }
}
