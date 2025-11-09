package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.widget.SwitchWidgetController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class TetherEnabler implements SwitchWidgetController.OnSwitchChangeListener, DataSaverBackend.Listener, LifecycleObserver {
    private static final boolean DEBUG = Log.isLoggable("TetherEnabler", 3);
    private final BluetoothAdapter mBluetoothAdapter;
    private boolean mBluetoothEnableForTether;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    @VisibleForTesting
    boolean mBluetoothTetheringStoppedByUser;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private final EthernetManager mEthernetManager;
    @VisibleForTesting
    final List<OnTetherStateUpdateListener> mListeners;
    private final Handler mMainThreadHandler;
    @VisibleForTesting
    ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback;
    private final SwitchWidgetController mSwitchWidgetController;
    @VisibleForTesting
    TetheringManager.TetheringEventCallback mTetheringEventCallback;
    private final TetheringManager mTetheringManager;
    private final UserManager mUserManager;
    private final WifiManager mWifiManager;
    private final EthernetManager.InterfaceStateListener mEthernetListener = new EthernetListener();
    private final ConcurrentHashMap<String, IpConfiguration> mAvailableInterfaces = new ConcurrentHashMap<>();
    private final BroadcastReceiver mTetherChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.TetherEnabler.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean handleBluetoothStateChanged;
            String action = intent.getAction();
            if (TextUtils.equals("android.net.wifi.WIFI_AP_STATE_CHANGED", action)) {
                handleBluetoothStateChanged = TetherEnabler.this.handleWifiApStateChanged(intent.getIntExtra("wifi_state", 14));
            } else {
                handleBluetoothStateChanged = TextUtils.equals("android.bluetooth.adapter.action.STATE_CHANGED", action) ? TetherEnabler.this.handleBluetoothStateChanged(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE)) : false;
            }
            if (handleBluetoothStateChanged) {
                TetherEnabler.this.updateState(null);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface OnTetherStateUpdateListener {
        void onTetherStateUpdated(int i);
    }

    public static boolean isTethering(int i, int i2) {
        return (i & (1 << i2)) != 0;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    /* loaded from: classes.dex */
    private final class EthernetListener implements EthernetManager.InterfaceStateListener {
        private EthernetListener() {
        }

        public void onInterfaceStateChanged(String str, int i, int i2, IpConfiguration ipConfiguration) {
            if (i == 2) {
                TetherEnabler.this.mAvailableInterfaces.put(str, ipConfiguration);
            } else {
                TetherEnabler.this.mAvailableInterfaces.remove(str, ipConfiguration);
            }
        }
    }

    public TetherEnabler(Context context, SwitchWidgetController switchWidgetController, AtomicReference<BluetoothPan> atomicReference) {
        this.mContext = context;
        this.mSwitchWidgetController = switchWidgetController;
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mTetheringManager = (TetheringManager) context.getSystemService("tethering");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothPan = atomicReference;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mListeners = new ArrayList();
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mEthernetManager = (EthernetManager) context.getSystemService(EthernetManager.class);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mDataSaverBackend.addListener(this);
        this.mSwitchWidgetController.setListener(this);
        this.mSwitchWidgetController.startListening();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        this.mTetheringEventCallback = new TetheringManager.TetheringEventCallback() { // from class: com.android.settings.network.TetherEnabler.1
            public void onTetheredInterfacesChanged(List<String> list) {
                TetherEnabler.this.updateState((String[]) list.toArray(new String[list.size()]));
            }
        };
        this.mTetheringManager.registerTetheringEventCallback(new HandlerExecutor(this.mMainThreadHandler), this.mTetheringEventCallback);
        this.mOnStartTetheringCallback = new OnStartTetheringCallback(this);
        updateState(null);
        EthernetManager ethernetManager = this.mEthernetManager;
        if (ethernetManager != null) {
            ethernetManager.addInterfaceStateListener(new Executor() { // from class: com.android.settings.network.TetherEnabler$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    TetherEnabler.this.lambda$onStart$0(runnable);
                }
            }, this.mEthernetListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(Runnable runnable) {
        this.mMainThreadHandler.post(runnable);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mBluetoothTetheringStoppedByUser = false;
        this.mDataSaverBackend.remListener(this);
        this.mSwitchWidgetController.stopListening();
        this.mContext.unregisterReceiver(this.mTetherChangeReceiver);
        this.mTetheringManager.unregisterTetheringEventCallback(this.mTetheringEventCallback);
        this.mTetheringEventCallback = null;
        EthernetManager ethernetManager = this.mEthernetManager;
        if (ethernetManager != null) {
            ethernetManager.removeInterfaceStateListener(this.mEthernetListener);
        }
    }

    public void addListener(OnTetherStateUpdateListener onTetherStateUpdateListener) {
        if (onTetherStateUpdateListener == null || this.mListeners.contains(onTetherStateUpdateListener)) {
            return;
        }
        onTetherStateUpdateListener.onTetherStateUpdated(getTetheringState(null));
        this.mListeners.add(onTetherStateUpdateListener);
    }

    public void removeListener(OnTetherStateUpdateListener onTetherStateUpdateListener) {
        if (onTetherStateUpdateListener != null) {
            this.mListeners.remove(onTetherStateUpdateListener);
        }
    }

    private void setSwitchEnabled(boolean z) {
        this.mSwitchWidgetController.setEnabled(z && !this.mDataSaverEnabled && this.mUserManager.isAdminUser());
    }

    @VisibleForTesting
    void updateState(String[] strArr) {
        int tetheringState = getTetheringState(strArr);
        if (DEBUG) {
            Log.d("TetherEnabler", "updateState: " + tetheringState);
        }
        setSwitchCheckedInternal(tetheringState != 0);
        setSwitchEnabled(true);
        int size = this.mListeners.size();
        for (int i = 0; i < size; i++) {
            this.mListeners.get(i).onTetherStateUpdated(tetheringState);
        }
    }

    private void setSwitchCheckedInternal(boolean z) {
        try {
            this.mSwitchWidgetController.stopListening();
            this.mSwitchWidgetController.setChecked(z);
            this.mSwitchWidgetController.startListening();
        } catch (IllegalStateException unused) {
            Log.e("TetherEnabler", "failed to stop switch widget listener when set check internally");
        }
    }

    @VisibleForTesting
    int getTetheringState(String[] strArr) {
        if (strArr == null) {
            strArr = this.mTetheringManager.getTetheredIfaces();
        }
        WifiManager wifiManager = this.mWifiManager;
        int i = (wifiManager == null || !wifiManager.isWifiApEnabled()) ? 0 : 1;
        if (!this.mBluetoothTetheringStoppedByUser) {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
            if (bluetoothAdapter != null && bluetoothAdapter.getState() == 12 && bluetoothPan != null && bluetoothPan.isTetheringOn()) {
                i |= 4;
            }
        }
        String[] tetherableUsbRegexs = this.mTetheringManager.getTetherableUsbRegexs();
        for (String str : strArr) {
            for (String str2 : tetherableUsbRegexs) {
                if (str.matches(str2)) {
                    i |= 2;
                }
            }
            if (this.mAvailableInterfaces.containsKey(str)) {
                i |= 32;
            }
        }
        return i;
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (z) {
            startTethering(0);
        } else {
            stopTethering(1);
            stopTethering(0);
            stopTethering(2);
            stopTethering(5);
        }
        return true;
    }

    public void stopTethering(int i) {
        if (isTethering(getTetheringState(null), i)) {
            setSwitchEnabled(false);
            this.mConnectivityManager.stopTethering(i);
            if (i == 2) {
                this.mBluetoothTetheringStoppedByUser = true;
                updateState(null);
            }
        }
    }

    public void startTethering(int i) {
        BluetoothAdapter bluetoothAdapter;
        if (i == 2) {
            this.mBluetoothTetheringStoppedByUser = false;
        }
        if (isTethering(getTetheringState(null), i)) {
            return;
        }
        if (i == 2 && (bluetoothAdapter = this.mBluetoothAdapter) != null && bluetoothAdapter.getState() == 10) {
            if (DEBUG) {
                Log.d("TetherEnabler", "Turn on bluetooth first.");
            }
            this.mBluetoothEnableForTether = true;
            this.mBluetoothAdapter.enable();
            return;
        }
        setSwitchEnabled(false);
        this.mConnectivityManager.startTethering(i, true, this.mOnStartTetheringCallback, this.mMainThreadHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleBluetoothStateChanged(int i) {
        if (i != Integer.MIN_VALUE && i != 10) {
            if (i != 12) {
                return false;
            }
            if (this.mBluetoothEnableForTether) {
                startTethering(2);
            }
        }
        this.mBluetoothEnableForTether = false;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleWifiApStateChanged(int i) {
        if (i == 11 || i == 13) {
            return true;
        }
        if (i != 14) {
            return false;
        }
        Log.e("TetherEnabler", "Wifi AP is failed!");
        return true;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        setSwitchEnabled(true);
    }

    /* loaded from: classes.dex */
    private static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<TetherEnabler> mTetherEnabler;

        OnStartTetheringCallback(TetherEnabler tetherEnabler) {
            this.mTetherEnabler = new WeakReference<>(tetherEnabler);
        }

        public void onTetheringStarted() {
            update();
        }

        public void onTetheringFailed() {
            update();
        }

        private void update() {
            TetherEnabler tetherEnabler = this.mTetherEnabler.get();
            if (tetherEnabler != null) {
                tetherEnabler.updateState(null);
            }
        }
    }
}
