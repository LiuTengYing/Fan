package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.SharedPreferences;
/* loaded from: classes.dex */
public class StorageCacheHelper {
    private final SharedPreferences mSharedPreferences;

    /* loaded from: classes.dex */
    public static class StorageCache {
        public long allAppsExceptGamesSize;
        public long audioSize;
        public long documentsAndOtherSize;
        public long gamesSize;
        public long imagesSize;
        public long systemSize;
        public long totalSize;
        public long totalUsedSize;
        public long trashSize;
        public long videosSize;
    }

    public StorageCacheHelper(Context context, int i) {
        this.mSharedPreferences = context.getSharedPreferences("StorageCache" + i, 0);
    }

    public boolean hasCachedSizeInfo() {
        return this.mSharedPreferences.getAll().size() > 0;
    }

    public void cacheSizeInfo(StorageCache storageCache) {
        this.mSharedPreferences.edit().putLong("images_size_key", storageCache.imagesSize).putLong("videos_size_key", storageCache.videosSize).putLong("audio_size_key", storageCache.audioSize).putLong("apps_size_key", storageCache.allAppsExceptGamesSize).putLong("games_size_key", storageCache.gamesSize).putLong("documents_and_other_size_key", storageCache.documentsAndOtherSize).putLong("trash_size_key", storageCache.trashSize).putLong("system_size_key", storageCache.systemSize).apply();
    }

    public void cacheTotalSizeAndTotalUsedSize(long j, long j2) {
        this.mSharedPreferences.edit().putLong("total_size_key", j).putLong("total_used_size_key", j2).apply();
    }

    public void cacheUsedSize(long j) {
        this.mSharedPreferences.edit().putLong("used_size_key", j).apply();
    }

    public long retrieveUsedSize() {
        return this.mSharedPreferences.getLong("used_size_key", 0L);
    }

    public StorageCache retrieveCachedSize() {
        StorageCache storageCache = new StorageCache();
        storageCache.totalSize = this.mSharedPreferences.getLong("total_size_key", 0L);
        storageCache.totalUsedSize = this.mSharedPreferences.getLong("total_used_size_key", 0L);
        storageCache.imagesSize = this.mSharedPreferences.getLong("images_size_key", 0L);
        storageCache.videosSize = this.mSharedPreferences.getLong("videos_size_key", 0L);
        storageCache.audioSize = this.mSharedPreferences.getLong("audio_size_key", 0L);
        storageCache.allAppsExceptGamesSize = this.mSharedPreferences.getLong("apps_size_key", 0L);
        storageCache.gamesSize = this.mSharedPreferences.getLong("games_size_key", 0L);
        storageCache.documentsAndOtherSize = this.mSharedPreferences.getLong("documents_and_other_size_key", 0L);
        storageCache.trashSize = this.mSharedPreferences.getLong("trash_size_key", 0L);
        storageCache.systemSize = this.mSharedPreferences.getLong("system_size_key", 0L);
        return storageCache;
    }
}
