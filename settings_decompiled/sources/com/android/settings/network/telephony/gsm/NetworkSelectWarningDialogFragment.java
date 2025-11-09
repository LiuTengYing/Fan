package com.android.settings.network.telephony.gsm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkSelectSettings;
import com.unisoc.settings.network.ProxySimStateManager;
/* loaded from: classes.dex */
public class NetworkSelectWarningDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private static Fragment mParentFragment;
    private WarningDialogListener mAutoSelectListener;
    private ProxySimStateManager.OnSimStateChangedListener mOnSimStateChangeListener;
    private WarningDialogListener mOpenNetworkListener;
    private int mPhoneId;
    private ProxySimStateManager mProxySimStateMgr;
    private int mSubId = -1;
    private boolean mNeedShow = true;

    /* loaded from: classes.dex */
    public interface WarningDialogListener {
        void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1581;
    }

    public static NetworkSelectWarningDialogFragment newInstance(Fragment fragment, int i) {
        mParentFragment = fragment;
        NetworkSelectWarningDialogFragment networkSelectWarningDialogFragment = new NetworkSelectWarningDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("subid", i);
        networkSelectWarningDialogFragment.setArguments(bundle);
        return networkSelectWarningDialogFragment;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt("subid");
        this.mSubId = i;
        this.mPhoneId = SubscriptionManager.getPhoneId(i);
        this.mNeedShow = true;
        ProxySimStateManager proxySimStateManager = ProxySimStateManager.getInstance(getContext());
        this.mProxySimStateMgr = proxySimStateManager;
        proxySimStateManager.setLifecycle(getLifecycle());
        ProxySimStateManager.OnSimStateChangedListener onSimStateChangedListener = new ProxySimStateManager.OnSimStateChangedListener() { // from class: com.android.settings.network.telephony.gsm.NetworkSelectWarningDialogFragment.1
            @Override // com.unisoc.settings.network.ProxySimStateManager.OnSimStateChangedListener
            public void onChanged() {
                if ("ABSENT".equals(NetworkSelectWarningDialogFragment.this.mProxySimStateMgr.getSimState(NetworkSelectWarningDialogFragment.this.mPhoneId))) {
                    Log.d("NetworkSelectWarning", "SIM " + NetworkSelectWarningDialogFragment.this.mPhoneId + "was removed");
                    NetworkSelectWarningDialogFragment.this.mNeedShow = false;
                    if (NetworkSelectWarningDialogFragment.this.isShowing()) {
                        NetworkSelectWarningDialogFragment.this.dismiss();
                    }
                }
            }
        };
        this.mOnSimStateChangeListener = onSimStateChangedListener;
        this.mProxySimStateMgr.addSimStateChangeListener(onSimStateChangedListener);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (!(((SubscriptionManager) getContext().getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(this.mSubId) && this.mNeedShow) && isShowing()) {
            dismiss();
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mProxySimStateMgr.removeSimStateChangeListener(this.mOnSimStateChangeListener);
        this.mNeedShow = false;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIconAttribute(16843605).setTitle(17039380).setMessage(R$string.dialog_network_selection_message).setPositiveButton(17039379, this).setNegativeButton(17039369, this);
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1 && MobileNetworkUtils.isNetworkSelectEnabled(getContext())) {
            Log.d("NetworkSelectWarning", "launch  NetworkSelectSettings for " + this.mSubId);
            Bundle bundle = new Bundle();
            bundle.putInt("android.provider.extra.SUB_ID", this.mSubId);
            new SubSettingLauncher(getContext()).setDestination(NetworkSelectSettings.class.getName()).setSourceMetricsCategory(1581).setTitleRes(R$string.choose_network_title).setArguments(bundle).setResultListener(mParentFragment, 100).launch();
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        WarningDialogListener warningDialogListener = this.mAutoSelectListener;
        if (warningDialogListener != null) {
            warningDialogListener.onDialogDismiss(this);
        }
        WarningDialogListener warningDialogListener2 = this.mOpenNetworkListener;
        if (warningDialogListener2 != null) {
            warningDialogListener2.onDialogDismiss(this);
        }
    }

    public static void setParentFragment(Fragment fragment) {
        mParentFragment = fragment;
    }

    public void registerForAutoSelect(AutoSelectPreferenceController autoSelectPreferenceController) {
        this.mAutoSelectListener = autoSelectPreferenceController;
    }

    public void registerForOpenNetwork(OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController) {
        this.mOpenNetworkListener = openNetworkSelectPagePreferenceController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isShowing() {
        Dialog dialog = getDialog();
        return dialog != null && dialog.isShowing();
    }
}
