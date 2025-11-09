package kotlin.jvm.internal;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
/* compiled from: ArrayIterator.kt */
/* loaded from: classes2.dex */
public final class ArrayIteratorKt {
    @NotNull
    public static final <T> Iterator<T> iterator(@NotNull T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        return new ArrayIterator(array);
    }
}
