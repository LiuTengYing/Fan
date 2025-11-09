package com.android.settings.biometrics.face;

import android.os.Bundle;
import android.view.View;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$style;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
/* loaded from: classes.dex */
public class FaceEnrollFinish extends BiometricEnrollBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1508;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.face_enroll_finish);
        setHeaderText(R$string.security_settings_face_enroll_finish_title);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(R$string.security_settings_face_enroll_done).setListener(new View.OnClickListener() { // from class: com.android.settings.biometrics.face.FaceEnrollFinish$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FaceEnrollFinish.this.onNextButtonClick(view);
            }
        }).setButtonType(5).setTheme(R$style.SudGlifButton_Primary).build());
    }

    public void onNextButtonClick(View view) {
        setResult(1);
        finish();
    }
}
