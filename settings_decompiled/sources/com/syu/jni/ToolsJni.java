package com.syu.jni;

import android.os.Bundle;
/* loaded from: classes2.dex */
public class ToolsJni {
    public static int cmd_12_get_usb_speed() {
        SyuJniNative syuJniNative = SyuJniNative.getInstance();
        if (syuJniNative != null) {
            Bundle bundle = new Bundle();
            syuJniNative.syu_jni_command(12, null, bundle);
            return bundle.getInt("param0", 0);
        }
        return 0;
    }

    public static int cmd_13_write_usb_speed(int i) {
        SyuJniNative syuJniNative = SyuJniNative.getInstance();
        if (syuJniNative != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("param0", i);
            return syuJniNative.syu_jni_command(13, bundle, null);
        }
        return 0;
    }

    public static int cmd_105_set_bl_adj(int i) {
        SyuJniNative syuJniNative = SyuJniNative.getInstance();
        if (syuJniNative != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("param0", i);
            return syuJniNative.syu_jni_command(105, bundle, null);
        }
        return 0;
    }

    public static int[] cmd_109_get_bl_limit() {
        int[] iArr = new int[3];
        SyuJniNative syuJniNative = SyuJniNative.getInstance();
        if (syuJniNative != null) {
            Bundle bundle = new Bundle();
            syuJniNative.syu_jni_command(109, null, bundle);
            iArr[0] = bundle.getInt("param0", 0);
            iArr[1] = bundle.getInt("param1", 0);
            iArr[2] = bundle.getInt("param2", 0);
        }
        return iArr;
    }

    public static int cmd_110_set_bl_limit(int i, int i2) {
        SyuJniNative syuJniNative = SyuJniNative.getInstance();
        if (syuJniNative != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("param0", i);
            bundle.putInt("param1", i2);
            return syuJniNative.syu_jni_command(110, bundle, null);
        }
        return 0;
    }
}
