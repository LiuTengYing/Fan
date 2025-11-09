package kotlin.reflect;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: KCallable.kt */
/* loaded from: classes2.dex */
public interface KCallable<R> extends KAnnotatedElement {
    R call(@NotNull Object... objArr);

    R callBy(@NotNull Map<Object, ? extends Object> map);

    @NotNull
    List<Object> getParameters();

    @NotNull
    KType getReturnType();

    @NotNull
    List<Object> getTypeParameters();

    @Nullable
    KVisibility getVisibility();

    boolean isAbstract();

    boolean isFinal();

    boolean isOpen();

    boolean isSuspend();
}
