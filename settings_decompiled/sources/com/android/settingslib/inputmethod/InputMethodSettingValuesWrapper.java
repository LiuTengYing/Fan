package com.android.settingslib.inputmethod;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes2.dex */
public class InputMethodSettingValuesWrapper {
    private static final String TAG = "InputMethodSettingValuesWrapper";
    private final ContentResolver mContentResolver;
    private final InputMethodManager mImm;
    private final ArrayList<InputMethodInfo> mMethodList = new ArrayList<>();
    private static final Object sInstanceMapLock = new Object();
    @GuardedBy({"sInstanceMapLock"})
    private static SparseArray<InputMethodSettingValuesWrapper> sInstanceMap = new SparseArray<>();

    public static InputMethodSettingValuesWrapper getInstance(Context context) {
        int userId = context.getUserId();
        synchronized (sInstanceMapLock) {
            if (sInstanceMap.size() == 0) {
                InputMethodSettingValuesWrapper inputMethodSettingValuesWrapper = new InputMethodSettingValuesWrapper(context);
                sInstanceMap.put(userId, inputMethodSettingValuesWrapper);
                return inputMethodSettingValuesWrapper;
            } else if (sInstanceMap.indexOfKey(userId) >= 0) {
                return sInstanceMap.get(userId);
            } else {
                InputMethodSettingValuesWrapper inputMethodSettingValuesWrapper2 = new InputMethodSettingValuesWrapper(context);
                sInstanceMap.put(context.getUserId(), inputMethodSettingValuesWrapper2);
                return inputMethodSettingValuesWrapper2;
            }
        }
    }

    private InputMethodSettingValuesWrapper(Context context) {
        this.mContentResolver = context.getContentResolver();
        this.mImm = (InputMethodManager) context.getSystemService(InputMethodManager.class);
        refreshAllInputMethodAndSubtypes();
    }

    public void refreshAllInputMethodAndSubtypes() {
        this.mMethodList.clear();
        this.mMethodList.addAll(this.mImm.getInputMethodListAsUser(this.mContentResolver.getUserId(), 1));
    }

    public List<InputMethodInfo> getInputMethodList() {
        return new ArrayList(this.mMethodList);
    }

    public boolean isAlwaysCheckedIme(InputMethodInfo inputMethodInfo) {
        boolean isEnabledImi = isEnabledImi(inputMethodInfo);
        if (getEnabledInputMethodList().size() > 1 || !isEnabledImi) {
            int enabledValidNonAuxAsciiCapableImeCount = getEnabledValidNonAuxAsciiCapableImeCount();
            return enabledValidNonAuxAsciiCapableImeCount <= 1 && (enabledValidNonAuxAsciiCapableImeCount != 1 || isEnabledImi) && inputMethodInfo.isSystem() && InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(inputMethodInfo);
        }
        return true;
    }

    private int getEnabledValidNonAuxAsciiCapableImeCount() {
        int i = 0;
        for (InputMethodInfo inputMethodInfo : getEnabledInputMethodList()) {
            if (InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(inputMethodInfo)) {
                i++;
            }
        }
        if (i == 0) {
            Log.w(TAG, "No \"enabledValidNonAuxAsciiCapableIme\"s found.");
        }
        return i;
    }

    public boolean isEnabledImi(InputMethodInfo inputMethodInfo) {
        for (InputMethodInfo inputMethodInfo2 : getEnabledInputMethodList()) {
            if (inputMethodInfo2.getId().equals(inputMethodInfo.getId())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<InputMethodInfo> getEnabledInputMethodList() {
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = InputMethodAndSubtypeUtil.getEnabledInputMethodsAndSubtypeList(this.mContentResolver);
        ArrayList<InputMethodInfo> arrayList = new ArrayList<>();
        Iterator<InputMethodInfo> it = this.mMethodList.iterator();
        while (it.hasNext()) {
            InputMethodInfo next = it.next();
            if (enabledInputMethodsAndSubtypeList.keySet().contains(next.getId())) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }
}
