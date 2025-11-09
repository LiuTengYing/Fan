package com.android.settingslib.applications;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;
import android.util.LruCache;
/* loaded from: classes2.dex */
public class AppIconCacheManager {
    private static final int MAX_CACHE_SIZE_IN_KB = getMaxCacheInKb();
    private static AppIconCacheManager sAppIconCacheManager;
    private final LruCache<String, Drawable> mDrawableCache = new LruCache<String, Drawable>(MAX_CACHE_SIZE_IN_KB) { // from class: com.android.settingslib.applications.AppIconCacheManager.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public int sizeOf(String str, Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap().getByteCount() / 1024;
            }
            return ((drawable.getIntrinsicHeight() * drawable.getIntrinsicWidth()) * 4) / 1024;
        }
    };

    private AppIconCacheManager() {
    }

    public static synchronized AppIconCacheManager getInstance() {
        AppIconCacheManager appIconCacheManager;
        synchronized (AppIconCacheManager.class) {
            if (sAppIconCacheManager == null) {
                sAppIconCacheManager = new AppIconCacheManager();
            }
            appIconCacheManager = sAppIconCacheManager;
        }
        return appIconCacheManager;
    }

    public void put(String str, int i, Drawable drawable) {
        String key = getKey(str, i);
        if (key == null || drawable == null || drawable.getIntrinsicHeight() < 0 || drawable.getIntrinsicWidth() < 0) {
            Log.w("AppIconCacheManager", "Invalid key or drawable.");
        } else {
            this.mDrawableCache.put(key, drawable);
        }
    }

    public Drawable get(String str, int i) {
        String key = getKey(str, i);
        if (key == null) {
            Log.w("AppIconCacheManager", "Invalid key with package or uid.");
            return null;
        }
        Drawable drawable = this.mDrawableCache.get(key);
        if (drawable != null) {
            return drawable.mutate();
        }
        return null;
    }

    public static void release() {
        AppIconCacheManager appIconCacheManager = sAppIconCacheManager;
        if (appIconCacheManager != null) {
            appIconCacheManager.mDrawableCache.evictAll();
        }
    }

    private static String getKey(String str, int i) {
        if (str == null || i < 0) {
            return null;
        }
        return str + ":" + UserHandle.getUserId(i);
    }

    private static int getMaxCacheInKb() {
        return Math.round((((float) Runtime.getRuntime().maxMemory()) * 0.1f) / 1024.0f);
    }
}
