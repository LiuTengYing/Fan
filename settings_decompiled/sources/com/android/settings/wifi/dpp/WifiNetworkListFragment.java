package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SimpleClock;
import android.os.SystemClock;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.wifi.AddNetworkFragment;
import com.android.settings.wifi.WifiEntryPreference;
import com.android.wifitrackerlib.SavedNetworkTracker;
import com.android.wifitrackerlib.WifiEntry;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class WifiNetworkListFragment extends SettingsPreferenceFragment implements SavedNetworkTracker.SavedNetworkTrackerCallback, Preference.OnPreferenceClickListener {
    static final int ADD_NETWORK_REQUEST = 1;
    static final String WIFI_CONFIG_KEY = "wifi_config_key";
    Preference mAddPreference;
    OnChooseNetworkListener mOnChooseNetworkListener;
    PreferenceCategory mPreferenceGroup;
    private WifiManager.ActionListener mSaveListener;
    SavedNetworkTracker mSavedNetworkTracker;
    WifiManager mWifiManager;
    HandlerThread mWorkerThread;

    /* loaded from: classes2.dex */
    public interface OnChooseNetworkListener {
        void onChooseNetwork(WifiNetworkConfig wifiNetworkConfig);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSubscriptionWifiEntriesChanged() {
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
    }

    /* loaded from: classes2.dex */
    private static class DisableUnreachableWifiEntryPreference extends WifiEntryPreference {
        DisableUnreachableWifiEntryPreference(Context context, WifiEntry wifiEntry) {
            super(context, wifiEntry);
        }

        @Override // com.android.settings.wifi.WifiEntryPreference, com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
        public void onUpdated() {
            super.onUpdated();
            setEnabled(getWifiEntry().getLevel() != -1);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnChooseNetworkListener)) {
            throw new IllegalArgumentException("Invalid context type");
        }
        this.mOnChooseNetworkListener = (OnChooseNetworkListener) context;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mOnChooseNetworkListener = null;
        super.onDetach();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Context context = getContext();
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mSaveListener = new WifiManager.ActionListener() { // from class: com.android.settings.wifi.dpp.WifiNetworkListFragment.1
            public void onSuccess() {
            }

            public void onFailure(int i) {
                FragmentActivity activity = WifiNetworkListFragment.this.getActivity();
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                Toast.makeText(activity, R$string.wifi_failed_save_message, 0).show();
            }
        };
        HandlerThread handlerThread = new HandlerThread("WifiNetworkListFragment{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mSavedNetworkTracker = new SavedNetworkTracker(getSettingsLifecycle(), context, (WifiManager) context.getSystemService(WifiManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), new Handler(Looper.getMainLooper()), this.mWorkerThread.getThreadHandler(), new SimpleClock(ZoneOffset.UTC) { // from class: com.android.settings.wifi.dpp.WifiNetworkListFragment.2
            public long millis() {
                return SystemClock.elapsedRealtime();
            }
        }, 15000L, 10000L, this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        this.mWorkerThread.quit();
        super.onDestroyView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        WifiConfiguration wifiConfiguration;
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1 && (wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra(WIFI_CONFIG_KEY)) != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R$xml.wifi_dpp_network_list);
        this.mPreferenceGroup = (PreferenceCategory) findPreference("access_points");
        Preference preference = new Preference(getPrefContext());
        this.mAddPreference = preference;
        preference.setIcon(R$drawable.ic_add_24dp);
        this.mAddPreference.setTitle(R$string.wifi_add_network);
        this.mAddPreference.setOnPreferenceClickListener(this);
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSavedWifiEntriesChanged() {
        this.mPreferenceGroup.removeAll();
        int i = 0;
        for (WifiEntry wifiEntry : (List) this.mSavedNetworkTracker.getSavedWifiEntries().stream().filter(new Predicate() { // from class: com.android.settings.wifi.dpp.WifiNetworkListFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onSavedWifiEntriesChanged$0;
                lambda$onSavedWifiEntriesChanged$0 = WifiNetworkListFragment.this.lambda$onSavedWifiEntriesChanged$0((WifiEntry) obj);
                return lambda$onSavedWifiEntriesChanged$0;
            }
        }).collect(Collectors.toList())) {
            DisableUnreachableWifiEntryPreference disableUnreachableWifiEntryPreference = new DisableUnreachableWifiEntryPreference(getContext(), wifiEntry);
            disableUnreachableWifiEntryPreference.setOnPreferenceClickListener(this);
            disableUnreachableWifiEntryPreference.setEnabled(wifiEntry.getLevel() != -1);
            disableUnreachableWifiEntryPreference.setOrder(i);
            this.mPreferenceGroup.addPreference(disableUnreachableWifiEntryPreference);
            i++;
        }
        this.mAddPreference.setOrder(i);
        this.mPreferenceGroup.addPreference(this.mAddPreference);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference instanceof WifiEntryPreference) {
            WifiEntry wifiEntry = ((WifiEntryPreference) preference).getWifiEntry();
            WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
            if (wifiConfiguration == null) {
                throw new IllegalArgumentException("Invalid access point");
            }
            WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(WifiDppUtils.getSecurityString(wifiEntry), wifiConfiguration.getPrintableSsid(), wifiConfiguration.preSharedKey, wifiConfiguration.hiddenSSID, wifiConfiguration.networkId, false);
            OnChooseNetworkListener onChooseNetworkListener = this.mOnChooseNetworkListener;
            if (onChooseNetworkListener != null) {
                onChooseNetworkListener.onChooseNetwork(validConfigOrNull);
            }
        } else if (preference == this.mAddPreference) {
            new SubSettingLauncher(getContext()).setTitleRes(R$string.wifi_add_network).setDestination(AddNetworkFragment.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 1).launch();
        } else {
            return super.onPreferenceTreeClick(preference);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: isValidForDppConfiguration */
    public boolean lambda$onSavedWifiEntriesChanged$0(WifiEntry wifiEntry) {
        int security = wifiEntry.getSecurity();
        return security == 2 || security == 5;
    }
}
