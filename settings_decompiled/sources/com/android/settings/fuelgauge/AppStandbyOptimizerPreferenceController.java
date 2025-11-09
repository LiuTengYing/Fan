package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
/* loaded from: classes.dex */
public class AppStandbyOptimizerPreferenceController extends BasePreferenceController {
    private static final String KEY_APP_BATTERY_SAVER = "app_battery_saver";
    private static final String TYPE_APP_CONFIG = "type_app_config";
    public static final int TYPE_APP_WAKEUP = 1000;
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
        return KEY_APP_BATTERY_SAVER;
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

    public AppStandbyOptimizerPreferenceController(Context context, String str) {
        super(context, str);
        this.isSupportUnisocPowerManager = 1 == SystemProperties.getInt("persist.sys.pwctl.enable", 1);
        this.mUserId = UserHandle.myUserId();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_APP_BATTERY_SAVER.equals(preference.getKey())) {
            Bundle bundle = new Bundle();
            bundle.putInt(TYPE_APP_CONFIG, TYPE_APP_WAKEUP);
            new SubSettingLauncher(this.mContext).setDestination(UnisocManageApplications.class.getName()).setArguments(bundle).setTitleRes(R$string.app_battery_saver_manager).setSourceMetricsCategory(getMetricsCategory()).launch();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.isSupportUnisocPowerManager && isAppStandbyOptimizerAvailable(this.mContext) && isUserSupported()) ? 0 : 3;
    }

    public static boolean isAppStandbyOptimizerAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_appStandbyOptimizer);
    }

    public boolean isUserSupported() {
        return this.mUserId == 0;
    }
}
