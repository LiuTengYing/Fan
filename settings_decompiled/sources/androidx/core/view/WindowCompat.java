package androidx.core.view;

import android.view.Window;
/* loaded from: classes.dex */
public final class WindowCompat {
    public static void setDecorFitsSystemWindows(Window window, boolean z) {
        Api30Impl.setDecorFitsSystemWindows(window, z);
    }

    /* loaded from: classes.dex */
    static class Api30Impl {
        static void setDecorFitsSystemWindows(Window window, boolean z) {
            window.setDecorFitsSystemWindows(z);
        }
    }
}
