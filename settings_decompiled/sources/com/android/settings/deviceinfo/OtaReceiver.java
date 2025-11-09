package com.android.settings.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
/* loaded from: classes.dex */
public class OtaReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d("OtaReceiver", "restore install_in_progress to 0");
        Settings.Global.putInt(context.getContentResolver(), "install_in_progress", 0);
    }
}
