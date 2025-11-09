package com.android.settingslib.activityembedding;

import android.app.Activity;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.core.os.BuildCompat;
import androidx.window.embedding.SplitController;
/* loaded from: classes2.dex */
public final class ActivityEmbeddingUtils {
    public static boolean isActivityEmbedded(Activity activity) {
        return SplitController.getInstance().isActivityEmbedded(activity);
    }

    public static boolean shouldHideNavigateUpButton(Activity activity, boolean z) {
        if (BuildCompat.isAtLeastT() && z) {
            String string = Settings.Global.getString(activity.getContentResolver(), "settings_hide_second_layer_page_navigate_up_button_in_two_pane");
            if (TextUtils.isEmpty(string) || Boolean.parseBoolean(string)) {
                return isActivityEmbedded(activity);
            }
            return false;
        }
        return false;
    }
}
