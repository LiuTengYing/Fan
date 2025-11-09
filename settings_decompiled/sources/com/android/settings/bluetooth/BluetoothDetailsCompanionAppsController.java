package com.android.settings.bluetooth;

import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$layout;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class BluetoothDetailsCompanionAppsController extends BluetoothDetailsController {
    private CachedBluetoothDevice mCachedDevice;
    CompanionDeviceManager mCompanionDeviceManager;
    PackageManager mPackageManager;
    PreferenceCategory mProfilesContainer;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_companion_apps";
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
    }

    public BluetoothDetailsCompanionAppsController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mCachedDevice = cachedBluetoothDevice;
        this.mCompanionDeviceManager = (CompanionDeviceManager) context.getSystemService(CompanionDeviceManager.class);
        this.mPackageManager = context.getPackageManager();
        lifecycle.addObserver(this);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mProfilesContainer = preferenceCategory;
        preferenceCategory.setLayoutResource(R$layout.preference_companion_app);
    }
}
