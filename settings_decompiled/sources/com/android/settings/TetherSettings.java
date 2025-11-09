package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.NetworkAdapterManager;
import android.net.TetheringManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.tether.WifiTetherPreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.TetherUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class TetherSettings extends RestrictedSettingsFragment implements DataSaverBackend.Listener {
    static final String KEY_ENABLE_BLUETOOTH_TETHERING = "enable_bluetooth_tethering";
    static final String KEY_TETHER_PREFS_SCREEN = "tether_prefs_screen";
    static final String KEY_TETHER_PREFS_TOP_INTRO = "tether_prefs_top_intro";
    static final String KEY_USB_TETHER_SETTINGS = "usb_tether_settings";
    static final String KEY_WIFI_TETHER = "wifi_tether";
    private boolean isBluetoothTethered;
    private boolean isEthernetTethered;
    private boolean isUsbTethered;
    private final HashSet<String> mAvailableInterfaces;
    private boolean mBluetoothEnableForTether;
    private AtomicReference<BluetoothPan> mBluetoothPan;
    private String[] mBluetoothRegexs;
    private SwitchPreference mBluetoothTether;
    private ConnectivityManager mCm;
    Context mContext;
    private DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    private Preference mDataSaverFooter;
    private EthernetManager mEm;
    private EthernetListener mEthernetListener;
    private SwitchPreference mEthernetTether;
    private Handler mHandler;
    private boolean mMassStorageActive;
    private BluetoothProfile.ServiceListener mProfileServiceListener;
    private OnStartTetheringCallback mStartTetheringCallback;
    private BroadcastReceiver mTetherChangeReceiver;
    private TetheringEventCallback mTetheringEventCallback;
    TetheringManager mTm;
    private boolean mUnavailable;
    private Uri mUri;
    private boolean mUsbConnected;
    String[] mUsbRegexs;
    private RestrictedSwitchPreference mUsbTether;
    private WifiTetherPreferenceController mWifiTetherPreferenceController;
    private static final boolean DEBUG = Log.isLoggable("TetheringSettings", 3);
    private static final Object mLock = new Object();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.TetherSettings.2
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R$xml.tether_prefs;
            return Arrays.asList(searchIndexableResource);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !FeatureFlagUtils.isEnabled(context, "settings_tether_all_in_one");
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            TetheringManager tetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
            if (!TetherUtil.isTetherAvailable(context)) {
                nonIndexableKeys.add(TetherSettings.KEY_TETHER_PREFS_SCREEN);
            }
            if (!((tetheringManager.getTetherableWifiRegexs() == null || tetheringManager.getTetherableWifiRegexs().length == 0 || !context.getPackageManager().hasSystemFeature("android.hardware.wifi")) ? false : true) || Utils.isMonkeyRunning()) {
                nonIndexableKeys.add(TetherSettings.KEY_WIFI_TETHER);
            }
            if (!(tetheringManager.getTetherableUsbRegexs().length != 0) || Utils.isMonkeyRunning()) {
                nonIndexableKeys.add(TetherSettings.KEY_USB_TETHER_SETTINGS);
            }
            if (!(tetheringManager.getTetherableBluetoothRegexs().length != 0 && context.getPackageManager().hasSystemFeature("android.hardware.bluetooth"))) {
                nonIndexableKeys.add(TetherSettings.KEY_ENABLE_BLUETOOTH_TETHERING);
            }
            if (!(((EthernetManager) context.getSystemService(EthernetManager.class)) != null)) {
                nonIndexableKeys.add("enable_ethernet_tethering");
            }
            return nonIndexableKeys;
        }
    };

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

    public TetherSettings() {
        super("no_config_tethering");
        this.mUri = Settings.Global.getUriFor("airplane_mode_on");
        this.mBluetoothPan = new AtomicReference<>();
        this.mHandler = new Handler();
        this.mAvailableInterfaces = new HashSet<>();
        this.mProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.TetherSettings.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                TetherSettings.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                TetherSettings.this.mBluetoothPan.set(null);
            }
        };
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mWifiTetherPreferenceController = new WifiTetherPreferenceController(context, getSettingsLifecycle());
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.tether_prefs);
        Context context = getContext();
        this.mContext = context;
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
        this.mDataSaverFooter = findPreference("disabled_on_data_saver");
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted()) {
            this.mUnavailable = true;
            getPreferenceScreen().removeAll();
            return;
        }
        FragmentActivity activity = getActivity();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(activity.getApplicationContext(), this.mProfileServiceListener, 5);
        }
        setupTetherPreference();
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
            getPreferenceScreen().removePreference(findPreference(KEY_TETHER_PREFS_TOP_INTRO));
            activity.setTitle(this.mContext.getString(R$string.tether_settings_title_usb_bluetooth));
        } else {
            setTopIntroPreferenceTitle();
        }
        this.mDataSaverBackend.addListener(this);
        this.mCm = (ConnectivityManager) getSystemService("connectivity");
        this.mTm = (TetheringManager) getSystemService("tethering");
        this.mEm = (EthernetManager) this.mContext.getSystemService(EthernetManager.class);
        this.mUsbRegexs = this.mTm.getTetherableUsbRegexs();
        this.mBluetoothRegexs = this.mTm.getTetherableBluetoothRegexs();
        createObserver(this.mUri);
        boolean z = this.mUsbRegexs.length != 0;
        boolean z2 = (defaultAdapter == null || this.mBluetoothRegexs.length == 0 || !activity.getPackageManager().hasSystemFeature("android.hardware.bluetooth")) ? false : true;
        boolean z3 = this.mEm != null;
        if (!z || Utils.isMonkeyRunning()) {
            getPreferenceScreen().removePreference(this.mUsbTether);
        }
        this.mWifiTetherPreferenceController.displayPreference(getPreferenceScreen());
        if (!z2) {
            getPreferenceScreen().removePreference(this.mBluetoothTether);
        } else {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            if (bluetoothPan != null && bluetoothPan.isTetheringOn()) {
                this.mBluetoothTether.setChecked(true);
            } else {
                this.mBluetoothTether.setChecked(false);
            }
        }
        if (!z3) {
            getPreferenceScreen().removePreference(this.mEthernetTether);
        }
        onDataSaverChanged(this.mDataSaverBackend.isDataSaverEnabled());
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mDataSaverBackend.remListener(this);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothProfile bluetoothProfile = (BluetoothProfile) this.mBluetoothPan.getAndSet(null);
        if (bluetoothProfile != null && defaultAdapter != null) {
            defaultAdapter.closeProfileProxy(5, bluetoothProfile);
        }
        super.onDestroy();
    }

    void setupTetherPreference() {
        this.mUsbTether = (RestrictedSwitchPreference) findPreference(KEY_USB_TETHER_SETTINGS);
        this.mBluetoothTether = (SwitchPreference) findPreference(KEY_ENABLE_BLUETOOTH_TETHERING);
        this.mEthernetTether = (SwitchPreference) findPreference("enable_ethernet_tethering");
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
        if (!this.mUsbConnected && !z) {
            this.mUsbTether.setEnabled(z);
        } else {
            this.mUsbTether.setEnabled(!z);
        }
        this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
        this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
        this.mDataSaverFooter.setVisible(this.mDataSaverEnabled);
    }

    void setTopIntroPreferenceTitle() {
        Preference findPreference = findPreference(KEY_TETHER_PREFS_TOP_INTRO);
        if (((WifiManager) this.mContext.getSystemService(WifiManager.class)).isStaApConcurrencySupported()) {
            findPreference.setTitle(R$string.tethering_footer_info_sta_ap_concurrency);
        } else {
            findPreference.setTitle(R$string.tethering_footer_info);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TetherChangeReceiver extends BroadcastReceiver {
        private TetherChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TetherSettings.DEBUG) {
                Log.d("TetheringSettings", "onReceive() action : " + action);
            }
            if (action.equals("android.net.conn.TETHER_STATE_CHANGED")) {
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra("availableArray");
                ArrayList<String> stringArrayListExtra2 = intent.getStringArrayListExtra("tetherArray");
                TetherSettings.this.updateBluetoothState();
                TetherSettings.this.updateEthernetState((String[]) stringArrayListExtra.toArray(new String[stringArrayListExtra.size()]), (String[]) stringArrayListExtra2.toArray(new String[stringArrayListExtra2.size()]));
            } else if (action.equals("android.intent.action.MEDIA_SHARED")) {
                TetherSettings.this.mMassStorageActive = true;
                TetherSettings.this.updateBluetoothAndEthernetState();
                TetherSettings.this.updateUsbPreference();
            } else if (action.equals("android.intent.action.MEDIA_UNSHARED")) {
                TetherSettings.this.mMassStorageActive = false;
                TetherSettings.this.updateBluetoothAndEthernetState();
                TetherSettings.this.updateUsbPreference();
            } else if (action.equals("android.hardware.usb.action.USB_STATE")) {
                TetherSettings.this.mUsbConnected = intent.getBooleanExtra("connected", false);
                TetherSettings.this.updateBluetoothAndEthernetState();
                TetherSettings.this.updateUsbPreference();
            } else if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                if (TetherSettings.this.mBluetoothEnableForTether) {
                    int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                    if (intExtra == Integer.MIN_VALUE || intExtra == 10) {
                        TetherSettings.this.mBluetoothEnableForTether = false;
                    } else if (intExtra == 12) {
                        TetherSettings.this.startTethering(2);
                        TetherSettings.this.mBluetoothEnableForTether = false;
                    }
                }
                TetherSettings.this.updateBluetoothAndEthernetState();
            } else if (action.equals("android.bluetooth.action.TETHERING_STATE_CHANGED")) {
                TetherSettings.this.updateBluetoothAndEthernetState();
            } else if (action.equals("android.net.action.ETH_DEVICE_ENABLED") && !TetherSettings.this.mAvailableInterfaces.contains("eth0")) {
                Log.e("TetheringSettings", "ETH_DEVICE_ENABLED then updateEthernetState");
                TetherSettings.this.mAvailableInterfaces.add("eth0");
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED") && TetherSettings.this.mAvailableInterfaces.contains("eth0")) {
                Log.e("TetheringSettings", "USB_DEVICE_DETACHED then updateEthernetState");
                TetherSettings.this.mAvailableInterfaces.remove("eth0");
            }
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (this.mUnavailable) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R$string.tethering_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        this.mStartTetheringCallback = new OnStartTetheringCallback(this);
        this.mTetheringEventCallback = new TetheringEventCallback();
        this.mTm.registerTetheringEventCallback(new Executor() { // from class: com.android.settings.TetherSettings$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                TetherSettings.this.lambda$onStart$0(runnable);
            }
        }, this.mTetheringEventCallback);
        this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
        registerReceiver();
        this.mEthernetListener = new EthernetListener();
        EthernetManager ethernetManager = this.mEm;
        if (ethernetManager != null) {
            ethernetManager.addInterfaceStateListener(new Executor() { // from class: com.android.settings.TetherSettings$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    TetherSettings.this.lambda$onStart$1(runnable);
                }
            }, this.mEthernetListener);
        }
        updateUsbState();
        updateBluetoothAndEthernetState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$1(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.mUnavailable) {
            return;
        }
        getActivity().unregisterReceiver(this.mTetherChangeReceiver);
        this.mTm.unregisterTetheringEventCallback(this.mTetheringEventCallback);
        EthernetManager ethernetManager = this.mEm;
        if (ethernetManager != null) {
            ethernetManager.removeInterfaceStateListener(this.mEthernetListener);
        }
        this.mTetherChangeReceiver = null;
        this.mStartTetheringCallback = null;
        this.mTetheringEventCallback = null;
    }

    void registerReceiver() {
        FragmentActivity activity = getActivity();
        this.mTetherChangeReceiver = new TetherChangeReceiver();
        Intent registerReceiver = activity.registerReceiver(this.mTetherChangeReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter2.addAction("android.intent.action.MEDIA_UNSHARED");
        intentFilter2.addDataScheme("file");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter3.addAction("android.bluetooth.action.TETHERING_STATE_CHANGED");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter3);
        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("android.net.action.ETH_DEVICE_ENABLED");
        intentFilter4.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        activity.registerReceiver(this.mTetherChangeReceiver, intentFilter4);
        if (registerReceiver != null) {
            this.mTetherChangeReceiver.onReceive(activity, registerReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothAndEthernetState() {
        updateBluetoothAndEthernetState(this.mTm.getTetheredIfaces());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothAndEthernetState(String[] strArr) {
        String[] tetherableIfaces = this.mTm.getTetherableIfaces();
        updateBluetoothState();
        updateEthernetState(tetherableIfaces, strArr);
    }

    private void updateUsbState() {
        updateUsbState(this.mTm.getTetheredIfaces());
    }

    void updateUsbState(String[] strArr) {
        boolean z = false;
        for (String str : strArr) {
            for (String str2 : this.mUsbRegexs) {
                if (str.matches(str2)) {
                    z = true;
                }
            }
        }
        Log.e("TetheringSettings", "updateUsbState() mUsbConnected : " + this.mUsbConnected + ", mMassStorageActive : " + this.mMassStorageActive + ", usbTethered : " + z);
        if (z) {
            this.mUsbTether.setEnabled(!this.mDataSaverEnabled);
            this.mUsbTether.setChecked(true);
            RestrictedLockUtils.EnforcedAdmin checkIfUsbDataSignalingIsDisabled = RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(this.mContext, UserHandle.myUserId());
            if (checkIfUsbDataSignalingIsDisabled != null) {
                this.mUsbTether.setDisabledByAdmin(checkIfUsbDataSignalingIsDisabled);
            }
        } else {
            this.mUsbTether.setChecked(false);
            updateUsbPreference();
        }
        synchronized (mLock) {
            this.isUsbTethered = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUsbPreference() {
        boolean z = this.mUsbConnected && !this.mMassStorageActive;
        RestrictedLockUtils.EnforcedAdmin checkIfUsbDataSignalingIsDisabled = RestrictedLockUtilsInternal.checkIfUsbDataSignalingIsDisabled(this.mContext, UserHandle.myUserId());
        if (checkIfUsbDataSignalingIsDisabled != null) {
            this.mUsbTether.setDisabledByAdmin(checkIfUsbDataSignalingIsDisabled);
        } else if (z) {
            this.mUsbTether.setEnabled(!this.mDataSaverEnabled);
        } else {
            this.mUsbTether.setEnabled(false);
        }
    }

    int getBluetoothState() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            return Integer.MIN_VALUE;
        }
        return defaultAdapter.getState();
    }

    boolean isBluetoothTetheringOn() {
        BluetoothPan bluetoothPan = this.mBluetoothPan.get();
        return bluetoothPan != null && bluetoothPan.isTetheringOn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBluetoothState() {
        int bluetoothState = getBluetoothState();
        if (DEBUG) {
            Log.d("TetheringSettings", "updateBluetoothState() btState : " + bluetoothState);
        }
        if (bluetoothState == Integer.MIN_VALUE) {
            Log.w("TetheringSettings", "updateBluetoothState() Bluetooth state is error!");
        } else if (bluetoothState == 13) {
            this.mBluetoothTether.setEnabled(false);
        } else if (bluetoothState == 11) {
            this.mBluetoothTether.setEnabled(false);
        } else if (bluetoothState == 12 && isBluetoothTetheringOn()) {
            this.mBluetoothTether.setChecked(true);
            this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
            synchronized (mLock) {
                this.isBluetoothTethered = true;
            }
        } else {
            this.mBluetoothTether.setEnabled(!this.mDataSaverEnabled);
            this.mBluetoothTether.setChecked(false);
            synchronized (mLock) {
                this.isBluetoothTethered = false;
            }
        }
    }

    void updateEthernetState(String[] strArr, String[] strArr2) {
        boolean z = false;
        for (String str : strArr) {
            if (str.matches("eth\\d") || this.mAvailableInterfaces.contains(str) || this.mAvailableInterfaces.contains("eth_off_on")) {
                z = true;
            }
        }
        boolean z2 = false;
        for (String str2 : strArr2) {
            if (str2.matches("eth\\d") || this.mAvailableInterfaces.contains(str2)) {
                z2 = true;
            }
        }
        Log.e("TetheringSettings", "updateEthernetState() isAvailable : " + z + ", isTethered : " + z2);
        if (z2) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(true);
        } else if (z || this.mAvailableInterfaces.size() > 0) {
            this.mEthernetTether.setEnabled(!this.mDataSaverEnabled);
            this.mEthernetTether.setChecked(false);
        } else {
            this.mEthernetTether.setEnabled(false);
            this.mEthernetTether.setChecked(false);
        }
        synchronized (mLock) {
            this.isEthernetTethered = z2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startTethering(int i) {
        if (i == 2) {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.getState() == 10) {
                this.mBluetoothEnableForTether = true;
                defaultAdapter.enable();
                this.mBluetoothTether.setEnabled(false);
                return;
            }
        }
        this.mCm.startTethering(i, true, this.mStartTetheringCallback, this.mHandler);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        RestrictedSwitchPreference restrictedSwitchPreference = this.mUsbTether;
        if (preference == restrictedSwitchPreference) {
            if (restrictedSwitchPreference.isChecked()) {
                startTethering(1);
            } else {
                this.mCm.stopTethering(1);
            }
        } else {
            SwitchPreference switchPreference = this.mBluetoothTether;
            if (preference == switchPreference) {
                if (switchPreference.isChecked()) {
                    startTethering(2);
                } else {
                    this.mCm.stopTethering(2);
                }
            } else {
                SwitchPreference switchPreference2 = this.mEthernetTether;
                if (preference == switchPreference2) {
                    if (switchPreference2.isChecked()) {
                        startTethering(5);
                    } else {
                        this.mCm.stopTethering(5);
                    }
                }
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_tether;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        final WeakReference<TetherSettings> mTetherSettings;

        OnStartTetheringCallback(TetherSettings tetherSettings) {
            this.mTetherSettings = new WeakReference<>(tetherSettings);
        }

        public void onTetheringStarted() {
            update();
        }

        public void onTetheringFailed() {
            update();
        }

        private void update() {
            TetherSettings tetherSettings = this.mTetherSettings.get();
            if (tetherSettings != null) {
                tetherSettings.updateBluetoothAndEthernetState();
            }
        }
    }

    /* loaded from: classes.dex */
    private final class TetheringEventCallback implements TetheringManager.TetheringEventCallback {
        private TetheringEventCallback() {
        }

        public void onTetheredInterfacesChanged(List<String> list) {
            Log.d("TetheringSettings", "onTetheredInterfacesChanged() interfaces : " + list.toString());
            String[] strArr = (String[]) list.toArray(new String[list.size()]);
            TetherSettings.this.updateUsbState(strArr);
            TetherSettings.this.updateBluetoothAndEthernetState(strArr);
        }
    }

    /* loaded from: classes.dex */
    private final class EthernetListener implements EthernetManager.InterfaceStateListener {
        private EthernetListener() {
        }

        public void onInterfaceStateChanged(String str, int i, int i2, IpConfiguration ipConfiguration) {
            if (ipConfiguration != null) {
                Log.e("TetheringSettings", "onInterfaceStateChanged interfaces: " + str + ", state: " + i + ", role: " + i2 + ", IpConfiguration: " + ipConfiguration.toString());
            }
            if (i == 2) {
                TetherSettings.this.mAvailableInterfaces.add(str);
            } else {
                TetherSettings.this.mAvailableInterfaces.remove(str);
            }
            TetherSettings.this.updateBluetoothAndEthernetState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEthernetInterface(String str, boolean z) {
        Log.d("TetheringSettings", "set ethernet interface: " + str + " to " + z);
        NetworkAdapterManager networkAdapterManager = NetworkAdapterManager.getInstance();
        if (networkAdapterManager == null) {
            Log.d("TetheringSettings", "set ethernet interface failed, NetworkAdapterManager is null ");
        } else if (!z) {
            networkAdapterManager.setEthernetInterface(0);
            this.mAvailableInterfaces.add("eth_off_on");
        } else {
            networkAdapterManager.setEthernetInterface(1);
            this.mAvailableInterfaces.remove("eth_off_on");
        }
    }

    private ContentObserver createObserver(final Uri uri) {
        ContentObserver contentObserver = new ContentObserver(this.mHandler) { // from class: com.android.settings.TetherSettings.3
            boolean airplaneRecord;

            {
                this.airplaneRecord = Settings.Global.getInt(TetherSettings.this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri2) {
                synchronized (TetherSettings.mLock) {
                    if (uri.equals(uri2)) {
                        boolean z2 = Settings.Global.getInt(TetherSettings.this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
                        Log.d("TetheringSettings", "airplane mode changed: " + z2);
                        if (z2 && z2 != this.airplaneRecord) {
                            if (TetherSettings.this.isUsbTethered) {
                                TetherSettings.this.mCm.stopTethering(1);
                                TetherSettings.this.isUsbTethered = false;
                            }
                            if (TetherSettings.this.isBluetoothTethered) {
                                TetherSettings.this.mCm.stopTethering(2);
                                TetherSettings.this.isBluetoothTethered = false;
                            }
                            if (TetherSettings.this.isEthernetTethered) {
                                TetherSettings.this.mCm.stopTethering(5);
                                TetherSettings.this.isEthernetTethered = false;
                                TetherSettings.this.setEthernetInterface("eth0", false);
                                TetherSettings.this.setEthernetInterface("eth0", true);
                            }
                            this.airplaneRecord = z2;
                        }
                    }
                }
            }
        };
        getContext().getContentResolver().registerContentObserver(uri, true, contentObserver);
        return contentObserver;
    }
}
