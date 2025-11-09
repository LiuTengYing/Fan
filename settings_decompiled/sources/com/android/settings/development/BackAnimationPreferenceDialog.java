package com.android.settings.development;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class BackAnimationPreferenceDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1925;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    private BackAnimationPreferenceDialog() {
    }

    public static void show(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("BackAnimationDlg") == null) {
            BackAnimationPreferenceDialog backAnimationPreferenceDialog = new BackAnimationPreferenceDialog();
            backAnimationPreferenceDialog.setTargetFragment(fragment, 0);
            backAnimationPreferenceDialog.show(supportFragmentManager, "BackAnimationDlg");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R$string.back_navigation_animation).setMessage(R$string.back_navigation_animation_dialog).setPositiveButton(17039370, this).create();
    }
}
