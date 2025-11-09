package com.android.settings.development;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.development.BluetoothHwOffloadRebootDialog;
import com.android.settings.development.DevelopmentSettingsDashboardFragment;
import com.android.settings.development.autofill.AutofillCategoryController;
import com.android.settings.development.autofill.AutofillLoggingLevelPreferenceController;
import com.android.settings.development.autofill.AutofillResetOptionsPreferenceController;
import com.android.settings.development.bluetooth.AbstractBluetoothDialogPreferenceController;
import com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController;
import com.android.settings.development.bluetooth.BluetoothBitPerSampleDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothChannelModeDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothCodecDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothHDAudioPreferenceController;
import com.android.settings.development.bluetooth.BluetoothQualityDialogPreferenceController;
import com.android.settings.development.bluetooth.BluetoothSampleRateDialogPreferenceController;
import com.android.settings.development.qstile.DevelopmentTiles;
import com.android.settings.development.storage.SharedDataPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.development.SystemPropPoker;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DevelopmentSettingsDashboardFragment extends RestrictedDashboardFragment implements OnMainSwitchChangeListener, OemUnlockDialogHost, AdbDialogHost, AdbClearKeysDialogHost, LogPersistDialogHost, BluetoothHwOffloadRebootDialog.OnHwOffloadDialogListener, AbstractBluetoothPreferenceController.Callback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.development_settings) { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.5
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DevelopmentSettingsDashboardFragment.buildPreferenceControllers(context, null, null, null, null);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (((ActivityManager) context.getSystemService(ActivityManager.class)).isLowRamDevice()) {
                nonIndexableKeys.add("enable_non_resizable_multi_window");
            }
            return nonIndexableKeys;
        }
    };
    private String mAccessPort;
    private BluetoothA2dp mBluetoothA2dp;
    private final BluetoothA2dpConfigStore mBluetoothA2dpConfigStore;
    private final BroadcastReceiver mBluetoothA2dpReceiver;
    private final BluetoothProfile.ServiceListener mBluetoothA2dpServiceListener;
    public SwitchPreference mBspAccessPort;
    private final BroadcastReceiver mEnableAdbReceiver;
    private boolean mIsAvailable;
    private List<AbstractPreferenceController> mPreferenceControllers;
    private SettingsMainSwitchBar mSwitchBar;
    private DevelopmentSwitchBarController mSwitchBarController;
    private final Runnable mSystemPropertiesChanged;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DevSettingsDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 39;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    protected boolean shouldSkipForInitialSUW() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.development.DevelopmentSettingsDashboardFragment$4  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass4 implements Runnable {
        AnonymousClass4() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (this) {
                FragmentActivity activity = DevelopmentSettingsDashboardFragment.this.getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment$4$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            DevelopmentSettingsDashboardFragment.AnonymousClass4.this.lambda$run$0();
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            DevelopmentSettingsDashboardFragment.this.updatePreferenceStates();
        }
    }

    public DevelopmentSettingsDashboardFragment() {
        super("no_debugging_features");
        this.mBluetoothA2dpConfigStore = new BluetoothA2dpConfigStore();
        this.mIsAvailable = true;
        this.mPreferenceControllers = new ArrayList();
        this.mEnableAdbReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof AdbOnChangeListener) {
                        ((AdbOnChangeListener) abstractPreferenceController).onAdbSettingChanged();
                    }
                }
            }
        };
        this.mBluetoothA2dpReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("DevSettingsDashboard", "mBluetoothA2dpReceiver.onReceive intent=" + intent);
                if ("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED".equals(intent.getAction())) {
                    Log.d("DevSettingsDashboard", "Received BluetoothCodecStatus=" + intent.getParcelableExtra("android.bluetooth.extra.CODEC_STATUS"));
                    for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                        if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                            ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothCodecUpdated();
                        }
                    }
                }
            }
        };
        this.mBluetoothA2dpServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.development.DevelopmentSettingsDashboardFragment.3
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                synchronized (DevelopmentSettingsDashboardFragment.this.mBluetoothA2dpConfigStore) {
                    DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp = (BluetoothA2dp) bluetoothProfile;
                }
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                        ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceConnected(DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp);
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                synchronized (DevelopmentSettingsDashboardFragment.this.mBluetoothA2dpConfigStore) {
                    DevelopmentSettingsDashboardFragment.this.mBluetoothA2dp = null;
                }
                for (AbstractPreferenceController abstractPreferenceController : DevelopmentSettingsDashboardFragment.this.mPreferenceControllers) {
                    if (abstractPreferenceController instanceof BluetoothServiceConnectionListener) {
                        ((BluetoothServiceConnectionListener) abstractPreferenceController).onBluetoothServiceDisconnected();
                    }
                }
            }
        };
        this.mSystemPropertiesChanged = new AnonymousClass4();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SearchMenuController.init(this);
        if (Utils.isMonkeyRunning()) {
            getActivity().finish();
            return;
        }
        this.mBspAccessPort = (SwitchPreference) getPreferenceScreen().findPreference("bsp_accessport_log");
        StringBuilder sb = new StringBuilder();
        sb.append("  mBspAccessPort = ");
        sb.append(String.valueOf(this.mBspAccessPort == null));
        Log.d("fangli", sb.toString());
        if (this.mBspAccessPort != null) {
            updatemBspAccessPortSwitch();
        }
    }

    private void updatemBspAccessPortSwitch() {
        String str = SystemProperties.get("sys.fyt.serialdebug", "0");
        this.mAccessPort = str;
        if ("0".equals(str)) {
            SystemProperties.set("sys.fyt.debug.set", "debugoff");
            this.mAccessPort = "off";
        }
        Log.d("fangli", "   mAccessPort = " + this.mAccessPort);
        this.mBspAccessPort.setChecked(this.mAccessPort.equals("off") ^ true);
    }

    private void writeBspAccessPortSwitch() {
        Log.d("fangli", " mAccessPort =  " + this.mAccessPort);
        if (this.mAccessPort.equals("on")) {
            SystemProperties.set("sys.fyt.debug.set", "debugoff");
            this.mAccessPort = "off";
            Log.d("fangli", "  set sys.fyt.debug.set --> debugoff ");
        } else if (this.mAccessPort.equals("off")) {
            SystemProperties.set("sys.fyt.debug.set", "debugon");
            this.mAccessPort = "on";
            Log.d("fangli", "  set sys.fyt.debug.set --> debugon ");
        }
        Log.d("fangli", "  sys.fyt.debug.set value --> " + this.mAccessPort);
        this.mBspAccessPort.setChecked(this.mAccessPort.equals("on"));
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setIfOnlyAvailableForAdmins(true);
        if (isUiRestricted() || !WizardManagerHelper.isDeviceProvisioned(getActivity())) {
            this.mIsAvailable = false;
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R$string.development_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        SettingsMainSwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setTitle(getContext().getString(R$string.developer_options_main_switch_title));
        this.mSwitchBar.hide();
        this.mSwitchBarController = new DevelopmentSwitchBarController(this, this.mSwitchBar, this.mIsAvailable, getSettingsLifecycle());
        if (DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext())) {
            enableDeveloperOptions();
            handleQsTileLongPressActionIfAny();
            return;
        }
        disableDeveloperOptions();
    }

    private void handleQsTileLongPressActionIfAny() {
        Intent intent = getActivity().getIntent();
        if (intent == null || !TextUtils.equals("android.service.quicksettings.action.QS_TILE_PREFERENCES", intent.getAction())) {
            return;
        }
        Log.d("DevSettingsDashboard", "Developer options started from qstile long-press");
        ComponentName componentName = (ComponentName) intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME");
        if (componentName != null && DevelopmentTiles.WirelessDebugging.class.getName().equals(componentName.getClassName()) && ((WirelessDebuggingPreferenceController) getDevelopmentOptionsController(WirelessDebuggingPreferenceController.class)).isAvailable()) {
            Log.d("DevSettingsDashboard", "Long press from wireless debugging qstile");
            new SubSettingLauncher(getContext()).setDestination(WirelessDebuggingFragment.class.getName()).setSourceMetricsCategory(1831).launch();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        registerReceivers();
        SystemProperties.addChangeCallback(this.mSystemPropertiesChanged);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(getActivity(), this.mBluetoothA2dpServiceListener, 2);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        unregisterReceivers();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.closeProfileProxy(2, this.mBluetoothA2dp);
            this.mBluetoothA2dp = null;
        }
        SystemProperties.removeChangeCallback(this.mSystemPropertiesChanged);
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        Log.d("fangli", "switchView===" + r3.getId() + z);
        if (r3 == this.mSwitchBar.getSwitch() && z != DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(getContext())) {
            if (z) {
                EnableDevelopmentSettingWarningDialog.show(this);
                return;
            }
            BluetoothA2dpHwOffloadPreferenceController bluetoothA2dpHwOffloadPreferenceController = (BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class);
            BluetoothLeAudioHwOffloadPreferenceController bluetoothLeAudioHwOffloadPreferenceController = (BluetoothLeAudioHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothLeAudioHwOffloadPreferenceController.class);
            if ((bluetoothA2dpHwOffloadPreferenceController == null || bluetoothA2dpHwOffloadPreferenceController.isDefaultValue()) && (bluetoothLeAudioHwOffloadPreferenceController == null || bluetoothLeAudioHwOffloadPreferenceController.isDefaultValue())) {
                disableDeveloperOptions();
            } else {
                DisableDevSettingsDialogFragment.show(this);
            }
        }
    }

    @Override // com.android.settings.development.OemUnlockDialogHost
    public void onOemUnlockDialogConfirmed() {
        ((OemUnlockPreferenceController) getDevelopmentOptionsController(OemUnlockPreferenceController.class)).onOemUnlockConfirmed();
    }

    @Override // com.android.settings.development.OemUnlockDialogHost
    public void onOemUnlockDialogDismissed() {
        ((OemUnlockPreferenceController) getDevelopmentOptionsController(OemUnlockPreferenceController.class)).onOemUnlockDismissed();
    }

    @Override // com.android.settings.development.AdbDialogHost
    public void onEnableAdbDialogConfirmed() {
        ((AdbPreferenceController) getDevelopmentOptionsController(AdbPreferenceController.class)).onAdbDialogConfirmed();
    }

    @Override // com.android.settings.development.AdbDialogHost
    public void onEnableAdbDialogDismissed() {
        ((AdbPreferenceController) getDevelopmentOptionsController(AdbPreferenceController.class)).onAdbDialogDismissed();
    }

    @Override // com.android.settings.development.AdbClearKeysDialogHost
    public void onAdbClearKeysDialogConfirmed() {
        ((ClearAdbKeysPreferenceController) getDevelopmentOptionsController(ClearAdbKeysPreferenceController.class)).onClearAdbKeysConfirmed();
    }

    @Override // com.android.settings.development.LogPersistDialogHost
    public void onDisableLogPersistDialogConfirmed() {
        ((LogPersistPreferenceController) getDevelopmentOptionsController(LogPersistPreferenceController.class)).onDisableLogPersistDialogConfirmed();
    }

    @Override // com.android.settings.development.LogPersistDialogHost
    public void onDisableLogPersistDialogRejected() {
        ((LogPersistPreferenceController) getDevelopmentOptionsController(LogPersistPreferenceController.class)).onDisableLogPersistDialogRejected();
    }

    @Override // com.android.settings.development.BluetoothHwOffloadRebootDialog.OnHwOffloadDialogListener
    public void onHwOffloadDialogConfirmed() {
        ((BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class)).onHwOffloadDialogConfirmed();
        ((BluetoothLeAudioHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothLeAudioHwOffloadPreferenceController.class)).onHwOffloadDialogConfirmed();
    }

    @Override // com.android.settings.development.BluetoothHwOffloadRebootDialog.OnHwOffloadDialogListener
    public void onHwOffloadDialogCanceled() {
        ((BluetoothA2dpHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothA2dpHwOffloadPreferenceController.class)).onHwOffloadDialogCanceled();
        ((BluetoothLeAudioHwOffloadPreferenceController) getDevelopmentOptionsController(BluetoothLeAudioHwOffloadPreferenceController.class)).onHwOffloadDialogCanceled();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        boolean z = false;
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof OnActivityResultListener) {
                z |= ((OnActivityResultListener) abstractPreferenceController).onActivityResult(i, i2, intent);
            }
        }
        if (z) {
            return;
        }
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return Utils.isMonkeyRunning() ? R$xml.placeholder_prefs : R$xml.development_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (Utils.isMonkeyRunning()) {
            this.mPreferenceControllers = new ArrayList();
            return null;
        }
        List<AbstractPreferenceController> buildPreferenceControllers = buildPreferenceControllers(context, getActivity(), getSettingsLifecycle(), this, new BluetoothA2dpConfigStore());
        this.mPreferenceControllers = buildPreferenceControllers;
        return buildPreferenceControllers;
    }

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(this.mEnableAdbReceiver, new IntentFilter("com.android.settingslib.development.AbstractEnableAdbController.ENABLE_ADB_STATE_CHANGED"));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.a2dp.profile.action.CODEC_CONFIG_CHANGED");
        getActivity().registerReceiver(this.mBluetoothA2dpReceiver, intentFilter);
    }

    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.mEnableAdbReceiver);
        getActivity().unregisterReceiver(this.mBluetoothA2dpReceiver);
    }

    private void enableDeveloperOptions() {
        if (Utils.isMonkeyRunning()) {
            return;
        }
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), true);
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof DeveloperOptionsPreferenceController) {
                ((DeveloperOptionsPreferenceController) abstractPreferenceController).onDeveloperOptionsEnabled();
            }
        }
    }

    private void disableDeveloperOptions() {
        if (Utils.isMonkeyRunning()) {
            return;
        }
        DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(getContext(), false);
        SystemPropPoker systemPropPoker = SystemPropPoker.getInstance();
        systemPropPoker.blockPokes();
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof DeveloperOptionsPreferenceController) {
                ((DeveloperOptionsPreferenceController) abstractPreferenceController).onDeveloperOptionsDisabled();
            }
        }
        systemPropPoker.unblockPokes();
        systemPropPoker.poke();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onEnableDevelopmentOptionsConfirmed() {
        enableDeveloperOptions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onEnableDevelopmentOptionsRejected() {
        this.mSwitchBar.setChecked(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDisableDevelopmentOptionsConfirmed() {
        disableDeveloperOptions();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDisableDevelopmentOptionsRejected() {
        this.mSwitchBar.setChecked(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Activity activity, Lifecycle lifecycle, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment, BluetoothA2dpConfigStore bluetoothA2dpConfigStore) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MemoryUsagePreferenceController(context));
        arrayList.add(new BugReportPreferenceController(context));
        arrayList.add(new BugReportHandlerPreferenceController(context));
        arrayList.add(new SystemServerHeapDumpPreferenceController(context));
        arrayList.add(new RebootWithMtePreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new LocalBackupPasswordPreferenceController(context));
        arrayList.add(new StayAwakePreferenceController(context, lifecycle));
        arrayList.add(new UsbModeSwitchPreferenceController(context, lifecycle));
        arrayList.add(new HdcpCheckingPreferenceController(context));
        arrayList.add(new BluetoothSnoopLogPreferenceController(context));
        arrayList.add(new OemUnlockPreferenceController(context, activity, developmentSettingsDashboardFragment));
        arrayList.add(new PictureColorModePreferenceController(context, lifecycle));
        arrayList.add(new WebViewAppPreferenceController(context));
        arrayList.add(new CoolColorTemperaturePreferenceController(context));
        arrayList.add(new DisableAutomaticUpdatesPreferenceController(context));
        arrayList.add(new SelectDSUPreferenceController(context));
        arrayList.add(new AdbPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new ClearAdbKeysPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new WirelessDebuggingPreferenceController(context, lifecycle));
        arrayList.add(new AdbAuthorizationTimeoutPreferenceController(context));
        arrayList.add(new LocalTerminalPreferenceController(context));
        arrayList.add(new BugReportInPowerPreferenceController(context));
        arrayList.add(new AutomaticSystemServerHeapDumpPreferenceController(context));
        arrayList.add(new MockLocationAppPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new MockModemPreferenceController(context));
        arrayList.add(new DebugViewAttributesPreferenceController(context));
        arrayList.add(new SelectDebugAppPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new WaitForDebuggerPreferenceController(context));
        arrayList.add(new EnableGpuDebugLayersPreferenceController(context));
        arrayList.add(new ForcePeakRefreshRatePreferenceController(context));
        arrayList.add(new EnableVerboseVendorLoggingPreferenceController(context));
        arrayList.add(new VerifyAppsOverUsbPreferenceController(context));
        arrayList.add(new ArtVerifierPreferenceController(context));
        arrayList.add(new LogdSizePreferenceController(context));
        arrayList.add(new LogPersistPreferenceController(context, developmentSettingsDashboardFragment, lifecycle));
        arrayList.add(new CameraLaserSensorPreferenceController(context));
        arrayList.add(new WifiDisplayCertificationPreferenceController(context));
        arrayList.add(new WifiVerboseLoggingPreferenceController(context));
        arrayList.add(new WifiScanThrottlingPreferenceController(context));
        arrayList.add(new WifiNonPersistentMacRandomizationPreferenceController(context));
        arrayList.add(new MobileDataAlwaysOnPreferenceController(context));
        arrayList.add(new TetheringHardwareAccelPreferenceController(context));
        arrayList.add(new BluetoothDeviceNoNamePreferenceController(context));
        arrayList.add(new BluetoothAbsoluteVolumePreferenceController(context));
        arrayList.add(new BluetoothAvrcpVersionPreferenceController(context));
        arrayList.add(new BluetoothMapVersionPreferenceController(context));
        arrayList.add(new BluetoothA2dpHwOffloadPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new BluetoothLeAudioHwOffloadPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new BluetoothMaxConnectedAudioDevicesPreferenceController(context));
        arrayList.add(new NfcStackDebugLogPreferenceController(context));
        arrayList.add(new ShowTapsPreferenceController(context));
        arrayList.add(new PointerLocationPreferenceController(context));
        arrayList.add(new ShowSurfaceUpdatesPreferenceController(context));
        arrayList.add(new ShowLayoutBoundsPreferenceController(context));
        arrayList.add(new ShowRefreshRatePreferenceController(context));
        arrayList.add(new RtlLayoutPreferenceController(context));
        arrayList.add(new WindowAnimationScalePreferenceController(context));
        arrayList.add(new EmulateDisplayCutoutPreferenceController(context));
        arrayList.add(new TransitionAnimationScalePreferenceController(context));
        arrayList.add(new AnimatorDurationScalePreferenceController(context));
        arrayList.add(new SecondaryDisplayPreferenceController(context));
        arrayList.add(new GpuViewUpdatesPreferenceController(context));
        arrayList.add(new HardwareLayersUpdatesPreferenceController(context));
        arrayList.add(new DebugGpuOverdrawPreferenceController(context));
        arrayList.add(new DebugNonRectClipOperationsPreferenceController(context));
        arrayList.add(new ForceDarkPreferenceController(context));
        arrayList.add(new EnableBlursPreferenceController(context));
        arrayList.add(new ForceMSAAPreferenceController(context));
        arrayList.add(new HardwareOverlaysPreferenceController(context));
        arrayList.add(new SimulateColorSpacePreferenceController(context));
        arrayList.add(new UsbAudioRoutingPreferenceController(context));
        arrayList.add(new StrictModePreferenceController(context));
        arrayList.add(new ProfileGpuRenderingPreferenceController(context));
        arrayList.add(new KeepActivitiesPreferenceController(context));
        arrayList.add(new BackgroundProcessLimitPreferenceController(context));
        arrayList.add(new CachedAppsFreezerPreferenceController(context));
        arrayList.add(new ShowFirstCrashDialogPreferenceController(context));
        arrayList.add(new AppsNotRespondingPreferenceController(context));
        arrayList.add(new NotificationChannelWarningsPreferenceController(context));
        arrayList.add(new AllowAppsOnExternalPreferenceController(context));
        arrayList.add(new ResizableActivityPreferenceController(context));
        arrayList.add(new FreeformWindowsPreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new DesktopModePreferenceController(context, developmentSettingsDashboardFragment));
        arrayList.add(new NonResizableMultiWindowPreferenceController(context));
        arrayList.add(new ShortcutManagerThrottlingPreferenceController(context));
        arrayList.add(new EnableGnssRawMeasFullTrackingPreferenceController(context));
        arrayList.add(new DefaultLaunchPreferenceController(context, "running_apps"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "demo_mode"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "quick_settings_tiles"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "feature_flags_dashboard"));
        arrayList.add(new DefaultUsbConfigurationPreferenceController(context));
        arrayList.add(new DefaultLaunchPreferenceController(context, "density"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "background_check"));
        arrayList.add(new DefaultLaunchPreferenceController(context, "inactive_apps"));
        arrayList.add(new AutofillCategoryController(context, lifecycle));
        arrayList.add(new AutofillLoggingLevelPreferenceController(context, lifecycle));
        arrayList.add(new AutofillResetOptionsPreferenceController(context));
        arrayList.add(new BluetoothCodecDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore, developmentSettingsDashboardFragment));
        arrayList.add(new BluetoothSampleRateDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothBitPerSampleDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothQualityDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothChannelModeDialogPreferenceController(context, lifecycle, bluetoothA2dpConfigStore));
        arrayList.add(new BluetoothHDAudioPreferenceController(context, lifecycle, bluetoothA2dpConfigStore, developmentSettingsDashboardFragment));
        arrayList.add(new SharedDataPreferenceController(context));
        arrayList.add(new OverlaySettingsPreferenceController(context));
        arrayList.add(new StylusHandwritingPreferenceController(context));
        arrayList.add(new IngressRateLimitPreferenceController(context));
        arrayList.add(new BackAnimationPreferenceController(context, developmentSettingsDashboardFragment));
        return arrayList;
    }

    <T extends AbstractPreferenceController> T getDevelopmentOptionsController(Class<T> cls) {
        return (T) use(cls);
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController.Callback
    public void onBluetoothCodecChanged() {
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if ((abstractPreferenceController instanceof AbstractBluetoothDialogPreferenceController) && !(abstractPreferenceController instanceof BluetoothCodecDialogPreferenceController)) {
                ((AbstractBluetoothDialogPreferenceController) abstractPreferenceController).onBluetoothCodecUpdated();
            }
        }
    }

    @Override // com.android.settings.development.bluetooth.AbstractBluetoothPreferenceController.Callback
    public void onBluetoothHDAudioEnabled(boolean z) {
        for (AbstractPreferenceController abstractPreferenceController : this.mPreferenceControllers) {
            if (abstractPreferenceController instanceof AbstractBluetoothDialogPreferenceController) {
                ((AbstractBluetoothDialogPreferenceController) abstractPreferenceController).onHDAudioEnabled(z);
            }
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (Utils.isMonkeyRunning()) {
            return false;
        }
        if (preference == this.mBspAccessPort) {
            writeBspAccessPortSwitch();
            return false;
        }
        return super.onPreferenceTreeClick(preference);
    }
}
