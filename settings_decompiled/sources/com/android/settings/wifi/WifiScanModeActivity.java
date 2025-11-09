package com.android.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.wifi.WifiPermissionChecker;
/* loaded from: classes.dex */
public class WifiScanModeActivity extends FragmentActivity {
    String mApp;
    private DialogFragment mDialog;
    WifiPermissionChecker mWifiPermissionChecker;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        Intent intent = getIntent();
        if (bundle == null) {
            if (intent != null && "android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE".equals(intent.getAction())) {
                refreshAppLabel();
            } else {
                finish();
                return;
            }
        } else {
            this.mApp = bundle.getString("app");
        }
        createDialog();
    }

    void refreshAppLabel() {
        if (this.mWifiPermissionChecker == null) {
            this.mWifiPermissionChecker = new WifiPermissionChecker(this);
        }
        String launchedPackage = this.mWifiPermissionChecker.getLaunchedPackage();
        if (TextUtils.isEmpty(launchedPackage)) {
            this.mApp = null;
        } else {
            this.mApp = Utils.getApplicationLabel(getApplicationContext(), launchedPackage).toString();
        }
    }

    void createDialog() {
        if (isGuestUser(getApplicationContext())) {
            Log.e("WifiScanModeActivity", "Guest user is not allowed to configure Wi-Fi Scan Mode!");
            EventLog.writeEvent(1397638484, "235601169", -1, "User is a guest");
            finish();
        } else if (this.mDialog == null) {
            AlertDialogFragment newInstance = AlertDialogFragment.newInstance(this.mApp);
            this.mDialog = newInstance;
            newInstance.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void dismissDialog() {
        DialogFragment dialogFragment = this.mDialog;
        if (dialogFragment != null) {
            dialogFragment.dismiss();
            this.mDialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doPositiveClick() {
        ((WifiManager) getApplicationContext().getSystemService(WifiManager.class)).setScanAlwaysAvailable(true);
        setResult(-1);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doNegativeClick() {
        setResult(0);
        finish();
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("app", this.mApp);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        createDialog();
    }

    /* loaded from: classes.dex */
    public static class AlertDialogFragment extends InstrumentedDialogFragment {
        private final String mApp;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 543;
        }

        static AlertDialogFragment newInstance(String str) {
            return new AlertDialogFragment(str);
        }

        public AlertDialogFragment(String str) {
            this.mApp = str;
        }

        public AlertDialogFragment() {
            this.mApp = null;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            String string;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (TextUtils.isEmpty(this.mApp)) {
                string = getString(R$string.wifi_scan_always_turn_on_message_unknown);
            } else {
                string = getString(R$string.wifi_scan_always_turnon_message, this.mApp);
            }
            return builder.setMessage(string).setPositiveButton(R$string.wifi_scan_always_confirm_allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiScanModeActivity.AlertDialogFragment.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((WifiScanModeActivity) AlertDialogFragment.this.getActivity()).doPositiveClick();
                }
            }).setNegativeButton(R$string.wifi_scan_always_confirm_deny, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiScanModeActivity.AlertDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((WifiScanModeActivity) AlertDialogFragment.this.getActivity()).doNegativeClick();
                }
            }).create();
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            ((WifiScanModeActivity) getActivity()).doNegativeClick();
        }
    }

    private static boolean isGuestUser(Context context) {
        UserManager userManager;
        if (context == null || (userManager = (UserManager) context.getSystemService(UserManager.class)) == null) {
            return false;
        }
        return userManager.isGuestUser();
    }
}
