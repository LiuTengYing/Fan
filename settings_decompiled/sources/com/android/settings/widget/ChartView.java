package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.widget.FrameLayout;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class ChartView extends FrameLayout {
    private Rect mContent;
    @ViewDebug.ExportedProperty
    private int mOptimalWidth;
    private float mOptimalWidthWeight;

    public ChartView(Context context) {
        this(context, null, 0);
    }

    public ChartView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChartView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOptimalWidth = -1;
        this.mOptimalWidthWeight = 0.0f;
        this.mContent = new Rect();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ChartView, i, 0);
        setOptimalWidth(obtainStyledAttributes.getDimensionPixelSize(R$styleable.ChartView_optimalWidth, -1), obtainStyledAttributes.getFloat(R$styleable.ChartView_optimalWidthWeight, 0.0f));
        obtainStyledAttributes.recycle();
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setOptimalWidth(int i, float f) {
        this.mOptimalWidth = i;
        this.mOptimalWidthWeight = f;
        requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int measuredWidth = getMeasuredWidth();
        int i3 = this.mOptimalWidth;
        int i4 = measuredWidth - i3;
        if (i3 <= 0 || i4 <= 0) {
            return;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (i3 + (i4 * this.mOptimalWidthWeight)), 1073741824), i2);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mContent.set(getPaddingLeft(), getPaddingTop(), (i3 - i) - getPaddingRight(), (i4 - i2) - getPaddingBottom());
        this.mContent.width();
        this.mContent.height();
        throw null;
    }
}
