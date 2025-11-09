package com.syu.util;

import android.content.Context;
import android.content.SharedPreferences;
/* loaded from: classes2.dex */
public class MySharePreference {
    public static Context mContext;
    private static SharedPreferences mSp;
    public static String mStrPath;

    public static void sync() {
    }

    public static void init(Context context, String str) {
        mContext = context;
        mSp = context.getSharedPreferences(str, 0);
        mStrPath = "/data/data/" + context.getPackageName() + "/shared_prefs/" + str + ".xml";
    }

    public static int getIntValue(String str, int i) {
        SharedPreferences sharedPreferences = mSp;
        return sharedPreferences != null ? sharedPreferences.getInt(str, i) : i;
    }

    public static boolean getBooleanValue(String str, boolean z) {
        SharedPreferences sharedPreferences = mSp;
        return sharedPreferences != null ? sharedPreferences.getBoolean(str, z) : z;
    }

    public static String getStringValue(String str) {
        SharedPreferences sharedPreferences = mSp;
        return sharedPreferences != null ? sharedPreferences.getString(str, "") : "";
    }

    public static void saveIntValue(String str, int i) {
        SharedPreferences sharedPreferences;
        if (getIntValue(str, i + 1) == i || (sharedPreferences = mSp) == null) {
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(str, i);
        commit(edit);
    }

    public static void saveBooleanValue(String str, boolean z) {
        SharedPreferences sharedPreferences;
        if (getBooleanValue(str, !z) == z || (sharedPreferences = mSp) == null) {
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(str, z);
        commit(edit);
    }

    public static void saveStringValue(String str, String str2) {
        SharedPreferences sharedPreferences;
        if (getStringValue(str) == str2 || (sharedPreferences = mSp) == null) {
            return;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(str, str2);
        commit(edit);
    }

    public static void commit(SharedPreferences.Editor editor) {
        if (editor != null) {
            editor.commit();
            sync();
        }
    }
}
