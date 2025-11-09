package com.android.settings.network.ims;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
/* loaded from: classes.dex */
class IntegerConsumer extends Semaphore implements Consumer<Integer> {
    private volatile AtomicInteger mValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IntegerConsumer() {
        super(0);
        this.mValue = new AtomicInteger();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int get(long j) throws InterruptedException {
        tryAcquire(j, TimeUnit.MILLISECONDS);
        return this.mValue.get();
    }

    @Override // java.util.function.Consumer
    public void accept(Integer num) {
        if (num != null) {
            this.mValue.set(num.intValue());
        }
        release();
    }
}
