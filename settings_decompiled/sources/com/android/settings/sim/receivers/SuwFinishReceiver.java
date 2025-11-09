package com.android.settings.sim.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.settings.R$bool;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Objects;
/* loaded from: classes.dex */
public class SuwFinishReceiver extends BroadcastReceiver {
    private final SimSlotChangeHandler mSlotChangeHandler = SimSlotChangeHandler.get();
    private final Object mLock = new Object();

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        if (!context.getResources().getBoolean(R$bool.config_handle_sim_slot_change)) {
            Log.i("SuwFinishReceiver", "The flag is off. Ignore SUW finish event.");
            return;
        }
        final BroadcastReceiver.PendingResult goAsync = goAsync();
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.sim.receivers.SuwFinishReceiver$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SuwFinishReceiver.this.lambda$onReceive$0(context, goAsync);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$0(Context context, BroadcastReceiver.PendingResult pendingResult) {
        synchronized (this.mLock) {
            Log.i("SuwFinishReceiver", "Detected SUW finished. Checking slot events.");
            this.mSlotChangeHandler.onSuwFinish(context.getApplicationContext());
        }
        Objects.requireNonNull(pendingResult);
        ThreadUtils.postOnMainThread(new SimSlotChangeReceiver$$ExternalSyntheticLambda1(pendingResult));
    }
}
