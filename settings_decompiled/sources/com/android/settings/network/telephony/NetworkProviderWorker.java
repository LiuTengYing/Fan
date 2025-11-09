package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.mediarouter.media.MediaRoute2Provider$$ExternalSyntheticLambda0;
import com.android.settings.network.InternetUpdater;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settings.network.MobileDataEnabledListener;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.network.telephony.DataConnectivityListener;
import com.android.settings.network.telephony.SignalStrengthListener;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settings.wifi.slice.WifiScanWorker;
import com.android.settingslib.mobile.MobileMappings;
import com.android.settingslib.mobile.TelephonyIcons;
import java.util.Collections;
import java.util.Objects;
/* loaded from: classes.dex */
public class NetworkProviderWorker extends WifiScanWorker implements SignalStrengthListener.Callback, MobileDataEnabledListener.Client, DataConnectivityListener.Client, InternetUpdater.InternetChangeListener, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private MobileMappings.Config mConfig;
    private final BroadcastReceiver mConnectionChangeReceiver;
    private DataConnectivityListener mConnectivityListener;
    private final Context mContext;
    private MobileDataEnabledListener mDataEnabledListener;
    private int mDefaultDataSubId;
    final Handler mHandler;
    private int mInternetType;
    private InternetUpdater mInternetUpdater;
    private DataContentObserver mMobileDataObserver;
    private SignalStrengthListener mSignalStrengthListener;
    private SubscriptionsChangeListener mSubscriptionsListener;
    final NetworkProviderTelephonyCallback mTelephonyCallback;
    private TelephonyDisplayInfo mTelephonyDisplayInfo;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settings.wifi.slice.WifiScanWorker
    public int getApRowCount() {
        return 6;
    }

    public NetworkProviderWorker(Context context, Uri uri) {
        super(context, uri);
        this.mDefaultDataSubId = -1;
        this.mConnectionChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.NetworkProviderWorker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                    Log.d("NetworkProviderWorker", "ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
                    NetworkProviderWorker.this.updateListener();
                }
            }
        };
        this.mConfig = null;
        this.mTelephonyDisplayInfo = new TelephonyDisplayInfo(0, 0);
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mMobileDataObserver = new DataContentObserver(handler, this);
        this.mContext = context;
        this.mDefaultDataSubId = getDefaultDataSubscriptionId();
        Log.d("NetworkProviderWorker", "Init, SubId: " + this.mDefaultDataSubId);
        this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mDefaultDataSubId);
        this.mTelephonyCallback = new NetworkProviderTelephonyCallback();
        this.mSubscriptionsListener = new SubscriptionsChangeListener(context, this);
        this.mDataEnabledListener = new MobileDataEnabledListener(context, this);
        this.mConnectivityListener = new DataConnectivityListener(context, this);
        this.mSignalStrengthListener = new SignalStrengthListener(context, this);
        this.mConfig = getConfig(context);
        InternetUpdater internetUpdater = new InternetUpdater(context, getLifecycle(), this);
        this.mInternetUpdater = internetUpdater;
        this.mInternetType = internetUpdater.getInternetType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.slice.WifiScanWorker, com.android.settings.slices.SliceBackgroundWorker
    public void onSlicePinned() {
        Log.d("NetworkProviderWorker", "onSlicePinned");
        this.mMobileDataObserver.register(this.mContext, this.mDefaultDataSubId);
        this.mSubscriptionsListener.start();
        this.mDataEnabledListener.start(this.mDefaultDataSubId);
        this.mConnectivityListener.start();
        this.mSignalStrengthListener.resume();
        TelephonyManager telephonyManager = this.mTelephonyManager;
        Handler handler = this.mHandler;
        Objects.requireNonNull(handler);
        telephonyManager.registerTelephonyCallback(new MediaRoute2Provider$$ExternalSyntheticLambda0(handler), this.mTelephonyCallback);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        this.mContext.registerReceiver(this.mConnectionChangeReceiver, intentFilter);
        super.onSlicePinned();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.slice.WifiScanWorker, com.android.settings.slices.SliceBackgroundWorker
    public void onSliceUnpinned() {
        Log.d("NetworkProviderWorker", "onSliceUnpinned");
        this.mMobileDataObserver.unregister(this.mContext);
        this.mSubscriptionsListener.stop();
        this.mDataEnabledListener.stop();
        this.mConnectivityListener.stop();
        this.mSignalStrengthListener.pause();
        this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
        BroadcastReceiver broadcastReceiver = this.mConnectionChangeReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
        super.onSliceUnpinned();
    }

    @Override // com.android.settings.wifi.slice.WifiScanWorker, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mMobileDataObserver = null;
        super.close();
    }

    public void updateSlice() {
        notifySliceChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateListener() {
        int defaultDataSubscriptionId = getDefaultDataSubscriptionId();
        if (this.mDefaultDataSubId == defaultDataSubscriptionId) {
            Log.d("NetworkProviderWorker", "DDS: no change");
            return;
        }
        this.mDefaultDataSubId = defaultDataSubscriptionId;
        Log.d("NetworkProviderWorker", "DDS: defaultDataSubId:" + this.mDefaultDataSubId);
        if (SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
            this.mTelephonyManager.unregisterTelephonyCallback(this.mTelephonyCallback);
            this.mMobileDataObserver.unregister(this.mContext);
            this.mSignalStrengthListener.updateSubscriptionIds(Collections.singleton(Integer.valueOf(defaultDataSubscriptionId)));
            TelephonyManager createForSubscriptionId = this.mTelephonyManager.createForSubscriptionId(defaultDataSubscriptionId);
            this.mTelephonyManager = createForSubscriptionId;
            Handler handler = this.mHandler;
            Objects.requireNonNull(handler);
            createForSubscriptionId.registerTelephonyCallback(new MediaRoute2Provider$$ExternalSyntheticLambda0(handler), this.mTelephonyCallback);
            this.mMobileDataObserver.register(this.mContext, defaultDataSubscriptionId);
            this.mConfig = getConfig(this.mContext);
        } else {
            this.mSignalStrengthListener.updateSubscriptionIds(Collections.emptySet());
        }
        updateSlice();
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        Log.d("NetworkProviderWorker", "onSubscriptionsChanged");
        updateListener();
    }

    @Override // com.android.settings.network.telephony.SignalStrengthListener.Callback
    public void onSignalStrengthChanged() {
        Log.d("NetworkProviderWorker", "onSignalStrengthChanged");
        updateSlice();
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onAirplaneModeChanged(boolean z) {
        Log.d("NetworkProviderWorker", "onAirplaneModeChanged");
        updateSlice();
    }

    @Override // com.android.settings.network.MobileDataEnabledListener.Client
    public void onMobileDataEnabledChange() {
        Log.d("NetworkProviderWorker", "onMobileDataEnabledChange");
        updateSlice();
    }

    @Override // com.android.settings.network.telephony.DataConnectivityListener.Client
    public void onDataConnectivityChange() {
        Log.d("NetworkProviderWorker", "onDataConnectivityChange");
        updateSlice();
    }

    /* loaded from: classes.dex */
    public class DataContentObserver extends ContentObserver {
        private final NetworkProviderWorker mNetworkProviderWorker;

        public DataContentObserver(Handler handler, NetworkProviderWorker networkProviderWorker) {
            super(handler);
            Log.d("NetworkProviderWorker", "DataContentObserver: init");
            this.mNetworkProviderWorker = networkProviderWorker;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            Log.d("NetworkProviderWorker", "DataContentObserver: onChange");
            this.mNetworkProviderWorker.updateSlice();
        }

        public void register(Context context, int i) {
            Uri observableUri = MobileDataContentObserver.getObservableUri(context, i);
            Log.d("NetworkProviderWorker", "DataContentObserver: register uri:" + observableUri);
            context.getContentResolver().registerContentObserver(observableUri, false, this);
        }

        public void unregister(Context context) {
            Log.d("NetworkProviderWorker", "DataContentObserver: unregister");
            context.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class NetworkProviderTelephonyCallback extends TelephonyCallback implements TelephonyCallback.DataConnectionStateListener, TelephonyCallback.DisplayInfoListener, TelephonyCallback.ServiceStateListener {
        NetworkProviderTelephonyCallback() {
        }

        @Override // android.telephony.TelephonyCallback.ServiceStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            Log.d("NetworkProviderWorker", "onServiceStateChanged voiceState=" + serviceState.getState() + " dataState=" + serviceState.getDataRegistrationState());
            NetworkProviderWorker.this.updateSlice();
        }

        @Override // android.telephony.TelephonyCallback.DisplayInfoListener
        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {
            Log.d("NetworkProviderWorker", "onDisplayInfoChanged: telephonyDisplayInfo=" + telephonyDisplayInfo);
            NetworkProviderWorker.this.mTelephonyDisplayInfo = telephonyDisplayInfo;
            NetworkProviderWorker.this.updateSlice();
        }

        @Override // android.telephony.TelephonyCallback.DataConnectionStateListener
        public void onDataConnectionStateChanged(int i, int i2) {
            Log.d("NetworkProviderWorker", "onDataConnectionStateChanged: networkType=" + i2 + " state=" + i);
            NetworkProviderWorker.this.updateSlice();
        }
    }

    int getDefaultDataSubscriptionId() {
        return SubscriptionManager.getDefaultDataSubscriptionId();
    }

    private String updateNetworkTypeName(Context context, MobileMappings.Config config, TelephonyDisplayInfo telephonyDisplayInfo, int i) {
        int i2 = MobileMappings.mapIconSets(config).get(MobileMappings.getIconKey(telephonyDisplayInfo)).dataContentDescription;
        WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
        if (wifiPickerTrackerHelper == null || !wifiPickerTrackerHelper.isCarrierNetworkActive()) {
            return i2 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i2) : "";
        }
        int i3 = TelephonyIcons.CARRIER_MERGED_WIFI.dataContentDescription;
        return i3 != 0 ? SubscriptionManager.getResourcesForSubId(context, i).getString(i3) : "";
    }

    MobileMappings.Config getConfig(Context context) {
        return MobileMappings.Config.readConfig(context);
    }

    public String getNetworkTypeDescription() {
        return updateNetworkTypeName(this.mContext, this.mConfig, this.mTelephonyDisplayInfo, this.mDefaultDataSubId);
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onInternetTypeChanged(int i) {
        int i2 = this.mInternetType;
        if (i2 == i) {
            return;
        }
        boolean z = i2 == 4 || i == 4;
        this.mInternetType = i;
        if (z) {
            updateSlice();
        }
    }

    public int getInternetType() {
        return this.mInternetType;
    }
}
