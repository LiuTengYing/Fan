package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.ToggleAutoclickPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ToggleAutoclickPreferenceFragment extends DashboardFragment implements ToggleAutoclickPreferenceController.OnChangeListener {
    private static final List<AbstractPreferenceController> sControllers = new ArrayList();
    private static final int[] AUTOCLICK_PREFERENCE_SUMMARIES = {R$plurals.accessibilty_autoclick_preference_subtitle_short_delay, R$plurals.accessibilty_autoclick_preference_subtitle_medium_delay, R$plurals.accessibilty_autoclick_preference_subtitle_long_delay};
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accessibility_autoclick_settings) { // from class: com.android.settings.accessibility.ToggleAutoclickPreferenceFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ToggleAutoclickPreferenceFragment.buildPreferenceControllers(context, null);
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AutoclickPrefFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 335;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence getAutoclickPreferenceSummary(Resources resources, int i) {
        int autoclickPreferenceSummaryIndex = getAutoclickPreferenceSummaryIndex(i);
        int i2 = i == 1000 ? 1 : 3;
        float f = i / 1000.0f;
        return resources.getQuantityString(AUTOCLICK_PREFERENCE_SUMMARIES[autoclickPreferenceSummaryIndex], i2, String.format(f == 1.0f ? "%.0f" : "%.1f", Float.valueOf(f)));
    }

    private static int getAutoclickPreferenceSummaryIndex(int i) {
        if (i <= 200) {
            return 0;
        }
        if (i >= 1000) {
            return AUTOCLICK_PREFERENCE_SUMMARIES.length - 1;
        }
        return (i - 200) / (800 / (AUTOCLICK_PREFERENCE_SUMMARIES.length - 1));
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_autoclick;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accessibility_autoclick_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Iterator<AbstractPreferenceController> it = sControllers.iterator();
        while (it.hasNext()) {
            ((ToggleAutoclickPreferenceController) it.next()).setOnChangeListener(this);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Iterator<AbstractPreferenceController> it = sControllers.iterator();
        while (it.hasNext()) {
            ((ToggleAutoclickPreferenceController) it.next()).setOnChangeListener(null);
        }
    }

    @Override // com.android.settings.accessibility.ToggleAutoclickPreferenceController.OnChangeListener
    public void onCheckedChanged(Preference preference) {
        for (AbstractPreferenceController abstractPreferenceController : sControllers) {
            abstractPreferenceController.updateState(preference);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        for (String str : context.getResources().getStringArray(R$array.accessibility_autoclick_control_selector_keys)) {
            sControllers.add(new ToggleAutoclickPreferenceController(context, lifecycle, str));
        }
        return sControllers;
    }
}
