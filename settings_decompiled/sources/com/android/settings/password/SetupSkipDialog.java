package com.android.settings.password;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class SetupSkipDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 573;
    }

    public static SetupSkipDialog newInstance(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        SetupSkipDialog setupSkipDialog = new SetupSkipDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("frp_supported", z);
        bundle.putBoolean("lock_type_pattern", z2);
        bundle.putBoolean("lock_type_alphanumeric", z3);
        bundle.putBoolean("for_fingerprint", z4);
        bundle.putBoolean("for_face", z5);
        bundle.putBoolean("for_biometrics", z6);
        setupSkipDialog.setArguments(bundle);
        return setupSkipDialog;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return onCreateDialogBuilder().create();
    }

    public AlertDialog.Builder onCreateDialogBuilder() {
        int pinSkipTitleRes;
        int pinSkipMessageRes;
        int i;
        Bundle arguments = getArguments();
        boolean z = arguments.getBoolean("for_face");
        boolean z2 = arguments.getBoolean("for_fingerprint");
        boolean z3 = arguments.getBoolean("for_biometrics");
        if (z || z2 || z3) {
            boolean z4 = false;
            boolean z5 = z || z3;
            if (z2 || z3) {
                z4 = true;
            }
            if (arguments.getBoolean("lock_type_pattern")) {
                pinSkipTitleRes = getPatternSkipTitleRes(z5, z4);
                pinSkipMessageRes = getPatternSkipMessageRes(z5, z4);
            } else if (arguments.getBoolean("lock_type_alphanumeric")) {
                pinSkipTitleRes = getPasswordSkipTitleRes(z5, z4);
                pinSkipMessageRes = getPasswordSkipMessageRes(z5, z4);
            } else {
                pinSkipTitleRes = getPinSkipTitleRes(z5, z4);
                pinSkipMessageRes = getPinSkipMessageRes(z5, z4);
            }
            return new AlertDialog.Builder(getContext()).setPositiveButton(R$string.skip_lock_screen_dialog_button_label, this).setNegativeButton(R$string.cancel_lock_screen_dialog_button_label, this).setTitle(pinSkipTitleRes).setMessage(pinSkipMessageRes);
        }
        AlertDialog.Builder title = new AlertDialog.Builder(getContext()).setPositiveButton(R$string.skip_anyway_button_label, this).setNegativeButton(R$string.go_back_button_label, this).setTitle(R$string.lock_screen_intro_skip_title);
        if (arguments.getBoolean("frp_supported")) {
            i = R$string.lock_screen_intro_skip_dialog_text_frp;
        } else {
            i = R$string.lock_screen_intro_skip_dialog_text;
        }
        return title.setMessage(i);
    }

    private int getPatternSkipTitleRes(boolean z, boolean z2) {
        if (z && z2) {
            return R$string.lock_screen_pattern_skip_biometrics_title;
        }
        if (z) {
            return R$string.lock_screen_pattern_skip_face_title;
        }
        if (z2) {
            return R$string.lock_screen_pattern_skip_fingerprint_title;
        }
        return R$string.lock_screen_pattern_skip_title;
    }

    private int getPatternSkipMessageRes(boolean z, boolean z2) {
        if (z && z2) {
            return R$string.lock_screen_pattern_skip_biometrics_message;
        }
        if (z) {
            return R$string.lock_screen_pattern_skip_face_message;
        }
        if (z2) {
            return R$string.lock_screen_pattern_skip_fingerprint_message;
        }
        return R$string.lock_screen_pattern_skip_message;
    }

    private int getPasswordSkipTitleRes(boolean z, boolean z2) {
        if (z && z2) {
            return R$string.lock_screen_password_skip_biometrics_title;
        }
        if (z) {
            return R$string.lock_screen_password_skip_face_title;
        }
        if (z2) {
            return R$string.lock_screen_password_skip_fingerprint_title;
        }
        return R$string.lock_screen_password_skip_title;
    }

    private int getPasswordSkipMessageRes(boolean z, boolean z2) {
        if (z && z2) {
            return R$string.lock_screen_password_skip_biometrics_message;
        }
        if (z) {
            return R$string.lock_screen_password_skip_face_message;
        }
        if (z2) {
            return R$string.lock_screen_password_skip_fingerprint_message;
        }
        return R$string.lock_screen_password_skip_message;
    }

    private int getPinSkipTitleRes(boolean z, boolean z2) {
        if (z && z2) {
            return R$string.lock_screen_pin_skip_biometrics_title;
        }
        if (z) {
            return R$string.lock_screen_pin_skip_face_title;
        }
        if (z2) {
            return R$string.lock_screen_pin_skip_fingerprint_title;
        }
        return R$string.lock_screen_pin_skip_title;
    }

    private int getPinSkipMessageRes(boolean z, boolean z2) {
        if (z && z2) {
            return R$string.lock_screen_pin_skip_biometrics_message;
        }
        if (z) {
            return R$string.lock_screen_pin_skip_face_message;
        }
        if (z2) {
            return R$string.lock_screen_pin_skip_fingerprint_message;
        }
        return R$string.lock_screen_pin_skip_message;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        FragmentActivity activity = getActivity();
        if (i != -2) {
            if (i != -1) {
                return;
            }
            activity.setResult(11);
            activity.finish();
            return;
        }
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.requestFocus();
            ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(currentFocus, 1);
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "skip_dialog");
    }
}
