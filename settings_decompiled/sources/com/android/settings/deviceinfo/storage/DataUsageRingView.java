package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.android.settings.R$color;
import com.android.settings.SettingsApplication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class DataUsageRingView extends View {
    private int centerPointRadius;
    private boolean isShowRateText;
    private List<Integer> mColorList;
    private Context mContext;
    private Paint mPaint;
    private int mRadius;
    private List<Float> mRateList;
    private List<Float> mStartAngel;
    private int offset;
    private float radius;
    private float showRateSize;
    private int sign;
    private float startAngle;
    private int xOffset;
    private int yOffset;

    public DataUsageRingView(Context context) {
        this(context, null);
    }

    public DataUsageRingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DataUsageRingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mColorList = new ArrayList();
        this.mRateList = new ArrayList();
        this.mStartAngel = new ArrayList();
        this.isShowRateText = true;
        this.startAngle = 270.0f;
        this.offset = 0;
        this.sign = 0;
        this.mRadius = 150;
        this.mContext = context;
        init();
    }

    public void setData(float[] fArr) {
        this.mRateList.clear();
        this.mStartAngel.clear();
        this.mColorList.clear();
        int[] iArr = {ContextCompat.getColor(this.mContext, R$color.storage_ring_view_system), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_app), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_img), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_audio), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_video), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_docu), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_game), ContextCompat.getColor(this.mContext, R$color.storage_ring_view_trash)};
        if (fArr.length > 8) {
            Log.d("fangli", "out of bound" + fArr.length);
            return;
        }
        int i = 0;
        for (int i2 = 0; i2 < fArr.length; i2++) {
            float f = fArr[i2];
            if (f > 0.0f) {
                this.mRateList.add(Float.valueOf((f / 100.0f) * 360.0f));
                if (i2 == 0) {
                    this.mStartAngel.add(Float.valueOf(this.startAngle));
                }
                if (i2 > 0 && i >= 1) {
                    Log.d("fangli", "setData: " + i + "  " + i2);
                    int i3 = i + (-1);
                    this.mStartAngel.add(Float.valueOf(this.startAngle + this.mRateList.get(i3).floatValue()));
                    this.startAngle = this.startAngle + this.mRateList.get(i3).floatValue();
                }
                i++;
                this.mColorList.add(Integer.valueOf(iArr[i2]));
            }
        }
        Collections.reverse(this.mStartAngel);
        Collections.reverse(this.mRateList);
        Collections.reverse(this.mColorList);
        invalidate();
    }

    public void setRadius(int i) {
        this.mRadius = i;
        init();
    }

    private void init() {
        if (SettingsApplication.mResources.getDisplayMetrics().densityDpi == 120) {
            this.radius = 100.0f;
        } else {
            this.radius = 150.0f;
        }
        this.centerPointRadius = 2;
        this.xOffset = 0;
        this.yOffset = 0;
        this.showRateSize = 10.0f;
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setColor(-65536);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setTextSize(this.showRateSize);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode2 == Integer.MIN_VALUE) {
            size2 = ((int) (this.radius * 2.0f)) + (this.centerPointRadius * 2) + getPaddingLeft() + getPaddingRight() + ((this.xOffset + this.yOffset) * 2);
        }
        if (mode == Integer.MIN_VALUE) {
            size = ((int) (this.radius * 2.0f)) + (this.centerPointRadius * 2) + getPaddingLeft() + getPaddingRight() + ((this.xOffset + this.yOffset) * 2);
        }
        setMeasuredDimension(size, size2);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        this.startAngle = 270.0f;
        int i2 = this.centerPointRadius;
        int i3 = this.xOffset;
        int i4 = this.yOffset;
        float f = this.radius;
        RectF rectF = new RectF(i2 + 0 + i3 + i4, i2 + 0 + i3 + i4, (f * 2.0f) + i2 + i3 + i4, (f * 2.0f) + i2 + i3 + i4);
        int i5 = this.centerPointRadius;
        int i6 = this.xOffset;
        int i7 = this.yOffset;
        float f2 = this.radius;
        RectF rectF2 = new RectF(i5 + 0 + i6 + i7 + ((f2 - (f2 / 1.4f)) / 2.0f), i5 + 0 + i6 + i7 + ((f2 - (f2 / 1.4f)) / 2.0f), (((f2 * 2.0f) + i5) + (i6 + i7)) - ((f2 - (f2 / 1.4f)) / 2.0f), (((f2 * 2.0f) + i5) + (i6 + i7)) - ((f2 - (f2 / 1.4f)) / 2.0f));
        ArrayList arrayList = new ArrayList();
        this.mPaint.setColor(ContextCompat.getColor(this.mContext, R$color.storage_ring_view_bk));
        this.mPaint.setStyle(Paint.Style.FILL);
        float f3 = this.radius;
        int i8 = this.centerPointRadius;
        int i9 = this.xOffset;
        int i10 = this.yOffset;
        canvas.drawCircle(i8 + f3 + i9 + i10, i8 + f3 + i9 + i10, f3, this.mPaint);
        List<Float> list = this.mStartAngel;
        if (list != null && this.mRateList != null && list.size() >= this.mRateList.size()) {
            int i11 = 0;
            while (i11 < this.mRateList.size()) {
                this.mPaint.setStyle(Paint.Style.FILL);
                this.mPaint.setColor(this.mColorList.get(i11).intValue());
                if (this.mRateList.get(i11).floatValue() > 0.0f) {
                    i = i11;
                    canvas.drawArc(rectF, this.mStartAngel.get(i11).floatValue(), this.mRateList.get(i11).floatValue(), true, this.mPaint);
                    dealPoint(rectF2, this.mStartAngel.get(i).floatValue(), this.mRateList.get(i).floatValue(), arrayList);
                    Point point = arrayList.get(i);
                    float f4 = this.radius;
                    canvas.drawCircle(point.x, point.y, (f4 - (f4 / 1.4f)) / 2.0f, this.mPaint);
                    Log.d("fangli", "mStartAngel: <<<" + this.mStartAngel.get(i));
                    Log.d("fangli", "mRateList: <<<" + this.mRateList.get(i));
                } else {
                    i = i11;
                }
                i11 = i + 1;
            }
        }
        List<Float> list2 = this.mStartAngel;
        if (list2 != null && this.mRateList != null && list2.size() < this.mRateList.size()) {
            for (int i12 = 0; i12 < this.mStartAngel.size(); i12++) {
                this.mPaint.setStyle(Paint.Style.FILL);
                this.mPaint.setColor(this.mColorList.get(i12).intValue());
                if (this.mRateList.get(i12).floatValue() > 0.0f) {
                    canvas.drawArc(rectF, this.mStartAngel.get(i12).floatValue(), this.mRateList.get(i12).floatValue(), true, this.mPaint);
                    dealPoint(rectF2, this.mStartAngel.get(i12).floatValue(), this.mRateList.get(i12).floatValue(), arrayList);
                    Point point2 = arrayList.get(i12);
                    float f5 = this.radius;
                    canvas.drawCircle(point2.x, point2.y, (f5 - (f5 / 1.4f)) / 2.0f, this.mPaint);
                    Log.d("fangli", "mStartAngel>>>: " + this.mStartAngel.get(i12));
                    Log.d("fangli", "mRateList:>>> " + this.mRateList.get(i12));
                }
            }
        }
        this.mPaint.setColor(ContextCompat.getColor(this.mContext, R$color.status_bar_color));
        this.mPaint.setStyle(Paint.Style.FILL);
        float f6 = this.radius;
        int i13 = this.centerPointRadius;
        int i14 = this.xOffset;
        int i15 = this.yOffset;
        canvas.drawCircle(i13 + f6 + i14 + i15, i13 + f6 + i14 + i15, f6 / 1.4f, this.mPaint);
    }

    private void dealPoint(RectF rectF, float f, float f2, List<Point> list) {
        Path path = new Path();
        path.addArc(rectF, f, f2);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float[] fArr = {0.0f, 0.0f};
        pathMeasure.getPosTan(pathMeasure.getLength() / 1, fArr, null);
        list.add(new Point(Math.round(fArr[0]), Math.round(fArr[1])));
    }
}
