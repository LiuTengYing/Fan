package com.android.settings.fuelgauge;

import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.IDeviceIdleController;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public final class BatteryBackupHelper implements BackupHelper {
    private static final boolean DEBUG = Build.TYPE.equals("userdebug");
    BatteryOptimizeUtils mBatteryOptimizeUtils;
    private final Context mContext;
    IDeviceIdleController mIDeviceIdleController;
    IPackageManager mIPackageManager;
    PowerAllowlistBackend mPowerAllowlistBackend;
    List<ApplicationInfo> mTestApplicationInfoList = null;

    @Override // android.app.backup.BackupHelper
    public void writeNewStateDescription(ParcelFileDescriptor parcelFileDescriptor) {
    }

    public BatteryBackupHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) {
        if (!isOwner() || backupDataOutput == null) {
            Log.w("BatteryBackupHelper", "ignore performBackup() for non-owner or empty data");
            return;
        }
        List<String> backupFullPowerList = backupFullPowerList(backupDataOutput);
        if (backupFullPowerList != null) {
            backupOptimizationMode(backupDataOutput, backupFullPowerList);
        }
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream backupDataInputStream) {
        if (!isOwner() || backupDataInputStream == null || backupDataInputStream.size() == 0) {
            Log.w("BatteryBackupHelper", "ignore restoreEntity() for non-owner or empty data");
        } else if ("optimization_mode_list".equals(backupDataInputStream.getKey())) {
            int size = backupDataInputStream.size();
            byte[] bArr = new byte[size];
            try {
                backupDataInputStream.read(bArr, 0, size);
                restoreOptimizationMode(bArr);
            } catch (IOException e) {
                Log.e("BatteryBackupHelper", "failed to load BackupDataInputStream", e);
            }
        }
    }

    private List<String> backupFullPowerList(BackupDataOutput backupDataOutput) {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            String[] fullPowerWhitelist = getIDeviceIdleController().getFullPowerWhitelist();
            if (fullPowerWhitelist == null || fullPowerWhitelist.length == 0) {
                Log.w("BatteryBackupHelper", "no data found in the getFullPowerList()");
                return new ArrayList();
            }
            writeBackupData(backupDataOutput, "full_power_list", String.join(",", fullPowerWhitelist));
            Log.d("BatteryBackupHelper", String.format("backup getFullPowerList() size=%d in %d/ms", Integer.valueOf(fullPowerWhitelist.length), Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
            return Arrays.asList(fullPowerWhitelist);
        } catch (RemoteException e) {
            Log.e("BatteryBackupHelper", "backupFullPowerList() failed", e);
            return null;
        }
    }

    void backupOptimizationMode(BackupDataOutput backupDataOutput, List<String> list) {
        long currentTimeMillis = System.currentTimeMillis();
        List<ApplicationInfo> installedApplications = getInstalledApplications();
        if (installedApplications == null || installedApplications.isEmpty()) {
            Log.w("BatteryBackupHelper", "no data found in the getInstalledApplications()");
            return;
        }
        StringBuilder sb = new StringBuilder();
        AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        int i = 0;
        for (ApplicationInfo applicationInfo : installedApplications) {
            int appOptimizationMode = BatteryOptimizeUtils.getAppOptimizationMode(appOpsManager.checkOpNoThrow(70, applicationInfo.uid, applicationInfo.packageName), list.contains(applicationInfo.packageName));
            if (appOptimizationMode != 3 && appOptimizationMode != 0 && !isSystemOrDefaultApp(applicationInfo.packageName)) {
                String str = applicationInfo.packageName + ":" + appOptimizationMode;
                sb.append(str + ",");
                debugLog(str);
                i++;
            }
        }
        writeBackupData(backupDataOutput, "optimization_mode_list", sb.toString());
        Log.d("BatteryBackupHelper", String.format("backup getInstalledApplications():%d count=%d in %d/ms", Integer.valueOf(installedApplications.size()), Integer.valueOf(i), Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
    }

    void restoreOptimizationMode(byte[] bArr) {
        long currentTimeMillis = System.currentTimeMillis();
        String str = new String(bArr, StandardCharsets.UTF_8);
        if (str.isEmpty()) {
            Log.w("BatteryBackupHelper", "no data found in the restoreOptimizationMode()");
            return;
        }
        String[] split = str.split(",");
        if (split == null || split.length == 0) {
            Log.w("BatteryBackupHelper", "no data found from the split() processing");
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < split.length; i2++) {
            String[] split2 = split[i2].split(":");
            if (split2 == null || split2.length != 2) {
                Log.w("BatteryBackupHelper", "invalid raw data found:" + split[i2]);
            } else {
                String str2 = split2[0];
                if (isSystemOrDefaultApp(str2)) {
                    Log.w("BatteryBackupHelper", "ignore from isSystemOrDefaultApp():" + str2);
                } else {
                    try {
                        restoreOptimizationMode(str2, Integer.parseInt(split2[1]));
                        i++;
                    } catch (NumberFormatException e) {
                        Log.e("BatteryBackupHelper", "failed to parse the optimization mode: " + split[i2], e);
                    }
                }
            }
        }
        Log.d("BatteryBackupHelper", String.format("restoreOptimizationMode() count=%d in %d/ms", Integer.valueOf(i), Long.valueOf(System.currentTimeMillis() - currentTimeMillis)));
    }

    private void restoreOptimizationMode(String str, int i) {
        int packageUid = BatteryUtils.getInstance(this.mContext).getPackageUid(str);
        if (packageUid == -1) {
            return;
        }
        BatteryOptimizeUtils batteryOptimizeUtils = this.mBatteryOptimizeUtils;
        if (batteryOptimizeUtils == null) {
            batteryOptimizeUtils = new BatteryOptimizeUtils(this.mContext, packageUid, str);
        }
        batteryOptimizeUtils.setAppUsageState(i);
        Log.d("BatteryBackupHelper", String.format("restore:%s mode=%d", str, Integer.valueOf(i)));
    }

    private IDeviceIdleController getIDeviceIdleController() {
        IDeviceIdleController iDeviceIdleController = this.mIDeviceIdleController;
        if (iDeviceIdleController != null) {
            return iDeviceIdleController;
        }
        IDeviceIdleController asInterface = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
        this.mIDeviceIdleController = asInterface;
        return asInterface;
    }

    private IPackageManager getIPackageManager() {
        IPackageManager iPackageManager = this.mIPackageManager;
        if (iPackageManager != null) {
            return iPackageManager;
        }
        IPackageManager packageManager = AppGlobals.getPackageManager();
        this.mIPackageManager = packageManager;
        return packageManager;
    }

    private PowerAllowlistBackend getPowerAllowlistBackend() {
        PowerAllowlistBackend powerAllowlistBackend = this.mPowerAllowlistBackend;
        if (powerAllowlistBackend != null) {
            return powerAllowlistBackend;
        }
        PowerAllowlistBackend powerAllowlistBackend2 = PowerAllowlistBackend.getInstance(this.mContext);
        this.mPowerAllowlistBackend = powerAllowlistBackend2;
        return powerAllowlistBackend2;
    }

    private boolean isSystemOrDefaultApp(String str) {
        PowerAllowlistBackend powerAllowlistBackend = getPowerAllowlistBackend();
        return powerAllowlistBackend.isSysAllowlisted(str) || powerAllowlistBackend.isDefaultActiveApp(str);
    }

    private List<ApplicationInfo> getInstalledApplications() {
        List<ApplicationInfo> list = this.mTestApplicationInfoList;
        if (list != null) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        for (UserInfo userInfo : ((UserManager) this.mContext.getSystemService(UserManager.class)).getProfiles(UserHandle.myUserId())) {
            try {
                ParceledListSlice installedApplications = getIPackageManager().getInstalledApplications(userInfo.isAdmin() ? 4227584L : 33280L, userInfo.id);
                if (installedApplications != null) {
                    arrayList.addAll(installedApplications.getList());
                }
            } catch (Exception e) {
                Log.e("BatteryBackupHelper", "getInstalledApplications() is failed", e);
                return null;
            }
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ApplicationInfo applicationInfo = (ApplicationInfo) arrayList.get(size);
            if (applicationInfo.enabledSetting != 3 && !applicationInfo.enabled) {
                arrayList.remove(size);
            }
        }
        return arrayList;
    }

    private void debugLog(String str) {
        if (DEBUG) {
            Log.d("BatteryBackupHelper", str);
        }
    }

    private static void writeBackupData(BackupDataOutput backupDataOutput, String str, String str2) {
        byte[] bytes = str2.getBytes();
        try {
            backupDataOutput.writeEntityHeader(str, bytes.length);
            backupDataOutput.writeEntityData(bytes, bytes.length);
        } catch (IOException e) {
            Log.e("BatteryBackupHelper", "writeBackupData() is failed for " + str, e);
        }
    }

    private static boolean isOwner() {
        return UserHandle.myUserId() == 0;
    }
}
