package com.android.settings.network.telephony;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;
import android.util.Log;
import com.android.settings.SidecarFragment;
import com.android.settings.network.SwitchSlotSidecar;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class EuiccOperationSidecar extends SidecarFragment implements SidecarFragment.Listener {
    private static AtomicInteger sCurrentOpId = new AtomicInteger((int) SystemClock.elapsedRealtime());
    private int mDetailedCode;
    protected EuiccManager mEuiccManager;
    private int mOpId;
    protected final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.EuiccOperationSidecar.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (EuiccOperationSidecar.this.getReceiverAction().equals(intent.getAction()) && EuiccOperationSidecar.this.mOpId == intent.getIntExtra("op_id", -1)) {
                EuiccOperationSidecar.this.mResultCode = getResultCode();
                EuiccOperationSidecar.this.mDetailedCode = intent.getIntExtra("android.telephony.euicc.extra.EMBEDDED_SUBSCRIPTION_DETAILED_CODE", 0);
                EuiccOperationSidecar.this.mResultIntent = intent;
                Log.i("EuiccOperationSidecar", String.format("Result code : %d; detailed code : %d", Integer.valueOf(EuiccOperationSidecar.this.mResultCode), Integer.valueOf(EuiccOperationSidecar.this.mDetailedCode)));
                EuiccOperationSidecar.this.onActionReceived();
            }
        }
    };
    private int mResultCode;
    private Intent mResultIntent;
    protected SwitchSlotSidecar mSwitchSlotSidecar;
    protected TelephonyManager mTelephonyManager;

    protected abstract String getReceiverAction();

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActionReceived() {
        int i = this.mResultCode;
        if (i == 0) {
            setState(2, 0);
        } else {
            setState(3, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PendingIntent createCallbackIntent() {
        this.mOpId = sCurrentOpId.incrementAndGet();
        Intent intent = new Intent(getReceiverAction());
        intent.putExtra("op_id", this.mOpId);
        return PendingIntent.getBroadcast(getContext(), 0, intent, 335544320);
    }

    @Override // com.android.settings.SidecarFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEuiccManager = (EuiccManager) getContext().getSystemService(EuiccManager.class);
        this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
        this.mSwitchSlotSidecar = SwitchSlotSidecar.get(getChildFragmentManager());
        getContext().getApplicationContext().registerReceiver(this.mReceiver, new IntentFilter(getReceiverAction()), "android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS", null, 2);
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSwitchSlotSidecar.addListener(this);
    }

    @Override // android.app.Fragment
    public void onPause() {
        this.mSwitchSlotSidecar.removeListener(this);
        super.onPause();
    }

    @Override // com.android.settings.SidecarFragment, android.app.Fragment
    public void onDestroy() {
        getContext().getApplicationContext().unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    public void onStateChange(SidecarFragment sidecarFragment) {
        SwitchSlotSidecar switchSlotSidecar = this.mSwitchSlotSidecar;
        if (sidecarFragment == switchSlotSidecar) {
            int state = switchSlotSidecar.getState();
            if (state == 2) {
                this.mSwitchSlotSidecar.reset();
                Log.i("EuiccOperationSidecar", "mSwitchSlotSidecar SUCCESS");
                return;
            } else if (state != 3) {
                return;
            } else {
                this.mSwitchSlotSidecar.reset();
                Log.i("EuiccOperationSidecar", "mSwitchSlotSidecar ERROR");
                return;
            }
        }
        Log.wtf("EuiccOperationSidecar", "Received state change from a sidecar not expected.");
    }

    public int getResultCode() {
        return this.mResultCode;
    }
}
