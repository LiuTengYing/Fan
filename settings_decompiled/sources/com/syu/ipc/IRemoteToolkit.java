package com.syu.ipc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.syu.ipc.IRemoteModule;
/* loaded from: classes2.dex */
public interface IRemoteToolkit extends IInterface {
    IRemoteModule getRemoteModule(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRemoteToolkit {
        public static IRemoteToolkit asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.syu.ipc.IRemoteToolkit");
            if (queryLocalInterface != null && (queryLocalInterface instanceof IRemoteToolkit)) {
                return (IRemoteToolkit) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRemoteToolkit {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.syu.ipc.IRemoteToolkit
            public IRemoteModule getRemoteModule(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.ipc.IRemoteToolkit");
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return IRemoteModule.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
