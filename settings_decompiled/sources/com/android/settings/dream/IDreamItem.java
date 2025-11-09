package com.android.settings.dream;

import android.graphics.drawable.Drawable;
/* loaded from: classes.dex */
public interface IDreamItem {
    default boolean allowCustomization() {
        return false;
    }

    Drawable getIcon();

    Drawable getPreviewImage();

    CharSequence getSummary();

    CharSequence getTitle();

    boolean isActive();

    default void onCustomizeClicked() {
    }

    void onItemClicked();
}
