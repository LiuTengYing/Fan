package com.android.settings.wifi;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class RequestToggleWiFiActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private CharSequence mAppLabel;
    private WifiManager mWiFiManager;
    private final StateChangeReceiver mReceiver = new StateChangeReceiver();
    private final Runnable mTimeoutCommand = new Runnable() { // from class: com.android.settings.wifi.RequestToggleWiFiActivity$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            RequestToggleWiFiActivity.this.lambda$new$0();
        }
    };
    @VisibleForTesting
    protected IActivityManager mActivityManager = ActivityManager.getService();
    private int mState = -1;
    private int mLastUpdateState = -1;

    public void dismiss() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        finish();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        this.mWiFiManager = (WifiManager) getSystemService(WifiManager.class);
        setResult(0);
        CharSequence appLabel = getAppLabel();
        this.mAppLabel = appLabel;
        if (TextUtils.isEmpty(appLabel)) {
            finish();
            return;
        }
        String action = getIntent().getAction();
        action.hashCode();
        if (action.equals("android.net.wifi.action.REQUEST_ENABLE")) {
            this.mState = 1;
        } else if (action.equals("android.net.wifi.action.REQUEST_DISABLE")) {
            this.mState = 3;
        } else {
            finish();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            finish();
        } else if (i != -1) {
        } else {
            int i2 = this.mState;
            if (i2 == 1) {
                this.mWiFiManager.setWifiEnabled(true);
                this.mState = 2;
                scheduleToggleTimeout();
                updateUi();
            } else if (i2 != 3) {
            } else {
                this.mWiFiManager.setWifiEnabled(false);
                this.mState = 4;
                scheduleToggleTimeout();
                updateUi();
            }
        }
    }

    protected void onStart() {
        super.onStart();
        this.mReceiver.register();
        int wifiState = this.mWiFiManager.getWifiState();
        int i = this.mState;
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        if (wifiState == 0) {
                            scheduleToggleTimeout();
                        } else if (wifiState == 1) {
                            setResult(-1);
                            finish();
                            return;
                        } else if (wifiState == 2 || wifiState == 3) {
                            this.mState = 3;
                        }
                    }
                } else if (wifiState == 1) {
                    setResult(-1);
                    finish();
                    return;
                } else if (wifiState == 2) {
                    this.mState = 4;
                    scheduleToggleTimeout();
                }
            } else if (wifiState == 0 || wifiState == 1) {
                this.mState = 1;
            } else if (wifiState == 2) {
                scheduleToggleTimeout();
            } else if (wifiState == 3) {
                setResult(-1);
                finish();
                return;
            }
        } else if (wifiState == 2) {
            this.mState = 2;
            scheduleToggleTimeout();
        } else if (wifiState == 3) {
            setResult(-1);
            finish();
            return;
        }
        updateUi();
    }

    protected void onStop() {
        this.mReceiver.unregister();
        unscheduleToggleTimeout();
        super.onStop();
    }

    @VisibleForTesting
    protected CharSequence getAppLabel() {
        try {
            String launchedFromPackage = this.mActivityManager.getLaunchedFromPackage(getActivityToken());
            if (TextUtils.isEmpty(launchedFromPackage)) {
                Log.d("RequestToggleWiFiActivity", "Package name is null");
                return null;
            }
            try {
                return getPackageManager().getApplicationInfo(launchedFromPackage, 0).loadSafeLabel(getPackageManager(), 1000.0f, 5);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("RequestToggleWiFiActivity", "Couldn't find app with package name " + launchedFromPackage);
                return null;
            }
        } catch (RemoteException unused2) {
            Log.e("RequestToggleWiFiActivity", "Can not get the package from activity manager");
            return null;
        }
    }

    private void updateUi() {
        int i = this.mLastUpdateState;
        int i2 = this.mState;
        if (i == i2) {
            return;
        }
        this.mLastUpdateState = i2;
        if (i2 == 1) {
            ((AlertActivity) this).mAlertParams.mPositiveButtonText = getString(R$string.allow);
            AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
            alertParams.mPositiveButtonListener = this;
            alertParams.mNegativeButtonText = getString(R$string.deny);
            AlertController.AlertParams alertParams2 = ((AlertActivity) this).mAlertParams;
            alertParams2.mNegativeButtonListener = this;
            alertParams2.mMessage = getString(R$string.wifi_ask_enable, new Object[]{this.mAppLabel});
        } else if (i2 == 2) {
            ((AlertActivity) this).mAlert.setButton(-1, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
            ((AlertActivity) this).mAlert.setButton(-2, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
            AlertController.AlertParams alertParams3 = ((AlertActivity) this).mAlertParams;
            alertParams3.mPositiveButtonText = null;
            alertParams3.mPositiveButtonListener = null;
            alertParams3.mNegativeButtonText = null;
            alertParams3.mNegativeButtonListener = null;
            alertParams3.mMessage = getString(R$string.wifi_starting);
        } else if (i2 == 3) {
            ((AlertActivity) this).mAlertParams.mPositiveButtonText = getString(R$string.allow);
            AlertController.AlertParams alertParams4 = ((AlertActivity) this).mAlertParams;
            alertParams4.mPositiveButtonListener = this;
            alertParams4.mNegativeButtonText = getString(R$string.deny);
            AlertController.AlertParams alertParams5 = ((AlertActivity) this).mAlertParams;
            alertParams5.mNegativeButtonListener = this;
            alertParams5.mMessage = getString(R$string.wifi_ask_disable, new Object[]{this.mAppLabel});
        } else if (i2 == 4) {
            ((AlertActivity) this).mAlert.setButton(-1, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
            ((AlertActivity) this).mAlert.setButton(-2, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
            AlertController.AlertParams alertParams6 = ((AlertActivity) this).mAlertParams;
            alertParams6.mPositiveButtonText = null;
            alertParams6.mPositiveButtonListener = null;
            alertParams6.mNegativeButtonText = null;
            alertParams6.mNegativeButtonListener = null;
            alertParams6.mMessage = getString(R$string.wifi_stopping);
        }
        setupAlert();
    }

    private void scheduleToggleTimeout() {
        getWindow().getDecorView().postDelayed(this.mTimeoutCommand, 10000L);
    }

    private void unscheduleToggleTimeout() {
        getWindow().getDecorView().removeCallbacks(this.mTimeoutCommand);
    }

    /* loaded from: classes.dex */
    private final class StateChangeReceiver extends BroadcastReceiver {
        private final IntentFilter mFilter;

        private StateChangeReceiver() {
            this.mFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        }

        public void register() {
            RequestToggleWiFiActivity.this.registerReceiver(this, this.mFilter);
        }

        public void unregister() {
            RequestToggleWiFiActivity.this.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AlertActivity alertActivity = RequestToggleWiFiActivity.this;
            if (alertActivity.isFinishing() || alertActivity.isDestroyed()) {
                return;
            }
            int wifiState = RequestToggleWiFiActivity.this.mWiFiManager.getWifiState();
            if (wifiState == 1 || wifiState == 3) {
                if (RequestToggleWiFiActivity.this.mState == 2 || RequestToggleWiFiActivity.this.mState == 4) {
                    RequestToggleWiFiActivity.this.setResult(-1);
                    RequestToggleWiFiActivity.this.finish();
                }
            }
        }
    }
}
