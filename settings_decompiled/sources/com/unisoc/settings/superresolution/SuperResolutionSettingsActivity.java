package com.unisoc.settings.superresolution;

import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import com.android.settings.SubSettings;
/* loaded from: classes2.dex */
public class SuperResolutionSettingsActivity extends SubSettings {
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
        if (z) {
            finish();
        }
    }

    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (isInMultiWindowMode()) {
            finish();
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (Settings.Global.getInt(getContentResolver(), "sprd.action.super_resolution_state", 0) == 1) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsActivity, android.app.Activity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SuperResolutionSettings.class.getName());
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SubSettings, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return SuperResolutionSettings.class.getName().equals(str);
    }

    @Override // com.android.settings.core.SettingsBaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.d("SuperResolutionSettingsActivity", "onConfigurationChanged");
        finish();
    }
}
