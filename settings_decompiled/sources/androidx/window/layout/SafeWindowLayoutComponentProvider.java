package androidx.window.layout;

import android.app.Activity;
import android.graphics.Rect;
import androidx.window.core.ConsumerAdapter;
import androidx.window.extensions.WindowExtensionsProvider;
import androidx.window.extensions.layout.WindowLayoutComponent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SafeWindowLayoutComponentProvider.kt */
/* loaded from: classes.dex */
public final class SafeWindowLayoutComponentProvider {
    @NotNull
    private final ConsumerAdapter consumerAdapter;
    @NotNull
    private final ClassLoader loader;

    public SafeWindowLayoutComponentProvider(@NotNull ClassLoader loader, @NotNull ConsumerAdapter consumerAdapter) {
        Intrinsics.checkNotNullParameter(loader, "loader");
        Intrinsics.checkNotNullParameter(consumerAdapter, "consumerAdapter");
        this.loader = loader;
        this.consumerAdapter = consumerAdapter;
    }

    @Nullable
    public final WindowLayoutComponent getWindowLayoutComponent() {
        if (canUseWindowLayoutComponent()) {
            try {
                return WindowExtensionsProvider.getWindowExtensions().getWindowLayoutComponent();
            } catch (UnsupportedOperationException unused) {
                return null;
            }
        }
        return null;
    }

    private final boolean canUseWindowLayoutComponent() {
        return isWindowLayoutProviderValid() && isWindowExtensionsValid() && isWindowLayoutComponentValid() && isFoldingFeatureValid();
    }

    private final boolean isWindowLayoutProviderValid() {
        return validate(new Function0<Boolean>() { // from class: androidx.window.layout.SafeWindowLayoutComponentProvider$isWindowLayoutProviderValid$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            @NotNull
            public final Boolean invoke() {
                Class windowExtensionsProviderClass;
                Class windowExtensionsClass;
                boolean doesReturn;
                boolean isPublic;
                windowExtensionsProviderClass = SafeWindowLayoutComponentProvider.this.getWindowExtensionsProviderClass();
                boolean z = false;
                Method getWindowExtensionsMethod = windowExtensionsProviderClass.getDeclaredMethod("getWindowExtensions", new Class[0]);
                windowExtensionsClass = SafeWindowLayoutComponentProvider.this.getWindowExtensionsClass();
                SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.this;
                Intrinsics.checkNotNullExpressionValue(getWindowExtensionsMethod, "getWindowExtensionsMethod");
                doesReturn = safeWindowLayoutComponentProvider.doesReturn(getWindowExtensionsMethod, windowExtensionsClass);
                if (doesReturn) {
                    isPublic = SafeWindowLayoutComponentProvider.this.isPublic(getWindowExtensionsMethod);
                    if (isPublic) {
                        z = true;
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    private final boolean isWindowExtensionsValid() {
        return validate(new Function0<Boolean>() { // from class: androidx.window.layout.SafeWindowLayoutComponentProvider$isWindowExtensionsValid$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            @NotNull
            public final Boolean invoke() {
                Class windowExtensionsClass;
                Class windowLayoutComponentClass;
                boolean isPublic;
                boolean doesReturn;
                windowExtensionsClass = SafeWindowLayoutComponentProvider.this.getWindowExtensionsClass();
                boolean z = false;
                Method getWindowLayoutComponentMethod = windowExtensionsClass.getMethod("getWindowLayoutComponent", new Class[0]);
                windowLayoutComponentClass = SafeWindowLayoutComponentProvider.this.getWindowLayoutComponentClass();
                SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.this;
                Intrinsics.checkNotNullExpressionValue(getWindowLayoutComponentMethod, "getWindowLayoutComponentMethod");
                isPublic = safeWindowLayoutComponentProvider.isPublic(getWindowLayoutComponentMethod);
                if (isPublic) {
                    doesReturn = SafeWindowLayoutComponentProvider.this.doesReturn(getWindowLayoutComponentMethod, windowLayoutComponentClass);
                    if (doesReturn) {
                        z = true;
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    private final boolean isFoldingFeatureValid() {
        return validate(new Function0<Boolean>() { // from class: androidx.window.layout.SafeWindowLayoutComponentProvider$isFoldingFeatureValid$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            @NotNull
            public final Boolean invoke() {
                Class foldingFeatureClass;
                boolean doesReturn;
                boolean isPublic;
                boolean doesReturn2;
                boolean isPublic2;
                boolean doesReturn3;
                boolean isPublic3;
                foldingFeatureClass = SafeWindowLayoutComponentProvider.this.getFoldingFeatureClass();
                boolean z = false;
                Method getBoundsMethod = foldingFeatureClass.getMethod("getBounds", new Class[0]);
                Method getTypeMethod = foldingFeatureClass.getMethod("getType", new Class[0]);
                Method getStateMethod = foldingFeatureClass.getMethod("getState", new Class[0]);
                SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.this;
                Intrinsics.checkNotNullExpressionValue(getBoundsMethod, "getBoundsMethod");
                doesReturn = safeWindowLayoutComponentProvider.doesReturn(getBoundsMethod, Reflection.getOrCreateKotlinClass(Rect.class));
                if (doesReturn) {
                    isPublic = SafeWindowLayoutComponentProvider.this.isPublic(getBoundsMethod);
                    if (isPublic) {
                        SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider2 = SafeWindowLayoutComponentProvider.this;
                        Intrinsics.checkNotNullExpressionValue(getTypeMethod, "getTypeMethod");
                        Class cls = Integer.TYPE;
                        doesReturn2 = safeWindowLayoutComponentProvider2.doesReturn(getTypeMethod, Reflection.getOrCreateKotlinClass(cls));
                        if (doesReturn2) {
                            isPublic2 = SafeWindowLayoutComponentProvider.this.isPublic(getTypeMethod);
                            if (isPublic2) {
                                SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider3 = SafeWindowLayoutComponentProvider.this;
                                Intrinsics.checkNotNullExpressionValue(getStateMethod, "getStateMethod");
                                doesReturn3 = safeWindowLayoutComponentProvider3.doesReturn(getStateMethod, Reflection.getOrCreateKotlinClass(cls));
                                if (doesReturn3) {
                                    isPublic3 = SafeWindowLayoutComponentProvider.this.isPublic(getStateMethod);
                                    if (isPublic3) {
                                        z = true;
                                    }
                                }
                            }
                        }
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    private final boolean isWindowLayoutComponentValid() {
        return validate(new Function0<Boolean>() { // from class: androidx.window.layout.SafeWindowLayoutComponentProvider$isWindowLayoutComponentValid$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(0);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            @NotNull
            public final Boolean invoke() {
                ConsumerAdapter consumerAdapter;
                Class windowLayoutComponentClass;
                boolean isPublic;
                boolean isPublic2;
                consumerAdapter = SafeWindowLayoutComponentProvider.this.consumerAdapter;
                Class<?> consumerClassOrNull$window_release = consumerAdapter.consumerClassOrNull$window_release();
                if (consumerClassOrNull$window_release == null) {
                    return Boolean.FALSE;
                }
                windowLayoutComponentClass = SafeWindowLayoutComponentProvider.this.getWindowLayoutComponentClass();
                boolean z = false;
                Method addListenerMethod = windowLayoutComponentClass.getMethod("addWindowLayoutInfoListener", Activity.class, consumerClassOrNull$window_release);
                Method removeListenerMethod = windowLayoutComponentClass.getMethod("removeWindowLayoutInfoListener", consumerClassOrNull$window_release);
                SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider = SafeWindowLayoutComponentProvider.this;
                Intrinsics.checkNotNullExpressionValue(addListenerMethod, "addListenerMethod");
                isPublic = safeWindowLayoutComponentProvider.isPublic(addListenerMethod);
                if (isPublic) {
                    SafeWindowLayoutComponentProvider safeWindowLayoutComponentProvider2 = SafeWindowLayoutComponentProvider.this;
                    Intrinsics.checkNotNullExpressionValue(removeListenerMethod, "removeListenerMethod");
                    isPublic2 = safeWindowLayoutComponentProvider2.isPublic(removeListenerMethod);
                    if (isPublic2) {
                        z = true;
                    }
                }
                return Boolean.valueOf(z);
            }
        });
    }

    private final boolean validate(Function0<Boolean> function0) {
        try {
            return function0.invoke().booleanValue();
        } catch (ClassNotFoundException | NoSuchMethodException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean doesReturn(Method method, KClass<?> kClass) {
        return doesReturn(method, JvmClassMappingKt.getJavaClass(kClass));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean doesReturn(Method method, Class<?> cls) {
        return method.getReturnType().equals(cls);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Class<?> getWindowExtensionsProviderClass() {
        Class<?> loadClass = this.loader.loadClass("androidx.window.extensions.WindowExtensionsProvider");
        Intrinsics.checkNotNullExpressionValue(loadClass, "loader.loadClass(\"androi…indowExtensionsProvider\")");
        return loadClass;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Class<?> getWindowExtensionsClass() {
        Class<?> loadClass = this.loader.loadClass("androidx.window.extensions.WindowExtensions");
        Intrinsics.checkNotNullExpressionValue(loadClass, "loader.loadClass(\"androi…nsions.WindowExtensions\")");
        return loadClass;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Class<?> getFoldingFeatureClass() {
        Class<?> loadClass = this.loader.loadClass("androidx.window.extensions.layout.FoldingFeature");
        Intrinsics.checkNotNullExpressionValue(loadClass, "loader.loadClass(\"androi…s.layout.FoldingFeature\")");
        return loadClass;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Class<?> getWindowLayoutComponentClass() {
        Class<?> loadClass = this.loader.loadClass("androidx.window.extensions.layout.WindowLayoutComponent");
        Intrinsics.checkNotNullExpressionValue(loadClass, "loader.loadClass(\"androi…t.WindowLayoutComponent\")");
        return loadClass;
    }
}
