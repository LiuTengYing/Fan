package androidx.window.layout;

import android.app.Activity;
import android.graphics.Rect;
import androidx.window.core.Bounds;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.HardwareFoldingFeature;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ExtensionsWindowLayoutInfoAdapter.kt */
/* loaded from: classes.dex */
public final class ExtensionsWindowLayoutInfoAdapter {
    @NotNull
    public static final ExtensionsWindowLayoutInfoAdapter INSTANCE = new ExtensionsWindowLayoutInfoAdapter();

    private ExtensionsWindowLayoutInfoAdapter() {
    }

    @Nullable
    public final FoldingFeature translate$window_release(@NotNull Activity activity, @NotNull androidx.window.extensions.layout.FoldingFeature oemFeature) {
        HardwareFoldingFeature.Type fold;
        FoldingFeature.State state;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(oemFeature, "oemFeature");
        int type = oemFeature.getType();
        if (type == 1) {
            fold = HardwareFoldingFeature.Type.Companion.getFOLD();
        } else if (type != 2) {
            return null;
        } else {
            fold = HardwareFoldingFeature.Type.Companion.getHINGE();
        }
        int state2 = oemFeature.getState();
        if (state2 == 1) {
            state = FoldingFeature.State.FLAT;
        } else if (state2 != 2) {
            return null;
        } else {
            state = FoldingFeature.State.HALF_OPENED;
        }
        Rect bounds = oemFeature.getBounds();
        Intrinsics.checkNotNullExpressionValue(bounds, "oemFeature.bounds");
        if (validBounds(activity, new Bounds(bounds))) {
            Rect bounds2 = oemFeature.getBounds();
            Intrinsics.checkNotNullExpressionValue(bounds2, "oemFeature.bounds");
            return new HardwareFoldingFeature(new Bounds(bounds2), fold, state);
        }
        return null;
    }

    @NotNull
    public final WindowLayoutInfo translate$window_release(@NotNull Activity activity, @NotNull androidx.window.extensions.layout.WindowLayoutInfo info) {
        FoldingFeature foldingFeature;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(info, "info");
        List<androidx.window.extensions.layout.FoldingFeature> displayFeatures = info.getDisplayFeatures();
        Intrinsics.checkNotNullExpressionValue(displayFeatures, "info.displayFeatures");
        ArrayList arrayList = new ArrayList();
        for (androidx.window.extensions.layout.FoldingFeature feature : displayFeatures) {
            if (feature instanceof androidx.window.extensions.layout.FoldingFeature) {
                ExtensionsWindowLayoutInfoAdapter extensionsWindowLayoutInfoAdapter = INSTANCE;
                Intrinsics.checkNotNullExpressionValue(feature, "feature");
                foldingFeature = extensionsWindowLayoutInfoAdapter.translate$window_release(activity, feature);
            } else {
                foldingFeature = null;
            }
            if (foldingFeature != null) {
                arrayList.add(foldingFeature);
            }
        }
        return new WindowLayoutInfo(arrayList);
    }

    private final boolean validBounds(Activity activity, Bounds bounds) {
        Rect bounds2 = WindowMetricsCalculatorCompat.INSTANCE.computeCurrentWindowMetrics(activity).getBounds();
        if (bounds.isZero()) {
            return false;
        }
        if (bounds.getWidth() == bounds2.width() || bounds.getHeight() == bounds2.height()) {
            if (bounds.getWidth() >= bounds2.width() || bounds.getHeight() >= bounds2.height()) {
                return (bounds.getWidth() == bounds2.width() && bounds.getHeight() == bounds2.height()) ? false : true;
            }
            return false;
        }
        return false;
    }
}
