package androidx.dynamicanimation.animation;
/* loaded from: classes.dex */
public interface FrameCallbackScheduler {
    boolean isCurrentThread();

    void postFrameCallback(Runnable runnable);
}
