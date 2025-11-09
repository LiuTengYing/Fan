package com.android.settings.display.syudisplay;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class ColorArcProgressBar extends View {
    private final int DEGREE_PROGRESS_DISTANCE;
    private Paint allArcPaint;
    private int aniSpeed;
    private String bgArcColor;
    private float bgArcWidth;
    private RectF bgRect;
    private int bkColor;
    private float centerX;
    private float centerY;
    private int[] colors;
    private Paint curSpeedPaint;
    private float curSpeedSize;
    private float curValues;
    private float currentAngle;
    private Paint degreePaint;
    private int diameter;
    private String hintColor;
    private Paint hintPaint;
    private float hintSize;
    private String hintString;
    private boolean isNeedContent;
    private boolean isNeedDial;
    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isShowCurrentSpeed;
    private float k;
    private float lastAngle;
    private String longDegreeColor;
    private float longdegree;
    private Drawable mBackground;
    private PaintFlagsDrawFilter mDrawFilter;
    private Drawable mThumb;
    private float maxValues;
    private ValueAnimator progressAnimator;
    private Paint progressPaint;
    private float progressWidth;
    private Matrix rotateMatrix;
    private String shortDegreeColor;
    private float shortdegree;
    private float startAngle;
    private float sweepAngle;
    private SweepGradient sweepGradient;
    private float textSize;
    private String titleString;
    private Paint vTextPaint;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        this.diameter = 20;
        this.startAngle = 200.0f;
        this.sweepAngle = 140.0f;
        this.currentAngle = 0.0f;
        this.colors = new int[]{-16711936, -256, -65536, -65536};
        this.maxValues = 60.0f;
        this.curValues = 0.0f;
        this.bgArcWidth = dipToPx(2.0f);
        this.progressWidth = dipToPx(10.0f);
        this.textSize = dipToPx(10.0f);
        this.hintSize = dipToPx(15.0f);
        this.curSpeedSize = dipToPx(13.0f);
        this.aniSpeed = 2000;
        this.longdegree = dipToPx(13.0f);
        this.shortdegree = dipToPx(5.0f);
        this.DEGREE_PROGRESS_DISTANCE = dipToPx(8.0f);
        this.hintColor = "#676767";
        this.longDegreeColor = "#111111";
        this.shortDegreeColor = "#111111";
        this.bgArcColor = "#19000000";
        this.isShowCurrentSpeed = true;
        this.bkColor = -16777216;
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.diameter = 20;
        this.startAngle = 200.0f;
        this.sweepAngle = 140.0f;
        this.currentAngle = 0.0f;
        this.colors = new int[]{-16711936, -256, -65536, -65536};
        this.maxValues = 60.0f;
        this.curValues = 0.0f;
        this.bgArcWidth = dipToPx(2.0f);
        this.progressWidth = dipToPx(10.0f);
        this.textSize = dipToPx(10.0f);
        this.hintSize = dipToPx(15.0f);
        this.curSpeedSize = dipToPx(13.0f);
        this.aniSpeed = 2000;
        this.longdegree = dipToPx(13.0f);
        this.shortdegree = dipToPx(5.0f);
        this.DEGREE_PROGRESS_DISTANCE = dipToPx(8.0f);
        this.hintColor = "#676767";
        this.longDegreeColor = "#111111";
        this.shortDegreeColor = "#111111";
        this.bgArcColor = "#19000000";
        this.isShowCurrentSpeed = true;
        this.bkColor = -16777216;
        initCofig(context, attributeSet);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.diameter = 20;
        this.startAngle = 200.0f;
        this.sweepAngle = 140.0f;
        this.currentAngle = 0.0f;
        this.colors = new int[]{-16711936, -256, -65536, -65536};
        this.maxValues = 60.0f;
        this.curValues = 0.0f;
        this.bgArcWidth = dipToPx(2.0f);
        this.progressWidth = dipToPx(10.0f);
        this.textSize = dipToPx(10.0f);
        this.hintSize = dipToPx(15.0f);
        this.curSpeedSize = dipToPx(13.0f);
        this.aniSpeed = 2000;
        this.longdegree = dipToPx(13.0f);
        this.shortdegree = dipToPx(5.0f);
        this.DEGREE_PROGRESS_DISTANCE = dipToPx(8.0f);
        this.hintColor = "#676767";
        this.longDegreeColor = "#111111";
        this.shortDegreeColor = "#111111";
        this.bgArcColor = "#19000000";
        this.isShowCurrentSpeed = true;
        this.bkColor = -16777216;
        initCofig(context, attributeSet);
        initView();
    }

    private void initCofig(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.ColorArcProgressBar);
        int color = obtainStyledAttributes.getColor(R$styleable.ColorArcProgressBar_front_color1, -16711936);
        int color2 = obtainStyledAttributes.getColor(R$styleable.ColorArcProgressBar_front_color2, color);
        int color3 = obtainStyledAttributes.getColor(R$styleable.ColorArcProgressBar_front_color3, color);
        this.colors = new int[]{color, color2, color3, color3};
        this.sweepAngle = obtainStyledAttributes.getInteger(R$styleable.ColorArcProgressBar_total_engle, 270);
        this.bgArcWidth = obtainStyledAttributes.getDimension(R$styleable.ColorArcProgressBar_back_width, dipToPx(2.0f));
        this.progressWidth = obtainStyledAttributes.getDimension(R$styleable.ColorArcProgressBar_front_width, dipToPx(10.0f));
        this.isNeedTitle = obtainStyledAttributes.getBoolean(R$styleable.ColorArcProgressBar_is_need_title, false);
        this.isNeedContent = obtainStyledAttributes.getBoolean(R$styleable.ColorArcProgressBar_is_need_content, false);
        this.isNeedUnit = obtainStyledAttributes.getBoolean(R$styleable.ColorArcProgressBar_is_need_unit, false);
        this.isNeedDial = obtainStyledAttributes.getBoolean(R$styleable.ColorArcProgressBar_is_need_dial, false);
        this.hintString = obtainStyledAttributes.getString(R$styleable.ColorArcProgressBar_string_unit);
        this.titleString = obtainStyledAttributes.getString(R$styleable.ColorArcProgressBar_string_title);
        this.curValues = obtainStyledAttributes.getFloat(R$styleable.ColorArcProgressBar_current_value, 0.0f);
        this.maxValues = obtainStyledAttributes.getFloat(R$styleable.ColorArcProgressBar_max_value, 60.0f);
        this.bkColor = obtainStyledAttributes.getColor(R$styleable.ColorArcProgressBar_bk, -16777216);
        int i = R$styleable.ColorArcProgressBar_arc_seek_bar_background;
        if (obtainStyledAttributes.hasValue(i)) {
            this.mBackground = obtainStyledAttributes.getDrawable(i);
        }
        int i2 = R$styleable.ColorArcProgressBar_seek_bar_thumb;
        if (obtainStyledAttributes.hasValue(i2)) {
            this.mThumb = obtainStyledAttributes.getDrawable(i2);
        }
        setCurrentValues(this.curValues);
        setMaxValues(this.maxValues);
        obtainStyledAttributes.recycle();
    }

    private void initView() {
        this.diameter = (getScreenWidth() * 2) / 7;
        RectF rectF = new RectF();
        this.bgRect = rectF;
        float f = this.longdegree;
        float f2 = this.progressWidth;
        int i = this.DEGREE_PROGRESS_DISTANCE;
        rectF.top = (f2 / 2.0f) + f + i;
        rectF.left = (f2 / 2.0f) + f + i;
        int i2 = this.diameter;
        rectF.right = i2 + (f2 / 2.0f) + f + i;
        rectF.bottom = i2 + (f2 / 2.0f) + f + i;
        this.centerX = ((((f * 2.0f) + f2) + i2) + (i * 2)) / 2.0f;
        this.centerY = ((((f * 2.0f) + f2) + i2) + (i * 2)) / 2.0f;
        Paint paint = new Paint();
        this.degreePaint = paint;
        paint.setColor(Color.parseColor(this.longDegreeColor));
        Paint paint2 = new Paint();
        this.allArcPaint = paint2;
        paint2.setAntiAlias(true);
        this.allArcPaint.setStyle(Paint.Style.STROKE);
        this.allArcPaint.setStrokeWidth(this.bgArcWidth);
        this.allArcPaint.setColor(this.bkColor);
        this.allArcPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint3 = new Paint();
        this.progressPaint = paint3;
        paint3.setAntiAlias(true);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth(this.progressWidth);
        this.progressPaint.setColor(-16711936);
        Paint paint4 = new Paint();
        this.vTextPaint = paint4;
        paint4.setTextSize(this.textSize);
        this.vTextPaint.setColor(-16777216);
        this.vTextPaint.setTextAlign(Paint.Align.CENTER);
        Paint paint5 = new Paint();
        this.hintPaint = paint5;
        paint5.setTextSize(this.hintSize);
        this.hintPaint.setColor(Color.parseColor(this.hintColor));
        this.hintPaint.setTextAlign(Paint.Align.CENTER);
        Paint paint6 = new Paint();
        this.curSpeedPaint = paint6;
        paint6.setTextSize(this.curSpeedSize);
        this.curSpeedPaint.setColor(Color.parseColor(this.hintColor));
        this.curSpeedPaint.setTextAlign(Paint.Align.CENTER);
        this.mDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.sweepGradient = new SweepGradient(this.centerX, this.centerY, this.colors, (float[]) null);
        this.rotateMatrix = new Matrix();
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(this.mDrawFilter);
        if (this.isNeedDial) {
            for (int i = 0; i < 40; i++) {
                if (i > 15 && i < 25) {
                    canvas.rotate(9.0f, this.centerX, this.centerY);
                } else {
                    if (i % 5 == 0) {
                        this.degreePaint.setStrokeWidth(dipToPx(2.0f));
                        this.degreePaint.setColor(Color.parseColor(this.longDegreeColor));
                        float f = this.centerX;
                        float f2 = this.centerY;
                        int i2 = this.diameter;
                        float f3 = this.progressWidth;
                        int i3 = this.DEGREE_PROGRESS_DISTANCE;
                        canvas.drawLine(f, ((f2 - (i2 / 2)) - (f3 / 2.0f)) - i3, f, (((f2 - (i2 / 2)) - (f3 / 2.0f)) - i3) - this.longdegree, this.degreePaint);
                    } else {
                        this.degreePaint.setStrokeWidth(dipToPx(1.4f));
                        this.degreePaint.setColor(Color.parseColor(this.shortDegreeColor));
                        float f4 = this.centerX;
                        float f5 = this.centerY;
                        int i4 = this.diameter;
                        float f6 = this.progressWidth;
                        int i5 = this.DEGREE_PROGRESS_DISTANCE;
                        float f7 = this.longdegree;
                        float f8 = this.shortdegree;
                        canvas.drawLine(f4, (((f5 - (i4 / 2)) - (f6 / 2.0f)) - i5) - ((f7 - f8) / 2.0f), f4, ((((f5 - (i4 / 2)) - (f6 / 2.0f)) - i5) - ((f7 - f8) / 2.0f)) - f8, this.degreePaint);
                    }
                    canvas.rotate(9.0f, this.centerX, this.centerY);
                }
            }
        }
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            float f9 = this.longdegree;
            float f10 = this.progressWidth;
            int i6 = this.DEGREE_PROGRESS_DISTANCE;
            int i7 = this.diameter;
            drawable.setBounds((int) ((f10 / 2.0f) + f9 + i6), (int) ((f10 / 2.0f) + f9 + i6), (int) (i7 + (f10 / 2.0f) + f9 + i6), (int) (i7 + f9 + (f10 / 2.0f) + i6));
            this.mBackground.draw(canvas);
        } else {
            canvas.drawArc(this.bgRect, this.startAngle, this.sweepAngle, false, this.allArcPaint);
        }
        this.rotateMatrix.setRotate(50.0f, this.centerX, this.centerY);
        this.sweepGradient.setLocalMatrix(this.rotateMatrix);
        this.progressPaint.setShader(this.sweepGradient);
        canvas.drawArc(this.bgRect, this.startAngle, this.currentAngle, false, this.progressPaint);
        if (this.isNeedContent) {
            canvas.drawText(String.format("%.0f", Float.valueOf(this.curValues)), this.centerX, this.centerY + (this.textSize / 3.0f), this.vTextPaint);
        }
        if (this.isNeedUnit) {
            canvas.drawText(this.hintString, this.centerX, this.centerY + ((this.textSize * 2.0f) / 3.0f), this.hintPaint);
        }
        if (this.isNeedTitle) {
            canvas.drawText(this.titleString, this.centerX, this.centerY - ((this.textSize * 2.0f) / 3.0f), this.curSpeedPaint);
        }
        drawThumb(canvas, this.centerX, this.centerY);
        invalidate();
    }

    private void drawThumb(Canvas canvas, float f, float f2) {
        float f3;
        float cos;
        float sin;
        float f4;
        int i;
        Drawable drawable;
        float f5 = this.diameter / 2;
        if (this.currentAngle == 70.0f) {
            cos = this.bgRect.top;
        } else {
            double d = f5;
            cos = ((float) (d - (Math.cos(Math.toRadians(Math.abs(70.0f - f3))) * d))) + ((((this.longdegree * 2.0f) + this.progressWidth) + (this.DEGREE_PROGRESS_DISTANCE * 2)) / 2.0f);
        }
        float f6 = this.currentAngle;
        if (f6 > 70.0f) {
            double d2 = f5;
            sin = (float) (d2 + (Math.sin(Math.toRadians(f6 - 70.0f)) * d2));
            f4 = (this.longdegree * 2.0f) + this.progressWidth;
            i = this.DEGREE_PROGRESS_DISTANCE;
        } else if (f6 == 70.0f) {
            double d3 = f5;
            sin = f5 + ((float) (d3 - (Math.sin(Math.toRadians(70.0d)) * d3)));
            f4 = (this.longdegree * 2.0f) + this.progressWidth;
            i = this.DEGREE_PROGRESS_DISTANCE;
        } else {
            double d4 = f5;
            sin = (float) ((Math.sin(Math.toRadians(70.0d)) * d4) - (d4 * Math.sin(Math.toRadians(70.0f - this.currentAngle))));
            f4 = (this.longdegree * 2.0f) + this.progressWidth;
            i = this.DEGREE_PROGRESS_DISTANCE;
        }
        float f7 = sin + ((f4 + (i * 2)) / 2.0f);
        this.mThumb.setBounds((int) (f7 - (drawable.getIntrinsicWidth() / 2)), (int) (cos - (this.mThumb.getIntrinsicHeight() / 2)), (int) (f7 + (this.mThumb.getIntrinsicWidth() / 2)), (int) (cos + (this.mThumb.getIntrinsicHeight() / 2)));
        this.mThumb.draw(canvas);
    }

    public void setMaxValues(float f) {
        this.maxValues = f;
        this.k = this.sweepAngle / f;
    }

    public void setCurrentValues(float f) {
        float f2 = this.maxValues;
        if (f > f2) {
            f = f2;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.curValues = f;
        float f3 = this.currentAngle;
        this.lastAngle = f3;
        setAnimation(f3, f * this.k, this.aniSpeed);
    }

    public void setBgArcWidth(int i) {
        this.bgArcWidth = i;
    }

    public void setProgressWidth(int i) {
        this.progressWidth = i;
    }

    public void setTextSize(int i) {
        this.textSize = i;
    }

    public void setHintSize(int i) {
        this.hintSize = i;
    }

    public void setUnit(String str) {
        this.hintString = str;
        invalidate();
    }

    public void setDiameter(int i) {
        this.diameter = dipToPx(i);
    }

    private void setTitle(String str) {
        this.titleString = str;
    }

    private void setIsNeedTitle(boolean z) {
        this.isNeedTitle = z;
    }

    private void setIsNeedUnit(boolean z) {
        this.isNeedUnit = z;
    }

    private void setIsNeedDial(boolean z) {
        this.isNeedDial = z;
    }

    private void setAnimation(float f, float f2, int i) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, f2);
        this.progressAnimator = ofFloat;
        ofFloat.setDuration(i);
        this.progressAnimator.setTarget(Float.valueOf(this.currentAngle));
        this.progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.syudisplay.ColorArcProgressBar.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ColorArcProgressBar.this.currentAngle = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                ColorArcProgressBar colorArcProgressBar = ColorArcProgressBar.this;
                colorArcProgressBar.curValues = colorArcProgressBar.currentAngle / ColorArcProgressBar.this.k;
            }
        });
        this.progressAnimator.start();
    }

    private int dipToPx(float f) {
        return (int) ((getContext().getResources().getDisplayMetrics().density * f) + ((f >= 0.0f ? 1 : -1) * 0.5f));
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
