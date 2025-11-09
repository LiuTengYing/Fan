package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class MobileNetworkListFragment extends DashboardFragment {
    static final String KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM = "provider_model_downloaded_sim_category";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.network.MobileNetworkListFragment.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.network_provider_sims_list;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser();
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add(MobileNetworkListFragment.KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM);
            return nonIndexableKeys;
        }
    };
    private Context mContext;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.MobileNetworkListFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.telephony.action.SIM_CARD_STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(MobileNetworkListFragment.this.mSubscriptionManager);
                if (intExtra != 1 || activeSubscriptions == null || activeSubscriptions.size() > 1) {
                    return;
                }
                MobileNetworkListFragment.this.finish();
            }
        }
    };
    private SubscriptionManager mSubscriptionManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkListFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1627;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.network_provider_sims_list;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mContext = context;
        arrayList.add(new NetworkProviderSimsCategoryController(context, "provider_model_sim_category", getSettingsLifecycle()));
        arrayList.add(new NetworkProviderDownloadedSimsCategoryController(context, KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM, getSettingsLifecycle()));
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        context.registerReceiver(this.mReceiver, new IntentFilter("android.telephony.action.SIM_CARD_STATE_CHANGED"));
        return arrayList;
    }
}
