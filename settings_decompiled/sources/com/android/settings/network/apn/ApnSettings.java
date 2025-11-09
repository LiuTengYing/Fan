package com.android.settings.network.apn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UniCarrierConfigManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.BroadcastReceiverChanged;
import com.android.settingslib.RestrictedLockUtils;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ApnSettings extends RestrictedSettingsFragment implements Preference.OnPreferenceChangeListener, BroadcastReceiverChanged.BroadcastReceiverChangedClient {
    private static final String[] CARRIERS_PROJECTION = {"_id", "name", "apn", "type", "mvno_type", "mvno_match_data", "edited"};
    private static final Uri DEFAULTAPN_URI = Uri.parse("content://telephony/carriers/restore");
    private static final Uri PREFERAPN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private boolean mAllowAddingApns;
    private BroadcastReceiverChanged mBroadcastReceiverChanged;
    private ProgressDialog mDialog;
    private String[] mHideApnTypes;
    private boolean mHideImsApn;
    private boolean mHidePresetApnDetails;
    private IntentFilter mIntentFilter;
    private String mMvnoMatchData;
    private String mMvnoType;
    private NotificationManager mNotificationManager;
    private boolean mPayStateSupport;
    private int mPhoneId;
    private final PhoneStateListener mPhoneStateListener;
    private final BroadcastReceiver mReceiver;
    private RestoreApnProcessHandler mRestoreApnProcessHandler;
    private RestoreApnUiHandler mRestoreApnUiHandler;
    private boolean mRestoreDefaultApnMode;
    private HandlerThread mRestoreDefaultApnThread;
    private MenuItem mRestoreItem;
    private String mSelectedKey;
    private int mSubId;
    private SubscriptionInfo mSubscriptionInfo;
    private TelephonyManager mTelephonyManager;
    private boolean mUnavailable;
    private UserManager mUserManager;
    private Dialog mWarningDialog;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i == 1001 ? 579 : 0;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 12;
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onCarrierConfigChanged(int i) {
    }

    public ApnSettings() {
        super("no_config_mobile_networks");
        this.mPhoneStateListener = new PhoneStateListener() { // from class: com.android.settings.network.apn.ApnSettings.1
            @Override // android.telephony.PhoneStateListener
            public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
                if (preciseDataConnectionState.getState() == 2) {
                    if (!ApnSettings.this.mRestoreDefaultApnMode) {
                        ApnSettings.this.fillList();
                    } else if (ApnSettings.this.mDialog == null || !ApnSettings.this.mDialog.isShowing()) {
                        ApnSettings.this.showDialog(1001);
                    }
                }
                if (ApnSettings.this.mPayStateSupport && ApnSettings.this.mTelephonyManager.getSimState() == 1) {
                    ApnSettings.this.updateNotification(((PhoneStateListener) this).mSubId.intValue(), false);
                }
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.apn.ApnSettings.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction()) && intent.getStringExtra("ss").equals("ABSENT")) {
                    SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
                    if (subscriptionManager == null || subscriptionManager.isActiveSubscriptionId(ApnSettings.this.mSubId)) {
                        return;
                    }
                    Log.d("ApnSettings", "Due to SIM absent, closes APN settings page");
                    ApnSettings.this.finish();
                } else if (!intent.getAction().equals("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED") || ApnSettings.this.mRestoreDefaultApnMode) {
                } else {
                    int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_ID", -1);
                    if (SubscriptionManager.isValidSubscriptionId(intExtra) && ApnSettings.this.mPhoneId == SubscriptionUtil.getPhoneId(context, intExtra) && intExtra != ApnSettings.this.mSubId) {
                        ApnSettings.this.mSubId = intExtra;
                        ApnSettings apnSettings = ApnSettings.this;
                        apnSettings.mSubscriptionInfo = apnSettings.getSubscriptionInfo(apnSettings.mSubId);
                        ApnSettings apnSettings2 = ApnSettings.this;
                        apnSettings2.restartPhoneStateListener(apnSettings2.mSubId);
                    }
                    ApnSettings.this.fillList();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartPhoneStateListener(int i) {
        if (this.mRestoreDefaultApnMode) {
            return;
        }
        TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(i);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        this.mTelephonyManager = createForSubscriptionId;
        createForSubscriptionId.listen(this.mPhoneStateListener, 4096);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mSubId = activity.getIntent().getIntExtra("sub_id", -1);
        Log.d("ApnSettings", "mSubId = " + this.mSubId);
        this.mPhoneId = SubscriptionUtil.getPhoneId(activity, this.mSubId);
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.telephony.action.SUBSCRIPTION_CARRIER_IDENTITY_CHANGED");
        this.mIntentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        setIfOnlyAvailableForAdmins(true);
        this.mSubscriptionInfo = getSubscriptionInfo(this.mSubId);
        this.mTelephonyManager = (TelephonyManager) activity.getSystemService(TelephonyManager.class);
        PersistableBundle configForSubId = ((CarrierConfigManager) getSystemService("carrier_config")).getConfigForSubId(this.mSubId);
        this.mHideImsApn = configForSubId.getBoolean("hide_ims_apn_bool");
        this.mAllowAddingApns = configForSubId.getBoolean("allow_adding_apns_bool");
        PersistableBundle configForSubId2 = new UniCarrierConfigManager(getContext()).getConfigForSubId(this.mSubId);
        if (configForSubId2 != null) {
            this.mHideApnTypes = configForSubId2.getStringArray("hide_apn_types_string_array");
        }
        if (this.mAllowAddingApns && ApnEditor.hasAllApns(configForSubId.getStringArray("read_only_apn_types_string_array"))) {
            Log.d("ApnSettings", "not allowing adding APN because all APN types are read only");
            this.mAllowAddingApns = false;
        }
        this.mHidePresetApnDetails = configForSubId.getBoolean("hide_preset_apn_details_bool");
        this.mUserManager = UserManager.get(activity);
        BroadcastReceiverChanged broadcastReceiverChanged = new BroadcastReceiverChanged(getContext(), this);
        this.mBroadcastReceiverChanged = broadcastReceiverChanged;
        broadcastReceiverChanged.start();
        this.mPayStateSupport = SubscriptionManager.getResourcesForSubId(getContext(), this.mSubId).getBoolean(134414340);
        this.mNotificationManager = (NotificationManager) activity.getSystemService("notification");
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getEmptyTextView().setText(R$string.apn_settings_not_available);
        boolean isUiRestricted = isUiRestricted();
        this.mUnavailable = isUiRestricted;
        setHasOptionsMenu(!isUiRestricted);
        if (this.mUnavailable) {
            addPreferencesFromResource(R$xml.placeholder_prefs);
        } else {
            addPreferencesFromResource(R$xml.apn_settings);
        }
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUnavailable) {
            return;
        }
        getActivity().registerReceiver(this.mReceiver, this.mIntentFilter, 2);
        restartPhoneStateListener(this.mSubId);
        ProgressDialog progressDialog = this.mDialog;
        if (progressDialog != null && progressDialog.isShowing() && !this.mRestoreDefaultApnMode) {
            removeDialog(1001);
            this.mDialog = null;
        }
        if (this.mRestoreDefaultApnMode) {
            return;
        }
        fillList();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mUnavailable) {
            return;
        }
        getActivity().unregisterReceiver(this.mReceiver);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        HandlerThread handlerThread = this.mRestoreDefaultApnThread;
        if (handlerThread != null) {
            handlerThread.quit();
            this.mRestoreDefaultApnMode = false;
        }
        Dialog dialog = this.mWarningDialog;
        if (dialog != null) {
            if (dialog.isShowing()) {
                this.mWarningDialog.dismiss();
            }
            this.mWarningDialog = null;
        }
        this.mBroadcastReceiverChanged.stop();
    }

    @Override // com.android.settings.RestrictedSettingsFragment
    public RestrictedLockUtils.EnforcedAdmin getRestrictionEnforcedAdmin() {
        UserHandle of = UserHandle.of(this.mUserManager.getProcessUserId());
        if (!this.mUserManager.hasUserRestriction("no_config_mobile_networks", of) || this.mUserManager.hasBaseUserRestriction("no_config_mobile_networks", of)) {
            return null;
        }
        return RestrictedLockUtils.EnforcedAdmin.MULTIPLE_ENFORCED_ADMIN;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubscriptionInfo getSubscriptionInfo(int i) {
        return SubscriptionManager.from(getActivity()).getActiveSubscriptionInfo(i);
    }

    private boolean allApnTypesMatch(String[] strArr, String str) {
        if (ArrayUtils.isEmpty(strArr)) {
            return false;
        }
        if (!ApnEditor.hasAllApns(strArr) && !TextUtils.isEmpty(str)) {
            List asList = Arrays.asList(strArr);
            for (String str2 : str.split(",")) {
                if (!asList.contains(str2.trim())) {
                    Log.d("ApnSettings", "config file does not contains apn: " + str2.trim());
                    return false;
                }
            }
            Log.d("ApnSettings", "allApnTypesMatch: true");
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fillList() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        int subscriptionId = subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1;
        Uri withAppendedPath = Uri.withAppendedPath(Telephony.Carriers.SIM_APN_URI, String.valueOf(subscriptionId));
        StringBuilder sb = new StringBuilder("NOT (type='ia' AND (apn=\"\" OR apn IS NULL)) AND user_visible!=0");
        sb.append(" AND NOT (type='emergency')");
        if (!UniTelephonyManager.isDeviceSupportLte() || this.mHideImsApn) {
            sb.append(" AND NOT (type='ims')");
        }
        Cursor query = getContentResolver().query(withAppendedPath, CARRIERS_PROJECTION, sb.toString(), null, "_id");
        if (query != null) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("apn_list");
            preferenceGroup.removeAll();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            this.mSelectedKey = getSelectedApnKey();
            query.moveToFirst();
            while (!query.isAfterLast()) {
                boolean z = true;
                String string = query.getString(1);
                String string2 = query.getString(2);
                String string3 = query.getString(0);
                String string4 = query.getString(3);
                int i = query.getInt(6);
                this.mMvnoType = query.getString(4);
                this.mMvnoMatchData = query.getString(5);
                if (!allApnTypesMatch(this.mHideApnTypes, string4)) {
                    ApnPreference apnPreference = new ApnPreference(getPrefContext());
                    apnPreference.setKey(string3);
                    apnPreference.setTitle(string);
                    apnPreference.setPersistent(false);
                    apnPreference.setOnPreferenceChangeListener(this);
                    apnPreference.setSubId(subscriptionId);
                    if (this.mPayStateSupport && this.mSelectedKey == null) {
                        Log.d("ApnSettings", "fillList: mSelectKey is null, clear checked");
                        apnPreference.clearChecked();
                    }
                    if (this.mHidePresetApnDetails && i == 0) {
                        apnPreference.setHideDetails();
                    } else {
                        apnPreference.setSummary(string2);
                    }
                    if (string4 != null && !string4.contains("default") && !string4.equals("*") && !string4.isEmpty()) {
                        z = false;
                    }
                    apnPreference.setSelectable(z);
                    if (z) {
                        String str = this.mSelectedKey;
                        if (str != null && str.equals(string3)) {
                            apnPreference.setChecked();
                        }
                        arrayList.add(apnPreference);
                    } else {
                        arrayList2.add(apnPreference);
                    }
                }
                query.moveToNext();
            }
            query.close();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Preference preference = (Preference) it.next();
                ApnPreference apnPreference2 = (ApnPreference) preference;
                Log.d("ApnSettings", "fillList: mPayStateSupport = " + this.mPayStateSupport);
                if (this.mSelectedKey == null && apnPreference2.getSelectable() && !this.mPayStateSupport) {
                    apnPreference2.setChecked();
                    setSelectedApnKey(apnPreference2.getKey());
                }
                preferenceGroup.addPreference(preference);
            }
            Iterator it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                preferenceGroup.addPreference((Preference) it2.next());
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!this.mUnavailable) {
            if (this.mAllowAddingApns) {
                menu.add(0, 1, 0, getResources().getString(R$string.menu_new)).setIcon(R$drawable.ic_add_24dp).setShowAsAction(1);
            }
            this.mRestoreItem = menu.add(0, 2, 0, getResources().getString(R$string.menu_restore)).setIcon(17301589);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
        updateState();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            if (isPhoneInCall()) {
                Toast.makeText(getActivity(), getResources().getString(R$string.inCall_status_forbidden), 1).show();
            } else {
                addNewApn();
            }
            return true;
        } else if (itemId == 2) {
            if (getActivity() != null) {
                Dialog dialog = this.mWarningDialog;
                if (dialog != null && dialog.isShowing()) {
                    return super.onOptionsItemSelected(menuItem);
                }
                AlertDialog create = new AlertDialog.Builder(getActivity()).setMessage(R$string.dialog_restore_default_apn).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.apn.ApnSettings.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ApnSettings.this.restoreDefaultApn();
                    }
                }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setCancelable(true).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.network.apn.ApnSettings.3
                    @Override // android.content.DialogInterface.OnDismissListener
                    public void onDismiss(DialogInterface dialogInterface) {
                    }
                }).create();
                this.mWarningDialog = create;
                create.show();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    private void addNewApn() {
        Intent intent = new Intent("android.intent.action.INSERT", Telephony.Carriers.CONTENT_URI);
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        intent.putExtra("sub_id", subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1);
        intent.addFlags(1);
        if (!TextUtils.isEmpty(this.mMvnoType) && !TextUtils.isEmpty(this.mMvnoMatchData)) {
            intent.putExtra("mvno_type", this.mMvnoType);
            intent.putExtra("mvno_match_data", this.mMvnoMatchData);
        }
        startActivity(intent);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Log.d("ApnSettings", "onPreferenceChange(): Preference - " + preference + ", newValue - " + obj + ", newValue type - " + obj.getClass());
        if (obj instanceof String) {
            setSelectedApnKey((String) obj);
            if (this.mPayStateSupport) {
                Log.d("ApnSettings", "onPreferenceChange(): apnPreference.setChecked");
                if (preference instanceof ApnPreference) {
                    ((ApnPreference) preference).setChecked();
                }
                updateNotification(this.mSubId, false);
                return true;
            }
            return true;
        }
        return true;
    }

    private void setSelectedApnKey(String str) {
        this.mSelectedKey = str;
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("apn_id", this.mSelectedKey);
        contentResolver.update(getUriForCurrSubId(PREFERAPN_URI), contentValues, null, null);
    }

    private String getSelectedApnKey() {
        String str;
        Cursor query = getContentResolver().query(getUriForCurrSubId(PREFERAPN_URI), new String[]{"_id"}, null, null, "name ASC");
        if (query.getCount() > 0) {
            query.moveToFirst();
            str = query.getString(0);
        } else {
            str = null;
        }
        query.close();
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean restoreDefaultApn() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        showDialog(1001);
        this.mRestoreDefaultApnMode = true;
        if (this.mRestoreApnUiHandler == null) {
            this.mRestoreApnUiHandler = new RestoreApnUiHandler();
        }
        if (this.mRestoreApnProcessHandler == null || this.mRestoreDefaultApnThread == null) {
            HandlerThread handlerThread = new HandlerThread("Restore default APN Handler: Process Thread");
            this.mRestoreDefaultApnThread = handlerThread;
            handlerThread.start();
            this.mRestoreApnProcessHandler = new RestoreApnProcessHandler(this.mRestoreDefaultApnThread.getLooper(), this.mRestoreApnUiHandler);
        }
        this.mRestoreApnProcessHandler.sendEmptyMessage(1);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri getUriForCurrSubId(Uri uri) {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        int subscriptionId = subscriptionInfo != null ? subscriptionInfo.getSubscriptionId() : -1;
        if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            return Uri.withAppendedPath(uri, "subId/" + String.valueOf(subscriptionId));
        }
        return uri;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RestoreApnUiHandler extends Handler {
        private RestoreApnUiHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 2) {
                return;
            }
            FragmentActivity activity = ApnSettings.this.getActivity();
            if (activity == null) {
                ApnSettings.this.mRestoreDefaultApnMode = false;
                return;
            }
            ApnSettings.this.fillList();
            ApnSettings.this.getPreferenceScreen().setEnabled(true);
            ApnSettings.this.mRestoreDefaultApnMode = false;
            ApnSettings.this.removeDialog(1001);
            Toast.makeText(activity, ApnSettings.this.getResources().getString(R$string.restore_default_apn_completed), 1).show();
            if (ApnSettings.this.mPayStateSupport) {
                Log.d("ApnSettings", "RestoreApnUiHandler: Restore refresh");
                ApnSettings apnSettings = ApnSettings.this;
                apnSettings.updateNotification(apnSettings.mSubId, true);
            }
            ApnSettings apnSettings2 = ApnSettings.this;
            apnSettings2.restartPhoneStateListener(apnSettings2.mSubId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotification(int i, boolean z) {
        NotificationManager notificationManager;
        if (!SubscriptionManager.isValidSubscriptionId(i) || (notificationManager = this.mNotificationManager) == null) {
            Log.d("ApnSettings", "updateNotification: subId is invalid or mNotificationManager is null.");
        } else if (z) {
            String string = SubscriptionManager.getResourcesForSubId(getContext(), i).getString(135004248);
            String string2 = SubscriptionManager.getResourcesForSubId(getContext(), i).getString(135004247);
            Intent intent = new Intent("android.settings.APN_SETTINGS");
            intent.setFlags(268468224);
            intent.putExtra(":settings:show_fragment_as_subsetting", true);
            intent.putExtra("sub_id", i);
            intent.setClassName("com.android.settings", "com.android.settings.Settings$ApnSettingsActivity");
            PendingIntent activity = PendingIntent.getActivity(getContext(), 0, intent, 201326592);
            String valueOf = String.valueOf(i);
            this.mNotificationManager.createNotificationChannel(new NotificationChannel(valueOf, String.valueOf(i), 5));
            Notification build = new Notification.Builder(getContext(), valueOf).setSmallIcon(17301642).setContentTitle(string).setContentText(string2).setContentIntent(activity).setPriority(2).setVisibility(1).build();
            build.flags = 2 | build.flags;
            NotificationManager notificationManager2 = this.mNotificationManager;
            notificationManager2.notifyAsUser("Notification:" + String.valueOf(i), i, build, UserHandle.ALL);
        } else {
            notificationManager.cancelAsUser("Notification:" + String.valueOf(i), i, UserHandle.ALL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RestoreApnProcessHandler extends Handler {
        private Handler mRestoreApnUiHandler;

        RestoreApnProcessHandler(Looper looper, Handler handler) {
            super(looper);
            this.mRestoreApnUiHandler = handler;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ContentResolver contentResolver;
            if (message.what == 1 && (contentResolver = ApnSettings.this.getContentResolver()) != null) {
                contentResolver.delete(ApnSettings.this.getUriForCurrSubId(ApnSettings.DEFAULTAPN_URI), null, null);
                this.mRestoreApnUiHandler.sendEmptyMessage(2);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1001) {
            ProgressDialog progressDialog = new ProgressDialog(getActivity()) { // from class: com.android.settings.network.apn.ApnSettings.5
                @Override // android.app.Dialog
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return true;
                }
            };
            this.mDialog = progressDialog;
            progressDialog.setMessage(getResources().getString(R$string.restore_default_apn));
            this.mDialog.setCancelable(false);
            return this.mDialog;
        }
        return null;
    }

    @Override // com.android.settings.network.telephony.BroadcastReceiverChanged.BroadcastReceiverChangedClient
    public void onPhoneStateChanged() {
        Log.d("ApnSettings", "onPhoneStateChanged");
        updateState();
    }

    private void updateState() {
        if (getPreferenceScreen() == null || this.mRestoreItem == null) {
            return;
        }
        getPreferenceScreen().setEnabled(!isPhoneInCall());
        this.mRestoreItem.setEnabled(!isPhoneInCall());
    }

    private boolean isPhoneInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) getContext().getSystemService(AirplaneModePreferenceController.TELECOM_SERVICE);
    }
}
