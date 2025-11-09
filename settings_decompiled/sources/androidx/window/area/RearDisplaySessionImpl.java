package androidx.window.area;

import androidx.window.core.ExperimentalWindowApi;
import androidx.window.extensions.area.WindowAreaComponent;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: RearDisplaySessionImpl.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class RearDisplaySessionImpl implements WindowAreaSession {
    @NotNull
    private final WindowAreaComponent windowAreaComponent;

    public RearDisplaySessionImpl(@NotNull WindowAreaComponent windowAreaComponent) {
        Intrinsics.checkNotNullParameter(windowAreaComponent, "windowAreaComponent");
        this.windowAreaComponent = windowAreaComponent;
    }

    @Override // androidx.window.area.WindowAreaSession
    public void close() {
        this.windowAreaComponent.endRearDisplaySession();
    }
}
