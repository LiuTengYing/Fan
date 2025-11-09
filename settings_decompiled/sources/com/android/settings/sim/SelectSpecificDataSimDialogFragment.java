package com.android.settings.sim;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class SelectSpecificDataSimDialogFragment extends SimDialogFragment implements DialogInterface.OnClickListener {
    private SubscriptionInfo mSubscriptionInfo;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1709;
    }

    public static SelectSpecificDataSimDialogFragment newInstance() {
        SelectSpecificDataSimDialogFragment selectSpecificDataSimDialogFragment = new SelectSpecificDataSimDialogFragment();
        selectSpecificDataSimDialogFragment.setArguments(SimDialogFragment.initArguments(0, R$string.select_specific_sim_for_data_title));
        return selectSpecificDataSimDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog create = new AlertDialog.Builder(getContext()).setNegativeButton(R$string.sim_action_no_thanks, (DialogInterface.OnClickListener) null).create();
        updateDialog(create);
        return create;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        SimDialogActivity simDialogActivity = (SimDialogActivity) getActivity();
        SubscriptionInfo targetSubscriptionInfo = getTargetSubscriptionInfo();
        if (targetSubscriptionInfo != null) {
            simDialogActivity.onSubscriptionSelected(getDialogType(), targetSubscriptionInfo.getSubscriptionId());
        }
    }

    private SubscriptionInfo getNonDefaultDataSubscriptionInfo(final SubscriptionInfo subscriptionInfo) {
        List<SubscriptionInfo> activeSubscriptionInfoList = getSubscriptionManager().getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null || subscriptionInfo == null) {
            return null;
        }
        return activeSubscriptionInfoList.stream().filter(new Predicate() { // from class: com.android.settings.sim.SelectSpecificDataSimDialogFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getNonDefaultDataSubscriptionInfo$0;
                lambda$getNonDefaultDataSubscriptionInfo$0 = SelectSpecificDataSimDialogFragment.lambda$getNonDefaultDataSubscriptionInfo$0(subscriptionInfo, (SubscriptionInfo) obj);
                return lambda$getNonDefaultDataSubscriptionInfo$0;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getNonDefaultDataSubscriptionInfo$0(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
        return subscriptionInfo2.getSubscriptionId() != subscriptionInfo.getSubscriptionId();
    }

    private SubscriptionInfo getDefaultDataSubInfo() {
        return getSubscriptionManager().getDefaultDataSubscriptionInfo();
    }

    private void updateDialog(AlertDialog alertDialog) {
        Log.d("PreferredSimDialogFrag", "Dialog updated, dismiss status: " + this.mWasDismissed);
        if (this.mWasDismissed) {
            return;
        }
        if (alertDialog == null) {
            Log.d("PreferredSimDialogFrag", "Dialog is null.");
            dismiss();
        }
        SubscriptionInfo defaultDataSubInfo = getDefaultDataSubInfo();
        SubscriptionInfo nonDefaultDataSubscriptionInfo = getNonDefaultDataSubscriptionInfo(defaultDataSubInfo);
        if (nonDefaultDataSubscriptionInfo == null || defaultDataSubInfo == null) {
            Log.d("PreferredSimDialogFrag", "one of target SubscriptionInfos is null");
            dismiss();
            return;
        }
        Log.d("PreferredSimDialogFrag", "newSubId: " + nonDefaultDataSubscriptionInfo.getSubscriptionId() + "currentDataSubID: " + defaultDataSubInfo.getSubscriptionId());
        setTargetSubscriptionInfo(nonDefaultDataSubscriptionInfo);
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(nonDefaultDataSubscriptionInfo, getContext());
        CharSequence uniqueSubscriptionDisplayName2 = SubscriptionUtil.getUniqueSubscriptionDisplayName(defaultDataSubInfo, getContext());
        CharSequence string = getContext().getString(R$string.select_specific_sim_for_data_button, uniqueSubscriptionDisplayName);
        String string2 = getContext().getString(R$string.select_specific_sim_for_data_msg, uniqueSubscriptionDisplayName, uniqueSubscriptionDisplayName2);
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.sim_confirm_dialog_multiple_enabled_profiles_supported, (ViewGroup) null);
        TextView textView = inflate != null ? (TextView) inflate.findViewById(R$id.msg) : null;
        if (!TextUtils.isEmpty(string2) && textView != null) {
            textView.setText(string2);
            textView.setVisibility(0);
        }
        alertDialog.setView(inflate);
        TextView textView2 = (TextView) LayoutInflater.from(getContext()).inflate(R$layout.sim_confirm_dialog_title_multiple_enabled_profiles_supported, (ViewGroup) null).findViewById(R$id.title);
        textView2.setText(getContext().getString(getTitleResId(), uniqueSubscriptionDisplayName));
        alertDialog.setCustomTitle(textView2);
        alertDialog.setButton(-1, string, this);
    }

    private void setTargetSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
        this.mSubscriptionInfo = subscriptionInfo;
    }

    private SubscriptionInfo getTargetSubscriptionInfo() {
        return this.mSubscriptionInfo;
    }

    @Override // com.android.settings.sim.SimDialogFragment
    public void updateDialog() {
        updateDialog((AlertDialog) getDialog());
    }

    protected SubscriptionManager getSubscriptionManager() {
        return (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
    }
}
