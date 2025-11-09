package com.android.settings.applications.specialaccess.zenaccess;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class ScaryWarningDialogFragment extends InstrumentedDialogFragment {
    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 554;
    }

    public ScaryWarningDialogFragment setPkgInfo(String str, CharSequence charSequence, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("p", str);
        if (!TextUtils.isEmpty(charSequence)) {
            str = charSequence.toString();
        }
        bundle.putString("l", str);
        setTargetFragment(fragment, 0);
        setArguments(bundle);
        return this;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        final String string = arguments.getString("p");
        String string2 = getResources().getString(R$string.zen_access_warning_dialog_title, arguments.getString("l"));
        String string3 = getResources().getString(R$string.zen_access_warning_dialog_summary);
        final ZenAccessDetails zenAccessDetails = (ZenAccessDetails) getTargetFragment();
        return new AlertDialog.Builder(getContext()).setMessage(string3).setTitle(string2).setCancelable(true).setPositiveButton(R$string.allow, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.zenaccess.ScaryWarningDialogFragment$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ScaryWarningDialogFragment.this.lambda$onCreateDialog$0(string, zenAccessDetails, dialogInterface, i);
            }
        }).setNegativeButton(R$string.deny, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.specialaccess.zenaccess.ScaryWarningDialogFragment$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ScaryWarningDialogFragment.lambda$onCreateDialog$1(dialogInterface, i);
            }
        }).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(String str, ZenAccessDetails zenAccessDetails, DialogInterface dialogInterface, int i) {
        ZenAccessController.setAccess(getContext(), str, true);
        zenAccessDetails.refreshUi();
    }
}
