package com.android.launcher3.icons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import com.android.launcher3.icons.FastBitmapDrawable;
/* loaded from: classes.dex */
public class ThemedIconDrawable extends FastBitmapDrawable {
    final BitmapInfo bitmapInfo;
    final int colorBg;
    final int colorFg;
    private final Bitmap mBgBitmap;
    private final ColorFilter mBgFilter;
    private final Paint mBgPaint;
    private final ColorFilter mMonoFilter;
    private final Bitmap mMonoIcon;
    private final Paint mMonoPaint;

    protected ThemedIconDrawable(ThemedConstantState themedConstantState) {
        super(themedConstantState.mBitmap, themedConstantState.colorFg);
        Paint paint = new Paint(3);
        this.mMonoPaint = paint;
        Paint paint2 = new Paint(3);
        this.mBgPaint = paint2;
        BitmapInfo bitmapInfo = themedConstantState.bitmapInfo;
        this.bitmapInfo = bitmapInfo;
        int i = themedConstantState.colorBg;
        this.colorBg = i;
        int i2 = themedConstantState.colorFg;
        this.colorFg = i2;
        this.mMonoIcon = bitmapInfo.mMono;
        BlendModeColorFilter blendModeColorFilter = new BlendModeColorFilter(i2, BlendMode.SRC_IN);
        this.mMonoFilter = blendModeColorFilter;
        paint.setColorFilter(blendModeColorFilter);
        this.mBgBitmap = bitmapInfo.mWhiteShadowLayer;
        BlendModeColorFilter blendModeColorFilter2 = new BlendModeColorFilter(i, BlendMode.SRC_IN);
        this.mBgFilter = blendModeColorFilter2;
        paint2.setColorFilter(blendModeColorFilter2);
    }

    @Override // com.android.launcher3.icons.FastBitmapDrawable
    protected void drawInternal(Canvas canvas, Rect rect) {
        canvas.drawBitmap(this.mBgBitmap, (Rect) null, rect, this.mBgPaint);
        canvas.drawBitmap(this.mMonoIcon, (Rect) null, rect, this.mMonoPaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.launcher3.icons.FastBitmapDrawable
    public void updateFilter() {
        super.updateFilter();
        int i = this.mIsDisabled ? (int) (this.mDisabledAlpha * 255.0f) : 255;
        this.mBgPaint.setAlpha(i);
        this.mBgPaint.setColorFilter(this.mIsDisabled ? new BlendModeColorFilter(FastBitmapDrawable.getDisabledColor(this.colorBg), BlendMode.SRC_IN) : this.mBgFilter);
        this.mMonoPaint.setAlpha(i);
        this.mMonoPaint.setColorFilter(this.mIsDisabled ? new BlendModeColorFilter(FastBitmapDrawable.getDisabledColor(this.colorFg), BlendMode.SRC_IN) : this.mMonoFilter);
    }

    @Override // com.android.launcher3.icons.FastBitmapDrawable
    public FastBitmapDrawable.FastBitmapConstantState newConstantState() {
        return new ThemedConstantState(this.bitmapInfo, this.colorBg, this.colorFg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ThemedConstantState extends FastBitmapDrawable.FastBitmapConstantState {
        final BitmapInfo bitmapInfo;
        final int colorBg;
        final int colorFg;

        public ThemedConstantState(BitmapInfo bitmapInfo, int i, int i2) {
            super(bitmapInfo.icon, bitmapInfo.color);
            this.bitmapInfo = bitmapInfo;
            this.colorBg = i;
            this.colorFg = i2;
        }

        @Override // com.android.launcher3.icons.FastBitmapDrawable.FastBitmapConstantState
        public FastBitmapDrawable createDrawable() {
            return new ThemedIconDrawable(this);
        }
    }

    public static FastBitmapDrawable newDrawable(BitmapInfo bitmapInfo, Context context) {
        int[] colors = getColors(context);
        return new ThemedConstantState(bitmapInfo, colors[0], colors[1]).newDrawable();
    }

    public static int[] getColors(Context context) {
        Resources resources = context.getResources();
        int[] iArr = new int[2];
        if ((resources.getConfiguration().uiMode & 48) == 32) {
            iArr[0] = resources.getColor(17170471);
            iArr[1] = resources.getColor(17170490);
        } else {
            iArr[0] = resources.getColor(17170490);
            iArr[1] = resources.getColor(17170483);
        }
        return iArr;
    }
}
