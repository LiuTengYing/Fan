package com.syu.remote;

import android.os.RemoteException;
import android.util.SparseArray;
import com.syu.ipc.IModuleCallback;
/* loaded from: classes2.dex */
public class RemoteCallback extends IModuleCallback.Stub {
    MessageListener mListener;
    int module;
    SparseArray<Boolean> notifies = new SparseArray<>();

    /* loaded from: classes2.dex */
    public interface MessageListener {
        void onChanged(int i, Message message);
    }

    public RemoteCallback(int i) {
        this.module = i;
    }

    @Override // com.syu.ipc.IModuleCallback
    public void update(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException {
        if (this.notifies.get(i, Boolean.FALSE).booleanValue()) {
            Message message = new Message(this.module, i, iArr, fArr, strArr);
            MessageListener messageListener = this.mListener;
            if (messageListener != null) {
                messageListener.onChanged(this.module, message);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void enable(int i) {
        if (this.notifies.get(i, Boolean.FALSE).booleanValue()) {
            return;
        }
        this.notifies.put(i, Boolean.TRUE);
    }

    public void setMessageListener(MessageListener messageListener) {
        if (this.mListener != messageListener) {
            this.mListener = messageListener;
        }
    }
}
