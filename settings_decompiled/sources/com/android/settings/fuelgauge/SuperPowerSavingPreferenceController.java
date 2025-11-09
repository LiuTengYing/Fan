package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.sprdpower.IPowerManagerEx;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class SuperPowerSavingPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final String KEY_SUPER_POWER_SAVING_MODE = "super_power_saving_mode";
    public static final int MODE_PERFORMANCE = 0;
    private static final int MODE_ULTRASAVING = 4;
    private static final String TAG = "SuperPowerSavingPreferenceController";
    private final boolean isSupportSuperPowerSavingMode;
    private final boolean isSupportUnisocPowerController;
    private String lockTaskKey;
    private final ContentResolver mContentResolver;
    private Context mContext;
    private boolean mInUltraPowerSaveMode;
    private ContentObserver mObserver;
    private boolean mPluggedIn;
    private IPowerManagerEx mPowerManagerEx;
    private BroadcastReceiver mReceiver;
    public SwitchPreference mSwitchPreference;
    private final int mUserId;
    private final Uri uri;

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
        return KEY_SUPER_POWER_SAVING_MODE;
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

    public SuperPowerSavingPreferenceController(Context context, String str) {
        super(context, str);
        this.isSupportSuperPowerSavingMode = SystemProperties.getBoolean("ro.sys.pwctl.ultrasaving", false);
        this.isSupportUnisocPowerController = 1 == SystemProperties.getInt("persist.sys.pwctl.enable", 1);
        this.mPluggedIn = false;
        this.mInUltraPowerSaveMode = false;
        this.lockTaskKey = "lock_task_mode_on";
        this.uri = Settings.Global.getUriFor("lock_task_mode_on");
        this.mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.fuelgauge.SuperPowerSavingPreferenceController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                SuperPowerSavingPreferenceController superPowerSavingPreferenceController = SuperPowerSavingPreferenceController.this;
                superPowerSavingPreferenceController.changeEnabled(superPowerSavingPreferenceController.mSwitchPreference);
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.fuelgauge.SuperPowerSavingPreferenceController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (SuperPowerSavingPreferenceController.this.mSwitchPreference == null) {
                    return;
                }
                if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                    SuperPowerSavingPreferenceController.this.mPluggedIn = intent.getIntExtra("plugged", 0) != 0;
                }
                if (intent.getAction().equals("android.os.action.POWEREX_SAVE_MODE_CHANGED")) {
                    SuperPowerSavingPreferenceController.this.mInUltraPowerSaveMode = intent.getIntExtra("mode", 0) == 4;
                }
                Log.d(SuperPowerSavingPreferenceController.TAG, "mPluggedIn = " + SuperPowerSavingPreferenceController.this.mPluggedIn + "   mInUltraPowerSaveMode = " + SuperPowerSavingPreferenceController.this.mInUltraPowerSaveMode);
                SuperPowerSavingPreferenceController superPowerSavingPreferenceController = SuperPowerSavingPreferenceController.this;
                superPowerSavingPreferenceController.mSwitchPreference.setChecked(superPowerSavingPreferenceController.mInUltraPowerSaveMode);
                SuperPowerSavingPreferenceController superPowerSavingPreferenceController2 = SuperPowerSavingPreferenceController.this;
                superPowerSavingPreferenceController2.changeEnabled(superPowerSavingPreferenceController2.mSwitchPreference);
            }
        };
        this.mContext = context;
        this.mUserId = UserHandle.myUserId();
        this.mPowerManagerEx = IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex"));
        this.mContentResolver = this.mContext.getContentResolver();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.isSupportUnisocPowerController && this.isSupportSuperPowerSavingMode && isUserSupported() && isSuperPowerSavingModeAvailable(this.mContext)) ? 0 : 3;
    }

    public boolean isSuperPowerSavingModeAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_support_superPowerSaving);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.POWEREX_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        this.mContentResolver.registerContentObserver(this.uri, false, this.mObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mContentResolver.unregisterContentObserver(this.mObserver);
    }

    public boolean isUserSupported() {
        return this.mUserId == 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        try {
            this.mInUltraPowerSaveMode = this.mPowerManagerEx.isUltraPowerSaveMode();
        } catch (RemoteException unused) {
        }
        if (preference == null || !(preference instanceof SwitchPreference)) {
            return;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        this.mSwitchPreference = switchPreference;
        switchPreference.setChecked(this.mInUltraPowerSaveMode);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        try {
            this.mInUltraPowerSaveMode = this.mPowerManagerEx.isUltraPowerSaveMode();
        } catch (RemoteException unused) {
        }
        changeEnabled(preferenceScreen.findPreference(getPreferenceKey()));
    }

    private boolean isLockTaskModePinned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), this.lockTaskKey, 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeEnabled(Preference preference) {
        boolean z = (this.mPluggedIn || this.mInUltraPowerSaveMode || isLockTaskModePinned()) ? false : true;
        if (preference != null) {
            preference.setEnabled(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            this.mPowerManagerEx.setUltraPowerSaveMode(((Boolean) obj).booleanValue());
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }
}
