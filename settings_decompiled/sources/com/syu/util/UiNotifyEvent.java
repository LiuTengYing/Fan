package com.syu.util;

import android.os.Handler;
import android.os.Looper;
/* loaded from: classes2.dex */
public class UiNotifyEvent {
    public static Handler HANDLER_UI = new Handler(Looper.getMainLooper());
    public IUiNotify uiNotify = null;

    public void updateNotify(int i, int[] iArr, float[] fArr, String[] strArr) {
        NotifyData notifyData = new NotifyData(i, iArr, fArr, strArr);
        if (Looper.getMainLooper() == Looper.myLooper()) {
            notifyData.run();
        } else {
            HANDLER_UI.post(notifyData);
        }
    }

    /* loaded from: classes2.dex */
    private class NotifyData implements Runnable {
        public float[] flts;
        public int[] ints;
        public String[] strs;
        public int updateCode;

        public NotifyData(int i, int[] iArr, float[] fArr, String[] strArr) {
            this.updateCode = i;
            this.ints = iArr;
            this.flts = fArr;
            this.strs = strArr;
        }

        @Override // java.lang.Runnable
        public void run() {
            IUiNotify iUiNotify = UiNotifyEvent.this.uiNotify;
            if (iUiNotify != null) {
                iUiNotify.onNotify(this.updateCode, this.ints, this.flts, this.strs);
            }
        }
    }
}
