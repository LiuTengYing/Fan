package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.telephony.CallsDefaultSubscriptionController;
import com.android.settings.network.telephony.NetworkProviderBackupCallingPreferenceController;
import com.android.settings.network.telephony.NetworkProviderWifiCallingPreferenceController;
import com.android.settings.network.telephony.SmsDefaultSubscriptionController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class NetworkProviderCallsSmsFragment extends DashboardFragment {
    static final String KEY_PREFERENCE_CALLS = "provider_model_calls_preference";
    static final String KEY_PREFERENCE_CATEGORY_BACKUP_CALLING = "provider_model_backup_calling_category";
    static final String KEY_PREFERENCE_CATEGORY_CALLING = "provider_model_calling_category";
    static final String KEY_PREFERENCE_SMS = "provider_model_sms_preference";
    static final String LOG_TAG = "NetworkProviderCallsSmsFragment";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.network_provider_calls_sms) { // from class: com.android.settings.network.NetworkProviderCallsSmsFragment.2
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser();
        }
    };
    private Context mContext;
    private List<AbstractPreferenceController> mControllers;
    private NetworkProviderWifiCallingPreferenceController mNetworkProviderWifiCallingPreferenceController;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.NetworkProviderCallsSmsFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d(NetworkProviderCallsSmsFragment.LOG_TAG, "onReceive: intent=" + intent);
            if (intent.getAction().equals("android.telephony.action.SIM_CARD_STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                List<SubscriptionInfo> activeSubscriptions = SubscriptionUtil.getActiveSubscriptions(NetworkProviderCallsSmsFragment.this.mSubscriptionManager);
                if (intExtra == 1 && activeSubscriptions != null && activeSubscriptions.size() == 0) {
                    NetworkProviderCallsSmsFragment.this.finish();
                }
                NetworkProviderCallsSmsFragment.this.updateControllerEntries();
                NetworkProviderCallsSmsFragment.this.updatePreferenceStates();
            }
        }
    };
    private SubscriptionManager mSubscriptionManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return LOG_TAG;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CallsDefaultSubscriptionController(context, KEY_PREFERENCE_CALLS));
        arrayList.add(new SmsDefaultSubscriptionController(context, KEY_PREFERENCE_SMS));
        NetworkProviderWifiCallingPreferenceController networkProviderWifiCallingPreferenceController = new NetworkProviderWifiCallingPreferenceController(context, KEY_PREFERENCE_CATEGORY_CALLING);
        this.mNetworkProviderWifiCallingPreferenceController = networkProviderWifiCallingPreferenceController;
        networkProviderWifiCallingPreferenceController.init(getSettingsLifecycle());
        arrayList.add(this.mNetworkProviderWifiCallingPreferenceController);
        NetworkProviderBackupCallingPreferenceController networkProviderBackupCallingPreferenceController = new NetworkProviderBackupCallingPreferenceController(context, KEY_PREFERENCE_CATEGORY_BACKUP_CALLING);
        networkProviderBackupCallingPreferenceController.init(getSettingsLifecycle());
        arrayList.add(networkProviderBackupCallingPreferenceController);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        context.registerReceiver(this.mReceiver, new IntentFilter("android.telephony.action.SIM_CARD_STATE_CHANGED"));
        this.mContext = context;
        this.mControllers = arrayList;
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePreferenceStates();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.network_provider_calls_sms;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateControllerEntries() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mControllers.stream().filter(new Predicate() { // from class: com.android.settings.network.NetworkProviderCallsSmsFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((AbstractPreferenceController) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.settings.network.NetworkProviderCallsSmsFragment$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractPreferenceController) obj).displayPreference(PreferenceScreen.this);
            }
        });
    }
}
