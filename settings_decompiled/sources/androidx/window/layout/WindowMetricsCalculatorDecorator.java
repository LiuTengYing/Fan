package androidx.window.layout;

import androidx.window.core.ExperimentalWindowApi;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowMetricsCalculator.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public interface WindowMetricsCalculatorDecorator {
    @ExperimentalWindowApi
    @NotNull
    WindowMetricsCalculator decorate(@NotNull WindowMetricsCalculator windowMetricsCalculator);
}
