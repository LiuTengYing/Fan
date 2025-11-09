package com.android.settings.wifi;

import android.content.Intent;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.wifi.p2p.WifiP2pSettings;
import com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsWifiSettings2;
/* loaded from: classes.dex */
public class WifiPickerActivity extends SettingsActivity {
    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        if (!intent.hasExtra(":settings:show_fragment")) {
            intent.putExtra(":settings:show_fragment", getWifiSettingsClass().getName());
            intent.putExtra(":settings:show_fragment_title_resid", R$string.wifi_select_network);
        }
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return WifiSettings.class.getName().equals(str) || WifiP2pSettings.class.getName().equals(str) || SavedAccessPointsWifiSettings2.class.getName().equals(str);
    }

    Class<? extends PreferenceFragmentCompat> getWifiSettingsClass() {
        return WifiSettings.class;
    }
}
