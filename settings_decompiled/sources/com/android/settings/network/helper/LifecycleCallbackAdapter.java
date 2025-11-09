package com.android.settings.network.helper;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
abstract class LifecycleCallbackAdapter implements LifecycleEventObserver, AutoCloseable {
    private AtomicReference<Lifecycle> mLifecycle;

    public abstract boolean isCallbackActive();

    public abstract void setCallbackActive(boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public LifecycleCallbackAdapter(Lifecycle lifecycle) {
        AtomicReference<Lifecycle> atomicReference = new AtomicReference<>();
        this.mLifecycle = atomicReference;
        atomicReference.set(lifecycle);
        lifecycle.addObserver(this);
    }

    public Lifecycle getLifecycle() {
        return this.mLifecycle.get();
    }

    @Override // androidx.lifecycle.LifecycleEventObserver
    public void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (this.mLifecycle.get() == null) {
            return;
        }
        Lifecycle.State targetState = event.getTargetState();
        boolean isAtLeast = targetState.isAtLeast(Lifecycle.State.STARTED);
        if (isAtLeast != isCallbackActive()) {
            setCallbackActive(isAtLeast);
        }
        if (targetState == Lifecycle.State.DESTROYED) {
            close();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        Lifecycle andSet = this.mLifecycle.getAndSet(null);
        if (andSet != null) {
            andSet.removeObserver(this);
        }
    }
}
