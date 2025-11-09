package com.android.settings.privacy;

import android.content.Context;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PrivacyControlsFragment extends DashboardFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PrivacyDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1587;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CameraToggleController(context, "privacy_camera_toggle"));
        arrayList.add(new MicToggleController(context, "privacy_mic_toggle"));
        arrayList.add(new LocationToggleController(context, "privacy_location_toggle", getSettingsLifecycle()));
        arrayList.add(new ShowClipAccessNotificationPreferenceController(context));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.privacy_controls_settings;
    }
}
