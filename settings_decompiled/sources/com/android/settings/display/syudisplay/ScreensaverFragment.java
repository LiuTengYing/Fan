package com.android.settings.display.syudisplay;

import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
/* loaded from: classes.dex */
public class ScreensaverFragment extends DashboardFragment {
    private static String TAG = "ScreensaverFragment";

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.display_screensaver_layout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }
}
