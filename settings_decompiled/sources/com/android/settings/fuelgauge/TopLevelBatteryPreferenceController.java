package com.android.settings.fuelgauge;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatteryBroadcastReceiver;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class TopLevelBatteryPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, BatteryPreferenceController {
    private final BatteryBroadcastReceiver mBatteryBroadcastReceiver;
    private BatteryInfo mBatteryInfo;
    private BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;
    private String mBatteryStatusLabel;
    protected boolean mIsBatteryPresent;
    Preference mPreference;

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

    public TopLevelBatteryPreferenceController(Context context, String str) {
        super(context, str);
        this.mIsBatteryPresent = true;
        BatteryBroadcastReceiver batteryBroadcastReceiver = new BatteryBroadcastReceiver(this.mContext);
        this.mBatteryBroadcastReceiver = batteryBroadcastReceiver;
        batteryBroadcastReceiver.setBatteryChangedListener(new BatteryBroadcastReceiver.OnBatteryChangedListener() { // from class: com.android.settings.fuelgauge.TopLevelBatteryPreferenceController$$ExternalSyntheticLambda1
            @Override // com.android.settings.fuelgauge.BatteryBroadcastReceiver.OnBatteryChangedListener
            public final void onBatteryChanged(int i) {
                TopLevelBatteryPreferenceController.this.lambda$new$1(i);
            }
        });
        this.mBatteryStatusFeatureProvider = FeatureFactory.getFactory(context).getBatteryStatusFeatureProvider(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i) {
        if (i == 5) {
            this.mIsBatteryPresent = false;
        }
        BatteryInfo.getBatteryInfo(this.mContext, new BatteryInfo.Callback() { // from class: com.android.settings.fuelgauge.TopLevelBatteryPreferenceController$$ExternalSyntheticLambda2
            @Override // com.android.settings.fuelgauge.BatteryInfo.Callback
            public final void onBatteryInfoLoaded(BatteryInfo batteryInfo) {
                TopLevelBatteryPreferenceController.this.lambda$new$0(batteryInfo);
            }
        }, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(BatteryInfo batteryInfo) {
        this.mBatteryInfo = batteryInfo;
        updateState(this.mPreference);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_top_level_battery) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mBatteryBroadcastReceiver.register();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mBatteryBroadcastReceiver.unRegister();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getSummary(true);
    }

    private CharSequence getSummary(boolean z) {
        if (!this.mIsBatteryPresent) {
            return this.mContext.getText(R$string.battery_missing_message);
        }
        return getDashboardLabel(this.mContext, this.mBatteryInfo, z);
    }

    protected CharSequence getDashboardLabel(Context context, BatteryInfo batteryInfo, boolean z) {
        if (batteryInfo == null || context == null) {
            return null;
        }
        if (z) {
            setSummaryAsync(batteryInfo);
        }
        String str = this.mBatteryStatusLabel;
        return str == null ? generateLabel(batteryInfo) : str;
    }

    private void setSummaryAsync(final BatteryInfo batteryInfo) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.TopLevelBatteryPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TopLevelBatteryPreferenceController.this.lambda$setSummaryAsync$3(batteryInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSummaryAsync$3(final BatteryInfo batteryInfo) {
        final boolean triggerBatteryStatusUpdate = this.mBatteryStatusFeatureProvider.triggerBatteryStatusUpdate(this, batteryInfo);
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.fuelgauge.TopLevelBatteryPreferenceController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TopLevelBatteryPreferenceController.this.lambda$setSummaryAsync$2(triggerBatteryStatusUpdate, batteryInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setSummaryAsync$2(boolean z, BatteryInfo batteryInfo) {
        if (!z) {
            this.mBatteryStatusLabel = null;
        }
        Preference preference = this.mPreference;
        CharSequence charSequence = this.mBatteryStatusLabel;
        if (charSequence == null) {
            charSequence = generateLabel(batteryInfo);
        }
        preference.setSummary(charSequence);
    }

    private CharSequence generateLabel(BatteryInfo batteryInfo) {
        CharSequence charSequence;
        if (batteryInfo.discharging || (charSequence = batteryInfo.chargeLabel) == null) {
            CharSequence charSequence2 = batteryInfo.remainingLabel;
            return charSequence2 == null ? batteryInfo.batteryPercentString : this.mContext.getString(R$string.power_remaining_settings_home_page, batteryInfo.batteryPercentString, charSequence2);
        }
        return charSequence;
    }

    public void updateBatteryStatus(String str, BatteryInfo batteryInfo) {
        CharSequence summary;
        this.mBatteryStatusLabel = str;
        if (this.mPreference == null || (summary = getSummary(false)) == null) {
            return;
        }
        this.mPreference.setSummary(summary);
    }

    protected static ComponentName convertClassPathToComponentName(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String[] split = str.split("\\.");
        int length = split.length - 1;
        if (length < 0) {
            return null;
        }
        int length2 = (str.length() - split[length].length()) - 1;
        return new ComponentName(length2 > 0 ? str.substring(0, length2) : "", split[length]);
    }
}
