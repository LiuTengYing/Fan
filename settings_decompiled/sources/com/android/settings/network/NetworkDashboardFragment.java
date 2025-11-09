package com.android.settings.network;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.UniWifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.AirplaneModeEnabler;
import com.android.settings.R$plurals;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.location.WifiScanningFragment;
import com.android.settings.network.InternetUpdater;
import com.android.settings.network.MobilePlanPreferenceController;
import com.android.settings.network.helper.OnSwitchItemClickLInstener;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RestrictedSwitchPreferences;
import com.android.settings.wifi.AddNetworkFragment;
import com.android.settings.wifi.AddWifiNetworkPreference;
import com.android.settings.wifi.ConfigureWifiEntryFragment;
import com.android.settings.wifi.ConnectedWifiEntryPreference;
import com.android.settings.wifi.LongPressWifiEntryPreference;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.WifiEntryPreference;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.FooterPreference;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class NetworkDashboardFragment extends DashboardFragment implements MobilePlanPreferenceController.MobilePlanPreferenceHost, OnActivityResultListener, InternetUpdater.InternetChangeListener, WifiPickerTracker.WifiPickerTrackerCallback, WifiDialog2.WifiDialog2Listener, DialogInterface.OnDismissListener, AirplaneModeEnabler.OnAirplaneModeChangedListener {
    static final int ADD_NETWORK_REQUEST = 2;
    static final String PREF_KEY_CONNECTED_ACCESS_POINTS = "connected_access_point";
    static final String PREF_KEY_FIRST_ACCESS_POINTS = "first_access_points";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.network_provider_internet) { // from class: com.android.settings.network.NetworkDashboardFragment.2
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return NetworkDashboardFragment.buildPreferenceControllers(context, null, null, null, null);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (((UserManager) context.getSystemService(UserManager.class)).isGuestUser()) {
                nonIndexableKeys.add("private_dns_settings");
            }
            return nonIndexableKeys;
        }
    };
    private boolean isWifiShouldShow;
    AddWifiNetworkPreference mAddWifiNetworkPreference;
    AirplaneModeEnabler mAirplaneModeEnabler;
    Preference mAirplaneModeMsgPreference;
    private boolean mClickedConnect;
    Preference mConfigureWifiSettingsPreference;
    ConnectedEthernetNetworkController mConnectedEthernetNetworkController;
    PreferenceCategory mConnectedWifiEntryPreferenceCategory;
    private WifiDialog2 mDialog;
    private WifiEntry mDialogWifiEntry;
    private String mDialogWifiEntryKey;
    private boolean mEnableNextOnConnection;
    PreferenceCategory mFirstWifiEntryPreferenceCategory;
    InternetUpdater mInternetUpdater;
    protected boolean mIsRestricted;
    private boolean mIsShowNetworkCategoryLabel;
    private boolean mIsViewLoading;
    private String mOpenSsid;
    private WifiManager.ActionListener mSaveListener;
    Preference mSavedNetworksPreference;
    PreferenceCategory mWifiEntryPreferenceCategory;
    protected WifiManager mWifiManager;
    WifiPickerTracker mWifiPickerTracker;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;
    FooterPreference mWifiStatusMessagePreference;
    private boolean mIsWifiEntryListStale = true;
    final Runnable mRemoveLoadingRunnable = new Runnable() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            NetworkDashboardFragment.this.lambda$new$0();
        }
    };
    final Runnable mUpdateWifiEntryPreferencesRunnable = new Runnable() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            NetworkDashboardFragment.this.lambda$new$1();
        }
    };
    final Runnable mHideProgressBarRunnable = new Runnable() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            NetworkDashboardFragment.this.lambda$new$2();
        }
    };
    private RestrictedSwitchPreferences.onItemClickListener onItemClickListener = new RestrictedSwitchPreferences.onItemClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment.1
        @Override // com.android.settings.widget.RestrictedSwitchPreferences.onItemClickListener
        public void onItemClick() {
            Log.d("NetworkDashboardFrag", "onItemClick: " + NetworkDashboardFragment.this.mWifiEntryPreferenceCategory.isVisible());
            if (NetworkDashboardFragment.this.mWifiEntryPreferenceCategory.isVisible()) {
                NetworkDashboardFragment.this.isWifiShouldShow = false;
                NetworkDashboardFragment.this.removeWifiEntryPreference();
                NetworkDashboardFragment.this.removeConnectedWifiPreference();
                return;
            }
            NetworkDashboardFragment.this.isWifiShouldShow = true;
            NetworkDashboardFragment.this.updateWifiEntryPreferencesDelayed();
        }

        @Override // com.android.settings.widget.RestrictedSwitchPreferences.onItemClickListener
        public void onSwitchCheck(boolean z) {
            Log.d("NetworkDashboardFrag", "onSwitchCheck: " + z);
            WifiManager wifiManager = NetworkDashboardFragment.this.mWifiManager;
            if (wifiManager != null) {
                wifiManager.setWifiEnabled(z);
            }
            if (z) {
                NetworkDashboardFragment.this.isWifiShouldShow = true;
                NetworkDashboardFragment.this.updateWifiEntryPreferences();
                AirplaneModeEnabler airplaneModeEnabler = NetworkDashboardFragment.this.mAirplaneModeEnabler;
                if (airplaneModeEnabler != null) {
                    airplaneModeEnabler.setAirplaneMode(false);
                }
            }
        }
    };

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 1 == i ? 609 : 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 746;
    }

    @Override // com.android.settings.network.MobilePlanPreferenceController.MobilePlanPreferenceHost
    public void showMobilePlanMessageDialog() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mIsViewLoading) {
            setLoading(false, false);
            this.mIsViewLoading = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (this.isWifiShouldShow) {
            updateWifiEntryPreferences();
            if (getView() != null) {
                getView().postDelayed(this.mRemoveLoadingRunnable, 20L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2() {
        setProgressBarVisible(false);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIsShowNetworkCategoryLabel = WifiUtils.hasSystemFeature(getContext(), "android.hardware.wifi") ? UniWifiManager.getInstance().isShowNetworkCategoryLabel() : false;
        this.mAirplaneModeEnabler = new AirplaneModeEnabler(getContext(), this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (hasWifiManager()) {
            WifiPickerTrackerHelper wifiPickerTrackerHelper = new WifiPickerTrackerHelper(getSettingsLifecycle(), getContext(), this);
            this.mWifiPickerTrackerHelper = wifiPickerTrackerHelper;
            this.mWifiPickerTracker = wifiPickerTrackerHelper.getWifiPickerTracker();
        }
        this.mInternetUpdater = new InternetUpdater(getContext(), getSettingsLifecycle(), this);
        this.mEnableNextOnConnection = getActivity().getIntent().getBooleanExtra("wifi_enable_next_on_connect", false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.network_provider_internet;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AirplaneModePreferenceController) use(AirplaneModePreferenceController.class)).setFragment(this);
        getSettingsLifecycle().addObserver((LifecycleObserver) use(AllInOneTetherPreferenceController.class));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        ((AllInOneTetherPreferenceController) use(AllInOneTetherPreferenceController.class)).initEnabler(getSettingsLifecycle());
        hasWifiManager();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mIsViewLoading) {
            getView().postDelayed(this.mRemoveLoadingRunnable, (hasWifiManager() && this.mWifiManager.isWifiEnabled()) ? 1000L : 100L);
        }
        if (this.mIsRestricted) {
            restrictUi();
        } else {
            this.mAirplaneModeEnabler.start();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        addPreferences();
        RecyclerView listView = getListView();
        if (listView != null) {
            listView.setItemAnimator(null);
        }
        WifiPickerTracker wifiPickerTracker = this.mWifiPickerTracker;
        changeNextButtonState((wifiPickerTracker == null || wifiPickerTracker.getConnectedWifiEntry() == null) ? false : true);
        InternetUpdater internetUpdater = this.mInternetUpdater;
        if (internetUpdater != null && internetUpdater.getInternetType() == 2) {
            this.isWifiShouldShow = false;
        } else {
            this.isWifiShouldShow = true;
        }
        Log.d("NetworkDashboardFrag", "onResume: " + this.isWifiShouldShow + "   mClickedConnect ===  " + this.mClickedConnect);
        OnSwitchItemClickLInstener.getInstance().setListener(this.onItemClickListener);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        this.mIsWifiEntryListStale = true;
        getView().removeCallbacks(this.mRemoveLoadingRunnable);
        getView().removeCallbacks(this.mUpdateWifiEntryPreferencesRunnable);
        getView().removeCallbacks(this.mHideProgressBarRunnable);
        this.mAirplaneModeEnabler.stop();
        this.isWifiShouldShow = false;
        OnSwitchItemClickLInstener.getInstance().removeListener();
        super.onStop();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        AirplaneModeEnabler airplaneModeEnabler = this.mAirplaneModeEnabler;
        if (airplaneModeEnabler != null) {
            airplaneModeEnabler.close();
        }
        super.onDestroy();
    }

    private void restrictUi() {
        getPreferenceScreen().removeAll();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_network_dashboard;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this.mMetricsFeatureProvider, this, this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, MetricsFeatureProvider metricsFeatureProvider, Fragment fragment, MobilePlanPreferenceController.MobilePlanPreferenceHost mobilePlanPreferenceHost) {
        MobilePlanPreferenceController mobilePlanPreferenceController = new MobilePlanPreferenceController(context, mobilePlanPreferenceHost);
        InternetPreferenceController internetPreferenceController = new InternetPreferenceController(context, lifecycle);
        VpnPreferenceController vpnPreferenceController = new VpnPreferenceController(context);
        PrivateDnsPreferenceController privateDnsPreferenceController = new PrivateDnsPreferenceController(context);
        if (lifecycle != null) {
            lifecycle.addObserver(mobilePlanPreferenceController);
            lifecycle.addObserver(vpnPreferenceController);
            lifecycle.addObserver(privateDnsPreferenceController);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MobileNetworkSummaryController(context, lifecycle));
        arrayList.add(new TetherPreferenceController(context, lifecycle));
        arrayList.add(vpnPreferenceController);
        arrayList.add(new ProxyPreferenceController(context));
        arrayList.add(mobilePlanPreferenceController);
        arrayList.add(internetPreferenceController);
        arrayList.add(privateDnsPreferenceController);
        arrayList.add(new WifiSwitchPreferenceController(context, lifecycle));
        arrayList.add(new NetworkProviderCallsSmsController(context, lifecycle));
        arrayList.add(new ConnectedEthernetNetworkController(context, lifecycle));
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        Log.d("NetworkDashboardFrag", "onCreateDialog: dialogId=" + i);
        if (i == 1) {
            final MobilePlanPreferenceController mobilePlanPreferenceController = (MobilePlanPreferenceController) use(MobilePlanPreferenceController.class);
            return new AlertDialog.Builder(getActivity()).setMessage(mobilePlanPreferenceController.getMobilePlanDialogMessage()).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    MobilePlanPreferenceController.this.setMobilePlanDialogMessage(null);
                }
            }).create();
        }
        return super.onCreateDialog(i);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        WifiConfiguration wifiConfiguration;
        WifiDialog2 wifiDialog2;
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            ((AirplaneModePreferenceController) use(AirplaneModePreferenceController.class)).onActivityResult(i, i2, intent);
        }
        if (hasWifiManager()) {
            if (i == 2) {
                handleAddNetworkRequest(i2, intent);
            } else if (i == 0) {
                if (i2 != -1 || (wifiDialog2 = this.mDialog) == null) {
                    return;
                }
                wifiDialog2.dismiss();
            } else if (i == 3 && i2 == -1 && (wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("network_config_key")) != null) {
                this.mWifiManager.connect(wifiConfiguration, new WifiConnectActionListener());
            }
        }
    }

    void handleAddNetworkRequest(int i, Intent intent) {
        if (i == -1) {
            handleAddNetworkSubmitEvent(intent);
        }
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration == null || !hasWifiManager()) {
            return;
        }
        this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
    }

    private boolean hasWifiManager() {
        if (this.mWifiManager != null) {
            return true;
        }
        Context context = getContext();
        if (context == null) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        this.mWifiManager = wifiManager;
        return wifiManager != null;
    }

    private void addConnectedEthernetNetworkController() {
        if (this.mConnectedEthernetNetworkController == null) {
            this.mConnectedEthernetNetworkController = new ConnectedEthernetNetworkController(getContext(), getSettingsLifecycle());
        }
        this.mConnectedEthernetNetworkController.displayPreference(getPreferenceScreen());
    }

    private void addPreferences() {
        ConnectedEthernetNetworkController connectedEthernetNetworkController = this.mConnectedEthernetNetworkController;
        if (connectedEthernetNetworkController != null) {
            connectedEthernetNetworkController.displayPreference(getPreferenceScreen());
        }
        this.mAddWifiNetworkPreference = new AddWifiNetworkPreference(getPrefContext());
        this.mConnectedWifiEntryPreferenceCategory = (PreferenceCategory) findPreference(PREF_KEY_CONNECTED_ACCESS_POINTS);
        this.mFirstWifiEntryPreferenceCategory = (PreferenceCategory) findPreference(PREF_KEY_FIRST_ACCESS_POINTS);
        this.mWifiEntryPreferenceCategory = (PreferenceCategory) findPreference("access_points");
        this.mSavedNetworksPreference = findPreference("saved_networks");
        addConnectedEthernetNetworkController();
        this.mWifiStatusMessagePreference = (FooterPreference) findPreference("wifi_status_message_footer");
        this.mWifiEntryPreferenceCategory.setVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeConnectedWifiPreference() {
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        this.mConnectedWifiEntryPreferenceCategory.setVisible(false);
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onInternetTypeChanged(int i) {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                NetworkDashboardFragment.this.lambda$onInternetTypeChanged$4();
            }
        });
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged */
    public void lambda$onInternetTypeChanged$4() {
        if (this.mIsRestricted || !hasWifiManager()) {
            return;
        }
        int wifiState = this.mWifiPickerTracker.getWifiState();
        if (isVerboseLoggingEnabled()) {
            Log.i("NetworkDashboardFrag", "onWifiStateChanged called with wifi state: " + wifiState);
        }
        if (isFinishingOrDestroyed()) {
            Log.w("NetworkDashboardFrag", "onWifiStateChanged shouldn't run when fragment is finishing or destroyed");
        } else if (wifiState == 0) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
        } else if (wifiState == 1) {
            setWifiScanMessage(false);
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            setAdditionalSettingsSummaries();
            setProgressBarVisible(false);
            this.mClickedConnect = false;
        } else if (wifiState == 2) {
            removeConnectedWifiEntryPreference();
            removeWifiEntryPreference();
            setProgressBarVisible(true);
        } else if (wifiState != 3) {
        } else {
            setWifiScanMessage(true);
            updateWifiEntryPreferences();
        }
    }

    void setWifiScanMessage(boolean z) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(LocationManager.class);
        if (!hasWifiManager() || z || !locationManager.isLocationEnabled() || !this.mWifiManager.isScanAlwaysAvailable()) {
            this.mWifiStatusMessagePreference.setVisible(false);
            return;
        }
        if (TextUtils.isEmpty(this.mWifiStatusMessagePreference.getTitle())) {
            this.mWifiStatusMessagePreference.setTitle(R$string.wifi_scan_notify_message);
            this.mWifiStatusMessagePreference.setLearnMoreText(context.getString(R$string.wifi_scan_change));
            this.mWifiStatusMessagePreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    NetworkDashboardFragment.this.lambda$setWifiScanMessage$5(view);
                }
            });
        }
        this.mWifiStatusMessagePreference.setVisible(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setWifiScanMessage$5(View view) {
        launchWifiScanningFragment();
    }

    ConnectedWifiEntryPreference createConnectedWifiEntryPreference(WifiEntry wifiEntry) {
        if (this.mInternetUpdater.getInternetType() == 2) {
            return new ConnectedWifiEntryPreference(getPrefContext(), wifiEntry, this);
        }
        return new FirstWifiEntryPreference(getPrefContext(), wifiEntry, this);
    }

    protected void updateWifiEntryPreferences() {
        WifiPickerTracker wifiPickerTracker;
        if (!this.isWifiShouldShow || getActivity() == null || getView() == null || this.mIsRestricted || (wifiPickerTracker = this.mWifiPickerTracker) == null || wifiPickerTracker.getWifiState() != 3) {
            return;
        }
        this.mWifiEntryPreferenceCategory.setVisible(true);
        final WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        PreferenceCategory connectedWifiPreferenceCategory = getConnectedWifiPreferenceCategory();
        connectedWifiPreferenceCategory.setVisible(connectedWifiEntry != null);
        if (connectedWifiEntry != null) {
            LongPressWifiEntryPreference longPressWifiEntryPreference = (LongPressWifiEntryPreference) connectedWifiPreferenceCategory.findPreference(connectedWifiEntry.getKey());
            if (longPressWifiEntryPreference == null || !(longPressWifiEntryPreference instanceof ConnectedWifiEntryPreference) || longPressWifiEntryPreference.getWifiEntry() != connectedWifiEntry) {
                connectedWifiPreferenceCategory.removeAll();
                final ConnectedWifiEntryPreference createConnectedWifiEntryPreference = createConnectedWifiEntryPreference(connectedWifiEntry);
                createConnectedWifiEntryPreference.setKey(connectedWifiEntry.getKey());
                createConnectedWifiEntryPreference.setOrder(0);
                createConnectedWifiEntryPreference.refresh();
                connectedWifiPreferenceCategory.addPreference(createConnectedWifiEntryPreference);
                createConnectedWifiEntryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda6
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$updateWifiEntryPreferences$6;
                        lambda$updateWifiEntryPreferences$6 = NetworkDashboardFragment.this.lambda$updateWifiEntryPreferences$6(connectedWifiEntry, createConnectedWifiEntryPreference, preference);
                        return lambda$updateWifiEntryPreferences$6;
                    }
                });
                createConnectedWifiEntryPreference.setOnGearClickListener(new ConnectedWifiEntryPreference.OnGearClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda7
                    @Override // com.android.settings.wifi.ConnectedWifiEntryPreference.OnGearClickListener
                    public final void onGearClick(ConnectedWifiEntryPreference connectedWifiEntryPreference) {
                        NetworkDashboardFragment.this.lambda$updateWifiEntryPreferences$7(createConnectedWifiEntryPreference, connectedWifiEntryPreference);
                    }
                });
                if (this.mClickedConnect) {
                    this.mClickedConnect = false;
                    scrollToPreference(connectedWifiPreferenceCategory);
                }
            }
        } else {
            connectedWifiPreferenceCategory.removeAll();
        }
        cacheRemoveAllPrefs(this.mWifiEntryPreferenceCategory);
        boolean z = false;
        int i = 0;
        for (final WifiEntry wifiEntry : this.mWifiPickerTracker.getWifiEntries()) {
            String key = wifiEntry.getKey();
            LongPressWifiEntryPreference longPressWifiEntryPreference2 = (LongPressWifiEntryPreference) getCachedPreference(key);
            if (longPressWifiEntryPreference2 != null) {
                if (longPressWifiEntryPreference2.getWifiEntry() == wifiEntry) {
                    longPressWifiEntryPreference2.setOrder(i);
                    i++;
                    z = true;
                } else {
                    removePreference(key);
                }
            }
            LongPressWifiEntryPreference createLongPressWifiEntryPreference = createLongPressWifiEntryPreference(wifiEntry);
            createLongPressWifiEntryPreference.setKey(wifiEntry.getKey());
            int i2 = i + 1;
            createLongPressWifiEntryPreference.setOrder(i);
            createLongPressWifiEntryPreference.refresh();
            if (wifiEntry.getHelpUriString() != null) {
                createLongPressWifiEntryPreference.setOnButtonClickListener(new WifiEntryPreference.OnButtonClickListener() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda8
                    @Override // com.android.settings.wifi.WifiEntryPreference.OnButtonClickListener
                    public final void onButtonClick(WifiEntryPreference wifiEntryPreference) {
                        NetworkDashboardFragment.this.lambda$updateWifiEntryPreferences$8(wifiEntry, wifiEntryPreference);
                    }
                });
            }
            Preference preference = (LongPressWifiEntryPreference) connectedWifiPreferenceCategory.findPreference(wifiEntry.getKey());
            if (preference != null) {
                connectedWifiPreferenceCategory.removePreference(preference);
            }
            if (this.mIsShowNetworkCategoryLabel && wifiEntry.isSaved() && !wifiEntry.getSsid().contains("<unknown ssid>")) {
                connectedWifiPreferenceCategory.setVisible(true);
                createLongPressWifiEntryPreference.setOrder(connectedWifiPreferenceCategory.getPreferenceCount());
                connectedWifiPreferenceCategory.addPreference(createLongPressWifiEntryPreference);
            } else {
                this.mWifiEntryPreferenceCategory.addPreference(createLongPressWifiEntryPreference);
            }
            z = true;
            i = i2;
        }
        removeCachedPrefs(this.mWifiEntryPreferenceCategory);
        if (!z) {
            setProgressBarVisible(true);
            Preference preference2 = new Preference(getPrefContext());
            preference2.setSelectable(false);
            preference2.setSummary(R$string.wifi_empty_list_wifi_on);
            preference2.setOrder(i);
            preference2.setKey("wifi_empty_list");
            this.mWifiEntryPreferenceCategory.addPreference(preference2);
            i++;
        } else {
            getView().postDelayed(this.mHideProgressBarRunnable, 1700L);
        }
        this.mAddWifiNetworkPreference.setOrder(i);
        this.mWifiEntryPreferenceCategory.addPreference(this.mAddWifiNetworkPreference);
        setAdditionalSettingsSummaries();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateWifiEntryPreferences$6(WifiEntry wifiEntry, ConnectedWifiEntryPreference connectedWifiEntryPreference, Preference preference) {
        if (wifiEntry.canSignIn()) {
            wifiEntry.signIn(null);
            return true;
        } else if (UniMobileNetworkUtils.isSubsidyShowing()) {
            return false;
        } else {
            launchNetworkDetailsFragment(connectedWifiEntryPreference);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$7(ConnectedWifiEntryPreference connectedWifiEntryPreference, ConnectedWifiEntryPreference connectedWifiEntryPreference2) {
        if (UniMobileNetworkUtils.isSubsidyShowing()) {
            return;
        }
        launchNetworkDetailsFragment(connectedWifiEntryPreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateWifiEntryPreferences$8(WifiEntry wifiEntry, WifiEntryPreference wifiEntryPreference) {
        openSubscriptionHelpPage(wifiEntry);
    }

    @Override // com.android.settings.network.InternetUpdater.InternetChangeListener
    public void onAirplaneModeChanged(boolean z) {
        updateAirplaneModeMsgPreference(z);
    }

    private void removeConnectedWifiEntryPreference() {
        PreferenceCategory connectedWifiPreferenceCategory = getConnectedWifiPreferenceCategory();
        connectedWifiPreferenceCategory.removeAll();
        connectedWifiPreferenceCategory.setVisible(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeWifiEntryPreference() {
        this.mWifiEntryPreferenceCategory.removeAll();
        this.mWifiEntryPreferenceCategory.setVisible(false);
    }

    private static boolean isVerboseLoggingEnabled() {
        return BaseWifiTracker.isVerboseLoggingEnabled();
    }

    protected void setProgressBarVisible(boolean z) {
        showPinnedHeader(z);
    }

    void setAdditionalSettingsSummaries() {
        if (UniMobileNetworkUtils.isSubsidyShowing()) {
            this.mSavedNetworksPreference.setVisible(false);
            return;
        }
        WifiPickerTracker wifiPickerTracker = this.mWifiPickerTracker;
        int numSavedNetworks = wifiPickerTracker == null ? 0 : wifiPickerTracker.getNumSavedNetworks();
        WifiPickerTracker wifiPickerTracker2 = this.mWifiPickerTracker;
        int numSavedSubscriptions = wifiPickerTracker2 == null ? 0 : wifiPickerTracker2.getNumSavedSubscriptions();
        if (numSavedNetworks + numSavedSubscriptions > 0) {
            this.mSavedNetworksPreference.setVisible(false);
            this.mSavedNetworksPreference.setSummary(getSavedNetworkSettingsSummaryText(numSavedNetworks, numSavedSubscriptions));
            return;
        }
        this.mSavedNetworksPreference.setVisible(false);
    }

    private void launchWifiScanningFragment() {
        new SubSettingLauncher(getContext()).setDestination(WifiScanningFragment.class.getName()).setSourceMetricsCategory(746).launch();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        setAdditionalSettingsSummaries();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
        if (isFinishingOrDestroyed()) {
            return;
        }
        setAdditionalSettingsSummaries();
    }

    /* loaded from: classes.dex */
    public class FirstWifiEntryPreference extends ConnectedWifiEntryPreference {
        public FirstWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment) {
            super(context, wifiEntry, fragment);
        }
    }

    PreferenceCategory getConnectedWifiPreferenceCategory() {
        if (this.mInternetUpdater.getInternetType() == 2) {
            this.mFirstWifiEntryPreferenceCategory.setVisible(false);
            this.mFirstWifiEntryPreferenceCategory.removeAll();
            return this.mConnectedWifiEntryPreferenceCategory;
        }
        this.mConnectedWifiEntryPreferenceCategory.setVisible(false);
        this.mConnectedWifiEntryPreferenceCategory.removeAll();
        return this.mFirstWifiEntryPreferenceCategory;
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        WifiPickerTracker wifiPickerTracker;
        boolean z = false;
        if (this.mIsWifiEntryListStale) {
            this.mIsWifiEntryListStale = false;
            updateWifiEntryPreferences();
        } else {
            updateWifiEntryPreferencesDelayed();
        }
        WifiPickerTracker wifiPickerTracker2 = this.mWifiPickerTracker;
        if (wifiPickerTracker2 != null && wifiPickerTracker2.getConnectedWifiEntry() != null) {
            z = true;
        }
        changeNextButtonState(z);
        if (this.mOpenSsid == null || (wifiPickerTracker = this.mWifiPickerTracker) == null) {
            return;
        }
        Optional<WifiEntry> findFirst = wifiPickerTracker.getWifiEntries().stream().filter(new Predicate() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onWifiEntriesChanged$9;
                lambda$onWifiEntriesChanged$9 = NetworkDashboardFragment.this.lambda$onWifiEntriesChanged$9((WifiEntry) obj);
                return lambda$onWifiEntriesChanged$9;
            }
        }).filter(new Predicate() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda10
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onWifiEntriesChanged$10;
                lambda$onWifiEntriesChanged$10 = NetworkDashboardFragment.lambda$onWifiEntriesChanged$10((WifiEntry) obj);
                return lambda$onWifiEntriesChanged$10;
            }
        }).filter(new Predicate() { // from class: com.android.settings.network.NetworkDashboardFragment$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$onWifiEntriesChanged$11;
                lambda$onWifiEntriesChanged$11 = NetworkDashboardFragment.lambda$onWifiEntriesChanged$11((WifiEntry) obj);
                return lambda$onWifiEntriesChanged$11;
            }
        }).findFirst();
        if (findFirst.isPresent()) {
            this.mOpenSsid = null;
            launchConfigNewNetworkFragment(findFirst.get());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onWifiEntriesChanged$9(WifiEntry wifiEntry) {
        return TextUtils.equals(this.mOpenSsid, wifiEntry.getSsid());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onWifiEntriesChanged$10(WifiEntry wifiEntry) {
        return (wifiEntry.getSecurity() == 0 || wifiEntry.getSecurity() == 4) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$onWifiEntriesChanged$11(WifiEntry wifiEntry) {
        return !wifiEntry.isSaved() || isDisabledByWrongPassword(wifiEntry);
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        this.mDialogWifiEntry = null;
        this.mDialogWifiEntryKey = null;
    }

    private String getSavedNetworkSettingsSummaryText(int i, int i2) {
        if (getResources() == null) {
            Log.w("NetworkDashboardFrag", "getSavedNetworkSettingsSummaryText shouldn't run if resource is not ready");
            return null;
        } else if (i2 == 0) {
            return getResources().getQuantityString(R$plurals.wifi_saved_access_points_summary, i, Integer.valueOf(i));
        } else {
            if (i == 0) {
                return getResources().getQuantityString(R$plurals.wifi_saved_passpoint_access_points_summary, i2, Integer.valueOf(i2));
            }
            int i3 = i + i2;
            return getResources().getQuantityString(R$plurals.wifi_saved_all_access_points_summary, i3, Integer.valueOf(i3));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWifiEntryPreferencesDelayed() {
        WifiPickerTracker wifiPickerTracker;
        Log.d("NetworkDashboardFrag", "updateWifiEntryPreferencesDelayed: " + this.isWifiShouldShow);
        if (getActivity() == null || (wifiPickerTracker = this.mWifiPickerTracker) == null || wifiPickerTracker.getWifiState() != 3) {
            return;
        }
        View view = getView();
        Handler handler = view.getHandler();
        if (handler == null || !handler.hasCallbacks(this.mUpdateWifiEntryPreferencesRunnable)) {
            setProgressBarVisible(true);
            view.postDelayed(this.mUpdateWifiEntryPreferencesRunnable, 300L);
        }
    }

    void changeNextButtonState(boolean z) {
        if (this.mEnableNextOnConnection && hasNextButton()) {
            getNextButton().setEnabled(z);
        }
    }

    private static boolean isDisabledByWrongPassword(WifiEntry wifiEntry) {
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus;
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        return (wifiConfiguration == null || (networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus()) == null || networkSelectionStatus.getNetworkSelectionStatus() == 0 || 8 != networkSelectionStatus.getNetworkSelectionDisableReason()) ? false : true;
    }

    void launchConfigNewNetworkFragment(WifiEntry wifiEntry) {
        if (this.mIsRestricted) {
            Log.e("NetworkDashboardFrag", "Can't configure Wi-Fi because NetworkProviderSettings is restricted.");
            EventLog.writeEvent(1397638484, "246301667", -1, "Fragment is restricted.");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(getContext()).setTitleText(wifiEntry.getTitle()).setDestination(ConfigureWifiEntryFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 3).launch();
    }

    private void updateAirplaneModeMsgPreference(boolean z) {
        Preference preference = this.mAirplaneModeMsgPreference;
        if (preference != null) {
            preference.setVisible(z);
        }
    }

    void launchNetworkDetailsFragment(LongPressWifiEntryPreference longPressWifiEntryPreference) {
        CharSequence text;
        WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
        Context context = getContext();
        if (FeatureFlagUtils.isEnabled(context, "settings_wifi_details_datausage_header")) {
            text = wifiEntry.getTitle();
        } else {
            text = context.getText(R$string.pref_title_network_details);
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        new SubSettingLauncher(context).setTitleText(text).setDestination(WifiNetworkDetailsFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    LongPressWifiEntryPreference createLongPressWifiEntryPreference(WifiEntry wifiEntry) {
        return new LongPressWifiEntryPreference(getPrefContext(), wifiEntry, this);
    }

    void openSubscriptionHelpPage(WifiEntry wifiEntry) {
        Intent helpIntent = getHelpIntent(getContext(), wifiEntry.getHelpUriString());
        if (helpIntent != null) {
            try {
                startActivityForResult(helpIntent, 4);
            } catch (ActivityNotFoundException unused) {
                Log.e("NetworkDashboardFrag", "Activity was not found for intent, " + helpIntent.toString());
            }
        }
    }

    Intent getHelpIntent(Context context, String str) {
        return HelpUtils.getHelpIntent(context, str, context.getClass().getName());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getFragment() != null) {
            preference.setOnPreferenceClickListener(null);
            return super.onPreferenceTreeClick(preference);
        } else if (preference instanceof LongPressWifiEntryPreference) {
            onSelectedWifiPreferenceClick((LongPressWifiEntryPreference) preference);
            return true;
        } else if (preference == this.mAddWifiNetworkPreference) {
            onAddNetworkPressed();
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    void onSelectedWifiPreferenceClick(LongPressWifiEntryPreference longPressWifiEntryPreference) {
        WifiEntry wifiEntry = longPressWifiEntryPreference.getWifiEntry();
        if (wifiEntry.shouldEditBeforeConnect()) {
            launchConfigNewNetworkFragment(wifiEntry);
        } else if (wifiEntry.canConnect()) {
            connect(wifiEntry, true, true);
        } else if (wifiEntry.isSaved()) {
            launchNetworkDetailsFragment(longPressWifiEntryPreference);
        }
    }

    private void onAddNetworkPressed() {
        launchAddNetworkFragment();
    }

    void connect(WifiEntry wifiEntry, boolean z, boolean z2) {
        this.mMetricsFeatureProvider.action(getActivity(), 135, wifiEntry.isSaved());
        wifiEntry.connect(new WifiEntryConnectCallback(wifiEntry, z, z2));
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onScan(WifiDialog2 wifiDialog2, String str) {
        startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(wifiDialog2.getContext(), str), 0);
    }

    private void forget(WifiEntry wifiEntry) {
        this.mMetricsFeatureProvider.action(getActivity(), 137, new Pair[0]);
        wifiEntry.forget(null);
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onForget(WifiDialog2 wifiDialog2) {
        forget(wifiDialog2.getWifiEntry());
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        if (hasWifiManager()) {
            int mode = wifiDialog2.getMode();
            WifiConfiguration config = wifiDialog2.getController().getConfig();
            WifiEntry wifiEntry = wifiDialog2.getWifiEntry();
            if (mode == 2) {
                if (config == null) {
                    Toast.makeText(getContext(), R$string.wifi_failed_save_message, 0).show();
                } else {
                    this.mWifiManager.save(config, this.mSaveListener);
                }
            } else if (mode == 1 || (mode == 0 && wifiEntry.canConnect())) {
                if (config == null) {
                    connect(wifiEntry, false, false);
                } else {
                    this.mWifiManager.connect(config, new WifiConnectActionListener());
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class WifiConnectActionListener implements WifiManager.ActionListener {
        private WifiConnectActionListener() {
        }

        public void onSuccess() {
            NetworkDashboardFragment.this.mClickedConnect = true;
        }

        public void onFailure(int i) {
            if (NetworkDashboardFragment.this.isFinishingOrDestroyed()) {
                return;
            }
            Toast.makeText(NetworkDashboardFragment.this.getContext(), R$string.wifi_failed_connect_message, 0).show();
        }
    }

    private void launchAddNetworkFragment() {
        new SubSettingLauncher(getContext()).setTitleRes(R$string.wifi_add_network).setDestination(AddNetworkFragment.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 2).launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WifiEntryConnectCallback implements WifiEntry.ConnectCallback {
        final WifiEntry mConnectWifiEntry;
        final boolean mEditIfNoConfig;
        final boolean mFullScreenEdit;

        WifiEntryConnectCallback(WifiEntry wifiEntry, boolean z, boolean z2) {
            this.mConnectWifiEntry = wifiEntry;
            this.mEditIfNoConfig = z;
            this.mFullScreenEdit = z2;
        }

        @Override // com.android.wifitrackerlib.WifiEntry.ConnectCallback
        public void onConnectResult(int i) {
            if (NetworkDashboardFragment.this.isFinishingOrDestroyed()) {
                return;
            }
            if (i == 0) {
                NetworkDashboardFragment.this.mClickedConnect = true;
            } else if (i == 1 && this.mEditIfNoConfig && this.mFullScreenEdit) {
                NetworkDashboardFragment.this.launchConfigNewNetworkFragment(this.mConnectWifiEntry);
            }
        }
    }
}
