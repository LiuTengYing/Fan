package com.android.settingslib.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.provider.Settings;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
/* loaded from: classes2.dex */
public class ConnectivitySubsystemsRecoveryManager {
    private final Context mContext;
    private final Handler mHandler;
    private TelephonyManager mTelephonyManager;
    private WifiManager mWifiManager;
    private RecoveryAvailableListener mRecoveryAvailableListener = null;
    private final BroadcastReceiver mApmMonitor = new BroadcastReceiver() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RecoveryAvailableListener recoveryAvailableListener = ConnectivitySubsystemsRecoveryManager.this.mRecoveryAvailableListener;
            if (recoveryAvailableListener != null) {
                recoveryAvailableListener.onRecoveryAvailableChangeListener(ConnectivitySubsystemsRecoveryManager.this.isRecoveryAvailable());
            }
        }
    };
    private boolean mApmMonitorRegistered = false;
    private boolean mWifiRestartInProgress = false;
    private boolean mTelephonyRestartInProgress = false;
    private RecoveryStatusCallback mCurrentRecoveryCallback = null;
    private final WifiManager.SubsystemRestartTrackingCallback mWifiSubsystemRestartTrackingCallback = new WifiManager.SubsystemRestartTrackingCallback() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager.2
        @Override // android.net.wifi.WifiManager.SubsystemRestartTrackingCallback
        public void onSubsystemRestarting() {
        }

        @Override // android.net.wifi.WifiManager.SubsystemRestartTrackingCallback
        public void onSubsystemRestarted() {
            ConnectivitySubsystemsRecoveryManager.this.mWifiRestartInProgress = false;
            ConnectivitySubsystemsRecoveryManager.this.stopTrackingWifiRestart();
            ConnectivitySubsystemsRecoveryManager.this.checkIfAllSubsystemsRestartsAreDone();
        }
    };
    private final MobileTelephonyCallback mTelephonyCallback = new MobileTelephonyCallback();

    /* loaded from: classes2.dex */
    public interface RecoveryAvailableListener {
        void onRecoveryAvailableChangeListener(boolean z);
    }

    /* loaded from: classes2.dex */
    public interface RecoveryStatusCallback {
        void onSubsystemRestartOperationBegin();

        void onSubsystemRestartOperationEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MobileTelephonyCallback extends TelephonyCallback implements TelephonyCallback.RadioPowerStateListener {
        private MobileTelephonyCallback() {
        }

        public void onRadioPowerStateChanged(int i) {
            if (!ConnectivitySubsystemsRecoveryManager.this.mTelephonyRestartInProgress || ConnectivitySubsystemsRecoveryManager.this.mCurrentRecoveryCallback == null) {
                ConnectivitySubsystemsRecoveryManager.this.stopTrackingTelephonyRestart();
            }
            if (i == 1) {
                ConnectivitySubsystemsRecoveryManager.this.mTelephonyRestartInProgress = false;
                ConnectivitySubsystemsRecoveryManager.this.stopTrackingTelephonyRestart();
                ConnectivitySubsystemsRecoveryManager.this.checkIfAllSubsystemsRestartsAreDone();
            }
        }
    }

    public ConnectivitySubsystemsRecoveryManager(Context context, Handler handler) {
        this.mWifiManager = null;
        this.mTelephonyManager = null;
        this.mContext = context;
        this.mHandler = new Handler(handler.getLooper());
        if (context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
            this.mWifiManager = wifiManager;
            if (wifiManager == null) {
                Log.e("ConnectivitySubsystemsRecoveryManager", "WifiManager not available!?");
            }
        }
        if (context.getPackageManager().hasSystemFeature("android.hardware.telephony")) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            this.mTelephonyManager = telephonyManager;
            if (telephonyManager == null) {
                Log.e("ConnectivitySubsystemsRecoveryManager", "TelephonyManager not available!?");
            }
        }
    }

    private boolean isApmEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = this.mWifiManager;
        return wifiManager != null && (wifiManager.isWifiEnabled() || this.mWifiManager.isWifiApEnabled());
    }

    public boolean isRecoveryAvailable() {
        if (isApmEnabled()) {
            return isWifiEnabled();
        }
        return true;
    }

    void startTrackingWifiRestart() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager == null) {
            return;
        }
        wifiManager.registerSubsystemRestartTrackingCallback(new HandlerExecutor(this.mHandler), this.mWifiSubsystemRestartTrackingCallback);
    }

    void stopTrackingWifiRestart() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager == null) {
            return;
        }
        wifiManager.unregisterSubsystemRestartTrackingCallback(this.mWifiSubsystemRestartTrackingCallback);
    }

    void startTrackingTelephonyRestart() {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            return;
        }
        telephonyManager.registerTelephonyCallback(new HandlerExecutor(this.mHandler), this.mTelephonyCallback);
    }

    void stopTrackingTelephonyRestart() {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            return;
        }
        telephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkIfAllSubsystemsRestartsAreDone() {
        RecoveryStatusCallback recoveryStatusCallback;
        if (this.mWifiRestartInProgress || this.mTelephonyRestartInProgress || (recoveryStatusCallback = this.mCurrentRecoveryCallback) == null) {
            return;
        }
        recoveryStatusCallback.onSubsystemRestartOperationEnd();
        this.mCurrentRecoveryCallback = null;
    }

    public void triggerSubsystemRestart(String str, final RecoveryStatusCallback recoveryStatusCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ConnectivitySubsystemsRecoveryManager.this.lambda$triggerSubsystemRestart$3(recoveryStatusCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerSubsystemRestart$3(RecoveryStatusCallback recoveryStatusCallback) {
        boolean z;
        if (this.mWifiRestartInProgress) {
            Log.e("ConnectivitySubsystemsRecoveryManager", "Wifi restart still in progress");
        } else if (this.mTelephonyRestartInProgress) {
            Log.e("ConnectivitySubsystemsRecoveryManager", "Telephony restart still in progress");
        } else {
            boolean z2 = true;
            if (isWifiEnabled()) {
                this.mWifiManager.restartWifiSubsystem();
                this.mWifiRestartInProgress = true;
                startTrackingWifiRestart();
                z = true;
            } else {
                z = false;
            }
            if (this.mTelephonyManager == null || isApmEnabled() || !this.mTelephonyManager.rebootRadio()) {
                z2 = z;
            } else {
                this.mTelephonyRestartInProgress = true;
                startTrackingTelephonyRestart();
            }
            if (z2) {
                this.mCurrentRecoveryCallback = recoveryStatusCallback;
                recoveryStatusCallback.onSubsystemRestartOperationBegin();
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.settingslib.connectivity.ConnectivitySubsystemsRecoveryManager$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ConnectivitySubsystemsRecoveryManager.this.lambda$triggerSubsystemRestart$2();
                    }
                }, 15000L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$triggerSubsystemRestart$2() {
        stopTrackingWifiRestart();
        stopTrackingTelephonyRestart();
        this.mWifiRestartInProgress = false;
        this.mTelephonyRestartInProgress = false;
        RecoveryStatusCallback recoveryStatusCallback = this.mCurrentRecoveryCallback;
        if (recoveryStatusCallback != null) {
            recoveryStatusCallback.onSubsystemRestartOperationEnd();
            this.mCurrentRecoveryCallback = null;
        }
    }
}
