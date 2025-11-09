package androidx.lifecycle;

import android.app.Application;
import java.lang.reflect.InvocationTargetException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ViewModelProvider.kt */
/* loaded from: classes.dex */
public class ViewModelProvider {
    @NotNull
    private final Factory factory;
    @NotNull
    private final ViewModelStore store;

    /* compiled from: ViewModelProvider.kt */
    /* loaded from: classes.dex */
    public interface Factory {
        @NotNull
        <T extends ViewModel> T create(@NotNull Class<T> cls);
    }

    /* compiled from: ViewModelProvider.kt */
    /* loaded from: classes.dex */
    public static class OnRequeryFactory {
        public void onRequery(@NotNull ViewModel viewModel) {
            Intrinsics.checkNotNullParameter(viewModel, "viewModel");
        }
    }

    public ViewModelProvider(@NotNull ViewModelStore store, @NotNull Factory factory) {
        Intrinsics.checkNotNullParameter(store, "store");
        Intrinsics.checkNotNullParameter(factory, "factory");
        this.store = store;
        this.factory = factory;
    }

    /* compiled from: ViewModelProvider.kt */
    /* loaded from: classes.dex */
    public static abstract class KeyedFactory extends OnRequeryFactory implements Factory {
        @NotNull
        public abstract <T extends ViewModel> T create(@NotNull String str, @NotNull Class<T> cls);

        @NotNull
        public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
            Intrinsics.checkNotNullParameter(modelClass, "modelClass");
            throw new UnsupportedOperationException("create(String, Class<?>) must be called on implementations of KeyedFactory");
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ViewModelProvider(@org.jetbrains.annotations.NotNull androidx.lifecycle.ViewModelStoreOwner r3) {
        /*
            r2 = this;
            java.lang.String r0 = "owner"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r3, r0)
            androidx.lifecycle.ViewModelStore r0 = r3.getViewModelStore()
            java.lang.String r1 = "owner.viewModelStore"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)
            androidx.lifecycle.ViewModelProvider$AndroidViewModelFactory$Companion r1 = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion
            androidx.lifecycle.ViewModelProvider$Factory r3 = r1.defaultFactory$lifecycle_viewmodel_release(r3)
            r2.<init>(r0, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.lifecycle.ViewModelProvider.<init>(androidx.lifecycle.ViewModelStoreOwner):void");
    }

    @NotNull
    public <T extends ViewModel> T get(@NotNull Class<T> modelClass) {
        Intrinsics.checkNotNullParameter(modelClass, "modelClass");
        String canonicalName = modelClass.getCanonicalName();
        if (canonicalName == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
        }
        return (T) get(Intrinsics.stringPlus("androidx.lifecycle.ViewModelProvider.DefaultKey:", canonicalName), modelClass);
    }

    @NotNull
    public <T extends ViewModel> T get(@NotNull String key, @NotNull Class<T> modelClass) {
        T viewModel;
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(modelClass, "modelClass");
        T viewModel2 = (T) this.store.get(key);
        if (modelClass.isInstance(viewModel2)) {
            Factory factory = this.factory;
            OnRequeryFactory onRequeryFactory = factory instanceof OnRequeryFactory ? (OnRequeryFactory) factory : null;
            if (onRequeryFactory != null) {
                Intrinsics.checkNotNullExpressionValue(viewModel2, "viewModel");
                onRequeryFactory.onRequery(viewModel2);
            }
            if (viewModel2 != null) {
                return viewModel2;
            }
            throw new NullPointerException("null cannot be cast to non-null type T of androidx.lifecycle.ViewModelProvider.get");
        }
        Factory factory2 = this.factory;
        if (factory2 instanceof KeyedFactory) {
            viewModel = (T) ((KeyedFactory) factory2).create(key, modelClass);
        } else {
            viewModel = (T) factory2.create(modelClass);
        }
        this.store.put(key, viewModel);
        Intrinsics.checkNotNullExpressionValue(viewModel, "viewModel");
        return viewModel;
    }

    /* compiled from: ViewModelProvider.kt */
    /* loaded from: classes.dex */
    public static class NewInstanceFactory implements Factory {
        @NotNull
        public static final Companion Companion = new Companion(null);
        @Nullable
        private static NewInstanceFactory sInstance;

        @NotNull
        public static final NewInstanceFactory getInstance() {
            return Companion.getInstance();
        }

        @Override // androidx.lifecycle.ViewModelProvider.Factory
        @NotNull
        public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
            Intrinsics.checkNotNullParameter(modelClass, "modelClass");
            try {
                T newInstance = modelClass.newInstance();
                Intrinsics.checkNotNullExpressionValue(newInstance, "{\n                modelC…wInstance()\n            }");
                return newInstance;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(Intrinsics.stringPlus("Cannot create an instance of ", modelClass), e);
            } catch (InstantiationException e2) {
                throw new RuntimeException(Intrinsics.stringPlus("Cannot create an instance of ", modelClass), e2);
            }
        }

        /* compiled from: ViewModelProvider.kt */
        /* loaded from: classes.dex */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @NotNull
            public final NewInstanceFactory getInstance() {
                if (NewInstanceFactory.sInstance == null) {
                    NewInstanceFactory.sInstance = new NewInstanceFactory();
                }
                NewInstanceFactory newInstanceFactory = NewInstanceFactory.sInstance;
                Intrinsics.checkNotNull(newInstanceFactory);
                return newInstanceFactory;
            }
        }
    }

    /* compiled from: ViewModelProvider.kt */
    /* loaded from: classes.dex */
    public static class AndroidViewModelFactory extends NewInstanceFactory {
        @NotNull
        public static final Companion Companion = new Companion(null);
        @Nullable
        private static AndroidViewModelFactory sInstance;
        @NotNull
        private final Application application;

        @NotNull
        public static final AndroidViewModelFactory getInstance(@NotNull Application application) {
            return Companion.getInstance(application);
        }

        public AndroidViewModelFactory(@NotNull Application application) {
            Intrinsics.checkNotNullParameter(application, "application");
            this.application = application;
        }

        @Override // androidx.lifecycle.ViewModelProvider.NewInstanceFactory, androidx.lifecycle.ViewModelProvider.Factory
        @NotNull
        public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
            Intrinsics.checkNotNullParameter(modelClass, "modelClass");
            if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
                try {
                    T newInstance = modelClass.getConstructor(Application.class).newInstance(this.application);
                    Intrinsics.checkNotNullExpressionValue(newInstance, "{\n                try {\n…          }\n            }");
                    return newInstance;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(Intrinsics.stringPlus("Cannot create an instance of ", modelClass), e);
                } catch (InstantiationException e2) {
                    throw new RuntimeException(Intrinsics.stringPlus("Cannot create an instance of ", modelClass), e2);
                } catch (NoSuchMethodException e3) {
                    throw new RuntimeException(Intrinsics.stringPlus("Cannot create an instance of ", modelClass), e3);
                } catch (InvocationTargetException e4) {
                    throw new RuntimeException(Intrinsics.stringPlus("Cannot create an instance of ", modelClass), e4);
                }
            }
            return (T) super.create(modelClass);
        }

        /* compiled from: ViewModelProvider.kt */
        /* loaded from: classes.dex */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @NotNull
            public final Factory defaultFactory$lifecycle_viewmodel_release(@NotNull ViewModelStoreOwner owner) {
                Intrinsics.checkNotNullParameter(owner, "owner");
                if (owner instanceof HasDefaultViewModelProviderFactory) {
                    Factory defaultViewModelProviderFactory = ((HasDefaultViewModelProviderFactory) owner).getDefaultViewModelProviderFactory();
                    Intrinsics.checkNotNullExpressionValue(defaultViewModelProviderFactory, "owner.defaultViewModelProviderFactory");
                    return defaultViewModelProviderFactory;
                }
                return NewInstanceFactory.Companion.getInstance();
            }

            @NotNull
            public final AndroidViewModelFactory getInstance(@NotNull Application application) {
                Intrinsics.checkNotNullParameter(application, "application");
                if (AndroidViewModelFactory.sInstance == null) {
                    AndroidViewModelFactory.sInstance = new AndroidViewModelFactory(application);
                }
                AndroidViewModelFactory androidViewModelFactory = AndroidViewModelFactory.sInstance;
                Intrinsics.checkNotNull(androidViewModelFactory);
                return androidViewModelFactory;
            }
        }
    }
}
