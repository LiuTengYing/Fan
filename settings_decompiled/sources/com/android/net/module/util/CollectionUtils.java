package com.android.net.module.util;

import java.util.Collection;
/* loaded from: classes.dex */
public final class CollectionUtils {
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}
