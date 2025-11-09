package com.android.settings.bluetooth;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.R$bool;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class RequestPermissionHelperActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private CharSequence mAppLabel;
    private BluetoothAdapter mBluetoothAdapter;
    private int mRequest;
    private int mTimeout = -1;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setResult(0);
        if (!parseIntent()) {
            finish();
            return;
        }
        if (getResources().getBoolean(R$bool.auto_confirm_bluetooth_activation_dialog)) {
            onClick(null, -1);
            dismiss();
        }
        createDialog();
    }

    void createDialog() {
        String string;
        String string2;
        String string3;
        String string4;
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        int i = this.mRequest;
        if (i == 1) {
            int i2 = this.mTimeout;
            if (i2 < 0) {
                CharSequence charSequence = this.mAppLabel;
                if (charSequence != null) {
                    string3 = getString(R$string.bluetooth_ask_enablement, new Object[]{charSequence});
                } else {
                    string3 = getString(R$string.bluetooth_ask_enablement_no_name);
                }
                alertParams.mMessage = string3;
            } else if (i2 == 0) {
                CharSequence charSequence2 = this.mAppLabel;
                if (charSequence2 != null) {
                    string2 = getString(R$string.bluetooth_ask_enablement_and_lasting_discovery, new Object[]{charSequence2});
                } else {
                    string2 = getString(R$string.bluetooth_ask_enablement_and_lasting_discovery_no_name);
                }
                alertParams.mMessage = string2;
            } else {
                CharSequence charSequence3 = this.mAppLabel;
                if (charSequence3 != null) {
                    string = getString(R$string.bluetooth_ask_enablement_and_discovery, new Object[]{charSequence3, Integer.valueOf(i2)});
                } else {
                    string = getString(R$string.bluetooth_ask_enablement_and_discovery_no_name, new Object[]{Integer.valueOf(i2)});
                }
                alertParams.mMessage = string;
            }
        } else if (i == 3) {
            CharSequence charSequence4 = this.mAppLabel;
            if (charSequence4 != null) {
                string4 = getString(R$string.bluetooth_ask_disablement, new Object[]{charSequence4});
            } else {
                string4 = getString(R$string.bluetooth_ask_disablement_no_name);
            }
            alertParams.mMessage = string4;
        }
        alertParams.mPositiveButtonText = getString(R$string.allow);
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonText = getString(R$string.deny);
        setupAlert();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.mRequest;
        if (i2 != 1 && i2 != 2) {
            if (i2 != 3) {
                return;
            }
            this.mBluetoothAdapter.disable();
            setResult(-1);
        } else if (((UserManager) getSystemService(UserManager.class)).hasUserRestriction("no_bluetooth")) {
            Intent createAdminSupportIntent = ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).createAdminSupportIntent("no_bluetooth");
            if (createAdminSupportIntent != null) {
                startActivity(createAdminSupportIntent);
            }
        } else {
            this.mBluetoothAdapter.enable();
            setResult(-1);
        }
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        String action = intent.getAction();
        if ("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON".equals(action)) {
            this.mRequest = 1;
            if (intent.hasExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION")) {
                this.mTimeout = intent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
            }
        } else if (!"com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_OFF".equals(action)) {
            return false;
        } else {
            this.mRequest = 3;
        }
        this.mAppLabel = getIntent().getCharSequenceExtra("com.android.settings.bluetooth.extra.APP_LABEL");
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            Log.e("RequestPermissionHelperActivity", "Error: there's a problem starting Bluetooth");
            return false;
        }
        return true;
    }
}
