package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.R$string;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class LowBatteryTip extends EarlyWarningTip {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: com.android.settings.fuelgauge.batterytip.tips.LowBatteryTip.1
        @Override // android.os.Parcelable.Creator
        public BatteryTip createFromParcel(Parcel parcel) {
            return new LowBatteryTip(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public BatteryTip[] newArray(int i) {
            return new LowBatteryTip[i];
        }
    };

    public LowBatteryTip(int i, boolean z) {
        super(i, z);
        this.mType = 5;
    }

    public LowBatteryTip(Parcel parcel) {
        super(parcel);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getTitle(Context context) {
        return context.getString(R$string.battery_tip_low_battery_title);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public CharSequence getSummary(Context context) {
        return context.getString(R$string.battery_tip_low_battery_summary);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
    }

    @Override // com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip, com.android.settings.fuelgauge.batterytip.tips.BatteryTip
    public void log(Context context, MetricsFeatureProvider metricsFeatureProvider) {
        metricsFeatureProvider.action(context, 1352, this.mState);
    }
}
