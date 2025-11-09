package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Switch;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
/* loaded from: classes.dex */
public class BatterySaverButtonPreferenceController extends TogglePreferenceController implements OnMainSwitchChangeListener, LifecycleObserver, OnStart, OnStop, BatterySaverReceiver.BatterySaverListener {
    private final BatterySaverReceiver mBatterySaverReceiver;
    private Handler mHandler;
    private final PowerManager mPowerManager;
    private MainSwitchPreference mPreference;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onUltraPowerSaveModeChanged(boolean z) {
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BatterySaverButtonPreferenceController(Context context, String str) {
        super(context, str);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mBatterySaverReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public Uri getSliceUri() {
        return new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("battery_saver").build();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mBatterySaverReceiver.setListening(true);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mBatterySaverReceiver.setListening(false);
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = mainSwitchPreference;
        mainSwitchPreference.addOnSwitchChangeListener(this);
        this.mPreference.updateStatus(isChecked());
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r4, final boolean z) {
        if (z && Settings.Secure.getInt(this.mContext.getContentResolver(), "low_power_warning_acknowledged", 0) == 0) {
            this.mPreference.setChecked(false);
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.settings.fuelgauge.batterysaver.BatterySaverButtonPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatterySaverButtonPreferenceController.this.lambda$onSwitchChanged$0(z);
            }
        }, 200L);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mPowerManager.isPowerSaveMode();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    /* renamed from: setChecked */
    public boolean lambda$onSwitchChanged$0(boolean z) {
        return BatterySaverUtils.setPowerSaveMode(this.mContext, z, true);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_battery;
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onPowerSaveModeChanged() {
        onPowerSaveModeChangedInternal();
    }

    private void onPowerSaveModeChangedInternal() {
        boolean isChecked = isChecked();
        MainSwitchPreference mainSwitchPreference = this.mPreference;
        if (mainSwitchPreference == null || mainSwitchPreference.isChecked() == isChecked) {
            return;
        }
        this.mPreference.setChecked(isChecked);
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onBatteryChanged(boolean z) {
        MainSwitchPreference mainSwitchPreference = this.mPreference;
        if (mainSwitchPreference != null) {
            mainSwitchPreference.setEnabled(!z);
        }
    }
}
