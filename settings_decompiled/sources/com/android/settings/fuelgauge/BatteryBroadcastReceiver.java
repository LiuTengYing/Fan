package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.android.settings.Utils;
import com.android.settings.homepage.contextualcards.slices.BatteryFixSlice;
/* loaded from: classes.dex */
public class BatteryBroadcastReceiver extends BroadcastReceiver {
    int mBatteryHealth;
    String mBatteryLevel;
    private OnBatteryChangedListener mBatteryListener;
    String mBatteryStatus;
    boolean mCharing;
    private Context mContext;
    int mStatus;

    /* loaded from: classes.dex */
    public interface OnBatteryChangedListener {
        void onBatteryChanged(int i);
    }

    public BatteryBroadcastReceiver(Context context) {
        this.mContext = context;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        updateBatteryStatus(intent, false);
    }

    public void setBatteryChangedListener(OnBatteryChangedListener onBatteryChangedListener) {
        this.mBatteryListener = onBatteryChangedListener;
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        updateBatteryStatus(this.mContext.registerReceiver(this, intentFilter), true);
    }

    public void unRegister() {
        this.mContext.unregisterReceiver(this);
    }

    private void updateBatteryStatus(Intent intent, boolean z) {
        if (intent != null && this.mBatteryListener != null) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                String batteryPercentage = Utils.getBatteryPercentage(intent);
                String batteryStatus = com.android.settingslib.Utils.getBatteryStatus(this.mContext, intent, false);
                int intExtra = intent.getIntExtra("health", 1);
                int intExtra2 = intent.getIntExtra("status", 1);
                boolean z2 = intent.getIntExtra("plugged", 0) != 0;
                if (!Utils.isBatteryPresent(intent)) {
                    Log.w("BatteryBroadcastRcvr", "Problem reading the battery meter.");
                    this.mBatteryListener.onBatteryChanged(5);
                } else if (z || z2 != this.mCharing || intExtra2 != this.mStatus) {
                    this.mBatteryListener.onBatteryChanged(0);
                } else if (intExtra != this.mBatteryHealth) {
                    this.mBatteryListener.onBatteryChanged(4);
                } else if (!batteryPercentage.equals(this.mBatteryLevel)) {
                    this.mBatteryListener.onBatteryChanged(1);
                } else if (!batteryStatus.equals(this.mBatteryStatus)) {
                    this.mBatteryListener.onBatteryChanged(3);
                }
                this.mBatteryLevel = batteryPercentage;
                this.mBatteryStatus = batteryStatus;
                this.mBatteryHealth = intExtra;
                this.mStatus = intExtra2;
                this.mCharing = z2;
            } else if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                this.mBatteryListener.onBatteryChanged(2);
            }
        }
        BatteryFixSlice.updateBatteryTipAvailabilityCache(this.mContext);
    }
}
