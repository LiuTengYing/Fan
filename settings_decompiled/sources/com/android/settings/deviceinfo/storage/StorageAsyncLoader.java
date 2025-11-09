package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class StorageAsyncLoader extends AsyncLoaderCompat<SparseArray<StorageResult>> {
    private PackageManager mPackageManager;
    private ArraySet<String> mSeenPackages;
    private StorageStatsSource mStatsManager;
    private UserManager mUserManager;
    private String mUuid;

    /* loaded from: classes.dex */
    public interface ResultHandler {
        void handleResult(SparseArray<StorageResult> sparseArray);
    }

    /* loaded from: classes.dex */
    public static class StorageResult {
        public long allAppsExceptGamesSize;
        public long audioSize;
        public long documentsAndOtherSize;
        public long duplicateCodeSize;
        public StorageStatsSource.ExternalStorageStats externalStats;
        public long gamesSize;
        public long imagesSize;
        public long trashSize;
        public long videosSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(SparseArray<StorageResult> sparseArray) {
    }

    public StorageAsyncLoader(Context context, UserManager userManager, String str, StorageStatsSource storageStatsSource, PackageManager packageManager) {
        super(context);
        this.mUserManager = userManager;
        this.mUuid = str;
        this.mStatsManager = storageStatsSource;
        this.mPackageManager = packageManager;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public SparseArray<StorageResult> loadInBackground() {
        return getStorageResultsForUsers();
    }

    private SparseArray<StorageResult> getStorageResultsForUsers() {
        this.mSeenPackages = new ArraySet<>();
        SparseArray<StorageResult> sparseArray = new SparseArray<>();
        List<UserInfo> users = this.mUserManager.getUsers();
        Collections.sort(users, new Comparator() { // from class: com.android.settings.deviceinfo.storage.StorageAsyncLoader$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$getStorageResultsForUsers$0;
                lambda$getStorageResultsForUsers$0 = StorageAsyncLoader.lambda$getStorageResultsForUsers$0((UserInfo) obj, (UserInfo) obj2);
                return lambda$getStorageResultsForUsers$0;
            }
        });
        for (UserInfo userInfo : users) {
            StorageResult appsAndGamesSize = getAppsAndGamesSize(userInfo.id);
            appsAndGamesSize.imagesSize = getFilesSize(userInfo.id, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
            appsAndGamesSize.videosSize = getFilesSize(userInfo.id, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null);
            appsAndGamesSize.audioSize = getFilesSize(userInfo.id, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null);
            Bundle bundle = new Bundle();
            bundle.putString("android:query-arg-sql-selection", "media_type!=1 AND media_type!=3 AND media_type!=2 AND mime_type IS NOT NULL");
            appsAndGamesSize.documentsAndOtherSize = getFilesSize(userInfo.id, MediaStore.Files.getContentUri("external"), bundle);
            Bundle bundle2 = new Bundle();
            bundle2.putInt("android:query-arg-match-trashed", 3);
            appsAndGamesSize.trashSize = getFilesSize(userInfo.id, MediaStore.Files.getContentUri("external"), bundle2);
            sparseArray.put(userInfo.id, appsAndGamesSize);
        }
        return sparseArray;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getStorageResultsForUsers$0(UserInfo userInfo, UserInfo userInfo2) {
        return Integer.compare(userInfo.id, userInfo2.id);
    }

    private long getFilesSize(int i, Uri uri, Bundle bundle) {
        try {
            Cursor query = getContext().createPackageContextAsUser(getContext().getApplicationContext().getPackageName(), 0, UserHandle.of(i)).getContentResolver().query(uri, new String[]{"sum(_size)"}, bundle, null);
            if (query == null) {
                if (query != null) {
                    query.close();
                }
                return 0L;
            }
            try {
                long j = query.moveToFirst() ? query.getLong(0) : 0L;
                query.close();
                return j;
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e("StorageAsyncLoader", "Not able to get Context for user ID " + i);
            return 0L;
        }
    }

    private StorageResult getAppsAndGamesSize(int i) {
        Log.d("StorageAsyncLoader", "Loading apps");
        List installedApplicationsAsUser = this.mPackageManager.getInstalledApplicationsAsUser(0, i);
        StorageResult storageResult = new StorageResult();
        UserHandle of = UserHandle.of(i);
        int size = installedApplicationsAsUser.size();
        for (int i2 = 0; i2 < size; i2++) {
            ApplicationInfo applicationInfo = (ApplicationInfo) installedApplicationsAsUser.get(i2);
            try {
                StorageStatsSource.AppStorageStats statsForPackage = this.mStatsManager.getStatsForPackage(this.mUuid, applicationInfo.packageName, of);
                long dataBytes = statsForPackage.getDataBytes();
                long cacheQuotaBytes = this.mStatsManager.getCacheQuotaBytes(this.mUuid, applicationInfo.uid);
                long cacheBytes = statsForPackage.getCacheBytes();
                long codeBytes = dataBytes + statsForPackage.getCodeBytes();
                if (cacheQuotaBytes < cacheBytes) {
                    codeBytes = (codeBytes - cacheBytes) + cacheQuotaBytes;
                }
                if (this.mSeenPackages.contains(applicationInfo.packageName)) {
                    storageResult.duplicateCodeSize += statsForPackage.getCodeBytes();
                } else {
                    this.mSeenPackages.add(applicationInfo.packageName);
                }
                int i3 = applicationInfo.category;
                if (i3 == 0) {
                    storageResult.gamesSize += codeBytes;
                } else if (i3 == 1 || i3 == 2 || i3 == 3) {
                    storageResult.allAppsExceptGamesSize += codeBytes;
                } else if ((applicationInfo.flags & 33554432) != 0) {
                    storageResult.gamesSize += codeBytes;
                } else {
                    storageResult.allAppsExceptGamesSize += codeBytes;
                }
            } catch (PackageManager.NameNotFoundException | IOException e) {
                Log.w("StorageAsyncLoader", "App unexpectedly not found", e);
            }
        }
        Log.d("StorageAsyncLoader", "Loading external stats");
        try {
            storageResult.externalStats = this.mStatsManager.getExternalStorageStats(this.mUuid, UserHandle.of(i));
        } catch (IOException e2) {
            Log.w("StorageAsyncLoader", e2);
        }
        Log.d("StorageAsyncLoader", "Obtaining result completed");
        return storageResult;
    }
}
