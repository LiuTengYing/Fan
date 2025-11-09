package com.android.settings.wifi.tether;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.MacAddress;
import android.net.TetheredClient;
import android.net.TetheringManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.UniWifiManager;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settings.wifi.tether.UniSoftApClientsDialog;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settings.wifi.tether.WifiTetherSettings;
import com.android.settings.wifi.tether.WifiTetherSoftApManager;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class WifiTetherSettings extends RestrictedDashboardFragment implements WifiTetherBasePreferenceController.OnTetherConfigUpdateListener, UniSoftApClientsDialog.SoftApClientsDialogListener, DialogInterface.OnDismissListener {
    static final String KEY_HOTSPOT_HIDDEN_SSID = "hotspot_hidden_ssid";
    static final String KEY_HOTSPOT_MODE = "hotspot_mode";
    static final String KEY_WIFI_TETHER_AP_BAND = "wifi_tether_network_ap_band";
    static final String KEY_WIFI_TETHER_AUTO_OFF = "wifi_tether_auto_turn_off";
    static final String KEY_WIFI_TETHER_MAXIMIZE_COMPATIBILITY = "wifi_tether_maximize_compatibility";
    static final String KEY_WIFI_TETHER_NETWORK_NAME = "wifi_tether_network_name";
    static final String KEY_WIFI_TETHER_NETWORK_PASSWORD = "wifi_tether_network_password";
    static final String KEY_WIFI_TETHER_SECURITY = "wifi_tether_security";
    static final String KEY_WIFI_TETHER_SUPPORT_MAX_CLIENTS = "wifi_tether_support_max_clients";
    private WifiTetherApBandPreferenceController mApBandPreferenceController;
    private UniWifiTetherRandomMacPreferenceController mApRandomMacPreferenceController;
    private UniSoftApClientsDialog.ClientData mClientData;
    private String mConnectedClientTitle;
    private PreferenceCategory mConnectedClientsCategory;
    private PreferenceCategory mConnectedWhiteListCategory;
    private Context mContext;
    private Handler mHandler;
    private UniWifiTetherHiddenSSIDPreferenceController mHiddSSIDPreferenceController;
    private Preference mHotspotNoConnectedClient;
    private Preference mManullyAddWhiteClientPref;
    private WifiTetherMaximizeCompatibilityPreferenceController mMaxCompatibilityPrefController;
    private UniWifiTetherSoftApMaxNumPreferenceController mMaxNumClientsPreferenceController;
    private String mNoClientConnectTitle;
    private WifiTetherPasswordPreferenceController mPasswordPreferenceController;
    private Preference mPreference;
    private int mPreferenceTypes;
    private final BroadcastReceiver mReceiver;
    private boolean mRestartWifiApAfterConfigChange;
    private WifiTetherSSIDPreferenceController mSSIDPreferenceController;
    private WifiTetherSecurityPreferenceController mSecurityPreferenceController;
    private UniSoftApClientsDialog mSoftApClientsDialog;
    private ListPreference mSoftApRandomPref;
    private WifiTetherSwitchBarController mSwitchBarController;
    private TetheringManager.TetheringEventCallback mTetheringCallback;
    private TetheringManager mTetheringManager;
    private boolean mUnavailable;
    private UniWifiManager mUniWifiManager;
    private UniWifiTetherSoftApManagerModeController mWhiteListModeController;
    private SharedPreferences mWhiteSharedPreferences;
    private WifiManager mWifiManager;
    private WifiRestriction mWifiRestriction;
    private WifiTetherSoftApManager mWifiTetherSoftApManager;
    private static final IntentFilter TETHER_STATE_CHANGE_FILTER = new IntentFilter("android.net.wifi.WIFI_AP_CLIENT_DETAILINFO_AVAILABLE_ACTION");
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new SearchIndexProvider(R$xml.uni_wifi_tether_settings);

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i != 1 ? 0 : 603;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "UniWifiTetherSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1014;
    }

    /* renamed from: com.android.settings.wifi.tether.WifiTetherSettings$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass1 implements TetheringManager.TetheringEventCallback {
        AnonymousClass1() {
        }

        public void onClientsChanged(Collection<TetheredClient> collection) {
            WifiTetherSettings.this.updateConnectedClientsPreference((Collection) collection.stream().filter(new Predicate() { // from class: com.android.settings.wifi.tether.WifiTetherSettings$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$onClientsChanged$0;
                    lambda$onClientsChanged$0 = WifiTetherSettings.AnonymousClass1.lambda$onClientsChanged$0((TetheredClient) obj);
                    return lambda$onClientsChanged$0;
                }
            }).collect(Collectors.toList()));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$onClientsChanged$0(TetheredClient tetheredClient) {
            return tetheredClient.getTetheringType() == 0;
        }
    }

    public WifiTetherSettings() {
        super("no_config_tethering");
        this.mWhiteSharedPreferences = null;
        this.mTetheringCallback = new AnonymousClass1();
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.tether.WifiTetherSettings.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d("UniWifiTetherSettings", "Receive action " + action);
                if (action.equals("android.net.wifi.WIFI_AP_CLIENT_DETAILINFO_AVAILABLE_ACTION")) {
                    WifiTetherSettings.this.updateWhilteListClientsPreference(WifiTetherSettings.this.mWifiManager.getSoftApConfiguration().getAllowedClientList());
                }
            }
        };
        this.mWifiRestriction = new WifiRestriction();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mPreferenceTypes = bundle.getInt("preference_type");
            Log.d("UniWifiTetherSettings", "mPreferenceTypes is " + this.mPreferenceTypes);
            if (this.mPreferenceTypes != 2) {
                this.mClientData = (UniSoftApClientsDialog.ClientData) bundle.getParcelable("clientdata");
            }
        }
        boolean z = true;
        setIfOnlyAvailableForAdmins(true);
        if (!isUiRestricted() && this.mWifiRestriction.isHotspotAvailable(getContext())) {
            z = false;
        }
        this.mUnavailable = z;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mUniWifiManager = UniWifiManager.getInstance();
        this.mWhiteSharedPreferences = this.mContext.getSharedPreferences("com.android.settings.wifi.tether.UniWhiteListWifiTetherClientsPreference", 0);
        this.mSSIDPreferenceController = (WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class);
        this.mSecurityPreferenceController = (WifiTetherSecurityPreferenceController) use(WifiTetherSecurityPreferenceController.class);
        this.mPasswordPreferenceController = (WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class);
        this.mMaxCompatibilityPrefController = (WifiTetherMaximizeCompatibilityPreferenceController) use(WifiTetherMaximizeCompatibilityPreferenceController.class);
        this.mApBandPreferenceController = (WifiTetherApBandPreferenceController) use(WifiTetherApBandPreferenceController.class);
        this.mHiddSSIDPreferenceController = (UniWifiTetherHiddenSSIDPreferenceController) use(UniWifiTetherHiddenSSIDPreferenceController.class);
        this.mWhiteListModeController = (UniWifiTetherSoftApManagerModeController) use(UniWifiTetherSoftApManagerModeController.class);
        this.mMaxNumClientsPreferenceController = (UniWifiTetherSoftApMaxNumPreferenceController) use(UniWifiTetherSoftApMaxNumPreferenceController.class);
        this.mApRandomMacPreferenceController = (UniWifiTetherRandomMacPreferenceController) use(UniWifiTetherRandomMacPreferenceController.class);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mUnavailable) {
            return;
        }
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SettingsMainSwitchBar switchBar = settingsActivity.getSwitchBar();
        switchBar.setTitle(getContext().getString(R$string.use_wifi_hotsopt_main_switch_title));
        this.mSwitchBarController = new WifiTetherSwitchBarController(settingsActivity, switchBar);
        getSettingsLifecycle().addObserver(this.mSwitchBarController);
        switchBar.show();
        this.mConnectedClientsCategory = (PreferenceCategory) findPreference("hotspot_connected_clients");
        this.mHotspotNoConnectedClient = findPreference("hotspot_no_connected_client");
        this.mConnectedWhiteListCategory = (PreferenceCategory) findPreference("hotspot_whitelist_clients");
        this.mManullyAddWhiteClientPref = findPreference("hotspot_add_whiltelist");
        this.mSoftApRandomPref = (ListPreference) findPreference("wifi_tether_random_mac");
        if (!this.mUniWifiManager.isWifiTetherSettingShowConnectedClientSupported() || !this.mUniWifiManager.isWifiTetherSettingAllowedClientListSupported()) {
            disabledUnisocUI();
        }
        initUnisocWifiTethering(settingsActivity);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mSoftApClientsDialog != null) {
            Log.d("UniWifiTetherSettings", "onSaveInstanceState");
            bundle.putInt("preference_type", this.mPreferenceTypes);
            if (this.mPreferenceTypes != 2) {
                bundle.putParcelable("clientdata", this.mClientData);
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (!this.mWifiRestriction.isHotspotAvailable(getContext())) {
            getEmptyTextView().setText(R$string.not_allowed_by_ent);
            getPreferenceScreen().removeAll();
        } else if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R$string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
        } else {
            this.mContext.registerReceiver(this.mReceiver, TETHER_STATE_CHANGE_FILTER, 2);
            if (this.mTetheringManager != null) {
                this.mHandler = createHandler();
                this.mTetheringManager.registerTetheringEventCallback(new HandlerExecutor(this.mHandler), this.mTetheringCallback);
            }
            WifiTetherSoftApManager wifiTetherSoftApManager = this.mWifiTetherSoftApManager;
            if (wifiTetherSoftApManager != null) {
                wifiTetherSoftApManager.registerSoftApCallback();
            }
            updateWhilteListClientsPreference(this.mWifiManager.getSoftApConfiguration().getAllowedClientList());
        }
    }

    private Handler createHandler() {
        return new Handler() { // from class: com.android.settings.wifi.tether.WifiTetherSettings.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i != 0) {
                    if (i == 1) {
                        WifiTetherSettings.this.startTether();
                        return;
                    } else {
                        super.handleMessage(message);
                        return;
                    }
                }
                Collection<TetheredClient> collection = (Collection) message.obj;
                WifiTetherSettings.this.mConnectedClientsCategory.removeAll();
                if (collection.size() == 0 || collection.isEmpty()) {
                    WifiTetherSettings.this.mConnectedClientsCategory.addPreference(WifiTetherSettings.this.mHotspotNoConnectedClient);
                    WifiTetherSettings.this.mConnectedClientsCategory.setTitle(WifiTetherSettings.this.mNoClientConnectTitle);
                    return;
                }
                WifiTetherSettings.this.mConnectedClientsCategory.setTitle(collection.size() + WifiTetherSettings.this.mConnectedClientTitle);
                for (TetheredClient tetheredClient : collection) {
                    String str = null;
                    String str2 = null;
                    for (TetheredClient.AddressInfo addressInfo : tetheredClient.getAddresses()) {
                        str = addressInfo.getHostname();
                        String inetAddress = addressInfo.getAddress().getAddress().toString();
                        str2 = inetAddress.substring(1, inetAddress.length());
                    }
                    WifiTetherSettings.this.mConnectedClientsCategory.addPreference(new UniConnectedWifiTetherClientsPreference(WifiTetherSettings.this.mContext, str, tetheredClient.getMacAddress(), str2, 0));
                }
            }
        };
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mUnavailable) {
            return;
        }
        this.mContext.unregisterReceiver(this.mReceiver);
        WifiTetherSoftApManager wifiTetherSoftApManager = this.mWifiTetherSoftApManager;
        if (wifiTetherSoftApManager != null) {
            wifiTetherSoftApManager.unRegisterSoftApCallback();
        }
        TetheringManager tetheringManager = this.mTetheringManager;
        if (tetheringManager != null) {
            tetheringManager.unregisterTetheringEventCallback(this.mTetheringCallback);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            preference.setOnPreferenceClickListener(null);
            return super.onPreferenceTreeClick(preference);
        } else if (preference instanceof UniWifiTetherClientsPreference) {
            int preferenceTypes = ((UniWifiTetherClientsPreference) preference).getPreferenceTypes();
            this.mPreferenceTypes = preferenceTypes;
            showDialog(preference, preferenceTypes);
            return true;
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.uni_wifi_tether_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, new WifiTetherBasePreferenceController.OnTetherConfigUpdateListener() { // from class: com.android.settings.wifi.tether.WifiTetherSettings$$ExternalSyntheticLambda0
            @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
            public final void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController) {
                WifiTetherSettings.this.onTetherConfigUpdated(abstractPreferenceController);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiTetherSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherSecurityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherPasswordPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new UniWifiTetherSoftApMaxNumPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new UniWifiTetherHiddenSSIDPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new UniWifiTetherSoftApManagerModeController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherAutoOffPreferenceController(context, KEY_WIFI_TETHER_AUTO_OFF));
        arrayList.add(new WifiTetherMaximizeCompatibilityPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new WifiTetherApBandPreferenceController(context, onTetherConfigUpdateListener));
        arrayList.add(new UniWifiTetherRandomMacPreferenceController(context, onTetherConfigUpdateListener));
        return arrayList;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController.OnTetherConfigUpdateListener
    public void onTetherConfigUpdated(AbstractPreferenceController abstractPreferenceController) {
        SoftApConfiguration buildNewConfig = buildNewConfig();
        this.mPasswordPreferenceController.setSecurityType(buildNewConfig.getSecurityType());
        if (checkConfigurationChangeNeedToRestart(this.mWifiManager.getSoftApConfiguration(), buildNewConfig) && this.mWifiManager.getWifiApState() == 13) {
            Log.d("UniWifiTetherSettings", "Wifi AP config changed while enabled, stop and restart");
            this.mRestartWifiApAfterConfigChange = true;
            this.mSwitchBarController.updateRestart(true);
            updateDisplayEnabled(false);
        }
        this.mSSIDPreferenceController.updateWifiApConfig(buildNewConfig);
        this.mHiddSSIDPreferenceController.updateWifiApConfig(buildNewConfig);
        this.mMaxNumClientsPreferenceController.updateWifiApConfig(buildNewConfig);
        this.mWhiteListModeController.updateWifiApConfig(buildNewConfig);
        this.mPasswordPreferenceController.updateWifiApConfig(buildNewConfig);
        this.mApBandPreferenceController.updateWifiApConfig(buildNewConfig);
        this.mApRandomMacPreferenceController.updateWifiApConfig(buildNewConfig);
        this.mWifiManager.setSoftApConfiguration(buildNewConfig);
        if (this.mRestartWifiApAfterConfigChange) {
            this.mSwitchBarController.stopTether();
        }
    }

    private SoftApConfiguration buildNewConfig() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder();
        int securityType = this.mSecurityPreferenceController.getSecurityType();
        Log.d("UniWifiTetherSettings", "buildNewConfig securityType is " + securityType);
        builder.setSsid(this.mSSIDPreferenceController.getSSID());
        if (securityType != 0) {
            builder.setPassphrase(this.mPasswordPreferenceController.getPasswordValidated(securityType), securityType);
        }
        this.mMaxCompatibilityPrefController.setupMaximizeCompatibility(builder);
        builder.setBand(this.mApBandPreferenceController.getBandIndex());
        builder.setHiddenSsid(this.mHiddSSIDPreferenceController.getIsHiddenSSID());
        builder.setMaxNumberOfClients(this.mMaxNumClientsPreferenceController.getMaximumClientNumber());
        builder.setClientControlByUserEnabled(this.mWhiteListModeController.getIsWhiteListMode());
        builder.setAllowedClientList(softApConfiguration.getAllowedClientList());
        builder.setAutoShutdownEnabled(softApConfiguration.isAutoShutdownEnabled());
        builder.setMacRandomizationSetting(this.mApRandomMacPreferenceController.getRandomMacType());
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTether() {
        this.mRestartWifiApAfterConfigChange = false;
        this.mSwitchBarController.startTether();
    }

    private void updateDisplayEnabled(boolean z) {
        this.mSSIDPreferenceController.updateEnabled(z);
        this.mSecurityPreferenceController.updateEnabled(z);
        this.mPasswordPreferenceController.updateEnabled(z);
        this.mApBandPreferenceController.updateEnabled(z);
        this.mHiddSSIDPreferenceController.updateEnabled(z);
        this.mApRandomMacPreferenceController.updateEnabled(z);
    }

    private void updateDisplayWithNewConfig() {
        ((WifiTetherSSIDPreferenceController) use(WifiTetherSSIDPreferenceController.class)).updateDisplay();
        ((WifiTetherPasswordPreferenceController) use(WifiTetherPasswordPreferenceController.class)).updateDisplay();
        ((WifiTetherMaximizeCompatibilityPreferenceController) use(WifiTetherMaximizeCompatibilityPreferenceController.class)).updateDisplay();
        ((WifiTetherApBandPreferenceController) use(WifiTetherApBandPreferenceController.class)).updateDisplay();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class SearchIndexProvider extends BaseSearchIndexProvider {
        private final WifiRestriction mWifiRestriction;

        SearchIndexProvider(int i) {
            super(i);
            this.mWifiRestriction = new WifiRestriction();
        }

        SearchIndexProvider(int i, WifiRestriction wifiRestriction) {
            super(i);
            this.mWifiRestriction = wifiRestriction;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!TetherUtil.isTetherAvailable(context) || !this.mWifiRestriction.isHotspotAvailable(context) || !context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_NAME);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_SECURITY);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_NETWORK_PASSWORD);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_AUTO_OFF);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_MAXIMIZE_COMPATIBILITY);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_AP_BAND);
                nonIndexableKeys.add("wifi_tether_random_mac");
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_SECURITY);
                nonIndexableKeys.add(WifiTetherSettings.KEY_WIFI_TETHER_SUPPORT_MAX_CLIENTS);
                nonIndexableKeys.add(WifiTetherSettings.KEY_HOTSPOT_HIDDEN_SSID);
                nonIndexableKeys.add(WifiTetherSettings.KEY_HOTSPOT_MODE);
                nonIndexableKeys.add("hotspot_connected_clients");
                nonIndexableKeys.add("hotspot_no_connected_client");
                nonIndexableKeys.add("hotspot_whitelist_clients");
                nonIndexableKeys.add("hotspot_add_whiltelist");
            }
            nonIndexableKeys.add("wifi_tether_settings_screen");
            return nonIndexableKeys;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return WifiTetherSettings.buildPreferenceControllers(context, null);
        }
    }

    /* loaded from: classes2.dex */
    static class WifiRestriction {
        WifiRestriction() {
        }

        public boolean isHotspotAvailable(Context context) {
            if (context == null) {
                return true;
            }
            return WifiEnterpriseRestrictionUtils.isWifiTetheringAllowed(context);
        }
    }

    /* loaded from: classes2.dex */
    class TetherChangeReceiver extends BroadcastReceiver {
        final /* synthetic */ WifiTetherSettings this$0;

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("UniWifiTetherSettings", "updating display config due to receiving broadcast action " + action);
            if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED") && intent.getIntExtra("wifi_state", 0) == 11 && this.this$0.mRestartWifiApAfterConfigChange) {
                this.this$0.startTether();
            }
        }
    }

    private void initUnisocWifiTethering(SettingsActivity settingsActivity) {
        this.mTetheringManager = (TetheringManager) getContext().getSystemService(TetheringManager.class);
        this.mConnectedClientTitle = settingsActivity.getString(R$string.wifi_tether_connect_title);
        this.mNoClientConnectTitle = settingsActivity.getString(R$string.hotspot_connected_clients);
        this.mWifiTetherSoftApManager = new WifiTetherSoftApManager(this.mWifiManager, new WifiTetherSoftApManager.WifiTetherSoftApCallback() { // from class: com.android.settings.wifi.tether.WifiTetherSettings.4
            @Override // com.android.settings.wifi.tether.WifiTetherSoftApManager.WifiTetherSoftApCallback
            public void onConnectedClientsChanged(List<WifiClient> list) {
            }

            @Override // com.android.settings.wifi.tether.WifiTetherSoftApManager.WifiTetherSoftApCallback
            public void onStateChanged(int i, int i2) {
                WifiTetherSettings.this.handleWifiApStateChanged(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiApStateChanged(int i) {
        Log.d("UniWifiTetherSettings", "SoftAPCallback state : " + i + ", mRestartWifiApAfterConfigChange : " + this.mRestartWifiApAfterConfigChange);
        if (i == 11) {
            updateDisplayEnabled(!this.mRestartWifiApAfterConfigChange);
        } else if (i == 13 || i == 14) {
            this.mRestartWifiApAfterConfigChange = false;
            this.mSwitchBarController.updateRestart(false);
            updateDisplayEnabled(true);
        } else {
            updateDisplayEnabled(false);
        }
        this.mSwitchBarController.handleWifiApStateChanged(i);
        this.mSSIDPreferenceController.updateWifiApState(i);
        updateDisplayWithNewConfig();
        if (this.mRestartWifiApAfterConfigChange && i == 11) {
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), 300L);
        }
    }

    private void disabledUnisocUI() {
        getPreferenceScreen().removePreference(this.mConnectedClientsCategory);
        getPreferenceScreen().removePreference(this.mConnectedWhiteListCategory);
        getPreferenceScreen().removePreference(this.mManullyAddWhiteClientPref);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        Preference preference;
        UniWifiTetherClientsPreference uniWifiTetherClientsPreference;
        if (i == 1) {
            Log.d("UniWifiTetherSettings", "onCreateDialog SOFTAP_CLIENT_DIALOG_ID");
            if (this.mPreferenceTypes != 2 && (preference = this.mPreference) != null && (preference instanceof UniWifiTetherClientsPreference) && (uniWifiTetherClientsPreference = (UniWifiTetherClientsPreference) preference) != null) {
                this.mClientData = new UniSoftApClientsDialog.ClientData(uniWifiTetherClientsPreference.getHostName(), uniWifiTetherClientsPreference.getMacAddress(), uniWifiTetherClientsPreference.getIpAddress());
            }
            UniSoftApClientsDialog createModal = UniSoftApClientsDialog.createModal(getActivity(), this, this.mClientData, this.mPreferenceTypes);
            this.mSoftApClientsDialog = createModal;
            return createModal;
        }
        return super.onCreateDialog(i);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mSoftApClientsDialog = null;
    }

    private void showDialog(Preference preference, int i) {
        if (this.mSoftApClientsDialog != null) {
            removeDialog(1);
            this.mSoftApClientsDialog = null;
        }
        this.mPreference = preference;
        this.mPreferenceTypes = i;
        showDialog(1);
    }

    @Override // com.android.settings.wifi.tether.UniSoftApClientsDialog.SoftApClientsDialogListener
    public void onWhite(UniSoftApClientsDialog uniSoftApClientsDialog) {
        clientsOperations(uniSoftApClientsDialog, 0);
    }

    @Override // com.android.settings.wifi.tether.UniSoftApClientsDialog.SoftApClientsDialogListener
    public void onCancel(UniSoftApClientsDialog uniSoftApClientsDialog) {
        clientsOperations(uniSoftApClientsDialog, 1);
    }

    private void clientsOperations(UniSoftApClientsDialog uniSoftApClientsDialog, int i) {
        String str;
        UniSoftApClientsDialog.ClientData clientData;
        int preferenceTypes = uniSoftApClientsDialog.getPreferenceTypes();
        MacAddress macAddress = null;
        if (preferenceTypes == 2 || (clientData = this.mClientData) == null) {
            str = null;
        } else {
            String hostName = clientData.getHostName();
            this.mClientData.getIpAddress();
            str = hostName;
            macAddress = this.mClientData.getMacAddress();
        }
        SoftApConfiguration softApConfig = uniSoftApClientsDialog.getSoftApConfig();
        SoftApConfiguration.Builder builder = new SoftApConfiguration.Builder(softApConfig);
        List allowedClientList = softApConfig.getAllowedClientList();
        int indexOf = allowedClientList.indexOf(macAddress);
        if (i == 0) {
            Log.d("UniWifiTetherSettings", "clientsOperations case BUTTON_POSITIVE");
            if (indexOf == -1) {
                if (preferenceTypes == 0 && macAddress != null) {
                    allowedClientList.add(macAddress);
                    this.mWhiteSharedPreferences.edit().putString(macAddress.toString(), str).commit();
                    builder.setAllowedClientList(allowedClientList);
                }
            } else if (indexOf != -1 && preferenceTypes == 1 && macAddress != null) {
                allowedClientList.remove(macAddress);
                this.mWhiteSharedPreferences.edit().remove(macAddress.toString()).commit();
                builder.setAllowedClientList(allowedClientList);
            }
            updateWhilteListClientsPreference(allowedClientList);
        }
        try {
            this.mWifiManager.setSoftApConfiguration(builder.build());
            this.mWhiteListModeController.updateWifiApConfig(softApConfig);
        } catch (IllegalArgumentException unused) {
            Log.d("UniWifiTetherSettings", "setSoftApConfiguration failed");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWhilteListClientsPreference(List<MacAddress> list) {
        this.mConnectedWhiteListCategory.removeAll();
        for (MacAddress macAddress : list) {
            this.mConnectedWhiteListCategory.addPreference(new UniWhiteListWifiTetherClientsPreference(this.mContext, null, macAddress, null, 1));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectedClientsPreference(Collection<TetheredClient> collection) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(0, collection));
    }

    private boolean checkConfigurationChangeNeedToRestart(SoftApConfiguration softApConfiguration, SoftApConfiguration softApConfiguration2) {
        return (Objects.equals(softApConfiguration.getWifiSsid(), softApConfiguration2.getWifiSsid()) && Objects.equals(softApConfiguration.getBssid(), softApConfiguration2.getBssid()) && softApConfiguration.getSecurityType() == softApConfiguration2.getSecurityType() && Objects.equals(softApConfiguration.getPassphrase(), softApConfiguration2.getPassphrase()) && softApConfiguration.isHiddenSsid() == softApConfiguration2.isHiddenSsid() && softApConfiguration.getBand() == softApConfiguration2.getBand() && softApConfiguration.getChannel() == softApConfiguration2.getChannel() && softApConfiguration.getChannels().toString().equals(softApConfiguration2.getChannels().toString())) ? false : true;
    }
}
