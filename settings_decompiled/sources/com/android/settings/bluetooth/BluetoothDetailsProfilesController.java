package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$layout;
import com.android.settingslib.bluetooth.A2dpProfile;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.HeadsetProfile;
import com.android.settingslib.bluetooth.LeAudioProfile;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager;
import com.android.settingslib.bluetooth.MapProfile;
import com.android.settingslib.bluetooth.PanProfile;
import com.android.settingslib.bluetooth.PbapServerProfile;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class BluetoothDetailsProfilesController extends BluetoothDetailsController implements Preference.OnPreferenceClickListener, LocalBluetoothProfileManager.ServiceListener {
    static final String HIGH_QUALITY_AUDIO_PREF_TAG = "A2dpProfileHighQualityAudio";
    private List<CachedBluetoothDevice> mAllOfCachedDevices;
    private CachedBluetoothDevice mCachedDevice;
    private boolean mIsLeContactSharingEnabled;
    private LocalBluetoothManager mManager;
    private Map<String, List<CachedBluetoothDevice>> mProfileDeviceMap;
    private LocalBluetoothProfileManager mProfileManager;
    PreferenceCategory mProfilesContainer;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_profiles";
    }

    public BluetoothDetailsProfilesController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, LocalBluetoothManager localBluetoothManager, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mProfileDeviceMap = new HashMap();
        this.mIsLeContactSharingEnabled = false;
        this.mManager = localBluetoothManager;
        this.mProfileManager = localBluetoothManager.getProfileManager();
        this.mCachedDevice = cachedBluetoothDevice;
        this.mAllOfCachedDevices = getAllOfCachedBluetoothDevices();
        lifecycle.addObserver(this);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mProfilesContainer = preferenceCategory;
        preferenceCategory.setLayoutResource(R$layout.preference_bluetooth_profile_category);
        this.mIsLeContactSharingEnabled = DeviceConfig.getBoolean("settings_ui", "bt_le_audio_contact_sharing_enabled", true);
        refresh();
    }

    private SwitchPreference createProfilePreference(Context context, LocalBluetoothProfile localBluetoothProfile) {
        SwitchPreference switchPreference = new SwitchPreference(context);
        switchPreference.setKey(localBluetoothProfile.toString());
        switchPreference.setTitle(localBluetoothProfile.getNameResource(this.mCachedDevice.getDevice()));
        switchPreference.setOnPreferenceClickListener(this);
        switchPreference.setOrder(localBluetoothProfile.getOrdinal());
        return switchPreference;
    }

    private void refreshProfilePreference(SwitchPreference switchPreference, LocalBluetoothProfile localBluetoothProfile) {
        BluetoothDevice device = this.mCachedDevice.getDevice();
        boolean isLeAudioEnabled = isLeAudioEnabled();
        boolean z = localBluetoothProfile instanceof A2dpProfile;
        boolean z2 = false;
        if (z || (localBluetoothProfile instanceof HeadsetProfile)) {
            if (isLeAudioEnabled) {
                Log.d("BtDetailsProfilesCtrl", "LE is enabled, gray out " + localBluetoothProfile.toString());
                switchPreference.setEnabled(false);
            } else {
                List<CachedBluetoothDevice> list = this.mProfileDeviceMap.get(localBluetoothProfile.toString());
                switchPreference.setEnabled(!(list != null && list.stream().anyMatch(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsProfilesController$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean isBusy;
                        isBusy = ((CachedBluetoothDevice) obj).isBusy();
                        return isBusy;
                    }
                })));
            }
        } else if (localBluetoothProfile instanceof LeAudioProfile) {
            List<CachedBluetoothDevice> list2 = this.mProfileDeviceMap.get(localBluetoothProfile.toString());
            boolean z3 = list2 != null && list2.stream().anyMatch(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsProfilesController$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean isBusy;
                    isBusy = ((CachedBluetoothDevice) obj).isBusy();
                    return isBusy;
                }
            });
            if (isLeAudioEnabled && !z3) {
                A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
                HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
                grayOutPreferenceWhenLeAudioIsEnabled(a2dpProfile);
                grayOutPreferenceWhenLeAudioIsEnabled(headsetProfile);
            }
            switchPreference.setEnabled(!z3);
        } else if ((localBluetoothProfile instanceof PbapServerProfile) && isLeAudioEnabled && !this.mIsLeContactSharingEnabled) {
            switchPreference.setEnabled(false);
        } else {
            switchPreference.setEnabled(!this.mCachedDevice.isBusy());
        }
        if (localBluetoothProfile instanceof MapProfile) {
            switchPreference.setChecked(device.getMessageAccessPermission() == 1);
        } else if (localBluetoothProfile instanceof PbapServerProfile) {
            switchPreference.setChecked(device.getPhonebookAccessPermission() == 1);
        } else if (localBluetoothProfile instanceof PanProfile) {
            switchPreference.setChecked(localBluetoothProfile.getConnectionStatus(device) == 2);
        } else {
            switchPreference.setChecked(localBluetoothProfile.isEnabled(device));
        }
        if (z) {
            A2dpProfile a2dpProfile2 = (A2dpProfile) localBluetoothProfile;
            SwitchPreference switchPreference2 = (SwitchPreference) this.mProfilesContainer.findPreference(HIGH_QUALITY_AUDIO_PREF_TAG);
            if (switchPreference2 != null) {
                if (a2dpProfile2.isEnabled(device) && a2dpProfile2.supportsHighQualityAudio(device)) {
                    switchPreference2.setVisible(true);
                    switchPreference2.setTitle(a2dpProfile2.getHighQualityAudioOptionLabel(device));
                    switchPreference2.setChecked(a2dpProfile2.isHighQualityAudioEnabled(device));
                    if (!this.mCachedDevice.isBusy() && !isLeAudioEnabled) {
                        z2 = true;
                    }
                    switchPreference2.setEnabled(z2);
                    return;
                }
                switchPreference2.setVisible(false);
            }
        }
    }

    private boolean isLeAudioEnabled() {
        List<CachedBluetoothDevice> list;
        final LeAudioProfile leAudioProfile = this.mProfileManager.getLeAudioProfile();
        return (leAudioProfile == null || (list = this.mProfileDeviceMap.get(leAudioProfile.toString())) == null || !list.stream().anyMatch(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsProfilesController$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isLeAudioEnabled$2;
                lambda$isLeAudioEnabled$2 = BluetoothDetailsProfilesController.lambda$isLeAudioEnabled$2(LocalBluetoothProfile.this, (CachedBluetoothDevice) obj);
                return lambda$isLeAudioEnabled$2;
            }
        })) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isLeAudioEnabled$2(LocalBluetoothProfile localBluetoothProfile, CachedBluetoothDevice cachedBluetoothDevice) {
        return localBluetoothProfile.isEnabled(cachedBluetoothDevice.getDevice());
    }

    private void grayOutPreferenceWhenLeAudioIsEnabled(LocalBluetoothProfile localBluetoothProfile) {
        SwitchPreference switchPreference;
        if (localBluetoothProfile == null || (switchPreference = (SwitchPreference) this.mProfilesContainer.findPreference(localBluetoothProfile.toString())) == null) {
            return;
        }
        Log.d("BtDetailsProfilesCtrl", "LE is enabled, gray out " + localBluetoothProfile.toString());
        switchPreference.setEnabled(false);
    }

    private void enableProfile(LocalBluetoothProfile localBluetoothProfile) {
        BluetoothDevice device = this.mCachedDevice.getDevice();
        if (localBluetoothProfile instanceof PbapServerProfile) {
            device.setPhonebookAccessPermission(1);
            return;
        }
        if (localBluetoothProfile instanceof MapProfile) {
            device.setMessageAccessPermission(1);
        }
        if (localBluetoothProfile instanceof LeAudioProfile) {
            enableLeAudioProfile(localBluetoothProfile);
        } else {
            localBluetoothProfile.setEnabled(device, true);
        }
    }

    private void disableProfile(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile instanceof LeAudioProfile) {
            disableLeAudioProfile(localBluetoothProfile);
            return;
        }
        BluetoothDevice device = this.mCachedDevice.getDevice();
        localBluetoothProfile.setEnabled(device, false);
        if (localBluetoothProfile instanceof MapProfile) {
            device.setMessageAccessPermission(2);
        } else if (localBluetoothProfile instanceof PbapServerProfile) {
            device.setPhonebookAccessPermission(2);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        LocalBluetoothProfile profileByName = this.mProfileManager.getProfileByName(preference.getKey());
        PbapServerProfile pbapServerProfile = profileByName;
        if (profileByName == null) {
            PbapServerProfile pbapProfile = this.mManager.getProfileManager().getPbapProfile();
            boolean equals = TextUtils.equals(preference.getKey(), pbapProfile.toString());
            pbapServerProfile = pbapProfile;
            if (!equals) {
                return false;
            }
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        if (switchPreference.isChecked()) {
            enableProfile(pbapServerProfile);
        } else {
            disableProfile(pbapServerProfile);
        }
        refreshProfilePreference(switchPreference, pbapServerProfile);
        return true;
    }

    private List<LocalBluetoothProfile> getProfiles() {
        ArrayList arrayList = new ArrayList();
        this.mProfileDeviceMap.clear();
        List<CachedBluetoothDevice> list = this.mAllOfCachedDevices;
        if (list != null && !list.isEmpty()) {
            for (CachedBluetoothDevice cachedBluetoothDevice : this.mAllOfCachedDevices) {
                for (LocalBluetoothProfile localBluetoothProfile : cachedBluetoothDevice.getConnectableProfiles()) {
                    if (this.mProfileDeviceMap.containsKey(localBluetoothProfile.toString())) {
                        this.mProfileDeviceMap.get(localBluetoothProfile.toString()).add(cachedBluetoothDevice);
                        Log.d("BtDetailsProfilesCtrl", "getProfiles: " + localBluetoothProfile.toString() + " add device " + cachedBluetoothDevice.getDevice().getAnonymizedAddress());
                    } else {
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(cachedBluetoothDevice);
                        this.mProfileDeviceMap.put(localBluetoothProfile.toString(), arrayList2);
                        arrayList.add(localBluetoothProfile);
                    }
                }
            }
            BluetoothDevice device = this.mCachedDevice.getDevice();
            int phonebookAccessPermission = device.getPhonebookAccessPermission();
            if (Utils.isSupportedBTSink()) {
                if (!this.mCachedDevice.isClientDevice() && !arrayList.contains(this.mManager.getProfileManager().getPbapClientProfile()) && phonebookAccessPermission != 0) {
                    arrayList.add(this.mManager.getProfileManager().getPbapProfile());
                }
            } else if (phonebookAccessPermission != 0) {
                arrayList.add(this.mManager.getProfileManager().getPbapProfile());
            }
            MapProfile mapProfile = this.mManager.getProfileManager().getMapProfile();
            if (device.getMessageAccessPermission() != 0) {
                arrayList.add(mapProfile);
            }
            Log.d("BtDetailsProfilesCtrl", "getProfiles:result:" + arrayList);
        }
        return arrayList;
    }

    private List<CachedBluetoothDevice> getAllOfCachedBluetoothDevices() {
        ArrayList arrayList = new ArrayList();
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        if (cachedBluetoothDevice == null) {
            return arrayList;
        }
        arrayList.add(cachedBluetoothDevice);
        if (this.mCachedDevice.getGroupId() != -1) {
            for (CachedBluetoothDevice cachedBluetoothDevice2 : this.mCachedDevice.getMemberDevice()) {
                arrayList.add(cachedBluetoothDevice2);
            }
        }
        return arrayList;
    }

    private void disableLeAudioProfile(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile == null || this.mProfileDeviceMap.get(localBluetoothProfile.toString()) == null) {
            Log.e("BtDetailsProfilesCtrl", "There is no the LE profile or no device in mProfileDeviceMap. Do nothing.");
            return;
        }
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mProfileDeviceMap.get(localBluetoothProfile.toString())) {
            Log.d("BtDetailsProfilesCtrl", "User disable LE device: " + cachedBluetoothDevice.getDevice().getAnonymizedAddress());
            localBluetoothProfile.setEnabled(cachedBluetoothDevice.getDevice(), false);
        }
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        enableProfileAfterUserDisablesLeAudio(a2dpProfile);
        enableProfileAfterUserDisablesLeAudio(headsetProfile);
    }

    private void enableLeAudioProfile(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile == null || this.mProfileDeviceMap.get(localBluetoothProfile.toString()) == null) {
            Log.e("BtDetailsProfilesCtrl", "There is no the LE profile or no device in mProfileDeviceMap. Do nothing.");
            return;
        }
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        HeadsetProfile headsetProfile = this.mProfileManager.getHeadsetProfile();
        disableProfileBeforeUserEnablesLeAudio(a2dpProfile);
        disableProfileBeforeUserEnablesLeAudio(headsetProfile);
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mProfileDeviceMap.get(localBluetoothProfile.toString())) {
            Log.d("BtDetailsProfilesCtrl", "User enable LE device: " + cachedBluetoothDevice.getDevice().getAnonymizedAddress());
            localBluetoothProfile.setEnabled(cachedBluetoothDevice.getDevice(), true);
        }
    }

    private void disableProfileBeforeUserEnablesLeAudio(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile == null || this.mProfileDeviceMap.get(localBluetoothProfile.toString()) == null) {
            return;
        }
        Log.d("BtDetailsProfilesCtrl", "Disable " + localBluetoothProfile.toString() + " before user enables LE");
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mProfileDeviceMap.get(localBluetoothProfile.toString())) {
            if (localBluetoothProfile.isEnabled(cachedBluetoothDevice.getDevice())) {
                localBluetoothProfile.setEnabled(cachedBluetoothDevice.getDevice(), false);
            } else {
                Log.d("BtDetailsProfilesCtrl", "The " + localBluetoothProfile.toString() + " profile is disabled. Do nothing.");
            }
        }
    }

    private void enableProfileAfterUserDisablesLeAudio(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile == null || this.mProfileDeviceMap.get(localBluetoothProfile.toString()) == null) {
            return;
        }
        Log.d("BtDetailsProfilesCtrl", "enable " + localBluetoothProfile.toString() + "after user disables LE");
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mProfileDeviceMap.get(localBluetoothProfile.toString())) {
            if (localBluetoothProfile.isEnabled(cachedBluetoothDevice.getDevice())) {
                Log.d("BtDetailsProfilesCtrl", "The " + localBluetoothProfile.toString() + " profile is enabled. Do nothing.");
            } else {
                localBluetoothProfile.setEnabled(cachedBluetoothDevice.getDevice(), true);
            }
        }
    }

    private void maybeAddHighQualityAudioPref(LocalBluetoothProfile localBluetoothProfile) {
        if (localBluetoothProfile instanceof A2dpProfile) {
            BluetoothDevice device = this.mCachedDevice.getDevice();
            final A2dpProfile a2dpProfile = (A2dpProfile) localBluetoothProfile;
            if (a2dpProfile.isProfileReady() && a2dpProfile.supportsHighQualityAudio(device)) {
                SwitchPreference switchPreference = new SwitchPreference(this.mProfilesContainer.getContext());
                switchPreference.setKey(HIGH_QUALITY_AUDIO_PREF_TAG);
                switchPreference.setVisible(false);
                switchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsProfilesController$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        boolean lambda$maybeAddHighQualityAudioPref$3;
                        lambda$maybeAddHighQualityAudioPref$3 = BluetoothDetailsProfilesController.this.lambda$maybeAddHighQualityAudioPref$3(a2dpProfile, preference);
                        return lambda$maybeAddHighQualityAudioPref$3;
                    }
                });
                this.mProfilesContainer.addPreference(switchPreference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$maybeAddHighQualityAudioPref$3(A2dpProfile a2dpProfile, Preference preference) {
        a2dpProfile.setHighQualityAudioEnabled(this.mCachedDevice.getDevice(), ((SwitchPreference) preference).isChecked());
        return true;
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mAllOfCachedDevices) {
            cachedBluetoothDevice.unregisterCallback(this);
        }
        this.mProfileManager.removeServiceListener(this);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mAllOfCachedDevices) {
            cachedBluetoothDevice.registerCallback(this);
        }
        this.mProfileManager.addServiceListener(this);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mAllOfCachedDevices) {
            cachedBluetoothDevice.unregisterCallback(this);
        }
        List<CachedBluetoothDevice> allOfCachedBluetoothDevices = getAllOfCachedBluetoothDevices();
        this.mAllOfCachedDevices = allOfCachedBluetoothDevices;
        for (CachedBluetoothDevice cachedBluetoothDevice2 : allOfCachedBluetoothDevices) {
            cachedBluetoothDevice2.registerCallback(this);
        }
        super.onDeviceAttributesChanged();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener
    public void onServiceConnected() {
        refresh();
    }

    @Override // com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener
    public void onServiceDisconnected() {
        refresh();
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        for (LocalBluetoothProfile localBluetoothProfile : getProfiles()) {
            if (localBluetoothProfile != null && localBluetoothProfile.isProfileReady()) {
                SwitchPreference switchPreference = (SwitchPreference) this.mProfilesContainer.findPreference(localBluetoothProfile.toString());
                if (switchPreference == null) {
                    switchPreference = createProfilePreference(this.mProfilesContainer.getContext(), localBluetoothProfile);
                    this.mProfilesContainer.addPreference(switchPreference);
                    maybeAddHighQualityAudioPref(localBluetoothProfile);
                }
                refreshProfilePreference(switchPreference, localBluetoothProfile);
            }
        }
        for (LocalBluetoothProfile localBluetoothProfile2 : this.mCachedDevice.getRemovedProfiles()) {
            SwitchPreference switchPreference2 = (SwitchPreference) this.mProfilesContainer.findPreference(localBluetoothProfile2.toString());
            if (switchPreference2 != null) {
                this.mProfilesContainer.removePreference(switchPreference2);
            }
        }
        if (this.mProfilesContainer.findPreference("bottom_preference") == null) {
            Preference preference = new Preference(((BluetoothDetailsController) this).mContext);
            preference.setLayoutResource(R$layout.preference_bluetooth_profile_category);
            preference.setEnabled(false);
            preference.setKey("bottom_preference");
            preference.setOrder(99);
            preference.setSelectable(false);
            this.mProfilesContainer.addPreference(preference);
        }
    }
}
