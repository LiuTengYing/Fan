package com.google.android.setupcompat.internal;

import android.annotation.TargetApi;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.ArrayMap;
import com.google.android.setupcompat.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
@TargetApi(22)
/* loaded from: classes2.dex */
public final class PersistableBundles {
    private static final Logger LOG = new Logger("PersistableBundles");

    public static PersistableBundle mergeBundles(PersistableBundle persistableBundle, PersistableBundle persistableBundle2, PersistableBundle... persistableBundleArr) {
        ArrayList<PersistableBundle> arrayList = new ArrayList();
        arrayList.addAll(Arrays.asList(persistableBundle, persistableBundle2));
        Collections.addAll(arrayList, persistableBundleArr);
        PersistableBundle persistableBundle3 = new PersistableBundle();
        for (PersistableBundle persistableBundle4 : arrayList) {
            for (String str : persistableBundle4.keySet()) {
                Preconditions.checkArgument(!persistableBundle3.containsKey(str), String.format("Found duplicate key [%s] while attempting to merge bundles.", str));
            }
            persistableBundle3.putAll(persistableBundle4);
        }
        return persistableBundle3;
    }

    public static Bundle toBundle(PersistableBundle persistableBundle) {
        Bundle bundle = new Bundle();
        bundle.putAll(persistableBundle);
        return bundle;
    }

    public static boolean equals(PersistableBundle persistableBundle, PersistableBundle persistableBundle2) {
        return persistableBundle == persistableBundle2 || toMap(persistableBundle).equals(toMap(persistableBundle2));
    }

    public static PersistableBundle assertIsValid(PersistableBundle persistableBundle) {
        Preconditions.checkNotNull(persistableBundle, "PersistableBundle cannot be null!");
        for (String str : persistableBundle.keySet()) {
            Object obj = persistableBundle.get(str);
            Preconditions.checkArgument(isSupportedDataType(obj), String.format("Unknown/unsupported data type [%s] for key %s", obj, str));
        }
        return persistableBundle;
    }

    private static ArrayMap<String, Object> toMap(BaseBundle baseBundle) {
        if (baseBundle == null || baseBundle.isEmpty()) {
            return new ArrayMap<>(0);
        }
        ArrayMap<String, Object> arrayMap = new ArrayMap<>(baseBundle.size());
        for (String str : baseBundle.keySet()) {
            Object obj = baseBundle.get(str);
            if (!isSupportedDataType(obj)) {
                LOG.w(String.format("Unknown/unsupported data type [%s] for key %s", obj, str));
            } else {
                arrayMap.put(str, baseBundle.get(str));
            }
        }
        return arrayMap;
    }

    private static boolean isSupportedDataType(Object obj) {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) || (obj instanceof Float) || (obj instanceof String) || (obj instanceof Boolean);
    }
}
