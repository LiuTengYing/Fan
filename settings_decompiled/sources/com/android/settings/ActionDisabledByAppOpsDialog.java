package com.android.settings;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
/* loaded from: classes.dex */
public class ActionDisabledByAppOpsDialog extends Activity implements DialogInterface.OnDismissListener {
    private ActionDisabledByAppOpsHelper mDialogHelper;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionDisabledByAppOpsHelper actionDisabledByAppOpsHelper = new ActionDisabledByAppOpsHelper(this);
        this.mDialogHelper = actionDisabledByAppOpsHelper;
        actionDisabledByAppOpsHelper.prepareDialogBuilder().setOnDismissListener(this).show();
        updateAppOps();
    }

    private void updateAppOps() {
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("android.intent.extra.PACKAGE_NAME");
        ((AppOpsManager) getSystemService(AppOpsManager.class)).setMode(119, intent.getIntExtra("android.intent.extra.UID", -1), stringExtra, 1);
    }

    @Override // android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mDialogHelper.updateDialog();
        updateAppOps();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }
}
