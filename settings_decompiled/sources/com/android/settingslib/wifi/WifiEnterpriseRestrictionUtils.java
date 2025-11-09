package com.android.settingslib.wifi;

import android.content.Context;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes2.dex */
public class WifiEnterpriseRestrictionUtils {
    private static boolean isAtLeastT() {
        return true;
    }

    public static boolean isWifiTetheringAllowed(Context context) {
        if (hasUserRestrictionFromT(context, "no_wifi_tethering")) {
            Log.w("WifiEntResUtils", "Wi-Fi Tethering isn't available due to user restriction.");
            return false;
        }
        return true;
    }

    public static boolean isWifiDirectAllowed(Context context) {
        if (hasUserRestrictionFromT(context, "no_wifi_direct")) {
            Log.w("WifiEntResUtils", "Wi-Fi Direct isn't available due to user restriction.");
            return false;
        }
        return true;
    }

    public static boolean isAddWifiConfigAllowed(Context context) {
        if (hasUserRestrictionFromT(context, "no_add_wifi_config")) {
            Log.w("WifiEntResUtils", "Wi-Fi Add network isn't available due to user restriction.");
            return false;
        }
        return true;
    }

    public static boolean isChangeWifiStateAllowed(Context context) {
        if (hasUserRestrictionFromT(context, "no_change_wifi_state")) {
            Log.w("WifiEntResUtils", "WI-FI state isn't allowed to change due to user restriction.");
            return false;
        }
        return true;
    }

    @VisibleForTesting
    static boolean hasUserRestrictionFromT(Context context, String str) {
        UserManager userManager;
        if (isAtLeastT() && (userManager = (UserManager) context.getSystemService(UserManager.class)) != null) {
            return userManager.hasUserRestriction(str);
        }
        return false;
    }
}
