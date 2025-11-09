package com.unisoc.settings.utils;

import android.widget.Toast;
/* loaded from: classes2.dex */
public class ToastManager {
    private static Toast mToast;

    public static void setToast(Toast toast) {
        Toast toast2 = mToast;
        if (toast2 != null) {
            toast2.cancel();
        }
        mToast = toast;
    }
}
