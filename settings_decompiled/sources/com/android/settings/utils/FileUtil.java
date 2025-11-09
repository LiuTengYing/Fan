package com.android.settings.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
/* loaded from: classes.dex */
public class FileUtil {
    public static Resources getResource(Context context, String str) {
        return new Resources(createAssetManager(str), context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }

    private static AssetManager createAssetManager(String str) {
        try {
            AssetManager assetManager = (AssetManager) AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(assetManager, str);
            return assetManager;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }
}
