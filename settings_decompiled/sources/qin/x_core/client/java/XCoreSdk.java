package qin.x_core.client.java;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SparseArray;
import qin.x_core.aidl.IManager;
import qin.x_core.aidl.IModule;
/* loaded from: classes2.dex */
public class XCoreSdk implements ServiceConnection, IBinder.DeathRecipient {
    String action;
    String appkey;
    Context context;
    OnInitListener initListener;
    String remotePackage;
    boolean isBound = false;
    IManager manager = null;
    boolean needInit = false;
    SparseArray<IModule> modules = new SparseArray<>();
    SparseArray<DataObserver> dataObservers = new SparseArray<>();
    boolean license = false;

    /* loaded from: classes2.dex */
    public interface OnInitListener {
        void onError(int i);

        void onInitFinish(boolean z);
    }

    public XCoreSdk(Context context, String str, String str2, String str3, OnInitListener onInitListener) {
        ApplicationInfo applicationInfo = null;
        this.appkey = "";
        this.remotePackage = "";
        this.action = "";
        this.initListener = null;
        this.context = context;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.appkey = (str == null || str.isEmpty()) ? applicationInfo.metaData.getString("xcore_appkey", "") : str;
        this.remotePackage = str2 == null ? "qin.x_core" : str2;
        this.action = str3 == null ? "qin.x_core.sdk" : str3;
        this.initListener = onInitListener;
    }

    public void initSdk() {
        this.needInit = true;
        bind();
    }

    public void deinitSdk() {
        this.needInit = false;
        if (this.isBound) {
            this.context.unbindService(this);
        }
        this.isBound = false;
    }

    public boolean isBound() {
        return this.isBound;
    }

    private void bind() {
        Intent intent = new Intent(this.action);
        intent.setPackage(this.remotePackage);
        this.context.bindService(intent, this, 1);
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (iBinder != null) {
            this.manager = IManager.Stub.asInterface(iBinder);
            try {
                iBinder.linkToDeath(this, 0);
                IManager iManager = this.manager;
                this.license = iManager == null ? false : iManager.authorize(this.context.getPackageName(), this.appkey);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        boolean z = this.manager != null;
        this.isBound = z;
        if (z) {
            this.initListener.onInitFinish(this.license);
        } else {
            this.initListener.onError(-1);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        this.isBound = false;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        this.manager.asBinder().unlinkToDeath(this, 0);
        this.manager = null;
        this.isBound = false;
        this.license = false;
        this.modules.clear();
        this.dataObservers.clear();
        if (this.needInit) {
            bind();
        }
    }

    private IModule findModule(int i) {
        if (this.modules.indexOfKey(i) < 0) {
            Bundle bundle = new Bundle();
            bundle.putString("package", this.context.getPackageName());
            bundle.putInt("module", i);
            try {
                IModule module = this.manager.getModule(bundle);
                if (module != null) {
                    this.modules.put(i, module);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return this.modules.get(i, null);
    }

    public void register(int i, int... iArr) {
        IModule findModule = findModule(i);
        if (findModule != null) {
            if (this.dataObservers.indexOfKey(i) < 0) {
                Bundle bundle = new Bundle();
                bundle.putString("package", this.context.getPackageName());
                bundle.putIntArray("flags", iArr);
                bundle.putInt("module", i);
                bundle.putInt("atonce", 1);
                DataObserver dataObserver = new DataObserver(bundle);
                this.dataObservers.put(i, dataObserver);
                try {
                    findModule.register(dataObserver, bundle);
                    return;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return;
                }
            }
            this.dataObservers.get(i).add(findModule, iArr);
        }
    }

    public void command(int i, Bundle bundle) {
        IModule findModule = findModule(i);
        if (findModule != null) {
            bundle.putString("package", this.context.getPackageName());
            try {
                findModule.command(bundle);
            } catch (RemoteException unused) {
            }
        }
    }

    public void commandValue(int i, int i2, int i3) {
        if (findModule(i) != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("module", i);
            bundle.putInt("flag", i2);
            bundle.putInt("value", i3);
            command(i, bundle);
        }
    }

    public int cmdidOf(Bundle bundle) {
        return bundle.getInt("flag", -1);
    }

    public int intOf(Bundle bundle, int i) {
        return bundle.getInt("value", i);
    }
}
