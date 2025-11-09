package com.android.settings.emergency;

import android.content.Context;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class EmergencyGestureSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.emergency_gesture_settings) { // from class: com.android.settings.emergency.EmergencyGestureSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            EmergencyGestureEntrypointPreferenceController emergencyGestureEntrypointPreferenceController = new EmergencyGestureEntrypointPreferenceController(context, "dummy_emergency_gesture_pref_key");
            return !emergencyGestureEntrypointPreferenceController.isAvailable() || emergencyGestureEntrypointPreferenceController.shouldSuppressFromSearch();
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "EmergencyGestureSetting";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1847;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.emergency_gesture_settings;
    }
}
