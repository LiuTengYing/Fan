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
public class BacklightSavingPowerPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final int SWITCH_OFF = 0;
    private static final int SWITCH_ON = 1;
    private final String LUMINOUS_PROTECTING_EYE;
    private final boolean isSupportBacklightSavingPower;
    private final ContentResolver mContentResolver;
    private final boolean mIsSupportLLD;
    private boolean mIsSupportLuminousProtectingEye;
    private ContentObserver mLuminousProtectingEyeObserver;
    public SwitchPreference mPreference;

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

    public BacklightSavingPowerPreferenceController(Context context, String str) {
        super(context, str);
        boolean z = false;
        this.isSupportBacklightSavingPower = 1 == SystemProperties.getInt("persist.sys.pq.cabc.enabled", 0);
        this.mIsSupportLuminousProtectingEye = 1 == SystemProperties.getInt("persist.sys.pq.lld.enabled", 0);
        this.LUMINOUS_PROTECTING_EYE = "luminous_protecting_eye";
        this.mContentResolver = this.mContext.getContentResolver();
        this.mLuminousProtectingEyeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.display.BacklightSavingPowerPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                BacklightSavingPowerPreferenceController backlightSavingPowerPreferenceController = BacklightSavingPowerPreferenceController.this;
                backlightSavingPowerPreferenceController.updateState(backlightSavingPowerPreferenceController.mPreference);
            }
        };
        if (this.mIsSupportLuminousProtectingEye && this.mContext.getResources().getBoolean(R$bool.config_luminous_protecting_eye_available)) {
            z = true;
        }
        this.mIsSupportLLD = z;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(R$bool.config_backlight_saving_power_setting_available) && this.isSupportBacklightSavingPower) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "backlight_saving_power", 0);
        int i2 = Settings.Global.getInt(this.mContext.getContentResolver(), "luminous_protecting_eye", 0);
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        this.mPreference = switchPreference;
        switchPreference.setChecked(i == 1);
        this.mPreference.setEnabled(i2 != 1);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), "backlight_saving_power", booleanValue ? 1 : 0);
        if (booleanValue && this.mIsSupportLLD) {
            Toast makeText = Toast.makeText(this.mContext, R$string.cabc_enabled_lld_disabled, 0);
            ToastManager.setToast(makeText);
            makeText.show();
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("luminous_protecting_eye"), false, this.mLuminousProtectingEyeObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContentResolver.unregisterContentObserver(this.mLuminousProtectingEyeObserver);
    }
}
