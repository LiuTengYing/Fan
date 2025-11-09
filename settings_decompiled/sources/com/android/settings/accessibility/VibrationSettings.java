package com.android.settings.accessibility;

import android.content.Context;
import android.os.Vibrator;
import android.provider.SearchIndexableResource;
import com.android.settings.R$integer;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class VibrationSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.accessibility.VibrationSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((Vibrator) context.getSystemService(Vibrator.class)).hasVibrator();
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = VibrationSettings.getVibrationXmlResourceId(context);
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "VibrationSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1292;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getVibrationXmlResourceId(Context context) {
        if (context.getResources().getInteger(R$integer.config_vibration_supported_intensity_levels) > 1) {
            return R$xml.accessibility_vibration_intensity_settings;
        }
        return R$xml.accessibility_vibration_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_accessibility_vibration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return getVibrationXmlResourceId(getContext());
    }
}
