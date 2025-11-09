package com.android.settings.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
/* loaded from: classes.dex */
public class ColorPickerView extends View {
    private float mCircleRadius;
    private float mCircleX;
    private float mCircleY;
    private Context mContext;
    private int mCurrentColor;
    private final int mMarkerPointColor;
    private float mMarkerPointRadius;
    private float mMarkerPointStrokeWidth;
    private float mMarkerPointX;
    private float mMarkerPointY;
    private OnColorPickerViewSizeChangedListener mOnColorPickerViewSizeChangedListener;
    private OnColorPickerViewTouchListener mOnColorPickerViewTouchListener;
    private final float mRotateDegrees;
    private boolean mTouchable;
    private int mViewHeight;
    private int mViewWidth;

    /* loaded from: classes.dex */
    public interface OnColorPickerViewSizeChangedListener {
        boolean onSizeChanged(int i, int i2);
    }

    /* loaded from: classes.dex */
    public interface OnColorPickerViewTouchListener {
        void onTouchActionDown(int i, float f, float f2);

        void onTouchActionMove(int i, float f, float f2);
    }

    public ColorPickerView(Context context) {
        super(context);
        this.mMarkerPointColor = -15694761;
        this.mViewHeight = 0;
        this.mViewWidth = 0;
        this.mCircleX = 0.0f;
        this.mCircleY = 0.0f;
        this.mCircleRadius = 0.0f;
        this.mMarkerPointX = 0.0f;
        this.mMarkerPointY = 0.0f;
        this.mMarkerPointRadius = 0.0f;
        this.mMarkerPointStrokeWidth = 0.0f;
        this.mRotateDegrees = 90.0f;
        this.mCurrentColor = 0;
        this.mTouchable = true;
        this.mContext = context;
    }

    public ColorPickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMarkerPointColor = -15694761;
        this.mViewHeight = 0;
        this.mViewWidth = 0;
        this.mCircleX = 0.0f;
        this.mCircleY = 0.0f;
        this.mCircleRadius = 0.0f;
        this.mMarkerPointX = 0.0f;
        this.mMarkerPointY = 0.0f;
        this.mMarkerPointRadius = 0.0f;
        this.mMarkerPointStrokeWidth = 0.0f;
        this.mRotateDegrees = 90.0f;
        this.mCurrentColor = 0;
        this.mTouchable = true;
        this.mContext = context;
    }

    public ColorPickerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMarkerPointColor = -15694761;
        this.mViewHeight = 0;
        this.mViewWidth = 0;
        this.mCircleX = 0.0f;
        this.mCircleY = 0.0f;
        this.mCircleRadius = 0.0f;
        this.mMarkerPointX = 0.0f;
        this.mMarkerPointY = 0.0f;
        this.mMarkerPointRadius = 0.0f;
        this.mMarkerPointStrokeWidth = 0.0f;
        this.mRotateDegrees = 90.0f;
        this.mCurrentColor = 0;
        this.mTouchable = true;
        this.mContext = context;
    }

    public ColorPickerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mMarkerPointColor = -15694761;
        this.mViewHeight = 0;
        this.mViewWidth = 0;
        this.mCircleX = 0.0f;
        this.mCircleY = 0.0f;
        this.mCircleRadius = 0.0f;
        this.mMarkerPointX = 0.0f;
        this.mMarkerPointY = 0.0f;
        this.mMarkerPointRadius = 0.0f;
        this.mMarkerPointStrokeWidth = 0.0f;
        this.mRotateDegrees = 90.0f;
        this.mCurrentColor = 0;
        this.mTouchable = true;
        this.mContext = context;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mViewHeight = i2;
        this.mViewWidth = i;
        if (i == i2) {
            float f = i / 2;
            this.mCircleX = f;
            this.mCircleY = i2 / 2;
            this.mCircleRadius = f;
        } else if (i > i2) {
            this.mCircleX = i / 2;
            float f2 = i2 / 2;
            this.mCircleY = f2;
            this.mCircleRadius = f2;
        } else if (i < i2) {
            float f3 = i / 2;
            this.mCircleX = f3;
            this.mCircleY = i2 / 2;
            this.mCircleRadius = f3;
        }
        if (!this.mOnColorPickerViewSizeChangedListener.onSizeChanged(i, i2)) {
            this.mMarkerPointX = this.mCircleX;
            this.mMarkerPointY = this.mCircleY;
        }
        float f4 = this.mCircleRadius;
        float f5 = f4 / 75.0f;
        this.mMarkerPointStrokeWidth = f5;
        float f6 = f4 - f5;
        this.mMarkerPointRadius = f6 / 16.0f;
        this.mCircleRadius = (f6 / 16.0f) * 15.0f;
        Log.d("ColorPickerView", "onSizeChanged: circle X:" + this.mCircleX + " Y:" + this.mCircleY + " radius:" + this.mCircleRadius + " mMarkerPointRadius:" + this.mMarkerPointRadius);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("ColorPickerView", "onDraw: ");
        drawColorCircle(canvas);
        drawMarker(canvas);
    }

    public void setOnColorPickerViewTouchListener(OnColorPickerViewTouchListener onColorPickerViewTouchListener) {
        this.mOnColorPickerViewTouchListener = onColorPickerViewTouchListener;
    }

    public void setOnColorPickerViewSizeChangedListener(OnColorPickerViewSizeChangedListener onColorPickerViewSizeChangedListener) {
        this.mOnColorPickerViewSizeChangedListener = onColorPickerViewSizeChangedListener;
    }

    public void setTouchable(boolean z) {
        this.mTouchable = z;
    }

    private void drawColorCircle(Canvas canvas) {
        int[] iArr = new int[7];
        float[] fArr = {0.0f, 0.498f, 1.0f};
        for (int i = 0; i < 7; i++) {
            fArr[0] = 360 - ((i * 60) % 360);
            iArr[i] = Color.HSVToColor(fArr);
        }
        iArr[6] = iArr[0];
        ComposeShader composeShader = new ComposeShader(new SweepGradient(this.mCircleX, this.mCircleY, iArr, (float[]) null), new RadialGradient(this.mCircleX, this.mCircleY, this.mCircleRadius, new int[]{-1, 0}, (float[]) null, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER);
        Matrix matrix = new Matrix();
        matrix.setRotate(-90.0f, this.mCircleX, this.mCircleY);
        composeShader.setLocalMatrix(matrix);
        Paint paint = new Paint();
        paint.setShader(composeShader);
        paint.setAntiAlias(true);
        canvas.drawCircle(this.mCircleX, this.mCircleY, this.mCircleRadius, paint);
    }

    private void drawMarker(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(-15694761);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(this.mMarkerPointStrokeWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(this.mMarkerPointX, this.mMarkerPointY, this.mMarkerPointRadius, paint);
        Paint paint2 = new Paint();
        paint2.setColor(-15694761);
        paint2.setStrokeWidth(this.mMarkerPointStrokeWidth);
        paint2.setAntiAlias(true);
        float f = this.mMarkerPointX;
        float f2 = this.mMarkerPointRadius;
        float f3 = this.mMarkerPointY;
        canvas.drawLine(f - ((float) (f2 * 0.6d)), f3, f + ((float) (f2 * 0.6d)), f3, paint2);
        float f4 = this.mMarkerPointX;
        float f5 = this.mMarkerPointY;
        float f6 = this.mMarkerPointRadius;
        canvas.drawLine(f4, f5 - ((float) (f6 * 0.6d)), f4, f5 + ((float) (f6 * 0.6d)), paint2);
    }

    private int getColorAtPoint(float f, float f2) {
        Log.d("ColorPickerView", "getColorAtPoint  eventX = " + f + ",  eventY = " + f2);
        double radians = (double) ((float) Math.toRadians(90.0d));
        float cos = (float) ((((double) (f - this.mCircleX)) * Math.cos(radians)) - (((double) (f2 - this.mCircleY)) * Math.sin(radians)));
        float sin = (float) ((((double) (f - this.mCircleX)) * Math.sin(radians)) + (((double) (f2 - this.mCircleY)) * Math.cos(radians)));
        double sqrt = Math.sqrt((double) ((cos * cos) + (sin * sin)));
        float[] fArr = {0.0f, 0.0f, 1.0f};
        fArr[0] = ((float) ((Math.atan2(sin, -cos) / 3.141592653589793d) * 180.0d)) + 180.0f;
        float max = Math.max(0.0f, Math.min(1.0f, (float) (sqrt / this.mCircleRadius)));
        fArr[1] = max;
        fArr[1] = max * 0.498f;
        return Color.HSVToColor(fArr);
    }

    public void setMarkerPoint(float f, float f2) {
        this.mMarkerPointX = f;
        this.mMarkerPointY = f2;
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mTouchable) {
            int action = motionEvent.getAction();
            if (action == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                Log.d("ColorPickerView", "onTouchEvent DOWN: x:" + motionEvent.getX() + " Y:" + motionEvent.getY());
                if (isInside(motionEvent.getX(), motionEvent.getY())) {
                    this.mMarkerPointX = motionEvent.getX();
                    this.mMarkerPointY = motionEvent.getY();
                    Log.d("ColorPickerView", "onTouchEvent DOWN: in side");
                    int colorAtPoint = getColorAtPoint(this.mMarkerPointX, this.mMarkerPointY);
                    this.mCurrentColor = colorAtPoint;
                    this.mOnColorPickerViewTouchListener.onTouchActionDown(colorAtPoint, this.mMarkerPointX, this.mMarkerPointY);
                    invalidate();
                } else {
                    Log.d("ColorPickerView", "onTouchEvent DOWN: out side");
                    return false;
                }
            } else if (action == 1) {
                Log.d("ColorPickerView", "onTouchEvent UP: x:" + motionEvent.getX() + " Y:" + motionEvent.getY());
            } else if (action == 2) {
                Log.d("ColorPickerView", "onTouchEvent MOVE: x:" + motionEvent.getX() + " Y:" + motionEvent.getY());
                if (isInside(motionEvent.getX(), motionEvent.getY())) {
                    this.mMarkerPointX = motionEvent.getX();
                    this.mMarkerPointY = motionEvent.getY();
                    Log.d("ColorPickerView", "onTouchEvent MOVE: in side");
                } else {
                    Log.d("ColorPickerView", "onTouchEvent MOVE: out side");
                    calculateNearestCoordinate(motionEvent.getX(), motionEvent.getY());
                }
                if (isInside(this.mMarkerPointX, this.mMarkerPointY)) {
                    int colorAtPoint2 = getColorAtPoint(this.mMarkerPointX, this.mMarkerPointY);
                    this.mCurrentColor = colorAtPoint2;
                    this.mOnColorPickerViewTouchListener.onTouchActionMove(colorAtPoint2, this.mMarkerPointX, this.mMarkerPointY);
                }
                invalidate();
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean isInside(float f, float f2) {
        float f3 = f - this.mCircleX;
        float f4 = f2 - this.mCircleY;
        return Math.sqrt((double) ((f3 * f3) + (f4 * f4))) <= ((double) this.mCircleRadius);
    }

    public void calculateNearestCoordinate(float f, float f2) {
        float f3 = f - this.mCircleX;
        float f4 = f2 - this.mCircleY;
        double sqrt = Math.sqrt((f3 * f3) + (f4 * f4));
        float min = f3 * Math.min(1.0f, (float) (this.mCircleRadius / sqrt));
        float min2 = f4 * Math.min(1.0f, (float) (this.mCircleRadius / sqrt));
        this.mMarkerPointX = this.mCircleX + min;
        this.mMarkerPointY = this.mCircleY + min2;
    }

    public float getCircleRadius() {
        return this.mCircleRadius;
    }

    public float[] getCircleCenter() {
        return new float[]{this.mCircleX, this.mCircleY};
    }

    public int[] getPickerViewSize() {
        return new int[]{this.mViewWidth, this.mViewHeight};
    }
}
