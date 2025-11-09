package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Settings;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAlarmsAndRemindersBridge;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class AlarmsAndRemindersDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppStateAlarmsAndRemindersBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private AppStateAlarmsAndRemindersBridge.AlarmsAndRemindersState mPermissionState;
    private RestrictedSwitchPreference mSwitchPref;
    private volatile Boolean mUncommittedState;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1869;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateAlarmsAndRemindersBridge appStateAlarmsAndRemindersBridge = new AppStateAlarmsAndRemindersBridge(context, null, null);
        ApplicationInfo applicationInfo = appEntry.info;
        return context.getString(appStateAlarmsAndRemindersBridge.createPermissionState(applicationInfo.packageName, applicationInfo.uid).isAllowed() ? R$string.app_permission_summary_allowed : R$string.app_permission_summary_not_allowed);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateAlarmsAndRemindersBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService(AppOpsManager.class);
        if (bundle != null) {
            this.mUncommittedState = (Boolean) bundle.get("uncommitted_state");
            if (this.mUncommittedState != null && isAppSpecific()) {
                setResult(this.mUncommittedState.booleanValue() ? -1 : 0);
            }
        }
        addPreferencesFromResource(R$xml.alarms_and_reminders);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("alarms_and_reminders_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mUncommittedState != null) {
            bundle.putObject("uncommitted_state", this.mUncommittedState);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            this.mUncommittedState = (Boolean) obj;
            if (isAppSpecific()) {
                setResult(this.mUncommittedState.booleanValue() ? -1 : 0);
            }
            refreshUi();
            return true;
        }
        return false;
    }

    private void setCanScheduleAlarms(boolean z) {
        this.mAppOpsManager.setUidMode("android:schedule_exact_alarm", this.mPackageInfo.applicationInfo.uid, z ? 0 : 2);
    }

    private void logPermissionChange(boolean z, String str) {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), 1752, getMetricsCategory(), str, z ? 1 : 0);
    }

    private boolean isAppSpecific() {
        return Settings.AlarmsAndRemindersAppActivity.class.getName().equals(getIntent().getComponent().getClassName());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (getActivity().isChangingConfigurations() || this.mPermissionState == null || this.mUncommittedState == null || this.mUncommittedState.booleanValue() == this.mPermissionState.isAllowed()) {
            return;
        }
        setCanScheduleAlarms(this.mUncommittedState.booleanValue());
        logPermissionChange(this.mUncommittedState.booleanValue(), this.mPackageName);
        this.mUncommittedState = null;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) {
            return false;
        }
        AppStateAlarmsAndRemindersBridge.AlarmsAndRemindersState createPermissionState = this.mAppBridge.createPermissionState(this.mPackageName, applicationInfo.uid);
        this.mPermissionState = createPermissionState;
        this.mSwitchPref.setEnabled(createPermissionState.shouldBeVisible());
        this.mSwitchPref.setChecked(this.mUncommittedState != null ? this.mUncommittedState.booleanValue() : this.mPermissionState.isAllowed());
        return true;
    }
}
