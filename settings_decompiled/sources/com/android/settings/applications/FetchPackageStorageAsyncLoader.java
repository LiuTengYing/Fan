package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.io.IOException;
/* loaded from: classes.dex */
public class FetchPackageStorageAsyncLoader extends AsyncLoaderCompat<StorageStatsSource.AppStorageStats> {
    private final ApplicationInfo mInfo;
    private final StorageStatsSource mSource;
    private final UserHandle mUser;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(StorageStatsSource.AppStorageStats appStorageStats) {
    }

    public FetchPackageStorageAsyncLoader(Context context, StorageStatsSource storageStatsSource, ApplicationInfo applicationInfo, UserHandle userHandle) {
        super(context);
        this.mSource = (StorageStatsSource) Preconditions.checkNotNull(storageStatsSource);
        this.mInfo = applicationInfo;
        this.mUser = userHandle;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public StorageStatsSource.AppStorageStats loadInBackground() {
        try {
            StorageStatsSource storageStatsSource = this.mSource;
            ApplicationInfo applicationInfo = this.mInfo;
            return storageStatsSource.getStatsForPackage(applicationInfo.volumeUuid, applicationInfo.packageName, this.mUser);
        } catch (PackageManager.NameNotFoundException | IOException e) {
            Log.w("FetchPackageStorage", "Package may have been removed during query, failing gracefully", e);
            return null;
        }
    }
}
