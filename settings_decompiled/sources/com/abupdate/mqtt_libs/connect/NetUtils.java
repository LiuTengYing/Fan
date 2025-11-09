package com.abupdate.mqtt_libs.connect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.abupdate.mqtt_libs.connect.ShellUtils;
/* loaded from: classes.dex */
public class NetUtils {
    private static boolean isAvailableByPing(String str) {
        ShellUtils.CommandResult execCmd = ShellUtils.execCmd(String.format("ping -c 1 -w 1 %s", (str == null || str.length() <= 0) ? "223.5.5.5" : "223.5.5.5"), false);
        boolean z = execCmd.result == 0;
        if (execCmd.errorMsg != null) {
            Log.d("NetworkUtils", "isAvailableByPing() called" + execCmd.errorMsg);
        }
        if (execCmd.successMsg != null) {
            Log.d("NetworkUtils", "isAvailableByPing() called" + execCmd.successMsg);
        }
        return z;
    }

    private static boolean isAvailableBySystem() {
        NetworkInfo[] allNetworkInfo;
        NetworkInfo networkInfo;
        Context context = MqttManager.sCx;
        if (context == null) {
            return false;
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager == null || (allNetworkInfo = connectivityManager.getAllNetworkInfo()) == null) {
                return false;
            }
            for (int i = 0; i < allNetworkInfo.length && (networkInfo = allNetworkInfo[i]) != null; i++) {
                if (networkInfo.isConnected() && allNetworkInfo[i].isAvailable()) {
                    return true;
                }
            }
            return false;
        } catch (Exception unused) {
            Log.d("NetUtils", "isNetWorkAvailable: not available");
            return false;
        }
    }

    public static boolean isNetWorkAvailable() {
        return isAvailableBySystem() || isAvailableByPing(null);
    }
}
