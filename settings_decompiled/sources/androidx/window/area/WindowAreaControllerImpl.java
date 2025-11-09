package androidx.window.area;

import android.app.Activity;
import android.util.Log;
import androidx.window.core.BuildConfig;
import androidx.window.core.ExperimentalWindowApi;
import androidx.window.core.SpecificationComputer;
import androidx.window.extensions.area.WindowAreaComponent;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: WindowAreaControllerImpl.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class WindowAreaControllerImpl implements WindowAreaController {
    private static final int BUFFER_CAPACITY = 10;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private static final String TAG = Reflection.getOrCreateKotlinClass(WindowAreaControllerImpl.class).getSimpleName();
    @Nullable
    private WindowAreaStatus currentStatus;
    private Consumer<Integer> rearDisplaySessionConsumer;
    @NotNull
    private final WindowAreaComponent windowAreaComponent;

    public WindowAreaControllerImpl(@NotNull WindowAreaComponent windowAreaComponent) {
        Intrinsics.checkNotNullParameter(windowAreaComponent, "windowAreaComponent");
        this.windowAreaComponent = windowAreaComponent;
    }

    @Override // androidx.window.area.WindowAreaController
    @NotNull
    public Flow<WindowAreaStatus> rearDisplayStatus() {
        return FlowKt.distinctUntilChanged(FlowKt.flow(new WindowAreaControllerImpl$rearDisplayStatus$1(this, null)));
    }

    @Override // androidx.window.area.WindowAreaController
    public void rearDisplayMode(@NotNull Activity activity, @NotNull Executor executor, @NotNull WindowAreaSessionCallback windowAreaSessionCallback) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(windowAreaSessionCallback, "windowAreaSessionCallback");
        WindowAreaStatus windowAreaStatus = this.currentStatus;
        if (windowAreaStatus != null && !Intrinsics.areEqual(windowAreaStatus, WindowAreaStatus.AVAILABLE)) {
            throw WindowAreaController.Companion.getREAR_DISPLAY_ERROR$window_release();
        }
        RearDisplaySessionConsumer rearDisplaySessionConsumer = new RearDisplaySessionConsumer(windowAreaSessionCallback, this.windowAreaComponent);
        this.rearDisplaySessionConsumer = rearDisplaySessionConsumer;
        this.windowAreaComponent.startRearDisplaySession(activity, rearDisplaySessionConsumer);
    }

    /* compiled from: WindowAreaControllerImpl.kt */
    /* loaded from: classes.dex */
    public static final class RearDisplaySessionConsumer implements Consumer<Integer> {
        @NotNull
        private final WindowAreaSessionCallback appCallback;
        @NotNull
        private final WindowAreaComponent extensionsComponent;
        @Nullable
        private WindowAreaSession session;

        public RearDisplaySessionConsumer(@NotNull WindowAreaSessionCallback appCallback, @NotNull WindowAreaComponent extensionsComponent) {
            Intrinsics.checkNotNullParameter(appCallback, "appCallback");
            Intrinsics.checkNotNullParameter(extensionsComponent, "extensionsComponent");
            this.appCallback = appCallback;
            this.extensionsComponent = extensionsComponent;
        }

        @Override // java.util.function.Consumer
        public /* bridge */ /* synthetic */ void accept(Integer num) {
            accept(num.intValue());
        }

        public void accept(int i) {
            if (i == 0) {
                onSessionFinished();
            } else if (i == 1) {
                onSessionStarted();
            } else {
                if (BuildConfig.INSTANCE.getVerificationMode() == SpecificationComputer.VerificationMode.STRICT) {
                    String str = WindowAreaControllerImpl.TAG;
                    Log.d(str, "Received an unknown session status value: " + i);
                }
                onSessionFinished();
            }
        }

        private final void onSessionStarted() {
            RearDisplaySessionImpl rearDisplaySessionImpl = new RearDisplaySessionImpl(this.extensionsComponent);
            this.session = rearDisplaySessionImpl;
            this.appCallback.onSessionStarted(rearDisplaySessionImpl);
        }

        private final void onSessionFinished() {
            this.session = null;
            this.appCallback.onSessionEnded();
        }
    }

    /* compiled from: WindowAreaControllerImpl.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
