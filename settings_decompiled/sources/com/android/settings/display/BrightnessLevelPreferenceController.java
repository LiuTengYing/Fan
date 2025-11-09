package com.android.settings.display;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.display.BrightnessUtils;
import java.text.NumberFormat;
/* loaded from: classes.dex */
public class BrightnessLevelPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    private int GAMMA_SPACE_MAX;
    private ContentObserver mBrightnessObserver;
    private final ContentResolver mContentResolver;
    private final DisplayManager.DisplayListener mDisplayListener;
    private final DisplayManager mDisplayManager;
    private final Handler mHandler;
    private final float mMaxVrBrightness;
    private final float mMinVrBrightness;
    private Preference mPreference;
    private static final Uri BRIGHTNESS_FOR_VR_URI = Settings.System.getUriFor("screen_brightness_for_vr");
    private static final Uri BRIGHTNESS_ADJ_URI = Settings.System.getUriFor("screen_auto_brightness_adj");

    private double getPercentage(double d, int i, int i2) {
        if (d > i2) {
            return 1.0d;
        }
        double d2 = i;
        if (d < d2) {
            return 0.0d;
        }
        return (d - d2) / (i2 - i);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "brightness";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference("brightness");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatedSummary(preference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContentResolver.registerContentObserver(BRIGHTNESS_FOR_VR_URI, false, this.mBrightnessObserver);
        this.mContentResolver.registerContentObserver(BRIGHTNESS_ADJ_URI, false, this.mBrightnessObserver);
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler, 8L);
        updatedSummary(this.mPreference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContentResolver.unregisterContentObserver(this.mBrightnessObserver);
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            Intent intent = new Intent("com.android.intent.action.SHOW_BRIGHTNESS_DIALOG");
            intent.putExtra("page_transition_type", -1);
            this.mContext.startActivityForResult(preference.getKey(), intent, 0, ActivityOptions.makeCustomAnimation(this.mContext, 17432576, 17432577).toBundle());
            return true;
        }
        return false;
    }

    private void updatedSummary(Preference preference) {
        if (preference != null) {
            preference.setSummary(NumberFormat.getPercentInstance().format(getCurrentBrightness()));
            if (this.mContext.getDisplayId() != 0) {
                preference.setEnabled(false);
            }
        }
    }

    private double getCurrentBrightness() {
        int convertLinearToGammaFloat;
        if (isInVrMode()) {
            convertLinearToGammaFloat = BrightnessUtils.convertLinearToGammaFloat(Settings.System.getFloat(this.mContentResolver, "screen_brightness_for_vr_float", this.mMaxVrBrightness), this.mMinVrBrightness, this.mMaxVrBrightness);
        } else {
            BrightnessInfo brightnessInfo = this.mContext.getDisplay().getBrightnessInfo();
            convertLinearToGammaFloat = brightnessInfo != null ? BrightnessUtils.convertLinearToGammaFloat(brightnessInfo.brightness, brightnessInfo.brightnessMinimum, brightnessInfo.brightnessMaximum) : 0;
        }
        return getPercentage(convertLinearToGammaFloat, 0, this.GAMMA_SPACE_MAX);
    }

    IVrManager safeGetVrManager() {
        return IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    }

    boolean isInVrMode() {
        IVrManager safeGetVrManager = safeGetVrManager();
        if (safeGetVrManager != null) {
            try {
                return safeGetVrManager.getVrModeState();
            } catch (RemoteException e) {
                Log.e("BrightnessPrefCtrl", "Failed to check vr mode!", e);
                return false;
            }
        }
        return false;
    }
}
