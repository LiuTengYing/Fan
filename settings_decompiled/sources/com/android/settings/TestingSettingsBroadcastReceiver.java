package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settings.Settings;
/* loaded from: classes.dex */
public class TestingSettingsBroadcastReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null || !intent.getAction().equals("android.telephony.action.SECRET_CODE")) {
            return;
        }
        Intent intent2 = new Intent("android.intent.action.MAIN");
        intent2.setClass(context, Settings.TestingSettingsActivity.class);
        intent2.setFlags(268435456);
        context.startActivity(intent2);
    }
}
