package com.android.settings.bluetooth;

import android.content.Context;
import android.media.AudioDeviceAttributes;
import android.media.AudioManager;
import android.media.Spatializer;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class BluetoothDetailsSpatialAudioController extends BluetoothDetailsController implements Preference.OnPreferenceClickListener {
    AudioDeviceAttributes mAudioDevice;
    PreferenceCategory mProfilesContainer;
    private final Spatializer mSpatializer;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "spatial_audio_group";
    }

    public BluetoothDetailsSpatialAudioController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mSpatializer = ((AudioManager) context.getSystemService(AudioManager.class)).getSpatializer();
        this.mAudioDevice = new AudioDeviceAttributes(2, 8, this.mCachedDevice.getAddress());
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mSpatializer.isAvailableForDevice(this.mAudioDevice);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        SwitchPreference switchPreference = (SwitchPreference) preference;
        String key = switchPreference.getKey();
        if (TextUtils.equals(key, "spatial_audio")) {
            if (switchPreference.isChecked()) {
                this.mSpatializer.addCompatibleAudioDevice(this.mAudioDevice);
            } else {
                this.mSpatializer.removeCompatibleAudioDevice(this.mAudioDevice);
            }
            refresh();
            return true;
        } else if (TextUtils.equals(key, "head_tracking")) {
            this.mSpatializer.setHeadTrackerEnabled(switchPreference.isChecked(), this.mAudioDevice);
            return true;
        } else {
            Log.w("BluetoothSpatialAudioController", "invalid key name.");
            return false;
        }
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mProfilesContainer = preferenceCategory;
        preferenceCategory.setLayoutResource(R$layout.preference_bluetooth_profile_category);
        refresh();
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        SwitchPreference switchPreference = (SwitchPreference) this.mProfilesContainer.findPreference("spatial_audio");
        if (switchPreference == null) {
            switchPreference = createSpatialAudioPreference(this.mProfilesContainer.getContext());
            this.mProfilesContainer.addPreference(switchPreference);
        }
        boolean contains = this.mSpatializer.getCompatibleAudioDevices().contains(this.mAudioDevice);
        Log.d("BluetoothSpatialAudioController", "refresh() isSpatialAudioOn : " + contains);
        switchPreference.setChecked(contains);
        SwitchPreference switchPreference2 = (SwitchPreference) this.mProfilesContainer.findPreference("head_tracking");
        if (switchPreference2 == null) {
            switchPreference2 = createHeadTrackingPreference(this.mProfilesContainer.getContext());
            this.mProfilesContainer.addPreference(switchPreference2);
        }
        boolean z = contains && this.mSpatializer.hasHeadTracker(this.mAudioDevice);
        Log.d("BluetoothSpatialAudioController", "refresh() has head tracker : " + this.mSpatializer.hasHeadTracker(this.mAudioDevice));
        switchPreference2.setVisible(z);
        if (z) {
            switchPreference2.setChecked(this.mSpatializer.isHeadTrackerEnabled(this.mAudioDevice));
        }
    }

    SwitchPreference createSpatialAudioPreference(Context context) {
        SwitchPreference switchPreference = new SwitchPreference(context);
        switchPreference.setKey("spatial_audio");
        switchPreference.setTitle(context.getString(R$string.bluetooth_details_spatial_audio_title));
        switchPreference.setSummary(context.getString(R$string.bluetooth_details_spatial_audio_summary));
        switchPreference.setOnPreferenceClickListener(this);
        return switchPreference;
    }

    SwitchPreference createHeadTrackingPreference(Context context) {
        SwitchPreference switchPreference = new SwitchPreference(context);
        switchPreference.setKey("head_tracking");
        switchPreference.setTitle(context.getString(R$string.bluetooth_details_head_tracking_title));
        switchPreference.setSummary(context.getString(R$string.bluetooth_details_head_tracking_summary));
        switchPreference.setOnPreferenceClickListener(this);
        return switchPreference;
    }
}
