package com.android.settings.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothLeBroadcastAssistant;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
/* loaded from: classes.dex */
public class BluetoothFindBroadcastsHeaderController extends BluetoothDetailsController {
    BluetoothFindBroadcastsFragment mBluetoothFindBroadcastsFragment;
    PreferenceCategory mBroadcastSourceList;
    LinearLayout mBtnBroadcastLayout;
    Button mBtnFindBroadcast;
    Button mBtnLeaveBroadcast;
    Button mBtnScanQrCode;
    LayoutPreference mLayoutPreference;
    TextView mSummary;
    TextView mTitle;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_find_broadcast_header";
    }

    public BluetoothFindBroadcastsHeaderController(Context context, BluetoothFindBroadcastsFragment bluetoothFindBroadcastsFragment, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle, LocalBluetoothManager localBluetoothManager) {
        super(context, bluetoothFindBroadcastsFragment, cachedBluetoothDevice, lifecycle);
        this.mBluetoothFindBroadcastsFragment = bluetoothFindBroadcastsFragment;
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        this.mLayoutPreference = (LayoutPreference) preferenceScreen.findPreference("bluetooth_find_broadcast_header");
        this.mBroadcastSourceList = (PreferenceCategory) preferenceScreen.findPreference("broadcast_source_list");
        refresh();
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
        LayoutPreference layoutPreference = this.mLayoutPreference;
        if (layoutPreference == null || this.mCachedDevice == null) {
            return;
        }
        TextView textView = (TextView) layoutPreference.findViewById(R$id.entity_header_title);
        this.mTitle = textView;
        textView.setText(this.mCachedDevice.getName());
        TextView textView2 = (TextView) this.mLayoutPreference.findViewById(R$id.entity_header_summary);
        this.mSummary = textView2;
        textView2.setText("");
        Button button = (Button) this.mLayoutPreference.findViewById(R$id.button_find_broadcast);
        this.mBtnFindBroadcast = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsHeaderController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothFindBroadcastsHeaderController.this.lambda$refresh$0(view);
            }
        });
        this.mBtnBroadcastLayout = (LinearLayout) this.mLayoutPreference.findViewById(R$id.button_broadcast_layout);
        Button button2 = (Button) this.mLayoutPreference.findViewById(R$id.button_leave_broadcast);
        this.mBtnLeaveBroadcast = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsHeaderController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothFindBroadcastsHeaderController.this.lambda$refresh$1(view);
            }
        });
        Button button3 = (Button) this.mLayoutPreference.findViewById(R$id.button_scan_qr_code);
        this.mBtnScanQrCode = button3;
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothFindBroadcastsHeaderController$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothFindBroadcastsHeaderController.this.lambda$refresh$2(view);
            }
        });
        updateHeaderLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$0(View view) {
        scanBroadcastSource();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$1(View view) {
        leaveBroadcastSession();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refresh$2(View view) {
        launchQrCodeScanner();
    }

    private boolean isBroadcastSourceExist() {
        return this.mBroadcastSourceList.getPreferenceCount() > 0;
    }

    private void updateHeaderLayout() {
        LocalBluetoothLeBroadcastAssistant leBroadcastAssistant;
        if (isBroadcastSourceExist()) {
            this.mBtnFindBroadcast.setVisibility(8);
            this.mBtnBroadcastLayout.setVisibility(0);
        } else {
            this.mBtnFindBroadcast.setVisibility(0);
            this.mBtnBroadcastLayout.setVisibility(8);
        }
        this.mBtnLeaveBroadcast.setEnabled(false);
        BluetoothFindBroadcastsFragment bluetoothFindBroadcastsFragment = this.mBluetoothFindBroadcastsFragment;
        if (bluetoothFindBroadcastsFragment == null || this.mCachedDevice == null || (leBroadcastAssistant = bluetoothFindBroadcastsFragment.getLeBroadcastAssistant()) == null || leBroadcastAssistant.getConnectionStatus(this.mCachedDevice.getDevice()) != 2) {
            return;
        }
        this.mBtnLeaveBroadcast.setEnabled(true);
    }

    private void scanBroadcastSource() {
        BluetoothFindBroadcastsFragment bluetoothFindBroadcastsFragment = this.mBluetoothFindBroadcastsFragment;
        if (bluetoothFindBroadcastsFragment == null) {
            return;
        }
        bluetoothFindBroadcastsFragment.scanBroadcastSource();
    }

    private void leaveBroadcastSession() {
        BluetoothFindBroadcastsFragment bluetoothFindBroadcastsFragment = this.mBluetoothFindBroadcastsFragment;
        if (bluetoothFindBroadcastsFragment == null) {
            return;
        }
        bluetoothFindBroadcastsFragment.leaveBroadcastSession();
    }

    private void launchQrCodeScanner() {
        Intent intent = new Intent(((BluetoothDetailsController) this).mContext, QrCodeScanModeActivity.class);
        intent.setAction("android.settings.BLUETOOTH_LE_AUDIO_QR_CODE_SCANNER").putExtra("bluetooth_sink_is_group", true).putExtra("bluetooth_device_sink", this.mCachedDevice.getDevice());
        ((BluetoothDetailsController) this).mContext.startActivity(intent);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        if (this.mCachedDevice != null) {
            refresh();
        }
    }

    public void refreshUi() {
        updateHeaderLayout();
    }
}
