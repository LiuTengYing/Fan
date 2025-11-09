package com.android.settings.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.sprdpower.IPowerManagerEx;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
/* loaded from: classes.dex */
public class BatterySaverController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, BatterySaverReceiver.BatterySaverListener {
    private static final String KEY_BATTERY_SAVER = "battery_saver_summary";
    private Preference mBatterySaverPref;
    private final BatterySaverReceiver mBatteryStateChangeReceiver;
    private final ContentObserver mObserver;
    private final PowerManager mPowerManager;
    private IPowerManagerEx mPowerManagerEx;
    private boolean mState;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_BATTERY_SAVER;
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

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onBatteryChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BatterySaverController(Context context) {
        super(context, KEY_BATTERY_SAVER);
        this.mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.fuelgauge.BatterySaverController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                BatterySaverController.this.updateSummary();
            }
        };
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mBatteryStateChangeReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
        BatterySaverUtils.revertScheduleToNoneIfNeeded(context);
        this.mPowerManagerEx = IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex"));
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference preference;
        super.displayPreference(preferenceScreen);
        this.mBatterySaverPref = preferenceScreen.findPreference(KEY_BATTERY_SAVER);
        try {
            this.mState = this.mPowerManagerEx.isUltraPowerSaveMode();
        } catch (RemoteException unused) {
        }
        if (!this.mState || (preference = this.mBatterySaverPref) == null) {
            return;
        }
        preference.setEnabled(false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        try {
            this.mState = this.mPowerManagerEx.isUltraPowerSaveMode();
        } catch (RemoteException unused) {
        }
        Preference preference2 = this.mBatterySaverPref;
        if (preference2 != null) {
            preference2.setEnabled(!this.mState);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), true, this.mObserver);
        this.mBatteryStateChangeReceiver.setListening(true);
        updateSummary();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
        this.mBatteryStateChangeReceiver.setListening(false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mPowerManager.isPowerSaveMode()) {
            return this.mContext.getString(R$string.battery_saver_on_summary);
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (Settings.Global.getInt(contentResolver, "automatic_power_save_mode", 0) == 0) {
            int i = Settings.Global.getInt(contentResolver, "low_power_trigger_level", 0);
            if (i != 0) {
                return this.mContext.getString(R$string.battery_saver_off_scheduled_summary, Utils.formatPercentage(i));
            }
            return this.mContext.getString(R$string.battery_saver_off_summary);
        }
        return this.mContext.getString(R$string.battery_saver_pref_auto_routine_summary);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSummary() {
        Preference preference = this.mBatterySaverPref;
        if (preference != null) {
            preference.setSummary(getSummary());
        }
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onPowerSaveModeChanged() {
        updateSummary();
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onUltraPowerSaveModeChanged(boolean z) {
        Preference preference = this.mBatterySaverPref;
        if (preference != null) {
            preference.setEnabled(!z);
        }
    }
}
