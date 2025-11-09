package com.android.settings.development;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class FreeformWindowsPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin, RebootConfirmationDialogHost {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;
    private final DevelopmentSettingsDashboardFragment mFragment;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enable_freeform_support";
    }

    public FreeformWindowsPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        this.mFragment = developmentSettingsDashboardFragment;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !((ActivityManager) this.mContext.getSystemService(ActivityManager.class)).isLowRamDevice();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.Global.putInt(this.mContext.getContentResolver(), "enable_freeform_support", booleanValue ? 1 : 0);
        if (booleanValue) {
            RebootConfirmationDialogFragment.show(this.mFragment, R$string.reboot_dialog_enable_freeform_support, this);
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Global.getInt(this.mContext.getContentResolver(), "enable_freeform_support", 0) != 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Global.putInt(this.mContext.getContentResolver(), "enable_freeform_support", 0);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // com.android.settings.development.RebootConfirmationDialogHost
    public void onRebootConfirmed() {
        this.mContext.startActivity(new Intent("android.intent.action.REBOOT"));
    }

    String getBuildType() {
        return Build.TYPE;
    }
}
