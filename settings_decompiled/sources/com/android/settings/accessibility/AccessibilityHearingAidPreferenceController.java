package com.android.settings.accessibility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.bluetooth.BluetoothDeviceDetailsFragment;
import com.android.settings.bluetooth.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class AccessibilityHearingAidPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, BluetoothCallback {
    private static final String TAG = "AccessibilityHearingAidPreferenceController";
    private final BluetoothAdapter mBluetoothAdapter;
    private FragmentManager mFragmentManager;
    private final BroadcastReceiver mHearingAidChangedReceiver;
    private Preference mHearingAidPreference;
    private final LocalBluetoothManager mLocalBluetoothManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onAclConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onAudioModeChanged() {
        super.onAudioModeChanged();
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onBluetoothStateChanged(int i) {
        super.onBluetoothStateChanged(i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onDeviceAdded(cachedBluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onDeviceBondStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onDeviceDeleted(cachedBluetoothDevice);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        super.onProfileConnectionStateChanged(cachedBluetoothDevice, i, i2);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AccessibilityHearingAidPreferenceController(Context context, String str) {
        super(context, str);
        this.mHearingAidChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.accessibility.AccessibilityHearingAidPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                    if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) == 2) {
                        AccessibilityHearingAidPreferenceController accessibilityHearingAidPreferenceController = AccessibilityHearingAidPreferenceController.this;
                        accessibilityHearingAidPreferenceController.updateState(accessibilityHearingAidPreferenceController.mHearingAidPreference);
                        return;
                    }
                    AccessibilityHearingAidPreferenceController.this.mHearingAidPreference.setSummary(R$string.accessibility_hearingaid_not_connected_summary);
                } else if (!"android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction()) || intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) == 12) {
                } else {
                    AccessibilityHearingAidPreferenceController.this.mHearingAidPreference.setSummary(R$string.accessibility_hearingaid_not_connected_summary);
                }
            }
        };
        this.mLocalBluetoothManager = getLocalBluetoothManager();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mHearingAidPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isHearingAidProfileSupported() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(this.mHearingAidChangedReceiver, intentFilter);
        this.mLocalBluetoothManager.getEventManager().registerCallback(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mHearingAidChangedReceiver);
        this.mLocalBluetoothManager.getEventManager().unregisterCallback(this);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            CachedBluetoothDevice connectedHearingAidDevice = getConnectedHearingAidDevice();
            if (connectedHearingAidDevice == null) {
                launchHearingAidInstructionDialog();
                return true;
            }
            launchBluetoothDeviceDetailSetting(connectedHearingAidDevice);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        CachedBluetoothDevice connectedHearingAidDevice = getConnectedHearingAidDevice();
        if (connectedHearingAidDevice == null) {
            return this.mContext.getText(R$string.accessibility_hearingaid_not_connected_summary);
        }
        int connectedHearingAidDeviceNum = getConnectedHearingAidDeviceNum();
        String name = connectedHearingAidDevice.getName();
        int deviceSide = connectedHearingAidDevice.getDeviceSide();
        CachedBluetoothDevice subDevice = connectedHearingAidDevice.getSubDevice();
        return connectedHearingAidDeviceNum > 1 ? this.mContext.getString(R$string.accessibility_hearingaid_more_device_summary, name) : (subDevice == null || !subDevice.isConnected()) ? deviceSide == -1 ? this.mContext.getString(R$string.accessibility_hearingaid_active_device_summary, name) : deviceSide == 0 ? this.mContext.getString(R$string.accessibility_hearingaid_left_side_device_summary, name) : this.mContext.getString(R$string.accessibility_hearingaid_right_side_device_summary, name) : this.mContext.getString(R$string.accessibility_hearingaid_left_and_right_side_device_summary, name);
    }

    @Override // com.android.settingslib.bluetooth.BluetoothCallback
    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (cachedBluetoothDevice != null && i == 21) {
            HearingAidUtils.launchHearingAidPairingDialog(this.mFragmentManager, cachedBluetoothDevice);
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    CachedBluetoothDevice getConnectedHearingAidDevice() {
        if (isHearingAidProfileSupported()) {
            CachedBluetoothDeviceManager cachedDeviceManager = this.mLocalBluetoothManager.getCachedDeviceManager();
            for (BluetoothDevice bluetoothDevice : this.mLocalBluetoothManager.getProfileManager().getHearingAidProfile().getConnectedDevices()) {
                if (!cachedDeviceManager.isSubDevice(bluetoothDevice)) {
                    return cachedDeviceManager.findDevice(bluetoothDevice);
                }
            }
            return null;
        }
        return null;
    }

    private int getConnectedHearingAidDeviceNum() {
        if (isHearingAidProfileSupported()) {
            final CachedBluetoothDeviceManager cachedDeviceManager = this.mLocalBluetoothManager.getCachedDeviceManager();
            return (int) this.mLocalBluetoothManager.getProfileManager().getHearingAidProfile().getConnectedDevices().stream().filter(new Predicate() { // from class: com.android.settings.accessibility.AccessibilityHearingAidPreferenceController$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getConnectedHearingAidDeviceNum$0;
                    lambda$getConnectedHearingAidDeviceNum$0 = AccessibilityHearingAidPreferenceController.lambda$getConnectedHearingAidDeviceNum$0(CachedBluetoothDeviceManager.this, (BluetoothDevice) obj);
                    return lambda$getConnectedHearingAidDeviceNum$0;
                }
            }).count();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getConnectedHearingAidDeviceNum$0(CachedBluetoothDeviceManager cachedBluetoothDeviceManager, BluetoothDevice bluetoothDevice) {
        return !cachedBluetoothDeviceManager.isSubDevice(bluetoothDevice);
    }

    private boolean isHearingAidProfileSupported() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }
        return this.mBluetoothAdapter.getSupportedProfiles().contains(21);
    }

    private LocalBluetoothManager getLocalBluetoothManager() {
        FutureTask futureTask = new FutureTask(new Callable() { // from class: com.android.settings.accessibility.AccessibilityHearingAidPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                LocalBluetoothManager lambda$getLocalBluetoothManager$1;
                lambda$getLocalBluetoothManager$1 = AccessibilityHearingAidPreferenceController.this.lambda$getLocalBluetoothManager$1();
                return lambda$getLocalBluetoothManager$1;
            }
        });
        try {
            futureTask.run();
            return (LocalBluetoothManager) futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.w(TAG, "Error getting LocalBluetoothManager.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ LocalBluetoothManager lambda$getLocalBluetoothManager$1() throws Exception {
        return Utils.getLocalBtManager(this.mContext);
    }

    void setPreference(Preference preference) {
        this.mHearingAidPreference = preference;
    }

    void launchBluetoothDeviceDetailSetting(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("device_address", cachedBluetoothDevice.getDevice().getAddress());
        new SubSettingLauncher(this.mContext).setDestination(BluetoothDeviceDetailsFragment.class.getName()).setArguments(bundle).setTitleRes(R$string.device_details_title).setSourceMetricsCategory(2).launch();
    }

    void launchHearingAidInstructionDialog() {
        HearingAidDialogFragment.newInstance().show(this.mFragmentManager, HearingAidDialogFragment.class.toString());
    }
}
