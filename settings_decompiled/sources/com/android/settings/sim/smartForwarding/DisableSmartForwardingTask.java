package com.android.settings.sim.smartForwarding;

import android.telephony.CallForwardingInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
/* loaded from: classes.dex */
public class DisableSmartForwardingTask implements Runnable {
    private final CallForwardingInfo[] callForwardingInfo;
    private final boolean[] callWaitingStatus;
    private final TelephonyManager tm;

    public DisableSmartForwardingTask(TelephonyManager telephonyManager, boolean[] zArr, CallForwardingInfo[] callForwardingInfoArr) {
        this.tm = telephonyManager;
        this.callWaitingStatus = zArr;
        this.callForwardingInfo = callForwardingInfoArr;
    }

    @Override // java.lang.Runnable
    public void run() {
        for (int i = 0; i < this.tm.getActiveModemCount(); i++) {
            int subId = getSubId(i);
            if (this.callWaitingStatus != null && subId != -1) {
                Log.d("SmartForwarding", "Restore call waiting to " + this.callWaitingStatus[i]);
                this.tm.createForSubscriptionId(subId).setCallWaitingEnabled(this.callWaitingStatus[i], null, null);
            }
            CallForwardingInfo[] callForwardingInfoArr = this.callForwardingInfo;
            if (callForwardingInfoArr != null && callForwardingInfoArr[i] != null && subId != -1) {
                Log.d("SmartForwarding", "Restore call forwarding to " + this.callForwardingInfo[i]);
                this.tm.createForSubscriptionId(subId).setCallForwarding(this.callForwardingInfo[i], null, null);
            }
        }
    }

    private int getSubId(int i) {
        int[] subId = SubscriptionManager.getSubId(i);
        if (subId == null || subId.length <= 0) {
            return -1;
        }
        return subId[0];
    }
}
