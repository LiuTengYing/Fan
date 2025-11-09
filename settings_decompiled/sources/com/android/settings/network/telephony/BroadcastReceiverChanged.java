package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
/* loaded from: classes.dex */
public class BroadcastReceiverChanged {
    private BroadcastReceiver mBroadcastReceiver;
    private BroadcastReceiverChangedClient mClient;
    private Context mContext;

    /* loaded from: classes.dex */
    public interface BroadcastReceiverChangedClient {
        void onCarrierConfigChanged(int i);

        void onPhoneStateChanged();
    }

    public BroadcastReceiverChanged(Context context, BroadcastReceiverChangedClient broadcastReceiverChangedClient) {
        this.mContext = context;
        this.mClient = broadcastReceiverChangedClient;
    }

    public void start() {
        if (this.mBroadcastReceiver == null) {
            this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.BroadcastReceiverChanged.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if ("android.intent.action.PHONE_STATE".equals(action)) {
                        BroadcastReceiverChanged.this.onPhoneStateChangedCallback();
                    } else if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
                        BroadcastReceiverChanged.this.onCarrierConfigChangedCallback(intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1));
                    }
                }
            };
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    public void stop() {
        BroadcastReceiver broadcastReceiver = this.mBroadcastReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
            this.mBroadcastReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPhoneStateChangedCallback() {
        this.mClient.onPhoneStateChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierConfigChangedCallback(int i) {
        this.mClient.onCarrierConfigChanged(i);
    }
}
