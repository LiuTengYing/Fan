package com.google.common.util.concurrent;

import com.google.common.base.Supplier;
import java.util.concurrent.locks.ReadWriteLock;
/* loaded from: classes2.dex */
public abstract class Striped<L> {
    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() { // from class: com.google.common.util.concurrent.Striped.5
    };
    private static final Supplier<ReadWriteLock> WEAK_SAFE_READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() { // from class: com.google.common.util.concurrent.Striped.6
    };

    /* loaded from: classes2.dex */
    static class LargeLazyStriped<L> extends PowerOfTwoStriped<L> {
    }

    /* loaded from: classes2.dex */
    private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
    }

    /* loaded from: classes2.dex */
    static class SmallLazyStriped<L> extends PowerOfTwoStriped<L> {
    }

    private Striped() {
    }
}
