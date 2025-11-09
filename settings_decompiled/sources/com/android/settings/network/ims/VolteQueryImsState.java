package com.android.settings.network.ims;

import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ProvisioningManager;
import android.util.Log;
/* loaded from: classes.dex */
public class VolteQueryImsState extends ImsQueryController {
    private Context mContext;
    private int mSubId;

    public VolteQueryImsState(Context context, int i) {
        super(1, 0, 1);
        this.mContext = context;
        this.mSubId = i;
    }

    boolean isEnabledByUser(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return new ImsQueryEnhanced4gLteModeUserSetting(i).query();
        }
        return false;
    }

    public boolean isVoLteProvisioned() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && isProvisionedOnDevice(this.mSubId)) {
            try {
                return isEnabledByPlatform(this.mSubId);
            } catch (ImsException | IllegalArgumentException | InterruptedException e) {
                Log.w("VolteQueryImsState", "fail to get VoLte supporting status. subId=" + this.mSubId, e);
                return false;
            }
        }
        return false;
    }

    public boolean isReadyToVoLte() {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && isVoLteProvisioned()) {
            try {
                return isServiceStateReady(this.mSubId);
            } catch (ImsException | IllegalArgumentException | InterruptedException e) {
                Log.w("VolteQueryImsState", "fail to get VoLte service status. subId=" + this.mSubId, e);
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

    public boolean isVoImsOptInEnabled() {
        return ProvisioningManager.createForSubscriptionId(this.mSubId).getProvisioningIntValue(68) == 1;
    }
}
