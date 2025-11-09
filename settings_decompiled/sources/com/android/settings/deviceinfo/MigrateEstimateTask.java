package com.android.settings.deviceinfo;

import android.app.usage.ExternalStorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.DataUnit;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;
/* loaded from: classes.dex */
public abstract class MigrateEstimateTask extends AsyncTask<Void, Void, Long> {
    private static final long SPEED_ESTIMATE_BPS = DataUnit.MEBIBYTES.toBytes(10);
    private final Context mContext;
    private long mSizeBytes = -1;

    public abstract void onPostExecute(String str, String str2);

    public MigrateEstimateTask(Context context) {
        this.mContext = context;
    }

    public void copyFrom(Intent intent) {
        this.mSizeBytes = intent.getLongExtra("size_bytes", -1L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Long doInBackground(Void... voidArr) {
        long j = this.mSizeBytes;
        if (j != -1) {
            return Long.valueOf(j);
        }
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        StorageStatsManager storageStatsManager = (StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class);
        VolumeInfo findEmulatedForPrivate = storageManager.findEmulatedForPrivate(this.mContext.getPackageManager().getPrimaryStorageCurrentVolume());
        if (findEmulatedForPrivate == null) {
            Log.w("MigrateEstimateTask", "Failed to find current primary storage");
            return -1L;
        }
        try {
            UUID uuidForPath = storageManager.getUuidForPath(findEmulatedForPrivate.getPath());
            Log.d("MigrateEstimateTask", "Measuring size of " + uuidForPath);
            long j2 = 0;
            for (UserInfo userInfo : userManager.getUsers()) {
                ExternalStorageStats queryExternalStatsForUser = storageStatsManager.queryExternalStatsForUser(uuidForPath, UserHandle.of(userInfo.id));
                j2 += queryExternalStatsForUser.getTotalBytes();
                if (userInfo.id == 0) {
                    j2 += queryExternalStatsForUser.getObbBytes();
                }
            }
            return Long.valueOf(j2);
        } catch (IOException e) {
            Log.w("MigrateEstimateTask", "Failed to measure", e);
            return -1L;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Long l) {
        long longValue = l.longValue();
        this.mSizeBytes = longValue;
        onPostExecute(Formatter.formatFileSize(this.mContext, this.mSizeBytes), DateUtils.formatDuration(Math.max((longValue * 1000) / SPEED_ESTIMATE_BPS, 1000L)).toString());
    }
}
