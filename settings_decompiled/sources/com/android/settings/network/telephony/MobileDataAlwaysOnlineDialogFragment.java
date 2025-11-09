package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.android.internal.telephony.GlobalSettingsHelper;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.unisoc.settings.network.ProxySimStateManager;
/* loaded from: classes.dex */
public class MobileDataAlwaysOnlineDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private MobileDataAlwaysOnlineDialogListener mListener;
    private ProxySimStateManager.OnSimStateChangedListener mOnSimStateChangeListener;
    private int mPhoneId;
    private ProxySimStateManager mProxySimStateMgr;
    private int mSubId;

    /* loaded from: classes.dex */
    public interface MobileDataAlwaysOnlineDialogListener {
        void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50958;
    }

    public static MobileDataAlwaysOnlineDialogFragment newInstance(int i) {
        MobileDataAlwaysOnlineDialogFragment mobileDataAlwaysOnlineDialogFragment = new MobileDataAlwaysOnlineDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        mobileDataAlwaysOnlineDialogFragment.setArguments(bundle);
        return mobileDataAlwaysOnlineDialogFragment;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt("sub_id_key");
        this.mSubId = i;
        this.mPhoneId = SubscriptionManager.getPhoneId(i);
        ProxySimStateManager proxySimStateManager = ProxySimStateManager.getInstance(getContext());
        this.mProxySimStateMgr = proxySimStateManager;
        proxySimStateManager.setLifecycle(getLifecycle());
        ProxySimStateManager.OnSimStateChangedListener onSimStateChangedListener = new ProxySimStateManager.OnSimStateChangedListener() { // from class: com.android.settings.network.telephony.MobileDataAlwaysOnlineDialogFragment.1
            @Override // com.unisoc.settings.network.ProxySimStateManager.OnSimStateChangedListener
            public void onChanged() {
                String simState = MobileDataAlwaysOnlineDialogFragment.this.mProxySimStateMgr.getSimState(MobileDataAlwaysOnlineDialogFragment.this.mPhoneId);
                Log.d("MobileDataAlwaysOnlineDialogFragment", "onAttach onSubscriptionsChanged: mPhoneId =" + MobileDataAlwaysOnlineDialogFragment.this.mPhoneId + " simState =" + simState);
                if ("ABSENT".equals(simState)) {
                    Dialog dialog = MobileDataAlwaysOnlineDialogFragment.this.getDialog();
                    if (dialog != null) {
                        dialog.dismiss();
                    } else {
                        Log.d("MobileDataAlwaysOnlineDialogFragment", "onAttach onSubscriptionsChanged: dialog is null");
                    }
                }
            }
        };
        this.mOnSimStateChangeListener = onSimStateChangedListener;
        this.mProxySimStateMgr.addSimStateChangeListener(onSimStateChangedListener);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R$string.mobile_data_always_online_dialog)).setTitle(17039380).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this);
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            setMobileDataAlwaysOnline(this.mSubId, false);
        }
    }

    public void setController(MobileDataAlwaysOnlinePreferenceController mobileDataAlwaysOnlinePreferenceController) {
        this.mListener = mobileDataAlwaysOnlinePreferenceController;
    }

    private void setMobileDataAlwaysOnline(int i, boolean z) {
        GlobalSettingsHelper.setInt(getContext(), "mobile_data_always_online", i, z ? 1 : 0);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        MobileDataAlwaysOnlineDialogListener mobileDataAlwaysOnlineDialogListener = this.mListener;
        if (mobileDataAlwaysOnlineDialogListener != null) {
            mobileDataAlwaysOnlineDialogListener.onDialogDismiss(this);
        }
        super.onDismiss(dialogInterface);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Log.d("MobileDataAlwaysOnlineDialogFragment", "onDestroy");
        this.mProxySimStateMgr.removeSimStateChangeListener(this.mOnSimStateChangeListener);
    }
}
