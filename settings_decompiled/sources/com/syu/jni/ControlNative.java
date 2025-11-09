package com.syu.jni;
/* loaded from: classes2.dex */
public class ControlNative {
    public static ControlNative INSTANCE;

    public native byte[] fyt_get_ui_time();

    static {
        System.loadLibrary("sqlcontrol");
        INSTANCE = new ControlNative();
    }
}
