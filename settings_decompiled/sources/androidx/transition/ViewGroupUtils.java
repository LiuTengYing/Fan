package androidx.transition;

import android.view.ViewGroup;
/* loaded from: classes.dex */
class ViewGroupUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static ViewGroupOverlayImpl getOverlay(ViewGroup viewGroup) {
        return new ViewGroupOverlayApi18(viewGroup);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void suppressLayout(ViewGroup viewGroup, boolean z) {
        viewGroup.suppressLayout(z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getChildDrawingOrder(ViewGroup viewGroup, int i) {
        return viewGroup.getChildDrawingOrder(i);
    }
}
