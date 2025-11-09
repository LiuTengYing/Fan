package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.network.BluetoothTetherPreferenceController;
import com.android.settings.network.EthernetTetherPreferenceController;
import com.android.settings.network.TetherEnabler;
import com.android.settings.network.UsbTetherPreferenceController;
import com.android.settings.network.WifiTetherDisablePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MainSwitchBarController;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.wifi.tether.WifiTetherApBandPreferenceController;
import com.android.settings.wifi.tether.WifiTetherAutoOffPreferenceController;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settings.wifi.tether.WifiTetherFooterPreferenceController;
import com.android.settings.wifi.tether.WifiTetherPasswordPreferenceController;
import com.android.settings.wifi.tether.WifiTetherSSIDPreferenceController;
import com.android.settings.wifi.tether.WifiTetherSecurityPreferenceController;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class AllInOneTetherSettings extends RestrictedDashboardFragment implements DataSaverBackend.Listener, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener {
    static final int EXPANDED_CHILD_COUNT_DEFAULT = 4;
    static final int EXPANDED_CHILD_COUNT_MAX = Integer.MAX_VALUE;
    static final int EXPANDED_CHILD_COUNT_WITH_SECURITY_NON = 3;
    static final String KEY_WIFI_TETHER_AUTO_OFF = "wifi_tether_auto_turn_off_2";
    static final String KEY_WIFI_TETHER_NETWORK_AP_BAND = "wifi_tether_network_ap_band_2";
    static final String KEY_WIFI_TETHER_NETWORK_NAME = "wifi_tether_network_name_2";
    static final String KEY_WIFI_TETHER_NETWORK_PASSWORD = "wifi_tether_network_password_2";
    static final String KEY_WIFI_TETHER_SECURITY = "wifi_tether_security_2";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.all_tether_prefs) { // from class: com.android.settings.AllInOneTetherSettings.3
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_NETWORK_AP_BAND);
                nonIndexableKeys.add(AllInOneTetherSettings.KEY_WIFI_TETHER_SECURITY);
            }
            return nonIndexableKeys;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AllInOneTetherSettings.buildPreferenceControllers(context, null);
        }
    };
    private WifiTetherApBandPreferenceController mApBandPreferenceController;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Preference mDataSaverFooter;
    private boolean mHasShownAdvance;
    private WifiTetherPasswordPreferenceController mPasswordPreferenceController;
    private final BluetoothProfile.ServiceListener mProfileServiceListener;
    private boolean mRestartWifiApAfterConfigChange;
    private WifiTetherSSIDPreferenceController mSSIDPreferenceController;
    private WifiTetherSecurityPreferenceController mSecurityPreferenceController;
    private boolean mShouldShowWifiConfig;
    final TetherEnabler.OnTetherStateUpdateListener mStateUpdateListener;
    private final BroadcastReceiver mTetherChangeReceiver;
    private TetherEnabler mTetherEnabler;
    private boolean mUnavailable;
    private WifiManager mWifiManager;
    private PreferenceGroup mWifiTetherGroup;

    @Override // com.android.settings.dashboard.DashboardFragment
    protected String getLogTag() {
        return "AllInOneTetherSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 90;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        boolean z = false;
        this.mShouldShowWifiConfig = (TetherEnabler.isTethering(i, 0) || i == 0) ? true : true;
        getPreferenceScreen().setInitialExpandedChildrenCount(getInitialExpandedChildCount());
        this.mWifiTetherGroup.setVisible(this.mShouldShowWifiConfig);
    }

    public AllInOneTetherSettings() {
        super("no_config_tethering");
        this.mBluetoothPan = new AtomicReference<>();
        this.mShouldShowWifiConfig = true;
        this.mStateUpdateListener = new TetherEnabler.OnTetherStateUpdateListener() { // from class: com.android.settings.AllInOneTetherSettings$$ExternalSyntheticLambda0
            @Override // com.android.settings.network.TetherEnabler.OnTetherStateUpdateListener
            public final void onTetherStateUpdated(int i) {
                AllInOneTetherSettings.this.lambda$new$0(i);
            }
        };
        this.mTetherChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.AllInOneTetherSettings.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Log.isLoggable("AllInOneTetherSettings", 3)) {
                    Log.d("AllInOneTetherSettings", "updating display config due to receiving broadcast action " + action);
                }
                AllInOneTetherSettings.this.updateDisplayWithNewConfig();
                if (TextUtils.equals(action, "android.net.conn.TETHER_STATE_CHANGED")) {
                    restartWifiTetherIfNeed(AllInOneTetherSettings.this.mWifiManager.getWifiApState());
                } else if (TextUtils.equals(action, "android.net.wifi.WIFI_AP_STATE_CHANGED")) {
                    restartWifiTetherIfNeed(intent.getIntExtra("wifi_state", 0));
                }
            }

            private void restartWifiTetherIfNeed(int i) {
                if (i == 11 && AllInOneTetherSettings.this.mRestartWifiApAfterConfigChange) {
                    AllInOneTetherSettings.this.mRestartWifiApAfterConfigChange = false;
                    AllInOneTetherSettings.this.mTetherEnabler.startTethering(0);
                }
            }
        };
        this.mProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.AllInOneTetherSettings.2
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                AllInOneTetherSettings.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                AllInOneTetherSettings.this.mBluetoothPan.set(null);
            }
        };
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mSSIDPreferenceController = (WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class);
        this.mSecurityPreferenceController = (WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class);
        this.mPasswordPreferenceController = (WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class);
        this.mApBandPreferenceController = (WifiTetherApBandPreferenceController) use(WifiTetherApBandPreferenceController.class);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(UsbTetherPreferenceController.class));
        getSettingsLifecycle().addObserver((LifecycleObserver) use(BluetoothTetherPreferenceController.class));
        getSettingsLifecycle().addObserver((LifecycleObserver) use(EthernetTetherPreferenceController.class));
        getSettingsLifecycle().addObserver((LifecycleObserver) use(WifiTetherDisablePreferenceController.class));
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DataSaverBackend dataSaverBackend = new DataSaverBackend(getContext());
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverFooter = findPreference("disabled_on_data_saver_2");
        this.mWifiTetherGroup = (PreferenceGroup) findPreference("wifi_tether_settings_group");
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
            return;
        }
        this.mDataSaverBackend.addListener(this);
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mUnavailable) {
            return;
        }
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(settingsActivity.getApplicationContext(), this.mProfileServiceListener, 5);
        }
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mTetherEnabler = new TetherEnabler(settingsActivity, new MainSwitchBarController(switchBar), this.mBluetoothPan);
        getSettingsLifecycle().addObserver(this.mTetherEnabler);
        ((UsbTetherPreferenceController) use(UsbTetherPreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
        ((BluetoothTetherPreferenceController) use(BluetoothTetherPreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
        ((EthernetTetherPreferenceController) use(EthernetTetherPreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
        ((WifiTetherDisablePreferenceController) use(WifiTetherDisablePreferenceController.class)).setTetherEnabler(this.mTetherEnabler);
        switchBar.show();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R$string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        Context context = getContext();
        if (context != null) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
            intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
            context.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        }
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        TetherEnabler tetherEnabler;
        super.onResume();
        if (this.mUnavailable || (tetherEnabler = this.mTetherEnabler) == null) {
            return;
        }
        tetherEnabler.addListener(this.mStateUpdateListener);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        TetherEnabler tetherEnabler;
        super.onPause();
        if (this.mUnavailable || (tetherEnabler = this.mTetherEnabler) == null) {
            return;
        }
        tetherEnabler.removeListener(this.mStateUpdateListener);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        Context context;
        super.onStop();
        if (this.mUnavailable || (context = getContext()) == null) {
            return;
        }
        context.unregisterReceiver(this.mTetherChangeReceiver);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mDataSaverBackend.remListener(this);
        super.onDestroy();
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        this.mDataSaverFooter.setVisible(z);
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherPasswordPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherApBandPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherSecurityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherAutoOffPreferenceController(context, KEY_WIFI_TETHER_AUTO_OFF));
        arrayList.add(new WifiTetherFooterPreferenceController(context));
        return arrayList;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    protected int getPreferenceScreenResId() {
        return R$xml.all_tether_prefs;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_tether;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
    public void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController) {
        SoftApConfiguration buildNewConfig = buildNewConfig();
        this.mPasswordPreferenceController.setSecurityType(buildNewConfig.getSecurityType());
        this.mWifiManager.setSoftApConfiguration(buildNewConfig);
        if (this.mWifiManager.getWifiApState() == 13) {
            if (Log.isLoggable("AllInOneTetherSettings", 3)) {
                Log.d("AllInOneTetherSettings", "Wifi AP config changed while enabled, stop and restart");
            }
            this.mRestartWifiApAfterConfigChange = true;
            this.mTetherEnabler.stopTethering(0);
        }
    }

    private SoftApConfiguration buildNewConfig() {
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder();
        int securityType = this.mSecurityPreferenceController.getSecurityType();
        builder.setSsid(this.mSSIDPreferenceController.getSSID());
        if (securityType == 1) {
            builder.setPassphrase(this.mPasswordPreferenceController.getPasswordValidated(securityType), 1);
        }
        builder.setBand(this.mApBandPreferenceController.getBandIndex());
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDisplayWithNewConfig() {
        this.mSSIDPreferenceController.updateDisplay();
        this.mSecurityPreferenceController.updateDisplay();
        this.mPasswordPreferenceController.updateDisplay();
        this.mApBandPreferenceController.updateDisplay();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getInitialExpandedChildCount() {
        if (this.mHasShownAdvance || !this.mShouldShowWifiConfig) {
            this.mHasShownAdvance = true;
            return EXPANDED_CHILD_COUNT_MAX;
        }
        WifiTetherSecurityPreferenceController wifiTetherSecurityPreferenceController = this.mSecurityPreferenceController;
        return (wifiTetherSecurityPreferenceController != null && wifiTetherSecurityPreferenceController.getSecurityType() == 0) ? 3 : 4;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceGroup.OnExpandButtonClickListener
    public void onExpandButtonClick() {
        super.onExpandButtonClick();
        this.mHasShownAdvance = true;
    }
}
