package androidx.slice.widget;

import android.content.Context;
import android.net.Uri;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SliceMetricsWrapper extends SliceMetrics {
    private final android.app.slice.SliceMetrics mSliceMetrics;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SliceMetricsWrapper(Context context, Uri uri) {
        this.mSliceMetrics = new android.app.slice.SliceMetrics(context, uri);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.slice.widget.SliceMetrics
    public void logVisible() {
        this.mSliceMetrics.logVisible();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.slice.widget.SliceMetrics
    public void logHidden() {
        this.mSliceMetrics.logHidden();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.slice.widget.SliceMetrics
    public void logTouch(int i, Uri uri) {
        this.mSliceMetrics.logTouch(i, uri);
    }
}
