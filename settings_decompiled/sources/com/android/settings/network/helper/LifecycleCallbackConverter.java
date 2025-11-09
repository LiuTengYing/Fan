package com.android.settings.network.helper;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class LifecycleCallbackConverter<T> extends LifecycleCallbackAdapter {
    private final AtomicLong mNumberOfActiveStatusChange;
    private final Consumer<T> mResultCallback;
    private final Thread mUiThread;

    private static final boolean isActiveStatus(long j) {
        return (j & 1) != 0;
    }

    @Override // com.android.settings.network.helper.LifecycleCallbackAdapter, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() {
        super.close();
    }

    @Override // com.android.settings.network.helper.LifecycleCallbackAdapter
    public /* bridge */ /* synthetic */ Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    @Override // com.android.settings.network.helper.LifecycleCallbackAdapter, androidx.lifecycle.LifecycleEventObserver
    public /* bridge */ /* synthetic */ void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        super.onStateChanged(lifecycleOwner, event);
    }

    public LifecycleCallbackConverter(Lifecycle lifecycle, Consumer<T> consumer) {
        super(lifecycle);
        this.mNumberOfActiveStatusChange = new AtomicLong();
        this.mUiThread = Thread.currentThread();
        this.mResultCallback = consumer;
    }

    public void postResult(T t) {
        long j = this.mNumberOfActiveStatusChange.get();
        if (Thread.currentThread() == this.mUiThread) {
            lambda$postResultToUiThread$0(j, t);
        } else {
            postResultToUiThread(j, t);
        }
    }

    protected void postResultToUiThread(final long j, final T t) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.helper.LifecycleCallbackConverter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LifecycleCallbackConverter.this.lambda$postResultToUiThread$0(j, t);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: dispatchExtResult */
    public void lambda$postResultToUiThread$0(long j, T t) {
        if (isActiveStatus(j) && j == this.mNumberOfActiveStatusChange.get() && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            this.mResultCallback.accept(t);
        }
    }

    @Override // com.android.settings.network.helper.LifecycleCallbackAdapter
    public boolean isCallbackActive() {
        return isActiveStatus(this.mNumberOfActiveStatusChange.get());
    }

    @Override // com.android.settings.network.helper.LifecycleCallbackAdapter
    public void setCallbackActive(boolean z) {
        if (isCallbackActive() != z) {
            this.mNumberOfActiveStatusChange.getAndIncrement();
        }
    }
}
