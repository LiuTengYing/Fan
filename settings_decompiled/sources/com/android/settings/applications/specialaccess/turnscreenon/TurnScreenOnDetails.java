package com.android.settings.applications.specialaccess.turnscreenon;

import android.app.AppOpsManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoWithHeader;
/* loaded from: classes.dex */
public class TurnScreenOnDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppOpsManager mAppOpsManager;
    private SwitchPreference mSwitchPref;

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1922;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mAppOpsManager = (AppOpsManager) getSystemService(AppOpsManager.class);
        addPreferencesFromResource(R$xml.turn_screen_on_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setTitle(R$string.allow_turn_screen_on);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            setTurnScreenOnAppOp(this.mPackageInfo.applicationInfo.uid, this.mPackageName, ((Boolean) obj).booleanValue());
            return true;
        }
        return false;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        this.mSwitchPref.setChecked(isTurnScreenOnAllowed(this.mAppOpsManager, this.mPackageInfo.applicationInfo.uid, this.mPackageName));
        return true;
    }

    void setTurnScreenOnAppOp(int i, String str, boolean z) {
        this.mAppOpsManager.setMode(61, i, str, z ? 0 : 2);
    }

    static boolean isTurnScreenOnAllowed(AppOpsManager appOpsManager, int i, String str) {
        return appOpsManager.checkOpNoThrow(61, i, str) == 0;
    }

    public static int getPreferenceSummary(AppOpsManager appOpsManager, int i, String str) {
        if (isTurnScreenOnAllowed(appOpsManager, i, str)) {
            return R$string.app_permission_summary_allowed;
        }
        return R$string.app_permission_summary_not_allowed;
    }
}
