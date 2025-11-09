package com.android.settings.development;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class DSUTermsOfServiceActivity extends Activity {
    /* JADX INFO: Access modifiers changed from: private */
    public void installDSU(Intent intent) {
        intent.setClassName("com.android.dynsystem", "com.android.dynsystem.VerificationActivity");
        startActivity(intent);
        finish();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.dsu_terms_of_service);
        TextView textView = (TextView) findViewById(R$id.tos_content);
        final Intent intent = getIntent();
        if (!intent.hasExtra("KEY_TOS")) {
            finish();
        }
        String stringExtra = intent.getStringExtra("KEY_TOS");
        if (TextUtils.isEmpty(stringExtra)) {
            installDSU(intent);
            return;
        }
        textView.setText(stringExtra);
        ((Button) findViewById(R$id.accept)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.development.DSUTermsOfServiceActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                DSUTermsOfServiceActivity.this.installDSU(intent);
            }
        });
    }
}
