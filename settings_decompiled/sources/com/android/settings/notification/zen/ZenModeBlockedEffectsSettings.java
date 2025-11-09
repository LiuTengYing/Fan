package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ZenModeBlockedEffectsSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.zen_mode_block_settings) { // from class: com.android.settings.notification.zen.ZenModeBlockedEffectsSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeBlockedEffectsSettings.buildPreferenceControllers(context, null);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1339;
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_intent", 4, 1332, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_light", 8, 1333, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_peek", 16, 1334, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_status", 32, 1335, new int[]{256}));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_badge", 64, 1336, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_ambient", 128, 1337, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_list", 256, 1338, null));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_mode_block_settings;
    }
}
