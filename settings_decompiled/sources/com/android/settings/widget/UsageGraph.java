package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settingslib.R$color;
import com.android.settingslib.R$dimen;
/* loaded from: classes.dex */
public class UsageGraph extends View {
    private int mAccentColor;
    private final int mCornerRadius;
    private final Drawable mDivider;
    private final int mDividerSize;
    private final Paint mDottedPaint;
    private final Paint mFillPaint;
    private final Paint mLinePaint;
    private final SparseIntArray mLocalPaths;
    private final SparseIntArray mLocalProjectedPaths;
    private float mMaxX;
    private float mMaxY;
    private float mMiddleDividerLoc;
    private int mMiddleDividerTint;
    private final Path mPath;
    private final SparseIntArray mPaths;
    private final SparseIntArray mProjectedPaths;
    private final Drawable mTintedDivider;
    private int mTopDividerTint;

    private int getColor(int i, float f) {
        return ((((int) (f * 255.0f)) << 24) | 16777215) & i;
    }

    public UsageGraph(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPath = new Path();
        this.mPaths = new SparseIntArray();
        this.mLocalPaths = new SparseIntArray();
        this.mProjectedPaths = new SparseIntArray();
        this.mLocalProjectedPaths = new SparseIntArray();
        this.mMaxX = 100.0f;
        this.mMaxY = 100.0f;
        this.mMiddleDividerLoc = 0.5f;
        this.mMiddleDividerTint = -1;
        this.mTopDividerTint = -1;
        Resources resources = context.getResources();
        Paint paint = new Paint();
        this.mLinePaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.usage_graph_line_corner_radius);
        this.mCornerRadius = dimensionPixelSize;
        paint.setPathEffect(new CornerPathEffect(dimensionPixelSize));
        paint.setStrokeWidth(resources.getDimensionPixelSize(R$dimen.usage_graph_line_width));
        Paint paint2 = new Paint(paint);
        this.mFillPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        Paint paint3 = new Paint(paint);
        this.mDottedPaint = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        float dimensionPixelSize2 = resources.getDimensionPixelSize(R$dimen.usage_graph_dot_size);
        paint3.setStrokeWidth(3.0f * dimensionPixelSize2);
        paint3.setPathEffect(new DashPathEffect(new float[]{dimensionPixelSize2, resources.getDimensionPixelSize(R$dimen.usage_graph_dot_interval)}, 0.0f));
        paint3.setColor(context.getColor(R$color.usage_graph_dots));
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843284, typedValue, true);
        this.mDivider = context.getDrawable(typedValue.resourceId);
        this.mTintedDivider = context.getDrawable(typedValue.resourceId);
        this.mDividerSize = resources.getDimensionPixelSize(R$dimen.usage_graph_divider_size);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearPaths() {
        this.mPaths.clear();
        this.mLocalPaths.clear();
        this.mProjectedPaths.clear();
        this.mLocalProjectedPaths.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMax(int i, int i2) {
        long currentTimeMillis = System.currentTimeMillis();
        this.mMaxX = i;
        this.mMaxY = i2;
        calculateLocalPaths();
        postInvalidate();
        BatteryUtils.logRuntime("UsageGraph", "setMax", currentTimeMillis);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDividerLoc(int i) {
        this.mMiddleDividerLoc = 1.0f - (i / this.mMaxY);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDividerColors(int i, int i2) {
        this.mMiddleDividerTint = i;
        this.mTopDividerTint = i2;
    }

    public void addPath(SparseIntArray sparseIntArray) {
        addPathAndUpdate(sparseIntArray, this.mPaths, this.mLocalPaths);
    }

    public void addProjectedPath(SparseIntArray sparseIntArray) {
        addPathAndUpdate(sparseIntArray, this.mProjectedPaths, this.mLocalProjectedPaths);
    }

    private void addPathAndUpdate(SparseIntArray sparseIntArray, SparseIntArray sparseIntArray2, SparseIntArray sparseIntArray3) {
        long currentTimeMillis = System.currentTimeMillis();
        int size = sparseIntArray.size();
        for (int i = 0; i < size; i++) {
            sparseIntArray2.put(sparseIntArray.keyAt(i), sparseIntArray.valueAt(i));
        }
        sparseIntArray2.put(sparseIntArray.keyAt(sparseIntArray.size() - 1) + 1, -1);
        calculateLocalPaths(sparseIntArray2, sparseIntArray3);
        postInvalidate();
        BatteryUtils.logRuntime("UsageGraph", "addPathAndUpdate", currentTimeMillis);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAccentColor(int i) {
        this.mAccentColor = i;
        this.mLinePaint.setColor(i);
        updateGradient();
        postInvalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        long currentTimeMillis = System.currentTimeMillis();
        super.onSizeChanged(i, i2, i3, i4);
        updateGradient();
        calculateLocalPaths();
        BatteryUtils.logRuntime("UsageGraph", "onSizeChanged", currentTimeMillis);
    }

    private void calculateLocalPaths() {
        calculateLocalPaths(this.mPaths, this.mLocalPaths);
        calculateLocalPaths(this.mProjectedPaths, this.mLocalProjectedPaths);
    }

    void calculateLocalPaths(SparseIntArray sparseIntArray, SparseIntArray sparseIntArray2) {
        long currentTimeMillis = System.currentTimeMillis();
        if (getWidth() == 0) {
            return;
        }
        sparseIntArray2.clear();
        int i = -1;
        boolean z = false;
        int i2 = 0;
        for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
            int keyAt = sparseIntArray.keyAt(i3);
            int valueAt = sparseIntArray.valueAt(i3);
            if (valueAt != -1) {
                i2 = getX(keyAt);
                i = getY(valueAt);
                if (sparseIntArray2.size() > 0) {
                    int keyAt2 = sparseIntArray2.keyAt(sparseIntArray2.size() - 1);
                    int valueAt2 = sparseIntArray2.valueAt(sparseIntArray2.size() - 1);
                    if (valueAt2 != -1 && !hasDiff(keyAt2, i2) && !hasDiff(valueAt2, i)) {
                        z = true;
                    }
                }
                sparseIntArray2.put(i2, i);
                z = false;
            } else if (i3 == 1) {
                sparseIntArray2.put(getX(keyAt + 1) - 1, getY(0.0f));
            } else {
                if (i3 == sparseIntArray.size() - 1 && z) {
                    sparseIntArray2.put(i2, i);
                }
                sparseIntArray2.put(i2 + 1, -1);
                z = false;
            }
        }
        BatteryUtils.logRuntime("UsageGraph", "calculateLocalPaths", currentTimeMillis);
    }

    private boolean hasDiff(int i, int i2) {
        return Math.abs(i2 - i) >= this.mCornerRadius;
    }

    private int getX(float f) {
        return (int) ((f / this.mMaxX) * getWidth());
    }

    private int getY(float f) {
        return (int) (getHeight() * (1.0f - (f / this.mMaxY)));
    }

    private void updateGradient() {
        this.mFillPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, getHeight(), getColor(this.mAccentColor, 0.2f), 0, Shader.TileMode.CLAMP));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.mMiddleDividerLoc != 0.0f) {
            drawDivider(0, canvas, this.mTopDividerTint);
        }
        drawDivider((int) ((canvas.getHeight() - this.mDividerSize) * this.mMiddleDividerLoc), canvas, this.mMiddleDividerTint);
        drawDivider(canvas.getHeight() - this.mDividerSize, canvas, -1);
        if (this.mLocalPaths.size() == 0 && this.mLocalProjectedPaths.size() == 0) {
            return;
        }
        canvas.save();
        if (getLayoutDirection() == 1) {
            canvas.scale(-1.0f, 1.0f, canvas.getWidth() * 0.5f, 0.0f);
        }
        drawLinePath(canvas, this.mLocalProjectedPaths, this.mDottedPaint);
        drawFilledPath(canvas, this.mLocalPaths, this.mFillPaint);
        drawLinePath(canvas, this.mLocalPaths, this.mLinePaint);
        canvas.restore();
        BatteryUtils.logRuntime("UsageGraph", "onDraw", currentTimeMillis);
    }

    private void drawLinePath(Canvas canvas, SparseIntArray sparseIntArray, Paint paint) {
        if (sparseIntArray.size() == 0) {
            return;
        }
        this.mPath.reset();
        this.mPath.moveTo(sparseIntArray.keyAt(0), sparseIntArray.valueAt(0));
        int i = 1;
        while (i < sparseIntArray.size()) {
            int keyAt = sparseIntArray.keyAt(i);
            int valueAt = sparseIntArray.valueAt(i);
            if (valueAt == -1) {
                i++;
                if (i < sparseIntArray.size()) {
                    this.mPath.moveTo(sparseIntArray.keyAt(i), sparseIntArray.valueAt(i));
                }
            } else {
                this.mPath.lineTo(keyAt, valueAt);
            }
            i++;
        }
        canvas.drawPath(this.mPath, paint);
    }

    void drawFilledPath(Canvas canvas, SparseIntArray sparseIntArray, Paint paint) {
        if (sparseIntArray.size() == 0) {
            return;
        }
        this.mPath.reset();
        float keyAt = sparseIntArray.keyAt(0);
        this.mPath.moveTo(sparseIntArray.keyAt(0), sparseIntArray.valueAt(0));
        int i = 1;
        while (i < sparseIntArray.size()) {
            int keyAt2 = sparseIntArray.keyAt(i);
            int valueAt = sparseIntArray.valueAt(i);
            if (valueAt == -1) {
                this.mPath.lineTo(sparseIntArray.keyAt(i - 1), getHeight());
                this.mPath.lineTo(keyAt, getHeight());
                this.mPath.close();
                i++;
                if (i < sparseIntArray.size()) {
                    keyAt = sparseIntArray.keyAt(i);
                    this.mPath.moveTo(sparseIntArray.keyAt(i), sparseIntArray.valueAt(i));
                }
            } else {
                this.mPath.lineTo(keyAt2, valueAt);
            }
            i++;
        }
        canvas.drawPath(this.mPath, paint);
    }

    private void drawDivider(int i, Canvas canvas, int i2) {
        Drawable drawable = this.mDivider;
        if (i2 != -1) {
            this.mTintedDivider.setTint(i2);
            drawable = this.mTintedDivider;
        }
        drawable.setBounds(0, i, canvas.getWidth(), this.mDividerSize + i);
        drawable.draw(canvas);
    }
}
