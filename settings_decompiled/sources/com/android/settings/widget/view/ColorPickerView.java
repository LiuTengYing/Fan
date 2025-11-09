package com.android.settings.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class ColorPickerView extends View {
    private static String TAG = "ColorPickerView";
    private float centerRadius;
    private boolean downInCircle;
    private boolean downInRect;
    private boolean highlightCenter;
    private boolean highlightCenterLittle;
    private Drawable mBackground;
    private Paint mCenterPaint;
    private int[] mCircleColors;
    private Context mContext;
    private int mHeight;
    private int mInitialColor;
    private Paint mLinePaint;
    private OnColorChangedListener mListener;
    private Paint mPaint;
    private Paint mPaintText;
    private int[] mRectColors;
    private Paint mRectPaint;
    private int mWidth;
    private float r;
    private float rectBottom;
    private float rectLeft;
    private float rectRight;
    private Shader rectShader;
    private float rectTop;
    private String title;

    /* loaded from: classes.dex */
    public interface OnColorChangedListener {
        void colorChanged(int i, boolean z);
    }

    private boolean inCenter(float f, float f2, float f3) {
        double d = f3;
        return ((double) ((f * f) + (f2 * f2))) * 3.141592653589793d < (d * 3.141592653589793d) * d;
    }

    private boolean inColorCircle(float f, float f2, float f3, float f4) {
        double d = f3;
        double d2 = f4;
        double d3 = ((f * f) + (f2 * f2)) * 3.141592653589793d;
        return d3 < (d * 3.141592653589793d) * d && d3 > (d2 * 3.141592653589793d) * d2;
    }

    public ColorPickerView(Context context) {
        super(context);
        this.mHeight = 300;
        this.mWidth = 300;
        this.downInCircle = true;
        this.mContext = context;
        init();
    }

    public ColorPickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHeight = 300;
        this.mWidth = 300;
        this.downInCircle = true;
        this.mContext = context;
        initConfig(context, attributeSet);
        init();
    }

    private void init() {
        this.mCircleColors = new int[]{-65536, -65281, -16776961, -16711681, -16711936, -256, -65536};
        SweepGradient sweepGradient = new SweepGradient(0.0f, 0.0f, this.mCircleColors, (float[]) null);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setShader(sweepGradient);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mHeight * 0.1f);
        this.r = ((this.mWidth / 2) * 0.7f) - (this.mPaint.getStrokeWidth() * 0.5f);
        Paint paint2 = new Paint(1);
        this.mCenterPaint = paint2;
        paint2.setColor(this.mInitialColor);
        this.mCenterPaint.setStrokeWidth(5.0f);
        this.centerRadius = (this.r - (this.mPaint.getStrokeWidth() / 2.0f)) * 0.7f;
        Paint paint3 = new Paint(1);
        this.mLinePaint = paint3;
        paint3.setColor(Color.parseColor("#000000"));
        this.mLinePaint.setStrokeWidth(4.0f);
        this.mRectColors = new int[]{-14079703, this.mCenterPaint.getColor(), -3618616};
        Paint paint4 = new Paint(1);
        this.mRectPaint = paint4;
        paint4.setStrokeWidth(5.0f);
        this.rectLeft = (-this.r) - (this.mPaint.getStrokeWidth() * 0.5f);
        this.rectTop = this.r + (this.mPaint.getStrokeWidth() * 0.5f) + (this.mLinePaint.getStrokeMiter() * 0.5f) + (this.mHeight * 0.07f);
        this.rectRight = this.r + (this.mPaint.getStrokeWidth() * 0.5f);
        this.rectBottom = this.rectTop + (this.mHeight * 0.08f);
        Paint paint5 = new Paint(1);
        this.mPaintText = paint5;
        paint5.setColor(Color.parseColor("#ffffff"));
        this.mPaintText.setTextSize(this.mHeight * 0.05f);
        this.mPaintText.setTextAlign(Paint.Align.CENTER);
    }

    private void initConfig(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ColorPickerView);
        this.mHeight = obtainStyledAttributes.getInteger(R$styleable.ColorPickerView_mHeight, 600);
        this.mWidth = obtainStyledAttributes.getInteger(R$styleable.ColorPickerView_mWidth, 600);
        this.mBackground = obtainStyledAttributes.getDrawable(R$styleable.ColorPickerView_mBackground);
        String str = TAG;
        Log.d(str, "initConfig: mWidth ===" + this.mWidth + "   mHeight ===" + this.mHeight);
        obtainStyledAttributes.recycle();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        drawableBackground(canvas);
        canvas.translate(this.mWidth / 2, this.mHeight * 0.45f);
        canvas.drawCircle(0.0f, 0.0f, this.centerRadius, this.mCenterPaint);
        float f = this.r;
        canvas.drawOval(new RectF(-f, -f, f, f), this.mPaint);
        if (this.downInCircle) {
            this.mRectColors[1] = this.mCenterPaint.getColor();
        }
        LinearGradient linearGradient = new LinearGradient(this.rectLeft, 0.0f, this.rectRight, 0.0f, this.mRectColors, (float[]) null, Shader.TileMode.MIRROR);
        this.rectShader = linearGradient;
        this.mRectPaint.setShader(linearGradient);
        canvas.drawRect(this.rectLeft, this.rectTop, this.rectRight, this.rectBottom, this.mRectPaint);
        float strokeWidth = this.mLinePaint.getStrokeWidth() / 2.0f;
        float f2 = this.rectLeft;
        float f3 = 2.0f * strokeWidth;
        canvas.drawLine(f2 - strokeWidth, this.rectTop - f3, f2 - strokeWidth, this.rectBottom + f3, this.mLinePaint);
        float f4 = this.rectLeft - f3;
        float f5 = this.rectTop;
        canvas.drawLine(f4, f5 - strokeWidth, this.rectRight + f3, f5 - strokeWidth, this.mLinePaint);
        float f6 = this.rectRight;
        canvas.drawLine(f6 + strokeWidth, this.rectTop - f3, f6 + strokeWidth, this.rectBottom + f3, this.mLinePaint);
        float f7 = this.rectLeft - f3;
        float f8 = this.rectBottom;
        canvas.drawLine(f7, f8 + strokeWidth, this.rectRight + f3, f8 + strokeWidth, this.mLinePaint);
        super.onDraw(canvas);
    }

    private void drawableBackground(Canvas canvas) {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setBounds(0, 0, this.mWidth, this.mHeight);
            this.mBackground.draw(canvas);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        getParent().requestDisallowInterceptTouchEvent(true);
        String str = TAG;
        Log.d(str, "onTouchEvent: " + motionEvent.getAction());
        float x = motionEvent.getX() - ((float) (this.mWidth / 2));
        float y = motionEvent.getY() - (((float) this.mHeight) * 0.45f);
        boolean inColorCircle = inColorCircle(x, y, this.r + (this.mPaint.getStrokeWidth() / 2.0f), this.r - (this.mPaint.getStrokeWidth() / 2.0f));
        boolean inCenter = inCenter(x, y, this.centerRadius);
        boolean inRect = inRect(x, y);
        int action = motionEvent.getAction();
        if (action == 0) {
            this.downInCircle = inColorCircle;
            this.downInRect = inRect;
            this.highlightCenter = inCenter;
        } else if (action == 1) {
            OnColorChangedListener onColorChangedListener = this.mListener;
            if (onColorChangedListener != null) {
                onColorChangedListener.colorChanged(this.mCenterPaint.getColor(), inCenter);
            }
            if (this.downInCircle) {
                this.downInCircle = false;
            }
            if (this.downInRect) {
                this.downInRect = false;
            }
            if (this.highlightCenter) {
                this.highlightCenter = false;
            }
            if (this.highlightCenterLittle) {
                this.highlightCenterLittle = false;
            }
            invalidate();
        } else if (action == 2) {
            if (this.downInCircle && inColorCircle) {
                float atan2 = (float) (((float) Math.atan2(y, x)) / 6.283185307179586d);
                if (atan2 < 0.0f) {
                    atan2 += 1.0f;
                }
                this.mCenterPaint.setColor(interpCircleColor(this.mCircleColors, atan2));
            } else if (this.downInRect && inRect) {
                this.mCenterPaint.setColor(interpRectColor(this.mRectColors, x));
            }
            boolean z2 = this.highlightCenter;
            if ((z2 && inCenter) || ((z = this.highlightCenterLittle) && inCenter)) {
                this.highlightCenter = true;
                this.highlightCenterLittle = false;
            } else if (z2 || z) {
                this.highlightCenter = false;
                this.highlightCenterLittle = true;
            } else {
                this.highlightCenter = false;
                this.highlightCenterLittle = false;
            }
            invalidate();
        }
        return true;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        String str = TAG;
        Log.d(str, "onMeasure: " + this.mWidth + "   " + this.mHeight);
        setMeasuredDimension(this.mWidth, this.mHeight);
    }

    private boolean inRect(float f, float f2) {
        return f <= this.rectRight && f >= this.rectLeft && f2 <= this.rectBottom && f2 >= this.rectTop;
    }

    private int interpCircleColor(int[] iArr, float f) {
        if (f <= 0.0f) {
            return iArr[0];
        }
        if (f >= 1.0f) {
            return iArr[iArr.length - 1];
        }
        float length = f * (iArr.length - 1);
        int i = (int) length;
        float f2 = length - i;
        int i2 = iArr[i];
        int i3 = iArr[i + 1];
        return Color.argb(ave(Color.alpha(i2), Color.alpha(i3), f2), ave(Color.red(i2), Color.red(i3), f2), ave(Color.green(i2), Color.green(i3), f2), ave(Color.blue(i2), Color.blue(i3), f2));
    }

    private int interpRectColor(int[] iArr, float f) {
        int i;
        int i2;
        float f2;
        if (f < 0.0f) {
            i = iArr[0];
            i2 = iArr[1];
            f2 = this.rectRight;
            f += f2;
        } else {
            i = iArr[1];
            i2 = iArr[2];
            f2 = this.rectRight;
        }
        float f3 = f / f2;
        return Color.argb(ave(Color.alpha(i), Color.alpha(i2), f3), ave(Color.red(i), Color.red(i2), f3), ave(Color.green(i), Color.green(i2), f3), ave(Color.blue(i), Color.blue(i2), f3));
    }

    private int ave(int i, int i2, float f) {
        return i + Math.round(f * (i2 - i));
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public int getmInitialColor() {
        return this.mInitialColor;
    }

    public void setmInitialColor(int i) {
        this.mInitialColor = i;
        init();
        invalidate();
    }

    public OnColorChangedListener getmListener() {
        return this.mListener;
    }

    public void setmListener(OnColorChangedListener onColorChangedListener) {
        this.mListener = onColorChangedListener;
    }
}
