package androidx.window.embedding;

import android.graphics.Rect;
import android.view.WindowMetrics;
import androidx.window.core.ExperimentalWindowApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SplitRule.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public class SplitRule extends EmbeddingRule {
    @NotNull
    public static final Companion Companion = new Companion(null);
    public static final int FINISH_ADJACENT = 2;
    public static final int FINISH_ALWAYS = 1;
    public static final int FINISH_NEVER = 0;
    private final int layoutDirection;
    private final int minSmallestWidth;
    private final int minWidth;
    private final float splitRatio;

    /* compiled from: SplitRule.kt */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LayoutDir {
    }

    /* compiled from: SplitRule.kt */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SplitFinishBehavior {
    }

    public SplitRule() {
        this(0, 0, 0.0f, 0, 15, null);
    }

    public /* synthetic */ SplitRule(int i, int i2, float f, int i3, int i4, DefaultConstructorMarker defaultConstructorMarker) {
        this((i4 & 1) != 0 ? 0 : i, (i4 & 2) != 0 ? 0 : i2, (i4 & 4) != 0 ? 0.5f : f, (i4 & 8) != 0 ? 3 : i3);
    }

    public final int getMinWidth() {
        return this.minWidth;
    }

    public final int getMinSmallestWidth() {
        return this.minSmallestWidth;
    }

    public final float getSplitRatio() {
        return this.splitRatio;
    }

    public final int getLayoutDirection() {
        return this.layoutDirection;
    }

    public SplitRule(int i, int i2, float f, int i3) {
        this.minWidth = i;
        this.minSmallestWidth = i2;
        this.splitRatio = f;
        this.layoutDirection = i3;
    }

    /* compiled from: SplitRule.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public final boolean checkParentMetrics(@NotNull WindowMetrics parentMetrics) {
        Intrinsics.checkNotNullParameter(parentMetrics, "parentMetrics");
        Rect bounds = Api30Impl.INSTANCE.getBounds(parentMetrics);
        return (this.minWidth == 0 || bounds.width() >= this.minWidth) && (this.minSmallestWidth == 0 || Math.min(bounds.width(), bounds.height()) >= this.minSmallestWidth);
    }

    /* compiled from: SplitRule.kt */
    /* loaded from: classes.dex */
    public static final class Api30Impl {
        @NotNull
        public static final Api30Impl INSTANCE = new Api30Impl();

        private Api30Impl() {
        }

        @NotNull
        public final Rect getBounds(@NotNull WindowMetrics windowMetrics) {
            Intrinsics.checkNotNullParameter(windowMetrics, "windowMetrics");
            Rect bounds = windowMetrics.getBounds();
            Intrinsics.checkNotNullExpressionValue(bounds, "windowMetrics.bounds");
            return bounds;
        }
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SplitRule) {
            SplitRule splitRule = (SplitRule) obj;
            if (this.minWidth == splitRule.minWidth && this.minSmallestWidth == splitRule.minSmallestWidth) {
                return ((this.splitRatio > splitRule.splitRatio ? 1 : (this.splitRatio == splitRule.splitRatio ? 0 : -1)) == 0) && this.layoutDirection == splitRule.layoutDirection;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return (((((this.minWidth * 31) + this.minSmallestWidth) * 31) + Float.hashCode(this.splitRatio)) * 31) + this.layoutDirection;
    }
}
