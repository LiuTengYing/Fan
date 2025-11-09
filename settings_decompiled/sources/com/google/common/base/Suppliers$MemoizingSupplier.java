package com.google.common.base;

import java.io.Serializable;
/* loaded from: classes2.dex */
class Suppliers$MemoizingSupplier<T> implements Supplier<T>, Serializable {
    private static final long serialVersionUID = 0;
    final Supplier<T> delegate;
    volatile transient boolean initialized;
    transient T value;

    public String toString() {
        Object obj;
        StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoize(");
        if (this.initialized) {
            obj = "<supplier that returned " + this.value + ">";
        } else {
            obj = this.delegate;
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
