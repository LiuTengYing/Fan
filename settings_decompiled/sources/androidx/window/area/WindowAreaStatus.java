package androidx.window.area;

import androidx.window.core.ExperimentalWindowApi;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
/* compiled from: WindowAreaStatus.kt */
@ExperimentalWindowApi
/* loaded from: classes.dex */
public final class WindowAreaStatus {
    @NotNull
    private final String mDescription;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    public static final WindowAreaStatus UNSUPPORTED = new WindowAreaStatus("UNSUPPORTED");
    @NotNull
    public static final WindowAreaStatus UNAVAILABLE = new WindowAreaStatus("UNAVAILABLE");
    @NotNull
    public static final WindowAreaStatus AVAILABLE = new WindowAreaStatus("AVAILABLE");

    private WindowAreaStatus(String str) {
        this.mDescription = str;
    }

    @NotNull
    public String toString() {
        return this.mDescription;
    }

    /* compiled from: WindowAreaStatus.kt */
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final WindowAreaStatus translate$window_release(int i) {
            if (i != 1) {
                if (i == 2) {
                    return WindowAreaStatus.AVAILABLE;
                }
                return WindowAreaStatus.UNSUPPORTED;
            }
            return WindowAreaStatus.UNAVAILABLE;
        }
    }
}
