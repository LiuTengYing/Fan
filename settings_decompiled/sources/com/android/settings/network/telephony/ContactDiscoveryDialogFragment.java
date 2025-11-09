package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.ims.ImsManager;
import android.text.TextUtils;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class ContactDiscoveryDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private CharSequence mCarrierName;
    private ImsManager mImsManager;
    private int mSubId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static ContactDiscoveryDialogFragment newInstance(int i, CharSequence charSequence) {
        ContactDiscoveryDialogFragment contactDiscoveryDialogFragment = new ContactDiscoveryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        bundle.putCharSequence("carrier_name_key", charSequence);
        contactDiscoveryDialogFragment.setArguments(bundle);
        return contactDiscoveryDialogFragment;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        this.mSubId = arguments.getInt("sub_id_key");
        this.mCarrierName = arguments.getCharSequence("carrier_name_key");
        this.mImsManager = getImsManager(context);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String string;
        String string2;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (!TextUtils.isEmpty(this.mCarrierName)) {
            string = getContext().getString(R$string.contact_discovery_opt_in_dialog_title, this.mCarrierName);
            string2 = getContext().getString(R$string.contact_discovery_opt_in_dialog_message, this.mCarrierName);
        } else {
            string = getContext().getString(R$string.contact_discovery_opt_in_dialog_title_no_carrier_defined);
            string2 = getContext().getString(R$string.contact_discovery_opt_in_dialog_message_no_carrier_defined);
        }
        builder.setMessage(string2).setTitle(string).setIconAttribute(16843605).setPositiveButton(R$string.confirmation_turn_on, this).setNegativeButton(17039360, this);
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            MobileNetworkUtils.setContactDiscoveryEnabled(this.mImsManager, this.mSubId, true);
        }
    }

    public ImsManager getImsManager(Context context) {
        return (ImsManager) context.getSystemService(ImsManager.class);
    }

    public static String getFragmentTag(int i) {
        return "discovery_dialog:" + i;
    }
}
