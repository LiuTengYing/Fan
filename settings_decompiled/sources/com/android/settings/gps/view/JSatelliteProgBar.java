package com.android.settings.gps.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R$drawable;
import com.android.settings.R$styleable;
import com.android.settings.SettingsApplication;
/* loaded from: classes.dex */
public class JSatelliteProgBar extends View {
    public static int PROGRESS_HIGHT_MAX = 141;
    protected Rect RECT_EMPTY;
    private int height;
    protected Drawable mDrawableGray;
    protected Drawable mDrawableGreen;
    protected Drawable mDrawableNormal;
    private int mHeight;
    protected int mId;
    protected int mIdFocus;
    protected int mIdSuccess;
    public int mIsUser;
    protected Paint mPaint;
    public int mProgressTarget;
    protected Rect mRectDrawable;
    protected Rect mRectStrong;
    private int mStepIncrement;
    private int mWidth;
    private int width;

    private float getFraction(int i) {
        return (float) ((i * 1.0d) / 60.0d);
    }

    public JSatelliteProgBar(Context context) {
        super(context);
        this.mId = R$drawable.satellite_preogress_null;
        this.mIdFocus = R$drawable.satellite_preogress_gray;
        this.mIdSuccess = R$drawable.satellite_preogress_green;
        this.mPaint = new Paint();
        this.mProgressTarget = 0;
        this.mIsUser = 0;
        this.mStepIncrement = 3;
        this.RECT_EMPTY = new Rect(0, 0, 0, 0);
        this.mRectDrawable = new Rect(0, 0, 24, 141);
        this.mRectStrong = new Rect(0, 0, 24, 141);
        initConfig(context, null);
        init();
    }

    public JSatelliteProgBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mId = R$drawable.satellite_preogress_null;
        this.mIdFocus = R$drawable.satellite_preogress_gray;
        this.mIdSuccess = R$drawable.satellite_preogress_green;
        this.mPaint = new Paint();
        this.mProgressTarget = 0;
        this.mIsUser = 0;
        this.mStepIncrement = 3;
        this.RECT_EMPTY = new Rect(0, 0, 0, 0);
        this.mRectDrawable = new Rect(0, 0, 24, 141);
        this.mRectStrong = new Rect(0, 0, 24, 141);
        initConfig(context, attributeSet);
        init();
    }

    public JSatelliteProgBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mId = R$drawable.satellite_preogress_null;
        this.mIdFocus = R$drawable.satellite_preogress_gray;
        this.mIdSuccess = R$drawable.satellite_preogress_green;
        this.mPaint = new Paint();
        this.mProgressTarget = 0;
        this.mIsUser = 0;
        this.mStepIncrement = 3;
        this.RECT_EMPTY = new Rect(0, 0, 0, 0);
        this.mRectDrawable = new Rect(0, 0, 24, 141);
        this.mRectStrong = new Rect(0, 0, 24, 141);
        initConfig(context, attributeSet);
        init();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();
        setMeasuredDimension(this.mWidth, this.mHeight);
    }

    private void initConfig(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.JSatelliteProgBar);
        this.mHeight = obtainStyledAttributes.getInteger(R$styleable.JSatelliteProgBar_barHeight, 25);
        this.mWidth = obtainStyledAttributes.getInteger(R$styleable.JSatelliteProgBar_barWidth, 200);
        obtainStyledAttributes.recycle();
    }

    private void init() {
        this.mPaint.setAntiAlias(true);
        this.mPaint.setFilterBitmap(true);
        this.mPaint.setTextAlign(Paint.Align.CENTER);
        int i = this.mHeight;
        int i2 = this.mWidth;
        PROGRESS_HIGHT_MAX = i;
        Rect rect = this.mRectDrawable;
        rect.top = 0;
        rect.left = 0;
        rect.right = 0 + i2;
        rect.bottom = i;
        Rect rect2 = this.mRectStrong;
        rect2.top = 0;
        rect2.left = 0;
        rect2.right = i2 + 0;
        rect2.bottom = i;
        this.mDrawableNormal = SettingsApplication.mApplication.getDrawable(this.mId);
        this.mDrawableGray = SettingsApplication.mApplication.getDrawable(this.mIdFocus);
        this.mDrawableGreen = SettingsApplication.mApplication.getDrawable(this.mIdSuccess);
    }

    public void setProgressNew(int i) {
        if (this.mProgressTarget != i) {
            this.mProgressTarget = i;
            invalidate();
        }
    }

    public void setBkWidthAndHeight(int i, int i2) {
        this.mWidth = i;
        this.mHeight = i2;
        init();
        requestLayout();
    }

    public void setIsUse(int i) {
        if (this.mIsUser != i) {
            this.mIsUser = i;
            invalidate();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawableProgress(canvas);
    }

    private void drawBackground(Canvas canvas) {
        this.mDrawableNormal.setBounds(this.mRectDrawable);
        this.mDrawableNormal.draw(canvas);
    }

    private void drawableProgress(Canvas canvas) {
        if (this.mIsUser != 0) {
            Drawable drawable = this.mDrawableGreen;
            if (drawable != null) {
                Rect rect = this.mRectDrawable;
                int i = rect.left;
                int round = rect.top + Math.round(this.mHeight - (getFraction(this.mProgressTarget) * this.mHeight));
                Rect rect2 = this.mRectDrawable;
                drawable.setBounds(i, round, rect2.right, rect2.bottom);
                this.mDrawableGreen.draw(canvas);
                return;
            }
            return;
        }
        Drawable drawable2 = this.mDrawableGray;
        if (drawable2 != null) {
            Rect rect3 = this.mRectDrawable;
            int i2 = rect3.left;
            int round2 = rect3.top + Math.round(this.mHeight - (getFraction(this.mProgressTarget) * this.mHeight));
            Rect rect4 = this.mRectDrawable;
            drawable2.setBounds(i2, round2, rect4.right, rect4.bottom);
            this.mDrawableGray.draw(canvas);
        }
    }
}
