package com.unisoc.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes2.dex */
public abstract class SimStateChangeListener {
    private Context mContext;
    private AtomicBoolean mListening;
    private Looper mLooper;
    private String[] mSimState;
    private BroadcastReceiver mSimStateChangeReceiver;
    private IntentFilter mSimStateIntentFilter;
    private TelephonyManager mTeleMgr;

    public abstract void onChanged();

    public SimStateChangeListener(Looper looper, Context context) {
        this(looper, context, -1);
    }

    public SimStateChangeListener(Looper looper, Context context, int i) {
        this.mLooper = looper;
        this.mContext = context;
        this.mListening = new AtomicBoolean(false);
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mTeleMgr = telephonyManager;
        String[] strArr = new String[telephonyManager.getSupportedModemCount()];
        this.mSimState = strArr;
        Arrays.fill(strArr, "UNKNOWN");
        this.mSimStateIntentFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
    }

    private BroadcastReceiver getSimStateChangeReceiver() {
        return new BroadcastReceiver() { // from class: com.unisoc.settings.network.SimStateChangeListener.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (isInitialStickyBroadcast()) {
                    return;
                }
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action) && "android.intent.action.SIM_STATE_CHANGED".equals(action)) {
                    String stringExtra = intent.getStringExtra("ss");
                    int intExtra = intent.getIntExtra("phone", -1);
                    Log.d("SimStateChangeListener", "Receive simstate " + stringExtra + " for phone" + intExtra);
                    SimStateChangeListener.this.mSimState[intExtra] = stringExtra;
                    SimStateChangeListener.this.onSimStateChanged();
                }
            }
        };
    }

    public void start() {
        monitorSimStateChange(true);
    }

    public void stop() {
        monitorSimStateChange(false);
    }

    public void onSimStateChanged() {
        if (this.mListening.get()) {
            onChanged();
        }
    }

    private void monitorSimStateChange(boolean z) {
        if (!this.mListening.compareAndSet(!z, z)) {
            Log.d("SimStateChangeListener", "don't monitor sim state repeatedly");
        } else if (z) {
            if (this.mSimStateChangeReceiver == null) {
                this.mSimStateChangeReceiver = getSimStateChangeReceiver();
            }
            this.mContext.registerReceiver(this.mSimStateChangeReceiver, this.mSimStateIntentFilter, null, new Handler(this.mLooper));
        } else {
            BroadcastReceiver broadcastReceiver = this.mSimStateChangeReceiver;
            if (broadcastReceiver != null) {
                this.mContext.unregisterReceiver(broadcastReceiver);
            }
        }
    }

    public String getSimState(int i) {
        return this.mSimState[i];
    }
}
