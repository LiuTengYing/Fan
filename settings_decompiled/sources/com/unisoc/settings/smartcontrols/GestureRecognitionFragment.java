package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes2.dex */
public class GestureRecognitionFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.gesture_recognition);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "GestureRecognitionFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50955;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        int i = Settings.Global.getInt(getContentResolver(), "gesture_recognition_screenshot", 0);
        int i2 = Settings.Global.getInt(getContentResolver(), "gesture_recognition_screen_slide", 0);
        int i3 = Settings.Global.getInt(getContentResolver(), "gesture_recognition_play", 0);
        findPreference("screen_silde").setSummary(isOpen(i2));
        findPreference("screenchut").setSummary(isOpen(i));
        findPreference("gesture_play").setSummary(isOpen(i3));
    }

    public int isOpen(int i) {
        if (i == 1) {
            return R$string.gesture_screen_on;
        }
        return R$string.gesture_screen_off;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.gesture_recognition;
    }
}
