package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class DrawOverlayDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AppOpsManager mAppOpsManager;
    private AppStateOverlayBridge mOverlayBridge;
    private AppStateOverlayBridge.OverlayState mOverlayState;
    private SwitchPreference mSwitchPref;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 221;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mOverlayBridge = new AppStateOverlayBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        if (!Utils.isSystemAlertWindowEnabled(activity)) {
            this.mPackageInfo = null;
            return;
        }
        addPreferencesFromResource(R$xml.draw_overlay_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mPackageInfo == null) {
            return layoutInflater.inflate(R$layout.manage_applications_apps_unsupported, (ViewGroup) null);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mOverlayBridge.release();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            if (this.mOverlayState != null && ((Boolean) obj).booleanValue() != this.mOverlayState.isPermissible()) {
                setCanDrawOverlay(!this.mOverlayState.isPermissible());
                refreshUi();
            }
            return true;
        }
        return false;
    }

    private void setCanDrawOverlay(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setMode(24, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
    }

    void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 770 : 771;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null) {
            return true;
        }
        AppStateOverlayBridge.OverlayState overlayInfo = this.mOverlayBridge.getOverlayInfo(this.mPackageName, packageInfo.applicationInfo.uid);
        this.mOverlayState = overlayInfo;
        this.mSwitchPref.setChecked(overlayInfo.isPermissible());
        SwitchPreference switchPreference = this.mSwitchPref;
        AppStateOverlayBridge.OverlayState overlayState = this.mOverlayState;
        switchPreference.setEnabled(overlayState.permissionDeclared && overlayState.controlEnabled);
        return true;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateOverlayBridge.OverlayState overlayInfo;
        if (appEntry == null) {
            return "";
        }
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateOverlayBridge.OverlayState) {
            overlayInfo = (AppStateOverlayBridge.OverlayState) obj;
        } else if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            overlayInfo = new AppStateOverlayBridge.OverlayState((AppStateAppOpsBridge.PermissionState) appEntry.extraInfo);
        } else {
            AppStateOverlayBridge appStateOverlayBridge = new AppStateOverlayBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            overlayInfo = appStateOverlayBridge.getOverlayInfo(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, overlayInfo);
    }

    public static CharSequence getSummary(Context context, AppStateOverlayBridge.OverlayState overlayState) {
        int i;
        if (overlayState.isPermissible()) {
            i = R$string.app_permission_summary_allowed;
        } else {
            i = R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }
}
