package com.android.settingslib.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settingslib.PrimarySwitchPreference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class InputMethodAndSubtypeUtilCompat {
    private static final TextUtils.SimpleStringSplitter sStringInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
    private static final TextUtils.SimpleStringSplitter sStringInputMethodSubtypeSplitter = new TextUtils.SimpleStringSplitter(';');

    public static String buildInputMethodsAndSubtypesString(HashMap<String, HashSet<String>> hashMap) {
        StringBuilder sb = new StringBuilder();
        for (String str : hashMap.keySet()) {
            if (sb.length() > 0) {
                sb.append(':');
            }
            sb.append(str);
            Iterator<String> it = hashMap.get(str).iterator();
            while (it.hasNext()) {
                sb.append(';');
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    private static String buildInputMethodsString(HashSet<String> hashSet) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = hashSet.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (sb.length() > 0) {
                sb.append(':');
            }
            sb.append(next);
        }
        return sb.toString();
    }

    private static int getInputMethodSubtypeSelected(ContentResolver contentResolver) {
        try {
            return Settings.Secure.getInt(contentResolver, "selected_input_method_subtype");
        } catch (Settings.SettingNotFoundException unused) {
            return -1;
        }
    }

    private static boolean isInputMethodSubtypeSelected(ContentResolver contentResolver) {
        return getInputMethodSubtypeSelected(contentResolver) != -1;
    }

    private static void putSelectedInputMethodSubtype(ContentResolver contentResolver, int i) {
        Settings.Secure.putInt(contentResolver, "selected_input_method_subtype", i);
    }

    static HashMap<String, HashSet<String>> getEnabledInputMethodsAndSubtypeList(ContentResolver contentResolver) {
        return parseInputMethodsAndSubtypesString(Settings.Secure.getString(contentResolver, "enabled_input_methods"));
    }

    public static HashMap<String, HashSet<String>> parseInputMethodsAndSubtypesString(String str) {
        HashMap<String, HashSet<String>> hashMap = new HashMap<>();
        if (TextUtils.isEmpty(str)) {
            return hashMap;
        }
        sStringInputMethodSplitter.setString(str);
        while (true) {
            TextUtils.SimpleStringSplitter simpleStringSplitter = sStringInputMethodSplitter;
            if (!simpleStringSplitter.hasNext()) {
                return hashMap;
            }
            String next = simpleStringSplitter.next();
            TextUtils.SimpleStringSplitter simpleStringSplitter2 = sStringInputMethodSubtypeSplitter;
            simpleStringSplitter2.setString(next);
            if (simpleStringSplitter2.hasNext()) {
                HashSet<String> hashSet = new HashSet<>();
                String next2 = simpleStringSplitter2.next();
                while (true) {
                    TextUtils.SimpleStringSplitter simpleStringSplitter3 = sStringInputMethodSubtypeSplitter;
                    if (!simpleStringSplitter3.hasNext()) {
                        break;
                    }
                    hashSet.add(simpleStringSplitter3.next());
                }
                hashMap.put(next2, hashSet);
            }
        }
    }

    private static HashSet<String> getDisabledSystemIMEs(ContentResolver contentResolver) {
        HashSet<String> hashSet = new HashSet<>();
        String string = Settings.Secure.getString(contentResolver, "disabled_system_input_methods");
        if (TextUtils.isEmpty(string)) {
            return hashSet;
        }
        sStringInputMethodSplitter.setString(string);
        while (true) {
            TextUtils.SimpleStringSplitter simpleStringSplitter = sStringInputMethodSplitter;
            if (!simpleStringSplitter.hasNext()) {
                return hashSet;
            }
            hashSet.add(simpleStringSplitter.next());
        }
    }

    public static void saveInputMethodSubtypeList(PreferenceFragmentCompat preferenceFragmentCompat, ContentResolver contentResolver, List<InputMethodInfo> list, boolean z) {
        saveInputMethodSubtypeListForUserInternal(preferenceFragmentCompat, contentResolver, list, z, UserHandle.myUserId());
    }

    public static void saveInputMethodSubtypeListForUser(PreferenceFragmentCompat preferenceFragmentCompat, ContentResolver contentResolver, List<InputMethodInfo> list, boolean z, int i) {
        saveInputMethodSubtypeListForUserInternal(preferenceFragmentCompat, contentResolver, list, z, i);
    }

    private static void saveInputMethodSubtypeListForUserInternal(PreferenceFragmentCompat preferenceFragmentCompat, ContentResolver contentResolver, List<InputMethodInfo> list, boolean z, int i) {
        boolean containsKey;
        Iterator<InputMethodInfo> it;
        int i2;
        Context createContextAsUser;
        String string = Settings.Secure.getString(contentResolver, "default_input_method");
        int inputMethodSubtypeSelected = getInputMethodSubtypeSelected(contentResolver);
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = getEnabledInputMethodsAndSubtypeList(contentResolver);
        HashSet<String> disabledSystemIMEs = getDisabledSystemIMEs(contentResolver);
        Iterator<InputMethodInfo> it2 = list.iterator();
        boolean z2 = false;
        while (it2.hasNext()) {
            InputMethodInfo next = it2.next();
            String id = next.getId();
            Preference findPreference = preferenceFragmentCompat.findPreference(id);
            if (findPreference != null) {
                if (findPreference instanceof TwoStatePreference) {
                    containsKey = ((TwoStatePreference) findPreference).isChecked();
                } else if (findPreference instanceof PrimarySwitchPreference) {
                    containsKey = ((PrimarySwitchPreference) findPreference).isChecked();
                } else {
                    containsKey = enabledInputMethodsAndSubtypeList.containsKey(id);
                }
                boolean equals = id.equals(string);
                boolean isSystem = next.isSystem();
                if (i == UserHandle.myUserId()) {
                    createContextAsUser = preferenceFragmentCompat.getActivity();
                    it = it2;
                    i2 = 0;
                } else {
                    it = it2;
                    i2 = 0;
                    createContextAsUser = preferenceFragmentCompat.getActivity().createContextAsUser(UserHandle.of(i), 0);
                }
                if ((!z && InputMethodSettingValuesWrapper.getInstance(createContextAsUser).isAlwaysCheckedIme(next)) || containsKey) {
                    if (!enabledInputMethodsAndSubtypeList.containsKey(id)) {
                        enabledInputMethodsAndSubtypeList.put(id, new HashSet<>());
                    }
                    HashSet<String> hashSet = enabledInputMethodsAndSubtypeList.get(id);
                    int subtypeCount = next.getSubtypeCount();
                    int i3 = i2;
                    while (i2 < subtypeCount) {
                        InputMethodSubtype subtypeAt = next.getSubtypeAt(i2);
                        boolean z3 = z2;
                        String valueOf = String.valueOf(subtypeAt.hashCode());
                        InputMethodInfo inputMethodInfo = next;
                        TwoStatePreference twoStatePreference = (TwoStatePreference) preferenceFragmentCompat.findPreference(id + valueOf);
                        if (twoStatePreference != null) {
                            if (i3 == 0) {
                                hashSet.clear();
                                i3 = 1;
                                z3 = true;
                            }
                            if (twoStatePreference.isEnabled() && twoStatePreference.isChecked()) {
                                hashSet.add(valueOf);
                                if (equals && inputMethodSubtypeSelected == subtypeAt.hashCode()) {
                                    z2 = false;
                                    i2++;
                                    next = inputMethodInfo;
                                }
                            } else {
                                hashSet.remove(valueOf);
                            }
                        }
                        z2 = z3;
                        i2++;
                        next = inputMethodInfo;
                    }
                } else {
                    enabledInputMethodsAndSubtypeList.remove(id);
                    if (equals) {
                        string = null;
                    }
                }
                if (isSystem && z) {
                    if (disabledSystemIMEs.contains(id)) {
                        if (containsKey) {
                            disabledSystemIMEs.remove(id);
                        }
                    } else if (!containsKey) {
                        disabledSystemIMEs.add(id);
                    }
                }
                it2 = it;
            }
        }
        String buildInputMethodsAndSubtypesString = buildInputMethodsAndSubtypesString(enabledInputMethodsAndSubtypeList);
        String buildInputMethodsString = buildInputMethodsString(disabledSystemIMEs);
        if (z2 || !isInputMethodSubtypeSelected(contentResolver)) {
            putSelectedInputMethodSubtype(contentResolver, -1);
        }
        Settings.Secure.putString(contentResolver, "enabled_input_methods", buildInputMethodsAndSubtypesString);
        if (buildInputMethodsString.length() > 0) {
            Settings.Secure.putString(contentResolver, "disabled_system_input_methods", buildInputMethodsString);
        }
        if (string == null) {
            string = "";
        }
        Settings.Secure.putString(contentResolver, "default_input_method", string);
    }

    public static void loadInputMethodSubtypeList(PreferenceFragmentCompat preferenceFragmentCompat, ContentResolver contentResolver, List<InputMethodInfo> list, Map<String, List<Preference>> map) {
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = getEnabledInputMethodsAndSubtypeList(contentResolver);
        for (InputMethodInfo inputMethodInfo : list) {
            String id = inputMethodInfo.getId();
            Preference findPreference = preferenceFragmentCompat.findPreference(id);
            if (findPreference instanceof TwoStatePreference) {
                boolean containsKey = enabledInputMethodsAndSubtypeList.containsKey(id);
                ((TwoStatePreference) findPreference).setChecked(containsKey);
                if (map != null) {
                    for (Preference preference : map.get(id)) {
                        preference.setEnabled(containsKey);
                    }
                }
                setSubtypesPreferenceEnabled(preferenceFragmentCompat, list, id, containsKey);
            }
        }
        updateSubtypesPreferenceChecked(preferenceFragmentCompat, list, enabledInputMethodsAndSubtypeList);
    }

    private static void setSubtypesPreferenceEnabled(PreferenceFragmentCompat preferenceFragmentCompat, List<InputMethodInfo> list, String str, boolean z) {
        PreferenceScreen preferenceScreen = preferenceFragmentCompat.getPreferenceScreen();
        for (InputMethodInfo inputMethodInfo : list) {
            if (str.equals(inputMethodInfo.getId())) {
                int subtypeCount = inputMethodInfo.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    InputMethodSubtype subtypeAt = inputMethodInfo.getSubtypeAt(i);
                    TwoStatePreference twoStatePreference = (TwoStatePreference) preferenceScreen.findPreference(str + subtypeAt.hashCode());
                    if (twoStatePreference != null) {
                        twoStatePreference.setEnabled(z);
                    }
                }
            }
        }
    }

    private static void updateSubtypesPreferenceChecked(PreferenceFragmentCompat preferenceFragmentCompat, List<InputMethodInfo> list, HashMap<String, HashSet<String>> hashMap) {
        PreferenceScreen preferenceScreen = preferenceFragmentCompat.getPreferenceScreen();
        for (InputMethodInfo inputMethodInfo : list) {
            String id = inputMethodInfo.getId();
            if (hashMap.containsKey(id)) {
                HashSet<String> hashSet = hashMap.get(id);
                int subtypeCount = inputMethodInfo.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    String valueOf = String.valueOf(inputMethodInfo.getSubtypeAt(i).hashCode());
                    TwoStatePreference twoStatePreference = (TwoStatePreference) preferenceScreen.findPreference(id + valueOf);
                    if (twoStatePreference != null) {
                        twoStatePreference.setChecked(hashSet.contains(valueOf));
                    }
                }
            }
        }
    }

    public static void removeUnnecessaryNonPersistentPreference(Preference preference) {
        SharedPreferences sharedPreferences;
        String key = preference.getKey();
        if (preference.isPersistent() || key == null || (sharedPreferences = preference.getSharedPreferences()) == null || !sharedPreferences.contains(key)) {
            return;
        }
        sharedPreferences.edit().remove(key).apply();
    }
}
