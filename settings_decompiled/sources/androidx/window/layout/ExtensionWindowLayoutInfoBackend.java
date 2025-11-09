package androidx.window.layout;

import android.app.Activity;
import androidx.core.util.Consumer;
import androidx.window.core.ConsumerAdapter;
import androidx.window.extensions.layout.WindowLayoutComponent;
import androidx.window.layout.ExtensionWindowLayoutInfoBackend;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ExtensionWindowLayoutInfoBackend.kt */
/* loaded from: classes.dex */
public final class ExtensionWindowLayoutInfoBackend implements WindowBackend {
    @NotNull
    private final Map<Activity, MulticastConsumer> activityToListeners;
    @NotNull
    private final WindowLayoutComponent component;
    @NotNull
    private final ConsumerAdapter consumerAdapter;
    @NotNull
    private final Map<MulticastConsumer, ConsumerAdapter.Subscription> consumerToToken;
    @NotNull
    private final ReentrantLock extensionWindowBackendLock;
    @NotNull
    private final Map<Consumer<WindowLayoutInfo>, Activity> listenerToActivity;

    public ExtensionWindowLayoutInfoBackend(@NotNull WindowLayoutComponent component, @NotNull ConsumerAdapter consumerAdapter) {
        Intrinsics.checkNotNullParameter(component, "component");
        Intrinsics.checkNotNullParameter(consumerAdapter, "consumerAdapter");
        this.component = component;
        this.consumerAdapter = consumerAdapter;
        this.extensionWindowBackendLock = new ReentrantLock();
        this.activityToListeners = new LinkedHashMap();
        this.listenerToActivity = new LinkedHashMap();
        this.consumerToToken = new LinkedHashMap();
    }

    @Override // androidx.window.layout.WindowBackend
    public void registerLayoutChangeCallback(@NotNull Activity activity, @NotNull Executor executor, @NotNull Consumer<WindowLayoutInfo> callback) {
        Unit unit;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(callback, "callback");
        ReentrantLock reentrantLock = this.extensionWindowBackendLock;
        reentrantLock.lock();
        try {
            MulticastConsumer multicastConsumer = this.activityToListeners.get(activity);
            if (multicastConsumer != null) {
                multicastConsumer.addListener(callback);
                this.listenerToActivity.put(callback, activity);
                unit = Unit.INSTANCE;
            } else {
                unit = null;
            }
            if (unit == null) {
                final MulticastConsumer multicastConsumer2 = new MulticastConsumer(activity);
                this.activityToListeners.put(activity, multicastConsumer2);
                this.listenerToActivity.put(callback, activity);
                multicastConsumer2.addListener(callback);
                this.consumerToToken.put(multicastConsumer2, this.consumerAdapter.createSubscription(this.component, Reflection.getOrCreateKotlinClass(androidx.window.extensions.layout.WindowLayoutInfo.class), "addWindowLayoutInfoListener", "removeWindowLayoutInfoListener", activity, new Function1<androidx.window.extensions.layout.WindowLayoutInfo, Unit>() { // from class: androidx.window.layout.ExtensionWindowLayoutInfoBackend$registerLayoutChangeCallback$1$2$disposableToken$1
                    /* JADX INFO: Access modifiers changed from: package-private */
                    {
                        super(1);
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public /* bridge */ /* synthetic */ Unit invoke(androidx.window.extensions.layout.WindowLayoutInfo windowLayoutInfo) {
                        invoke2(windowLayoutInfo);
                        return Unit.INSTANCE;
                    }

                    /* renamed from: invoke  reason: avoid collision after fix types in other method */
                    public final void invoke2(@NotNull androidx.window.extensions.layout.WindowLayoutInfo value) {
                        Intrinsics.checkNotNullParameter(value, "value");
                        ExtensionWindowLayoutInfoBackend.MulticastConsumer.this.accept(value);
                    }
                }));
            }
            Unit unit2 = Unit.INSTANCE;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override // androidx.window.layout.WindowBackend
    public void unregisterLayoutChangeCallback(@NotNull Consumer<WindowLayoutInfo> callback) {
        Intrinsics.checkNotNullParameter(callback, "callback");
        ReentrantLock reentrantLock = this.extensionWindowBackendLock;
        reentrantLock.lock();
        try {
            Activity activity = this.listenerToActivity.get(callback);
            if (activity == null) {
                return;
            }
            MulticastConsumer multicastConsumer = this.activityToListeners.get(activity);
            if (multicastConsumer == null) {
                return;
            }
            multicastConsumer.removeListener(callback);
            if (multicastConsumer.isEmpty()) {
                ConsumerAdapter.Subscription remove = this.consumerToToken.remove(multicastConsumer);
                if (remove != null) {
                    remove.dispose();
                }
                this.listenerToActivity.remove(callback);
                this.activityToListeners.remove(activity);
            }
            Unit unit = Unit.INSTANCE;
        } finally {
            reentrantLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ExtensionWindowLayoutInfoBackend.kt */
    /* loaded from: classes.dex */
    public static final class MulticastConsumer implements Consumer<androidx.window.extensions.layout.WindowLayoutInfo> {
        @NotNull
        private final Activity activity;
        @Nullable
        private WindowLayoutInfo lastKnownValue;
        @NotNull
        private final ReentrantLock multicastConsumerLock;
        @NotNull
        private final Set<Consumer<WindowLayoutInfo>> registeredListeners;

        public MulticastConsumer(@NotNull Activity activity) {
            Intrinsics.checkNotNullParameter(activity, "activity");
            this.activity = activity;
            this.multicastConsumerLock = new ReentrantLock();
            this.registeredListeners = new LinkedHashSet();
        }

        @Override // androidx.core.util.Consumer
        public void accept(@NotNull androidx.window.extensions.layout.WindowLayoutInfo value) {
            Intrinsics.checkNotNullParameter(value, "value");
            ReentrantLock reentrantLock = this.multicastConsumerLock;
            reentrantLock.lock();
            try {
                this.lastKnownValue = ExtensionsWindowLayoutInfoAdapter.INSTANCE.translate$window_release(this.activity, value);
                Iterator<T> it = this.registeredListeners.iterator();
                while (it.hasNext()) {
                    ((Consumer) it.next()).accept(this.lastKnownValue);
                }
                Unit unit = Unit.INSTANCE;
            } finally {
                reentrantLock.unlock();
            }
        }

        public final void addListener(@NotNull Consumer<WindowLayoutInfo> listener) {
            Intrinsics.checkNotNullParameter(listener, "listener");
            ReentrantLock reentrantLock = this.multicastConsumerLock;
            reentrantLock.lock();
            try {
                WindowLayoutInfo windowLayoutInfo = this.lastKnownValue;
                if (windowLayoutInfo != null) {
                    listener.accept(windowLayoutInfo);
                }
                this.registeredListeners.add(listener);
            } finally {
                reentrantLock.unlock();
            }
        }

        public final void removeListener(@NotNull Consumer<WindowLayoutInfo> listener) {
            Intrinsics.checkNotNullParameter(listener, "listener");
            ReentrantLock reentrantLock = this.multicastConsumerLock;
            reentrantLock.lock();
            try {
                this.registeredListeners.remove(listener);
            } finally {
                reentrantLock.unlock();
            }
        }

        public final boolean isEmpty() {
            return this.registeredListeners.isEmpty();
        }
    }
}
