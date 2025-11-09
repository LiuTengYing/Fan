package com.android.settings.fuelgauge;

import android.content.Context;
import com.android.settingslib.utils.AsyncLoaderCompat;
/* loaded from: classes.dex */
public class BatteryInfoLoader extends AsyncLoaderCompat<BatteryInfo> {
    BatteryUtils mBatteryUtils;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.utils.AsyncLoaderCompat
    public void onDiscardResult(BatteryInfo batteryInfo) {
    }

    public BatteryInfoLoader(Context context) {
        super(context);
        this.mBatteryUtils = BatteryUtils.getInstance(context);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public BatteryInfo loadInBackground() {
        return this.mBatteryUtils.getBatteryInfo("BatteryInfoLoader");
    }
}
