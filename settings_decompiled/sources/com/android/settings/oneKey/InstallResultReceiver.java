package com.android.settings.oneKey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/* loaded from: classes.dex */
public class InstallResultReceiver extends BroadcastReceiver {
    public final String TAG = "fangli";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d("fangli", "收到安装反馈广播了");
        if (intent != null) {
            Log.d("fangli", "onReceive: " + intent.getAction());
            int intExtra = intent.getIntExtra("android.content.pm.extra.STATUS", 1);
            Log.d("fangli", "收到安装反馈广播了--" + intExtra);
            if (intExtra == 0) {
                int intExtra2 = intent.getIntExtra("flag", 0);
                if (intExtra2 == 1) {
                    Log.d("fangli", "静默安装成功");
                    InstallResult.getmInstance().updateSuccess();
                } else if (intExtra2 == 2) {
                    Log.d("fangli", "静默卸载成功");
                }
                Log.d("fangli", "APP Install Success!");
                return;
            }
            String stringExtra = intent.getStringExtra("android.content.pm.extra.STATUS_MESSAGE");
            Log.e("fangli", "Install FAILURE status_massage" + stringExtra);
            InstallResult.getmInstance().updateFailed(stringExtra);
        }
    }
}
