package com.syu.remote;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.syu.ipc.IRemoteModule;
import com.syu.ipc.IRemoteToolkit;
import com.syu.remote.RemoteCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes2.dex */
public class Remote implements ServiceConnection, IBinder.DeathRecipient, RemoteCallback.MessageListener {
    static final HashMap<String, Remote> mTools = new HashMap<>();
    String action;
    Looper looper;
    Context mContext;
    Handler mHandler;
    IRemoteToolkit mToolkit;
    String pkgName;
    final int CONN_DELAY_TIME_MAX = 1500;
    final int CONN_DELAY_TIME_MIN = 500;
    SparseArray<Module> mModules = new SparseArray<>();
    SparseArray<RemoteCallback> mCallbacks = new SparseArray<>();
    boolean autoConn = true;
    List<MessageObserver> mObservers = new ArrayList();
    List<OnConnectionListener> connectionListeners = new ArrayList();
    ExecutorService executor = Executors.newFixedThreadPool(2);

    /* loaded from: classes2.dex */
    public interface OnConnectionListener {
        void onConnected(String str, boolean z);
    }

    @Override // com.syu.remote.RemoteCallback.MessageListener
    public synchronized void onChanged(int i, Message message) {
        for (MessageObserver messageObserver : this.mObservers) {
            if (messageObserver.isListening(i)) {
                messageObserver.onReceiver(message);
            }
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        IRemoteToolkit iRemoteToolkit = this.mToolkit;
        if (iRemoteToolkit != null) {
            iRemoteToolkit.asBinder().unlinkToDeath(this, 0);
        }
        this.mModules.clear();
        this.mToolkit = null;
        if (this.autoConn) {
            bind();
        }
    }

    public static Remote getAutoTools(Context context, OnConnectionListener onConnectionListener) {
        return getAutoTools(context, onConnectionListener, "com.syu.ms.toolkit", "com.syu.ms");
    }

    public static Remote getAutoTools(Context context, OnConnectionListener onConnectionListener, String str, String str2) {
        String str3 = String.valueOf(str2) + "#" + str;
        HashMap<String, Remote> hashMap = mTools;
        if (hashMap.containsKey(str3)) {
            return hashMap.get(str3);
        }
        Remote remote = new Remote(context, onConnectionListener, str, str2);
        hashMap.put(str3, remote);
        return remote;
    }

    Remote(Context context, OnConnectionListener onConnectionListener, String str, String str2) {
        this.mContext = context;
        this.action = str;
        this.pkgName = str2;
        HandlerThread handlerThread = new HandlerThread("remote connection");
        handlerThread.start();
        this.looper = handlerThread.getLooper();
        this.mHandler = new Handler(this.looper);
        this.connectionListeners.add(onConnectionListener);
        bind();
    }

    void bind() {
        Intent intent = new Intent(this.action);
        this.autoConn = true;
        intent.setPackage(this.pkgName);
        Log.e("Remote", "intent: " + intent.toString() + " action: " + this.action + " pkg: " + this.pkgName);
        this.mContext.bindService(intent, this, 1);
        this.mHandler.postDelayed(new Runnable() { // from class: com.syu.remote.Remote.1
            @Override // java.lang.Runnable
            public void run() {
                Remote remote = Remote.this;
                if (remote.autoConn && remote.mToolkit == null) {
                    remote.bind();
                }
                Remote.this.mHandler.removeCallbacks(this);
            }
        }, (long) (new Random().nextInt(1500) + 500));
    }

    Module module(int i) {
        return this.mModules.get(i, null);
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder != null) {
            try {
                iBinder.linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
            IRemoteToolkit asInterface = IRemoteToolkit.Stub.asInterface(iBinder);
            this.mToolkit = asInterface;
            if (asInterface == null) {
                return;
            }
            for (int i = 0; i < 20; i++) {
                IRemoteModule iRemoteModule = null;
                try {
                    iRemoteModule = this.mToolkit.getRemoteModule(i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (iRemoteModule != null) {
                    RemoteCallback remoteCallback = this.mCallbacks.get(i, new RemoteCallback(i));
                    remoteCallback.setMessageListener(this);
                    Module module = new Module(iRemoteModule, remoteCallback);
                    this.mModules.put(i, module);
                    module.register();
                }
            }
            for (OnConnectionListener onConnectionListener : this.connectionListeners) {
                onConnectionListener.onConnected(this.pkgName, true);
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.mToolkit = null;
        for (OnConnectionListener onConnectionListener : this.connectionListeners) {
            onConnectionListener.onConnected(this.pkgName, false);
        }
    }

    public void addMessageObserver(MessageObserver messageObserver) {
        if (this.mObservers.contains(messageObserver)) {
            return;
        }
        this.mObservers.add(messageObserver);
    }

    public void removeMessageObserver(MessageObserver messageObserver) {
        if (this.mObservers.contains(messageObserver)) {
            this.mObservers.remove(messageObserver);
        }
    }

    public void observe(int i, int... iArr) {
        Module module = module(i);
        if (module != null) {
            module.observe(iArr);
        }
    }

    public boolean command(int i, int i2, int[] iArr, float[] fArr, String[] strArr) {
        return handCommand(new Command(i, i2, iArr, fArr, strArr));
    }

    public boolean command(int i, int i2, int... iArr) {
        return handCommand(new Command(i, i2, iArr, null, null));
    }

    public boolean handCommand(final Command command) {
        if (isConnected()) {
            this.executor.submit(new Runnable() { // from class: com.syu.remote.Remote.2
                @Override // java.lang.Runnable
                public void run() {
                    Module module = Remote.this.module(command.module);
                    if (module != null) {
                        Command command2 = command;
                        module.command(command2.cmdid, command2.ints, command2.flts, command2.strs);
                    }
                }
            });
            return isConnected();
        }
        return false;
    }

    public boolean isConnected() {
        return this.mToolkit != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class Command {
        int cmdid;
        float[] flts;
        int[] ints;
        int module;
        String[] strs;

        public Command(int i, int i2, int[] iArr, float[] fArr, String[] strArr) {
            this.module = i;
            this.cmdid = i2;
            this.ints = iArr;
            this.flts = fArr;
            this.strs = strArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public class Module {
        RemoteCallback callback;
        IRemoteModule module;

        public Module(IRemoteModule iRemoteModule, RemoteCallback remoteCallback) {
            this.module = iRemoteModule;
            this.callback = remoteCallback;
        }

        void register() {
            SparseArray<Boolean> clone = this.callback.notifies.clone();
            int size = clone.size();
            for (int i = 0; i < size; i++) {
                try {
                    this.module.register(this.callback, clone.keyAt(i), clone.valueAt(i).booleanValue() ? 1 : 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        void observe(int... iArr) {
            if (iArr == null) {
                return;
            }
            for (int i : iArr) {
                this.callback.enable(i);
                try {
                    this.module.register(this.callback, i, 1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        void command(int i, int[] iArr, float[] fArr, String[] strArr) {
            try {
                this.module.cmd(i, iArr, fArr, strArr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
