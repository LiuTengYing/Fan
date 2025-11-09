package androidx.core.view;

import android.view.ViewGroup;
/* loaded from: classes.dex */
public final class ViewGroupCompat {
    public static boolean isTransitionGroup(ViewGroup viewGroup) {
        return Api21Impl.isTransitionGroup(viewGroup);
    }

    /* loaded from: classes.dex */
    static class Api21Impl {
        static boolean isTransitionGroup(ViewGroup viewGroup) {
            return viewGroup.isTransitionGroup();
        }
    }
}
