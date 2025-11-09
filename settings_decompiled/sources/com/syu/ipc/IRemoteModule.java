package com.syu.ipc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.syu.ipc.IModuleCallback;
/* loaded from: classes2.dex */
public interface IRemoteModule extends IInterface {
    void cmd(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException;

    ModuleObject get(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException;

    void register(IModuleCallback iModuleCallback, int i, int i2) throws RemoteException;

    void unregister(IModuleCallback iModuleCallback, int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRemoteModule {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.syu.ipc.IRemoteModule");
        }

        public static IRemoteModule asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.syu.ipc.IRemoteModule");
            if (queryLocalInterface != null && (queryLocalInterface instanceof IRemoteModule)) {
                return (IRemoteModule) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface("com.syu.ipc.IRemoteModule");
                cmd(parcel.readInt(), parcel.createIntArray(), parcel.createFloatArray(), parcel.createStringArray());
                return true;
            } else if (i == 2) {
                parcel.enforceInterface("com.syu.ipc.IRemoteModule");
                ModuleObject moduleObject = get(parcel.readInt(), parcel.createIntArray(), parcel.createFloatArray(), parcel.createStringArray());
                parcel2.writeNoException();
                if (moduleObject != null) {
                    parcel2.writeInt(1);
                    parcel2.writeIntArray(moduleObject.ints);
                    parcel2.writeFloatArray(moduleObject.flts);
                    parcel2.writeStringArray(moduleObject.strs);
                } else {
                    parcel2.writeInt(0);
                }
                return true;
            } else if (i == 3) {
                parcel.enforceInterface("com.syu.ipc.IRemoteModule");
                register(IModuleCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt(), parcel.readInt());
                return true;
            } else if (i == 4) {
                parcel.enforceInterface("com.syu.ipc.IRemoteModule");
                unregister(IModuleCallback.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                return true;
            } else if (i == 1598968902) {
                parcel2.writeString("com.syu.ipc.IRemoteModule");
                return true;
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRemoteModule {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.syu.ipc.IRemoteModule
            public void cmd(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.ipc.IRemoteModule");
                    obtain.writeInt(i);
                    obtain.writeIntArray(iArr);
                    obtain.writeFloatArray(fArr);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(1, obtain, obtain2, 1);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.ipc.IRemoteModule
            public ModuleObject get(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException {
                ModuleObject moduleObject;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.ipc.IRemoteModule");
                    obtain.writeInt(i);
                    obtain.writeIntArray(iArr);
                    obtain.writeFloatArray(fArr);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        moduleObject = new ModuleObject();
                        moduleObject.ints = obtain2.createIntArray();
                        moduleObject.flts = obtain2.createFloatArray();
                        moduleObject.strs = obtain2.createStringArray();
                    } else {
                        moduleObject = null;
                    }
                    return moduleObject;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.ipc.IRemoteModule
            public void register(IModuleCallback iModuleCallback, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.ipc.IRemoteModule");
                    obtain.writeStrongBinder(iModuleCallback != null ? iModuleCallback.asBinder() : null);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(3, obtain, obtain2, 1);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.ipc.IRemoteModule
            public void unregister(IModuleCallback iModuleCallback, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.ipc.IRemoteModule");
                    obtain.writeStrongBinder(iModuleCallback != null ? iModuleCallback.asBinder() : null);
                    obtain.writeInt(i);
                    this.mRemote.transact(4, obtain, obtain2, 1);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
