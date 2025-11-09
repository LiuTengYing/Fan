package com.syu.ipcself;

import android.os.Handler;
import android.os.HandlerThread;
import com.syu.ipc.IModuleCallback;
import com.syu.ipc.IRemoteModule;
import com.syu.ipc.ModuleObject;
/* loaded from: classes2.dex */
public class ModuleProxy extends IRemoteModule.Stub {
    public static Handler mHandlerCmd;
    public static HandlerThread mHandlerThreadCmd;
    private IRemoteModule mRemoteModule;

    static {
        HandlerThread handlerThread = new HandlerThread("ModuleProxy-CMD-Thread");
        mHandlerThreadCmd = handlerThread;
        handlerThread.start();
        mHandlerCmd = new Handler(mHandlerThreadCmd.getLooper());
    }

    /* loaded from: classes2.dex */
    public class Runnable_Cmd implements Runnable {
        int cmdCode;
        float[] flts;
        int[] ints;
        String[] strs;

        public Runnable_Cmd(int i, int[] iArr, float[] fArr, String[] strArr) {
            this.cmdCode = i;
            this.ints = iArr;
            this.flts = fArr;
            this.strs = strArr;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (ModuleProxy.this.mRemoteModule != null) {
                    ModuleProxy.this.mRemoteModule.cmd(this.cmdCode, this.ints, this.flts, this.strs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.syu.ipc.IRemoteModule
    public void cmd(int i, int[] iArr, float[] fArr, String[] strArr) {
        Handler handler = mHandlerCmd;
        if (handler != null) {
            handler.post(new Runnable_Cmd(i, iArr, fArr, strArr));
        }
    }

    @Override // com.syu.ipc.IRemoteModule
    public ModuleObject get(int i, int[] iArr, float[] fArr, String[] strArr) {
        IRemoteModule iRemoteModule = this.mRemoteModule;
        if (iRemoteModule != null) {
            try {
                return iRemoteModule.get(i, iArr, fArr, strArr);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override // com.syu.ipc.IRemoteModule
    public void register(IModuleCallback iModuleCallback, int i, int i2) {
        IRemoteModule iRemoteModule = this.mRemoteModule;
        if (iRemoteModule != null) {
            try {
                iRemoteModule.register(iModuleCallback, i, i2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.syu.ipc.IRemoteModule
    public void unregister(IModuleCallback iModuleCallback, int i) {
        IRemoteModule iRemoteModule = this.mRemoteModule;
        if (iRemoteModule != null) {
            try {
                iRemoteModule.unregister(iModuleCallback, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
