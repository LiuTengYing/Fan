package com.android.settings.connecteddevice;

import androidx.preference.Preference;
/* loaded from: classes.dex */
public interface DevicePreferenceCallback {
    void onDeviceAdded(Preference preference);

    void onDeviceRemoved(Preference preference);
}
