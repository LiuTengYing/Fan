package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.UserHandle;
import android.util.SparseBooleanArray;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.util.FlagOp;
/* loaded from: classes.dex */
public class BaseIconFactory implements AutoCloseable {
    private static int PLACEHOLDER_BACKGROUND_COLOR = Color.rgb(245, 245, 245);
    private final Canvas mCanvas;
    private final ColorExtractor mColorExtractor;
    protected final Context mContext;
    private boolean mDisableColorExtractor;
    protected final int mFillResIconDpi;
    protected final int mIconBitmapSize;
    private final SparseBooleanArray mIsUserBadged;
    protected boolean mMonoIconEnabled;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds;
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private final boolean mShapeDetection;
    private final Paint mTextPaint;
    private Bitmap mWhiteShadowLayer;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;

    public static int getBadgeSizeForIconSize(int i) {
        return (int) (i * 0.444f);
    }

    protected BaseIconFactory(Context context, int i, int i2, boolean z) {
        this.mOldBounds = new Rect();
        this.mIsUserBadged = new SparseBooleanArray();
        this.mWrapperBackgroundColor = -1;
        Paint paint = new Paint(3);
        this.mTextPaint = paint;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mShapeDetection = z;
        this.mFillResIconDpi = i;
        this.mIconBitmapSize = i2;
        this.mPm = applicationContext.getPackageManager();
        this.mColorExtractor = new ColorExtractor();
        Canvas canvas = new Canvas();
        this.mCanvas = canvas;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(PLACEHOLDER_BACKGROUND_COLOR);
        paint.setTextSize(context.getResources().getDisplayMetrics().density * 20.0f);
        clear();
    }

    public BaseIconFactory(Context context, int i, int i2) {
        this(context, i, i2, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clear() {
        this.mWrapperBackgroundColor = -1;
        this.mDisableColorExtractor = false;
    }

    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mIconBitmapSize);
        }
        return this.mShadowGenerator;
    }

    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext, this.mIconBitmapSize, this.mShapeDetection);
        }
        return this.mNormalizer;
    }

    @TargetApi(33)
    public BitmapInfo createBadgedIconBitmap(Drawable drawable, IconOptions iconOptions) {
        Drawable monochrome;
        float[] fArr = new float[1];
        Drawable normalizeAndWrapToAdaptiveIcon = normalizeAndWrapToAdaptiveIcon(drawable, iconOptions == null || iconOptions.mShrinkNonAdaptiveIcons, null, fArr);
        Bitmap createIconBitmap = createIconBitmap(normalizeAndWrapToAdaptiveIcon, fArr[0]);
        boolean z = normalizeAndWrapToAdaptiveIcon instanceof AdaptiveIconDrawable;
        if (z) {
            this.mCanvas.setBitmap(createIconBitmap);
            getShadowGenerator().recreateIcon(Bitmap.createBitmap(createIconBitmap), this.mCanvas);
            this.mCanvas.setBitmap(null);
        }
        int extractColor = extractColor(createIconBitmap);
        BitmapInfo of = BitmapInfo.of(createIconBitmap, extractColor);
        if (normalizeAndWrapToAdaptiveIcon instanceof BitmapInfo.Extender) {
            of = ((BitmapInfo.Extender) normalizeAndWrapToAdaptiveIcon).getExtendedInfo(createIconBitmap, extractColor, this, fArr[0]);
        } else if (this.mMonoIconEnabled && IconProvider.ATLEAST_T && z && (monochrome = ((AdaptiveIconDrawable) normalizeAndWrapToAdaptiveIcon).getMonochrome()) != null) {
            of.setMonoIcon(createIconBitmap(new ClippedMonoDrawable(monochrome), fArr[0], this.mIconBitmapSize, Bitmap.Config.ALPHA_8), this);
        }
        return of.withFlags(getBitmapFlagOp(iconOptions));
    }

    public FlagOp getBitmapFlagOp(IconOptions iconOptions) {
        boolean z;
        FlagOp flagOp = FlagOp.NO_OP;
        if (iconOptions != null) {
            if (iconOptions.mIsInstantApp) {
                flagOp = flagOp.addFlag(2);
            }
            UserHandle userHandle = iconOptions.mUserHandle;
            if (userHandle != null) {
                int hashCode = userHandle.hashCode();
                int indexOfKey = this.mIsUserBadged.indexOfKey(hashCode);
                if (indexOfKey >= 0) {
                    z = this.mIsUserBadged.valueAt(indexOfKey);
                } else {
                    NoopDrawable noopDrawable = new NoopDrawable();
                    boolean z2 = noopDrawable != this.mPm.getUserBadgedIcon(noopDrawable, iconOptions.mUserHandle);
                    this.mIsUserBadged.put(hashCode, z2);
                    z = z2;
                }
                return flagOp.setFlag(1, z);
            }
            return flagOp;
        }
        return flagOp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap getWhiteShadowLayer() {
        if (this.mWhiteShadowLayer == null) {
            this.mWhiteShadowLayer = createScaledBitmapWithShadow(new AdaptiveIconDrawable(new ColorDrawable(-1), null));
        }
        return this.mWhiteShadowLayer;
    }

    public Bitmap createScaledBitmapWithShadow(Drawable drawable) {
        Bitmap createIconBitmap = createIconBitmap(drawable, getNormalizer().getScale(drawable, null, null, null));
        this.mCanvas.setBitmap(createIconBitmap);
        getShadowGenerator().recreateIcon(Bitmap.createBitmap(createIconBitmap), this.mCanvas);
        this.mCanvas.setBitmap(null);
        return createIconBitmap;
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(Drawable drawable, boolean z, RectF rectF, float[] fArr) {
        float scale;
        if (drawable == null) {
            return null;
        }
        if (z && !(drawable instanceof AdaptiveIconDrawable)) {
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R$drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) this.mWrapperIcon;
            adaptiveIconDrawable.setBounds(0, 0, 1, 1);
            boolean[] zArr = new boolean[1];
            scale = getNormalizer().getScale(drawable, rectF, adaptiveIconDrawable.getIconMask(), zArr);
            if (!zArr[0]) {
                FixedScaleDrawable fixedScaleDrawable = (FixedScaleDrawable) adaptiveIconDrawable.getForeground();
                fixedScaleDrawable.setDrawable(drawable);
                fixedScaleDrawable.setScale(scale);
                scale = getNormalizer().getScale(adaptiveIconDrawable, rectF, null, null);
                ((ColorDrawable) adaptiveIconDrawable.getBackground()).setColor(this.mWrapperBackgroundColor);
                drawable = adaptiveIconDrawable;
            }
        } else {
            scale = getNormalizer().getScale(drawable, rectF, null, null);
        }
        fArr[0] = scale;
        return drawable;
    }

    private Bitmap createIconBitmap(Drawable drawable, float f) {
        return createIconBitmap(drawable, f, this.mIconBitmapSize);
    }

    public Bitmap createIconBitmap(Drawable drawable, float f, int i) {
        return createIconBitmap(drawable, f, i, Bitmap.Config.ARGB_8888);
    }

    private Bitmap createIconBitmap(Drawable drawable, float f, int i, Bitmap.Config config) {
        int i2;
        int i3;
        float f2;
        Bitmap createBitmap = Bitmap.createBitmap(i, i, config);
        if (drawable == null) {
            return createBitmap;
        }
        this.mCanvas.setBitmap(createBitmap);
        this.mOldBounds.set(drawable.getBounds());
        if (drawable instanceof AdaptiveIconDrawable) {
            int max = Math.max((int) Math.ceil(0.035f * f2), Math.round((i * (1.0f - f)) / 2.0f));
            int i4 = (i - max) - max;
            drawable.setBounds(0, 0, i4, i4);
            int save = this.mCanvas.save();
            float f3 = max;
            this.mCanvas.translate(f3, f3);
            if (drawable instanceof BitmapInfo.Extender) {
                ((BitmapInfo.Extender) drawable).drawForPersistence(this.mCanvas);
            } else {
                drawable.draw(this.mCanvas);
            }
            this.mCanvas.restoreToCount(save);
        } else {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (createBitmap != null && bitmap.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(this.mContext.getResources().getDisplayMetrics());
                }
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float f4 = intrinsicWidth / intrinsicHeight;
                if (intrinsicWidth > intrinsicHeight) {
                    i3 = (int) (i / f4);
                    i2 = i;
                } else if (intrinsicHeight > intrinsicWidth) {
                    i2 = (int) (i * f4);
                    i3 = i;
                }
                int i5 = (i - i2) / 2;
                int i6 = (i - i3) / 2;
                drawable.setBounds(i5, i6, i2 + i5, i3 + i6);
                this.mCanvas.save();
                float f5 = i / 2;
                this.mCanvas.scale(f, f, f5, f5);
                drawable.draw(this.mCanvas);
                this.mCanvas.restore();
            }
            i2 = i;
            i3 = i2;
            int i52 = (i - i2) / 2;
            int i62 = (i - i3) / 2;
            drawable.setBounds(i52, i62, i2 + i52, i3 + i62);
            this.mCanvas.save();
            float f52 = i / 2;
            this.mCanvas.scale(f, f, f52, f52);
            drawable.draw(this.mCanvas);
            this.mCanvas.restore();
        }
        drawable.setBounds(this.mOldBounds);
        this.mCanvas.setBitmap(null);
        return createBitmap;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        clear();
    }

    private int extractColor(Bitmap bitmap) {
        if (this.mDisableColorExtractor) {
            return 0;
        }
        return this.mColorExtractor.findDominantColorByHue(bitmap);
    }

    /* loaded from: classes.dex */
    public static class IconOptions {
        boolean mIsInstantApp;
        boolean mShrinkNonAdaptiveIcons = true;
        UserHandle mUserHandle;

        public IconOptions setUser(UserHandle userHandle) {
            this.mUserHandle = userHandle;
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NoopDrawable extends ColorDrawable {
        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return 1;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return 1;
        }

        private NoopDrawable() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ClippedMonoDrawable extends InsetDrawable {
        private final AdaptiveIconDrawable mCrop;

        public ClippedMonoDrawable(Drawable drawable) {
            super(drawable, -AdaptiveIconDrawable.getExtraInsetFraction());
            this.mCrop = new AdaptiveIconDrawable(new ColorDrawable(-16777216), null);
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.mCrop.setBounds(getBounds());
            int save = canvas.save();
            canvas.clipPath(this.mCrop.getIconMask());
            super.draw(canvas);
            canvas.restoreToCount(save);
        }
    }
}
