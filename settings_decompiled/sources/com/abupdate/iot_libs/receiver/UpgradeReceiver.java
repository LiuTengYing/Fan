package com.abupdate.iot_libs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.abupdate.iot_libs.engine.thread.Dispatcher;
import com.abupdate.iot_libs.engine.thread.NetWorkChangeTask;
/* loaded from: classes.dex */
public class UpgradeReceiver extends BroadcastReceiver {
    private boolean checkVersionIsRun = false;
    private Context mCx;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.mCx = context;
        dispatch_action(intent.getAction());
    }

    private void dispatch_action(String str) {
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(str)) {
            network_process();
        } else {
            "android.intent.action.BOOT_COMPLETED".equals(str);
        }
    }

    private void network_process() {
        Dispatcher.getDispatcher().enqueue(new NetWorkChangeTask());
    }
}
