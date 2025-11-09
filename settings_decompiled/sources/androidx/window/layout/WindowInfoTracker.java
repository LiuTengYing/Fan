package androidx.window.layout;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.window.core.ConsumerAdapter;
import androidx.window.extensions.layout.WindowLayoutComponent;
import androidx.window.layout.WindowInfoTracker;
import kotlin.Lazy;
import kotlin.LazyKt__LazyJVMKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlinx.coroutines.flow.Flow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: WindowInfoTracker.kt */
/* loaded from: classes.dex */
public interface WindowInfoTracker {
    @NotNull
    public static final Companion Companion = Companion.$$INSTANCE;

    @NotNull
    static WindowInfoTracker getOrCreate(@NotNull Context context) {
        return Companion.getOrCreate(context);
    }

    static void overrideDecorator(@NotNull WindowInfoTrackerDecorator windowInfoTrackerDecorator) {
        Companion.overrideDecorator(windowInfoTrackerDecorator);
    }

    static void reset() {
        Companion.reset();
    }

    @NotNull
    Flow<WindowLayoutInfo> windowLayoutInfo(@NotNull Activity activity);

    /* compiled from: WindowInfoTracker.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        private static final boolean DEBUG = false;
        @NotNull
        private static WindowInfoTrackerDecorator decorator;
        @NotNull
        private static final Lazy<ExtensionWindowLayoutInfoBackend> extensionBackend$delegate;
        static final /* synthetic */ Companion $$INSTANCE = new Companion();
        @Nullable
        private static final String TAG = Reflection.getOrCreateKotlinClass(WindowInfoTracker.class).getSimpleName();

        public static /* synthetic */ void getExtensionBackend$window_release$annotations() {
        }

        private Companion() {
        }

        static {
            Lazy<ExtensionWindowLayoutInfoBackend> lazy;
            lazy = LazyKt__LazyJVMKt.lazy(new Function0<ExtensionWindowLayoutInfoBackend>() { // from class: androidx.window.layout.WindowInfoTracker$Companion$extensionBackend$2
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // kotlin.jvm.functions.Function0
                @Nullable
                public final ExtensionWindowLayoutInfoBackend invoke() {
                    boolean z;
                    String str;
                    WindowLayoutComponent windowLayoutComponent;
                    try {
                        ClassLoader loader = WindowInfoTracker.class.getClassLoader();
                        SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = loader != null ? new SafeWindowLayoutComponentProvider(loader, new ConsumerAdapter(loader)) : null;
                        if (safeWindowLayoutComponentProvider == null || (windowLayoutComponent = safeWindowLayoutComponentProvider.getWindowLayoutComponent()) == null) {
                            return null;
                        }
                        Intrinsics.checkNotNullExpressionValue(loader, "loader");
                        return new ExtensionWindowLayoutInfoBackend(windowLayoutComponent, new ConsumerAdapter(loader));
                    } catch (Throwable unused) {
                        z = WindowInfoTracker.Companion.DEBUG;
                        if (z) {
                            str = WindowInfoTracker.Companion.TAG;
                            Log.d(str, "Failed to load WindowExtensions");
                            return null;
                        }
                        return null;
                    }
                }
            });
            extensionBackend$delegate = lazy;
            decorator = EmptyDecorator.INSTANCE;
        }

        @Nullable
        public final WindowBackend getExtensionBackend$window_release() {
            return extensionBackend$delegate.getValue();
        }

        @NotNull
        public final WindowInfoTracker getOrCreate(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            WindowBackend extensionBackend$window_release = getExtensionBackend$window_release();
            if (extensionBackend$window_release == null) {
                extensionBackend$window_release = SidecarWindowBackend.Companion.getInstance(context);
            }
            return decorator.decorate(new WindowInfoTrackerImpl(WindowMetricsCalculatorCompat.INSTANCE, extensionBackend$window_release));
        }

        public final void overrideDecorator(@NotNull WindowInfoTrackerDecorator overridingDecorator) {
            Intrinsics.checkNotNullParameter(overridingDecorator, "overridingDecorator");
            decorator = overridingDecorator;
        }

        public final void reset() {
            decorator = EmptyDecorator.INSTANCE;
        }
    }
}
