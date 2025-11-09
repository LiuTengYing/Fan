package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import com.android.settings.R$string;
import com.android.settings.widget.SettingsMainSwitchPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class VibrationMainSwitchPreferenceController extends SettingsMainSwitchPreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final ContentObserver mSettingObserver;
    private final Vibrator mVibrator;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.widget.SettingsMainSwitchPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VibrationMainSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mVibrator = (Vibrator) context.getSystemService(Vibrator.class);
        this.mSettingObserver = new ContentObserver(new Handler(true)) { // from class: com.android.settings.accessibility.VibrationMainSwitchPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                VibrationMainSwitchPreferenceController vibrationMainSwitchPreferenceController = VibrationMainSwitchPreferenceController.this;
                vibrationMainSwitchPreferenceController.updateState(((SettingsMainSwitchPreferenceController) vibrationMainSwitchPreferenceController).mSwitchPreference);
            }
        };
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("vibrate_on"), false, this.mSettingObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingObserver);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return VibrationPreferenceConfig.isMainVibrationSwitchEnabled(this.mContext.getContentResolver());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        boolean putInt = Settings.System.putInt(this.mContext.getContentResolver(), "vibrate_on", z ? 1 : 0);
        if (putInt && z) {
            VibrationPreferenceConfig.playVibrationPreview(this.mVibrator, 18);
        }
        return putInt;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }
}
