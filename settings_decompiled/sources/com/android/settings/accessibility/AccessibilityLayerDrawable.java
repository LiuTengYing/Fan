package com.android.settings.accessibility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import com.android.settings.R$drawable;
import java.util.Objects;
/* loaded from: classes.dex */
public class AccessibilityLayerDrawable extends LayerDrawable {
    private AccessibilityLayerDrawableState mState;

    private AccessibilityLayerDrawable(Drawable[] drawableArr) {
        super(drawableArr);
    }

    public static AccessibilityLayerDrawable createLayerDrawable(Context context, int i, int i2) {
        AccessibilityLayerDrawable accessibilityLayerDrawable = new AccessibilityLayerDrawable(new Drawable[]{context.getDrawable(R$drawable.accessibility_button_preview_base), null});
        accessibilityLayerDrawable.updateLayerDrawable(context, i, i2);
        return accessibilityLayerDrawable;
    }

    public void updateLayerDrawable(Context context, int i, int i2) {
        Drawable drawable = context.getDrawable(i);
        drawable.setAlpha(i2);
        setDrawable(1, drawable);
        setConstantState(context, i, i2);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    private void setConstantState(Context context, int i, int i2) {
        this.mState = new AccessibilityLayerDrawableState(context, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class AccessibilityLayerDrawableState extends Drawable.ConstantState {
        private final Context mContext;
        private final int mOpacity;
        private final int mResId;

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return 0;
        }

        AccessibilityLayerDrawableState(Context context, int i, int i2) {
            this.mContext = context;
            this.mResId = i;
            this.mOpacity = i2;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return AccessibilityLayerDrawable.createLayerDrawable(this.mContext, this.mResId, this.mOpacity);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            AccessibilityLayerDrawableState accessibilityLayerDrawableState = (AccessibilityLayerDrawableState) obj;
            return this.mResId == accessibilityLayerDrawableState.mResId && this.mOpacity == accessibilityLayerDrawableState.mOpacity && Objects.equals(this.mContext, accessibilityLayerDrawableState.mContext);
        }

        public int hashCode() {
            return Objects.hash(this.mContext, Integer.valueOf(this.mResId), Integer.valueOf(this.mOpacity));
        }
    }
}
