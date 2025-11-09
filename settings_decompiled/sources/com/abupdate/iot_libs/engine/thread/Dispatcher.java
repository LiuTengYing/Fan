package com.abupdate.iot_libs.engine.thread;

import com.abupdate.iot_libs.utils.Utils;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class Dispatcher {
    private static Dispatcher mInstance;
    private ExecutorService executorService;
    private Runnable idleCallback;
    private int maxRequests = 64;
    private final Deque<NamedRunnable> readyAsyncCalls = new ArrayDeque();
    private final Deque<NamedRunnable> runningAsyncCalls = new ArrayDeque();
    private final Deque<Object> runningSyncCalls = new ArrayDeque();

    public static Dispatcher getDispatcher() {
        if (mInstance == null) {
            synchronized (Dispatcher.class) {
                if (mInstance == null) {
                    mInstance = new Dispatcher();
                }
            }
        }
        return mInstance;
    }

    private Dispatcher() {
    }

    public synchronized ExecutorService executorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Utils.threadFactory("FOTA_APP Dispatcher", false));
        }
        return this.executorService;
    }

    public void enqueue(NamedRunnable namedRunnable) {
        if (runningCallsCount() < this.maxRequests) {
            this.runningAsyncCalls.add(namedRunnable);
            executorService().submit(namedRunnable);
            return;
        }
        this.readyAsyncCalls.add(namedRunnable);
    }

    private void promoteCalls() {
        if (runningCallsCount() < this.maxRequests && !this.readyAsyncCalls.isEmpty()) {
            Iterator<NamedRunnable> it = this.readyAsyncCalls.iterator();
            while (it.hasNext()) {
                NamedRunnable next = it.next();
                it.remove();
                this.runningAsyncCalls.add(next);
                executorService().execute(next);
                if (runningCallsCount() >= this.maxRequests) {
                    return;
                }
            }
        }
    }

    public void finished(NamedRunnable namedRunnable) {
        finished(this.runningAsyncCalls, namedRunnable, true);
    }

    private <T> void finished(Deque<T> deque, T t, boolean z) {
        int runningCallsCount;
        Runnable runnable;
        synchronized (this) {
            if (!deque.remove(t)) {
                throw new AssertionError("Call wasn't in-flight!");
            }
            if (z) {
                promoteCalls();
            }
            runningCallsCount = runningCallsCount();
            runnable = this.idleCallback;
        }
        if (runningCallsCount != 0 || runnable == null) {
            return;
        }
        runnable.run();
    }

    public synchronized int runningCallsCount() {
        return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
    }
}
