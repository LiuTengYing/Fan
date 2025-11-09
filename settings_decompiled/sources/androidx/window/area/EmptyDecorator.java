package androidx.window.area;

import androidx.window.core.ExperimentalWindowApi;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowAreaController.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
final class EmptyDecorator implements WindowAreaControllerDecorator {
    @NotNull
    public static final EmptyDecorator INSTANCE = new EmptyDecorator();

    @Override // androidx.window.area.WindowAreaControllerDecorator
    @NotNull
    public WindowAreaController decorate(@NotNull WindowAreaController controller) {
        Intrinsics.checkNotNullParameter(controller, "controller");
        return controller;
    }

    private EmptyDecorator() {
    }
}
