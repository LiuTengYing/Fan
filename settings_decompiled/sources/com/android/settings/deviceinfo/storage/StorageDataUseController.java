package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.util.DataUnit;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RelativeLayout;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.SettingsApplication;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.StorageCacheHelper;
import com.android.settingslib.widget.LayoutPreference;
import java.util.Arrays;
/* loaded from: classes.dex */
public class StorageDataUseController extends BasePreferenceController {
    private static String KEY = "storage_data_ring_view";
    private static String TAG = "StorageDataUseController";
    private LayoutPreference layoutPreference;
    private Context mContext;
    private RelativeLayout mRootView;
    private StorageCacheHelper mStorageCacheHelper;
    private long mTotalSize;
    private long mUsedBytes;
    private int mUserId;
    private UserManager mUserManager;
    private float[] rates;
    private DataUsageRingView ringView;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public StorageDataUseController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        initViews();
        super.displayPreference(preferenceScreen);
    }

    private void initViews() {
        this.mRootView = (RelativeLayout) this.layoutPreference.findViewById(R$id.data_use_ring_view);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mStorageCacheHelper = new StorageCacheHelper(this.mContext, this.mUserId);
    }

    private void initData() {
        if (this.mStorageCacheHelper.hasCachedSizeInfo()) {
            StorageCacheHelper.StorageCache retrieveCachedSize = this.mStorageCacheHelper.retrieveCachedSize();
            setUsedSize(retrieveCachedSize.totalUsedSize);
            setTotalSize(retrieveCachedSize.totalSize);
        }
    }

    public void onLoadFinished(SparseArray<StorageAsyncLoader.StorageResult> sparseArray, int i) {
        StorageCacheHelper.StorageCache sizeInfo = getSizeInfo(sparseArray, i);
        long j = this.mTotalSize;
        if (j == 0) {
            return;
        }
        float[] fArr = {percent(sizeInfo.systemSize, j), percent(sizeInfo.allAppsExceptGamesSize, this.mTotalSize), percent(sizeInfo.trashSize, this.mTotalSize), percent(sizeInfo.documentsAndOtherSize, this.mTotalSize), percent(sizeInfo.gamesSize, this.mTotalSize), percent(sizeInfo.audioSize, this.mTotalSize), percent(sizeInfo.videosSize, this.mTotalSize), percent(sizeInfo.imagesSize, this.mTotalSize)};
        this.ringView = new DataUsageRingView(this.mContext);
        if (SettingsApplication.mResources.getDisplayMetrics().density == 120.0f) {
            this.ringView.setRadius(100);
        }
        String str = TAG;
        Log.d(str, "onLoadFinished: " + Arrays.toString(fArr));
        this.ringView.setData(fArr);
        this.mRootView.addView(this.ringView);
    }

    private void setRates(float[] fArr) {
        this.rates = fArr;
    }

    int getCurrentUserId() {
        return Utils.getCurrentUserId(this.mUserManager, true);
    }

    public void setUsedSize(long j) {
        this.mUsedBytes = j;
    }

    public void setTotalSize(long j) {
        this.mTotalSize = j;
    }

    private StorageCacheHelper.StorageCache getSizeInfo(SparseArray<StorageAsyncLoader.StorageResult> sparseArray, int i) {
        if (sparseArray == null) {
            return this.mStorageCacheHelper.retrieveCachedSize();
        }
        StorageAsyncLoader.StorageResult storageResult = sparseArray.get(i);
        StorageCacheHelper.StorageCache storageCache = new StorageCacheHelper.StorageCache();
        storageCache.imagesSize = storageResult.imagesSize;
        storageCache.videosSize = storageResult.videosSize;
        storageCache.audioSize = storageResult.audioSize;
        storageCache.allAppsExceptGamesSize = storageResult.allAppsExceptGamesSize;
        storageCache.gamesSize = storageResult.gamesSize;
        storageCache.documentsAndOtherSize = storageResult.documentsAndOtherSize;
        storageCache.trashSize = storageResult.trashSize;
        long j = 0;
        for (int i2 = 0; i2 < sparseArray.size(); i2++) {
            StorageAsyncLoader.StorageResult valueAt = sparseArray.valueAt(i2);
            j = (j + ((((((valueAt.gamesSize + valueAt.audioSize) + valueAt.videosSize) + valueAt.imagesSize) + valueAt.documentsAndOtherSize) + valueAt.trashSize) + valueAt.allAppsExceptGamesSize)) - valueAt.duplicateCodeSize;
        }
        storageCache.systemSize = Math.max(DataUnit.GIBIBYTES.toBytes(1L), this.mUsedBytes - j);
        return storageCache;
    }

    private float percent(long j, long j2) {
        if (j2 == 0) {
            return 0.0f;
        }
        return (float) ((j * 100) / j2);
    }
}
