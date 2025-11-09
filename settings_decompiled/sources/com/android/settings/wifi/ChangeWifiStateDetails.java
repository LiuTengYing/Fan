package com.android.settings.wifi;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class ChangeWifiStateDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppStateChangeWifiStateBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private SwitchPreference mSwitchPref;
    private AppStateChangeWifiStateBridge.WifiSettingsState mWifiSettingsState;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 338;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateChangeWifiStateBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        addPreferencesFromResource(R$xml.change_wifi_state_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setTitle(R$string.change_wifi_state_app_detail_switch);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            if (this.mWifiSettingsState != null && ((Boolean) obj).booleanValue() != this.mWifiSettingsState.isPermissible()) {
                setCanChangeWifiState(!this.mWifiSettingsState.isPermissible());
                refreshUi();
            }
            return true;
        }
        return false;
    }

    private void setCanChangeWifiState(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setMode(71, this.mPackageInfo.applicationInfo.uid, this.mPackageName, !z ? 1 : 0);
    }

    protected void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), z ? 774 : 775, str);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) {
            return false;
        }
        AppStateChangeWifiStateBridge.WifiSettingsState wifiSettingsInfo = this.mAppBridge.getWifiSettingsInfo(this.mPackageName, applicationInfo.uid);
        this.mWifiSettingsState = wifiSettingsInfo;
        this.mSwitchPref.setChecked(wifiSettingsInfo.isPermissible());
        this.mSwitchPref.setEnabled(this.mWifiSettingsState.permissionDeclared);
        return true;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateChangeWifiStateBridge.WifiSettingsState wifiSettingsInfo;
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateChangeWifiStateBridge.WifiSettingsState) {
            wifiSettingsInfo = (AppStateChangeWifiStateBridge.WifiSettingsState) obj;
        } else if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            wifiSettingsInfo = new AppStateChangeWifiStateBridge.WifiSettingsState((AppStateAppOpsBridge.PermissionState) obj);
        } else {
            AppStateChangeWifiStateBridge appStateChangeWifiStateBridge = new AppStateChangeWifiStateBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            wifiSettingsInfo = appStateChangeWifiStateBridge.getWifiSettingsInfo(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, wifiSettingsInfo);
    }

    public static CharSequence getSummary(Context context, AppStateChangeWifiStateBridge.WifiSettingsState wifiSettingsState) {
        int i;
        if (wifiSettingsState.isPermissible()) {
            i = R$string.app_permission_summary_allowed;
        } else {
            i = R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }
}
