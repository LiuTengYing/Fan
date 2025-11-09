package com.google.android.material.resources;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import androidx.core.math.MathUtils;
import com.android.settings.fuelgauge.AppStandbyOptimizerPreferenceController;
/* loaded from: classes2.dex */
public class TypefaceUtils {
    public static Typeface maybeCopyWithFontWeightAdjustment(Context context, Typeface typeface) {
        return maybeCopyWithFontWeightAdjustment(context.getResources().getConfiguration(), typeface);
    }

    public static Typeface maybeCopyWithFontWeightAdjustment(Configuration configuration, Typeface typeface) {
        int i = configuration.fontWeightAdjustment;
        if (i == Integer.MAX_VALUE || i == 0) {
            return null;
        }
        return Typeface.create(typeface, MathUtils.clamp(typeface.getWeight() + configuration.fontWeightAdjustment, 1, (int) AppStandbyOptimizerPreferenceController.TYPE_APP_WAKEUP), typeface.isItalic());
    }
}
