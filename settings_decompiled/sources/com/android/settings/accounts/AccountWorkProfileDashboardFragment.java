package com.android.settings.accounts;

import android.content.Context;
import androidx.lifecycle.LifecycleObserver;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.autofill.PasswordsPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.users.AutoSyncDataPreferenceController;
import com.android.settings.users.AutoSyncWorkDataPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AccountWorkProfileDashboardFragment extends DashboardFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccountWorkProfileFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1807;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.accounts_work_dashboard_settings;
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
        AccountDashboardFragment.buildAutofillPreferenceControllers(context, arrayList);
        buildAccountPreferenceControllers(context, this, getIntent().getStringArrayExtra("authorities"), arrayList);
        return arrayList;
    }

    private static void buildAccountPreferenceControllers(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr, List<AbstractPreferenceController> list) {
        AccountPreferenceController accountPreferenceController = new AccountPreferenceController(context, settingsPreferenceFragment, strArr, 2);
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getSettingsLifecycle().addObserver(accountPreferenceController);
        }
        list.add(accountPreferenceController);
        list.add(new AutoSyncDataPreferenceController(context, settingsPreferenceFragment));
        list.add(new AutoSyncWorkDataPreferenceController(context, settingsPreferenceFragment));
    }
}
