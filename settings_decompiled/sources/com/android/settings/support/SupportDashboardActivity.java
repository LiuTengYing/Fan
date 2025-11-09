package com.android.settings.support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class SupportDashboardActivity extends Activity {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.support.SupportDashboardActivity.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            int i = R$string.page_tab_title_support;
            searchIndexableRaw.title = context.getString(i);
            searchIndexableRaw.screenTitle = context.getString(i);
            searchIndexableRaw.summaryOn = context.getString(R$string.support_summary);
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = context.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = SupportDashboardActivity.class.getName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "support_dashboard_activity";
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!context.getResources().getBoolean(R$bool.config_support_enabled)) {
                nonIndexableKeys.add("support_dashboard_activity");
            }
            return nonIndexableKeys;
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SupportFeatureProvider supportFeatureProvider = FeatureFactory.getFactory(this).getSupportFeatureProvider(this);
        if (supportFeatureProvider != null) {
            supportFeatureProvider.startSupport(this);
            finish();
        }
    }
}
