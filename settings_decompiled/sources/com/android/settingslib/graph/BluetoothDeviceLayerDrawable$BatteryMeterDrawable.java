package com.android.settingslib.graph;
/* loaded from: classes2.dex */
class BluetoothDeviceLayerDrawable$BatteryMeterDrawable extends BatteryMeterDrawableBase {
    private final float mAspectRatio;
    int mFrameColor;

    @Override // com.android.settingslib.graph.BatteryMeterDrawableBase
    protected float getRadiusRatio() {
        return 0.0f;
    }

    @Override // com.android.settingslib.graph.BatteryMeterDrawableBase
    protected float getAspectRatio() {
        return this.mAspectRatio;
    }
}
