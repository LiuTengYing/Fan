package com.android.settingslib.graph;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
/* loaded from: classes2.dex */
public class BatteryMeterDrawableBase extends Drawable {
    public static final String TAG = BatteryMeterDrawableBase.class.getSimpleName();
    protected final Paint mBatteryPaint;
    private final RectF mBoltFrame;
    protected final Paint mBoltPaint;
    private final Path mBoltPath;
    private final float[] mBoltPoints;
    private final RectF mButtonFrame;
    protected float mButtonHeightFraction;
    private int mChargeColor;
    private boolean mCharging;
    private final int[] mColors;
    private final int mCriticalLevel;
    private final RectF mFrame;
    protected final Paint mFramePaint;
    private int mHeight;
    private int mIconTint;
    private final int mIntrinsicHeight;
    private final int mIntrinsicWidth;
    private int mLevel;
    private final Path mOutlinePath;
    private final Rect mPadding;
    private final RectF mPlusFrame;
    protected final Paint mPlusPaint;
    private final Path mPlusPath;
    private final float[] mPlusPoints;
    protected boolean mPowerSaveAsColorError;
    private boolean mPowerSaveEnabled;
    protected final Paint mPowersavePaint;
    private final Path mShapePath;
    private boolean mShowPercent;
    private float mTextHeight;
    protected final Paint mTextPaint;
    private final Path mTextPath;
    private String mWarningString;
    private float mWarningTextHeight;
    protected final Paint mWarningTextPaint;
    private int mWidth;

    protected float getAspectRatio() {
        return 0.58f;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    protected float getRadiusRatio() {
        return 0.05882353f;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        updateSize();
    }

    private void updateSize() {
        Rect bounds = getBounds();
        int i = bounds.bottom;
        Rect rect = this.mPadding;
        int i2 = (i - rect.bottom) - (bounds.top + rect.top);
        this.mHeight = i2;
        this.mWidth = (bounds.right - rect.right) - (bounds.left + rect.left);
        this.mWarningTextPaint.setTextSize(i2 * 0.75f);
        this.mWarningTextHeight = -this.mWarningTextPaint.getFontMetrics().ascent;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        Rect rect2 = this.mPadding;
        if (rect2.left == 0 && rect2.top == 0 && rect2.right == 0 && rect2.bottom == 0) {
            return super.getPadding(rect);
        }
        rect.set(rect2);
        return true;
    }

    private int getColorForLevel(int i) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int[] iArr = this.mColors;
            if (i2 >= iArr.length) {
                return i3;
            }
            int i4 = iArr[i2];
            int i5 = iArr[i2 + 1];
            if (i <= i4) {
                return i2 == iArr.length + (-2) ? this.mIconTint : i5;
            }
            i2 += 2;
            i3 = i5;
        }
    }

    protected int batteryColorForLevel(int i) {
        if (this.mCharging || (this.mPowerSaveEnabled && this.mPowerSaveAsColorError)) {
            return this.mChargeColor;
        }
        return getColorForLevel(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int i;
        float height;
        float[] fArr;
        float f;
        float f2;
        float[] fArr2;
        int i2 = this.mLevel;
        Rect bounds = getBounds();
        if (i2 == -1) {
            return;
        }
        float f3 = i2 / 100.0f;
        int i3 = this.mHeight;
        int aspectRatio = (int) (getAspectRatio() * this.mHeight);
        float f4 = i3;
        int round = Math.round(this.mButtonHeightFraction * f4);
        Rect rect = this.mPadding;
        int i4 = rect.left + bounds.left;
        float f5 = i4;
        float f6 = (bounds.bottom - rect.bottom) - i3;
        this.mFrame.set(f5, f6, i4 + aspectRatio, i3 + i);
        this.mFrame.offset((this.mWidth - aspectRatio) / 2, 0.0f);
        RectF rectF = this.mButtonFrame;
        float f7 = aspectRatio * 0.28f;
        float round2 = this.mFrame.left + Math.round(f7);
        RectF rectF2 = this.mFrame;
        float f8 = round;
        rectF.set(round2, rectF2.top, rectF2.right - Math.round(f7), this.mFrame.top + f8);
        this.mFrame.top += f8;
        this.mBatteryPaint.setColor(batteryColorForLevel(i2));
        if (i2 >= 96) {
            f3 = 1.0f;
        } else if (i2 <= this.mCriticalLevel) {
            f3 = 0.0f;
        }
        if (f3 == 1.0f) {
            height = this.mButtonFrame.top;
        } else {
            RectF rectF3 = this.mFrame;
            height = (rectF3.height() * (1.0f - f3)) + rectF3.top;
        }
        this.mShapePath.reset();
        this.mOutlinePath.reset();
        float radiusRatio = getRadiusRatio() * (this.mFrame.height() + f8);
        this.mShapePath.setFillType(Path.FillType.WINDING);
        this.mShapePath.addRoundRect(this.mFrame, radiusRatio, radiusRatio, Path.Direction.CW);
        this.mShapePath.addRect(this.mButtonFrame, Path.Direction.CW);
        this.mOutlinePath.addRoundRect(this.mFrame, radiusRatio, radiusRatio, Path.Direction.CW);
        Path path = new Path();
        path.addRect(this.mButtonFrame, Path.Direction.CW);
        this.mOutlinePath.op(path, Path.Op.XOR);
        if (this.mCharging) {
            RectF rectF4 = this.mFrame;
            float width = rectF4.left + (rectF4.width() / 4.0f) + 1.0f;
            RectF rectF5 = this.mFrame;
            float height2 = rectF5.top + (rectF5.height() / 6.0f);
            RectF rectF6 = this.mFrame;
            float width2 = (rectF6.right - (rectF6.width() / 4.0f)) + 1.0f;
            RectF rectF7 = this.mFrame;
            float height3 = rectF7.bottom - (rectF7.height() / 10.0f);
            RectF rectF8 = this.mBoltFrame;
            if (rectF8.left != width || rectF8.top != height2 || rectF8.right != width2 || rectF8.bottom != height3) {
                rectF8.set(width, height2, width2, height3);
                this.mBoltPath.reset();
                Path path2 = this.mBoltPath;
                RectF rectF9 = this.mBoltFrame;
                RectF rectF10 = this.mBoltFrame;
                path2.moveTo(rectF9.left + (this.mBoltPoints[0] * rectF9.width()), rectF10.top + (this.mBoltPoints[1] * rectF10.height()));
                int i5 = 2;
                while (true) {
                    fArr2 = this.mBoltPoints;
                    if (i5 >= fArr2.length) {
                        break;
                    }
                    Path path3 = this.mBoltPath;
                    RectF rectF11 = this.mBoltFrame;
                    float width3 = rectF11.left + (fArr2[i5] * rectF11.width());
                    RectF rectF12 = this.mBoltFrame;
                    path3.lineTo(width3, rectF12.top + (this.mBoltPoints[i5 + 1] * rectF12.height()));
                    i5 += 2;
                }
                Path path4 = this.mBoltPath;
                RectF rectF13 = this.mBoltFrame;
                float width4 = rectF13.left + (fArr2[0] * rectF13.width());
                RectF rectF14 = this.mBoltFrame;
                path4.lineTo(width4, rectF14.top + (this.mBoltPoints[1] * rectF14.height()));
            }
            RectF rectF15 = this.mBoltFrame;
            float f9 = rectF15.bottom;
            if (Math.min(Math.max((f9 - height) / (f9 - rectF15.top), 0.0f), 1.0f) <= 0.3f) {
                canvas.drawPath(this.mBoltPath, this.mBoltPaint);
            } else {
                this.mShapePath.op(this.mBoltPath, Path.Op.DIFFERENCE);
            }
        } else if (this.mPowerSaveEnabled) {
            float width5 = (this.mFrame.width() * 2.0f) / 3.0f;
            RectF rectF16 = this.mFrame;
            float width6 = rectF16.left + ((rectF16.width() - width5) / 2.0f);
            RectF rectF17 = this.mFrame;
            float height4 = rectF17.top + ((rectF17.height() - width5) / 2.0f);
            RectF rectF18 = this.mFrame;
            float width7 = rectF18.right - ((rectF18.width() - width5) / 2.0f);
            RectF rectF19 = this.mFrame;
            float height5 = rectF19.bottom - ((rectF19.height() - width5) / 2.0f);
            RectF rectF20 = this.mPlusFrame;
            if (rectF20.left != width6 || rectF20.top != height4 || rectF20.right != width7 || rectF20.bottom != height5) {
                rectF20.set(width6, height4, width7, height5);
                this.mPlusPath.reset();
                Path path5 = this.mPlusPath;
                RectF rectF21 = this.mPlusFrame;
                RectF rectF22 = this.mPlusFrame;
                path5.moveTo(rectF21.left + (this.mPlusPoints[0] * rectF21.width()), rectF22.top + (this.mPlusPoints[1] * rectF22.height()));
                int i6 = 2;
                while (true) {
                    fArr = this.mPlusPoints;
                    if (i6 >= fArr.length) {
                        break;
                    }
                    Path path6 = this.mPlusPath;
                    RectF rectF23 = this.mPlusFrame;
                    float width8 = rectF23.left + (fArr[i6] * rectF23.width());
                    RectF rectF24 = this.mPlusFrame;
                    path6.lineTo(width8, rectF24.top + (this.mPlusPoints[i6 + 1] * rectF24.height()));
                    i6 += 2;
                }
                Path path7 = this.mPlusPath;
                RectF rectF25 = this.mPlusFrame;
                float width9 = rectF25.left + (fArr[0] * rectF25.width());
                RectF rectF26 = this.mPlusFrame;
                path7.lineTo(width9, rectF26.top + (this.mPlusPoints[1] * rectF26.height()));
            }
            this.mShapePath.op(this.mPlusPath, Path.Op.DIFFERENCE);
            if (this.mPowerSaveAsColorError) {
                canvas.drawPath(this.mPlusPath, this.mPlusPaint);
            }
        }
        String str = null;
        if (this.mCharging || this.mPowerSaveEnabled || i2 <= this.mCriticalLevel || !this.mShowPercent) {
            f = 0.0f;
            f2 = 0.0f;
        } else {
            this.mTextPaint.setColor(getColorForLevel(i2));
            this.mTextPaint.setTextSize(f4 * (this.mLevel == 100 ? 0.38f : 0.5f));
            this.mTextHeight = -this.mTextPaint.getFontMetrics().ascent;
            str = String.valueOf(i2);
            f = (this.mWidth * 0.5f) + f5;
            f2 = ((this.mHeight + this.mTextHeight) * 0.47f) + f6;
            r6 = height > f2;
            if (!r6) {
                this.mTextPath.reset();
                this.mTextPaint.getTextPath(str, 0, str.length(), f, f2, this.mTextPath);
                this.mShapePath.op(this.mTextPath, Path.Op.DIFFERENCE);
            }
        }
        canvas.drawPath(this.mShapePath, this.mFramePaint);
        this.mFrame.top = height;
        canvas.save();
        canvas.clipRect(this.mFrame);
        canvas.drawPath(this.mShapePath, this.mBatteryPaint);
        canvas.restore();
        if (!this.mCharging && !this.mPowerSaveEnabled) {
            if (i2 <= this.mCriticalLevel) {
                canvas.drawText(this.mWarningString, (this.mWidth * 0.5f) + f5, ((this.mHeight + this.mWarningTextHeight) * 0.48f) + f6, this.mWarningTextPaint);
            } else if (r6) {
                canvas.drawText(str, f, f2, this.mTextPaint);
            }
        }
        if (!this.mCharging && this.mPowerSaveEnabled && this.mPowerSaveAsColorError) {
            canvas.drawPath(this.mOutlinePath, this.mPowersavePaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mFramePaint.setColorFilter(colorFilter);
        this.mBatteryPaint.setColorFilter(colorFilter);
        this.mWarningTextPaint.setColorFilter(colorFilter);
        this.mBoltPaint.setColorFilter(colorFilter);
        this.mPlusPaint.setColorFilter(colorFilter);
    }
}
