package com.syu.util;

import android.os.RemoteException;
import com.syu.ipc.IModuleCallback;
/* loaded from: classes2.dex */
public class UiNotifyEventString extends UiNotifyEvent {
    private String mValue = "";
    private IModuleCallback mCallback = new IModuleCallback.Stub() { // from class: com.syu.util.UiNotifyEventString.1
        @Override // com.syu.ipc.IModuleCallback
        public void update(int i, int[] iArr, float[] fArr, String[] strArr) throws RemoteException {
            if (strArr == null || strArr.length <= 0) {
                return;
            }
            String str = strArr[0];
            if (str == null) {
                if (UiNotifyEventString.this.mValue != null) {
                    UiNotifyEventString.this.mValue = str;
                    UiNotifyEventString.this.updateNotify(i, iArr, fArr, strArr);
                }
            } else if (str.equals(UiNotifyEventString.this.mValue)) {
            } else {
                UiNotifyEventString.this.mValue = str;
                UiNotifyEventString.this.updateNotify(i, iArr, fArr, strArr);
            }
        }
    };
}
