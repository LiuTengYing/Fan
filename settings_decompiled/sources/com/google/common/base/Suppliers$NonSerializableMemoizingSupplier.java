package com.google.common.base;
/* loaded from: classes2.dex */
class Suppliers$NonSerializableMemoizingSupplier<T> implements Supplier<T> {
    volatile Supplier<T> delegate;
    T value;

    public String toString() {
        Object obj = this.delegate;
        StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoize(");
        if (obj == null) {
            obj = "<supplier that returned " + this.value + ">";
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
