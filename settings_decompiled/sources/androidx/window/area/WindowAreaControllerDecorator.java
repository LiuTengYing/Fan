package androidx.window.area;

import androidx.window.core.ExperimentalWindowApi;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowAreaController.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public interface WindowAreaControllerDecorator {
    @NotNull
    WindowAreaController decorate(@NotNull WindowAreaController windowAreaController);
}
