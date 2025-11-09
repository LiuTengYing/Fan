package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class WifiNoInternetDialog extends AlertActivity implements DialogInterface.OnClickListener {
    private String mAction;
    CheckBox mAlwaysAllow;
    private boolean mButtonClicked;
    private ConnectivityManager mCM;
    private Network mNetwork;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private String mNetworkName;

    private boolean isKnownAction(Intent intent) {
        return "android.net.action.PROMPT_UNVALIDATED".equals(intent.getAction()) || "android.net.action.PROMPT_LOST_VALIDATION".equals(intent.getAction()) || "android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(intent.getAction());
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null || !isKnownAction(intent)) {
            Log.e("WifiNoInternetDialog", "Unexpected intent " + intent + ", exiting");
            finish();
            return;
        }
        this.mAction = intent.getAction();
        Network network = (Network) intent.getParcelableExtra("android.net.extra.NETWORK");
        this.mNetwork = network;
        if (network == null) {
            Log.e("WifiNoInternetDialog", "Can't determine network from intent extra, exiting");
            finish();
            return;
        }
        NetworkRequest build = new NetworkRequest.Builder().clearCapabilities().build();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.wifi.WifiNoInternetDialog.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network2) {
                if (WifiNoInternetDialog.this.mNetwork.equals(network2)) {
                    Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.this.mNetwork + " disconnected");
                    WifiNoInternetDialog.this.finish();
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network2, NetworkCapabilities networkCapabilities) {
                if (WifiNoInternetDialog.this.mNetwork.equals(network2) && networkCapabilities.hasCapability(16)) {
                    Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.this.mNetwork + " validated");
                    WifiNoInternetDialog.this.finish();
                }
            }
        };
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mCM = connectivityManager;
        connectivityManager.registerNetworkCallback(build, this.mNetworkCallback);
        NetworkInfo networkInfo = this.mCM.getNetworkInfo(this.mNetwork);
        NetworkCapabilities networkCapabilities = this.mCM.getNetworkCapabilities(this.mNetwork);
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting() || networkCapabilities == null) {
            Log.d("WifiNoInternetDialog", "Network " + this.mNetwork + " is not connected: " + networkInfo);
            finish();
            return;
        }
        String ssid = networkCapabilities.getSsid();
        this.mNetworkName = ssid;
        if (ssid != null) {
            this.mNetworkName = android.net.wifi.WifiInfo.sanitizeSsid(ssid);
        }
        createDialog();
    }

    private void createDialog() {
        ((AlertActivity) this).mAlert.setIcon(R$drawable.ic_settings_wireless);
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction)) {
            alertParams.mTitle = this.mNetworkName;
            alertParams.mMessage = getString(R$string.no_internet_access_text);
            alertParams.mPositiveButtonText = getString(R$string.yes);
            alertParams.mNegativeButtonText = getString(R$string.no);
        } else if ("android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
            alertParams.mTitle = this.mNetworkName;
            alertParams.mMessage = getString(R$string.partial_connectivity_text);
            alertParams.mPositiveButtonText = getString(R$string.yes);
            alertParams.mNegativeButtonText = getString(R$string.no);
        } else {
            alertParams.mTitle = getString(R$string.lost_internet_access_title);
            alertParams.mMessage = getString(R$string.lost_internet_access_text);
            alertParams.mPositiveButtonText = getString(R$string.lost_internet_access_switch);
            alertParams.mNegativeButtonText = getString(R$string.lost_internet_access_cancel);
        }
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonListener = this;
        View inflate = LayoutInflater.from(alertParams.mContext).inflate(17367093, (ViewGroup) null);
        alertParams.mView = inflate;
        this.mAlwaysAllow = (CheckBox) inflate.findViewById(16908774);
        if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction) || "android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
            this.mAlwaysAllow.setText(getString(R$string.no_internet_access_remember));
        } else {
            this.mAlwaysAllow.setText(getString(R$string.lost_internet_access_persist));
        }
        setupAlert();
    }

    protected void onDestroy() {
        ConnectivityManager.NetworkCallback networkCallback = this.mNetworkCallback;
        if (networkCallback != null) {
            this.mCM.unregisterNetworkCallback(networkCallback);
            this.mNetworkCallback = null;
        }
        if (isFinishing() && !this.mButtonClicked) {
            if ("android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
                this.mCM.setAcceptPartialConnectivity(this.mNetwork, false, false);
            } else if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction)) {
                this.mCM.setAcceptUnvalidated(this.mNetwork, false, false);
            }
        }
        super.onDestroy();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        boolean z;
        String str;
        String str2;
        if (i == -2 || i == -1) {
            boolean isChecked = this.mAlwaysAllow.isChecked();
            this.mButtonClicked = true;
            if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction)) {
                z = i == -1;
                str = z ? "Connect" : "Ignore";
                this.mCM.setAcceptUnvalidated(this.mNetwork, z, isChecked);
                str2 = "NO_INTERNET";
            } else if ("android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
                z = i == -1;
                str = z ? "Connect" : "Ignore";
                this.mCM.setAcceptPartialConnectivity(this.mNetwork, z, isChecked);
                str2 = "PARTIAL_CONNECTIVITY";
            } else {
                z = i == -1;
                str = z ? "Switch away" : "Get stuck";
                if (isChecked) {
                    Settings.Global.putString(((AlertActivity) this).mAlertParams.mContext.getContentResolver(), "network_avoid_bad_wifi", z ? "1" : "0");
                } else if (z) {
                    this.mCM.setAvoidUnvalidated(this.mNetwork);
                }
                str2 = "LOST_INTERNET";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(": ");
            sb.append(str);
            sb.append(" network=");
            sb.append(this.mNetwork);
            sb.append(isChecked ? " and remember" : "");
            Log.d("WifiNoInternetDialog", sb.toString());
        }
    }
}
