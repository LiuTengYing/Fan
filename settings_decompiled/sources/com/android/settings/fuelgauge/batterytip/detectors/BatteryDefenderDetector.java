package com.android.settings.fuelgauge.batterytip.detectors;

import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.fuelgauge.batterytip.tips.BatteryDefenderTip;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
/* loaded from: classes.dex */
public class BatteryDefenderDetector {
    private BatteryInfo mBatteryInfo;

    public BatteryDefenderDetector(BatteryInfo batteryInfo) {
        this.mBatteryInfo = batteryInfo;
    }

    public BatteryTip detect() {
        return new BatteryDefenderTip(this.mBatteryInfo.isOverheated ? 0 : 2);
    }
}
