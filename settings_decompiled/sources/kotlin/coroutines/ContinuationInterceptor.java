package kotlin.coroutines;

import kotlin.coroutines.CoroutineContext;
import org.jetbrains.annotations.NotNull;
/* compiled from: ContinuationInterceptor.kt */
/* loaded from: classes2.dex */
public interface ContinuationInterceptor extends CoroutineContext.Element {
    @NotNull
    public static final Key Key = Key.$$INSTANCE;

    @NotNull
    <T> Continuation<T> interceptContinuation(@NotNull Continuation<? super T> continuation);

    void releaseInterceptedContinuation(@NotNull Continuation<?> continuation);

    /* compiled from: ContinuationInterceptor.kt */
    /* loaded from: classes2.dex */
    public static final class Key implements CoroutineContext.Key<ContinuationInterceptor> {
        static final /* synthetic */ Key $$INSTANCE = new Key();

        private Key() {
        }
    }
}
