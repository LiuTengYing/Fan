package com.android.settings.network.telephony;

import android.content.Context;
import android.os.PersistableBundle;
import android.telephony.CellInfo;
import android.telephony.NetworkScan;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessSpecifier;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyScanManager;
import android.telephony.UniCarrierConfigManager;
import android.util.Log;
import com.android.internal.telephony.CellNetworkScanResult;
import com.android.internal.telephony.OperatorInfo;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class NetworkScanHelper {
    static final boolean INCREMENTAL_RESULTS = true;
    static final int INCREMENTAL_RESULTS_PERIODICITY_SEC = 3;
    static final int MAX_SEARCH_TIME_SEC = 300;
    static final int SEARCH_PERIODICITY_SEC = 5;
    private Context mContext;
    private final Executor mExecutor;
    private final TelephonyScanManager.NetworkScanCallback mInternalNetworkScanCallback;
    private final NetworkScanCallback mNetworkScanCallback;
    private ListenableFuture<List<CellInfo>> mNetworkScanFuture;
    private NetworkScan mNetworkScanRequester;
    private PersistableBundle mPersistableBundle;
    private final TelephonyManager mTelephonyManager;
    private UniCarrierConfigManager mUniCarrierConfigManager;

    /* loaded from: classes.dex */
    public interface NetworkScanCallback {
        void onComplete();

        void onError(int i);

        void onResults(List<CellInfo> list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int convertToScanErrorCode(int i) {
        return i != 2 ? 1 : 10000;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$hasNrSaCapability$0(int i) {
        return i == 2;
    }

    public NetworkScanHelper(TelephonyManager telephonyManager, NetworkScanCallback networkScanCallback, Executor executor) {
        this.mTelephonyManager = telephonyManager;
        this.mNetworkScanCallback = networkScanCallback;
        this.mInternalNetworkScanCallback = new NetworkScanCallbackImpl();
        this.mExecutor = executor;
    }

    public NetworkScanHelper(TelephonyManager telephonyManager, NetworkScanCallback networkScanCallback, Executor executor, Context context) {
        this(telephonyManager, networkScanCallback, executor);
        this.mContext = context;
        this.mUniCarrierConfigManager = new UniCarrierConfigManager(this.mContext);
    }

    NetworkScanRequest createNetworkScanForPreferredAccessNetworks() {
        long preferredNetworkTypeBitmask = this.mTelephonyManager.getPreferredNetworkTypeBitmask() & 906119;
        ArrayList arrayList = new ArrayList();
        if (!ignoreScanUnder2G() && (preferredNetworkTypeBitmask == 0 || (32843 & preferredNetworkTypeBitmask) != 0)) {
            arrayList.add(new RadioAccessSpecifier(1, null, null));
        }
        int i = (preferredNetworkTypeBitmask > 0L ? 1 : (preferredNetworkTypeBitmask == 0L ? 0 : -1));
        if (i == 0 || (93108 & preferredNetworkTypeBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(2, null, null));
        }
        if (i == 0 || (397312 & preferredNetworkTypeBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(3, null, null));
        }
        if (hasNrSaCapability() && (i == 0 || (preferredNetworkTypeBitmask & 524288) != 0)) {
            arrayList.add(new RadioAccessSpecifier(6, null, null));
            Log.d("NetworkScanHelper", "radioAccessSpecifiers add NGRAN.");
        }
        return new NetworkScanRequest(0, (RadioAccessSpecifier[]) arrayList.toArray(new RadioAccessSpecifier[arrayList.size()]), 5, MAX_SEARCH_TIME_SEC, true, 3, null);
    }

    public void startNetworkScan(int i) {
        if (i == 1) {
            SettableFuture create = SettableFuture.create();
            this.mNetworkScanFuture = create;
            Futures.addCallback(create, new FutureCallback<List<CellInfo>>() { // from class: com.android.settings.network.telephony.NetworkScanHelper.1
                @Override // com.google.common.util.concurrent.FutureCallback
                public void onSuccess(List<CellInfo> list) {
                    NetworkScanHelper.this.onResults(list);
                    NetworkScanHelper.this.onComplete();
                }

                @Override // com.google.common.util.concurrent.FutureCallback
                public void onFailure(Throwable th) {
                    if (th instanceof CancellationException) {
                        return;
                    }
                    NetworkScanHelper.this.onError(Integer.parseInt(th.getMessage()));
                }
            }, MoreExecutors.directExecutor());
            this.mExecutor.execute(new NetworkScanSyncTask(this.mTelephonyManager, (SettableFuture) this.mNetworkScanFuture));
        } else if (i == 2 && this.mNetworkScanRequester == null) {
            NetworkScan requestNetworkScan = this.mTelephonyManager.requestNetworkScan(createNetworkScanForPreferredAccessNetworks(), this.mExecutor, this.mInternalNetworkScanCallback);
            this.mNetworkScanRequester = requestNetworkScan;
            if (requestNetworkScan == null) {
                onError(10000);
            }
        }
    }

    public void stopNetworkQuery() {
        NetworkScan networkScan = this.mNetworkScanRequester;
        if (networkScan != null) {
            networkScan.stopScan();
            this.mNetworkScanRequester = null;
        }
        ListenableFuture<List<CellInfo>> listenableFuture = this.mNetworkScanFuture;
        if (listenableFuture != null) {
            listenableFuture.cancel(true);
            this.mNetworkScanFuture = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onResults(List<CellInfo> list) {
        this.mNetworkScanCallback.onResults(list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onComplete() {
        this.mNetworkScanCallback.onComplete();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onError(int i) {
        this.mNetworkScanCallback.onError(i);
    }

    private boolean hasNrSaCapability() {
        return Arrays.stream(this.mTelephonyManager.getPhoneCapability().getDeviceNrCapabilities()).anyMatch(new IntPredicate() { // from class: com.android.settings.network.telephony.NetworkScanHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                boolean lambda$hasNrSaCapability$0;
                lambda$hasNrSaCapability$0 = NetworkScanHelper.lambda$hasNrSaCapability$0(i);
                return lambda$hasNrSaCapability$0;
            }
        });
    }

    /* loaded from: classes.dex */
    private final class NetworkScanCallbackImpl extends TelephonyScanManager.NetworkScanCallback {
        private NetworkScanCallbackImpl() {
        }

        @Override // android.telephony.TelephonyScanManager.NetworkScanCallback
        public void onResults(List<CellInfo> list) {
            Log.d("NetworkScanHelper", "Async scan onResults() results = " + CellInfoUtil.cellInfoListToString(list));
            NetworkScanHelper.this.onResults(list);
        }

        @Override // android.telephony.TelephonyScanManager.NetworkScanCallback
        public void onComplete() {
            Log.d("NetworkScanHelper", "async scan onComplete()");
            NetworkScanHelper.this.onComplete();
        }

        @Override // android.telephony.TelephonyScanManager.NetworkScanCallback
        public void onError(int i) {
            Log.d("NetworkScanHelper", "async scan onError() errorCode = " + i);
            NetworkScanHelper.this.onError(i);
        }
    }

    /* loaded from: classes.dex */
    private static final class NetworkScanSyncTask implements Runnable {
        private final SettableFuture<List<CellInfo>> mCallback;
        private final TelephonyManager mTelephonyManager;

        NetworkScanSyncTask(TelephonyManager telephonyManager, SettableFuture<List<CellInfo>> settableFuture) {
            this.mTelephonyManager = telephonyManager;
            this.mCallback = settableFuture;
        }

        @Override // java.lang.Runnable
        public void run() {
            CellNetworkScanResult availableNetworks = this.mTelephonyManager.getAvailableNetworks();
            if (availableNetworks.getStatus() == 1) {
                List<CellInfo> list = (List) availableNetworks.getOperators().stream().map(new Function() { // from class: com.android.settings.network.telephony.NetworkScanHelper$NetworkScanSyncTask$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        CellInfo convertOperatorInfoToCellInfo;
                        convertOperatorInfoToCellInfo = CellInfoUtil.convertOperatorInfoToCellInfo((OperatorInfo) obj);
                        return convertOperatorInfoToCellInfo;
                    }
                }).collect(Collectors.toList());
                Log.d("NetworkScanHelper", "Sync network scan completed, cellInfos = " + CellInfoUtil.cellInfoListToString(list));
                this.mCallback.set(list);
                return;
            }
            Throwable th = new Throwable(Integer.toString(NetworkScanHelper.convertToScanErrorCode(availableNetworks.getStatus())));
            this.mCallback.setException(th);
            Log.d("NetworkScanHelper", "Sync network scan error, ex = " + th);
        }
    }

    private boolean ignoreScanUnder2G() {
        UniCarrierConfigManager uniCarrierConfigManager = this.mUniCarrierConfigManager;
        if (uniCarrierConfigManager != null) {
            PersistableBundle configForSubId = uniCarrierConfigManager.getConfigForSubId(this.mTelephonyManager.getSubscriptionId());
            this.mPersistableBundle = configForSubId;
            if (configForSubId != null) {
                return configForSubId.getBoolean("ignore_network_scan_under_2g");
            }
            return false;
        }
        return false;
    }
}
