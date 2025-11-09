package com.abupdate.iot_libs.utils;

import android.content.Context;
import android.content.SharedPreferences;
/* loaded from: classes.dex */
public class SPFTool {
    private static String SP_NAME_IPORT = "";
    private static SharedPreferences.Editor editor;
    private static Context sCx;

    public static void putLong(String str, long j) {
        SharedPreferences.Editor edit = sCx.getSharedPreferences(SP_NAME_IPORT, 0).edit();
        editor = edit;
        edit.putLong(str, j);
        editor.commit();
    }

    public static long getLong(String str, long j) {
        return sCx.getSharedPreferences(SP_NAME_IPORT, 0).getLong(str, j);
    }

    public static void putString(String str, String str2) {
        SharedPreferences.Editor edit = sCx.getSharedPreferences(SP_NAME_IPORT, 0).edit();
        editor = edit;
        edit.putString(str, str2);
        editor.commit();
    }

    public static String getString(String str, String str2) {
        return sCx.getSharedPreferences(SP_NAME_IPORT, 0).getString(str, str2);
    }
}
