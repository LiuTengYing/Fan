package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class PeakRefreshRatePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    static float DEFAULT_REFRESH_RATE = 60.0f;
    private static final float INVALIDATE_REFRESH_RATE = -1.0f;
    private static final String TAG = "RefreshRatePrefCtr";
    private final DeviceConfigDisplaySettings mDeviceConfigDisplaySettings;
    private final Handler mHandler;
    private final IDeviceConfigChange mOnDeviceConfigChange;
    float mPeakRefreshRate;
    private Preference mPreference;

    /* loaded from: classes.dex */
    private interface IDeviceConfigChange {
        void onDefaultRefreshRateChanged();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public PeakRefreshRatePreferenceController(Context context, String str) {
        super(context, str);
        this.mHandler = new Handler(context.getMainLooper());
        this.mDeviceConfigDisplaySettings = new DeviceConfigDisplaySettings();
        this.mOnDeviceConfigChange = new IDeviceConfigChange() { // from class: com.android.settings.display.PeakRefreshRatePreferenceController.1
            @Override // com.android.settings.display.PeakRefreshRatePreferenceController.IDeviceConfigChange
            public void onDefaultRefreshRateChanged() {
                PeakRefreshRatePreferenceController peakRefreshRatePreferenceController = PeakRefreshRatePreferenceController.this;
                peakRefreshRatePreferenceController.updateState(peakRefreshRatePreferenceController.mPreference);
            }
        };
        Display display = ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(0);
        if (display == null) {
            Log.w(TAG, "No valid default display device");
            this.mPeakRefreshRate = DEFAULT_REFRESH_RATE;
        } else {
            this.mPeakRefreshRate = findPeakRefreshRate(display.getSupportedModes());
        }
        Log.d(TAG, "DEFAULT_REFRESH_RATE : " + DEFAULT_REFRESH_RATE + " mPeakRefreshRate : " + this.mPeakRefreshRate);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(R$bool.config_support_variable_refresh_rate) || !this.mContext.getResources().getBoolean(R$bool.config_show_smooth_display) || this.mPeakRefreshRate <= DEFAULT_REFRESH_RATE) ? 3 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Math.round(Settings.System.getFloat(this.mContext.getContentResolver(), "peak_refresh_rate", getDefaultPeakRefreshRate())) == Math.round(this.mPeakRefreshRate);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        float f = z ? this.mPeakRefreshRate : DEFAULT_REFRESH_RATE;
        Log.d(TAG, "setChecked to : " + f);
        return Settings.System.putFloat(this.mContext.getContentResolver(), "peak_refresh_rate", f);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mDeviceConfigDisplaySettings.startListening();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mDeviceConfigDisplaySettings.stopListening();
    }

    float findPeakRefreshRate(Display.Mode[] modeArr) {
        float f = DEFAULT_REFRESH_RATE;
        for (Display.Mode mode : modeArr) {
            if (Math.round(mode.getRefreshRate()) > f) {
                f = mode.getRefreshRate();
            }
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DeviceConfigDisplaySettings implements DeviceConfig.OnPropertiesChangedListener, Executor {
        private DeviceConfigDisplaySettings() {
        }

        public void startListening() {
            DeviceConfig.addOnPropertiesChangedListener("display_manager", this, this);
        }

        public void stopListening() {
            DeviceConfig.removeOnPropertiesChangedListener(this);
        }

        public float getDefaultPeakRefreshRate() {
            float f = DeviceConfig.getFloat("display_manager", "peak_refresh_rate_default", (float) PeakRefreshRatePreferenceController.INVALIDATE_REFRESH_RATE);
            Log.d(PeakRefreshRatePreferenceController.TAG, "DeviceConfig getDefaultPeakRefreshRate : " + f);
            return f;
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            if (PeakRefreshRatePreferenceController.this.mOnDeviceConfigChange != null) {
                PeakRefreshRatePreferenceController.this.mOnDeviceConfigChange.onDefaultRefreshRateChanged();
                PeakRefreshRatePreferenceController peakRefreshRatePreferenceController = PeakRefreshRatePreferenceController.this;
                peakRefreshRatePreferenceController.updateState(peakRefreshRatePreferenceController.mPreference);
            }
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable runnable) {
            if (PeakRefreshRatePreferenceController.this.mHandler != null) {
                PeakRefreshRatePreferenceController.this.mHandler.post(runnable);
            }
        }
    }

    private float getDefaultPeakRefreshRate() {
        float defaultPeakRefreshRate = this.mDeviceConfigDisplaySettings.getDefaultPeakRefreshRate();
        if (defaultPeakRefreshRate == INVALIDATE_REFRESH_RATE) {
            defaultPeakRefreshRate = this.mContext.getResources().getInteger(17694794);
        }
        Log.d(TAG, "DeviceConfig getDefaultPeakRefreshRate : " + defaultPeakRefreshRate);
        return defaultPeakRefreshRate;
    }
}
