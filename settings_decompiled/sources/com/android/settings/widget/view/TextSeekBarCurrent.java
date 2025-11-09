package com.android.settings.widget.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class TextSeekBarCurrent extends View {
    private boolean isTouch;
    private float mBackgroundBottom;
    private Drawable mBackgroundDrawable;
    private float mBackgroundHeight;
    private float mBackgroundLeft;
    private float mBackgroundRight;
    private float mBackgroundTop;
    private float mBackgroundWidth;
    private int mHeight;
    private boolean mIsDragging;
    private int mMaxProgress;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private int mProgress;
    private ClipDrawable mProgressDrawable;
    private ClipDrawable mSecondaryDrawable;
    private int mSecondaryProgress;
    private String mText;
    private Drawable mTextBgDrawable;
    private int mTextBgHeight;
    private int mTextBgWidth;
    private int mTextColor;
    private int mTextLength;
    private int mTextPaddingTop;
    private Paint mTextPaint;
    private float mTextSize;
    private Drawable mThumb;
    private int mThumbBottom;
    private int mThumbHeight;
    private int mThumbLeft;
    private Paint mThumbPaint;
    private int mThumbRight;
    private int mThumbTop;
    private int mThumbWith;
    private int mWidth;
    private int max;
    private int min;
    private float padding;
    private int paddingTop;
    private ValueAnimator progressAnimator;

    /* loaded from: classes.dex */
    public interface OnSeekBarChangeListener {
        void onProgressChanged(TextSeekBarCurrent textSeekBarCurrent, int i, boolean z);

        void onStartTrackingTouch(TextSeekBarCurrent textSeekBarCurrent);

        void onStopTrackingTouch(TextSeekBarCurrent textSeekBarCurrent);
    }

    public TextSeekBarCurrent(Context context) {
        super(context);
        this.mProgress = 0;
        this.mSecondaryProgress = 0;
        this.mThumbWith = 0;
        this.mThumbHeight = 0;
        this.mTextBgWidth = 0;
        this.mTextBgHeight = 0;
        this.mTextSize = 12.0f;
        this.mTextLength = 0;
        this.mText = "";
        this.max = 100;
        this.min = 0;
        this.isTouch = false;
        this.padding = 0.0f;
        this.paddingTop = 8;
        init();
    }

    public TextSeekBarCurrent(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mProgress = 0;
        this.mSecondaryProgress = 0;
        this.mThumbWith = 0;
        this.mThumbHeight = 0;
        this.mTextBgWidth = 0;
        this.mTextBgHeight = 0;
        this.mTextSize = 12.0f;
        this.mTextLength = 0;
        this.mText = "";
        this.max = 100;
        this.min = 0;
        this.isTouch = false;
        this.padding = 0.0f;
        this.paddingTop = 8;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.TextSeekBar);
            int i = R$styleable.TextSeekBar_thumb;
            if (obtainStyledAttributes.hasValue(i)) {
                Drawable drawable = obtainStyledAttributes.getDrawable(i);
                this.mThumb = drawable;
                if (drawable != null) {
                    this.mThumbWith = drawable.getIntrinsicWidth();
                    this.mThumbHeight = this.mThumb.getIntrinsicHeight();
                }
            }
            this.mThumbWith = (int) obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_thumbWidth, this.mThumbWith);
            this.mThumbHeight = (int) obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_thumbHeight, this.mThumbHeight);
            this.mBackgroundHeight = (int) obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_progressDrawableHeight, this.mBackgroundHeight);
            int i2 = R$styleable.TextSeekBar_progressDrawable;
            if (obtainStyledAttributes.hasValue(i2)) {
                Drawable drawable2 = obtainStyledAttributes.getDrawable(i2);
                if (drawable2 instanceof LayerDrawable) {
                    LayerDrawable layerDrawable = (LayerDrawable) drawable2;
                    this.mBackgroundDrawable = layerDrawable.findDrawableByLayerId(16908288);
                    Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(16908303);
                    if (findDrawableByLayerId instanceof ClipDrawable) {
                        this.mSecondaryDrawable = (ClipDrawable) findDrawableByLayerId;
                    }
                    Drawable findDrawableByLayerId2 = layerDrawable.findDrawableByLayerId(16908301);
                    if (findDrawableByLayerId2 instanceof ClipDrawable) {
                        this.mProgressDrawable = (ClipDrawable) findDrawableByLayerId2;
                        Log.d("TextSeekBarCenter", "TextSeekBar: mProgressDrawable instanceof Clip");
                    }
                }
            }
            this.mTextSize = obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_textSize, 12.0f);
            this.mTextPaddingTop = (int) obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_textPaddingTop, 0.0f);
            this.mTextColor = obtainStyledAttributes.getColor(R$styleable.TextSeekBar_textColor, -16777216);
            this.max = obtainStyledAttributes.getInteger(R$styleable.TextSeekBar_textMax, 0);
            this.min = obtainStyledAttributes.getInteger(R$styleable.TextSeekBar_textMin, 0);
            int i3 = R$styleable.TextSeekBar_textBackground;
            if (obtainStyledAttributes.hasValue(i3)) {
                Drawable drawable3 = obtainStyledAttributes.getDrawable(i3);
                this.mTextBgDrawable = drawable3;
                if (drawable3 != null) {
                    this.mTextBgHeight = drawable3.getIntrinsicHeight();
                    this.mTextBgWidth = this.mTextBgDrawable.getIntrinsicWidth();
                }
            }
            this.mTextBgWidth = (int) obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_textBgWidth, this.mTextBgWidth);
            this.mTextBgHeight = (int) obtainStyledAttributes.getDimension(R$styleable.TextSeekBar_textBgHeight, this.mTextBgHeight);
            obtainStyledAttributes.recycle();
            StringBuilder sb = new StringBuilder();
            sb.append("TextSeekBar: mTextBgDrawable ");
            sb.append(this.mTextBgDrawable == null);
            Log.d("TextSeekBarCenter", sb.toString());
            Log.d("TextSeekBarCenter", "TextSeekBar: " + this.mThumbWith + " " + this.mThumbHeight + " textPaddingTop " + this.mTextPaddingTop + " textSize" + this.mTextSize);
        }
        init();
    }

    private void init() {
        Paint paint = new Paint();
        this.mTextPaint = paint;
        paint.setAntiAlias(true);
        this.mTextPaint.setColor(this.mTextColor);
        this.mTextPaint.setTextSize(this.mTextSize);
        Paint paint2 = new Paint();
        this.mThumbPaint = paint2;
        paint2.setAntiAlias(true);
        this.mThumbPaint.setColor(-16777216);
        this.mMaxProgress = this.max - this.min;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i2);
        int size2 = View.MeasureSpec.getSize(i);
        Log.d("TextSeekBarCenter", "onMeasure:  " + size2 + " h " + size);
        int i3 = this.mTextBgHeight;
        int max = Math.max(size + i3, this.mThumbHeight + i3 + (((int) this.padding) * 2));
        int max2 = Math.max(this.mThumbWith + size2, size2 + this.mTextBgWidth);
        Log.d("TextSeekBarCenter", "onMeasure: width " + max2 + " height " + max + " backHeight " + this.mBackgroundHeight);
        setMeasuredDimension(max2, max);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mWidth = i;
        this.mHeight = i2;
        this.mBackgroundWidth = Math.min(i - this.mThumbWith, i - this.mTextBgWidth);
        float max = Math.max((this.mThumbWith / 2) + this.paddingTop, this.mTextBgWidth / 2);
        this.mBackgroundLeft = max;
        this.mBackgroundRight = ((max + this.mBackgroundWidth) - this.padding) - this.paddingTop;
        float f = this.mBackgroundHeight;
        float f2 = ((this.mThumbHeight - f) / 2.0f) + this.mTextBgHeight;
        this.mBackgroundTop = f2;
        this.mBackgroundBottom = f2 + f;
        int max2 = Math.max((this.mTextBgWidth / 2) - (this.mThumbWith / 2), 0);
        this.mThumbLeft = max2;
        int i5 = this.mTextBgHeight;
        this.mThumbTop = i5;
        this.mThumbRight = max2 + this.mThumbWith;
        this.mThumbBottom = i5 + this.mThumbHeight;
        ClipDrawable clipDrawable = this.mSecondaryDrawable;
        if (clipDrawable != null) {
            clipDrawable.setBounds((int) this.mBackgroundLeft, (int) this.mBackgroundTop, (int) this.mBackgroundRight, (int) this.mBackgroundBottom);
        }
        Log.d("TextSeekBarCenter", "onSizeChanged: " + this.mWidth + " height " + this.mHeight + " mBackHeight " + this.mBackgroundHeight + " mTop " + this.mBackgroundTop + " thumbWidth " + this.mThumbWith + " " + this.mBackgroundLeft + " " + this.mBackgroundBottom);
        this.paddingTop = ((int) (((float) i2) - this.mBackgroundBottom)) / 2;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawSecondary(canvas);
        drawProgress(canvas);
        drawThumb(canvas, this.isTouch);
        invalidate();
    }

    private void drawBackground(Canvas canvas) {
        Drawable drawable;
        if ((this.mSecondaryProgress != this.mMaxProgress || this.mSecondaryDrawable == null) && (drawable = this.mBackgroundDrawable) != null) {
            float f = this.padding;
            int i = ((int) this.mBackgroundTop) - ((int) f);
            int i2 = this.paddingTop;
            drawable.setBounds((int) this.mBackgroundLeft, i + i2, (int) this.mBackgroundRight, ((int) this.mBackgroundBottom) + ((int) f) + i2);
            this.mBackgroundDrawable.draw(canvas);
        }
    }

    private void drawProgress(Canvas canvas) {
        ClipDrawable clipDrawable = this.mProgressDrawable;
        if (clipDrawable != null) {
            float f = this.padding;
            int i = ((int) this.mBackgroundTop) - ((int) f);
            int i2 = this.paddingTop;
            clipDrawable.setBounds((int) this.mBackgroundLeft, i + i2, (int) this.mBackgroundRight, ((int) this.mBackgroundBottom) + ((int) f) + i2);
            this.mProgressDrawable.setLevel((int) (getFraction(this.mProgress) * 10000.0f));
            this.mProgressDrawable.draw(canvas);
        }
    }

    private void drawSecondary(Canvas canvas) {
        ClipDrawable clipDrawable = this.mSecondaryDrawable;
        if (clipDrawable != null) {
            clipDrawable.setLevel((int) (getFraction(this.mSecondaryProgress) * 10000.0f));
            this.mSecondaryDrawable.draw(canvas);
        }
    }

    private void drawThumb(Canvas canvas, boolean z) {
        Drawable drawable;
        if (z || (drawable = this.mThumb) == null) {
            return;
        }
        drawable.setBounds(((int) (getFraction(this.mProgress) * this.mBackgroundWidth)) + this.mThumbLeft, this.mThumbTop + this.paddingTop, (int) ((getFraction(this.mProgress) * this.mBackgroundWidth) + this.mThumbRight), this.mThumbBottom + this.paddingTop);
        this.mThumb.draw(canvas);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    private float getFraction(int i) {
        return (float) ((i * 1.0d) / this.mMaxProgress);
    }

    public void setProgress(int i) {
        if (this.mProgress == i) {
            return;
        }
        this.mProgress = i;
        invalidate();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setSecondaryProgress(int i) {
        if (this.mSecondaryProgress == i) {
            return;
        }
        this.mSecondaryProgress = i;
        invalidate();
    }

    public void setText(String str) {
        this.mText = str;
    }

    public void setTextColor(int i) {
        this.mTextColor = i;
    }

    public void setTextSize(float f) {
        this.mTextSize = f;
    }

    public void setMax(int i) {
        this.max = i;
        this.mMaxProgress = i - this.min;
    }

    public void setMin(int i) {
        this.min = i;
        this.mMaxProgress = this.max - i;
    }

    public void setThumbImage(Drawable drawable) {
        this.mThumb = drawable;
    }

    public void setThumbWith(int i) {
        this.mThumbWith = i;
    }

    public void setThumbHeight(int i) {
        this.mThumbHeight = i;
    }

    public void setTextBgDrawable(Drawable drawable) {
        this.mTextBgDrawable = drawable;
    }

    public void setTextBgWidth(int i) {
        this.mTextBgWidth = i;
    }

    public void setTextBgHeight(int i) {
        this.mTextBgHeight = i;
    }

    public void setTextPaddingTop(int i) {
        this.mTextPaddingTop = i;
    }

    public int getMax() {
        return this.mMaxProgress;
    }

    private void onProgressRefresh() {
        OnSeekBarChangeListener onSeekBarChangeListener = this.mOnSeekBarChangeListener;
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(this, this.mProgress, this.mIsDragging);
        }
    }

    private boolean checkProgressBound(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float f = this.mBackgroundLeft;
        return x >= (f - ((float) this.mThumbLeft)) - 10.0f && x <= (((f + this.mBackgroundWidth) + ((float) this.mThumbWith)) + this.padding) + 10.0f;
    }

    private int getProgress(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        motionEvent.getY();
        return (int) (Math.min(Math.max(0.0f, (x - this.mBackgroundLeft) / this.mBackgroundWidth), 1.0f) * getMax());
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0012, code lost:
        if (r0 != 3) goto L8;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getAction()
            r1 = 500(0x1f4, float:7.0E-43)
            r2 = 0
            r3 = 1092616192(0x41200000, float:10.0)
            r4 = 1
            if (r0 == 0) goto L41
            if (r0 == r4) goto L2a
            r5 = 2
            if (r0 == r5) goto L15
            r7 = 3
            if (r0 == r7) goto L2a
            goto L80
        L15:
            boolean r0 = r6.mIsDragging
            if (r0 == 0) goto L80
            boolean r0 = r6.checkProgressBound(r7)
            if (r0 == 0) goto L80
            int r7 = r6.getProgress(r7)
            r6.setProgress(r7)
            r6.onProgressRefresh()
            return r4
        L2a:
            com.android.settings.widget.view.TextSeekBarCurrent$OnSeekBarChangeListener r7 = r6.mOnSeekBarChangeListener
            if (r7 == 0) goto L35
            boolean r0 = r6.mIsDragging
            if (r0 == 0) goto L35
            r7.onStopTrackingTouch(r6)
        L35:
            r7 = 0
            r6.isTouch = r7
            r6.mIsDragging = r7
            r6.setAnimation(r3, r2, r1)
            r6.invalidate()
            goto L80
        L41:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "onTouchEvent: "
            r0.append(r5)
            boolean r5 = r6.checkProgressBound(r7)
            r0.append(r5)
            float r5 = r7.getX()
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r5 = "TextSeekBarCenter"
            android.util.Log.d(r5, r0)
            android.view.ViewParent r0 = r6.getParent()
            r0.requestDisallowInterceptTouchEvent(r4)
            boolean r7 = r6.checkProgressBound(r7)
            if (r7 == 0) goto L80
            r6.mIsDragging = r4
            com.android.settings.widget.view.TextSeekBarCurrent$OnSeekBarChangeListener r7 = r6.mOnSeekBarChangeListener
            if (r7 == 0) goto L78
            r7.onStartTrackingTouch(r6)
        L78:
            r6.isTouch = r4
            r6.setAnimation(r2, r3, r1)
            r6.invalidate()
        L80:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.widget.view.TextSeekBarCurrent.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void setAnimation(float f, float f2, int i) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
        this.progressAnimator = ofFloat;
        ofFloat.setDuration(i);
        this.progressAnimator.setTarget(10);
        this.progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.view.TextSeekBarCurrent.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSeekBarCurrent.this.padding = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            }
        });
        this.progressAnimator.start();
    }
}
