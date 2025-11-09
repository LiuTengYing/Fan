package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
/* loaded from: classes.dex */
public class AdjustListView extends ListView {
    public AdjustListView(Context context) {
        super(context);
    }

    public AdjustListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AdjustListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
    }
}
