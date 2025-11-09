package com.syu.systemupdate.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
/* loaded from: classes2.dex */
public interface UpdateStatusListener extends IInterface {
    void basicTypes(int i, long j, boolean z, float f, double d, String str) throws RemoteException;

    void enterRecoveryFail(int i) throws RemoteException;

    void onCheckFailed(int i) throws RemoteException;

    void onCheckSucess(NewVersionInfo newVersionInfo) throws RemoteException;

    void onDownloadFailed(int i) throws RemoteException;

    void onDownloadSuccess() throws RemoteException;

    void updateDownloadProgress(int i) throws RemoteException;

    void upgradeProgress(int i) throws RemoteException;

    void upgradeSuccess() throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements UpdateStatusListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.syu.systemupdate.aidl.UpdateStatusListener");
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface("com.syu.systemupdate.aidl.UpdateStatusListener");
            }
            if (i == 1598968902) {
                parcel2.writeString("com.syu.systemupdate.aidl.UpdateStatusListener");
                return true;
            }
            switch (i) {
                case 1:
                    int readInt = parcel.readInt();
                    long readLong = parcel.readLong();
                    boolean readBoolean = parcel.readBoolean();
                    float readFloat = parcel.readFloat();
                    double readDouble = parcel.readDouble();
                    String readString = parcel.readString();
                    parcel.enforceNoDataAvail();
                    basicTypes(readInt, readLong, readBoolean, readFloat, readDouble, readString);
                    parcel2.writeNoException();
                    break;
                case 2:
                    NewVersionInfo newVersionInfo = (NewVersionInfo) parcel.readTypedObject(NewVersionInfo.CREATOR);
                    parcel.enforceNoDataAvail();
                    onCheckSucess(newVersionInfo);
                    parcel2.writeNoException();
                    parcel2.writeTypedObject(newVersionInfo, 1);
                    break;
                case 3:
                    int readInt2 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onCheckFailed(readInt2);
                    parcel2.writeNoException();
                    break;
                case 4:
                    int readInt3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    updateDownloadProgress(readInt3);
                    parcel2.writeNoException();
                    break;
                case 5:
                    onDownloadSuccess();
                    parcel2.writeNoException();
                    break;
                case 6:
                    int readInt4 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onDownloadFailed(readInt4);
                    parcel2.writeNoException();
                    break;
                case 7:
                    int readInt5 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    enterRecoveryFail(readInt5);
                    parcel2.writeNoException();
                    break;
                case 8:
                    int readInt6 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    upgradeProgress(readInt6);
                    parcel2.writeNoException();
                    break;
                case 9:
                    upgradeSuccess();
                    parcel2.writeNoException();
                    break;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }
    }
}
