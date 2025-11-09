package androidx.window.area;

import android.app.Activity;
import androidx.window.core.ExperimentalWindowApi;
import java.util.concurrent.Executor;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: EmptyWindowAreaControllerImpl.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class EmptyWindowAreaControllerImpl implements WindowAreaController {
    @Override // androidx.window.area.WindowAreaController
    @NotNull
    public Flow<WindowAreaStatus> rearDisplayStatus() {
        return FlowKt.flowOf(WindowAreaStatus.UNSUPPORTED);
    }

    @Override // androidx.window.area.WindowAreaController
    public void rearDisplayMode(@NotNull Activity activity, @NotNull Executor executor, @NotNull WindowAreaSessionCallback windowAreaSessionCallback) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(windowAreaSessionCallback, "windowAreaSessionCallback");
        throw WindowAreaController.Companion.getREAR_DISPLAY_ERROR$window_release();
    }
}
