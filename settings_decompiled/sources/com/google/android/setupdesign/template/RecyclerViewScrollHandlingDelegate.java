package com.google.android.setupdesign.template;

import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.template.RequireScrollMixin;
/* loaded from: classes2.dex */
public class RecyclerViewScrollHandlingDelegate implements RequireScrollMixin.ScrollHandlingDelegate {
    private final RecyclerView recyclerView;
    private final RequireScrollMixin requireScrollMixin;

    public RecyclerViewScrollHandlingDelegate(RequireScrollMixin requireScrollMixin, RecyclerView recyclerView) {
        this.requireScrollMixin = requireScrollMixin;
        this.recyclerView = recyclerView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canScrollDown() {
        RecyclerView recyclerView = this.recyclerView;
        if (recyclerView != null) {
            int computeVerticalScrollOffset = recyclerView.computeVerticalScrollOffset();
            int computeVerticalScrollRange = this.recyclerView.computeVerticalScrollRange() - this.recyclerView.computeVerticalScrollExtent();
            return computeVerticalScrollRange != 0 && computeVerticalScrollOffset < computeVerticalScrollRange - 1;
        }
        return false;
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void startListening() {
        RecyclerView recyclerView = this.recyclerView;
        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.google.android.setupdesign.template.RecyclerViewScrollHandlingDelegate.1
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView2, int i, int i2) {
                    RecyclerViewScrollHandlingDelegate.this.requireScrollMixin.notifyScrollabilityChange(RecyclerViewScrollHandlingDelegate.this.canScrollDown());
                }
            });
            if (canScrollDown()) {
                this.requireScrollMixin.notifyScrollabilityChange(true);
                return;
            }
            return;
        }
        Log.w("RVRequireScrollMixin", "Cannot require scroll. Recycler view is null.");
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void pageScrollDown() {
        RecyclerView recyclerView = this.recyclerView;
        if (recyclerView != null) {
            this.recyclerView.smoothScrollBy(0, recyclerView.getHeight());
        }
    }
}
