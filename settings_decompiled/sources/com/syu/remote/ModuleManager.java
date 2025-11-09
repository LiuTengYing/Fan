package com.syu.remote;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;
import com.syu.remote.Remote;
import com.syu.utils.Utils;
/* loaded from: classes2.dex */
public class ModuleManager implements Remote.OnConnectionListener {
    public Handler mHandler;
    SparseArray<int[]> moduleCodes;
    public Remote remote;
    private String servicePkg;

    public ModuleManager(Context context) {
        this(context, null, null);
    }

    public ModuleManager(Context context, String str, String str2) {
        this.servicePkg = null;
        this.moduleCodes = new SparseArray<>();
        this.mHandler = null;
        HandlerThread handlerThread = new HandlerThread("ModuleManager_thread");
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
        this.servicePkg = Utils.isEmptyStr(str) ? "com.syu.ms" : str;
        if (!Utils.isEmptyStr(str) && !Utils.isEmptyStr(str2)) {
            this.remote = Remote.getAutoTools(context, this, str2, str);
        } else {
            this.remote = Remote.getAutoTools(context, this);
        }
    }

    @Override // com.syu.remote.Remote.OnConnectionListener
    public void onConnected(String str, boolean z) {
        if (Utils.equals(str, this.servicePkg) && z && this.moduleCodes.size() > 0) {
            for (int i = 0; i < this.moduleCodes.size(); i++) {
                int keyAt = this.moduleCodes.keyAt(i);
                int[] iArr = this.moduleCodes.get(keyAt, null);
                if (iArr != null) {
                    this.remote.observe(keyAt, iArr);
                }
            }
        }
    }

    public boolean isConnected() {
        Remote remote = this.remote;
        if (remote == null) {
            return false;
        }
        return remote.isConnected();
    }

    public boolean observeModule(int i, int... iArr) {
        this.moduleCodes.put(i, iArr);
        boolean isConnected = isConnected();
        if (isConnected) {
            this.remote.observe(i, iArr);
        }
        return isConnected;
    }

    public boolean addObserver(MessageObserver messageObserver) {
        this.remote.addMessageObserver(messageObserver);
        return isConnected();
    }

    public boolean removeObserver(MessageObserver messageObserver) {
        this.remote.removeMessageObserver(messageObserver);
        return isConnected();
    }

    public boolean sendCmd(int i, int i2, int... iArr) {
        if (isConnected()) {
            return this.remote.command(i, i2, iArr);
        }
        return false;
    }

    public boolean sendCmd(int i, int i2, int[] iArr, float[] fArr, String[] strArr) {
        if (isConnected()) {
            return this.remote.command(i, i2, iArr, fArr, strArr);
        }
        return false;
    }
}
