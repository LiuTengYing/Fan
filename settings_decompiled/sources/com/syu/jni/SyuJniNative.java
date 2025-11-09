package com.syu.jni;

import android.os.Bundle;
/* loaded from: classes2.dex */
public class SyuJniNative {
    private static final SyuJniNative INSTANCE = new SyuJniNative();

    public native int syu_jni_command(int i, Object obj, Object obj2);

    static {
        System.loadLibrary("syu_jni");
    }

    private SyuJniNative() {
    }

    public static SyuJniNative getInstance() {
        return INSTANCE;
    }

    public byte[] cmd_148_read_data(int i, int i2) {
        Bundle bundle = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle.putInt("offset", i2);
        bundle.putInt("readsize", i);
        if (getInstance().syu_jni_command(148, bundle, bundle2) == 0) {
            return bundle2.getByteArray("appdata");
        }
        return null;
    }
}
