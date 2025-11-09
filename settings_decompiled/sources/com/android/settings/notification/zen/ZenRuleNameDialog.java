package com.android.settings.notification.zen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
/* loaded from: classes.dex */
public class ZenRuleNameDialog extends InstrumentedDialogFragment {
    protected static PositiveClickListener mPositiveClickListener;

    /* loaded from: classes.dex */
    public interface PositiveClickListener {
        void onOk(String str, Fragment fragment);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1269;
    }

    public static void show(Fragment fragment, String str, Uri uri, PositiveClickListener positiveClickListener) {
        Bundle bundle = new Bundle();
        bundle.putString("zen_rule_name", str);
        bundle.putParcelable("extra_zen_condition_id", uri);
        mPositiveClickListener = positiveClickListener;
        ZenRuleNameDialog zenRuleNameDialog = new ZenRuleNameDialog();
        zenRuleNameDialog.setArguments(bundle);
        zenRuleNameDialog.setTargetFragment(fragment, 0);
        zenRuleNameDialog.show(fragment.getFragmentManager(), "ZenRuleNameDialog");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        Uri uri = (Uri) arguments.getParcelable("extra_zen_condition_id");
        final String string = arguments.getString("zen_rule_name");
        final boolean z = string == null;
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(R$layout.zen_rule_name, (ViewGroup) null, false);
        final EditText editText = (EditText) inflate.findViewById(R$id.zen_mode_rule_name);
        if (!z) {
            editText.setText(string);
            editText.setSelection(editText.getText().length());
        }
        editText.setSelectAllOnFocus(true);
        return new AlertDialog.Builder(context).setTitle(getTitleResource(uri, z)).setView(inflate).setPositiveButton(z ? R$string.zen_mode_add : R$string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.zen.ZenRuleNameDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                CharSequence charSequence;
                String trimmedText = ZenRuleNameDialog.this.trimmedText(editText);
                if (TextUtils.isEmpty(trimmedText)) {
                    return;
                }
                if (z || (charSequence = string) == null || !charSequence.equals(trimmedText)) {
                    ZenRuleNameDialog.mPositiveClickListener.onOk(trimmedText, ZenRuleNameDialog.this.getTargetFragment());
                }
            }
        }).setNegativeButton(R$string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String trimmedText(EditText editText) {
        if (editText.getText() == null) {
            return null;
        }
        return editText.getText().toString().trim();
    }

    private int getTitleResource(Uri uri, boolean z) {
        boolean isValidEventConditionId = ZenModeConfig.isValidEventConditionId(uri);
        boolean isValidScheduleConditionId = ZenModeConfig.isValidScheduleConditionId(uri);
        int i = R$string.zen_mode_rule_name;
        if (z) {
            if (isValidEventConditionId) {
                return R$string.zen_mode_add_event_rule;
            }
            return isValidScheduleConditionId ? R$string.zen_mode_add_time_rule : i;
        }
        return i;
    }
}
