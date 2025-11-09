package androidx.window.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import kotlin.reflect.KClasses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ConsumerAdapter.kt */
@SuppressLint({"BanUncheckedReflection"})
/* loaded from: classes.dex */
public final class ConsumerAdapter {
    @NotNull
    private final ClassLoader loader;

    /* compiled from: ConsumerAdapter.kt */
    /* loaded from: classes.dex */
    public interface Subscription {
        void dispose();
    }

    public ConsumerAdapter(@NotNull ClassLoader loader) {
        Intrinsics.checkNotNullParameter(loader, "loader");
        this.loader = loader;
    }

    @Nullable
    public final Class<?> consumerClassOrNull$window_release() {
        try {
            return unsafeConsumerClass();
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }

    private final Class<?> unsafeConsumerClass() {
        Class<?> loadClass = this.loader.loadClass("java.util.function.Consumer");
        Intrinsics.checkNotNullExpressionValue(loadClass, "loader.loadClass(\"java.util.function.Consumer\")");
        return loadClass;
    }

    private final <T> Object buildConsumer(KClass<T> kClass, Function1<? super T, Unit> function1) {
        Object newProxyInstance = Proxy.newProxyInstance(this.loader, new Class[]{unsafeConsumerClass()}, new ConsumerHandler(kClass, function1));
        Intrinsics.checkNotNullExpressionValue(newProxyInstance, "newProxyInstance(loader,…onsumerClass()), handler)");
        return newProxyInstance;
    }

    public final <T> void addConsumer(@NotNull Object obj, @NotNull KClass<T> clazz, @NotNull String methodName, @NotNull Function1<? super T, Unit> consumer) {
        Intrinsics.checkNotNullParameter(obj, "obj");
        Intrinsics.checkNotNullParameter(clazz, "clazz");
        Intrinsics.checkNotNullParameter(methodName, "methodName");
        Intrinsics.checkNotNullParameter(consumer, "consumer");
        obj.getClass().getMethod(methodName, unsafeConsumerClass()).invoke(obj, buildConsumer(clazz, consumer));
    }

    @NotNull
    public final <T> Subscription createSubscription(@NotNull final Object obj, @NotNull KClass<T> clazz, @NotNull String addMethodName, @NotNull String removeMethodName, @NotNull Activity activity, @NotNull Function1<? super T, Unit> consumer) {
        Intrinsics.checkNotNullParameter(obj, "obj");
        Intrinsics.checkNotNullParameter(clazz, "clazz");
        Intrinsics.checkNotNullParameter(addMethodName, "addMethodName");
        Intrinsics.checkNotNullParameter(removeMethodName, "removeMethodName");
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(consumer, "consumer");
        final Object buildConsumer = buildConsumer(clazz, consumer);
        obj.getClass().getMethod(addMethodName, Activity.class, unsafeConsumerClass()).invoke(obj, activity, buildConsumer);
        final Method method = obj.getClass().getMethod(removeMethodName, unsafeConsumerClass());
        return new Subscription() { // from class: androidx.window.core.ConsumerAdapter$createSubscription$1
            @Override // androidx.window.core.ConsumerAdapter.Subscription
            public void dispose() {
                method.invoke(obj, buildConsumer);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ConsumerAdapter.kt */
    /* loaded from: classes.dex */
    public static final class ConsumerHandler<T> implements InvocationHandler {
        @NotNull
        private final KClass<T> clazz;
        @NotNull
        private final Function1<T, Unit> consumer;

        /* JADX WARN: Multi-variable type inference failed */
        public ConsumerHandler(@NotNull KClass<T> clazz, @NotNull Function1<? super T, Unit> consumer) {
            Intrinsics.checkNotNullParameter(clazz, "clazz");
            Intrinsics.checkNotNullParameter(consumer, "consumer");
            this.clazz = clazz;
            this.consumer = consumer;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.lang.reflect.InvocationHandler
        @NotNull
        public Object invoke(@NotNull Object obj, @NotNull Method method, @Nullable Object[] objArr) {
            Intrinsics.checkNotNullParameter(obj, "obj");
            Intrinsics.checkNotNullParameter(method, "method");
            if (isAccept(method, objArr)) {
                invokeAccept(KClasses.cast(this.clazz, objArr != null ? objArr[0] : null));
                return Unit.INSTANCE;
            } else if (isEquals(method, objArr)) {
                return Boolean.valueOf(obj == (objArr != null ? objArr[0] : null));
            } else if (isHashCode(method, objArr)) {
                return Integer.valueOf(this.consumer.hashCode());
            } else {
                if (isToString(method, objArr)) {
                    return this.consumer.toString();
                }
                throw new UnsupportedOperationException("Unexpected method call object:" + obj + ", method: " + method + ", args: " + objArr);
            }
        }

        public final void invokeAccept(@NotNull T parameter) {
            Intrinsics.checkNotNullParameter(parameter, "parameter");
            this.consumer.invoke(parameter);
        }

        private final boolean isEquals(Method method, Object[] objArr) {
            if (Intrinsics.areEqual(method.getName(), "equals") && method.getReturnType().equals(Boolean.TYPE)) {
                if (objArr != null && objArr.length == 1) {
                    return true;
                }
            }
            return false;
        }

        private final boolean isHashCode(Method method, Object[] objArr) {
            return Intrinsics.areEqual(method.getName(), "hashCode") && method.getReturnType().equals(Integer.TYPE) && objArr == null;
        }

        private final boolean isAccept(Method method, Object[] objArr) {
            if (Intrinsics.areEqual(method.getName(), "accept")) {
                if (objArr != null && objArr.length == 1) {
                    return true;
                }
            }
            return false;
        }

        private final boolean isToString(Method method, Object[] objArr) {
            return Intrinsics.areEqual(method.getName(), "toString") && method.getReturnType().equals(String.class) && objArr == null;
        }
    }
}
