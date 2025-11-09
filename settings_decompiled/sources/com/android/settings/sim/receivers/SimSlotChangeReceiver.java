package com.android.settings.sim.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class SimSlotChangeReceiver extends BroadcastReceiver {
    private final SimSlotChangeHandler mSlotChangeHandler = SimSlotChangeHandler.get();
    private final Object mLock = new Object();

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (!"android.telephony.action.SIM_SLOT_STATUS_CHANGED".equals(action)) {
            Log.e("SlotChangeReceiver", "Ignore slot changes due to unexpected action: " + action);
            return;
        }
        final BroadcastReceiver.PendingResult goAsync = goAsync();
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.sim.receivers.SimSlotChangeReceiver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SimSlotChangeReceiver.this.lambda$onReceive$0(context, goAsync);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$0(Context context, BroadcastReceiver.PendingResult pendingResult) {
        synchronized (this.mLock) {
            if (shouldHandleSlotChange(context)) {
                this.mSlotChangeHandler.onSlotsStatusChange(context.getApplicationContext());
            }
        }
        Objects.requireNonNull(pendingResult);
        ThreadUtils.postOnMainThread(new SimSlotChangeReceiver$$ExternalSyntheticLambda1(pendingResult));
    }

    private boolean shouldHandleSlotChange(Context context) {
        if (!context.getResources().getBoolean(R$bool.config_handle_sim_slot_change)) {
            Log.i("SlotChangeReceiver", "The flag is off. Ignore slot changes.");
            return false;
        }
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        if (euiccManager == null || !euiccManager.isEnabled()) {
            Log.i("SlotChangeReceiver", "Ignore slot changes because EuiccManager is disabled.");
            return false;
        } else if (euiccManager.getOtaStatus() == 1) {
            Log.i("SlotChangeReceiver", "Ignore slot changes because eSIM OTA is in progress.");
            return false;
        } else if (isSimSlotStateValid(context)) {
            return true;
        } else {
            Log.i("SlotChangeReceiver", "Ignore slot changes because SIM states are not valid.");
            return false;
        }
    }

    private boolean isSimSlotStateValid(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        UiccSlotInfo[] uiccSlotsInfo = telephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            Log.e("SlotChangeReceiver", "slotInfos is null. Unable to get slot infos.");
            return false;
        }
        boolean z = true;
        for (int i = 0; i < uiccSlotsInfo.length; i++) {
            UiccSlotInfo uiccSlotInfo = uiccSlotsInfo[i];
            if (uiccSlotInfo == null) {
                return false;
            }
            if (uiccSlotInfo.getCardStateInfo() == 3 || uiccSlotInfo.getCardStateInfo() == 4) {
                Log.i("SlotChangeReceiver", "The SIM state is in an error. Drop the event. SIM info: " + uiccSlotInfo);
                return false;
            }
            UiccCardInfo findUiccCardInfoBySlot = findUiccCardInfoBySlot(telephonyManager, i);
            if (findUiccCardInfoBySlot != null) {
                for (UiccPortInfo uiccPortInfo : findUiccCardInfoBySlot.getPorts()) {
                    if (!TextUtils.isEmpty(uiccSlotInfo.getCardId()) || !TextUtils.isEmpty(uiccPortInfo.getIccId())) {
                        z = false;
                    }
                }
            }
        }
        if (z) {
            Log.i("SlotChangeReceiver", "All UICC card strings are empty. Drop this event.");
            return false;
        }
        return true;
    }

    private UiccCardInfo findUiccCardInfoBySlot(TelephonyManager telephonyManager, final int i) {
        List<UiccCardInfo> uiccCardsInfo = telephonyManager.getUiccCardsInfo();
        if (uiccCardsInfo == null) {
            return null;
        }
        return uiccCardsInfo.stream().filter(new Predicate() { // from class: com.android.settings.sim.receivers.SimSlotChangeReceiver$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$findUiccCardInfoBySlot$1;
                lambda$findUiccCardInfoBySlot$1 = SimSlotChangeReceiver.lambda$findUiccCardInfoBySlot$1(i, (UiccCardInfo) obj);
                return lambda$findUiccCardInfoBySlot$1;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$findUiccCardInfoBySlot$1(int i, UiccCardInfo uiccCardInfo) {
        return uiccCardInfo.getPhysicalSlotIndex() == i;
    }
}
