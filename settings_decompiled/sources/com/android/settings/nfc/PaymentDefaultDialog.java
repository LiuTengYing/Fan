package com.android.settings.nfc;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.R$string;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.nfc.PaymentBackend;
/* loaded from: classes.dex */
public final class PaymentDefaultDialog extends AlertActivity implements DialogInterface.OnClickListener {
    private PaymentBackend mBackend;
    private PaymentBackend.PaymentInfo mNewDefault;

    /* JADX WARN: Multi-variable type inference failed */
    protected void onCreate(Bundle bundle) {
        int identifier;
        super.onCreate(bundle);
        getWindow().addPrivateFlags(524288);
        try {
            this.mBackend = new PaymentBackend(this);
        } catch (NullPointerException unused) {
            finish();
        }
        Intent intent = getIntent();
        ComponentName componentName = (ComponentName) intent.getParcelableExtra("component");
        String stringExtra = intent.getStringExtra(DashboardFragment.CATEGORY);
        UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        if (userHandle == null) {
            identifier = UserHandle.myUserId();
        } else {
            identifier = userHandle.getIdentifier();
        }
        setResult(0);
        if (buildDialog(componentName, stringExtra, identifier)) {
            return;
        }
        finish();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != -1) {
            return;
        }
        PaymentBackend paymentBackend = this.mBackend;
        PaymentBackend.PaymentInfo paymentInfo = this.mNewDefault;
        paymentBackend.setDefaultPaymentApp(paymentInfo.componentName, paymentInfo.userId);
        setResult(-1);
    }

    private boolean buildDialog(ComponentName componentName, String str, int i) {
        if (componentName == null || str == null) {
            Log.e("PaymentDefaultDialog", "Component or category are null");
            return false;
        } else if (!"payment".equals(str)) {
            Log.e("PaymentDefaultDialog", "Don't support defaults for category " + str);
            return false;
        } else {
            PaymentBackend.PaymentAppInfo paymentAppInfo = null;
            PaymentBackend.PaymentAppInfo paymentAppInfo2 = null;
            for (PaymentBackend.PaymentAppInfo paymentAppInfo3 : this.mBackend.getPaymentAppInfos()) {
                if (componentName.equals(paymentAppInfo3.componentName) && paymentAppInfo3.userHandle.getIdentifier() == i) {
                    paymentAppInfo = paymentAppInfo3;
                }
                if (paymentAppInfo3.isDefault && paymentAppInfo3.userHandle.getIdentifier() == i) {
                    paymentAppInfo2 = paymentAppInfo3;
                }
            }
            if (paymentAppInfo == null) {
                Log.e("PaymentDefaultDialog", "Component " + componentName + " is not a registered payment service.");
                return false;
            }
            PaymentBackend.PaymentInfo defaultPaymentApp = this.mBackend.getDefaultPaymentApp();
            if (defaultPaymentApp != null && defaultPaymentApp.componentName.equals(componentName) && defaultPaymentApp.userId == i) {
                Log.e("PaymentDefaultDialog", "Component " + componentName + " is already default.");
                return false;
            }
            PaymentBackend.PaymentInfo paymentInfo = new PaymentBackend.PaymentInfo();
            this.mNewDefault = paymentInfo;
            paymentInfo.componentName = componentName;
            paymentInfo.userId = i;
            AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
            if (paymentAppInfo2 == null) {
                alertParams.mTitle = getString(R$string.nfc_payment_set_default_label);
                alertParams.mMessage = String.format(getString(R$string.nfc_payment_set_default), sanitizePaymentAppCaption(paymentAppInfo.label.toString()));
                alertParams.mPositiveButtonText = getString(R$string.nfc_payment_btn_text_set_deault);
            } else {
                alertParams.mTitle = getString(R$string.nfc_payment_update_default_label);
                alertParams.mMessage = String.format(getString(R$string.nfc_payment_set_default_instead_of), sanitizePaymentAppCaption(paymentAppInfo.label.toString()), sanitizePaymentAppCaption(paymentAppInfo2.label.toString()));
                alertParams.mPositiveButtonText = getString(R$string.nfc_payment_btn_text_update);
            }
            alertParams.mNegativeButtonText = getString(R$string.cancel);
            alertParams.mPositiveButtonListener = this;
            alertParams.mNegativeButtonListener = this;
            setupAlert();
            return true;
        }
    }

    private String sanitizePaymentAppCaption(String str) {
        String trim = str.replace('\n', ' ').replace('\r', ' ').trim();
        return trim.length() > 40 ? trim.substring(0, 40) : trim;
    }
}
