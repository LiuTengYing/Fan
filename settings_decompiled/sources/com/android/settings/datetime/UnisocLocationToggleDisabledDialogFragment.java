package com.android.settings.datetime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class UnisocLocationToggleDisabledDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private final GpsUpdateTimeCancelCallback mCancleCallback;
    private final Context mContext;
    private int mDialogId;

    /* loaded from: classes.dex */
    public interface GpsUpdateTimeCancelCallback {
        void gpsUpdateTimeCancel();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public UnisocLocationToggleDisabledDialogFragment(Context context, int i, GpsUpdateTimeCancelCallback gpsUpdateTimeCancelCallback) {
        this.mContext = context;
        this.mDialogId = i;
        this.mCancleCallback = gpsUpdateTimeCancelCallback;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5 = this.mDialogId;
        if (i5 == 2) {
            i = R$string.location_time_zone_detection_location_is_off_dialog_title;
            i2 = R$string.location_date_time_detection_location_is_off_dialog_message;
            i3 = R$string.location_time_zone_detection_location_is_off_dialog_ok_button;
            i4 = R$string.location_time_zone_detection_location_is_off_dialog_cancel_button;
        } else if (i5 != 1) {
            return null;
        } else {
            i = R$string.proxy_error;
            i2 = R$string.gps_time_sync_attention_gps_on;
            i3 = 17039379;
            i4 = 17039369;
        }
        return new AlertDialog.Builder(getActivity()).setTitle(i).setIcon(R$drawable.ic_warning_24dp).setMessage(i2).setPositiveButton(i3, this).setNegativeButton(i4, this).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            if (this.mDialogId == 2) {
                this.mContext.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        } else if (i == -2 && this.mDialogId == 1) {
            this.mCancleCallback.gpsUpdateTimeCancel();
        }
    }
}
