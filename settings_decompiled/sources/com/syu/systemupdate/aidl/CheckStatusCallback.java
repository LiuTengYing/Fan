package com.syu.systemupdate.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes2.dex */
public interface CheckStatusCallback extends IInterface {
    void cancelDownload() throws RemoteException;

    void clearData() throws RemoteException;

    int getCheckTime() throws RemoteException;

    NewVersionInfo getNewVersionInfo() throws RemoteException;

    boolean getSilentUpdate() throws RemoteException;

    String getStatus() throws RemoteException;

    boolean getWifiDownload() throws RemoteException;

    void registerCallBack(UpdateStatusListener updateStatusListener) throws RemoteException;

    void removeListener(UpdateStatusListener updateStatusListener) throws RemoteException;

    void setCheckTime(int i) throws RemoteException;

    void setProductId(int i) throws RemoteException;

    void setSlientDownload(boolean z) throws RemoteException;

    void setWifiDownload(boolean z) throws RemoteException;

    void startCheck() throws RemoteException;

    void startDownload() throws RemoteException;

    void startUpgrade() throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements CheckStatusCallback {
        public static CheckStatusCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.syu.systemupdate.aidl.CheckStatusCallback");
            if (queryLocalInterface != null && (queryLocalInterface instanceof CheckStatusCallback)) {
                return (CheckStatusCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements CheckStatusCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void startCheck() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void startDownload() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void cancelDownload() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void startUpgrade() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public String getStatus() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void setProductId(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    obtain.writeInt(i);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void registerCallBack(UpdateStatusListener updateStatusListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    obtain.writeStrongInterface(updateStatusListener);
                    this.mRemote.transact(8, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public NewVersionInfo getNewVersionInfo() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return (NewVersionInfo) obtain2.readTypedObject(NewVersionInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void setWifiDownload(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    obtain.writeBoolean(z);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void setSlientDownload(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    obtain.writeBoolean(z);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void setCheckTime(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    obtain.writeInt(i);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void clearData() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public boolean getWifiDownload() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public boolean getSilentUpdate() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readBoolean();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public int getCheckTime() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.syu.systemupdate.aidl.CheckStatusCallback
            public void removeListener(UpdateStatusListener updateStatusListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.syu.systemupdate.aidl.CheckStatusCallback");
                    obtain.writeStrongInterface(updateStatusListener);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
