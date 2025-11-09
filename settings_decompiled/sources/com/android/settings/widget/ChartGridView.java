package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.R;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class ChartGridView extends View {
    private Drawable mBorder;
    private int mLabelColor;
    private Layout mLabelEnd;
    private Layout mLabelMid;
    private int mLabelSize;
    private Layout mLabelStart;
    private Drawable mPrimary;
    private Drawable mSecondary;

    public ChartGridView(Context context) {
        this(context, null, 0);
    }

    public ChartGridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChartGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setWillNotDraw(false);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ChartGridView, i, 0);
        this.mPrimary = obtainStyledAttributes.getDrawable(R$styleable.ChartGridView_primaryDrawable);
        this.mSecondary = obtainStyledAttributes.getDrawable(R$styleable.ChartGridView_secondaryDrawable);
        this.mBorder = obtainStyledAttributes.getDrawable(R$styleable.ChartGridView_borderDrawable);
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(obtainStyledAttributes.getResourceId(R$styleable.ChartGridView_android_textAppearance, -1), R.styleable.TextAppearance);
        this.mLabelSize = obtainStyledAttributes2.getDimensionPixelSize(0, 0);
        obtainStyledAttributes2.recycle();
        this.mLabelColor = obtainStyledAttributes.getColorStateList(R$styleable.ChartGridView_android_textColor).getDefaultColor();
        obtainStyledAttributes.recycle();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight() - getPaddingBottom();
        Drawable drawable = this.mSecondary;
        if (drawable != null) {
            drawable.getIntrinsicHeight();
            throw null;
        }
        Drawable drawable2 = this.mPrimary;
        if (drawable2 != null) {
            drawable2.getIntrinsicWidth();
            drawable2.getIntrinsicHeight();
            throw null;
        }
        this.mBorder.setBounds(0, 0, width, height);
        this.mBorder.draw(canvas);
        Layout layout = this.mLabelStart;
        int height2 = layout != null ? layout.getHeight() / 8 : 0;
        Layout layout2 = this.mLabelStart;
        if (layout2 != null) {
            int save = canvas.save();
            canvas.translate(0.0f, height + height2);
            layout2.draw(canvas);
            canvas.restoreToCount(save);
        }
        Layout layout3 = this.mLabelMid;
        if (layout3 != null) {
            int save2 = canvas.save();
            canvas.translate((width - layout3.getWidth()) / 2, height + height2);
            layout3.draw(canvas);
            canvas.restoreToCount(save2);
        }
        Layout layout4 = this.mLabelEnd;
        if (layout4 != null) {
            int save3 = canvas.save();
            canvas.translate(width - layout4.getWidth(), height + height2);
            layout4.draw(canvas);
            canvas.restoreToCount(save3);
        }
    }
}
