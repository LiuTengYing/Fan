package com.android.settings.sound;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.media.MediaOutputUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.A2dpProfile;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.HearingAidProfile;
import java.util.List;
/* loaded from: classes.dex */
public class MediaOutputPreferenceController extends AudioSwitchPreferenceController {
    private MediaController mMediaController;

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onAclConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onConnectionStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        super.onDeviceBondStateChanged(cachedBluetoothDevice, i);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        super.onDeviceDeleted(cachedBluetoothDevice);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settingslib.bluetooth.BluetoothCallback
    public /* bridge */ /* synthetic */ void onScanningStateChanged(boolean z) {
        super.onScanningStateChanged(z);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public MediaOutputPreferenceController(Context context, String str) {
        super(context, str);
        this.mMediaController = MediaOutputUtils.getActiveLocalMediaController((MediaSessionManager) context.getSystemService(MediaSessionManager.class));
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (Utils.isAudioModeOngoingCall(this.mContext) || this.mMediaController == null) {
            return;
        }
        this.mPreference.setVisible(true);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        CharSequence alias;
        if (preference == null || this.mMediaController == null) {
            return;
        }
        if (Utils.isAudioModeOngoingCall(this.mContext)) {
            this.mPreference.setVisible(false);
            preference.setSummary(this.mContext.getText(R$string.media_out_summary_ongoing_call_state));
            return;
        }
        BluetoothDevice bluetoothDevice = null;
        List<BluetoothDevice> connectedA2dpDevices = getConnectedA2dpDevices();
        List<BluetoothDevice> connectedHearingAidDevices = getConnectedHearingAidDevices();
        if (this.mAudioManager.getMode() == 0 && ((connectedA2dpDevices != null && !connectedA2dpDevices.isEmpty()) || (connectedHearingAidDevices != null && !connectedHearingAidDevices.isEmpty()))) {
            bluetoothDevice = findActiveDevice();
        }
        Preference preference2 = this.mPreference;
        Context context = this.mContext;
        preference2.setTitle(context.getString(R$string.media_output_label_title, com.android.settings.Utils.getApplicationLabel(context, this.mMediaController.getPackageName())));
        Preference preference3 = this.mPreference;
        if (bluetoothDevice == null) {
            alias = this.mContext.getText(R$string.media_output_default_summary);
        } else {
            alias = bluetoothDevice.getAlias();
        }
        preference3.setSummary(alias);
    }

    @Override // com.android.settings.sound.AudioSwitchPreferenceController
    public BluetoothDevice findActiveDevice() {
        BluetoothDevice findActiveHearingAidDevice = findActiveHearingAidDevice();
        A2dpProfile a2dpProfile = this.mProfileManager.getA2dpProfile();
        return (findActiveHearingAidDevice != null || a2dpProfile == null) ? findActiveHearingAidDevice : a2dpProfile.getActiveDevice();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.sound.AudioSwitchPreferenceController
    public BluetoothDevice findActiveHearingAidDevice() {
        HearingAidProfile hearingAidProfile = this.mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            for (BluetoothDevice bluetoothDevice : hearingAidProfile.getActiveDevices()) {
                if (bluetoothDevice != null) {
                    return bluetoothDevice;
                }
            }
            return null;
        }
        return null;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            this.mContext.sendBroadcast(new Intent().setAction("com.android.systemui.action.LAUNCH_MEDIA_OUTPUT_DIALOG").setPackage("com.android.systemui").putExtra("package_name", this.mMediaController.getPackageName()).putExtra("key_media_session_token", this.mMediaController.getSessionToken()));
            return true;
        }
        return false;
    }
}
