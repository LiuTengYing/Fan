package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessController;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class ZenAccessSettings extends EmptyTextSettings implements ZenAccessSettingObserverMixin.Listener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.zen_access_settings);
    private final String TAG = "ZenAccessSettings";
    private Context mContext;
    private NotificationManager mNoMan;
    private PackageManager mPkgMan;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 180;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPkgMan = activity.getPackageManager();
        this.mNoMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        getSettingsLifecycle().addObserver(new ZenAccessSettingObserverMixin(getContext(), this));
    }

    @Override // com.android.settings.widget.EmptyTextSettings, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(R$string.zen_access_empty_text);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.zen_access_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        reloadList();
    }

    @Override // com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin.Listener
    public void onZenAccessPolicyChanged() {
        reloadList();
    }

    private void reloadList() {
        List<ApplicationInfo> installedApplications;
        if (((UserManager) this.mContext.getSystemService(UserManager.class)).isManagedProfile(UserHandle.myUserId())) {
            Log.w("ZenAccessSettings", "DND access cannot be enabled in a work profile");
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        ArrayList arrayList = new ArrayList();
        Set<String> packagesRequestingNotificationPolicyAccess = ZenAccessController.getPackagesRequestingNotificationPolicyAccess();
        if (!packagesRequestingNotificationPolicyAccess.isEmpty() && (installedApplications = this.mPkgMan.getInstalledApplications(0)) != null) {
            for (ApplicationInfo applicationInfo : installedApplications) {
                if (packagesRequestingNotificationPolicyAccess.contains(applicationInfo.packageName)) {
                    arrayList.add(applicationInfo);
                }
            }
        }
        ArraySet arraySet = new ArraySet();
        arraySet.addAll(this.mNoMan.getEnabledNotificationListenerPackages());
        arraySet.addAll(ZenAccessController.getPackagesWithManageNotifications());
        Collections.sort(arrayList, new PackageItemInfo.DisplayNameComparator(this.mPkgMan));
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            final ApplicationInfo applicationInfo2 = (ApplicationInfo) it.next();
            final String str = applicationInfo2.packageName;
            CharSequence loadLabel = applicationInfo2.loadLabel(this.mPkgMan);
            AppPreference appPreference = new AppPreference(getPrefContext());
            appPreference.setKey(str);
            appPreference.setIcon(applicationInfo2.loadIcon(this.mPkgMan));
            appPreference.setTitle(loadLabel);
            if (arraySet.contains(str)) {
                appPreference.setEnabled(false);
                appPreference.setSummary(getString(R$string.zen_access_disabled_package_warning));
            } else {
                appPreference.setSummary(getPreferenceSummary(str));
            }
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.zen.ZenAccessSettings$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$reloadList$0;
                    lambda$reloadList$0 = ZenAccessSettings.this.lambda$reloadList$0(str, applicationInfo2, preference);
                    return lambda$reloadList$0;
                }
            });
            preferenceScreen.addPreference(appPreference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reloadList$0(String str, ApplicationInfo applicationInfo, Preference preference) {
        AppInfoBase.startAppInfoFragment(ZenAccessDetails.class, getString(R$string.manage_zen_access_title), str, applicationInfo.uid, this, -1, getMetricsCategory());
        return true;
    }

    private int getPreferenceSummary(String str) {
        if (ZenAccessController.hasAccess(getContext(), str)) {
            return R$string.app_permission_summary_allowed;
        }
        return R$string.app_permission_summary_not_allowed;
    }
}
