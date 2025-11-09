package com.unisoc.settings.network.telephony;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.telephony.PrimarySubManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.unisoc.telephony.RadioInteractor;
/* loaded from: classes2.dex */
public class UniMobileNetworkUtils {
    public static boolean isDataSwitchEnabledForSubsidy(Context context, int i) {
        ServiceState serviceState;
        if (!isSubsidyLocked(context) || (serviceState = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i).getServiceState()) == null || (serviceState.getState() == 0 && serviceState.getDataRegState() == 0)) {
            return true;
        }
        Toast.makeText(context, R$string.data_switch_not_allowed, 1).show();
        return false;
    }

    public static boolean isPrefEnabledForSubsidy(Context context, int i) {
        if (isSubsidyLocked(context)) {
            int slotIndex = SubscriptionManager.getSlotIndex(i);
            if (SubscriptionManager.isValidPhoneId(slotIndex)) {
                PrimarySubManager from = PrimarySubManager.from(context);
                return from != null && from.isOperatorCardForSubsidy(slotIndex);
            }
            return true;
        }
        return true;
    }

    public static boolean isDataSwitchAllowedForSubsidy(Context context) {
        if (isSubsidyLocked(context)) {
            PrimarySubManager from = PrimarySubManager.from(context);
            return from != null && from.isDataSwitchAllowedForSubsidy();
        }
        return true;
    }

    public static boolean isDisableSimAllowedForSubsidy(Context context, String str) {
        if (isSubsidyLocked(context)) {
            PrimarySubManager from = PrimarySubManager.from(context);
            return from != null && from.isDisableSimAllowedByIccId(str);
        }
        return true;
    }

    public static boolean isSubsidyLocked(Context context) {
        return Resources.getSystem().getBoolean(134414357) && new RadioInteractor(context).getSubsidyLockStatus(0) == 1;
    }

    public static boolean isSubsidyShowing() {
        String str = SystemProperties.get("gsm.subsidy.lock.state");
        if (!TextUtils.isEmpty(str) && str.equals("false")) {
            SettingsActivity.mIsSubsidyLocked = false;
        }
        return SettingsActivity.mIsSubsidyLocked;
    }

    public static boolean isAppDisableAllowed(Context context, String str) {
        String[] stringArray;
        for (String str2 : context.getResources().getStringArray(R$array.cannot_disabled_app)) {
            if (str2 != null && str2.equals(str)) {
                return false;
            }
        }
        return true;
    }
}
