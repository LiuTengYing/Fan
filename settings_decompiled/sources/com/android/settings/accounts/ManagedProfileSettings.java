package com.android.settings.accounts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.util.Log;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ManagedProfileSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.accounts.ManagedProfileSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.managed_profile_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };
    private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver;
    private UserHandle mManagedUser;
    private UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ManagedProfileSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 401;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.managed_profile_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUserManager = (UserManager) getSystemService("user");
        UserHandle managedUserFromArgument = getManagedUserFromArgument();
        this.mManagedUser = managedUserFromArgument;
        if (managedUserFromArgument == null) {
            getActivity().finish();
        }
        ((WorkModePreferenceController) use(WorkModePreferenceController.class)).setManagedUser(this.mManagedUser);
        ((ContactSearchPreferenceController) use(ContactSearchPreferenceController.class)).setManagedUser(this.mManagedUser);
        ((CrossProfileCalendarPreferenceController) use(CrossProfileCalendarPreferenceController.class)).setManagedUser(this.mManagedUser);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ManagedProfileBroadcastReceiver managedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver();
        this.mManagedProfileBroadcastReceiver = managedProfileBroadcastReceiver;
        managedProfileBroadcastReceiver.register(getActivity());
        replaceEnterprisePreferenceScreenTitle("Settings.MANAGED_PROFILE_SETTINGS_TITLE", R$string.managed_profile_settings_title);
        replaceEnterpriseStringTitle("work_mode", "Settings.WORK_PROFILE_SETTING", R$string.work_mode_label);
        replaceEnterpriseStringTitle("contacts_search", "Settings.WORK_PROFILE_CONTACT_SEARCH_TITLE", R$string.managed_profile_contact_search_title);
        replaceEnterpriseStringSummary("contacts_search", "Settings.WORK_PROFILE_CONTACT_SEARCH_SUMMARY", R$string.managed_profile_contact_search_summary);
        replaceEnterpriseStringTitle("cross_profile_calendar", "Settings.CROSS_PROFILE_CALENDAR_TITLE", R$string.cross_profile_calendar_title);
        replaceEnterpriseStringSummary("cross_profile_calendar", "Settings.CROSS_PROFILE_CALENDAR_SUMMARY", R$string.cross_profile_calendar_summary);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        ManagedProfileBroadcastReceiver managedProfileBroadcastReceiver = this.mManagedProfileBroadcastReceiver;
        if (managedProfileBroadcastReceiver != null) {
            managedProfileBroadcastReceiver.unregister(getActivity());
        }
    }

    private UserHandle getManagedUserFromArgument() {
        UserHandle userHandle;
        Bundle arguments = getArguments();
        return (arguments == null || (userHandle = (UserHandle) arguments.getParcelable("android.intent.extra.USER")) == null || !this.mUserManager.isManagedProfile(userHandle.getIdentifier())) ? Utils.getManagedProfile(this.mUserManager) : userHandle;
    }

    /* loaded from: classes.dex */
    private class ManagedProfileBroadcastReceiver extends BroadcastReceiver {
        private ManagedProfileBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            Log.v("ManagedProfileSettings", "Received broadcast: " + action);
            if ("android.intent.action.MANAGED_PROFILE_REMOVED".equals(action)) {
                if (intent.getIntExtra("android.intent.extra.user_handle", -10000) == ManagedProfileSettings.this.mManagedUser.getIdentifier()) {
                    ManagedProfileSettings.this.getActivity().finish();
                    return;
                }
                return;
            }
            Log.w("ManagedProfileSettings", "Cannot handle received broadcast: " + intent.getAction());
        }

        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            context.registerReceiver(this, intentFilter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }
    }
}
