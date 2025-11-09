package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import java.util.List;
/* loaded from: classes.dex */
public abstract class NetworkRequestDialogBaseFragment extends InstrumentedDialogFragment {
    static final String EXTRA_APP_NAME = "com.android.settings.wifi.extra.APP_NAME";
    NetworkRequestDialogActivity mActivity = null;
    private String mAppName = "";

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1373;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onMatch(List<ScanResult> list) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUserSelectionCallbackRegistration(WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback) {
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NetworkRequestDialogActivity) {
            this.mActivity = (NetworkRequestDialogActivity) context;
        }
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            this.mAppName = intent.getStringExtra(EXTRA_APP_NAME);
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        NetworkRequestDialogActivity networkRequestDialogActivity = this.mActivity;
        if (networkRequestDialogActivity != null) {
            networkRequestDialogActivity.onCancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getTitle() {
        return getString(R$string.network_connection_request_dialog_title);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getSummary() {
        return getString(R$string.network_connection_request_dialog_summary, this.mAppName);
    }
}
