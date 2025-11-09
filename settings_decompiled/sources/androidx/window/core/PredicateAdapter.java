package androidx.window.core;

import android.annotation.SuppressLint;
import android.util.Pair;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import kotlin.reflect.KClasses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: PredicateAdapter.kt */
@SuppressLint({"BanUncheckedReflection"})
/* loaded from: classes.dex */
public final class PredicateAdapter {
    @NotNull
    private final ClassLoader loader;

    public PredicateAdapter(@NotNull ClassLoader loader) {
        Intrinsics.checkNotNullParameter(loader, "loader");
        this.loader = loader;
    }

    @Nullable
    public final Class<?> predicateClassOrNull$window_release() {
        try {
            return predicateClassOrThrow();
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }

    private final Class<?> predicateClassOrThrow() {
        Class<?> loadClass = this.loader.loadClass("java.util.function.Predicate");
        Intrinsics.checkNotNullExpressionValue(loadClass, "loader.loadClass(\"java.util.function.Predicate\")");
        return loadClass;
    }

    @NotNull
    public final <T> Object buildPredicate(@NotNull KClass<T> clazz, @NotNull Function1<? super T, Boolean> predicate) {
        Intrinsics.checkNotNullParameter(clazz, "clazz");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Object newProxyInstance = Proxy.newProxyInstance(this.loader, new Class[]{predicateClassOrThrow()}, new PredicateStubHandler(clazz, predicate));
        Intrinsics.checkNotNullExpressionValue(newProxyInstance, "newProxyInstance(loader,…row()), predicateHandler)");
        return newProxyInstance;
    }

    @NotNull
    public final <T, U> Object buildPairPredicate(@NotNull KClass<T> firstClazz, @NotNull KClass<U> secondClazz, @NotNull Function2<? super T, ? super U, Boolean> predicate) {
        Intrinsics.checkNotNullParameter(firstClazz, "firstClazz");
        Intrinsics.checkNotNullParameter(secondClazz, "secondClazz");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        Object newProxyInstance = Proxy.newProxyInstance(this.loader, new Class[]{predicateClassOrThrow()}, new PairPredicateStubHandler(firstClazz, secondClazz, predicate));
        Intrinsics.checkNotNullExpressionValue(newProxyInstance, "newProxyInstance(loader,…row()), predicateHandler)");
        return newProxyInstance;
    }

    /* compiled from: PredicateAdapter.kt */
    /* loaded from: classes.dex */
    private static abstract class BaseHandler<T> implements InvocationHandler {
        @NotNull
        private final KClass<T> clazz;

        public abstract boolean invokeTest(@NotNull Object obj, @NotNull T t);

        public BaseHandler(@NotNull KClass<T> clazz) {
            Intrinsics.checkNotNullParameter(clazz, "clazz");
            this.clazz = clazz;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.lang.reflect.InvocationHandler
        @NotNull
        public Object invoke(@NotNull Object obj, @NotNull Method method, @Nullable Object[] objArr) {
            Intrinsics.checkNotNullParameter(obj, "obj");
            Intrinsics.checkNotNullParameter(method, "method");
            if (isTest(method, objArr)) {
                return Boolean.valueOf(invokeTest(obj, KClasses.cast(this.clazz, objArr != null ? objArr[0] : null)));
            } else if (isEquals(method, objArr)) {
                Object obj2 = objArr != null ? objArr[0] : null;
                Intrinsics.checkNotNull(obj2);
                return Boolean.valueOf(obj == obj2);
            } else if (isHashCode(method, objArr)) {
                return Integer.valueOf(hashCode());
            } else {
                if (isToString(method, objArr)) {
                    return toString();
                }
                throw new UnsupportedOperationException("Unexpected method call object:" + obj + ", method: " + method + ", args: " + objArr);
            }
        }

        protected final boolean isEquals(@NotNull Method method, @Nullable Object[] objArr) {
            Intrinsics.checkNotNullParameter(method, "<this>");
            if (Intrinsics.areEqual(method.getName(), "equals") && method.getReturnType().equals(Boolean.TYPE)) {
                if (objArr != null && objArr.length == 1) {
                    return true;
                }
            }
            return false;
        }

        protected final boolean isHashCode(@NotNull Method method, @Nullable Object[] objArr) {
            Intrinsics.checkNotNullParameter(method, "<this>");
            return Intrinsics.areEqual(method.getName(), "hashCode") && method.getReturnType().equals(Integer.TYPE) && objArr == null;
        }

        protected final boolean isTest(@NotNull Method method, @Nullable Object[] objArr) {
            Intrinsics.checkNotNullParameter(method, "<this>");
            if (Intrinsics.areEqual(method.getName(), "test") && method.getReturnType().equals(Boolean.TYPE)) {
                if (objArr != null && objArr.length == 1) {
                    return true;
                }
            }
            return false;
        }

        protected final boolean isToString(@NotNull Method method, @Nullable Object[] objArr) {
            Intrinsics.checkNotNullParameter(method, "<this>");
            return Intrinsics.areEqual(method.getName(), "toString") && method.getReturnType().equals(String.class) && objArr == null;
        }
    }

    /* compiled from: PredicateAdapter.kt */
    /* loaded from: classes.dex */
    private static final class PredicateStubHandler<T> extends BaseHandler<T> {
        @NotNull
        private final Function1<T, Boolean> predicate;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public PredicateStubHandler(@NotNull KClass<T> clazzT, @NotNull Function1<? super T, Boolean> predicate) {
            super(clazzT);
            Intrinsics.checkNotNullParameter(clazzT, "clazzT");
            Intrinsics.checkNotNullParameter(predicate, "predicate");
            this.predicate = predicate;
        }

        @Override // androidx.window.core.PredicateAdapter.BaseHandler
        public boolean invokeTest(@NotNull Object obj, @NotNull T parameter) {
            Intrinsics.checkNotNullParameter(obj, "obj");
            Intrinsics.checkNotNullParameter(parameter, "parameter");
            return this.predicate.invoke(parameter).booleanValue();
        }

        public int hashCode() {
            return this.predicate.hashCode();
        }

        @NotNull
        public String toString() {
            return this.predicate.toString();
        }
    }

    /* compiled from: PredicateAdapter.kt */
    /* loaded from: classes.dex */
    private static final class PairPredicateStubHandler<T, U> extends BaseHandler<Pair<?, ?>> {
        @NotNull
        private final KClass<T> clazzT;
        @NotNull
        private final KClass<U> clazzU;
        @NotNull
        private final Function2<T, U, Boolean> predicate;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public PairPredicateStubHandler(@NotNull KClass<T> clazzT, @NotNull KClass<U> clazzU, @NotNull Function2<? super T, ? super U, Boolean> predicate) {
            super(Reflection.getOrCreateKotlinClass(Pair.class));
            Intrinsics.checkNotNullParameter(clazzT, "clazzT");
            Intrinsics.checkNotNullParameter(clazzU, "clazzU");
            Intrinsics.checkNotNullParameter(predicate, "predicate");
            this.clazzT = clazzT;
            this.clazzU = clazzU;
            this.predicate = predicate;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.window.core.PredicateAdapter.BaseHandler
        public boolean invokeTest(@NotNull Object obj, @NotNull Pair<?, ?> parameter) {
            Intrinsics.checkNotNullParameter(obj, "obj");
            Intrinsics.checkNotNullParameter(parameter, "parameter");
            return ((Boolean) this.predicate.invoke(KClasses.cast(this.clazzT, parameter.first), KClasses.cast(this.clazzU, parameter.second))).booleanValue();
        }

        public int hashCode() {
            return this.predicate.hashCode();
        }

        @NotNull
        public String toString() {
            return this.predicate.toString();
        }
    }
}
