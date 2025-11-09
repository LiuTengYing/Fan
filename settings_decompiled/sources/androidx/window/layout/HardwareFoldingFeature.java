package androidx.window.layout;

import android.graphics.Rect;
import androidx.window.core.Bounds;
import androidx.window.layout.FoldingFeature;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: HardwareFoldingFeature.kt */
/* loaded from: classes.dex */
public final class HardwareFoldingFeature implements FoldingFeature {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Bounds featureBounds;
    @NotNull
    private final FoldingFeature.State state;
    @NotNull
    private final Type type;

    public HardwareFoldingFeature(@NotNull Bounds featureBounds, @NotNull Type type, @NotNull FoldingFeature.State state) {
        Intrinsics.checkNotNullParameter(featureBounds, "featureBounds");
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(state, "state");
        this.featureBounds = featureBounds;
        this.type = type;
        this.state = state;
        Companion.validateFeatureBounds$window_release(featureBounds);
    }

    @NotNull
    public final Type getType$window_release() {
        return this.type;
    }

    @Override // androidx.window.layout.FoldingFeature
    @NotNull
    public FoldingFeature.State getState() {
        return this.state;
    }

    @Override // androidx.window.layout.DisplayFeature
    @NotNull
    public Rect getBounds() {
        return this.featureBounds.toRect();
    }

    @Override // androidx.window.layout.FoldingFeature
    public boolean isSeparating() {
        Type type = this.type;
        Type.Companion companion = Type.Companion;
        if (Intrinsics.areEqual(type, companion.getHINGE())) {
            return true;
        }
        return Intrinsics.areEqual(this.type, companion.getFOLD()) && Intrinsics.areEqual(getState(), FoldingFeature.State.HALF_OPENED);
    }

    @Override // androidx.window.layout.FoldingFeature
    @NotNull
    public FoldingFeature.OcclusionType getOcclusionType() {
        if (this.featureBounds.getWidth() == 0 || this.featureBounds.getHeight() == 0) {
            return FoldingFeature.OcclusionType.NONE;
        }
        return FoldingFeature.OcclusionType.FULL;
    }

    @Override // androidx.window.layout.FoldingFeature
    @NotNull
    public FoldingFeature.Orientation getOrientation() {
        if (this.featureBounds.getWidth() > this.featureBounds.getHeight()) {
            return FoldingFeature.Orientation.HORIZONTAL;
        }
        return FoldingFeature.Orientation.VERTICAL;
    }

    @NotNull
    public String toString() {
        return HardwareFoldingFeature.class.getSimpleName() + " { " + this.featureBounds + ", type=" + this.type + ", state=" + getState() + " }";
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (Intrinsics.areEqual(HardwareFoldingFeature.class, obj != null ? obj.getClass() : null)) {
            if (obj != null) {
                HardwareFoldingFeature hardwareFoldingFeature = (HardwareFoldingFeature) obj;
                return Intrinsics.areEqual(this.featureBounds, hardwareFoldingFeature.featureBounds) && Intrinsics.areEqual(this.type, hardwareFoldingFeature.type) && Intrinsics.areEqual(getState(), hardwareFoldingFeature.getState());
            }
            throw new NullPointerException("null cannot be cast to non-null type androidx.window.layout.HardwareFoldingFeature");
        }
        return false;
    }

    public int hashCode() {
        return (((this.featureBounds.hashCode() * 31) + this.type.hashCode()) * 31) + getState().hashCode();
    }

    /* compiled from: HardwareFoldingFeature.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final void validateFeatureBounds$window_release(@NotNull Bounds bounds) {
            Intrinsics.checkNotNullParameter(bounds, "bounds");
            boolean z = false;
            if (!((bounds.getWidth() == 0 && bounds.getHeight() == 0) ? false : true)) {
                throw new IllegalArgumentException("Bounds must be non zero".toString());
            }
            if (bounds.getLeft() == 0 || bounds.getTop() == 0) {
                z = true;
            }
            if (!z) {
                throw new IllegalArgumentException("Bounding rectangle must start at the top or left window edge for folding features".toString());
            }
        }
    }

    /* compiled from: HardwareFoldingFeature.kt */
    /* loaded from: classes.dex */
    public static final class Type {
        @NotNull
        public static final Companion Companion = new Companion(null);
        @NotNull
        private static final Type FOLD = new Type("FOLD");
        @NotNull
        private static final Type HINGE = new Type("HINGE");
        @NotNull
        private final String description;

        private Type(String str) {
            this.description = str;
        }

        @NotNull
        public String toString() {
            return this.description;
        }

        /* compiled from: HardwareFoldingFeature.kt */
        /* loaded from: classes.dex */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @NotNull
            public final Type getFOLD() {
                return Type.FOLD;
            }

            @NotNull
            public final Type getHINGE() {
                return Type.HINGE;
            }
        }
    }
}
