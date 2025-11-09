package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.appcompat.app.AlertDialog;
import com.android.settingslib.RestrictedLockUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class MonitoringCertInfoActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private int mUserId;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        this.mUserId = intExtra;
        UserHandle of = intExtra == -10000 ? null : UserHandle.of(intExtra);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        int intExtra2 = getIntent().getIntExtra("android.settings.extra.number_of_certificates", 1);
        if (RestrictedLockUtils.getProfileOrDeviceOwner(this, of) != null) {
            i = R$plurals.ssl_ca_cert_settings_button;
        } else {
            i = R$plurals.ssl_ca_cert_dialog_title;
        }
        CharSequence quantityText = getResources().getQuantityText(i, intExtra2);
        setTitle(quantityText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(quantityText);
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getQuantityText(R$plurals.ssl_ca_cert_settings_button, intExtra2), this);
        builder.setNeutralButton(R$string.cancel, null);
        builder.setOnDismissListener(this);
        if (devicePolicyManager.getProfileOwnerAsUser(this.mUserId) != null) {
            MessageFormat messageFormat = new MessageFormat(devicePolicyManager.getResources().getString("Settings.WORK_PROFILE_INSTALLED_CERTIFICATE_AUTHORITY_WARNING", new Supplier() { // from class: com.android.settings.MonitoringCertInfoActivity$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreate$0;
                    lambda$onCreate$0 = MonitoringCertInfoActivity.this.lambda$onCreate$0();
                    return lambda$onCreate$0;
                }
            }), Locale.getDefault());
            HashMap hashMap = new HashMap();
            hashMap.put("numberOfCertificates", Integer.valueOf(intExtra2));
            hashMap.put("orgName", devicePolicyManager.getProfileOwnerNameAsUser(this.mUserId));
            builder.setMessage(messageFormat.format(hashMap));
        } else if (devicePolicyManager.getDeviceOwnerComponentOnCallingUser() != null) {
            MessageFormat messageFormat2 = new MessageFormat(devicePolicyManager.getResources().getString("Settings.DEVICE_OWNER_INSTALLED_CERTIFICATE_AUTHORITY_WARNING", new Supplier() { // from class: com.android.settings.MonitoringCertInfoActivity$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreate$1;
                    lambda$onCreate$1 = MonitoringCertInfoActivity.this.lambda$onCreate$1();
                    return lambda$onCreate$1;
                }
            }), Locale.getDefault());
            HashMap hashMap2 = new HashMap();
            hashMap2.put("numberOfCertificates", Integer.valueOf(intExtra2));
            hashMap2.put("orgName", devicePolicyManager.getDeviceOwnerNameOnAnyUser());
            builder.setMessage(messageFormat2.format(hashMap2));
        } else {
            builder.setIcon(17301624);
            builder.setMessage(R$string.ssl_ca_cert_warning_message);
        }
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$0() {
        return getString(R$string.ssl_ca_cert_info_message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$1() {
        return getResources().getString(R$string.ssl_ca_cert_info_message_device_owner);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("com.android.settings.TRUSTED_CREDENTIALS_USER");
        intent.setFlags(335544320);
        intent.putExtra("ARG_SHOW_NEW_FOR_USER", this.mUserId);
        startActivity(intent);
        finish();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }
}
