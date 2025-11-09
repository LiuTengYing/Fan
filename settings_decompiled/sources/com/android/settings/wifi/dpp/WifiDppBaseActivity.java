package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R$layout;
import com.android.settings.R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.InstrumentedActivity;
import com.google.android.setupdesign.util.ThemeHelper;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
/* loaded from: classes2.dex */
public abstract class WifiDppBaseActivity extends InstrumentedActivity {
    protected FragmentManager mFragmentManager;

    protected abstract void handleIntent(Intent intent);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        applyTheme();
        if (UniMobileNetworkUtils.isSubsidyShowing()) {
            getWindow().addFlags(524288);
        }
        setContentView(R$layout.wifi_dpp_activity);
        this.mFragmentManager = getSupportFragmentManager();
        if (bundle == null) {
            handleIntent(getIntent());
        }
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    private void applyTheme() {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        setTheme(R$style.SettingsPreferenceTheme_SetupWizard);
        ThemeHelper.trySetDynamicColor(this);
    }
}
