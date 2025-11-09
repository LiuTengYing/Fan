package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.WirelessUtils;
import com.unisoc.settings.network.ProxySimStateManager;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.util.List;
/* loaded from: classes.dex */
public class MobileDataDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private ProxySimStateManager.OnSimStateChangedListener mOnSimStateChangeListener;
    private ProxySimStateManager mProxySimStateMgr;
    private int mSubId;
    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager;
    private int mType;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1582;
    }

    public static MobileDataDialogFragment newInstance(int i, int i2) {
        MobileDataDialogFragment mobileDataDialogFragment = new MobileDataDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("dialog_type", i);
        bundle.putInt("subId", i2);
        mobileDataDialogFragment.setArguments(bundle);
        return mobileDataDialogFragment;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i = getArguments().getInt("subId");
        this.mSubId = i;
        final int phoneId = SubscriptionManager.getPhoneId(i);
        final int phoneId2 = SubscriptionManager.getPhoneId(SubscriptionManager.getDefaultDataSubscriptionId());
        this.mTelephonyManager = TelephonyManager.from(getContext()).createForSubscriptionId(this.mSubId);
        this.mSubscriptionManager = (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
        this.mWifiPickerTrackerHelper = new WifiPickerTrackerHelper(getSettingsLifecycle(), getContext(), null);
        ProxySimStateManager proxySimStateManager = ProxySimStateManager.getInstance(getContext());
        this.mProxySimStateMgr = proxySimStateManager;
        proxySimStateManager.setLifecycle(getLifecycle());
        ProxySimStateManager.OnSimStateChangedListener onSimStateChangedListener = new ProxySimStateManager.OnSimStateChangedListener() { // from class: com.android.settings.network.telephony.MobileDataDialogFragment.1
            @Override // com.unisoc.settings.network.ProxySimStateManager.OnSimStateChangedListener
            public void onChanged() {
                String simState = MobileDataDialogFragment.this.mProxySimStateMgr.getSimState(phoneId);
                String simState2 = MobileDataDialogFragment.this.mProxySimStateMgr.getSimState(phoneId2);
                Log.d("MobileDataDialogFragment", "onSubscriptionsChanged: ddsPhoneId =" + phoneId2 + " phoneId =" + phoneId + " simState =" + simState + " ddsSimState =" + simState2);
                if ("ABSENT".equals(simState) || "NOT_READY".equals(simState) || "ABSENT".equals(simState2) || "NOT_READY".equals(simState2)) {
                    Dialog dialog = MobileDataDialogFragment.this.getDialog();
                    if (dialog != null) {
                        dialog.dismiss();
                    } else {
                        Log.d("MobileDataDialogFragment", "onSubscriptionsChanged: dialog is null");
                    }
                }
            }
        };
        this.mOnSimStateChangeListener = onSimStateChangedListener;
        this.mProxySimStateMgr.addSimStateChangeListener(onSimStateChangedListener);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String charSequence;
        String charSequence2;
        Bundle arguments = getArguments();
        Context context = getContext();
        this.mType = arguments.getInt("dialog_type");
        int i = arguments.getInt("subId");
        this.mSubId = i;
        int i2 = this.mType;
        if (i2 != 0) {
            if (i2 == 1) {
                SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(i);
                SubscriptionInfo activeSubscriptionInfo2 = this.mSubscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
                if (activeSubscriptionInfo2 == null) {
                    charSequence = getContext().getResources().getString(R$string.sim_selection_required_pref);
                } else {
                    charSequence = SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptionInfo2, getContext()).toString();
                }
                if (activeSubscriptionInfo == null) {
                    charSequence2 = getContext().getResources().getString(R$string.sim_selection_required_pref);
                } else {
                    charSequence2 = SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptionInfo, getContext()).toString();
                }
                return new AlertDialog.Builder(context).setTitle(context.getString(R$string.sim_change_data_title, charSequence2)).setMessage(context.getString(R$string.sim_change_data_message, charSequence2, charSequence)).setPositiveButton(context.getString(R$string.sim_change_data_ok, charSequence2), this).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
            }
            throw new IllegalArgumentException("unknown type " + this.mType);
        }
        return new AlertDialog.Builder(context).setMessage(R$string.data_usage_disable_mobile).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Log.d("MobileDataDialogFragment", "onStart");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Log.d("MobileDataDialogFragment", "onDestroy");
        this.mProxySimStateMgr.removeSimStateChangeListener(this.mOnSimStateChangeListener);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.mType;
        if (i2 == 0) {
            MobileNetworkUtils.setMobileDataEnabled(getContext(), this.mSubId, false, false);
            WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
            if (wifiPickerTrackerHelper == null || wifiPickerTrackerHelper.isCarrierNetworkProvisionEnabled(this.mSubId)) {
                return;
            }
            this.mWifiPickerTrackerHelper.setCarrierNetworkEnabled(false);
        } else if (i2 == 1) {
            if (isPhoneStateInCall()) {
                Toast.makeText(getContext(), getContext().getResources().getString(R$string.do_not_switch_default_data_subscription), 1).show();
            } else if (UniMobileNetworkUtils.isDataSwitchEnabledForSubsidy(getContext(), this.mSubId)) {
                if (!WirelessUtils.isAirplaneModeOn(getContext())) {
                    this.mSubscriptionManager.setDefaultDataSubId(this.mSubId);
                    MobileNetworkUtils.setMobileDataEnabled(getContext(), this.mSubId, true, true);
                }
                WifiPickerTrackerHelper wifiPickerTrackerHelper2 = this.mWifiPickerTrackerHelper;
                if (wifiPickerTrackerHelper2 == null || wifiPickerTrackerHelper2.isCarrierNetworkProvisionEnabled(this.mSubId)) {
                    return;
                }
                this.mWifiPickerTrackerHelper.setCarrierNetworkEnabled(true);
            }
        } else {
            throw new IllegalArgumentException("unknown type " + this.mType);
        }
    }

    private boolean isPhoneStateInCall() {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList(true);
        if (activeSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (this.mTelephonyManager.getCallStateForSlot(subscriptionInfo.getSimSlotIndex()) != 0) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
