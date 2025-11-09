package com.android.settings.sim;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.network.SubscriptionUtil;
/* loaded from: classes.dex */
public class PreferredSimDialogFragment extends SimDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1709;
    }

    public static PreferredSimDialogFragment newInstance() {
        PreferredSimDialogFragment preferredSimDialogFragment = new PreferredSimDialogFragment();
        preferredSimDialogFragment.setArguments(SimDialogFragment.initArguments(3, R$string.sim_preferred_title));
        return preferredSimDialogFragment;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(getTitleResId()).setPositiveButton(R$string.yes, this).setNegativeButton(R$string.no, (DialogInterface.OnClickListener) null).create();
        updateDialog(create);
        return create;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        SimDialogActivity simDialogActivity = (SimDialogActivity) getActivity();
        SubscriptionInfo preferredSubscription = getPreferredSubscription();
        if (preferredSubscription != null) {
            simDialogActivity.onSubscriptionSelected(getDialogType(), preferredSubscription.getSubscriptionId());
        }
    }

    public SubscriptionInfo getPreferredSubscription() {
        return getSubscriptionManager().getActiveSubscriptionInfoForSimSlotIndex(getActivity().getIntent().getIntExtra(SimDialogActivity.PREFERRED_SIM, -1));
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
        SubscriptionInfo preferredSubscription = getPreferredSubscription();
        if (preferredSubscription == null) {
            dismiss();
        } else {
            alertDialog.setMessage(getContext().getString(R$string.sim_preferred_message, SubscriptionUtil.getUniqueSubscriptionDisplayName(preferredSubscription, getContext())));
        }
    }

    @Override // com.android.settings.sim.SimDialogFragment
    public void updateDialog() {
        updateDialog((AlertDialog) getDialog());
    }

    protected SubscriptionManager getSubscriptionManager() {
        return (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
    }
}
