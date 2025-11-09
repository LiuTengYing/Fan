package com.android.settings.network.telephony;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Messenger;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseDataConnectionState;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.R$string;
import com.android.unisoc.telephony.RadioInteractor;
import com.unisoc.sdk.common.telephony.UniTelephonyManager;
/* loaded from: classes.dex */
public class UniNetworkScanHelper {
    private int mConnectedPhoneId;
    private Context mContext;
    private int[] mDataState;
    private int mDisconnectPendingCount;
    private NetworkScanHelper mNetworkScanHelper;
    private PhoneStateListener[] mPhoneStateListeners;
    private boolean mScanFlag;
    private TelephonyManager mTelephonyManager;
    private UniTelephonyManager mUniTelephonyManager;
    private int mPhoneCount = TelephonyManager.getDefault().getPhoneCount();
    private DataHandler mDataHandler = new DataHandler();

    public UniNetworkScanHelper(TelephonyManager telephonyManager, NetworkScanHelper networkScanHelper, Context context) {
        int i = this.mPhoneCount;
        this.mPhoneStateListeners = new PhoneStateListener[i];
        this.mDataState = new int[i];
        this.mConnectedPhoneId = -1;
        this.mDisconnectPendingCount = i;
        this.mScanFlag = true;
        this.mTelephonyManager = telephonyManager;
        this.mNetworkScanHelper = networkScanHelper;
        this.mUniTelephonyManager = UniTelephonyManager.from(context);
        this.mContext = context;
    }

    public void startNetworkScan() {
        deactivateDataConnections();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DataHandler extends Handler {
        DataHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.d("UniNetworkScanHelper", "DataHandler handleMessage: " + message);
            int i = message.what;
            if (i != 100) {
                if (i == 200) {
                    UniNetworkScanHelper uniNetworkScanHelper = UniNetworkScanHelper.this;
                    uniNetworkScanHelper.forceDetachDataConn(uniNetworkScanHelper.mConnectedPhoneId);
                    return;
                } else if (i != 300) {
                    return;
                } else {
                    Log.d("UniNetworkScanHelper", "All data detached, do query network");
                    UniNetworkScanHelper.this.unregister();
                    UniNetworkScanHelper.this.mScanFlag = false;
                    UniNetworkScanHelper.this.mNetworkScanHelper.startNetworkScan(2);
                    return;
                }
            }
            boolean disconnectedOnAllPhones = UniNetworkScanHelper.this.disconnectedOnAllPhones();
            if (UniNetworkScanHelper.this.mDisconnectPendingCount > 0) {
                UniNetworkScanHelper uniNetworkScanHelper2 = UniNetworkScanHelper.this;
                uniNetworkScanHelper2.mDisconnectPendingCount--;
            }
            Log.d("UniNetworkScanHelper", "mScanFlag: " + UniNetworkScanHelper.this.mScanFlag + ", disconnected: " + disconnectedOnAllPhones + ", mDisconnectPendingCount: " + UniNetworkScanHelper.this.mDisconnectPendingCount);
            if (UniNetworkScanHelper.this.mScanFlag && disconnectedOnAllPhones && UniNetworkScanHelper.this.mDisconnectPendingCount == 0) {
                UniNetworkScanHelper uniNetworkScanHelper3 = UniNetworkScanHelper.this;
                if (uniNetworkScanHelper3.needForceDetach(uniNetworkScanHelper3.mConnectedPhoneId)) {
                    UniNetworkScanHelper.this.mScanFlag = false;
                    sendEmptyMessage(200);
                    return;
                }
                Log.d("UniNetworkScanHelper", "Not need detach, do query network");
                UniNetworkScanHelper.this.unregister();
                UniNetworkScanHelper.this.mScanFlag = false;
                UniNetworkScanHelper.this.mNetworkScanHelper.startNetworkScan(2);
            }
        }
    }

    private void deactivateDataConnections() {
        Log.d("UniNetworkScanHelper", "deactivateDataConnections");
        Toast.makeText(this.mContext, R$string.mobile_data_not_allowed, 1).show();
        for (int i = 0; i < this.mPhoneCount; i++) {
            if (this.mConnectedPhoneId == -1 && !this.mUniTelephonyManager.isAllDataDisconnected(i)) {
                this.mConnectedPhoneId = i;
                Log.d("UniNetworkScanHelper", "mConnectedPhoneId: " + this.mConnectedPhoneId);
            }
            this.mUniTelephonyManager.setVendorDataEnabled(i, false);
        }
        register();
    }

    private PhoneStateListener getPhoneStateListener(final int i) {
        this.mPhoneStateListeners[i] = new PhoneStateListener() { // from class: com.android.settings.network.telephony.UniNetworkScanHelper.1
            @Override // android.telephony.PhoneStateListener
            public void onPreciseDataConnectionStateChanged(PreciseDataConnectionState preciseDataConnectionState) {
                UniNetworkScanHelper.this.mDataState[i] = preciseDataConnectionState.getDataConnectionState();
                Boolean valueOf = Boolean.valueOf(UniNetworkScanHelper.this.mUniTelephonyManager.isAllDataDisconnected(i));
                Log.d("UniNetworkScanHelper", "onPreciseDataConnectionStateChanged for phone: " + i + ", connection state: " + UniNetworkScanHelper.this.mDataState[i] + ", isAllDataDisconnected: " + valueOf);
                if (UniNetworkScanHelper.this.mDataState[i] == 0 && valueOf.booleanValue()) {
                    UniNetworkScanHelper.this.mDataHandler.sendMessage(UniNetworkScanHelper.this.mDataHandler.obtainMessage(100));
                }
            }
        };
        return this.mPhoneStateListeners[i];
    }

    private void register() {
        for (int i = 0; i < this.mPhoneCount; i++) {
            if (!this.mUniTelephonyManager.isAllDataDisconnected(i)) {
                Log.d("UniNetworkScanHelper", "register for data state change on phone " + i + ", subId= " + SubscriptionManager.getSubId(i)[0]);
                ((TelephonyManager) this.mContext.getSystemService("phone")).createForSubscriptionId(SubscriptionManager.getSubId(i)[0]).listen(getPhoneStateListener(i), 4096);
            } else {
                Log.d("UniNetworkScanHelper", "data already disconnected on phone " + i);
                this.mDataState[i] = 0;
                DataHandler dataHandler = this.mDataHandler;
                dataHandler.sendMessage(dataHandler.obtainMessage(100));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregister() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        for (int i = 0; i < this.mPhoneCount; i++) {
            PhoneStateListener phoneStateListener = this.mPhoneStateListeners[i];
            if (phoneStateListener != null) {
                telephonyManager.listen(phoneStateListener, 0);
                this.mPhoneStateListeners[i] = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean disconnectedOnAllPhones() {
        for (int i = 0; i < this.mPhoneCount; i++) {
            if (this.mDataState[i] != 0) {
                Log.d("UniNetworkScanHelper", "data not disconnected on phone: " + i);
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean needForceDetach(int i) {
        ServiceState serviceState;
        if (!SubscriptionManager.isValidPhoneId(i) || (serviceState = this.mTelephonyManager.createForSubscriptionId(getSubId(i)).getServiceState()) == null) {
            return false;
        }
        int rilDataRadioTechnology = serviceState.getRilDataRadioTechnology();
        Log.d("UniNetworkScanHelper", "needForceDetach() radioTech=" + rilDataRadioTechnology);
        return (rilDataRadioTechnology == 14 || rilDataRadioTechnology == 20) ? false : true;
    }

    private void forceDetachDataConn(Messenger messenger, int i) {
        new RadioInteractor(this.mContext).forceDetachDataConn(messenger, -1, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceDetachDataConn(int i) {
        HandlerThread handlerThread = new HandlerThread("UniNetworkScanHelper");
        handlerThread.start();
        forceDetachDataConn(new Messenger(new Handler(handlerThread.getLooper()) { // from class: com.android.settings.network.telephony.UniNetworkScanHelper.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                Log.d("UniNetworkScanHelper", "Messenger handleMessage: " + message);
                UniNetworkScanHelper.this.mDataHandler.sendEmptyMessage(300);
            }
        }), i);
    }

    public void enableData() {
        for (int i = 0; i < this.mPhoneCount; i++) {
            this.mUniTelephonyManager.setVendorDataEnabled(i, true);
        }
    }

    public void onDestroy() {
        unregister();
    }

    private int getSubId(int i) {
        int[] subId = SubscriptionManager.getSubId(i);
        if (subId == null || subId.length < 1) {
            return -1;
        }
        return subId[0];
    }
}
