package com.android.settings.deviceinfo.storage;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.deviceinfo.storage.StorageCacheHelper;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.UsageProgressBarPreference;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public class StorageUsageProgressBarPreferenceController extends BasePreferenceController {
    private static final String TAG = "StorageProgressCtrl";
    boolean mIsUpdateStateFromSelectedStorageEntry;
    private StorageCacheHelper mStorageCacheHelper;
    private StorageEntry mStorageEntry;
    private final StorageStatsManager mStorageStatsManager;
    long mTotalBytes;
    private UsageProgressBarPreference mUsageProgressBarPreference;
    long mUsedBytes;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public StorageUsageProgressBarPreferenceController(Context context, String str) {
        super(context, str);
        this.mStorageStatsManager = (StorageStatsManager) context.getSystemService(StorageStatsManager.class);
        this.mStorageCacheHelper = new StorageCacheHelper(context, UserHandle.myUserId());
    }

    public void setSelectedStorageEntry(StorageEntry storageEntry) {
        this.mStorageEntry = storageEntry;
        getStorageStatsAndUpdateUi();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mUsageProgressBarPreference = (UsageProgressBarPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    private void getStorageStatsAndUpdateUi() {
        StorageEntry storageEntry = this.mStorageEntry;
        if (storageEntry != null && storageEntry.isMounted() && this.mStorageEntry.isPrivate()) {
            StorageCacheHelper.StorageCache retrieveCachedSize = this.mStorageCacheHelper.retrieveCachedSize();
            this.mTotalBytes = retrieveCachedSize.totalSize;
            this.mUsedBytes = retrieveCachedSize.totalUsedSize;
            this.mIsUpdateStateFromSelectedStorageEntry = true;
            updateState(this.mUsageProgressBarPreference);
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.deviceinfo.storage.StorageUsageProgressBarPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                StorageUsageProgressBarPreferenceController.this.lambda$getStorageStatsAndUpdateUi$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getStorageStatsAndUpdateUi$1() {
        StorageEntry storageEntry;
        try {
            storageEntry = this.mStorageEntry;
        } catch (IOException unused) {
            this.mTotalBytes = 0L;
            this.mUsedBytes = 0L;
        }
        if (storageEntry == null || !storageEntry.isMounted()) {
            throw new IOException();
        }
        if (this.mStorageEntry.isPrivate()) {
            long totalBytes = this.mStorageStatsManager.getTotalBytes(this.mStorageEntry.getFsUuid());
            this.mTotalBytes = totalBytes;
            this.mUsedBytes = totalBytes - this.mStorageStatsManager.getFreeBytes(this.mStorageEntry.getFsUuid());
        } else {
            File path = this.mStorageEntry.getPath();
            if (path == null) {
                Log.d(TAG, "Mounted public storage has null root path: " + this.mStorageEntry);
                throw new IOException();
            }
            long totalSpace = path.getTotalSpace();
            this.mTotalBytes = totalSpace;
            this.mUsedBytes = totalSpace - path.getFreeSpace();
        }
        if (this.mUsageProgressBarPreference == null) {
            return;
        }
        this.mIsUpdateStateFromSelectedStorageEntry = true;
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.deviceinfo.storage.StorageUsageProgressBarPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                StorageUsageProgressBarPreferenceController.this.lambda$getStorageStatsAndUpdateUi$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getStorageStatsAndUpdateUi$0() {
        updateState(this.mUsageProgressBarPreference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mIsUpdateStateFromSelectedStorageEntry) {
            this.mIsUpdateStateFromSelectedStorageEntry = false;
            this.mUsageProgressBarPreference.setUsageSummary(StorageUtils.getStorageSummary(this.mContext, R$string.storage_usage_summary, this.mUsedBytes));
            this.mUsageProgressBarPreference.setTotalSummary(StorageUtils.getStorageSummary(this.mContext, R$string.storage_total_summary, this.mTotalBytes));
            this.mUsageProgressBarPreference.setPercent(this.mUsedBytes, this.mTotalBytes);
        }
    }
}
