package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settingslib.HelpUtils;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ActionDisabledByAppOpsHelper {
    private final Activity mActivity;
    private final ViewGroup mDialogView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionDisabledByAppOpsHelper(Activity activity) {
        this.mActivity = activity;
        this.mDialogView = (ViewGroup) LayoutInflater.from(activity).inflate(R$layout.support_details_dialog, (ViewGroup) null);
    }

    public AlertDialog.Builder prepareDialogBuilder() {
        final String string = this.mActivity.getString(R$string.help_url_action_disabled_by_restricted_settings);
        AlertDialog.Builder view = new AlertDialog.Builder(this.mActivity).setPositiveButton(R$string.okay, (DialogInterface.OnClickListener) null).setView(this.mDialogView);
        if (!TextUtils.isEmpty(string)) {
            view.setNeutralButton(R$string.learn_more, new DialogInterface.OnClickListener() { // from class: com.android.settings.ActionDisabledByAppOpsHelper$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ActionDisabledByAppOpsHelper.this.lambda$prepareDialogBuilder$0(string, dialogInterface, i);
                }
            });
        }
        initializeDialogViews(this.mDialogView);
        return view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareDialogBuilder$0(String str, DialogInterface dialogInterface, int i) {
        Activity activity = this.mActivity;
        Intent helpIntent = HelpUtils.getHelpIntent(activity, str, activity.getClass().getName());
        if (helpIntent != null) {
            this.mActivity.startActivity(helpIntent);
        }
    }

    public void updateDialog() {
        initializeDialogViews(this.mDialogView);
    }

    private void initializeDialogViews(View view) {
        setSupportTitle(view);
        setSupportDetails(view);
    }

    void setSupportTitle(View view) {
        TextView textView = (TextView) view.findViewById(R$id.admin_support_dialog_title);
        if (textView == null) {
            return;
        }
        textView.setText(R$string.blocked_by_restricted_settings_title);
    }

    void setSupportDetails(View view) {
        ((TextView) view.findViewById(R$id.admin_support_msg)).setText(R$string.blocked_by_restricted_settings_content);
    }
}
