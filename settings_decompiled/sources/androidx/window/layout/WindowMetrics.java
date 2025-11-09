package androidx.window.layout;

import android.graphics.Rect;
import androidx.window.core.Bounds;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: WindowMetrics.kt */
/* loaded from: classes.dex */
public final class WindowMetrics {
    @NotNull
    private final Bounds _bounds;

    public WindowMetrics(@NotNull Bounds _bounds) {
        Intrinsics.checkNotNullParameter(_bounds, "_bounds");
        this._bounds = _bounds;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public WindowMetrics(@NotNull Rect bounds) {
        this(new Bounds(bounds));
        Intrinsics.checkNotNullParameter(bounds, "bounds");
    }

    @NotNull
    public final Rect getBounds() {
        return this._bounds.toRect();
    }

    @NotNull
    public String toString() {
        return "WindowMetrics { bounds: " + getBounds() + " }";
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !Intrinsics.areEqual(WindowMetrics.class, obj.getClass())) {
            return false;
        }
        return Intrinsics.areEqual(this._bounds, ((WindowMetrics) obj)._bounds);
    }

    public int hashCode() {
        return this._bounds.hashCode();
    }
}
