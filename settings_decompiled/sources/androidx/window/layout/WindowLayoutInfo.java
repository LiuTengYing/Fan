package androidx.window.layout;

import java.util.List;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: WindowLayoutInfo.kt */
/* loaded from: classes.dex */
public final class WindowLayoutInfo {
    @NotNull
    private final List<DisplayFeature> displayFeatures;

    @NotNull
    public final List<DisplayFeature> getDisplayFeatures() {
        return this.displayFeatures;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public WindowLayoutInfo(@NotNull List<? extends DisplayFeature> displayFeatures) {
        Intrinsics.checkNotNullParameter(displayFeatures, "displayFeatures");
        this.displayFeatures = displayFeatures;
    }

    @NotNull
    public String toString() {
        String joinToString$default;
        joinToString$default = CollectionsKt___CollectionsKt.joinToString$default(this.displayFeatures, ", ", "WindowLayoutInfo{ DisplayFeatures[", "] }", 0, null, null, 56, null);
        return joinToString$default;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !Intrinsics.areEqual(WindowLayoutInfo.class, obj.getClass())) {
            return false;
        }
        return Intrinsics.areEqual(this.displayFeatures, ((WindowLayoutInfo) obj).displayFeatures);
    }

    public int hashCode() {
        return this.displayFeatures.hashCode();
    }
}
