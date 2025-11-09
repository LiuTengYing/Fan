package androidx.window.layout;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowInfoTracker.kt */
/* loaded from: classes.dex */
final class EmptyDecorator implements WindowInfoTrackerDecorator {
    @NotNull
    public static final EmptyDecorator INSTANCE = new EmptyDecorator();

    @Override // androidx.window.layout.WindowInfoTrackerDecorator
    @NotNull
    public WindowInfoTracker decorate(@NotNull WindowInfoTracker tracker) {
        Intrinsics.checkNotNullParameter(tracker, "tracker");
        return tracker;
    }

    private EmptyDecorator() {
    }
}
