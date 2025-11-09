package androidx.window.embedding;

import android.app.Activity;
import android.util.Log;
import androidx.window.core.ConsumerAdapter;
import androidx.window.core.ExperimentalWindowApi;
import androidx.window.embedding.EmbeddingCompat;
import androidx.window.embedding.EmbeddingInterfaceCompat;
import androidx.window.extensions.WindowExtensionsProvider;
import androidx.window.extensions.embedding.ActivityEmbeddingComponent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: EmbeddingCompat.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class EmbeddingCompat implements EmbeddingInterfaceCompat {
    @NotNull
    public static final Companion Companion = new Companion(null);
    public static final boolean DEBUG = true;
    @NotNull
    private static final String TAG = "EmbeddingCompat";
    @NotNull
    private final EmbeddingAdapter adapter;
    @NotNull
    private final ConsumerAdapter consumerAdapter;
    @NotNull
    private final ActivityEmbeddingComponent embeddingExtension;

    public EmbeddingCompat(@NotNull ActivityEmbeddingComponent embeddingExtension, @NotNull EmbeddingAdapter adapter, @NotNull ConsumerAdapter consumerAdapter) {
        Intrinsics.checkNotNullParameter(embeddingExtension, "embeddingExtension");
        Intrinsics.checkNotNullParameter(adapter, "adapter");
        Intrinsics.checkNotNullParameter(consumerAdapter, "consumerAdapter");
        this.embeddingExtension = embeddingExtension;
        this.adapter = adapter;
        this.consumerAdapter = consumerAdapter;
    }

    @Override // androidx.window.embedding.EmbeddingInterfaceCompat
    public void setSplitRules(@NotNull Set<? extends EmbeddingRule> rules) {
        Intrinsics.checkNotNullParameter(rules, "rules");
        this.embeddingExtension.setEmbeddingRules(this.adapter.translate(rules));
    }

    @Override // androidx.window.embedding.EmbeddingInterfaceCompat
    public void setEmbeddingCallback(@NotNull final EmbeddingInterfaceCompat.EmbeddingCallbackInterface embeddingCallback) {
        Intrinsics.checkNotNullParameter(embeddingCallback, "embeddingCallback");
        this.consumerAdapter.addConsumer(this.embeddingExtension, Reflection.getOrCreateKotlinClass(List.class), "setSplitInfoCallback", new Function1<List<?>, Unit>() { // from class: androidx.window.embedding.EmbeddingCompat$setEmbeddingCallback$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(List<?> list) {
                invoke2(list);
                return Unit.INSTANCE;
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final void invoke2(@NotNull List<?> values) {
                EmbeddingAdapter embeddingAdapter;
                Intrinsics.checkNotNullParameter(values, "values");
                ArrayList arrayList = new ArrayList();
                for (Object obj : values) {
                    if (obj instanceof androidx.window.extensions.embedding.SplitInfo) {
                        arrayList.add(obj);
                    }
                }
                EmbeddingInterfaceCompat.EmbeddingCallbackInterface embeddingCallbackInterface = EmbeddingInterfaceCompat.EmbeddingCallbackInterface.this;
                embeddingAdapter = this.adapter;
                embeddingCallbackInterface.onSplitInfoChanged(embeddingAdapter.translate(arrayList));
            }
        });
    }

    @Override // androidx.window.embedding.EmbeddingInterfaceCompat
    public boolean isActivityEmbedded(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        return this.embeddingExtension.isActivityEmbedded(activity);
    }

    /* compiled from: EmbeddingCompat.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Nullable
        public final Integer getExtensionApiLevel() {
            try {
                return Integer.valueOf(WindowExtensionsProvider.getWindowExtensions().getVendorApiLevel());
            } catch (NoClassDefFoundError unused) {
                Log.d(EmbeddingCompat.TAG, "Embedding extension version not found");
                return null;
            } catch (UnsupportedOperationException unused2) {
                Log.d(EmbeddingCompat.TAG, "Stub Extension");
                return null;
            }
        }

        public final boolean isEmbeddingAvailable() {
            try {
                return WindowExtensionsProvider.getWindowExtensions().getActivityEmbeddingComponent() != null;
            } catch (NoClassDefFoundError unused) {
                Log.d(EmbeddingCompat.TAG, "Embedding extension version not found");
                return false;
            } catch (UnsupportedOperationException unused2) {
                Log.d(EmbeddingCompat.TAG, "Stub Extension");
                return false;
            }
        }

        @NotNull
        public final ActivityEmbeddingComponent embeddingComponent() {
            if (isEmbeddingAvailable()) {
                ActivityEmbeddingComponent activityEmbeddingComponent = WindowExtensionsProvider.getWindowExtensions().getActivityEmbeddingComponent();
                if (activityEmbeddingComponent == null) {
                    Object newProxyInstance = Proxy.newProxyInstance(EmbeddingCompat.class.getClassLoader(), new Class[]{ActivityEmbeddingComponent.class}, new InvocationHandler() { // from class: androidx.window.embedding.EmbeddingCompat$Companion$$ExternalSyntheticLambda0
                        @Override // java.lang.reflect.InvocationHandler
                        public final Object invoke(Object obj, Method method, Object[] objArr) {
                            Unit m12embeddingComponent$lambda0;
                            m12embeddingComponent$lambda0 = EmbeddingCompat.Companion.m12embeddingComponent$lambda0(obj, method, objArr);
                            return m12embeddingComponent$lambda0;
                        }
                    });
                    if (newProxyInstance != null) {
                        return (ActivityEmbeddingComponent) newProxyInstance;
                    }
                    throw new NullPointerException("null cannot be cast to non-null type androidx.window.extensions.embedding.ActivityEmbeddingComponent");
                }
                return activityEmbeddingComponent;
            }
            Object newProxyInstance2 = Proxy.newProxyInstance(EmbeddingCompat.class.getClassLoader(), new Class[]{ActivityEmbeddingComponent.class}, new InvocationHandler() { // from class: androidx.window.embedding.EmbeddingCompat$Companion$$ExternalSyntheticLambda1
                @Override // java.lang.reflect.InvocationHandler
                public final Object invoke(Object obj, Method method, Object[] objArr) {
                    Unit m13embeddingComponent$lambda1;
                    m13embeddingComponent$lambda1 = EmbeddingCompat.Companion.m13embeddingComponent$lambda1(obj, method, objArr);
                    return m13embeddingComponent$lambda1;
                }
            });
            if (newProxyInstance2 != null) {
                return (ActivityEmbeddingComponent) newProxyInstance2;
            }
            throw new NullPointerException("null cannot be cast to non-null type androidx.window.extensions.embedding.ActivityEmbeddingComponent");
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: embeddingComponent$lambda-0  reason: not valid java name */
        public static final Unit m12embeddingComponent$lambda0(Object obj, Method method, Object[] objArr) {
            return Unit.INSTANCE;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: embeddingComponent$lambda-1  reason: not valid java name */
        public static final Unit m13embeddingComponent$lambda1(Object obj, Method method, Object[] objArr) {
            return Unit.INSTANCE;
        }
    }
}
