package com.android.settings.wallpaper;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class StyleSuggestionActivity extends StyleSuggestionActivityBase {
    @VisibleForTesting
    public static boolean isSuggestionComplete(Context context) {
        return (StyleSuggestionActivityBase.isWallpaperServiceEnabled(context) && TextUtils.isEmpty(Settings.Secure.getStringForUser(context.getContentResolver(), "theme_customization_overlay_packages", context.getUserId()))) ? false : true;
    }
}
