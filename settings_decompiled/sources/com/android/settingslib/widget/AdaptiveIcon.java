package com.android.settingslib.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import com.android.settingslib.drawer.Tile;
/* loaded from: classes2.dex */
public class AdaptiveIcon extends LayerDrawable {
    private AdaptiveConstantState mAdaptiveConstantState;
    int mBackgroundColor;

    public AdaptiveIcon(Context context, Drawable drawable) {
        this(context, drawable, R$dimen.dashboard_tile_foreground_image_inset);
    }

    public AdaptiveIcon(Context context, Drawable drawable, int i) {
        super(new Drawable[]{new AdaptiveIconShapeDrawable(context.getResources()), drawable});
        this.mBackgroundColor = -1;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(i);
        setLayerInset(1, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        this.mAdaptiveConstantState = new AdaptiveConstantState(context, drawable);
    }

    public void setBackgroundColor(Context context, Tile tile) {
        int i;
        Bundle metaData = tile.getMetaData();
        if (metaData != null) {
            try {
                int i2 = metaData.getInt("com.android.settings.bg.argb", 0);
                if (i2 == 0 && (i = metaData.getInt("com.android.settings.bg.hint", 0)) != 0) {
                    i2 = context.getPackageManager().getResourcesForApplication(tile.getPackageName()).getColor(i, null);
                }
                if (i2 != 0) {
                    setBackgroundColor(i2);
                    return;
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("AdaptiveHomepageIcon", "Failed to set background color for " + tile.getPackageName());
            }
        }
        setBackgroundColor(context.getColor(R$color.homepage_generic_icon_background));
    }

    public void setBackgroundColor(int i) {
        this.mBackgroundColor = i;
        getDrawable(0).setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
        Log.d("AdaptiveHomepageIcon", "Setting background color " + this.mBackgroundColor);
        this.mAdaptiveConstantState.mColor = i;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mAdaptiveConstantState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class AdaptiveConstantState extends Drawable.ConstantState {
        int mColor;
        Context mContext;
        Drawable mDrawable;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        AdaptiveConstantState(Context context, Drawable drawable) {
            this.mContext = context;
            this.mDrawable = drawable;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            AdaptiveIcon adaptiveIcon = new AdaptiveIcon(this.mContext, this.mDrawable);
            adaptiveIcon.setBackgroundColor(this.mColor);
            return adaptiveIcon;
        }
    }
}
