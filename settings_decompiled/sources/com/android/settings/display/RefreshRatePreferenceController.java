package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class RefreshRatePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    public static float DEFAULT_REFRESH_RATE = 60.0f;
    private static final String KEY_REFRESH_RATE = "refresh_rate";
    private static final String TAG = "RefreshRatePreferenceController";
    public float mPeakRefreshRate;
    private Preference mPreference;
    private ContentObserver mRefreshRateObserver;

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

    public RefreshRatePreferenceController(Context context, String str) {
        super(context, str);
        this.mRefreshRateObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.display.RefreshRatePreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (RefreshRatePreferenceController.this.mPreference != null) {
                    RefreshRatePreferenceController refreshRatePreferenceController = RefreshRatePreferenceController.this;
                    refreshRatePreferenceController.updateState(refreshRatePreferenceController.mPreference);
                }
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

    private float findPeakRefreshRate(Display.Mode[] modeArr) {
        float f = DEFAULT_REFRESH_RATE;
        for (Display.Mode mode : modeArr) {
            if (Math.round(mode.getRefreshRate()) > f) {
                f = mode.getRefreshRate();
            }
        }
        return f;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(KEY_REFRESH_RATE);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mPeakRefreshRate <= DEFAULT_REFRESH_RATE || !this.mContext.getResources().getBoolean(R$bool.config_support_variable_refresh_rate)) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uriFor = Settings.System.getUriFor("min_refresh_rate");
        Uri uriFor2 = Settings.System.getUriFor("peak_refresh_rate");
        contentResolver.registerContentObserver(uriFor, false, this.mRefreshRateObserver);
        contentResolver.registerContentObserver(uriFor2, false, this.mRefreshRateObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mRefreshRateObserver);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String string;
        if (ScreenRefreshRatePreferenceFragment.isForcePeakRefreshRateEnabled(this.mContext)) {
            string = this.mContext.getString(R$string.high_refresh_rate_title);
        } else if (ScreenRefreshRatePreferenceFragment.isForceDefaultRefreshRateEnabled(this.mContext)) {
            string = this.mContext.getString(R$string.low_refresh_rate_title);
        } else if (ScreenRefreshRatePreferenceFragment.isForceNewPeakRefreshRateEnabled(this.mContext)) {
            string = this.mContext.getString(R$string.super_refresh_rate_title);
        } else {
            string = this.mContext.getString(R$string.auto_refresh_rate_title);
        }
        if (preference != null) {
            preference.setSummary(string);
        }
    }
}
