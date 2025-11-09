package com.android.settings.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Slog;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.R$string;
/* loaded from: classes.dex */
public class NotificationAccessConfirmationActivity extends Activity implements DialogInterface {
    private ComponentName mComponentName;
    private NotificationManager mNm;
    private int mUserId;

    @Override // android.app.Activity
    public void onBackPressed() {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addSystemFlags(524288);
        this.mNm = (NotificationManager) getSystemService("notification");
        this.mComponentName = (ComponentName) getIntent().getParcelableExtra("component_name");
        this.mUserId = getIntent().getIntExtra("user_id", -10000);
        ComponentName componentName = this.mComponentName;
        if (componentName == null || componentName.getPackageName() == null) {
            finish();
            return;
        }
        try {
            CharSequence loadSafeLabel = getPackageManager().getApplicationInfo(this.mComponentName.getPackageName(), 0).loadSafeLabel(getPackageManager(), 1000.0f, 5);
            if (TextUtils.isEmpty(loadSafeLabel)) {
                finish();
                return;
            }
            AlertController.AlertParams alertParams = new AlertController.AlertParams(this);
            alertParams.mTitle = getString(R$string.notification_listener_security_warning_title, new Object[]{loadSafeLabel});
            alertParams.mMessage = getString(R$string.notification_listener_security_warning_summary, new Object[]{loadSafeLabel});
            alertParams.mPositiveButtonText = getString(R$string.allow);
            alertParams.mPositiveButtonListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.NotificationAccessConfirmationActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NotificationAccessConfirmationActivity.this.lambda$onCreate$0(dialogInterface, i);
                }
            };
            alertParams.mNegativeButtonText = getString(R$string.deny);
            alertParams.mNegativeButtonListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.notification.NotificationAccessConfirmationActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NotificationAccessConfirmationActivity.this.lambda$onCreate$1(dialogInterface, i);
                }
            };
            AlertController.create(this, this, getWindow()).installContent(alertParams);
            getWindow().setCloseOnTouchOutside(false);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("NotificationAccessConfirmationActivity", "Couldn't find app with package name for " + this.mComponentName, e);
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(DialogInterface dialogInterface, int i) {
        onAllow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$1(DialogInterface dialogInterface, int i) {
        cancel();
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        getWindow().addFlags(524288);
    }

    @Override // android.app.Activity
    public void onPause() {
        getWindow().clearFlags(524288);
        super.onPause();
    }

    private void onAllow() {
        try {
            if (!"android.permission.BIND_NOTIFICATION_LISTENER_SERVICE".equals(getPackageManager().getServiceInfo(this.mComponentName, 0).permission)) {
                Slog.e("NotificationAccessConfirmationActivity", "Service " + this.mComponentName + " lacks permission android.permission.BIND_NOTIFICATION_LISTENER_SERVICE");
                return;
            }
            this.mNm.setNotificationListenerAccessGranted(this.mComponentName, true);
            finish();
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e("NotificationAccessConfirmationActivity", "Failed to get service info for " + this.mComponentName, e);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return AlertActivity.dispatchPopulateAccessibilityEvent(this, accessibilityEvent);
    }

    @Override // android.content.DialogInterface
    public void cancel() {
        finish();
    }

    @Override // android.content.DialogInterface
    public void dismiss() {
        if (isFinishing()) {
            return;
        }
        finish();
    }
}
