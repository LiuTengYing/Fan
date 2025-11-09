package com.android.settings.users;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.users.AppCopyHelper;
import com.android.settingslib.widget.AppSwitchPreference;
/* loaded from: classes.dex */
public class AppCopyFragment extends SettingsPreferenceFragment {
    private static final String TAG = AppCopyFragment.class.getSimpleName();
    private PreferenceGroup mAppList;
    private boolean mAppListChanged;
    private AsyncTask mAppLoadingTask;
    private AppCopyHelper mHelper;
    protected UserHandle mUser;
    protected UserManager mUserManager;
    private final BroadcastReceiver mUserBackgrounding = new BroadcastReceiver() { // from class: com.android.settings.users.AppCopyFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (AppCopyFragment.this.mAppListChanged) {
                AppCopyFragment.this.mHelper.installSelectedApps();
            }
        }
    };
    private final BroadcastReceiver mPackageObserver = new BroadcastReceiver() { // from class: com.android.settings.users.AppCopyFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AppCopyFragment.this.onPackageChanged(intent);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1897;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        init(bundle);
    }

    protected void init(Bundle bundle) {
        if (bundle != null) {
            this.mUser = new UserHandle(bundle.getInt("user_id"));
        } else {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey("user_id")) {
                this.mUser = new UserHandle(arguments.getInt("user_id"));
            }
        }
        if (this.mUser == null) {
            throw new IllegalStateException("No user specified.");
        }
        this.mHelper = new AppCopyHelper(getContext(), this.mUser);
        this.mUserManager = (UserManager) getActivity().getSystemService("user");
        addPreferencesFromResource(R$xml.app_copier);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mAppList = preferenceScreen;
        preferenceScreen.setOrderingAsAdded(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("user_id", this.mUser.getIdentifier());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.mUserBackgrounding, new IntentFilter("android.intent.action.USER_BACKGROUND"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        getActivity().registerReceiver(this.mPackageObserver, intentFilter);
        this.mAppListChanged = false;
        AsyncTask asyncTask = this.mAppLoadingTask;
        if (asyncTask == null || asyncTask.getStatus() == AsyncTask.Status.FINISHED) {
            this.mAppLoadingTask = new AppLoadingTask().execute(new Void[0]);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mUserBackgrounding);
        getActivity().unregisterReceiver(this.mPackageObserver);
        if (this.mAppListChanged) {
            new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.users.AppCopyFragment.3
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    AppCopyFragment.this.mHelper.installSelectedApps();
                    return null;
                }
            }.execute(new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPackageChanged(Intent intent) {
        String action = intent.getAction();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        AppSwitchPreference appSwitchPreference = (AppSwitchPreference) findPreference(getKeyForPackage(schemeSpecificPart));
        if (appSwitchPreference == null) {
            return;
        }
        if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
            appSwitchPreference.setEnabled(false);
            appSwitchPreference.setChecked(false);
            this.mHelper.setPackageSelected(schemeSpecificPart, false);
        } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
            appSwitchPreference.setEnabled(true);
        }
    }

    /* loaded from: classes.dex */
    private class AppLoadingTask extends AsyncTask<Void, Void, Void> {
        private AppLoadingTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            AppCopyFragment.this.mHelper.fetchAndMergeApps();
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            AppCopyFragment.this.populateApps();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void populateApps() {
        if (Utils.getExistingUser(this.mUserManager, this.mUser) == null) {
            return;
        }
        this.mHelper.resetSelectedPackages();
        this.mAppList.removeAll();
        for (AppCopyHelper.SelectableAppInfo selectableAppInfo : this.mHelper.getVisibleApps()) {
            if (selectableAppInfo.packageName != null) {
                AppSwitchPreference appSwitchPreference = new AppSwitchPreference(getPrefContext());
                Drawable drawable = selectableAppInfo.icon;
                appSwitchPreference.setIcon(drawable != null ? drawable.mutate() : null);
                appSwitchPreference.setChecked(false);
                appSwitchPreference.setTitle(selectableAppInfo.appName);
                appSwitchPreference.setKey(getKeyForPackage(selectableAppInfo.packageName));
                appSwitchPreference.setPersistent(false);
                appSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.users.AppCopyFragment$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$populateApps$0;
                        lambda$populateApps$0 = AppCopyFragment.this.lambda$populateApps$0(preference, obj);
                        return lambda$populateApps$0;
                    }
                });
                this.mAppList.addPreference(appSwitchPreference);
            }
        }
        this.mAppListChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$populateApps$0(Preference preference, Object obj) {
        if (preference.isEnabled()) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.mHelper.setPackageSelected(preference.getKey().substring(4), booleanValue);
            this.mAppListChanged = true;
            return true;
        }
        return false;
    }

    private String getKeyForPackage(String str) {
        return "pkg_" + str;
    }
}
