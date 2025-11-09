package com.android.settings.dashboard;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.android.settingslib.utils.ThreadUtils;
import java.util.concurrent.CountDownLatch;
/* loaded from: classes.dex */
public abstract class DynamicDataObserver extends ContentObserver {
    private CountDownLatch mCountDownLatch;
    private boolean mUpdateDelegated;
    private Runnable mUpdateRunnable;

    public abstract Uri getUri();

    public abstract void onDataChanged();

    /* JADX INFO: Access modifiers changed from: protected */
    public DynamicDataObserver() {
        super(new Handler(Looper.getMainLooper()));
        this.mCountDownLatch = new CountDownLatch(1);
        onDataChanged();
    }

    public synchronized void updateUi() {
        this.mUpdateDelegated = true;
        Runnable runnable = this.mUpdateRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    public CountDownLatch getCountDownLatch() {
        return this.mCountDownLatch;
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z) {
        onDataChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void post(Runnable runnable) {
        if (this.mUpdateDelegated) {
            ThreadUtils.postOnMainThread(runnable);
        } else {
            this.mUpdateRunnable = runnable;
            this.mCountDownLatch.countDown();
        }
    }
}
