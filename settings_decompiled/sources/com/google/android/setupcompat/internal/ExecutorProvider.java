package com.google.android.setupcompat.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public final class ExecutorProvider<T extends Executor> {
    public static final ExecutorProvider<ExecutorService> setupCompatServiceInvoker = new ExecutorProvider<>(createSizeBoundedExecutor("SetupCompatServiceInvoker", 50));
    private final T executor;
    private T injectedExecutor;

    private ExecutorProvider(T t) {
        this.executor = t;
    }

    public T get() {
        T t = this.injectedExecutor;
        return t != null ? t : this.executor;
    }

    public void injectExecutor(T t) {
        this.injectedExecutor = t;
    }

    public static void resetExecutors() {
        ((ExecutorProvider) setupCompatServiceInvoker).injectedExecutor = null;
    }

    public static ExecutorService createSizeBoundedExecutor(final String str, int i) {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue(i), new ThreadFactory() { // from class: com.google.android.setupcompat.internal.ExecutorProvider$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.ThreadFactory
            public final Thread newThread(Runnable runnable) {
                Thread lambda$createSizeBoundedExecutor$0;
                lambda$createSizeBoundedExecutor$0 = ExecutorProvider.lambda$createSizeBoundedExecutor$0(str, runnable);
                return lambda$createSizeBoundedExecutor$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Thread lambda$createSizeBoundedExecutor$0(String str, Runnable runnable) {
        return new Thread(runnable, str);
    }
}
