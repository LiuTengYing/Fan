package com.android.settings.dream;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes.dex */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final boolean mRtl;
    private final int mSpacing;

    public GridSpacingItemDecoration(Context context, int i) {
        this.mSpacing = context.getResources().getDimensionPixelSize(i);
        this.mRtl = context.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            int i = childAdapterPosition % spanCount;
            int i2 = this.mSpacing;
            int i3 = (i * i2) / spanCount;
            int i4 = i2 - (((i + 1) * i2) / spanCount);
            boolean z = this.mRtl;
            rect.left = z ? i4 : i3;
            if (!z) {
                i3 = i4;
            }
            rect.right = i3;
            if (childAdapterPosition >= spanCount) {
                rect.top = i2;
            }
        }
    }
}
