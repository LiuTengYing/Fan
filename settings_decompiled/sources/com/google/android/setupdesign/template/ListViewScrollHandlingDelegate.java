package com.google.android.setupdesign.template;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import com.google.android.setupdesign.template.RequireScrollMixin;
/* loaded from: classes2.dex */
public class ListViewScrollHandlingDelegate implements RequireScrollMixin.ScrollHandlingDelegate, AbsListView.OnScrollListener {
    private final ListView listView;
    private final RequireScrollMixin requireScrollMixin;

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    public ListViewScrollHandlingDelegate(RequireScrollMixin requireScrollMixin, ListView listView) {
        this.requireScrollMixin = requireScrollMixin;
        this.listView = listView;
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void startListening() {
        ListView listView = this.listView;
        if (listView != null) {
            listView.setOnScrollListener(this);
            if (this.listView.getLastVisiblePosition() < this.listView.getAdapter().getCount()) {
                this.requireScrollMixin.notifyScrollabilityChange(true);
                return;
            }
            return;
        }
        Log.w("ListViewDelegate", "Cannot require scroll. List view is null");
    }

    @Override // com.google.android.setupdesign.template.RequireScrollMixin.ScrollHandlingDelegate
    public void pageScrollDown() {
        ListView listView = this.listView;
        if (listView != null) {
            this.listView.smoothScrollBy(listView.getHeight(), 500);
        }
    }

    @Override // android.widget.AbsListView.OnScrollListener
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (i + i2 >= i3) {
            this.requireScrollMixin.notifyScrollabilityChange(false);
        } else {
            this.requireScrollMixin.notifyScrollabilityChange(true);
        }
    }
}
