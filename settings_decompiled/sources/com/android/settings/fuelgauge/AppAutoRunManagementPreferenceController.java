package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.os.UserHandle;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class AppAutoRunManagementPreferenceController extends BasePreferenceController {
    private static final String KEY_APP_AUTO_RUN = "app_auto_run";
    private final boolean isSupportUnisocPowerManager;
    private final int mUserId;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getMetricsCategory() {
        return 54;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_APP_AUTO_RUN;
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

    public AppAutoRunManagementPreferenceController(Context context, String str) {
        super(context, str);
        this.isSupportUnisocPowerManager = 1 == SystemProperties.getInt("persist.sys.pwctl.enable", 1);
        this.mUserId = UserHandle.myUserId();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.isSupportUnisocPowerManager && isAppAutoRunManagementAvailable(this.mContext) && isUserSupported()) ? 0 : 3;
    }

    public static boolean isAppAutoRunManagementAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_appAutoRunManagement);
    }

    public boolean isUserSupported() {
        return this.mUserId == 0;
    }
}
