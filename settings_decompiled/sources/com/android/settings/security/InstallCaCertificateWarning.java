package com.android.settings.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
/* loaded from: classes.dex */
public class InstallCaCertificateWarning extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        setContentView(R$layout.ca_certificate_warning_dialog);
        getWindow().addSystemFlags(524288);
        GlifLayout glifLayout = (GlifLayout) findViewById(R$id.setup_wizard_layout);
        glifLayout.setHeaderText(R$string.ca_certificate_warning_title);
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(R$string.certificate_warning_install_anyway).setListener(installCaCertificate()).setButtonType(0).setTheme(R$style.SudGlifButton_Secondary).build());
        footerBarMixin.getSecondaryButtonView().setFilterTouchesWhenObscured(true);
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R$string.certificate_warning_dont_install).setListener(returnToInstallCertificateFromStorage()).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
        footerBarMixin.getPrimaryButtonView().setFilterTouchesWhenObscured(true);
    }

    private View.OnClickListener installCaCertificate() {
        return new View.OnClickListener() { // from class: com.android.settings.security.InstallCaCertificateWarning$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallCaCertificateWarning.this.lambda$installCaCertificate$0(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$installCaCertificate$0(View view) {
        Intent intent = new Intent();
        intent.setAction("android.credentials.INSTALL");
        intent.putExtra("certificate_install_usage", "ca");
        startActivity(intent);
        finish();
    }

    private View.OnClickListener returnToInstallCertificateFromStorage() {
        return new View.OnClickListener() { // from class: com.android.settings.security.InstallCaCertificateWarning$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallCaCertificateWarning.this.lambda$returnToInstallCertificateFromStorage$1(view);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$returnToInstallCertificateFromStorage$1(View view) {
        Toast.makeText(this, R$string.cert_not_installed, 0).show();
        finish();
    }
}
