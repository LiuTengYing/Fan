package com.android.settings.wifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.UserManager;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.p2p.WifiP2pPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ConfigureWifiSettings extends DashboardFragment {
    static final String KEY_INSTALL_CREDENTIALS = "install_credentials";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.wifi_configure_settings) { // from class: com.android.settings.wifi.ConfigureWifiSettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            if (ConfigureWifiSettings.isGuestUser(context)) {
                return false;
            }
            return context.getResources().getBoolean(R$bool.config_show_wifi_settings);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!context.getPackageManager().hasSystemFeature("android.hardware.wifi.direct")) {
                nonIndexableKeys.add("wifi_direct");
            }
            return nonIndexableKeys;
        }
    };
    private WifiWakeupPreferenceController mWifiWakeupPreferenceController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ConfigureWifiSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 338;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(R$string.network_and_internet_preferences_title);
        if (isGuestUser(getContext())) {
            return;
        }
        Preference findPreference = findPreference(KEY_INSTALL_CREDENTIALS);
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.ConfigureWifiSettings$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$onCreate$0;
                    lambda$onCreate$0 = ConfigureWifiSettings.this.lambda$onCreate$0(preference);
                    return lambda$onCreate$0;
                }
            });
        } else {
            Log.d("ConfigureWifiSettings", "Can not find the preference.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$0(Preference preference) {
        Intent intent = new Intent("android.credentials.INSTALL");
        intent.setFlags(268435456);
        intent.setComponent(new ComponentName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain"));
        intent.putExtra("certificate_install_usage", "wifi");
        getContext().startActivity(intent);
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (isGuestUser(getContext())) {
            Log.w("ConfigureWifiSettings", "Displays the restricted UI because the user is a guest.");
            EventLog.writeEvent(1397638484, "231987122", -1, "User is a guest");
            TextView textView = (TextView) getActivity().findViewById(16908292);
            if (textView != null) {
                textView.setVisibility(0);
                textView.setText(R$string.wifi_empty_list_user_restricted);
            }
            getPreferenceScreen().removeAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.wifi_configure_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (isGuestUser(context)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiP2pPreferenceController(context, getSettingsLifecycle(), (WifiManager) getSystemService(WifiManager.class)));
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        WifiWakeupPreferenceController wifiWakeupPreferenceController = (WifiWakeupPreferenceController) use(WifiWakeupPreferenceController.class);
        this.mWifiWakeupPreferenceController = wifiWakeupPreferenceController;
        wifiWakeupPreferenceController.setFragment(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        WifiWakeupPreferenceController wifiWakeupPreferenceController = this.mWifiWakeupPreferenceController;
        if (wifiWakeupPreferenceController != null && i == 600) {
            wifiWakeupPreferenceController.onActivityResult(i, i2);
        } else {
            super.onActivityResult(i, i2, intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isGuestUser(Context context) {
        UserManager userManager;
        if (context == null || (userManager = (UserManager) context.getSystemService(UserManager.class)) == null) {
            return false;
        }
        return userManager.isGuestUser();
    }
}
