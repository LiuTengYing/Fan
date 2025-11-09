package com.android.settings.homepage.contextualcards.slices;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.android.settings.homepage.contextualcards.slices.BluetoothUpdateWorker;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
/* loaded from: classes.dex */
public class BluetoothUpdateWorker extends SliceBackgroundWorker implements BluetoothCallback {
    private static LocalBluetoothManager sLocalBluetoothManager;
    private LoadBtManagerHandler mLoadBtManagerHandler;

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    public BluetoothUpdateWorker(Context context, Uri uri) {
        super(context, uri);
        LoadBtManagerHandler loadBtManagerHandler = LoadBtManagerHandler.getInstance(context);
        this.mLoadBtManagerHandler = loadBtManagerHandler;
        if (sLocalBluetoothManager == null) {
            loadBtManagerHandler.startLoadingBtManager(this);
        }
    }

    public static void initLocalBtManager(Context context) {
        if (sLocalBluetoothManager == null) {
            LoadBtManagerHandler.getInstance(context).startLoadingBtManager();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static LocalBluetoothManager getLocalBtManager() {
        return sLocalBluetoothManager;
    }

    @Override // com.android.settings.slices.SliceBackgroundWorker
    protected void onSlicePinned() {
        LocalBluetoothManager localBtManager = this.mLoadBtManagerHandler.getLocalBtManager();
        if (localBtManager == null) {
            return;
        }
        localBtManager.getEventManager().registerCallback(this);
    }

    @Override // com.android.settings.slices.SliceBackgroundWorker
    protected void onSliceUnpinned() {
        LocalBluetoothManager localBtManager = this.mLoadBtManagerHandler.getLocalBtManager();
        if (localBtManager == null) {
            return;
        }
        localBtManager.getEventManager().unregisterCallback(this);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onBluetoothStateChanged(int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        notifySliceChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LoadBtManagerHandler extends Handler {
        private static LoadBtManagerHandler sHandler;
        private final Context mContext;
        private final Runnable mLoadBtManagerTask;
        private BluetoothUpdateWorker mWorker;

        /* JADX INFO: Access modifiers changed from: private */
        public static LoadBtManagerHandler getInstance(Context context) {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("BluetoothUpdateWorker", 10);
                handlerThread.start();
                sHandler = new LoadBtManagerHandler(context, handlerThread.getLooper());
            }
            return sHandler;
        }

        private LoadBtManagerHandler(Context context, Looper looper) {
            super(looper);
            this.mContext = context;
            this.mLoadBtManagerTask = new Runnable() { // from class: com.android.settings.homepage.contextualcards.slices.BluetoothUpdateWorker$LoadBtManagerHandler$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BluetoothUpdateWorker.LoadBtManagerHandler.this.lambda$new$0();
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            Log.d("BluetoothUpdateWorker", "LoadBtManagerHandler: start loading...");
            long currentTimeMillis = System.currentTimeMillis();
            BluetoothUpdateWorker.sLocalBluetoothManager = getLocalBtManager();
            Log.d("BluetoothUpdateWorker", "LoadBtManagerHandler took " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public LocalBluetoothManager getLocalBtManager() {
            if (BluetoothUpdateWorker.sLocalBluetoothManager != null) {
                return BluetoothUpdateWorker.sLocalBluetoothManager;
            }
            return LocalBluetoothManager.getInstance(this.mContext, new LocalBluetoothManager.BluetoothManagerCallback() { // from class: com.android.settings.homepage.contextualcards.slices.BluetoothUpdateWorker$LoadBtManagerHandler$$ExternalSyntheticLambda1
                @Override // com.android.settingslib.bluetooth.LocalBluetoothManager.BluetoothManagerCallback
                public final void onBluetoothManagerInitialized(Context context, LocalBluetoothManager localBluetoothManager) {
                    BluetoothUpdateWorker.LoadBtManagerHandler.this.lambda$getLocalBtManager$1(context, localBluetoothManager);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getLocalBtManager$1(Context context, LocalBluetoothManager localBluetoothManager) {
            BluetoothUpdateWorker bluetoothUpdateWorker = this.mWorker;
            if (bluetoothUpdateWorker != null) {
                bluetoothUpdateWorker.notifySliceChange();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startLoadingBtManager() {
            if (hasCallbacks(this.mLoadBtManagerTask)) {
                return;
            }
            post(this.mLoadBtManagerTask);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startLoadingBtManager(BluetoothUpdateWorker bluetoothUpdateWorker) {
            this.mWorker = bluetoothUpdateWorker;
            startLoadingBtManager();
        }
    }
}
