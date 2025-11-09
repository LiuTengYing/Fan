package com.syu.util;
/* loaded from: classes2.dex */
public class IpcUtil {
    public static boolean intsOk(int[] iArr, int i) {
        return iArr != null && iArr.length >= i;
    }

    public static boolean strsOk(Object[] objArr, int i) {
        return objArr != null && objArr.length >= i;
    }
}
