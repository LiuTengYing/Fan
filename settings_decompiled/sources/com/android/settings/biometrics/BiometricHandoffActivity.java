package com.android.settings.biometrics;

import android.os.Bundle;
import android.view.View;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
/* loaded from: classes.dex */
public class BiometricHandoffActivity extends BiometricEnrollBase {
    private FooterButton mPrimaryFooterButton;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1894;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.biometric_handoff);
        setHeaderText(R$string.biometric_settings_hand_back_to_guardian);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(getPrimaryFooterButton());
    }

    protected FooterButton getPrimaryFooterButton() {
        if (this.mPrimaryFooterButton == null) {
            this.mPrimaryFooterButton = new FooterButton.Builder(this).setText(R$string.biometric_settings_hand_back_to_guardian_ok).setButtonType(5).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.BiometricHandoffActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BiometricHandoffActivity.this.onNextButtonClick(view);
                }
            }).setTheme(R$style.SudGlifButton_Primary).build();
        }
        return this.mPrimaryFooterButton;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        setResult(-1);
        finish();
    }
}
