package kotlin.reflect;

import org.jetbrains.annotations.Nullable;
/* compiled from: KClass.kt */
/* loaded from: classes2.dex */
public interface KClass<T> extends KDeclarationContainer, KAnnotatedElement {
    @Nullable
    String getQualifiedName();

    @Nullable
    String getSimpleName();

    boolean isInstance(@Nullable Object obj);
}
