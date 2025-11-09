package com.android.settings.deviceinfo.simstatus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.CellSignalStrength;
import android.telephony.ICellBroadcastService;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.ims.ImsManager;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class SimStatusDialogController implements LifecycleObserver {
    static final int MAX_PHONE_COUNT_SINGLE_SIM = 1;
    private final CarrierConfigManager mCarrierConfigManager;
    private CellBroadcastServiceConnection mCellBroadcastServiceConnection;
    private final Context mContext;
    private final SimStatusDialogFragment mDialog;
    private final EuiccManager mEuiccManager;
    private ServiceState mPreviousServiceState;
    private final Resources mRes;
    private boolean mShowLatestAreaInfo;
    private final int mSlotIndex;
    private SubscriptionInfo mSubscriptionInfo;
    private final SubscriptionManager mSubscriptionManager;
    protected SimStatusDialogTelephonyCallback mTelephonyCallback;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyManager mTelephonyManager;
    private boolean mVoWifiRegistered;
    static final int NETWORK_PROVIDER_VALUE_ID = R$id.operator_name_value;
    static final int PHONE_NUMBER_VALUE_ID = R$id.number_value;
    static final int CELLULAR_NETWORK_STATE = R$id.data_state_value;
    static final int OPERATOR_INFO_LABEL_ID = R$id.latest_area_info_label;
    static final int OPERATOR_INFO_VALUE_ID = R$id.latest_area_info_value;
    static final int SERVICE_STATE_VALUE_ID = R$id.service_state_value;
    static final int SIGNAL_STRENGTH_LABEL_ID = R$id.signal_strength_label;
    static final int SIGNAL_STRENGTH_VALUE_ID = R$id.signal_strength_value;
    static final int CELL_VOICE_NETWORK_TYPE_VALUE_ID = R$id.voice_network_type_value;
    static final int CELL_DATA_NETWORK_TYPE_VALUE_ID = R$id.data_network_type_value;
    static final int ROAMING_INFO_VALUE_ID = R$id.roaming_state_value;
    static final int ICCID_INFO_LABEL_ID = R$id.icc_id_label;
    static final int ICCID_INFO_VALUE_ID = R$id.icc_id_value;
    static final int EID_INFO_LABEL_ID = R$id.esim_id_label;
    static final int EID_INFO_VALUE_ID = R$id.esim_id_value;
    static final int IMS_REGISTRATION_STATE_LABEL_ID = R$id.ims_reg_state_label;
    static final int IMS_REGISTRATION_STATE_VALUE_ID = R$id.ims_reg_state_value;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.1
        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            int subscriptionId = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            SimStatusDialogController simStatusDialogController = SimStatusDialogController.this;
            simStatusDialogController.mSubscriptionInfo = simStatusDialogController.getPhoneSubscriptionInfo(simStatusDialogController.mSlotIndex);
            int subscriptionId2 = SimStatusDialogController.this.mSubscriptionInfo != null ? SimStatusDialogController.this.mSubscriptionInfo.getSubscriptionId() : -1;
            if (subscriptionId != subscriptionId2) {
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                    SimStatusDialogController.this.unregisterImsRegistrationCallback(subscriptionId);
                }
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId2)) {
                    SimStatusDialogController simStatusDialogController2 = SimStatusDialogController.this;
                    simStatusDialogController2.mTelephonyManager = simStatusDialogController2.getTelephonyManager().createForSubscriptionId(subscriptionId2);
                    SimStatusDialogController.this.registerImsRegistrationCallback(subscriptionId2);
                }
            }
            SimStatusDialogController.this.updateSubscriptionStatus();
        }
    };
    private boolean mIsRegisteredListener = false;
    private final BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.telephony.action.AREA_INFO_UPDATED".equals(intent.getAction()) && intent.getIntExtra("android.telephony.extra.SLOT_INDEX", 0) == SimStatusDialogController.this.mSlotIndex) {
                SimStatusDialogController.this.updateAreaInfoText();
            }
        }
    };
    private ImsMmTelManager.RegistrationCallback mImsRegStateCallback = new ImsMmTelManager.RegistrationCallback() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.3
        public void onRegistered(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R$string.ims_reg_status_registered));
            Log.d("SimStatusDialogCtrl", "onRegistered imsTransportType = " + i);
            if (i == 2) {
                SimStatusDialogController.this.mVoWifiRegistered = true;
            } else {
                SimStatusDialogController.this.mVoWifiRegistered = false;
            }
            SimStatusDialogController.this.updateNetworkType();
        }

        public void onRegistering(int i) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R$string.ims_reg_status_not_registered));
        }

        public void onUnregistered(ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R$string.ims_reg_status_not_registered));
            Log.d("SimStatusDialogCtrl", "onUnregistered");
            SimStatusDialogController.this.mVoWifiRegistered = false;
            SimStatusDialogController.this.updateNetworkType();
        }

        public void onTechnologyChangeFailed(int i, ImsReasonInfo imsReasonInfo) {
            SimStatusDialogController.this.mDialog.setText(SimStatusDialogController.IMS_REGISTRATION_STATE_VALUE_ID, SimStatusDialogController.this.mRes.getString(R$string.ims_reg_status_not_registered));
        }
    };

    static String getNetworkTypeName(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "CDMA - EvDo rev. 0";
            case 6:
                return "CDMA - EvDo rev. A";
            case 7:
                return "CDMA - 1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "CDMA - EvDo rev. B";
            case 13:
                return "LTE";
            case 14:
                return "CDMA - eHRPD";
            case 15:
                return "HSPA+";
            case 16:
                return "GSM";
            case 17:
                return "TD_SCDMA";
            case 18:
                return "IWLAN";
            case 19:
            default:
                return "UNKNOWN";
            case 20:
                return "NR SA";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CellBroadcastServiceConnection implements ServiceConnection {
        private IBinder mService;

        private CellBroadcastServiceConnection() {
        }

        public IBinder getService() {
            return this.mService;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("SimStatusDialogCtrl", "connected to CellBroadcastService");
            this.mService = iBinder;
            SimStatusDialogController.this.updateAreaInfoText();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "mICellBroadcastService has disconnected unexpectedly");
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Binding died");
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            this.mService = null;
            Log.d("SimStatusDialogCtrl", "Null binding");
        }
    }

    public SimStatusDialogController(SimStatusDialogFragment simStatusDialogFragment, Lifecycle lifecycle, int i) {
        this.mVoWifiRegistered = false;
        this.mDialog = simStatusDialogFragment;
        Context context = simStatusDialogFragment.getContext();
        this.mContext = context;
        this.mSlotIndex = i;
        this.mSubscriptionInfo = getPhoneSubscriptionInfo(i);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mEuiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mRes = context.getResources();
        ImsManager imsManager = ImsManager.getInstance(context, i);
        if (imsManager != null && imsManager.getRegistrationTech() == 1) {
            this.mVoWifiRegistered = true;
        }
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public TelephonyManager getTelephonyManager() {
        return this.mTelephonyManager;
    }

    public void initialize() {
        requestForUpdateEid();
        if (this.mSubscriptionInfo == null) {
            return;
        }
        this.mTelephonyManager = getTelephonyManager().createForSubscriptionId(this.mSubscriptionInfo.getSubscriptionId());
        this.mTelephonyCallback = new SimStatusDialogTelephonyCallback();
        updateLatestAreaInfo();
        updateSubscriptionStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSubscriptionStatus() {
        updateNetworkProvider();
        ServiceState serviceState = getTelephonyManager().getServiceState();
        SignalStrength signalStrength = getTelephonyManager().getSignalStrength();
        updatePhoneNumber();
        updateServiceState(serviceState);
        updateSignalStrength(signalStrength);
        updateNetworkType();
        updateRoamingStatus(serviceState);
        updateIccidNumber();
        updateImsRegistrationState();
    }

    public void deinitialize() {
        if (this.mShowLatestAreaInfo) {
            CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
            if (cellBroadcastServiceConnection != null && cellBroadcastServiceConnection.getService() != null) {
                this.mContext.unbindService(this.mCellBroadcastServiceConnection);
            }
            this.mCellBroadcastServiceConnection = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mSubscriptionInfo == null) {
            return;
        }
        this.mTelephonyManager = getTelephonyManager().createForSubscriptionId(this.mSubscriptionInfo.getSubscriptionId());
        getTelephonyManager().registerTelephonyCallback(this.mContext.getMainExecutor(), this.mTelephonyCallback);
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mOnSubscriptionsChangedListener);
        registerImsRegistrationCallback(this.mSubscriptionInfo.getSubscriptionId());
        if (this.mShowLatestAreaInfo) {
            updateAreaInfoText();
            this.mContext.registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.telephony.action.AREA_INFO_UPDATED"));
        }
        this.mIsRegisteredListener = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            if (this.mIsRegisteredListener) {
                this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
                getTelephonyManager().unregisterTelephonyCallback(this.mTelephonyCallback);
                if (this.mShowLatestAreaInfo) {
                    this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
                }
                this.mIsRegisteredListener = false;
                return;
            }
            return;
        }
        unregisterImsRegistrationCallback(subscriptionInfo.getSubscriptionId());
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
        getTelephonyManager().unregisterTelephonyCallback(this.mTelephonyCallback);
        if (this.mShowLatestAreaInfo) {
            this.mContext.unregisterReceiver(this.mAreaInfoReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkProvider() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        this.mDialog.setText(NETWORK_PROVIDER_VALUE_ID, subscriptionInfo != null ? subscriptionInfo.getCarrierName() : null);
    }

    public void updatePhoneNumber() {
        this.mDialog.setText(PHONE_NUMBER_VALUE_ID, DeviceInfoUtils.getBidiFormattedPhoneNumber(this.mContext, this.mSubscriptionInfo));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDataState(int i) {
        String string;
        if (i == 0) {
            string = this.mRes.getString(R$string.radioInfo_data_disconnected);
        } else if (i == 1) {
            string = this.mRes.getString(R$string.radioInfo_data_connecting);
        } else if (i == 2) {
            string = this.mRes.getString(R$string.radioInfo_data_connected);
        } else if (i == 3) {
            string = this.mRes.getString(R$string.radioInfo_data_suspended);
        } else {
            string = this.mRes.getString(R$string.radioInfo_unknown);
        }
        this.mDialog.setText(CELLULAR_NETWORK_STATE, string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAreaInfoText() {
        CellBroadcastServiceConnection cellBroadcastServiceConnection;
        ICellBroadcastService asInterface;
        if (!this.mShowLatestAreaInfo || (cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection) == null || (asInterface = ICellBroadcastService.Stub.asInterface(cellBroadcastServiceConnection.getService())) == null) {
            return;
        }
        try {
            this.mDialog.setText(OPERATOR_INFO_VALUE_ID, asInterface.getCellBroadcastAreaInfo(this.mSlotIndex));
        } catch (RemoteException e) {
            Log.d("SimStatusDialogCtrl", "Can't get area info. e=" + e);
        }
    }

    private void bindCellBroadcastService() {
        this.mCellBroadcastServiceConnection = new CellBroadcastServiceConnection();
        Intent intent = new Intent("android.telephony.CellBroadcastService");
        String cellBroadcastServicePackage = getCellBroadcastServicePackage();
        if (TextUtils.isEmpty(cellBroadcastServicePackage)) {
            return;
        }
        intent.setPackage(cellBroadcastServicePackage);
        CellBroadcastServiceConnection cellBroadcastServiceConnection = this.mCellBroadcastServiceConnection;
        if (cellBroadcastServiceConnection != null && cellBroadcastServiceConnection.getService() == null) {
            if (this.mContext.bindService(intent, this.mCellBroadcastServiceConnection, 1)) {
                return;
            }
            Log.e("SimStatusDialogCtrl", "Unable to bind to service");
            return;
        }
        Log.d("SimStatusDialogCtrl", "skipping bindService because connection already exists");
    }

    private String getCellBroadcastServicePackage() {
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.telephony.CellBroadcastService"), 1048576);
        if (queryIntentServices.size() != 1) {
            Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found " + queryIntentServices.size() + " CBS packages");
        }
        for (ResolveInfo resolveInfo : queryIntentServices) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                String str = serviceInfo.packageName;
                if (!TextUtils.isEmpty(str)) {
                    if (packageManager.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str) == 0) {
                        Log.d("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str);
                        return str;
                    }
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: " + str + " does not have READ_PRIVILEGED_PHONE_STATE permission");
                } else {
                    Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: found a CBS package but packageName is null/empty");
                }
            }
        }
        Log.e("SimStatusDialogCtrl", "getCellBroadcastServicePackageName: package name not found");
        return null;
    }

    private void updateLatestAreaInfo() {
        boolean z = Resources.getSystem().getBoolean(17891746) && getTelephonyManager().getPhoneType() != 2;
        this.mShowLatestAreaInfo = z;
        if (z) {
            bindCellBroadcastService();
            return;
        }
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_LABEL_ID);
        this.mDialog.removeSettingFromScreen(OPERATOR_INFO_VALUE_ID);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateServiceState(ServiceState serviceState) {
        String string;
        int combinedServiceState = Utils.getCombinedServiceState(serviceState);
        if (!Utils.isInService(serviceState)) {
            resetSignalStrength();
        } else if (!Utils.isInService(this.mPreviousServiceState)) {
            updateSignalStrength(getTelephonyManager().getSignalStrength());
        }
        if (combinedServiceState == 0) {
            string = this.mRes.getString(R$string.radioInfo_service_in);
        } else if (combinedServiceState == 1 || combinedServiceState == 2) {
            string = this.mRes.getString(R$string.radioInfo_service_out);
        } else if (combinedServiceState == 3) {
            string = this.mRes.getString(R$string.radioInfo_service_off);
        } else {
            string = this.mRes.getString(R$string.radioInfo_unknown);
        }
        this.mDialog.setText(SERVICE_STATE_VALUE_ID, string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x014b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateSignalStrength(android.telephony.SignalStrength r15) {
        /*
            Method dump skipped, instructions count: 361
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.simstatus.SimStatusDialogController.updateSignalStrength(android.telephony.SignalStrength):void");
    }

    private void resetSignalStrength() {
        this.mDialog.setText(SIGNAL_STRENGTH_VALUE_ID, "0");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkType() {
        String str;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            String networkTypeName = getNetworkTypeName(0);
            this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName);
            this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, networkTypeName);
            return;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        ServiceState serviceStateForSubscriber = this.mTelephonyManager.getServiceStateForSubscriber(subscriptionId);
        Log.d("SimStatusDialogCtrl", "Current serviceState = " + serviceStateForSubscriber);
        if (!Utils.isInService(serviceStateForSubscriber)) {
            this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, null);
            this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, null);
            return;
        }
        int dataNetworkType = getTelephonyManager().getDataNetworkType();
        int voiceNetworkType = getTelephonyManager().getVoiceNetworkType();
        TelephonyDisplayInfo telephonyDisplayInfo = this.mTelephonyDisplayInfo;
        int overrideNetworkType = telephonyDisplayInfo == null ? 0 : telephonyDisplayInfo.getOverrideNetworkType();
        if (dataNetworkType == 0) {
            str = null;
        } else if (13 == dataNetworkType) {
            if (serviceStateForSubscriber != null && serviceStateForSubscriber.isUsingCarrierAggregation()) {
                str = TelephonyManager.getNetworkTypeName(19);
            } else {
                str = TelephonyManager.getNetworkTypeName(dataNetworkType);
            }
        } else {
            str = TelephonyManager.getNetworkTypeName(dataNetworkType);
        }
        String networkTypeName2 = voiceNetworkType != 0 ? getNetworkTypeName(voiceNetworkType) : null;
        boolean z = overrideNetworkType == 5 || overrideNetworkType == 3;
        if (dataNetworkType == 13 && z) {
            str = "NR NSA";
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
        boolean z2 = configForSubId != null ? configForSubId.getBoolean("show_4g_for_lte_data_icon_bool") : false;
        Log.d("SimStatusDialogCtrl", "updateNetworkType mVoWifiRegistered = " + this.mVoWifiRegistered);
        if (this.mVoWifiRegistered) {
            str = getNetworkTypeName(18);
        } else if (z2) {
            if ("LTE".equals(str) && (serviceStateForSubscriber.getNrState() == 2 || serviceStateForSubscriber.getNrState() == 3)) {
                networkTypeName2 = "5G";
                str = networkTypeName2;
            } else {
                if ("LTE".equals(str)) {
                    str = "4G";
                }
                if ("LTE".equals(networkTypeName2)) {
                    networkTypeName2 = "4G";
                }
            }
        }
        this.mDialog.setText(CELL_VOICE_NETWORK_TYPE_VALUE_ID, networkTypeName2);
        this.mDialog.setText(CELL_DATA_NETWORK_TYPE_VALUE_ID, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRoamingStatus(ServiceState serviceState) {
        if (serviceState == null) {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(R$string.radioInfo_unknown));
        } else if (serviceState.getRoaming()) {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(R$string.radioInfo_roaming_in));
        } else {
            this.mDialog.setText(ROAMING_INFO_VALUE_ID, this.mRes.getString(R$string.radioInfo_roaming_not));
        }
    }

    private void updateIccidNumber() {
        this.mDialog.setText(ICCID_INFO_VALUE_ID, getTelephonyManager().getSimSerialNumber());
    }

    protected void requestForUpdateEid() {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestForUpdateEid$1() {
        final AtomicReference<String> eid = getEid(this.mSlotIndex);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.deviceinfo.simstatus.SimStatusDialogController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SimStatusDialogController.this.lambda$requestForUpdateEid$0(eid);
            }
        });
    }

    public AtomicReference<String> getEid(int i) {
        String eid;
        boolean z = true;
        if (getTelephonyManager().getActiveModemCount() > 1) {
            int intValue = ((Integer) this.mTelephonyManager.getLogicalToPhysicalSlotMapping().getOrDefault(Integer.valueOf(i), -1)).intValue();
            if (intValue != -1) {
                Iterator<UiccCardInfo> it = getTelephonyManager().getUiccCardsInfo().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    UiccCardInfo next = it.next();
                    if (next.getPhysicalSlotIndex() == intValue) {
                        if (next.isEuicc()) {
                            eid = next.getEid();
                            if (TextUtils.isEmpty(eid)) {
                                eid = this.mEuiccManager.createForCardId(next.getCardId()).getEid();
                            }
                        }
                    }
                }
            }
            eid = null;
            z = false;
        } else {
            if (this.mEuiccManager.isEnabled()) {
                eid = this.mEuiccManager.getEid();
            }
            eid = null;
            z = false;
        }
        if (z || eid != null) {
            return new AtomicReference<>(eid);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: updateEid */
    public void lambda$requestForUpdateEid$0(AtomicReference<String> atomicReference) {
        if (atomicReference == null) {
            this.mDialog.removeSettingFromScreen(EID_INFO_LABEL_ID);
            this.mDialog.removeSettingFromScreen(EID_INFO_VALUE_ID);
        } else if (atomicReference.get() != null) {
            this.mDialog.setText(EID_INFO_VALUE_ID, atomicReference.get());
        }
    }

    private boolean isImsRegistrationStateShowUp() {
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        if (subscriptionInfo == null) {
            return false;
        }
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionInfo.getSubscriptionId());
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("show_ims_registration_status_bool");
    }

    private void updateImsRegistrationState() {
        if (isImsRegistrationStateShowUp()) {
            return;
        }
        this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_LABEL_ID);
        this.mDialog.removeSettingFromScreen(IMS_REGISTRATION_STATE_VALUE_ID);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerImsRegistrationCallback(int i) {
        try {
            ImsMmTelManager.createForSubscriptionId(i).registerImsRegistrationCallback(this.mDialog.getContext().getMainExecutor(), this.mImsRegStateCallback);
        } catch (ImsException e) {
            Log.w("SimStatusDialogCtrl", "fail to register IMS status for subId=" + i, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterImsRegistrationCallback(int i) {
        ImsMmTelManager.createForSubscriptionId(i).unregisterImsRegistrationCallback(this.mImsRegStateCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SubscriptionInfo getPhoneSubscriptionInfo(int i) {
        return SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
    }

    private int getDbm(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getDbm() != -1) {
                return cellSignalStrength.getDbm();
            }
        }
        return -1;
    }

    private int getAsuLevel(SignalStrength signalStrength) {
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        if (cellSignalStrengths == null) {
            return -1;
        }
        for (CellSignalStrength cellSignalStrength : cellSignalStrengths) {
            if (cellSignalStrength.getAsuLevel() != -1) {
                return cellSignalStrength.getAsuLevel();
            }
        }
        return -1;
    }

    /* loaded from: classes.dex */
    class SimStatusDialogTelephonyCallback extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener, TelephonyCallback.SignalStrengthsListener, TelephonyCallback.ServiceStateListener, TelephonyCallback.DisplayInfoListener {
        SimStatusDialogTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.DataConnectionStateListener
        public void onDataConnectionStateChanged(int i, int i2) {
            SimStatusDialogController.this.updateDataState(i);
            SimStatusDialogController.this.updateNetworkType();
        }

        @Override // android.telephony.TelephonyCallback.SignalStrengthsListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SimStatusDialogController.this.updateSignalStrength(signalStrength);
        }

        @Override // android.telephony.TelephonyCallback.ServiceStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            SimStatusDialogController.this.updateNetworkProvider();
            SimStatusDialogController.this.updateServiceState(serviceState);
            SimStatusDialogController.this.updateRoamingStatus(serviceState);
            SimStatusDialogController.this.mPreviousServiceState = serviceState;
        }

        @Override // android.telephony.TelephonyCallback.DisplayInfoListener
        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
            SimStatusDialogController.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
            SimStatusDialogController.this.updateNetworkType();
        }
    }
}
