package com.android.settings.dashboard;

import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class UiBlockerController {
    private boolean mBlockerFinished;
    private CountDownLatch mCountDownLatch;
    private Set<String> mKeys;
    private long mTimeoutMillis;

    public UiBlockerController(List<String> list) {
        this(list, 300L);
    }

    public UiBlockerController(List<String> list, long j) {
        this.mCountDownLatch = new CountDownLatch(list.size());
        this.mBlockerFinished = list.isEmpty();
        this.mKeys = new HashSet(list);
        this.mTimeoutMillis = j;
    }

    public boolean start(final Runnable runnable) {
        if (this.mKeys.isEmpty()) {
            return false;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.dashboard.UiBlockerController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UiBlockerController.this.lambda$start$0(runnable);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$start$0(Runnable runnable) {
        try {
            this.mCountDownLatch.await(this.mTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException unused) {
            Log.w("UiBlockerController", "interrupted");
        }
        this.mBlockerFinished = true;
        ThreadUtils.postOnMainThread(runnable);
    }

    public boolean isBlockerFinished() {
        return this.mBlockerFinished;
    }

    public boolean countDown(String str) {
        if (this.mKeys.remove(str)) {
            this.mCountDownLatch.countDown();
            return true;
        }
        return false;
    }
}
