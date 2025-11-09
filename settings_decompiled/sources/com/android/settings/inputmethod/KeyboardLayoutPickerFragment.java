package com.android.settings.inputmethod;

import android.content.Context;
import android.hardware.input.InputDeviceIdentifier;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
/* loaded from: classes.dex */
public class KeyboardLayoutPickerFragment extends DashboardFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "KeyboardLayoutPicker";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 58;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        InputDeviceIdentifier parcelableExtra = getActivity().getIntent().getParcelableExtra("input_device_identifier");
        if (parcelableExtra == null) {
            getActivity().finish();
        }
        ((KeyboardLayoutPickerController) use(KeyboardLayoutPickerController.class)).initialize(this, parcelableExtra);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.keyboard_layout_picker_fragment;
    }
}
