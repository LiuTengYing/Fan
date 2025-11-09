package com.google.common.collect;

import java.util.Comparator;
/* loaded from: classes2.dex */
interface SortedIterable<T> extends Iterable<T> {
    Comparator<? super T> comparator();
}
