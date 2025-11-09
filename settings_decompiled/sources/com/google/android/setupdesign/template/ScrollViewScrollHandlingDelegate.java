package com.google.android.setupdesign.template;

import android.util.Log;
import android.widget.ScrollView;
import com.google.android.setupdesign.template.RequireScrollMixin;
import com.google.android.setupdesign.view.BottomScrollView;
/* loaded from: classes2.dex */
public class ScrollViewScrollHandlingDelegate implements RequireScrollMixin.ScrollHandlingDelegate, BottomScrollView.BottomScrollListener {
    private final RequireScrollMixin requireScrollMixin;
    private final BottomScrollView scrollView;

    public ScrollViewScrollHandlingDelegate(RequireScrollMixin requireScrollMixin, ScrollView scrollView) {
        this.requireScrollMixin = requireScrollMixin;
        if (scrollView instanceof BottomScrollView) {
            this.scrollView = (BottomScrollView) scrollView;
            return;
        }
        Log.w("ScrollViewDelegate", "Cannot set non-BottomScrollView. Found=" + scrollView);
        this.scrollView = null;
    }

    @Override // com.google.android.setupdesign.view.BottomScrollView.BottomScrollListener
    public void onScrolledToBottom() {
        this.requireScrollMixin.notifyScrollabilityChange(false);
    }

    @Override // com.google.android.setupdesign.view.BottomScrollView.BottomScrollListener
    public void onRequiresScroll() {
        this.requireScrollMixin.notifyScrollabilityChange(true);
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void startListening() {
        BottomScrollView bottomScrollView = this.scrollView;
        if (bottomScrollView != null) {
            bottomScrollView.setBottomScrollListener(this);
        } else {
            Log.w("ScrollViewDelegate", "Cannot require scroll. Scroll view is null.");
        }
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void pageScrollDown() {
        BottomScrollView bottomScrollView = this.scrollView;
        if (bottomScrollView != null) {
            bottomScrollView.pageScroll(130);
        }
    }
}
