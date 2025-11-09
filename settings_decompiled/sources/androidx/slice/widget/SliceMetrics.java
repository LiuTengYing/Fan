package androidx.slice.widget;

import android.content.Context;
import android.net.Uri;
/* loaded from: classes.dex */
class SliceMetrics {
    /* JADX INFO: Access modifiers changed from: protected */
    public void logHidden() {
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logTouch(int i, Uri uri) {
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logVisible() {
        throw null;
    }

    public static SliceMetrics getInstance(Context context, Uri uri) {
        return new SliceMetricsWrapper(context, uri);
    }
}
