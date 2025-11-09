package com.android.settings.network.apn;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R$array;
import com.android.settings.R$bool;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.network.ProxySubscriptionManager;
import com.android.settings.network.telephony.BroadcastReceiverChanged;
import com.android.settingslib.utils.ThreadUtils;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public class ApnEditor extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, View.OnKeyListener, BroadcastReceiverChanged.BroadcastReceiverChangedClient {
    static final int APN_INDEX = 2;
    static final int CARRIER_ENABLED_INDEX = 17;
    static final int MCC_INDEX = 9;
    static final int MNC_INDEX = 10;
    static final int NAME_INDEX = 1;
    static final int PROTOCOL_INDEX = 16;
    static final int ROAMING_PROTOCOL_INDEX = 20;
    static final int TYPE_INDEX = 15;
    static String sNotSet;
    EditTextPreference mApn;
    ApnData mApnData;
    EditTextPreference mApnType;
    ListPreference mAuthType;
    MultiSelectListPreference mBearerMulti;
    private BroadcastReceiverChanged mBroadcastReceiverChanged;
    private MenuItem mCancelItem;
    SwitchPreference mCarrierEnabled;
    private Uri mCarrierUri;
    private String mCurMcc;
    private String mCurMnc;
    String mDefaultApnProtocol;
    String mDefaultApnRoamingProtocol;
    String[] mDefaultApnTypes;
    private MenuItem mDeleteItem;
    private boolean mIsCarrierIdApn;
    EditTextPreference mMcc;
    EditTextPreference mMmsPort;
    EditTextPreference mMmsProxy;
    EditTextPreference mMmsc;
    EditTextPreference mMnc;
    EditTextPreference mMvnoMatchData;
    private String mMvnoMatchDataStr;
    ListPreference mMvnoType;
    private String mMvnoTypeStr;
    EditTextPreference mName;
    private boolean mNewApn;
    private ProxySubscriptionManager.OnActiveSubscriptionChangedListener mOnSubscriptionsChangeListener;
    EditTextPreference mPassword;
    EditTextPreference mPort;
    ListPreference mProtocol;
    EditTextPreference mProxy;
    ProxySubscriptionManager mProxySubscriptionMgr;
    private boolean mReadOnlyApn;
    private String[] mReadOnlyApnFields;
    String[] mReadOnlyApnTypes;
    ListPreference mRoamingProtocol;
    private MenuItem mSaveItem;
    EditTextPreference mServer;
    private int mSubId;
    EditTextPreference mUser;
    private static final String TAG = ApnEditor.class.getSimpleName();
    public static final String[] APN_TYPES = {"default", "mms", "supl", "dun", "hipri", "fota", "ims", "cbs", "ia", "emergency", "mcx", "xcap"};
    private static final String[] sProjection = {"_id", "name", "apn", "proxy", "port", "user", "server", "password", "mmsc", "mcc", "mnc", "numeric", "mmsproxy", "mmsport", "authtype", "type", "protocol", "carrier_enabled", "bearer", "bearer_bitmask", "roaming_protocol", "mvno_type", "mvno_match_data", "edited", "user_editable", "carrier_id"};
    private int mBearerInitialVal = 0;
    private boolean mAllowEmptyApn = false;

    private static boolean bitmaskHasTech(int i, int i2) {
        if (i == 0) {
            return true;
        }
        return i2 >= 1 && (i & (1 << (i2 - 1))) != 0;
    }

    private static int getBitmaskForTech(int i) {
        if (i >= 1) {
            return 1 << (i - 1);
        }
        return 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 13;
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onCarrierConfigChanged(int i) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLifecycleForAllControllers();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            finish();
            return;
        }
        this.mSubId = intent.getIntExtra("sub_id", -1);
        initApnEditorUi();
        getCarrierCustomizedConfig();
        Uri uri = null;
        if (action.equals("android.intent.action.EDIT")) {
            uri = intent.getData();
            if (!uri.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
                String str = TAG;
                Log.e(str, "Edit request not for carrier table. Uri: " + uri);
                finish();
                return;
            }
        } else if (action.equals("android.intent.action.INSERT")) {
            Uri data = intent.getData();
            this.mCarrierUri = data;
            if (!data.isPathPrefixMatch(Telephony.Carriers.CONTENT_URI)) {
                String str2 = TAG;
                Log.e(str2, "Insert request not for carrier table. Uri: " + this.mCarrierUri);
                finish();
                return;
            }
            this.mNewApn = true;
            this.mMvnoTypeStr = intent.getStringExtra("mvno_type");
            this.mMvnoMatchDataStr = intent.getStringExtra("mvno_match_data");
        } else {
            finish();
            return;
        }
        if (uri != null) {
            this.mApnData = getApnDataFromUri(uri);
        } else {
            this.mApnData = new ApnData(sProjection.length);
        }
        this.mIsCarrierIdApn = this.mApnData.getInteger(25, -1).intValue() > -1;
        this.mAllowEmptyApn = getContext().getResources().getBoolean(R$bool.config_allow_empty_apn);
        ApnData apnData = this.mApnData;
        if (apnData == null) {
            finish();
            return;
        }
        boolean z = apnData.getInteger(23, 1).intValue() == 1;
        String str3 = TAG;
        Log.d(str3, "onCreate: EDITED " + z);
        if (!z && (this.mApnData.getInteger(24, 1).intValue() == 0 || apnTypesMatch(this.mReadOnlyApnTypes, this.mApnData.getString(15)))) {
            Log.d(str3, "onCreate: apnTypesMatch; read-only APN");
            this.mReadOnlyApn = true;
            disableAllFields();
        } else if (!ArrayUtils.isEmpty(this.mReadOnlyApnFields)) {
            disableFields(this.mReadOnlyApnFields);
        }
        if (this.mIsCarrierIdApn) {
            disableFieldsForCarrieridApn();
        }
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            getPreferenceScreen().getPreference(i).setOnPreferenceChangeListener(this);
        }
        ProxySubscriptionManager.OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener = new ProxySubscriptionManager.OnActiveSubscriptionChangedListener() { // from class: com.android.settings.network.apn.ApnEditor.1
            @Override // com.android.settings.network.ProxySubscriptionManager.OnActiveSubscriptionChangedListener
            public void onChanged() {
                ApnEditor apnEditor = ApnEditor.this;
                SubscriptionInfo activeSubscriptionInfo = apnEditor.mProxySubscriptionMgr.getActiveSubscriptionInfo(apnEditor.mSubId);
                String str4 = ApnEditor.TAG;
                Log.d(str4, "onSubscriptionsChanged: active subinfo " + activeSubscriptionInfo);
                if (activeSubscriptionInfo == null) {
                    ApnEditor.this.finish();
                }
            }
        };
        this.mOnSubscriptionsChangeListener = onActiveSubscriptionChangedListener;
        this.mProxySubscriptionMgr.addActiveSubscriptionsListener(onActiveSubscriptionChangedListener);
        BroadcastReceiverChanged broadcastReceiverChanged = new BroadcastReceiverChanged(getContext(), this);
        this.mBroadcastReceiverChanged = broadcastReceiverChanged;
        broadcastReceiverChanged.start();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mProxySubscriptionMgr.removeActiveSubscriptionsListener(this.mOnSubscriptionsChangeListener);
        this.mBroadcastReceiverChanged.stop();
    }

    private void setLifecycleForAllControllers() {
        if (this.mProxySubscriptionMgr == null) {
            this.mProxySubscriptionMgr = ProxySubscriptionManager.getInstance(getContext());
        }
        this.mProxySubscriptionMgr.setLifecycle(getLifecycle());
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        fillUI(bundle == null);
        setCarrierCustomizedConfigToUi();
    }

    static String formatInteger(String str) {
        try {
            return String.format(getCorrectDigitsFormat(str), Integer.valueOf(Integer.parseInt(str)));
        } catch (NumberFormatException unused) {
            return str;
        }
    }

    static String getCorrectDigitsFormat(String str) {
        return str.length() == 2 ? "%02d" : "%03d";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasAllApns(String[] strArr) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        List asList = Arrays.asList(strArr);
        if (asList.contains("*")) {
            Log.d(TAG, "hasAllApns: true because apnList.contains(APN_TYPE_ALL)");
            return true;
        }
        for (String str : APN_TYPES) {
            if (!asList.contains(str)) {
                return false;
            }
        }
        Log.d(TAG, "hasAllApns: true");
        return true;
    }

    private boolean apnTypesMatch(String[] strArr, String str) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        if (hasAllApns(strArr) || TextUtils.isEmpty(str)) {
            return true;
        }
        List asList = Arrays.asList(strArr);
        for (String str2 : str.split(",")) {
            if (asList.contains(str2.trim())) {
                Log.d(TAG, "apnTypesMatch: true because match found for " + str2.trim());
                return true;
            }
        }
        Log.d(TAG, "apnTypesMatch: false");
        return false;
    }

    private String hideApnMatchTypes(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return str;
        }
        String[] split = str.split(",");
        StringBuilder sb = new StringBuilder();
        int length = split.length;
        for (int i = 0; i < length; i++) {
            if (TextUtils.isEmpty(split[i]) || split[i].equals(str2)) {
                Log.d(TAG, "apnType " + str2 + " need to be hidden");
            } else {
                sb.append(",");
                sb.append(split[i]);
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        Log.d(TAG, "return apn types: " + sb.toString());
        return sb.toString();
    }

    private Preference getPreferenceFromFieldName(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2135515857:
                if (str.equals("mvno_type")) {
                    c = 0;
                    break;
                }
                break;
            case -1954254981:
                if (str.equals("mmsproxy")) {
                    c = 1;
                    break;
                }
                break;
            case -1640523526:
                if (str.equals("carrier_enabled")) {
                    c = 2;
                    break;
                }
                break;
            case -1393032351:
                if (str.equals("bearer")) {
                    c = 3;
                    break;
                }
                break;
            case -1230508389:
                if (str.equals("bearer_bitmask")) {
                    c = 4;
                    break;
                }
                break;
            case -1039601666:
                if (str.equals("roaming_protocol")) {
                    c = 5;
                    break;
                }
                break;
            case -989163880:
                if (str.equals("protocol")) {
                    c = 6;
                    break;
                }
                break;
            case -905826493:
                if (str.equals("server")) {
                    c = 7;
                    break;
                }
                break;
            case -520149991:
                if (str.equals("mvno_match_data")) {
                    c = '\b';
                    break;
                }
                break;
            case 96799:
                if (str.equals("apn")) {
                    c = '\t';
                    break;
                }
                break;
            case 107917:
                if (str.equals("mcc")) {
                    c = '\n';
                    break;
                }
                break;
            case 108258:
                if (str.equals("mnc")) {
                    c = 11;
                    break;
                }
                break;
            case 3355632:
                if (str.equals("mmsc")) {
                    c = '\f';
                    break;
                }
                break;
            case 3373707:
                if (str.equals("name")) {
                    c = '\r';
                    break;
                }
                break;
            case 3446913:
                if (str.equals("port")) {
                    c = 14;
                    break;
                }
                break;
            case 3575610:
                if (str.equals("type")) {
                    c = 15;
                    break;
                }
                break;
            case 3599307:
                if (str.equals("user")) {
                    c = 16;
                    break;
                }
                break;
            case 106941038:
                if (str.equals("proxy")) {
                    c = 17;
                    break;
                }
                break;
            case 1183882708:
                if (str.equals("mmsport")) {
                    c = 18;
                    break;
                }
                break;
            case 1216985755:
                if (str.equals("password")) {
                    c = 19;
                    break;
                }
                break;
            case 1433229538:
                if (str.equals("authtype")) {
                    c = 20;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return this.mMvnoType;
            case 1:
                return this.mMmsProxy;
            case 2:
                return this.mCarrierEnabled;
            case 3:
            case 4:
                return this.mBearerMulti;
            case 5:
                return this.mRoamingProtocol;
            case 6:
                return this.mProtocol;
            case 7:
                return this.mServer;
            case '\b':
                return this.mMvnoMatchData;
            case '\t':
                return this.mApn;
            case '\n':
                return this.mMcc;
            case 11:
                return this.mMnc;
            case '\f':
                return this.mMmsc;
            case '\r':
                return this.mName;
            case 14:
                return this.mPort;
            case 15:
                return this.mApnType;
            case 16:
                return this.mUser;
            case 17:
                return this.mProxy;
            case 18:
                return this.mMmsPort;
            case 19:
                return this.mPassword;
            case 20:
                return this.mAuthType;
            default:
                return null;
        }
    }

    private void disableFields(String[] strArr) {
        for (String str : strArr) {
            Preference preferenceFromFieldName = getPreferenceFromFieldName(str);
            if (preferenceFromFieldName != null) {
                preferenceFromFieldName.setEnabled(false);
            }
        }
    }

    private void disableAllFields() {
        this.mName.setEnabled(false);
        this.mApn.setEnabled(false);
        this.mProxy.setEnabled(false);
        this.mPort.setEnabled(false);
        this.mUser.setEnabled(false);
        this.mServer.setEnabled(false);
        this.mPassword.setEnabled(false);
        this.mMmsProxy.setEnabled(false);
        this.mMmsPort.setEnabled(false);
        this.mMmsc.setEnabled(false);
        this.mMcc.setEnabled(false);
        this.mMnc.setEnabled(false);
        this.mApnType.setEnabled(false);
        this.mAuthType.setEnabled(false);
        this.mProtocol.setEnabled(false);
        this.mRoamingProtocol.setEnabled(false);
        this.mCarrierEnabled.setEnabled(false);
        this.mBearerMulti.setEnabled(false);
        this.mMvnoType.setEnabled(false);
        this.mMvnoMatchData.setEnabled(false);
    }

    private void disableFieldsForCarrieridApn() {
        this.mMcc.setEnabled(false);
        this.mMnc.setEnabled(false);
        this.mMvnoType.setEnabled(false);
        this.mMvnoMatchData.setEnabled(false);
    }

    void fillUI(boolean z) {
        String str;
        if (z) {
            this.mName.setText(this.mApnData.getString(1));
            this.mApn.setText(this.mApnData.getString(2));
            this.mProxy.setText(this.mApnData.getString(3));
            this.mPort.setText(this.mApnData.getString(4));
            this.mUser.setText(this.mApnData.getString(5));
            this.mServer.setText(this.mApnData.getString(6));
            this.mPassword.setText(this.mApnData.getString(7));
            this.mMmsProxy.setText(this.mApnData.getString(12));
            this.mMmsPort.setText(this.mApnData.getString(13));
            this.mMmsc.setText(this.mApnData.getString(8));
            this.mMcc.setText(this.mApnData.getString(9));
            this.mMnc.setText(this.mApnData.getString(10));
            this.mApnType.setText(hideApnMatchTypes(this.mApnData.getString(15), "xcap"));
            if (this.mNewApn) {
                SubscriptionInfo accessibleSubscriptionInfo = this.mProxySubscriptionMgr.getAccessibleSubscriptionInfo(this.mSubId);
                String mccString = accessibleSubscriptionInfo == null ? null : accessibleSubscriptionInfo.getMccString();
                String mncString = accessibleSubscriptionInfo == null ? null : accessibleSubscriptionInfo.getMncString();
                if (!TextUtils.isEmpty(mccString) && !TextUtils.isEmpty(mccString)) {
                    this.mMcc.setText(mccString);
                    this.mMnc.setText(mncString);
                    this.mCurMnc = mncString;
                    this.mCurMcc = mccString;
                }
            }
            int intValue = this.mApnData.getInteger(14, -1).intValue();
            if (intValue != -1) {
                this.mAuthType.setValueIndex(intValue);
            } else {
                this.mAuthType.setValue(null);
            }
            this.mProtocol.setValue(this.mApnData.getString(16));
            this.mRoamingProtocol.setValue(this.mApnData.getString(20));
            this.mCarrierEnabled.setChecked(this.mApnData.getInteger(17, 1).intValue() == 1);
            this.mBearerInitialVal = this.mApnData.getInteger(18, 0).intValue();
            HashSet hashSet = new HashSet();
            int intValue2 = this.mApnData.getInteger(19, 0).intValue();
            if (intValue2 != 0) {
                int i = 1;
                while (intValue2 != 0) {
                    if ((intValue2 & 1) == 1) {
                        hashSet.add("" + i);
                    }
                    intValue2 >>= 1;
                    i++;
                }
            } else if (this.mBearerInitialVal == 0) {
                hashSet.add("0");
            }
            if (this.mBearerInitialVal != 0) {
                if (!hashSet.contains("" + this.mBearerInitialVal)) {
                    hashSet.add("" + this.mBearerInitialVal);
                }
            }
            this.mBearerMulti.setValues(hashSet);
            this.mMvnoType.setValue(this.mApnData.getString(21));
            this.mMvnoMatchData.setEnabled(false);
            this.mMvnoMatchData.setText(this.mApnData.getString(22));
            if (this.mNewApn && (str = this.mMvnoTypeStr) != null && this.mMvnoMatchDataStr != null) {
                this.mMvnoType.setValue(str);
                this.mMvnoMatchData.setText(this.mMvnoMatchDataStr);
            }
        }
        EditTextPreference editTextPreference = this.mName;
        editTextPreference.setSummary(checkNull(editTextPreference.getText()));
        EditTextPreference editTextPreference2 = this.mApn;
        editTextPreference2.setSummary(checkNull(editTextPreference2.getText()));
        EditTextPreference editTextPreference3 = this.mProxy;
        editTextPreference3.setSummary(checkNull(editTextPreference3.getText()));
        EditTextPreference editTextPreference4 = this.mPort;
        editTextPreference4.setSummary(checkNull(editTextPreference4.getText()));
        EditTextPreference editTextPreference5 = this.mUser;
        editTextPreference5.setSummary(checkNull(editTextPreference5.getText()));
        EditTextPreference editTextPreference6 = this.mServer;
        editTextPreference6.setSummary(checkNull(editTextPreference6.getText()));
        EditTextPreference editTextPreference7 = this.mPassword;
        editTextPreference7.setSummary(starify(editTextPreference7.getText()));
        EditTextPreference editTextPreference8 = this.mMmsProxy;
        editTextPreference8.setSummary(checkNull(editTextPreference8.getText()));
        EditTextPreference editTextPreference9 = this.mMmsPort;
        editTextPreference9.setSummary(checkNull(editTextPreference9.getText()));
        EditTextPreference editTextPreference10 = this.mMmsc;
        editTextPreference10.setSummary(checkNull(editTextPreference10.getText()));
        EditTextPreference editTextPreference11 = this.mMcc;
        editTextPreference11.setSummary(formatInteger(checkNull(editTextPreference11.getText())));
        EditTextPreference editTextPreference12 = this.mMnc;
        editTextPreference12.setSummary(formatInteger(checkNull(editTextPreference12.getText())));
        EditTextPreference editTextPreference13 = this.mApnType;
        editTextPreference13.setSummary(checkNull(editTextPreference13.getText()));
        String value = this.mAuthType.getValue();
        if (value != null) {
            int parseInt = Integer.parseInt(value);
            this.mAuthType.setValueIndex(parseInt);
            this.mAuthType.setSummary(getResources().getStringArray(R$array.apn_auth_entries)[parseInt]);
        } else {
            this.mAuthType.setSummary(sNotSet);
        }
        ListPreference listPreference = this.mProtocol;
        listPreference.setSummary(checkNull(protocolDescription(listPreference.getValue(), this.mProtocol)));
        ListPreference listPreference2 = this.mRoamingProtocol;
        listPreference2.setSummary(checkNull(protocolDescription(listPreference2.getValue(), this.mRoamingProtocol)));
        MultiSelectListPreference multiSelectListPreference = this.mBearerMulti;
        multiSelectListPreference.setSummary(checkNull(bearerMultiDescription(multiSelectListPreference.getValues())));
        ListPreference listPreference3 = this.mMvnoType;
        listPreference3.setSummary(checkNull(mvnoDescription(listPreference3.getValue())));
        EditTextPreference editTextPreference14 = this.mMvnoMatchData;
        editTextPreference14.setSummary(checkNullforMvnoValue(editTextPreference14.getText()));
        if (getResources().getBoolean(R$bool.config_allow_edit_carrier_enabled)) {
            this.mCarrierEnabled.setEnabled(true);
        } else {
            this.mCarrierEnabled.setEnabled(false);
        }
    }

    private String protocolDescription(String str, ListPreference listPreference) {
        String upperCase = checkNull(str).toUpperCase();
        if (upperCase.equals("IPV4")) {
            upperCase = "IP";
        }
        int findIndexOfValue = listPreference.findIndexOfValue(upperCase);
        if (findIndexOfValue == -1) {
            return null;
        }
        try {
            return getResources().getStringArray(R$array.apn_protocol_entries)[findIndexOfValue];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return null;
        }
    }

    private String bearerMultiDescription(Set<String> set) {
        String[] stringArray = getResources().getStringArray(R$array.bearer_entries);
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (String str : set) {
            int findIndexOfValue = this.mBearerMulti.findIndexOfValue(str);
            if (z) {
                try {
                    sb.append(stringArray[findIndexOfValue]);
                    z = false;
                } catch (ArrayIndexOutOfBoundsException unused) {
                }
            } else {
                sb.append(", " + stringArray[findIndexOfValue]);
            }
        }
        String sb2 = sb.toString();
        if (TextUtils.isEmpty(sb2)) {
            return null;
        }
        return sb2;
    }

    private String mvnoDescription(String str) {
        String[] strArr;
        int findIndexOfValue = this.mMvnoType.findIndexOfValue(str);
        String value = this.mMvnoType.getValue();
        if (findIndexOfValue == -1) {
            return null;
        }
        String[] stringArray = getResources().getStringArray(R$array.mvno_type_entries_ex);
        boolean z = false;
        boolean z2 = this.mReadOnlyApn || ((strArr = this.mReadOnlyApnFields) != null && Arrays.asList(strArr).contains("mvno_match_data"));
        Log.d(TAG, "values[" + findIndexOfValue + "]= " + stringArray[findIndexOfValue]);
        EditTextPreference editTextPreference = this.mMvnoMatchData;
        if (!z2 && findIndexOfValue != 0) {
            z = true;
        }
        editTextPreference.setEnabled(z);
        if (str != null && !str.equals(value)) {
            if (stringArray[findIndexOfValue].equals("SPN")) {
                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
                TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(this.mSubId);
                if (createForSubscriptionId != null) {
                    telephonyManager = createForSubscriptionId;
                }
                this.mMvnoMatchData.setText(telephonyManager.getSimOperatorName());
                this.mMvnoMatchData.setSummary(telephonyManager.getSimOperatorName());
            } else {
                if (stringArray[findIndexOfValue].equals("IMSI")) {
                    SubscriptionInfo accessibleSubscriptionInfo = this.mProxySubscriptionMgr.getAccessibleSubscriptionInfo(this.mSubId);
                    String objects = accessibleSubscriptionInfo == null ? "" : Objects.toString(accessibleSubscriptionInfo.getMccString(), "");
                    String objects2 = accessibleSubscriptionInfo != null ? Objects.toString(accessibleSubscriptionInfo.getMncString(), "") : "";
                    this.mMvnoMatchData.setText(objects + objects2 + "x");
                    this.mMvnoMatchData.setSummary(objects + objects2 + "x");
                } else if (stringArray[findIndexOfValue].equals("GID")) {
                    TelephonyManager telephonyManager2 = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
                    TelephonyManager createForSubscriptionId2 = telephonyManager2.createForSubscriptionId(this.mSubId);
                    if (createForSubscriptionId2 != null) {
                        telephonyManager2 = createForSubscriptionId2;
                    }
                    this.mMvnoMatchData.setText(telephonyManager2.getGroupIdLevel1());
                    this.mMvnoMatchData.setSummary(telephonyManager2.getGroupIdLevel1());
                } else if (stringArray[findIndexOfValue].equals("PNN")) {
                    UniTelephonyManager from = UniTelephonyManager.from(getContext());
                    this.mMvnoMatchData.setText(checkNull(from.getPnnHomeName(this.mSubId)));
                    this.mMvnoMatchData.setSummary(checkNull(from.getPnnHomeName(this.mSubId)));
                } else if (stringArray[findIndexOfValue].equals("None")) {
                    this.mMvnoMatchData.setText("");
                    this.mMvnoMatchData.setSummary(sNotSet);
                } else {
                    this.mMvnoMatchData.setText("");
                }
            }
        }
        try {
            return stringArray[findIndexOfValue];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return null;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("auth_type".equals(key)) {
            try {
                int parseInt = Integer.parseInt((String) obj);
                this.mAuthType.setValueIndex(parseInt);
                this.mAuthType.setSummary(getResources().getStringArray(R$array.apn_auth_entries)[parseInt]);
                return true;
            } catch (NumberFormatException unused) {
                return false;
            }
        } else if ("apn_type".equals(key)) {
            String str = (String) obj;
            if (TextUtils.isEmpty(str) && !ArrayUtils.isEmpty(this.mDefaultApnTypes)) {
                str = getEditableApnType(this.mDefaultApnTypes);
            }
            if (TextUtils.isEmpty(str)) {
                return true;
            }
            this.mApnType.setSummary(str);
            return true;
        } else if ("apn_protocol".equals(key)) {
            String str2 = (String) obj;
            String protocolDescription = protocolDescription(str2, this.mProtocol);
            if (protocolDescription == null) {
                return false;
            }
            this.mProtocol.setSummary(protocolDescription);
            this.mProtocol.setValue(str2);
            return true;
        } else if ("apn_roaming_protocol".equals(key)) {
            String str3 = (String) obj;
            String protocolDescription2 = protocolDescription(str3, this.mRoamingProtocol);
            if (protocolDescription2 == null) {
                return false;
            }
            this.mRoamingProtocol.setSummary(protocolDescription2);
            this.mRoamingProtocol.setValue(str3);
            return true;
        } else if ("bearer_multi".equals(key)) {
            Set<String> set = (Set) obj;
            String bearerMultiDescription = bearerMultiDescription(set);
            if (bearerMultiDescription == null) {
                return false;
            }
            this.mBearerMulti.setValues(set);
            this.mBearerMulti.setSummary(bearerMultiDescription);
            return true;
        } else if ("mvno_type".equals(key)) {
            String str4 = (String) obj;
            String mvnoDescription = mvnoDescription(str4);
            if (mvnoDescription == null) {
                return false;
            }
            this.mMvnoType.setValue(str4);
            this.mMvnoType.setSummary(mvnoDescription);
            EditTextPreference editTextPreference = this.mMvnoMatchData;
            editTextPreference.setSummary(checkNullforMvnoValue(editTextPreference.getText()));
            return true;
        } else if ("apn_password".equals(key)) {
            this.mPassword.setSummary(starify(obj != null ? String.valueOf(obj) : ""));
            return true;
        } else if ("carrier_enabled".equals(key)) {
            return true;
        } else {
            preference.setSummary(checkNull(obj != null ? String.valueOf(obj) : null));
            return true;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (!this.mNewApn && !this.mReadOnlyApn) {
            this.mDeleteItem = menu.add(0, 1, 0, R$string.menu_delete).setIcon(R$drawable.ic_delete);
        }
        if (!this.mReadOnlyApn) {
            this.mSaveItem = menu.add(0, 2, 0, R$string.menu_save).setIcon(17301582);
        }
        this.mCancelItem = menu.add(0, 3, 0, R$string.menu_cancel).setIcon(17301560);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            deleteApn();
            finish();
            return true;
        } else if (itemId == 2) {
            if (validateAndSaveApnData()) {
                finish();
            }
            return true;
        } else if (itemId == 3) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && i == 4) {
            if (validateAndSaveApnData()) {
                finish();
                return true;
            }
            return true;
        }
        return false;
    }

    boolean setStringValueAndCheckIfDiff(ContentValues contentValues, String str, String str2, boolean z, int i) {
        String string = this.mApnData.getString(i);
        boolean z2 = z || (!(TextUtils.isEmpty(str2) && TextUtils.isEmpty(string)) && (str2 == null || !str2.equals(string)));
        if (z2 && str2 != null) {
            contentValues.put(str, str2);
        }
        return z2;
    }

    boolean setIntValueAndCheckIfDiff(ContentValues contentValues, String str, int i, boolean z, int i2) {
        boolean z2 = z || i != this.mApnData.getInteger(i2).intValue();
        if (z2) {
            contentValues.put(str, Integer.valueOf(i));
        }
        return z2;
    }

    boolean validateAndSaveApnData() {
        int i;
        int i2;
        if (this.mReadOnlyApn) {
            return true;
        }
        String checkNotSet = checkNotSet(this.mName.getText());
        String checkNotSet2 = checkNotSet(this.mApn.getText());
        String checkNotSet3 = checkNotSet(this.mMcc.getText());
        String checkNotSet4 = checkNotSet(this.mMnc.getText());
        if (validateApnData() != null) {
            showError();
            return false;
        }
        ContentValues contentValues = new ContentValues();
        boolean stringValueAndCheckIfDiff = setStringValueAndCheckIfDiff(contentValues, "mmsc", checkNotSet(this.mMmsc.getText()), setStringValueAndCheckIfDiff(contentValues, "password", checkNotSet(this.mPassword.getText()), setStringValueAndCheckIfDiff(contentValues, "server", checkNotSet(this.mServer.getText()), setStringValueAndCheckIfDiff(contentValues, "user", checkNotSet(this.mUser.getText()), setStringValueAndCheckIfDiff(contentValues, "mmsport", checkNotSet(this.mMmsPort.getText()), setStringValueAndCheckIfDiff(contentValues, "mmsproxy", checkNotSet(this.mMmsProxy.getText()), setStringValueAndCheckIfDiff(contentValues, "port", checkNotSet(this.mPort.getText()), setStringValueAndCheckIfDiff(contentValues, "proxy", checkNotSet(this.mProxy.getText()), setStringValueAndCheckIfDiff(contentValues, "apn", checkNotSet2, setStringValueAndCheckIfDiff(contentValues, "name", checkNotSet, this.mNewApn, 1), 2), 3), 4), 12), 13), 5), 6), 7), 8);
        String value = this.mAuthType.getValue();
        if (value != null) {
            stringValueAndCheckIfDiff = setIntValueAndCheckIfDiff(contentValues, "authtype", Integer.parseInt(value), stringValueAndCheckIfDiff, 14);
        }
        boolean stringValueAndCheckIfDiff2 = setStringValueAndCheckIfDiff(contentValues, "mnc", checkNotSet4, setStringValueAndCheckIfDiff(contentValues, "mcc", checkNotSet3, setStringValueAndCheckIfDiff(contentValues, "type", checkNotSet(getUserEnteredApnType()), setStringValueAndCheckIfDiff(contentValues, "roaming_protocol", checkNotSet(this.mRoamingProtocol.getValue()), setStringValueAndCheckIfDiff(contentValues, "protocol", checkNotSet(this.mProtocol.getValue()), stringValueAndCheckIfDiff, 16), 20), 15), 9), 10);
        contentValues.put("numeric", checkNotSet3 + checkNotSet4);
        String str = this.mCurMnc;
        if (str != null && this.mCurMcc != null && str.equals(checkNotSet4) && this.mCurMcc.equals(checkNotSet3)) {
            contentValues.put("current", (Integer) 1);
        }
        Iterator<String> it = this.mBearerMulti.getValues().iterator();
        int i3 = 0;
        while (true) {
            if (!it.hasNext()) {
                i = i3;
                break;
            }
            String next = it.next();
            if (Integer.parseInt(next) == 0) {
                i = 0;
                break;
            }
            i3 |= getBitmaskForTech(Integer.parseInt(next));
        }
        boolean intValueAndCheckIfDiff = setIntValueAndCheckIfDiff(contentValues, "carrier_enabled", this.mCarrierEnabled.isChecked() ? 1 : 0, setStringValueAndCheckIfDiff(contentValues, "mvno_match_data", checkNotSet(this.mMvnoMatchData.getText()), setStringValueAndCheckIfDiff(contentValues, "mvno_type", checkNotSet(this.mMvnoType.getValue()), setIntValueAndCheckIfDiff(contentValues, "bearer", (i == 0 || (i2 = this.mBearerInitialVal) == 0 || !bitmaskHasTech(i, i2)) ? 0 : this.mBearerInitialVal, setIntValueAndCheckIfDiff(contentValues, "bearer_bitmask", i, stringValueAndCheckIfDiff2, 19), 18), 21), 22), 17);
        contentValues.put("edited", (Integer) 1);
        if (intValueAndCheckIfDiff) {
            updateApnDataToDatabase(this.mApnData.getUri() == null ? this.mCarrierUri : this.mApnData.getUri(), contentValues);
        }
        return true;
    }

    private void updateApnDataToDatabase(final Uri uri, final ContentValues contentValues) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.apn.ApnEditor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ApnEditor.this.lambda$updateApnDataToDatabase$0(uri, contentValues);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateApnDataToDatabase$0(Uri uri, ContentValues contentValues) {
        if (uri.equals(this.mCarrierUri)) {
            if (getContentResolver().insert(this.mCarrierUri, contentValues) == null) {
                String str = TAG;
                Log.e(str, "Can't add a new apn to database " + this.mCarrierUri);
                return;
            }
            return;
        }
        getContentResolver().update(uri, contentValues, null, null);
    }

    String validateApnData() {
        String string;
        String[] strArr;
        String checkNotSet = checkNotSet(this.mName.getText());
        String checkNotSet2 = checkNotSet(this.mApn.getText());
        String checkNotSet3 = checkNotSet(this.mMcc.getText());
        String checkNotSet4 = checkNotSet(this.mMnc.getText());
        boolean z = this.mIsCarrierIdApn && TextUtils.isEmpty(checkNotSet3) && TextUtils.isEmpty(checkNotSet4);
        if (TextUtils.isEmpty(checkNotSet)) {
            string = getResources().getString(R$string.error_name_empty);
        } else if (!TextUtils.isEmpty(checkNotSet2) || this.mAllowEmptyApn) {
            if (z) {
                Log.d(TAG, "validateApnData: carrier id APN does not have mcc/mnc defined");
            } else if (checkNotSet3 == null || checkNotSet3.length() != 3) {
                string = getResources().getString(R$string.error_mcc_not3);
            } else if (checkNotSet4 == null || (checkNotSet4.length() & 65534) != 2) {
                string = getResources().getString(R$string.error_mnc_not23);
            }
            string = null;
        } else {
            string = getResources().getString(R$string.error_apn_empty);
        }
        if (string == null && !ArrayUtils.isEmpty(this.mReadOnlyApnTypes) && apnTypesMatch(this.mReadOnlyApnTypes, getUserEnteredApnType())) {
            StringBuilder sb = new StringBuilder();
            for (String str : this.mReadOnlyApnTypes) {
                sb.append(str);
                sb.append(", ");
                Log.d(TAG, "validateApnData: appending type: " + str);
            }
            if (sb.length() >= 2) {
                sb.delete(sb.length() - 2, sb.length());
            }
            return String.format(getResources().getString(R$string.error_adding_apn_type), sb);
        }
        return string;
    }

    void showError() {
        ErrorDialog.showError(this);
    }

    private void deleteApn() {
        if (this.mApnData.getUri() != null) {
            getContentResolver().delete(this.mApnData.getUri(), null, null);
            this.mApnData = new ApnData(sProjection.length);
        }
    }

    private String starify(String str) {
        if (str == null || str.length() == 0) {
            return sNotSet;
        }
        int length = str.length();
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            cArr[i] = '*';
        }
        return new String(cArr);
    }

    private String checkNull(String str) {
        return TextUtils.isEmpty(str) ? sNotSet : str;
    }

    private String checkNullforMvnoValue(String str) {
        return TextUtils.isEmpty(str) ? getResources().getString(R$string.apn_not_set_for_mvno) : str;
    }

    private String checkNotSet(String str) {
        if (sNotSet.equals(str)) {
            return null;
        }
        return str;
    }

    String getUserEnteredApnType() {
        String text = this.mApnType.getText();
        if (text != null) {
            text = text.trim();
        }
        if (TextUtils.isEmpty(text) || "*".equals(text)) {
            text = getEditableApnType(APN_TYPES);
        }
        String str = TAG;
        Log.d(str, "getUserEnteredApnType: changed apn type to editable apn types: " + text);
        return text;
    }

    private String getEditableApnType(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        List asList = Arrays.asList(this.mReadOnlyApnTypes);
        boolean z = true;
        for (String str : strArr) {
            if (!asList.contains(str) && !str.equals("ia") && !str.equals("emergency") && !str.equals("mcx") && !str.equals("ims")) {
                if (z) {
                    z = false;
                } else {
                    sb.append(",");
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }

    private void initApnEditorUi() {
        addPreferencesFromResource(R$xml.apn_editor);
        sNotSet = getResources().getString(R$string.apn_not_set);
        this.mName = (EditTextPreference) findPreference("apn_name");
        this.mApn = (EditTextPreference) findPreference("apn_apn");
        this.mProxy = (EditTextPreference) findPreference("apn_http_proxy");
        this.mPort = (EditTextPreference) findPreference("apn_http_port");
        this.mUser = (EditTextPreference) findPreference("apn_user");
        this.mServer = (EditTextPreference) findPreference("apn_server");
        this.mPassword = (EditTextPreference) findPreference("apn_password");
        this.mMmsProxy = (EditTextPreference) findPreference("apn_mms_proxy");
        this.mMmsPort = (EditTextPreference) findPreference("apn_mms_port");
        this.mMmsc = (EditTextPreference) findPreference("apn_mmsc");
        this.mMcc = (EditTextPreference) findPreference("apn_mcc");
        this.mMnc = (EditTextPreference) findPreference("apn_mnc");
        this.mApnType = (EditTextPreference) findPreference("apn_type");
        this.mAuthType = (ListPreference) findPreference("auth_type");
        this.mProtocol = (ListPreference) findPreference("apn_protocol");
        this.mRoamingProtocol = (ListPreference) findPreference("apn_roaming_protocol");
        this.mCarrierEnabled = (SwitchPreference) findPreference("carrier_enabled");
        this.mBearerMulti = (MultiSelectListPreference) findPreference("bearer_multi");
        this.mMvnoType = (ListPreference) findPreference("mvno_type");
        this.mMvnoMatchData = (EditTextPreference) findPreference("mvno_match_data");
    }

    private void getCarrierCustomizedConfig() {
        PersistableBundle configForSubId;
        this.mReadOnlyApn = false;
        this.mReadOnlyApnTypes = null;
        this.mReadOnlyApnFields = null;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) getSystemService("carrier_config");
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mSubId)) == null) {
            return;
        }
        String[] stringArray = configForSubId.getStringArray("read_only_apn_types_string_array");
        this.mReadOnlyApnTypes = stringArray;
        if (!ArrayUtils.isEmpty(stringArray)) {
            String str = TAG;
            Log.d(str, "onCreate: read only APN type: " + Arrays.toString(this.mReadOnlyApnTypes));
        }
        this.mReadOnlyApnFields = configForSubId.getStringArray("read_only_apn_fields_string_array");
        String[] stringArray2 = configForSubId.getStringArray("apn_settings_default_apn_types_string_array");
        this.mDefaultApnTypes = stringArray2;
        if (!ArrayUtils.isEmpty(stringArray2)) {
            String str2 = TAG;
            Log.d(str2, "onCreate: default apn types: " + Arrays.toString(this.mDefaultApnTypes));
        }
        String string = configForSubId.getString("apn.settings_default_protocol_string");
        this.mDefaultApnProtocol = string;
        if (!TextUtils.isEmpty(string)) {
            String str3 = TAG;
            Log.d(str3, "onCreate: default apn protocol: " + this.mDefaultApnProtocol);
        }
        String string2 = configForSubId.getString("apn.settings_default_roaming_protocol_string");
        this.mDefaultApnRoamingProtocol = string2;
        if (TextUtils.isEmpty(string2)) {
            return;
        }
        String str4 = TAG;
        Log.d(str4, "onCreate: default apn roaming protocol: " + this.mDefaultApnRoamingProtocol);
    }

    private void setCarrierCustomizedConfigToUi() {
        if (TextUtils.isEmpty(this.mApnType.getText()) && !ArrayUtils.isEmpty(this.mDefaultApnTypes)) {
            String editableApnType = getEditableApnType(this.mDefaultApnTypes);
            this.mApnType.setText(editableApnType);
            this.mApnType.setSummary(editableApnType);
        }
        String protocolDescription = protocolDescription(this.mDefaultApnProtocol, this.mProtocol);
        if (TextUtils.isEmpty(this.mProtocol.getValue()) && !TextUtils.isEmpty(protocolDescription)) {
            this.mProtocol.setValue(this.mDefaultApnProtocol);
            this.mProtocol.setSummary(protocolDescription);
        }
        String protocolDescription2 = protocolDescription(this.mDefaultApnRoamingProtocol, this.mRoamingProtocol);
        if (!TextUtils.isEmpty(this.mRoamingProtocol.getValue()) || TextUtils.isEmpty(protocolDescription2)) {
            return;
        }
        this.mRoamingProtocol.setValue(this.mDefaultApnRoamingProtocol);
        this.mRoamingProtocol.setSummary(protocolDescription2);
    }

    /* loaded from: classes.dex */
    public static class ErrorDialog extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 530;
        }

        public static void showError(ApnEditor apnEditor) {
            if (apnEditor == null || apnEditor.getFragmentManager() == null) {
                Log.d(ApnEditor.TAG, "showError: editor || getFragmentManager is null");
                return;
            }
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.setTargetFragment(apnEditor, 0);
            errorDialog.show(apnEditor.getFragmentManager(), "error");
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            return new AlertDialog.Builder(getContext()).setTitle(R$string.error_title).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setMessage(((ApnEditor) getTargetFragment()).validateApnData()).create();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0029  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    com.android.settings.network.apn.ApnEditor.ApnData getApnDataFromUri(android.net.Uri r7) {
        /*
            r6 = this;
            android.content.ContentResolver r0 = r6.getContentResolver()
            java.lang.String[] r2 = com.android.settings.network.apn.ApnEditor.sProjection
            r3 = 0
            r4 = 0
            r5 = 0
            r1 = r7
            android.database.Cursor r6 = r0.query(r1, r2, r3, r4, r5)
            if (r6 == 0) goto L26
            boolean r0 = r6.moveToFirst()     // Catch: java.lang.Throwable -> L1c
            if (r0 == 0) goto L26
            com.android.settings.network.apn.ApnEditor$ApnData r0 = new com.android.settings.network.apn.ApnEditor$ApnData     // Catch: java.lang.Throwable -> L1c
            r0.<init>(r7, r6)     // Catch: java.lang.Throwable -> L1c
            goto L27
        L1c:
            r7 = move-exception
            r6.close()     // Catch: java.lang.Throwable -> L21
            goto L25
        L21:
            r6 = move-exception
            r7.addSuppressed(r6)
        L25:
            throw r7
        L26:
            r0 = 0
        L27:
            if (r6 == 0) goto L2c
            r6.close()
        L2c:
            if (r0 != 0) goto L44
            java.lang.String r6 = com.android.settings.network.apn.ApnEditor.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Can't get apnData from Uri "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r7 = r1.toString()
            android.util.Log.d(r6, r7)
        L44:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.apn.ApnEditor.getApnDataFromUri(android.net.Uri):com.android.settings.network.apn.ApnEditor$ApnData");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ApnData {
        Object[] mData;
        Uri mUri;

        ApnData(int i) {
            this.mData = new Object[i];
        }

        ApnData(Uri uri, Cursor cursor) {
            this.mUri = uri;
            this.mData = new Object[cursor.getColumnCount()];
            for (int i = 0; i < this.mData.length; i++) {
                int type = cursor.getType(i);
                if (type == 1) {
                    this.mData[i] = Integer.valueOf(cursor.getInt(i));
                } else if (type == 2) {
                    this.mData[i] = Float.valueOf(cursor.getFloat(i));
                } else if (type == 3) {
                    this.mData[i] = cursor.getString(i);
                } else if (type == 4) {
                    this.mData[i] = cursor.getBlob(i);
                } else {
                    this.mData[i] = null;
                }
            }
        }

        Uri getUri() {
            return this.mUri;
        }

        Integer getInteger(int i) {
            return (Integer) this.mData[i];
        }

        Integer getInteger(int i, Integer num) {
            Integer integer = getInteger(i);
            return integer == null ? num : integer;
        }

        String getString(int i) {
            return (String) this.mData[i];
        }
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onPhoneStateChanged() {
        Log.d(TAG, "onPhoneStateChanged");
        if (getPreferenceScreen() == null || this.mDeleteItem == null || this.mSaveItem == null || this.mCancelItem == null) {
            return;
        }
        getPreferenceScreen().setEnabled(!isPhoneInCall());
        this.mDeleteItem.setEnabled(!isPhoneInCall());
        this.mSaveItem.setEnabled(!isPhoneInCall());
        this.mCancelItem.setEnabled(!isPhoneInCall());
    }

    private boolean isPhoneInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) getContext().getSystemService(AirplaneModePreferenceController.TELECOM_SERVICE);
    }
}
