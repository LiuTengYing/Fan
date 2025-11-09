package com.android.settings.bluetooth;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.ActionButtonsPreference;
/* loaded from: classes.dex */
public class BluetoothDetailsButtonsController extends BluetoothDetailsController {
    private ActionButtonsPreference mActionButtons;
    private boolean mConnectButtonInitialized;
    private boolean mIsConnected;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "action_buttons";
    }

    public BluetoothDetailsButtonsController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mIsConnected = cachedBluetoothDevice.isConnected();
    }

    private void onForgetButtonPressed() {
        ForgetDeviceDialogFragment.newInstance(this.mCachedDevice.getAddress()).show(this.mFragment.getFragmentManager(), "ForgetBluetoothDevice");
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        this.mActionButtons = ((ActionButtonsPreference) preferenceScreen.findPreference(getPreferenceKey())).setButton1Text(R$string.forget).setButton1Icon(R$drawable.ic_settings_delete).setButton1OnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsButtonsController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothDetailsButtonsController.this.lambda$init$0(view);
            }
        }).setButton1Enabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0(View view) {
        onForgetButtonPressed();
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        this.mActionButtons.setButton2Enabled(!this.mCachedDevice.isBusy());
        boolean z = this.mIsConnected;
        boolean isConnected = this.mCachedDevice.isConnected();
        this.mIsConnected = isConnected;
        if (isConnected) {
            if (this.mConnectButtonInitialized && z) {
                return;
            }
            this.mActionButtons.setButton2Text(R$string.bluetooth_device_context_disconnect).setButton2Icon(R$drawable.ic_settings_close).setButton2OnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsButtonsController$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BluetoothDetailsButtonsController.this.lambda$refresh$1(view);
                }
            });
            this.mConnectButtonInitialized = true;
        } else if (!this.mConnectButtonInitialized || z) {
            this.mActionButtons.setButton2Text(R$string.bluetooth_device_context_connect).setButton2Icon(R$drawable.ic_add_24dp).setButton2OnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothDetailsButtonsController$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BluetoothDetailsButtonsController.this.lambda$refresh$2(view);
                }
            });
            this.mConnectButtonInitialized = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$1(View view) {
        this.mMetricsFeatureProvider.action(((BluetoothDetailsController) this).mContext, 868, new Pair[0]);
        this.mCachedDevice.disconnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$2(View view) {
        this.mMetricsFeatureProvider.action(((BluetoothDetailsController) this).mContext, 867, new Pair[0]);
        Log.d("fangli", "refresh: bluetooth");
        this.mCachedDevice.connect();
    }
}
