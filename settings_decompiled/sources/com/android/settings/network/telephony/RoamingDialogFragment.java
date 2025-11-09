package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class RoamingDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private CarrierConfigManager mCarrierConfigManager;
    private int mSubId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1583;
    }

    public static RoamingDialogFragment newInstance(int i) {
        RoamingDialogFragment roamingDialogFragment = new RoamingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        roamingDialogFragment.setArguments(bundle);
        return roamingDialogFragment;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSubId = getArguments().getInt("sub_id_key");
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        ServiceState serviceStateForSubscriber;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int i = R$string.roaming_alert_title;
        Resources resourcesForSubId = SubscriptionManager.getResourcesForSubId(getContext(), this.mSubId);
        int i2 = R$string.roaming_warning;
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
        if (configForSubId != null && configForSubId.getBoolean("check_pricing_with_carrier_data_roaming_bool")) {
            i2 = R$string.roaming_check_price_warning;
        }
        if (getResources().getBoolean(R$bool.config_enable_roaming_warning_message_update) && (serviceStateForSubscriber = TelephonyManager.from(getContext()).getServiceStateForSubscriber(this.mSubId)) != null && serviceStateForSubscriber.getRoaming()) {
            i2 = R$string.roaming_warning_in_roaming_state;
        }
        builder.setMessage(resourcesForSubId.getString(i2)).setTitle(i).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this);
        AlertDialog create = builder.create();
        create.setCanceledOnTouchOutside(false);
        return create;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        TelephonyManager createForSubscriptionId;
        if (i != -1 || (createForSubscriptionId = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId)) == null) {
            return;
        }
        createForSubscriptionId.setDataRoamingEnabled(true);
    }
}
