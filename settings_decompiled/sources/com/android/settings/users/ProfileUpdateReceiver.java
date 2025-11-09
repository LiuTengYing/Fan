package com.android.settings.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.Utils;
/* loaded from: classes.dex */
public class ProfileUpdateReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        new Thread() { // from class: com.android.settings.users.ProfileUpdateReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                UserSettings.copyMeProfilePhoto(context, null);
                ProfileUpdateReceiver.copyProfileName(context);
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void copyProfileName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("profile", 0);
        int myUserId = UserHandle.myUserId();
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        String meProfileName = Utils.getMeProfileName(context, true);
        if (meProfileName == null || meProfileName.length() <= 0) {
            return;
        }
        userManager.setUserName(myUserId, meProfileName);
        sharedPreferences.edit().putBoolean("name_copied_once", true).commit();
    }
}
