package com.android.settings.wifi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class NetworkRequestErrorDialogFragment extends InstrumentedDialogFragment {
    private WifiManager.NetworkRequestUserSelectionCallback mRejectCallback;

    /* loaded from: classes.dex */
    public enum ERROR_DIALOG_TYPE {
        TIME_OUT,
        ABORT
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1373;
    }

    public static NetworkRequestErrorDialogFragment newInstance() {
        return new NetworkRequestErrorDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        rejectNetworkRequestAndFinish();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        ERROR_DIALOG_TYPE error_dialog_type = ERROR_DIALOG_TYPE.TIME_OUT;
        ERROR_DIALOG_TYPE error_dialog_type2 = getArguments() != null ? (ERROR_DIALOG_TYPE) getArguments().getSerializable("DIALOG_ERROR_TYPE") : error_dialog_type;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (error_dialog_type2 == error_dialog_type) {
            builder.setMessage(R$string.network_connection_timeout_dialog_message).setPositiveButton(R$string.network_connection_timeout_dialog_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestErrorDialogFragment$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NetworkRequestErrorDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
                }
            }).setNegativeButton(R$string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestErrorDialogFragment$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NetworkRequestErrorDialogFragment.this.lambda$onCreateDialog$1(dialogInterface, i);
                }
            });
        } else {
            builder.setMessage(R$string.network_connection_errorstate_dialog_message).setPositiveButton(R$string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.NetworkRequestErrorDialogFragment$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NetworkRequestErrorDialogFragment.this.lambda$onCreateDialog$2(dialogInterface, i);
                }
            });
        }
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        onRescanClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        rejectNetworkRequestAndFinish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$2(DialogInterface dialogInterface, int i) {
        rejectNetworkRequestAndFinish();
    }

    public void setRejectCallback(WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback) {
        this.mRejectCallback = networkRequestUserSelectionCallback;
    }

    protected void onRescanClick() {
        if (getActivity() != null) {
            dismiss();
            ((NetworkRequestDialogActivity) getActivity()).onClickRescanButton();
        }
    }

    private void rejectNetworkRequestAndFinish() {
        if (getActivity() != null) {
            WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback = this.mRejectCallback;
            if (networkRequestUserSelectionCallback != null) {
                networkRequestUserSelectionCallback.reject();
            }
            getActivity().finish();
        }
    }
}
