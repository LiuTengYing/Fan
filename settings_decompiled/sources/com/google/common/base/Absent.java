package com.google.common.base;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class Absent<T> extends Optional<T> {
    static final Absent<Object> INSTANCE = new Absent<>();
    private static final long serialVersionUID = 0;

    @Override // com.google.common.base.Optional
    public boolean equals(Object obj) {
        return obj == this;
    }

    public int hashCode() {
        return 2040732332;
    }

    @Override // com.google.common.base.Optional
    public boolean isPresent() {
        return false;
    }

    public String toString() {
        return "Optional.absent()";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Optional<T> withType() {
        return INSTANCE;
    }

    private Absent() {
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
