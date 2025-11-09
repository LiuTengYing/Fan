package com.android.settings.fuelgauge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.NumberFormat;
import android.os.PowerManager;
import android.text.TextUtils;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.widget.UsageProgressBarPreference;
/* loaded from: classes.dex */
public class BatteryHeaderPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, BatteryPreferenceController {
    private static final int BATTERY_MAX_LEVEL = 100;
    static final String KEY_BATTERY_HEADER = "battery_header";
    private Activity mActivity;
    BatteryStatusFeatureProvider mBatteryStatusFeatureProvider;
    private BatteryTip mBatteryTip;
    UsageProgressBarPreference mBatteryUsageProgressBarPref;
    private PreferenceFragmentCompat mHost;
    private Lifecycle mLifecycle;
    private final PowerManager mPowerManager;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

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

    public BatteryHeaderPreferenceController(Context context, String str) {
        super(context, str);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mBatteryStatusFeatureProvider = FeatureFactory.getFactory(context).getBatteryStatusFeatureProvider(context);
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    public void setFragment(PreferenceFragmentCompat preferenceFragmentCompat) {
        this.mHost = preferenceFragmentCompat;
    }

    public void setLifecycle(Lifecycle lifecycle) {
        this.mLifecycle = lifecycle;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        UsageProgressBarPreference usageProgressBarPreference = (UsageProgressBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mBatteryUsageProgressBarPref = usageProgressBarPreference;
        usageProgressBarPreference.setBottomSummary(this.mContext.getString(R$string.settings_license_activity_loading));
        if (Utils.isBatteryPresent(this.mContext)) {
            quickUpdateHeaderPreference();
        } else {
            this.mBatteryUsageProgressBarPref.setVisible(false);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        EntityHeaderController.newInstance(this.mActivity, this.mHost, null).setRecyclerView(this.mHost.getListView(), this.mLifecycle);
    }

    private CharSequence generateLabel(BatteryInfo batteryInfo) {
        if (BatteryUtils.isBatteryDefenderOn(batteryInfo)) {
            return null;
        }
        CharSequence charSequence = batteryInfo.remainingLabel;
        if (charSequence == null || batteryInfo.batteryStatus == 4) {
            return batteryInfo.statusLabel;
        }
        String str = batteryInfo.statusLabel;
        if (str == null || batteryInfo.discharging) {
            if (this.mPowerManager.isPowerSaveMode()) {
                return this.mContext.getString(R$string.battery_state_and_duration, this.mContext.getString(R$string.battery_tip_early_heads_up_done_title), batteryInfo.remainingLabel);
            }
            BatteryTip batteryTip = this.mBatteryTip;
            if (batteryTip != null && batteryTip.getType() == 5) {
                return this.mContext.getString(R$string.battery_state_and_duration, this.mContext.getString(R$string.low_battery_summary), batteryInfo.remainingLabel);
            }
            return batteryInfo.remainingLabel;
        }
        return this.mContext.getString(R$string.battery_state_and_duration, str, charSequence);
    }

    public void updateHeaderPreference(BatteryInfo batteryInfo) {
        if (!this.mBatteryStatusFeatureProvider.triggerBatteryStatusUpdate(this, batteryInfo)) {
            this.mBatteryUsageProgressBarPref.setBottomSummary(generateLabel(batteryInfo));
        }
        this.mBatteryUsageProgressBarPref.setUsageSummary(formatBatteryPercentageText(batteryInfo.batteryLevel));
        this.mBatteryUsageProgressBarPref.setPercent(batteryInfo.batteryLevel, 100L);
    }

    public void updateBatteryStatus(String str, BatteryInfo batteryInfo) {
        UsageProgressBarPreference usageProgressBarPreference = this.mBatteryUsageProgressBarPref;
        String str2 = str;
        if (str == null) {
            str2 = generateLabel(batteryInfo);
        }
        usageProgressBarPreference.setBottomSummary(str2);
    }

    public void quickUpdateHeaderPreference() {
        Intent registerReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int batteryLevel = com.android.settingslib.Utils.getBatteryLevel(registerReceiver);
        registerReceiver.getIntExtra("plugged", -1);
        this.mBatteryUsageProgressBarPref.setUsageSummary(formatBatteryPercentageText(batteryLevel));
        this.mBatteryUsageProgressBarPref.setPercent(batteryLevel, 100L);
    }

    public void updateHeaderByBatteryTips(BatteryTip batteryTip, BatteryInfo batteryInfo) {
        this.mBatteryTip = batteryTip;
        if (batteryTip == null || batteryInfo == null) {
            return;
        }
        updateHeaderPreference(batteryInfo);
    }

    private CharSequence formatBatteryPercentageText(int i) {
        return TextUtils.expandTemplate(this.mContext.getText(R$string.battery_header_title_alternate), NumberFormat.getIntegerInstance().format(i));
    }
}
