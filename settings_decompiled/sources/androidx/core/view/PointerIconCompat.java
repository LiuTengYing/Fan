package androidx.core.view;

import android.content.Context;
import android.view.PointerIcon;
/* loaded from: classes.dex */
public final class PointerIconCompat {
    private final PointerIcon mPointerIcon;

    private PointerIconCompat(PointerIcon pointerIcon) {
        this.mPointerIcon = pointerIcon;
    }

    public Object getPointerIcon() {
        return this.mPointerIcon;
    }

    public static PointerIconCompat getSystemIcon(Context context, int i) {
        return new PointerIconCompat(Api24Impl.getSystemIcon(context, i));
    }

    /* loaded from: classes.dex */
    static class Api24Impl {
        static PointerIcon getSystemIcon(Context context, int i) {
            return PointerIcon.getSystemIcon(context, i);
        }
    }
}
