package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telephony.PrimarySubManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.unisoc.telephony.RadioInteractor;
import java.io.UnsupportedEncodingException;
/* loaded from: classes.dex */
public class PsDataOffDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private Context mContext;
    private int mExceptService;
    private CharSequence[] mExceptServiceNames;
    private PsDataOffDialogListener mListener;
    private int mPhoneId;
    private int mSubId;

    /* loaded from: classes.dex */
    public interface PsDataOffDialogListener {
        void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50960;
    }

    public static PsDataOffDialogFragment newInstance(int i) {
        PsDataOffDialogFragment psDataOffDialogFragment = new PsDataOffDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        psDataOffDialogFragment.setArguments(bundle);
        return psDataOffDialogFragment;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        int i = getArguments().getInt("sub_id_key");
        this.mSubId = i;
        int phoneId = SubscriptionManager.getPhoneId(i);
        this.mPhoneId = phoneId;
        this.mContext = context;
        this.mExceptService = getExceptServices(phoneId, context);
        Log.d("PsDataOffDialogFragment", "mExceptService is " + this.mExceptService);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String str;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int i = R$string.ps_data_off_dialog;
        CharSequence[] textArray = getResources().getTextArray(R$array.except_service_name);
        this.mExceptServiceNames = textArray;
        if (textArray != null) {
            int length = textArray.length;
            int i2 = this.mExceptService;
            if (length > i2) {
                str = textArray[i2].toString();
                builder.setMessage(getResources().getString(i) + str).setTitle(17039380).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this);
                return builder.create();
            }
        }
        str = "";
        builder.setMessage(getResources().getString(i) + str).setTitle(17039380).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this);
        return builder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            setPsDataOff(this.mPhoneId, true, this.mExceptService, this.mContext);
        }
    }

    public void setController(PsDataOffPreferenceController psDataOffPreferenceController) {
        this.mListener = psDataOffPreferenceController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setPsDataOff(int i, boolean z, int i2, Context context) {
        new RadioInteractor(context).setPsDataOff(i, z, i2);
        if (z) {
            setTelephonyProperty(i, PsDataOffPreferenceController.PS_DATA_OFF_ENABLED, Integer.toString(i2));
        } else {
            setTelephonyProperty(i, PsDataOffPreferenceController.PS_DATA_OFF_ENABLED, "-1");
        }
    }

    public static int getExceptServices(int i, Context context) {
        TelephonyManager from = TelephonyManager.from(context);
        PrimarySubManager from2 = PrimarySubManager.from(context);
        ServiceState serviceStateForSubscriber = from.getServiceStateForSubscriber(i);
        if (serviceStateForSubscriber != null && SubscriptionManager.isValidPhoneId(i)) {
            if (serviceStateForSubscriber.getDataRoaming()) {
                return from2.getRomingExceptService(i);
            }
            if (serviceStateForSubscriber.getDataRegState() == 0) {
                return from2.getHomeExceptService(i);
            }
        }
        return 0;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        PsDataOffDialogListener psDataOffDialogListener = this.mListener;
        if (psDataOffDialogListener != null) {
            psDataOffDialogListener.onDialogDismiss(this);
        }
        super.onDismiss(dialogInterface);
    }

    /* loaded from: classes.dex */
    public static class BootCompletedReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("PsDataOffDialogFragment", "action: " + action);
            if (SystemProperties.getBoolean("persist.radio.psdataoff.debug", false) && "android.telephony.action.SIM_APPLICATION_STATE_CHANGED".equals(action)) {
                int intExtra = intent.getIntExtra("phone", -1);
                int intExtra2 = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                int exceptServices = PsDataOffDialogFragment.getExceptServices(intExtra, context);
                String telephonyProperty = TelephonyManager.getTelephonyProperty(intExtra, PsDataOffPreferenceController.PS_DATA_OFF_ENABLED, "-1");
                if (intExtra2 == 10) {
                    Log.d("PsDataOffDialogFragment", "exceptService is " + exceptServices);
                    if ("-1".equals(telephonyProperty)) {
                        PsDataOffDialogFragment.setPsDataOff(intExtra, false, -1, context);
                    } else {
                        PsDataOffDialogFragment.setPsDataOff(intExtra, true, exceptServices, context);
                    }
                }
            }
        }
    }

    public static void setTelephonyProperty(int i, String str, String str2) {
        String str3 = SystemProperties.get(str);
        if (str2 == null) {
            str2 = "";
        }
        str2.replace(',', ' ');
        String[] split = str3 != null ? str3.split(",") : null;
        if (!SubscriptionManager.isValidPhoneId(i)) {
            Log.d("PsDataOffDialogFragment", "setTelephonyProperty: invalid phoneId=" + i + " property=" + str + " value: " + str2 + " prop=" + str3);
            return;
        }
        int i2 = 0;
        String str4 = "";
        while (i2 < i) {
            str4 = str4 + ((split == null || i2 >= split.length) ? "" : split[i2]) + ",";
            i2++;
        }
        String str5 = str4 + str2;
        if (split != null) {
            for (int i3 = i + 1; i3 < split.length; i3++) {
                str5 = str5 + "," + split[i3];
            }
        }
        int length = str5.length();
        try {
            length = str5.getBytes("utf-8").length;
        } catch (UnsupportedEncodingException unused) {
            Log.d("PsDataOffDialogFragment", "setTelephonyProperty: utf-8 not supported");
        }
        if (length > 91) {
            Log.d("PsDataOffDialogFragment", "setTelephonyProperty: property too long phoneId=" + i + " property=" + str + " value: " + str2 + " propVal=" + str5);
            return;
        }
        SystemProperties.set(str, str5);
    }
}
