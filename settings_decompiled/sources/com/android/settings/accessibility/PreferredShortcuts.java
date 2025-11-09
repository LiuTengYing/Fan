package com.android.settings.accessibility;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class PreferredShortcuts {
    public static int retrieveUserShortcutType(Context context, final String str, int i) {
        if (str == null) {
            return i;
        }
        HashSet hashSet = new HashSet(getFromSharedPreferences(context));
        hashSet.removeIf(new Predicate() { // from class: com.android.settings.accessibility.PreferredShortcuts$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$retrieveUserShortcutType$0;
                lambda$retrieveUserShortcutType$0 = PreferredShortcuts.lambda$retrieveUserShortcutType$0(str, (String) obj);
                return lambda$retrieveUserShortcutType$0;
            }
        });
        return hashSet.isEmpty() ? i : PreferredShortcut.fromString((String) hashSet.stream().findFirst().get()).getType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$retrieveUserShortcutType$0(String str, String str2) {
        return !str2.contains(str);
    }

    public static void saveUserShortcutType(Context context, PreferredShortcut preferredShortcut) {
        final String componentName = preferredShortcut.getComponentName();
        if (componentName == null) {
            return;
        }
        HashSet hashSet = new HashSet(getFromSharedPreferences(context));
        hashSet.removeIf(new Predicate() { // from class: com.android.settings.accessibility.PreferredShortcuts$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$saveUserShortcutType$1;
                lambda$saveUserShortcutType$1 = PreferredShortcuts.lambda$saveUserShortcutType$1(componentName, (String) obj);
                return lambda$saveUserShortcutType$1;
            }
        });
        hashSet.add(preferredShortcut.toString());
        saveToSharedPreferences(context, hashSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$saveUserShortcutType$1(String str, String str2) {
        return str2.contains(str);
    }

    private static Set<String> getFromSharedPreferences(Context context) {
        return getSharedPreferences(context).getStringSet("user_shortcut_type", Set.of());
    }

    private static void saveToSharedPreferences(Context context, Set<String> set) {
        getSharedPreferences(context).edit().putStringSet("user_shortcut_type", set).apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("accessibility_prefs", 0);
    }
}
