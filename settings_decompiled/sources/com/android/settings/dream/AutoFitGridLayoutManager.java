package com.android.settings.dream;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$dimen;
/* loaded from: classes.dex */
public final class AutoFitGridLayoutManager extends GridLayoutManager {
    private final float mColumnWidth;

    public AutoFitGridLayoutManager(Context context) {
        super(context, 1);
        this.mColumnWidth = context.getResources().getDimensionPixelSize(R$dimen.dream_item_min_column_width);
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        setSpanCount(Math.max(1, (int) (((getWidth() - getPaddingRight()) - getPaddingLeft()) / this.mColumnWidth)));
        super.onLayoutChildren(recycler, state);
    }
}
