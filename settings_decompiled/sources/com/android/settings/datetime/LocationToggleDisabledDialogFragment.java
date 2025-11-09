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
public class LocationToggleDisabledDialogFragment extends InstrumentedDialogFragment {
    private final Context mContext;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1876;
    }

    public LocationToggleDisabledDialogFragment(Context context) {
        this.mContext = context;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R$string.location_time_zone_detection_location_is_off_dialog_title).setIcon(R$drawable.ic_warning_24dp).setMessage(R$string.location_time_zone_detection_location_is_off_dialog_message).setPositiveButton(R$string.location_time_zone_detection_location_is_off_dialog_ok_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.datetime.LocationToggleDisabledDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LocationToggleDisabledDialogFragment.this.lambda$onCreateDialog$0(dialogInterface, i);
            }
        }).setNegativeButton(R$string.location_time_zone_detection_location_is_off_dialog_cancel_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.datetime.LocationToggleDisabledDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LocationToggleDisabledDialogFragment.lambda$onCreateDialog$1(dialogInterface, i);
            }
        }).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        this.mContext.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }
}
