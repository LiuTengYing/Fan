package com.android.settings.network.ims;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.util.Log;
/* loaded from: classes.dex */
public class WifiCallingQueryImsState extends ImsQueryController {
    private Context mContext;
    private int mSubId;

    public WifiCallingQueryImsState(Context context, int i) {
        super(1, 1, 2);
        this.mContext = context;
        this.mSubId = i;
    }

    boolean isEnabledByUser(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return new ImsQueryWfcUserSetting(i).query();
        }
        return false;
    }

    public boolean isWifiCallingSupported() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            try {
                return isEnabledByPlatform(this.mSubId);
            } catch (ImsException | IllegalArgumentException | InterruptedException e) {
                Log.w("WifiCallingQueryImsState", "fail to get WFC supporting status. subId=" + this.mSubId, e);
                return false;
            }
        }
        return false;
    }

    public boolean isWifiCallingProvisioned() {
        return isWifiCallingSupported() && isProvisionedOnDevice(this.mSubId);
    }

    public boolean isReadyToWifiCalling() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && isWifiCallingProvisioned()) {
            try {
                return isServiceStateReady(this.mSubId);
            } catch (ImsException | IllegalArgumentException | InterruptedException e) {
                Log.w("WifiCallingQueryImsState", "fail to get WFC service status. subId=" + this.mSubId, e);
                return false;
            }
        }
        return false;
    }

    public boolean isAllowUserControl() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return !isTtyEnabled(this.mContext) || isTtyOnVolteEnabled(this.mSubId);
        }
        return false;
    }

    boolean isTtyEnabled(Context context) {
        return ((TelecomManager) context.getSystemService(TelecomManager.class)).getCurrentTtyMode() != 0;
    }

    public boolean isEnabledByUser() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return isEnabledByUser(this.mSubId);
        }
        return false;
    }
}
