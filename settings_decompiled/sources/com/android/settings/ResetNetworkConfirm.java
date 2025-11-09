package com.android.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkPolicyManager;
import android.net.Uri;
import android.net.VpnManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.RecoverySystem;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
/* loaded from: classes.dex */
public class ResetNetworkConfirm extends InstrumentedFragment {
    Activity mActivity;
    private AlertDialog mAlertDialog;
    View mContentView;
    boolean mEraseEsim;
    private ProgressDialog mProgressDialog;
    ResetNetworkTask mResetNetworkTask;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener;
    private int mSubId = -1;
    View.OnClickListener mFinalClickListener = new View.OnClickListener() { // from class: com.android.settings.ResetNetworkConfirm.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (Utils.isMonkeyRunning()) {
                return;
            }
            if (ResetNetworkConfirm.this.mSubId != -1) {
                SubscriptionManager subscriptionManager = ResetNetworkConfirm.this.getSubscriptionManager();
                ResetNetworkConfirm.this.stopMonitorSubscriptionChange(subscriptionManager);
                ResetNetworkConfirm resetNetworkConfirm = ResetNetworkConfirm.this;
                if (!resetNetworkConfirm.isSubscriptionRemainActive(subscriptionManager, resetNetworkConfirm.mSubId)) {
                    Log.w("ResetNetworkConfirm", "subId " + ResetNetworkConfirm.this.mSubId + " disappear when confirm");
                    ResetNetworkConfirm.this.mActivity.finish();
                    return;
                }
            }
            if (ResetNetworkConfirm.this.mProgressDialog != null && ResetNetworkConfirm.this.mProgressDialog.isShowing()) {
                ResetNetworkConfirm.this.mProgressDialog.dismiss();
            }
            ResetNetworkConfirm resetNetworkConfirm2 = ResetNetworkConfirm.this;
            resetNetworkConfirm2.mProgressDialog = resetNetworkConfirm2.getProgressDialog(resetNetworkConfirm2.mActivity);
            ResetNetworkConfirm.this.mProgressDialog.show();
            ResetNetworkConfirm resetNetworkConfirm3 = ResetNetworkConfirm.this;
            ResetNetworkConfirm resetNetworkConfirm4 = ResetNetworkConfirm.this;
            resetNetworkConfirm3.mResetNetworkTask = new ResetNetworkTask(resetNetworkConfirm4.mActivity);
            ResetNetworkConfirm.this.mResetNetworkTask.execute(new Void[0]);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 84;
    }

    /* loaded from: classes.dex */
    private class ResetNetworkTask extends AsyncTask<Void, Void, Boolean> {
        private final Context mContext;
        private final String mPackageName;

        ResetNetworkTask(Context context) {
            this.mContext = context;
            this.mPackageName = context.getPackageName();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            BluetoothAdapter adapter;
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
            if (connectivityManager != null) {
                connectivityManager.factoryReset();
            }
            VpnManager vpnManager = (VpnManager) this.mContext.getSystemService(VpnManager.class);
            if (vpnManager != null) {
                vpnManager.factoryReset();
            }
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            if (wifiManager != null) {
                wifiManager.factoryReset();
            }
            ResetNetworkConfirm.this.p2pFactoryReset(this.mContext);
            boolean wipeEuiccData = ResetNetworkConfirm.this.mEraseEsim ? RecoverySystem.wipeEuiccData(this.mContext, this.mPackageName) : true;
            TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(ResetNetworkConfirm.this.mSubId);
            if (createForSubscriptionId != null) {
                createForSubscriptionId.resetSettings();
            }
            NetworkPolicyManager networkPolicyManager = (NetworkPolicyManager) this.mContext.getSystemService("netpolicy");
            if (networkPolicyManager != null) {
                networkPolicyManager.factoryReset(createForSubscriptionId.getSubscriberId());
            }
            BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
            if (bluetoothManager != null && (adapter = bluetoothManager.getAdapter()) != null) {
                adapter.clearBluetooth();
            }
            ResetNetworkConfirm.this.restoreDefaultApn(this.mContext);
            Log.d("ResetNetworkTask", "network factoryReset complete. succeeded: " + String.valueOf(wipeEuiccData));
            return Boolean.valueOf(wipeEuiccData);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            if (ResetNetworkConfirm.this.mProgressDialog != null && ResetNetworkConfirm.this.mProgressDialog.isShowing()) {
                ResetNetworkConfirm.this.mProgressDialog.dismiss();
            }
            if (bool.booleanValue()) {
                Toast.makeText(this.mContext, R$string.reset_network_complete_toast, 0).show();
                return;
            }
            ResetNetworkConfirm.this.mAlertDialog = new AlertDialog.Builder(this.mContext).setTitle(R$string.reset_esim_error_title).setMessage(R$string.reset_esim_error_msg).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
        }
    }

    void p2pFactoryReset(Context context) {
        WifiP2pManager.Channel initialize;
        WifiP2pManager wifiP2pManager = (WifiP2pManager) context.getSystemService("wifip2p");
        if (wifiP2pManager == null || (initialize = wifiP2pManager.initialize(context.getApplicationContext(), context.getMainLooper(), null)) == null) {
            return;
        }
        wifiP2pManager.factoryReset(initialize, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R$string.main_clear_progress_text));
        return progressDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreDefaultApn(Context context) {
        Uri parse = Uri.parse("content://telephony/carriers/restore");
        if (SubscriptionManager.isUsableSubscriptionId(this.mSubId)) {
            parse = Uri.withAppendedPath(parse, "subId/" + String.valueOf(this.mSubId));
        }
        context.getContentResolver().delete(parse, null, null);
    }

    private void establishFinalConfirmationState() {
        this.mContentView.findViewById(R$id.execute_reset_network).setOnClickListener(this.mFinalClickListener);
    }

    void setSubtitle() {
        if (this.mEraseEsim) {
            ((TextView) this.mContentView.findViewById(R$id.reset_network_confirm)).setText(R$string.reset_network_final_desc_esim);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mActivity, "no_network_reset", UserHandle.myUserId());
        if (RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mActivity, "no_network_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(R$layout.network_reset_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            new ActionDisabledByAdminDialogHelper(this.mActivity).prepareDialogBuilder("no_network_reset", checkIfRestrictionEnforced).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.ResetNetworkConfirm$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ResetNetworkConfirm.this.lambda$onCreateView$0(dialogInterface);
                }
            }).show();
            return new View(this.mActivity);
        }
        this.mContentView = layoutInflater.inflate(R$layout.reset_network_confirm, (ViewGroup) null);
        establishFinalConfirmationState();
        setSubtitle();
        return this.mContentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateView$0(DialogInterface dialogInterface) {
        this.mActivity.finish();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mSubId = arguments.getInt("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
            this.mEraseEsim = arguments.getBoolean("erase_esim");
        }
        this.mActivity = getActivity();
        if (this.mSubId == -1) {
            return;
        }
        startMonitorSubscriptionChange(getSubscriptionManager());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubscriptionManager getSubscriptionManager() {
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mActivity.getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null) {
            Log.w("ResetNetworkConfirm", "No SubscriptionManager");
        }
        return subscriptionManager;
    }

    private void startMonitorSubscriptionChange(SubscriptionManager subscriptionManager) {
        if (subscriptionManager == null) {
            return;
        }
        this.mSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener(Looper.getMainLooper()) { // from class: com.android.settings.ResetNetworkConfirm.2
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                SubscriptionManager subscriptionManager2 = ResetNetworkConfirm.this.getSubscriptionManager();
                ResetNetworkConfirm resetNetworkConfirm = ResetNetworkConfirm.this;
                if (resetNetworkConfirm.isSubscriptionRemainActive(subscriptionManager2, resetNetworkConfirm.mSubId)) {
                    return;
                }
                Log.w("ResetNetworkConfirm", "subId " + ResetNetworkConfirm.this.mSubId + " no longer active.");
                ResetNetworkConfirm.this.stopMonitorSubscriptionChange(subscriptionManager2);
                ResetNetworkConfirm.this.mActivity.finish();
            }
        };
        subscriptionManager.addOnSubscriptionsChangedListener(this.mActivity.getMainExecutor(), this.mSubscriptionsChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSubscriptionRemainActive(SubscriptionManager subscriptionManager, int i) {
        return (subscriptionManager == null || subscriptionManager.getActiveSubscriptionInfo(i) == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopMonitorSubscriptionChange(SubscriptionManager subscriptionManager) {
        SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener;
        if (subscriptionManager == null || (onSubscriptionsChangedListener = this.mSubscriptionsChangedListener) == null) {
            return;
        }
        subscriptionManager.removeOnSubscriptionsChangedListener(onSubscriptionsChangedListener);
        this.mSubscriptionsChangedListener = null;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ResetNetworkTask resetNetworkTask = this.mResetNetworkTask;
        if (resetNetworkTask != null) {
            resetNetworkTask.cancel(true);
            this.mResetNetworkTask = null;
        }
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        stopMonitorSubscriptionChange(getSubscriptionManager());
        super.onDestroy();
    }
}
