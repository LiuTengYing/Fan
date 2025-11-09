package com.android.settings.bluetooth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$style;
import com.android.settingslib.bluetooth.LocalBluetoothLeBroadcast;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
/* loaded from: classes.dex */
public class BluetoothBroadcastDialog extends InstrumentedDialogFragment {
    private static final CharSequence UNKNOWN_APP_LABEL = "unknown";
    private AlertDialog mAlertDialog;
    private Context mContext;
    private CharSequence mCurrentAppLabel = UNKNOWN_APP_LABEL;
    private String mDeviceAddress;
    private LocalBluetoothManager mLocalBluetoothManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        this.mCurrentAppLabel = getActivity().getIntent().getCharSequenceExtra("app_label");
        this.mDeviceAddress = getActivity().getIntent().getStringExtra("device_address");
        this.mLocalBluetoothManager = Utils.getLocalBtManager(this.mContext);
        setShowsDialog(true);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        View inflate = View.inflate(this.mContext, R$layout.broadcast_dialog, null);
        Context context = this.mContext;
        int i = R$string.bluetooth_broadcast_dialog_title;
        ((TextView) inflate.findViewById(R$id.dialog_title)).setText(context.getString(i));
        ((TextView) inflate.findViewById(R$id.dialog_subtitle)).setText(this.mContext.getString(R$string.bluetooth_broadcast_dialog_broadcast_message));
        Button button = (Button) inflate.findViewById(R$id.positive_btn);
        if (TextUtils.isEmpty(this.mCurrentAppLabel)) {
            button.setText(this.mContext.getString(i));
        } else {
            button.setText(this.mContext.getString(R$string.bluetooth_broadcast_dialog_broadcast_app, String.valueOf(this.mCurrentAppLabel)));
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothBroadcastDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothBroadcastDialog.this.lambda$onCreateDialog$0(view);
            }
        });
        Button button2 = (Button) inflate.findViewById(R$id.negative_btn);
        button2.setText(this.mContext.getString(R$string.bluetooth_find_broadcast));
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothBroadcastDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothBroadcastDialog.this.lambda$onCreateDialog$1(view);
            }
        });
        ((Button) inflate.findViewById(R$id.neutral_btn)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.bluetooth.BluetoothBroadcastDialog$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BluetoothBroadcastDialog.this.lambda$onCreateDialog$2(view);
            }
        });
        AlertDialog create = new AlertDialog.Builder(this.mContext, R$style.Theme_AlertDialog_SettingsLib).setView(inflate).create();
        this.mAlertDialog = create;
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(View view) {
        launchMediaOutputBroadcastDialog();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(View view) {
        launchFindBroadcastsActivity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$2(View view) {
        dismiss();
        getActivity().finish();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    private void launchFindBroadcastsActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("device_address", this.mDeviceAddress);
        new SubSettingLauncher(this.mContext).setTitleRes(R$string.bluetooth_find_broadcast_title).setDestination(BluetoothFindBroadcastsFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(0).launch();
        dismissVolumePanel();
    }

    private void launchMediaOutputBroadcastDialog() {
        if (startBroadcast()) {
            this.mContext.sendBroadcast(new Intent().setPackage("com.android.systemui").setAction("com.android.systemui.action.LAUNCH_MEDIA_OUTPUT_BROADCAST_DIALOG").putExtra("package_name", getActivity().getPackageName()));
            dismissVolumePanel();
        }
    }

    private LocalBluetoothLeBroadcast getLEAudioBroadcastProfile() {
        LocalBluetoothLeBroadcast leAudioBroadcastProfile;
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager == null || localBluetoothManager.getProfileManager() == null || (leAudioBroadcastProfile = this.mLocalBluetoothManager.getProfileManager().getLeAudioBroadcastProfile()) == null) {
            Log.d("BTBroadcastsDialog", "Can not get LE Audio Broadcast Profile");
            return null;
        }
        return leAudioBroadcastProfile;
    }

    private boolean startBroadcast() {
        LocalBluetoothLeBroadcast lEAudioBroadcastProfile = getLEAudioBroadcastProfile();
        if (lEAudioBroadcastProfile != null) {
            lEAudioBroadcastProfile.startBroadcast(String.valueOf(this.mCurrentAppLabel), null);
            return true;
        }
        Log.d("BTBroadcastsDialog", "Can not broadcast successfully");
        return false;
    }

    private void dismissVolumePanel() {
        this.mContext.sendBroadcast(new Intent().setPackage("com.android.settings").setAction("com.android.settings.panel.action.CLOSE_PANEL"));
    }
}
