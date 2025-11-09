package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class BluetoothDeviceRenamePreferenceController extends BluetoothDeviceNamePreferenceController {
    private Fragment mFragment;
    private MetricsFeatureProvider mMetricsFeatureProvider;

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BluetoothDeviceRenamePreferenceController(Context context, String str) {
        super(context, str);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        return (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) ? 3 : 0;
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController
    protected void updatePreferenceState(Preference preference) {
        preference.setSummary(getSummary());
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        preference.setVisible(bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }

    @Override // com.android.settings.bluetooth.BluetoothDeviceNamePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getDeviceName();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey()) || this.mFragment == null) {
            return false;
        }
        this.mMetricsFeatureProvider.action(this.mContext, 161, new Pair[0]);
        new LocalDeviceNameDialogFragment().show(this.mFragment.getFragmentManager(), "LocalAdapterName");
        return true;
    }
}
