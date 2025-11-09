package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.MacAddress;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$id;
import com.android.settings.widget.MacAddressEditText;
import com.android.settingslib.CustomDialogPreferenceCompat;
import java.util.List;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public class UniHotspotAddWhiteListPreference extends CustomDialogPreferenceCompat implements DialogInterface.OnShowListener {
    TextWatcher mAddTextChangedListener;
    private Context mContext;
    private boolean mIsPositiveButtonEnabled;
    private MacAddressEditText mMacText;
    MacAddressEditText.MacWatcher mMacTextChangedListener;
    private EditText mNameText;
    private SharedPreferences mWhiteSharedPreferences;
    private WifiManager mWifiManager;
    private WifiTetherSettings mWifiTetherSettings;

    public UniHotspotAddWhiteListPreference(Context context) {
        super(context);
        this.mIsPositiveButtonEnabled = false;
        this.mWifiTetherSettings = new WifiTetherSettings();
        this.mWhiteSharedPreferences = null;
        this.mAddTextChangedListener = new TextWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mMacTextChangedListener = new MacAddressEditText.MacWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.2
            @Override // com.android.settings.widget.MacAddressEditText.MacWatcher
            public void onTextChanged() {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public UniHotspotAddWhiteListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsPositiveButtonEnabled = false;
        this.mWifiTetherSettings = new WifiTetherSettings();
        this.mWhiteSharedPreferences = null;
        this.mAddTextChangedListener = new TextWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mMacTextChangedListener = new MacAddressEditText.MacWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.2
            @Override // com.android.settings.widget.MacAddressEditText.MacWatcher
            public void onTextChanged() {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public UniHotspotAddWhiteListPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsPositiveButtonEnabled = false;
        this.mWifiTetherSettings = new WifiTetherSettings();
        this.mWhiteSharedPreferences = null;
        this.mAddTextChangedListener = new TextWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mMacTextChangedListener = new MacAddressEditText.MacWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.2
            @Override // com.android.settings.widget.MacAddressEditText.MacWatcher
            public void onTextChanged() {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public UniHotspotAddWhiteListPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mIsPositiveButtonEnabled = false;
        this.mWifiTetherSettings = new WifiTetherSettings();
        this.mWhiteSharedPreferences = null;
        this.mAddTextChangedListener = new TextWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i22, int i222, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i22, int i222, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mMacTextChangedListener = new MacAddressEditText.MacWatcher() { // from class: com.android.settings.wifi.tether.UniHotspotAddWhiteListPreference.2
            @Override // com.android.settings.widget.MacAddressEditText.MacWatcher
            public void onTextChanged() {
                UniHotspotAddWhiteListPreference uniHotspotAddWhiteListPreference = UniHotspotAddWhiteListPreference.this;
                uniHotspotAddWhiteListPreference.mIsPositiveButtonEnabled = uniHotspotAddWhiteListPreference.isAddWhitelistButtonEnabled();
                UniHotspotAddWhiteListPreference.this.updatePositiveButton();
            }
        };
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.mWhiteSharedPreferences = getContext().getSharedPreferences("com.android.settings.wifi.tether.UniWhiteListWifiTetherClientsPreference", 0);
        setOnShowListener(this);
        addWhiteListViews((ScrollView) view);
    }

    private void addWhiteListViews(ScrollView scrollView) {
        this.mNameText = (EditText) scrollView.findViewById(R$id.nameText);
        this.mMacText = (MacAddressEditText) scrollView.findViewById(R$id.macText);
        this.mNameText.addTextChangedListener(this.mAddTextChangedListener);
        this.mMacText.addTextChangedListener(this.mMacTextChangedListener);
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        this.mIsPositiveButtonEnabled = isAddWhitelistButtonEnabled();
        updatePositiveButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAddWhitelistButtonEnabled() {
        return (TextUtils.isEmpty(this.mMacText.getText()) || !checkMac(this.mMacText.getText().toString().trim()) || TextUtils.isEmpty(this.mNameText.getText()) || this.mNameText.getText().toString().trim().equals("")) ? false : true;
    }

    private boolean checkMac(String str) {
        return Pattern.matches("^[A-Fa-f0-9]{2}(:[A-Fa-f0-9]{2}){5}$", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePositiveButton() {
        AlertDialog alertDialog = (AlertDialog) getDialog();
        Button button = alertDialog == null ? null : alertDialog.getButton(-1);
        if (button == null || this.mNameText == null || this.mMacText == null) {
            return;
        }
        button.setEnabled(this.mIsPositiveButtonEnabled);
    }

    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    protected void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            MacAddress fromString = MacAddress.fromString(this.mMacText.getText().toString().trim());
            String trim = this.mNameText.getText().toString().trim();
            SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
            SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder(softApConfiguration);
            List allowedClientList = softApConfiguration.getAllowedClientList();
            if (fromString != null) {
                allowedClientList.add(fromString);
                builder.setAllowedClientList(allowedClientList);
                this.mWifiManager.setSoftApConfiguration(builder.build());
                this.mWhiteSharedPreferences.edit().putString(fromString.toString(), trim).commit();
                if (getContext() != null) {
                    Intent intent = new Intent("android.net.wifi.WIFI_AP_CLIENT_DETAILINFO_AVAILABLE_ACTION");
                    intent.addFlags(67108864);
                    getContext().sendBroadcastAsUser(intent, UserHandle.ALL);
                }
            }
            callChangeListener(Boolean.TRUE);
            return;
        }
        callChangeListener(Boolean.FALSE);
    }
}
