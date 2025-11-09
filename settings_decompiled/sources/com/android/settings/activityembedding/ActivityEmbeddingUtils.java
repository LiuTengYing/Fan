package com.android.settings.activityembedding;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.TypedValue;
import com.android.settings.R$dimen;
import com.android.settings.SettingsApplication;
/* loaded from: classes.dex */
public class ActivityEmbeddingUtils {
    public static int getMinCurrentScreenSplitWidthPx(Context context) {
        return (int) TypedValue.applyDimension(1, 600.0f, context.getResources().getDisplayMetrics());
    }

    public static int getMinSmallestScreenSplitWidthPx(Context context) {
        return (int) TypedValue.applyDimension(1, 720.0f, context.getResources().getDisplayMetrics());
    }

    public static float getSplitRatio(Context context) {
        if (SettingsApplication.mWidthFix > SettingsApplication.mHeightFix) {
            return context.getResources().getFloat(R$dimen.config_activity_embed_split_ratio);
        }
        return context.getResources().getFloat(R$dimen.config_activity_embed_split_ratio_h);
    }

    public static boolean isEmbeddingActivityEnabled(Context context) {
        boolean isEnabled = FeatureFlagUtils.isEnabled(context, "settings_support_large_screen");
        Log.d("ActivityEmbeddingUtils", "isFlagEnabled = " + isEnabled);
        Log.d("ActivityEmbeddingUtils", "isSplitSupported = true");
        return isEnabled;
    }

    public static boolean isRegularHomepageLayout(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels >= ((int) TypedValue.applyDimension(1, 0.0f, displayMetrics));
    }
}
