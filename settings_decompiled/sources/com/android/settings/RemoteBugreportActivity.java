package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class RemoteBugreportActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        final int i;
        super.onCreate(bundle);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        int intExtra = getIntent().getIntExtra("android.app.extra.bugreport_notification_type", -1);
        if (intExtra == 2) {
            new AlertDialog.Builder(this).setMessage(devicePolicyManager.getResources().getString("Settings.SHARING_REMOTE_BUGREPORT_MESSAGE", new Supplier() { // from class: com.android.settings.RemoteBugreportActivity$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreate$0;
                    lambda$onCreate$0 = RemoteBugreportActivity.this.lambda$onCreate$0();
                    return lambda$onCreate$0;
                }
            })).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.RemoteBugreportActivity.2
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            }).setNegativeButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.RemoteBugreportActivity.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    RemoteBugreportActivity.this.finish();
                }
            }).create().show();
        } else if (intExtra == 1 || intExtra == 3) {
            if (intExtra == 1) {
                i = R$string.share_remote_bugreport_dialog_message;
            } else {
                i = R$string.share_remote_bugreport_dialog_message_finished;
            }
            new AlertDialog.Builder(this).setTitle(devicePolicyManager.getResources().getString("Settings.SHARE_REMOTE_BUGREPORT_DIALOG_TITLE", new Supplier() { // from class: com.android.settings.RemoteBugreportActivity$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreate$1;
                    lambda$onCreate$1 = RemoteBugreportActivity.this.lambda$onCreate$1();
                    return lambda$onCreate$1;
                }
            })).setMessage(devicePolicyManager.getResources().getString(intExtra == 1 ? "Settings.SHARE_REMOTE_BUGREPORT_NOT_FINISHED_REQUEST_CONSENT" : "Settings.SHARE_REMOTE_BUGREPORT_FINISHED_REQUEST_CONSENT", new Supplier() { // from class: com.android.settings.RemoteBugreportActivity$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreate$2;
                    lambda$onCreate$2 = RemoteBugreportActivity.this.lambda$onCreate$2(i);
                    return lambda$onCreate$2;
                }
            })).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.RemoteBugreportActivity.5
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            }).setNegativeButton(R$string.decline_remote_bugreport_action, new DialogInterface.OnClickListener() { // from class: com.android.settings.RemoteBugreportActivity.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            }).setPositiveButton(R$string.share_remote_bugreport_action, new DialogInterface.OnClickListener() { // from class: com.android.settings.RemoteBugreportActivity.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            }).create().show();
        } else {
            Log.e("RemoteBugreportActivity", "Incorrect dialog type, no dialog shown. Received: " + intExtra);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$0() {
        return getString(R$string.sharing_remote_bugreport_dialog_message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$1() {
        return getString(R$string.share_remote_bugreport_dialog_title);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$2(int i) {
        return getString(i);
    }
}
