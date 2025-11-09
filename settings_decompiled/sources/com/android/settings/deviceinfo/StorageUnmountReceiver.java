package com.android.settings.deviceinfo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import com.android.settings.deviceinfo.storage.StorageUtils;
/* loaded from: classes.dex */
public class StorageUnmountReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (ActivityManager.isUserAMonkey()) {
            return;
        }
        String stringExtra = intent.getStringExtra("android.os.storage.extra.VOLUME_ID");
        VolumeInfo findVolumeById = ((StorageManager) context.getSystemService(StorageManager.class)).findVolumeById(stringExtra);
        if (findVolumeById != null) {
            new StorageUtils.UnmountTask(context, findVolumeById).execute(new Void[0]);
            return;
        }
        Log.w("StorageUnmountReceiver", "Missing volume " + stringExtra);
    }
}
