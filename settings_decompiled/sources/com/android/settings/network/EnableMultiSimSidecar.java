package com.android.settings.network;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.util.ArraySet;
import android.util.Log;
import com.android.settings.AsyncTaskSidecar;
import com.android.settings.SidecarFragment;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class EnableMultiSimSidecar extends AsyncTaskSidecar<Void, Boolean> {
    private TelephonyManager mTelephonyManager;
    final CountDownLatch mSimCardStateChangedLatch = new CountDownLatch(1);
    private int mNumOfActiveSim = 0;
    private final BroadcastReceiver mCarrierConfigChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.EnableMultiSimSidecar.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int readySimsCount = EnableMultiSimSidecar.this.getReadySimsCount();
            int activePortsCount = EnableMultiSimSidecar.this.getActivePortsCount();
            if (readySimsCount == EnableMultiSimSidecar.this.mNumOfActiveSim && activePortsCount == EnableMultiSimSidecar.this.mNumOfActiveSim) {
                Log.i("EnableMultiSimSidecar", String.format("%d ports are active and ready.", Integer.valueOf(EnableMultiSimSidecar.this.mNumOfActiveSim)));
                EnableMultiSimSidecar.this.mSimCardStateChangedLatch.countDown();
                return;
            }
            Log.i("EnableMultiSimSidecar", String.format("%d ports are active and %d SIMs are ready. Keep waiting until timeout.", Integer.valueOf(activePortsCount), Integer.valueOf(readySimsCount)));
        }
    };

    public static EnableMultiSimSidecar get(FragmentManager fragmentManager) {
        return (EnableMultiSimSidecar) SidecarFragment.get(fragmentManager, "EnableMultiSimSidecar", EnableMultiSimSidecar.class, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.AsyncTaskSidecar
    public Boolean doInBackground(Void r1) {
        return Boolean.valueOf(updateMultiSimConfig());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.AsyncTaskSidecar
    public void onPostExecute(Boolean bool) {
        if (bool.booleanValue()) {
            setState(2, 0);
        } else {
            setState(3, 0);
        }
    }

    public void run(int i) {
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        this.mTelephonyManager = telephonyManager;
        this.mNumOfActiveSim = i;
        if (i > telephonyManager.getSupportedModemCount()) {
            Log.e("EnableMultiSimSidecar", "Requested number of active SIM is greater than supported modem count.");
            setState(3, 0);
        } else if (this.mTelephonyManager.doesSwitchMultiSimConfigTriggerReboot()) {
            Log.e("EnableMultiSimSidecar", "The device does not support reboot free DSDS.");
            setState(3, 0);
        } else {
            super.run((EnableMultiSimSidecar) null);
        }
    }

    private boolean updateMultiSimConfig() {
        try {
            try {
                getContext().registerReceiver(this.mCarrierConfigChangeReceiver, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"));
                this.mTelephonyManager.switchMultiSimConfig(this.mNumOfActiveSim);
            } catch (InterruptedException e) {
                Log.e("EnableMultiSimSidecar", "Failed to enable multiple SIM due to InterruptedException", e);
            }
            if (this.mSimCardStateChangedLatch.await(Settings.Global.getLong(getContext().getContentResolver(), "enable_multi_slot_timeout_millis", 40000L), TimeUnit.MILLISECONDS)) {
                Log.i("EnableMultiSimSidecar", "Multi SIM were successfully enabled.");
                return true;
            }
            Log.e("EnableMultiSimSidecar", "Timeout for waiting SIM status.");
            return false;
        } finally {
            getContext().unregisterReceiver(this.mCarrierConfigChangeReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getReadySimsCount() {
        int activeModemCount = this.mTelephonyManager.getActiveModemCount();
        Set<Integer> activeRemovableLogicalSlotIds = getActiveRemovableLogicalSlotIds();
        int i = 0;
        for (int i2 = 0; i2 < activeModemCount; i2++) {
            int simState = this.mTelephonyManager.getSimState(i2);
            if (simState == 5 || simState == 6 || simState == 10 || (simState == 1 && activeRemovableLogicalSlotIds.contains(Integer.valueOf(i2)))) {
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getActivePortsCount() {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return 0;
        }
        int i = 0;
        for (UiccSlotInfo uiccSlotInfo : uiccSlotsInfo) {
            for (UiccPortInfo uiccPortInfo : uiccSlotInfo.getPorts()) {
                if (uiccPortInfo.isActive()) {
                    i++;
                }
            }
        }
        return i;
    }

    public Set<Integer> getActiveRemovableLogicalSlotIds() {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet();
        for (UiccSlotInfo uiccSlotInfo : uiccSlotsInfo) {
            for (UiccPortInfo uiccPortInfo : uiccSlotInfo.getPorts()) {
                if (uiccPortInfo.isActive() && uiccSlotInfo.isRemovable()) {
                    arraySet.add(Integer.valueOf(uiccPortInfo.getLogicalSlotIndex()));
                }
            }
        }
        return arraySet;
    }
}
