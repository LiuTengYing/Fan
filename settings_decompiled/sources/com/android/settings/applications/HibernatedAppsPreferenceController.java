package com.android.settings.applications;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.permission.PermissionControllerManager;
import android.provider.DeviceConfig;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
/* loaded from: classes.dex */
public final class HibernatedAppsPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final String TAG = "HibernatedAppsPrefController";
    private boolean mLoadedUnusedCount;
    private boolean mLoadingUnusedApps;
    private final Executor mMainExecutor;
    private PreferenceScreen mScreen;
    private int mUnusedCount;

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

    public HibernatedAppsPreferenceController(Context context, String str) {
        this(context, str, context.getMainExecutor());
    }

    HibernatedAppsPreferenceController(Context context, String str, Executor executor) {
        super(context, str);
        this.mUnusedCount = 0;
        this.mMainExecutor = executor;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isHibernationEnabled() ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mLoadedUnusedCount) {
            Resources resources = this.mContext.getResources();
            int i = R$plurals.unused_apps_summary;
            int i2 = this.mUnusedCount;
            return resources.getQuantityString(i, i2, Integer.valueOf(i2));
        }
        return this.mContext.getResources().getString(R$string.summary_placeholder);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updatePreference();
    }

    private void updatePreference() {
        if (this.mScreen == null || this.mLoadingUnusedApps) {
            return;
        }
        ((PermissionControllerManager) this.mContext.getSystemService(PermissionControllerManager.class)).getUnusedAppCount(this.mMainExecutor, new IntConsumer() { // from class: com.android.settings.applications.HibernatedAppsPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.IntConsumer
            public final void accept(int i) {
                HibernatedAppsPreferenceController.this.lambda$updatePreference$0(i);
            }
        });
        this.mLoadingUnusedApps = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePreference$0(int i) {
        this.mUnusedCount = i;
        this.mLoadingUnusedApps = false;
        this.mLoadedUnusedCount = true;
        refreshSummary(this.mScreen.findPreference(this.mPreferenceKey));
    }

    private static boolean isHibernationEnabled() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_enabled", true);
    }
}
