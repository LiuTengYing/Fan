package androidx.transition;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
/* loaded from: classes.dex */
class CanvasUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"SoonBlockedPrivateApi"})
    public static void enableZ(Canvas canvas, boolean z) {
        if (z) {
            canvas.enableZ();
        } else {
            canvas.disableZ();
        }
    }
}
