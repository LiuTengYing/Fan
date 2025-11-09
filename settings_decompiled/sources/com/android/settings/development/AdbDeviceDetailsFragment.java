package com.android.settings.development;

import android.content.Context;
import android.debug.PairDevice;
import android.os.Bundle;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AdbDeviceDetailsFragment extends DashboardFragment {
    private PairDevice mPairedDevice;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdbDeviceDetailsFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1836;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        Bundle arguments = getArguments();
        if (arguments.containsKey("paired_device")) {
            this.mPairedDevice = arguments.getParcelable("paired_device");
        }
        super.onAttach(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.adb_device_details_fragment;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AdbDeviceDetailsHeaderController(this.mPairedDevice, context, this));
        arrayList.add(new AdbDeviceDetailsActionController(this.mPairedDevice, context, this));
        arrayList.add(new AdbDeviceDetailsFingerprintController(this.mPairedDevice, context, this));
        return arrayList;
    }
}
