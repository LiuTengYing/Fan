package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Settings;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class ExternalSourcesDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppStateInstallAppsBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private AppStateInstallAppsBridge.InstallAppsState mInstallAppsState;
    private RestrictedSwitchPreference mSwitchPref;
    private UserManager mUserManager;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 808;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateInstallAppsBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        this.mUserManager = UserManager.get(activity);
        addPreferencesFromResource(R$xml.external_sources_details);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("external_sources_settings_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mSwitchPref) {
            AppStateInstallAppsBridge.InstallAppsState installAppsState = this.mInstallAppsState;
            if (installAppsState == null || booleanValue == installAppsState.canInstallApps()) {
                return true;
            }
            if (Settings.ManageAppExternalSourcesActivity.class.getName().equals(getIntent().getComponent().getClassName())) {
                setResult(booleanValue ? -1 : 0);
            }
            setCanInstallApps(booleanValue);
            refreshUi();
            return true;
        }
        return false;
    }

    public static CharSequence getPreferenceSummary(Context context, ApplicationsState.AppEntry appEntry) {
        int i;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(appEntry.info.uid);
        UserManager userManager = UserManager.get(context);
        int userRestrictionSource = userManager.getUserRestrictionSource("no_install_unknown_sources_globally", userHandleForUid) | userManager.getUserRestrictionSource("no_install_unknown_sources", userHandleForUid);
        if ((userRestrictionSource & 1) != 0) {
            return context.getString(R$string.disabled_by_admin);
        }
        if (userRestrictionSource != 0) {
            return context.getString(R$string.disabled);
        }
        AppStateInstallAppsBridge appStateInstallAppsBridge = new AppStateInstallAppsBridge(context, null, null);
        ApplicationInfo applicationInfo = appEntry.info;
        if (appStateInstallAppsBridge.createInstallAppsStateFor(applicationInfo.packageName, applicationInfo.uid).canInstallApps()) {
            i = R$string.app_permission_summary_allowed;
        } else {
            i = R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }

    private void setCanInstallApps(boolean z) {
        this.mAppOpsManager.setMode(66, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return false;
        }
        if (this.mUserManager.hasBaseUserRestriction("no_install_unknown_sources", UserHandle.of(UserHandle.myUserId()))) {
            this.mSwitchPref.setChecked(false);
            this.mSwitchPref.setSummary(R$string.disabled);
            this.mSwitchPref.setEnabled(false);
            return true;
        }
        this.mSwitchPref.checkRestrictionAndSetDisabled("no_install_unknown_sources");
        if (!this.mSwitchPref.isDisabledByAdmin()) {
            this.mSwitchPref.checkRestrictionAndSetDisabled("no_install_unknown_sources_globally");
        }
        if (this.mSwitchPref.isDisabledByAdmin()) {
            return true;
        }
        AppStateInstallAppsBridge.InstallAppsState createInstallAppsStateFor = this.mAppBridge.createInstallAppsStateFor(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
        this.mInstallAppsState = createInstallAppsStateFor;
        if (!createInstallAppsStateFor.isPotentialAppSource()) {
            this.mSwitchPref.setEnabled(false);
            return true;
        }
        this.mSwitchPref.setChecked(this.mInstallAppsState.canInstallApps());
        return true;
    }
}
