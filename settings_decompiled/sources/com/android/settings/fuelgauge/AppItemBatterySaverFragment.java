package com.android.settings.fuelgauge;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.sprdpower.AppPowerSaveConfig;
import android.os.sprdpower.IPowerManagerEx;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
/* loaded from: classes.dex */
public class AppItemBatterySaverFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private AppPowerSaveConfig mAppConfig;
    private SwitchPreference mAppOptimizedPref;
    private ListPreference mNetworkDataPreference;
    private String mPkgName;
    private IPowerManagerEx mPowerManagerEx;
    private ListPreference mSleepPreference;
    private ListPreference mWakeupPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 54;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mPkgName = arguments.getString("package");
        }
        addPreferencesFromResource(R$xml.app_item_batterysaver_fragment);
        IPowerManagerEx asInterface = IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex"));
        this.mPowerManagerEx = asInterface;
        boolean z = true;
        try {
            this.mAppConfig = asInterface.getAppPowerSaveConfig(this.mPkgName);
            if (this.mPowerManagerEx.getAppPowerSaveConfigWithType(this.mPkgName, 0) != 1) {
                z = false;
            }
        } catch (RemoteException unused) {
        }
        Log.d("AppItemBatterySaver", " mPkgName = " + this.mPkgName + " mAppConfig = " + this.mAppConfig + " optimizedEnable = " + z);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_optimized");
        this.mAppOptimizedPref = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mAppOptimizedPref.setChecked(z);
        ListPreference listPreference = (ListPreference) findPreference("app_standby_wakeup");
        this.mWakeupPreference = listPreference;
        listPreference.setOnPreferenceChangeListener(this);
        ListPreference listPreference2 = (ListPreference) findPreference("app_standby_sleep");
        this.mSleepPreference = listPreference2;
        listPreference2.setOnPreferenceChangeListener(this);
        ListPreference listPreference3 = (ListPreference) findPreference("app_standby_network");
        this.mNetworkDataPreference = listPreference3;
        listPreference3.setOnPreferenceChangeListener(this);
        AppPowerSaveConfig appPowerSaveConfig = this.mAppConfig;
        if (appPowerSaveConfig != null) {
            this.mWakeupPreference.setValueIndex(appPowerSaveConfig.alarm);
            ListPreference listPreference4 = this.mWakeupPreference;
            listPreference4.setSummary(listPreference4.getEntry());
            this.mSleepPreference.setValueIndex(this.mAppConfig.wakelock);
            ListPreference listPreference5 = this.mSleepPreference;
            listPreference5.setSummary(listPreference5.getEntry());
            this.mNetworkDataPreference.setValueIndex(this.mAppConfig.network);
            ListPreference listPreference6 = this.mNetworkDataPreference;
            listPreference6.setSummary(listPreference6.getEntry());
            return;
        }
        this.mWakeupPreference.setValueIndex(0);
        ListPreference listPreference7 = this.mWakeupPreference;
        listPreference7.setSummary(listPreference7.getEntry());
        this.mSleepPreference.setValueIndex(0);
        ListPreference listPreference8 = this.mSleepPreference;
        listPreference8.setSummary(listPreference8.getEntry());
        this.mNetworkDataPreference.setValueIndex(0);
        ListPreference listPreference9 = this.mNetworkDataPreference;
        listPreference9.setSummary(listPreference9.getEntry());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int i;
        if ("app_optimized".equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            Log.d("AppItemBatterySaver", "onPreferenceChange value:" + booleanValue);
            try {
                this.mPowerManagerEx.setAppPowerSaveConfigWithType(this.mPkgName, 0, booleanValue ? 1 : 0);
            } catch (RemoteException unused) {
            }
            return true;
        }
        int parseInt = Integer.parseInt((String) obj);
        if ("app_standby_wakeup".equals(preference.getKey())) {
            Log.d("AppItemBatterySaver", "onPreferenceChange newValue:" + parseInt + " enty:" + ((Object) this.mWakeupPreference.getEntry()));
            ListPreference listPreference = this.mWakeupPreference;
            listPreference.setSummary(listPreference.getEntries()[parseInt]);
            i = 1;
        } else if ("app_standby_sleep".equals(preference.getKey())) {
            Log.d("AppItemBatterySaver", "onPreferenceChange newValue:" + parseInt + " enty:" + ((Object) this.mSleepPreference.getEntry()));
            ListPreference listPreference2 = this.mSleepPreference;
            listPreference2.setSummary(listPreference2.getEntries()[parseInt]);
            i = 2;
        } else if ("app_standby_network".equals(preference.getKey())) {
            Log.d("AppItemBatterySaver", "onPreferenceChange newValue:" + parseInt + " enty:" + ((Object) this.mNetworkDataPreference.getEntry()));
            ListPreference listPreference3 = this.mNetworkDataPreference;
            listPreference3.setSummary(listPreference3.getEntries()[parseInt]);
            i = 3;
        } else {
            i = -1;
        }
        if (i != -1) {
            try {
                this.mPowerManagerEx.setAppPowerSaveConfigWithType(this.mPkgName, i, parseInt);
                Log.d("AppItemBatterySaver", "type = " + i + " value = " + parseInt + " getType = " + this.mPowerManagerEx.getAppPowerSaveConfigWithType(this.mPkgName, i));
            } catch (RemoteException unused2) {
            }
        }
        return true;
    }
}
