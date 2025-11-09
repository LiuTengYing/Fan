package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.MacAddress;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.widget.MacAddressEditText;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public class UniSoftApClientsDialog extends AlertDialog implements DialogInterface.OnClickListener, DialogInterface.OnShowListener {
    TextWatcher mAddTextChangedListener;
    private ClientData mClientData;
    private Context mContext;
    private TextView mIp;
    private TextView mIpLinear;
    private boolean mIsPositiveButtonEnabled;
    private final SoftApClientsDialogListener mListener;
    private TextView mMac;
    MacAddressEditText mMacText;
    MacAddressEditText.MacWatcher mMacTextChangedListener;
    EditText mNameText;
    private int mPreferenceTypes;
    private TextView mTitle;
    private View mView;
    private final WifiManager mWifiManager;

    /* loaded from: classes2.dex */
    public interface SoftApClientsDialogListener {
        default void onCancel(UniSoftApClientsDialog uniSoftApClientsDialog) {
        }

        default void onWhite(UniSoftApClientsDialog uniSoftApClientsDialog) {
        }
    }

    public static UniSoftApClientsDialog createModal(Context context, SoftApClientsDialogListener softApClientsDialogListener, ClientData clientData, int i) {
        return new UniSoftApClientsDialog(context, softApClientsDialogListener, clientData, i);
    }

    UniSoftApClientsDialog(Context context, SoftApClientsDialogListener softApClientsDialogListener, ClientData clientData, int i) {
        super(context);
        this.mIsPositiveButtonEnabled = false;
        this.mAddTextChangedListener = new TextWatcher() { // from class: com.android.settings.wifi.tether.UniSoftApClientsDialog.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                UniSoftApClientsDialog uniSoftApClientsDialog = UniSoftApClientsDialog.this;
                uniSoftApClientsDialog.mIsPositiveButtonEnabled = uniSoftApClientsDialog.isAddWhitelistButtonEnabled();
                UniSoftApClientsDialog.this.updatePositiveButton();
            }
        };
        this.mMacTextChangedListener = new MacAddressEditText.MacWatcher() { // from class: com.android.settings.wifi.tether.UniSoftApClientsDialog.2
            @Override // com.android.settings.widget.MacAddressEditText.MacWatcher
            public void onTextChanged() {
                UniSoftApClientsDialog uniSoftApClientsDialog = UniSoftApClientsDialog.this;
                uniSoftApClientsDialog.mIsPositiveButtonEnabled = uniSoftApClientsDialog.isAddWhitelistButtonEnabled();
                UniSoftApClientsDialog.this.updatePositiveButton();
            }
        };
        this.mContext = context;
        this.mListener = softApClientsDialogListener;
        this.mPreferenceTypes = i;
        this.mClientData = clientData;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(R$layout.softap_client_dialog, (ViewGroup) null);
        this.mView = inflate;
        setView(inflate);
        initSoftApClientsController(this.mClientData, this.mPreferenceTypes);
        super.onCreate(bundle);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        SoftApClientsDialogListener softApClientsDialogListener = this.mListener;
        if (softApClientsDialogListener != null) {
            if (i == -3) {
                softApClientsDialogListener.onCancel(this);
            } else if (i != -1) {
            } else {
                softApClientsDialogListener.onWhite(this);
            }
        }
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        this.mIsPositiveButtonEnabled = isAddWhitelistButtonEnabled();
        updatePositiveButton();
    }

    public int getPreferenceTypes() {
        return this.mPreferenceTypes;
    }

    public void setWhiteButton(CharSequence charSequence) {
        setButton(-1, charSequence, this);
    }

    public void setCancelButton(CharSequence charSequence) {
        setButton(-3, charSequence, this);
    }

    public SoftApConfiguration getSoftApConfig() {
        return this.mWifiManager.getSoftApConfiguration();
    }

    private void initSoftApClientsController(ClientData clientData, int i) {
        String str;
        MacAddress macAddress;
        Log.d("UniSoftApClientsDialog", "initSoftApClientsController");
        this.mPreferenceTypes = i;
        Resources resources = this.mContext.getResources();
        this.mIpLinear = (TextView) this.mView.findViewById(R$id.ip_linear);
        this.mTitle = (TextView) this.mView.findViewById(R$id.title);
        this.mMac = (TextView) this.mView.findViewById(R$id.mac);
        this.mIp = (TextView) this.mView.findViewById(R$id.ip);
        String str2 = null;
        if (clientData != null) {
            str2 = clientData.getHostName();
            macAddress = clientData.getMacAddress();
            str = clientData.getIpAddress();
        } else {
            str = null;
            macAddress = null;
        }
        if (str2 != null) {
            this.mTitle.setText(str2);
        }
        if (macAddress != null) {
            this.mMac.setText(macAddress.toString());
        }
        if (i == 0) {
            this.mIp.setText(str);
        } else {
            this.mIpLinear.setVisibility(8);
            this.mIp.setVisibility(8);
        }
        displayDialogButton(resources, i);
    }

    private void displayDialogButton(Resources resources, int i) {
        if (i == 0) {
            setWhiteButton(resources.getString(R$string.hotspot_whitelist_add));
            setCancelButton(resources.getString(R$string.hotspot_whitelist_cancel));
        } else if (i != 1) {
        } else {
            setWhiteButton(resources.getString(R$string.hotspot_offwhite));
            setCancelButton(resources.getString(R$string.hotspot_whitelist_cancel));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePositiveButton() {
        Button button = getButton(-1);
        if (button == null || this.mNameText == null || this.mMacText == null) {
            return;
        }
        button.setEnabled(this.mIsPositiveButtonEnabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAddWhitelistButtonEnabled() {
        return (TextUtils.isEmpty(this.mMacText.getText()) || !checkMac(this.mMacText.getText().toString().trim()) || TextUtils.isEmpty(this.mNameText.getText()) || this.mNameText.getText().toString().trim().equals("")) ? false : true;
    }

    private boolean checkMac(String str) {
        return Pattern.matches("^[A-Fa-f0-9]{2}(:[A-Fa-f0-9]{2}){5}$", str);
    }

    /* loaded from: classes2.dex */
    public static class ClientData implements Parcelable {
        public static final Parcelable.Creator<ClientData> CREATOR = new Parcelable.Creator<ClientData>() { // from class: com.android.settings.wifi.tether.UniSoftApClientsDialog.ClientData.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ClientData createFromParcel(Parcel parcel) {
                return new ClientData(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ClientData[] newArray(int i) {
                return new ClientData[i];
            }
        };
        private String mHostName;
        private String mIpAddress;
        private MacAddress mMacAddress;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ClientData(String str, MacAddress macAddress, String str2) {
            this.mHostName = str;
            this.mMacAddress = macAddress;
            this.mIpAddress = str2;
        }

        public ClientData() {
        }

        public String getHostName() {
            return this.mHostName;
        }

        public MacAddress getMacAddress() {
            return this.mMacAddress;
        }

        public String getIpAddress() {
            return this.mIpAddress;
        }

        protected ClientData(Parcel parcel) {
            this.mHostName = parcel.readString();
            this.mMacAddress = (MacAddress) parcel.readParcelable(MacAddress.class.getClassLoader());
            this.mIpAddress = parcel.readString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.mHostName);
            parcel.writeParcelable(this.mMacAddress, i);
            parcel.writeString(this.mIpAddress);
        }
    }
}
