package com.android.settings.security;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class UnificationConfirmationDialog extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 532;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        final int i;
        int i2;
        final SecuritySettings securitySettings = (SecuritySettings) getParentFragment();
        boolean z = getArguments().getBoolean("compliant");
        String str = z ? "Settings.WORK_PROFILE_UNIFY_LOCKS_DETAIL" : "Settings.WORK_PROFILE_UNIFY_LOCKS_NONCOMPLIANT";
        if (z) {
            i = R$string.lock_settings_profile_unification_dialog_body;
        } else {
            i = R$string.lock_settings_profile_unification_dialog_uncompliant_body;
        }
        AlertDialog.Builder message = new AlertDialog.Builder(getActivity()).setTitle(R$string.lock_settings_profile_unification_dialog_title).setMessage(((DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class)).getResources().getString(str, new Supplier() { // from class: com.android.settings.security.UnificationConfirmationDialog$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$onCreateDialog$0;
                lambda$onCreateDialog$0 = UnificationConfirmationDialog.this.lambda$onCreateDialog$0(i);
                return lambda$onCreateDialog$0;
            }
        }));
        if (z) {
            i2 = R$string.lock_settings_profile_unification_dialog_confirm;
        } else {
            i2 = R$string.lock_settings_profile_unification_dialog_uncompliant_confirm;
        }
        return message.setPositiveButton(i2, new DialogInterface.OnClickListener() { // from class: com.android.settings.security.UnificationConfirmationDialog$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                SecuritySettings.this.startUnification();
            }
        }).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreateDialog$0(int i) {
        return getString(i);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        ((SecuritySettings) getParentFragment()).updateUnificationPreference();
    }
}
