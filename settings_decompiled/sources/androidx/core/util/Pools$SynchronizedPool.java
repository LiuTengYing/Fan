package androidx.core.util;
/* loaded from: classes.dex */
public class Pools$SynchronizedPool<T> extends Pools$SimplePool<T> {
    private final Object mLock;

    public Pools$SynchronizedPool(int i) {
        super(i);
        this.mLock = new Object();
    }

    @Override // androidx.core.util.Pools$SimplePool, androidx.core.util.Pools$Pool
    public T acquire() {
        T t;
        synchronized (this.mLock) {
            t = (T) super.acquire();
        }
        return t;
    }

    @Override // androidx.core.util.Pools$SimplePool, androidx.core.util.Pools$Pool
    public boolean release(T t) {
        boolean release;
        synchronized (this.mLock) {
            release = super.release(t);
        }
        return release;
    }
}
