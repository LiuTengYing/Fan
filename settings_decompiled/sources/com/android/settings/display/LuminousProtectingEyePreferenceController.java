package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.provider.Settings;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.unisoc.settings.utils.ToastManager;
/* loaded from: classes.dex */
public class LuminousProtectingEyePreferenceController extends BasePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final String BACKLIGHT_SAVING_POWER = "backlight_saving_power";
    private static final String LUMINOUS_PROTECTING_EYE = "luminous_protecting_eye";
    private static final int SWITCH_OFF = 0;
    private static final int SWITCH_ON = 1;
    private ContentObserver mBacklightSavingPowerObserver;
    private ContentResolver mContentResolver;
    private boolean mIsSupportBacklightSavingPower;
    private final boolean mIsSupportCABC;
    private boolean mIsSupportLuminousProtectingEye;
    private SwitchPreference mPreference;

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

    public LuminousProtectingEyePreferenceController(Context context, String str) {
        super(context, str);
        boolean z = false;
        this.mIsSupportBacklightSavingPower = 1 == SystemProperties.getInt("persist.sys.pq.cabc.enabled", 0);
        this.mIsSupportLuminousProtectingEye = 1 == SystemProperties.getInt("persist.sys.pq.lld.enabled", 0);
        this.mContentResolver = this.mContext.getContentResolver();
        this.mBacklightSavingPowerObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.display.LuminousProtectingEyePreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                LuminousProtectingEyePreferenceController luminousProtectingEyePreferenceController = LuminousProtectingEyePreferenceController.this;
                luminousProtectingEyePreferenceController.updateState(luminousProtectingEyePreferenceController.mPreference);
            }
        };
        if (this.mIsSupportBacklightSavingPower && this.mContext.getResources().getBoolean(R$bool.config_backlight_saving_power_setting_available)) {
            z = true;
        }
        this.mIsSupportCABC = z;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(R$bool.config_luminous_protecting_eye_available) && this.mIsSupportLuminousProtectingEye) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), LUMINOUS_PROTECTING_EYE, 0);
        int i2 = Settings.Global.getInt(this.mContext.getContentResolver(), BACKLIGHT_SAVING_POWER, 0);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        this.mPreference = switchPreference;
        if (i2 == 1) {
            switchPreference.setEnabled(false);
            if (i == 1) {
                Settings.Global.putInt(this.mContext.getContentResolver(), LUMINOUS_PROTECTING_EYE, 0);
                i = 0;
            }
        } else {
            switchPreference.setEnabled(true);
        }
        this.mPreference.setChecked(i == 1);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), LUMINOUS_PROTECTING_EYE, booleanValue ? 1 : 0);
        if (booleanValue && this.mIsSupportCABC) {
            Toast makeText = Toast.makeText(this.mContext, R$string.lld_enabled_cabc_disabled, 0);
            ToastManager.setToast(makeText);
            makeText.show();
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor(BACKLIGHT_SAVING_POWER), false, this.mBacklightSavingPowerObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContentResolver.unregisterContentObserver(this.mBacklightSavingPowerObserver);
    }
}
