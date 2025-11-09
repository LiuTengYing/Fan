package com.android.settings.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$style;
import com.android.settingslib.core.lifecycle.ObservableActivity;
/* loaded from: classes.dex */
public abstract class QrCodeScanModeBaseActivity extends ObservableActivity {
    protected FragmentManager mFragmentManager;

    protected abstract void handleIntent(Intent intent);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(R$style.SudThemeGlifV3_DayNight);
        setContentView(R$layout.qrcode_scan_mode_activity);
        this.mFragmentManager = getSupportFragmentManager();
        if (bundle == null) {
            handleIntent(getIntent());
        }
    }
}
