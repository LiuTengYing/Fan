package com.abupdate.iot_libs.engine.thread;

import com.abupdate.iot_libs.utils.Utils;
import com.abupdate.trace.Trace;
/* loaded from: classes.dex */
public abstract class NamedRunnable implements Runnable {
    protected final String name;

    protected abstract void execute();

    public NamedRunnable(String str, Object... objArr) {
        this.name = Utils.format(str, objArr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v4, types: [com.abupdate.iot_libs.engine.thread.Dispatcher] */
    @Override // java.lang.Runnable
    public final void run() {
        String name = Thread.currentThread().getName();
        Thread.currentThread().setName(this.name);
        try {
            try {
                execute();
            } catch (Exception e) {
                e.printStackTrace();
                Trace.d("NamedRunnable", "Exception: " + e);
            }
        } finally {
            Thread.currentThread().setName(name);
            Dispatcher.getDispatcher().finished(this);
        }
    }
}
