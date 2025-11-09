package com.syu.utils;
/* loaded from: classes2.dex */
public class Utils {
    public static boolean isEmptyStr(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean equals(String str, String str2) {
        if (isEmptyStr(str) || isEmptyStr(str2)) {
            return false;
        }
        return str.equals(str2);
    }
}
