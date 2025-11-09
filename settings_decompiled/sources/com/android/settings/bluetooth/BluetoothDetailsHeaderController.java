package com.android.settings.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class BluetoothDetailsHeaderController extends BluetoothDetailsController {
    private CachedBluetoothDeviceManager mDeviceManager;
    private EntityHeaderController mHeaderController;
    private LocalBluetoothManager mLocalManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_device_header";
    }

    public BluetoothDetailsHeaderController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle, LocalBluetoothManager localBluetoothManager) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mLocalManager = localBluetoothManager;
        this.mDeviceManager = localBluetoothManager.getCachedDeviceManager();
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (Utils.isAdvancedDetailsHeader(this.mCachedDevice.getDevice()) || this.mCachedDevice.getConnectableProfiles().stream().anyMatch(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsHeaderController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isAvailable$0;
                lambda$isAvailable$0 = BluetoothDetailsHeaderController.lambda$isAvailable$0((LocalBluetoothProfile) obj);
                return lambda$isAvailable$0;
            }
        })) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$isAvailable$0(LocalBluetoothProfile localBluetoothProfile) {
        return localBluetoothProfile.getProfileId() == 22;
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference("bluetooth_device_header");
        this.mHeaderController = EntityHeaderController.newInstance(this.mFragment.getActivity(), this.mFragment, layoutPreference.findViewById(R$id.entity_header));
        preferenceScreen.addPreference(layoutPreference);
    }

    protected void setHeaderProperties() {
        Pair<Drawable, String> btRainbowDrawableWithDescription = BluetoothUtils.getBtRainbowDrawableWithDescription(((BluetoothDetailsController) this).mContext, this.mCachedDevice);
        String connectionSummary = this.mCachedDevice.getConnectionSummary();
        if (TextUtils.isEmpty(connectionSummary)) {
            this.mHeaderController.setSecondSummary(null);
        }
        this.mHeaderController.setLabel(this.mCachedDevice.getName());
        this.mHeaderController.setIcon((Drawable) btRainbowDrawableWithDescription.first);
        this.mHeaderController.setIconContentDescription((String) btRainbowDrawableWithDescription.second);
        this.mHeaderController.setSummary(connectionSummary);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        if (isAvailable()) {
            setHeaderProperties();
            this.mHeaderController.done((Activity) this.mFragment.getActivity(), true);
        }
    }
}
