package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.notification.RedactionInterstitial;
import com.google.android.setupcompat.util.WizardManagerHelper;
/* loaded from: classes.dex */
public class SetupRedactionInterstitial extends RedactionInterstitial {

    /* loaded from: classes.dex */
    public static class SetupRedactionInterstitialFragment extends RedactionInterstitial.RedactionInterstitialFragment {
    }

    public static void setEnabled(Context context, boolean z) {
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, SetupRedactionInterstitial.class), z ? 1 : 2, 1);
    }

    @Override // com.android.settings.notification.RedactionInterstitial, com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        if (!WizardManagerHelper.isAnySetupWizard(getIntent())) {
            finish();
        }
        super.onCreate(bundle);
    }

    @Override // com.android.settings.notification.RedactionInterstitial, com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SetupRedactionInterstitialFragment.class.getName());
        return intent;
    }

    @Override // com.android.settings.notification.RedactionInterstitial, com.android.settings.SettingsActivity
    protected boolean isValidFragment(String str) {
        return SetupRedactionInterstitialFragment.class.getName().equals(str);
    }
}
