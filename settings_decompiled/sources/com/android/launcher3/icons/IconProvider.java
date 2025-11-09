package com.android.launcher3.icons;

import android.content.res.Resources;
import androidx.core.os.BuildCompat;
import com.android.settingslib.applications.RecentAppOpsAccess;
/* loaded from: classes.dex */
public class IconProvider {
    private static final int CONFIG_ICON_MASK_RES_ID = Resources.getSystem().getIdentifier("config_icon_mask", "string", RecentAppOpsAccess.ANDROID_SYSTEM_PACKAGE_NAME);
    public static final boolean ATLEAST_T = BuildCompat.isAtLeastT();
}
