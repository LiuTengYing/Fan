package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.bluetooth.RequestPermissionActivity;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
import com.android.settingslib.bluetooth.BluetoothDiscoverableTimeoutReceiver;
import java.time.Duration;
/* loaded from: classes.dex */
public class RequestPermissionActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private CharSequence mAppLabel;
    private BluetoothAdapter mBluetoothAdapter;
    private AlertDialog mDialog;
    private BroadcastReceiver mReceiver;
    private int mRequest;
    private int mTimeout = 120;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        setResult(0);
        if (parseIntent()) {
            finish();
            return;
        }
        int state = this.mBluetoothAdapter.getState();
        int i = this.mRequest;
        if (i == 3) {
            switch (state) {
                case 10:
                case 13:
                    proceedAndFinish();
                    return;
                case 11:
                case 12:
                    Intent intent = new Intent(this, RequestPermissionHelperActivity.class);
                    intent.putExtra("com.android.settings.bluetooth.extra.APP_LABEL", this.mAppLabel);
                    intent.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_OFF");
                    startActivityForResult(intent, 0);
                    return;
                default:
                    Log.e("BtRequestPermission", "Unknown adapter state: " + state);
                    cancelAndFinish();
                    return;
            }
        }
        switch (state) {
            case 10:
            case 11:
            case 13:
                Intent intent2 = new Intent(this, RequestPermissionHelperActivity.class);
                intent2.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON");
                intent2.putExtra("com.android.settings.bluetooth.extra.APP_LABEL", this.mAppLabel);
                if (this.mRequest == 2) {
                    intent2.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", this.mTimeout);
                }
                startActivityForResult(intent2, 0);
                return;
            case 12:
                if (i == 1) {
                    proceedAndFinish();
                    return;
                } else {
                    createDialog();
                    return;
                }
            default:
                Log.e("BtRequestPermission", "Unknown adapter state: " + state);
                cancelAndFinish();
                return;
        }
    }

    private void createDialog() {
        String string;
        String string2;
        if (getResources().getBoolean(R$bool.auto_confirm_bluetooth_activation_dialog)) {
            onClick(null, -1);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (this.mReceiver != null) {
            int i = this.mRequest;
            if (i == 1 || i == 2) {
                builder.setMessage(getString(R$string.bluetooth_turning_on));
            } else {
                builder.setMessage(getString(R$string.bluetooth_turning_off));
            }
            builder.setCancelable(false);
        } else {
            int i2 = this.mTimeout;
            if (i2 == 0) {
                CharSequence charSequence = this.mAppLabel;
                if (charSequence != null) {
                    string2 = getString(R$string.bluetooth_ask_lasting_discovery, new Object[]{charSequence});
                } else {
                    string2 = getString(R$string.bluetooth_ask_lasting_discovery_no_name);
                }
                builder.setMessage(string2);
            } else {
                CharSequence charSequence2 = this.mAppLabel;
                if (charSequence2 != null) {
                    string = getString(R$string.bluetooth_ask_discovery, new Object[]{charSequence2, Integer.valueOf(i2)});
                } else {
                    string = getString(R$string.bluetooth_ask_discovery_no_name, new Object[]{Integer.valueOf(i2)});
                }
                builder.setMessage(string);
            }
            builder.setPositiveButton(getString(R$string.allow), this);
            builder.setNegativeButton(getString(R$string.deny), this);
        }
        builder.setOnDismissListener(this);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i2 != -1) {
            cancelAndFinish();
            return;
        }
        int i3 = this.mRequest;
        if (i3 == 1 || i3 == 2) {
            if (this.mBluetoothAdapter.getState() == 12) {
                proceedAndFinish();
                return;
            }
            StateChangeReceiver stateChangeReceiver = new StateChangeReceiver();
            this.mReceiver = stateChangeReceiver;
            registerReceiver(stateChangeReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            createDialog();
        } else if (i3 == 3) {
            if (this.mBluetoothAdapter.getState() == 10) {
                proceedAndFinish();
                return;
            }
            StateChangeReceiver stateChangeReceiver2 = new StateChangeReceiver();
            this.mReceiver = stateChangeReceiver2;
            registerReceiver(stateChangeReceiver2, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            createDialog();
        } else {
            cancelAndFinish();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            cancelAndFinish();
        } else if (i != -1) {
        } else {
            proceedAndFinish();
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        cancelAndFinish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void proceedAndFinish() {
        int i = this.mRequest;
        int i2 = 1;
        if (i == 1 || i == 3) {
            i2 = -1;
        } else {
            this.mBluetoothAdapter.setDiscoverableTimeout(Duration.ofSeconds(this.mTimeout));
            if (this.mBluetoothAdapter.setScanMode(23) == 0) {
                long currentTimeMillis = System.currentTimeMillis() + (this.mTimeout * 1000);
                LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this, currentTimeMillis);
                if (this.mTimeout > 0) {
                    BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this, currentTimeMillis);
                }
                int i3 = this.mTimeout;
                if (i3 >= 1) {
                    i2 = i3;
                }
            } else {
                i2 = 0;
            }
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        setResult(i2);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAndFinish() {
        setResult(0);
        finish();
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return true;
        }
        if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_ENABLE")) {
            this.mRequest = 1;
        } else if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISABLE")) {
            this.mRequest = 3;
        } else if (intent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE")) {
            this.mRequest = 2;
            this.mTimeout = intent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
            Log.d("BtRequestPermission", "Setting Bluetooth Discoverable Timeout = " + this.mTimeout);
            int i = this.mTimeout;
            if (i < 1 || i > 3600) {
                this.mTimeout = 120;
            }
        } else {
            Log.e("BtRequestPermission", "Error: this activity may be started only with intent android.bluetooth.adapter.action.REQUEST_ENABLE, android.bluetooth.adapter.action.REQUEST_DISABLE or android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
            setResult(0);
            return true;
        }
        String launchedFromPackage = getLaunchedFromPackage();
        int launchedFromUid = getLaunchedFromUid();
        if (UserHandle.isSameApp(launchedFromUid, AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP) && getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME") != null) {
            launchedFromPackage = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        }
        if (!UserHandle.isSameApp(launchedFromUid, AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP) && getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME") != null) {
            Log.w("BtRequestPermission", "Non-system Uid: " + launchedFromUid + " tried to override packageName \n");
        }
        if (!TextUtils.isEmpty(launchedFromPackage)) {
            try {
                this.mAppLabel = getPackageManager().getApplicationInfo(launchedFromPackage, 0).loadSafeLabel(getPackageManager(), 1000.0f, 5);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("BtRequestPermission", "Couldn't find app with package name " + launchedFromPackage);
                setResult(0);
                return true;
            }
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Log.e("BtRequestPermission", "Error: there's a problem starting Bluetooth");
            setResult(0);
            return true;
        }
        return false;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        BroadcastReceiver broadcastReceiver = this.mReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            this.mReceiver = null;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class StateChangeReceiver extends BroadcastReceiver {
        public StateChangeReceiver() {
            RequestPermissionActivity.this.getWindow().getDecorView().postDelayed(new Runnable() { // from class: com.android.settings.bluetooth.RequestPermissionActivity$StateChangeReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RequestPermissionActivity.StateChangeReceiver.this.lambda$new$0();
                }
            }, 10000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            if (RequestPermissionActivity.this.isFinishing() || RequestPermissionActivity.this.isDestroyed()) {
                return;
            }
            RequestPermissionActivity.this.cancelAndFinish();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            int i = RequestPermissionActivity.this.mRequest;
            if (i == 1 || i == 2) {
                if (intExtra == 12) {
                    RequestPermissionActivity.this.proceedAndFinish();
                }
            } else if (i == 3 && intExtra == 10) {
                RequestPermissionActivity.this.proceedAndFinish();
            }
        }
    }
}
