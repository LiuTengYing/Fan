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
public class LockScreenBatterySaverPreferenceController extends BasePreferenceController {
    private static final String KEY_LOCK_SCREEN_BATTERY_SAVE = "lock_screen_battery_save";
    private static final int TYPE_APP_CLOSE_LOCKED = 1005;
    private static final String TYPE_APP_CONFIG = "type_app_config";
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
        return KEY_LOCK_SCREEN_BATTERY_SAVE;
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

    public LockScreenBatterySaverPreferenceController(Context context, String str) {
        super(context, str);
        this.isSupportUnisocPowerManager = 1 == SystemProperties.getInt("persist.sys.pwctl.enable", 1);
        this.mUserId = UserHandle.myUserId();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_LOCK_SCREEN_BATTERY_SAVE.equals(preference.getKey())) {
            new Bundle().putInt(TYPE_APP_CONFIG, TYPE_APP_CLOSE_LOCKED);
            new SubSettingLauncher(this.mContext).setDestination(UnisocManageApplications.class.getName()).setTitleRes(R$string.lock_screen_battery_save).setSourceMetricsCategory(getMetricsCategory()).launch();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.isSupportUnisocPowerManager && isLockScreenBatterySaverAvailable(this.mContext) && isUserSupported()) ? 0 : 3;
    }

    public static boolean isLockScreenBatterySaverAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_lockScreenBatterySaver);
    }

    public boolean isUserSupported() {
        return this.mUserId == 0;
    }
}
