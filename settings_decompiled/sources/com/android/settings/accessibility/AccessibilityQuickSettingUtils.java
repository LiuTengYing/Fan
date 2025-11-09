package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.StringJoiner;
/* loaded from: classes.dex */
final class AccessibilityQuickSettingUtils {
    private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

    public static void optInValueToSharedPreferences(Context context, ComponentName componentName) {
        String fromSharedPreferences = getFromSharedPreferences(context);
        if (hasValueInSharedPreferences(fromSharedPreferences, componentName)) {
            return;
        }
        StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
        if (!TextUtils.isEmpty(fromSharedPreferences)) {
            stringJoiner.add(fromSharedPreferences);
        }
        stringJoiner.add(componentName.flattenToString());
        getSharedPreferences(context).edit().putString("tile_service_shown", stringJoiner.toString()).apply();
    }

    public static boolean hasValueInSharedPreferences(Context context, ComponentName componentName) {
        return hasValueInSharedPreferences(getFromSharedPreferences(context), componentName);
    }

    private static boolean hasValueInSharedPreferences(String str, ComponentName componentName) {
        TextUtils.SimpleStringSplitter simpleStringSplitter;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        sStringColonSplitter.setString(str);
        do {
            simpleStringSplitter = sStringColonSplitter;
            if (!simpleStringSplitter.hasNext()) {
                return false;
            }
        } while (!TextUtils.equals(componentName.flattenToString(), simpleStringSplitter.next()));
        return true;
    }

    private static String getFromSharedPreferences(Context context) {
        return getSharedPreferences(context).getString("tile_service_shown", "");
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("accessibility_prefs", 0);
    }
}
