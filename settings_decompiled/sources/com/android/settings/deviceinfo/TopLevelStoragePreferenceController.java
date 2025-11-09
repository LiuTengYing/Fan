package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import com.android.settingslib.utils.ThreadUtils;
import java.text.NumberFormat;
import java.util.concurrent.Future;
/* loaded from: classes.dex */
public class TopLevelStoragePreferenceController extends BasePreferenceController {
    private final StorageManager mStorageManager;
    private final StorageManagerVolumeProvider mStorageManagerVolumeProvider;

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

    public TopLevelStoragePreferenceController(Context context, String str) {
        super(context, str);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        this.mStorageManagerVolumeProvider = new StorageManagerVolumeProvider(storageManager);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void refreshSummary(Preference preference) {
        if (preference == null) {
            return;
        }
        refreshSummaryThread(preference);
    }

    protected Future refreshSummaryThread(final Preference preference) {
        return ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.deviceinfo.TopLevelStoragePreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TopLevelStoragePreferenceController.this.lambda$refreshSummaryThread$1(preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshSummaryThread$1(final Preference preference) {
        final NumberFormat percentInstance = NumberFormat.getPercentInstance();
        final PrivateStorageInfo privateStorageInfo = PrivateStorageInfo.getPrivateStorageInfo(getStorageManagerVolumeProvider());
        final double d = privateStorageInfo.totalBytes - privateStorageInfo.freeBytes;
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.deviceinfo.TopLevelStoragePreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TopLevelStoragePreferenceController.this.lambda$refreshSummaryThread$0(preference, percentInstance, d, privateStorageInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshSummaryThread$0(Preference preference, NumberFormat numberFormat, double d, PrivateStorageInfo privateStorageInfo) {
        preference.setSummary(this.mContext.getString(R$string.storage_summary, numberFormat.format(d / privateStorageInfo.totalBytes), Formatter.formatFileSize(this.mContext, privateStorageInfo.freeBytes)));
    }

    protected StorageManagerVolumeProvider getStorageManagerVolumeProvider() {
        return this.mStorageManagerVolumeProvider;
    }
}
