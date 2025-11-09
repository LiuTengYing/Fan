package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableData;
import androidx.lifecycle.LifecycleObserver;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.autofill.PasswordsPreferenceController;
import com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController;
import com.android.settings.applications.defaultapps.DefaultWorkAutofillPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.users.AutoSyncDataPreferenceController;
import com.android.settings.users.AutoSyncPersonalDataPreferenceController;
import com.android.settings.users.AutoSyncWorkDataPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AccountDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.accounts_dashboard_settings) { // from class: com.android.settings.accounts.AccountDashboardFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            AccountDashboardFragment.buildAccountPreferenceControllers(context, null, null, arrayList);
            AccountDashboardFragment.buildAutofillPreferenceControllers(context, arrayList);
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getDynamicRawDataToIndex(Context context, boolean z) {
            Account[] accounts;
            ArrayList arrayList = new ArrayList();
            for (UserInfo userInfo : ((UserManager) context.getSystemService("user")).getProfiles(UserHandle.myUserId())) {
                if (userInfo.isManagedProfile()) {
                    return arrayList;
                }
            }
            for (Account account : AccountManager.get(context).getAccounts()) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw).key = AccountTypePreference.buildKey(account);
                searchIndexableRaw.title = account.name;
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccountDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 8;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    protected boolean shouldSkipForInitialSUW() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accounts_dashboard_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_user_and_account_dashboard;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(PasswordsPreferenceController.class));
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        buildAutofillPreferenceControllers(context, arrayList);
        buildAccountPreferenceControllers(context, this, getIntent().getStringArrayExtra("authorities"), arrayList);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void buildAutofillPreferenceControllers(Context context, List<AbstractPreferenceController> list) {
        list.add(new DefaultAutofillPreferenceController(context));
        list.add(new DefaultWorkAutofillPreferenceController(context));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void buildAccountPreferenceControllers(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, List<AbstractPreferenceController> list) {
        AccountPreferenceController accountPreferenceController = new AccountPreferenceController(context, settingsPreferenceFragment, strArr, 3);
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getSettingsLifecycle().addObserver(accountPreferenceController);
        }
        list.add(accountPreferenceController);
        list.add(new AutoSyncDataPreferenceController(context, settingsPreferenceFragment));
        list.add(new AutoSyncPersonalDataPreferenceController(context, settingsPreferenceFragment));
        list.add(new AutoSyncWorkDataPreferenceController(context, settingsPreferenceFragment));
    }
}
